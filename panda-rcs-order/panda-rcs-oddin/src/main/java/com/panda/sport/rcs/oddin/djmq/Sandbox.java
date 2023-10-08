package com.panda.sport.rcs.oddin.djmq;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 沙箱环境
 * <p>
 * 沙箱是一个与现有环境隔离的独立环境，沙箱内的类及对象和沙箱外的类及对象本质上是两个不同的类定义
 * 因此通过沙箱内对象操作，可以绕过部分类对象自身限制（譬如RocketMQ驱动的一JVM只能有一个MQ实例的限制），也可以发挥想象看什么地方可以用上
 * <p>
 * 此Sandbox设计原理如下：
 * 1、采用ClassLoader从代码加载层级进行隔离
 * 2、为Sandbox指定包后，通过Sandbox创建的对象，只要属于这些包内都会运行在Sandbox当中
 * 3、调用方创建类实例时使用Sandbox进行创建，确保运行在隔离环境
 */
public class Sandbox {
    private SandboxClassLoader classLoader;
    private SandboxUtil util = new SandboxUtil();
    private List<String> redefinedPackages;

    public Sandbox(List<String> packages) {
        redefinedPackages = packages;
        classLoader = new SandboxClassLoader(getContextClassLoader());
    }

    /**
     * 沙箱对象构造方法
     *
     * @param redefinedPackages 需工作在沙箱内的包
     *                          此包下面所有类都在工作在沙箱内
     */
    public Sandbox(String... redefinedPackages) {
        this(Lists.newArrayList(redefinedPackages));
    }


    /**
     * 获取当前上下文的类装载器
     * <p>
     * 此类装载器需包含MQClient相关类定义
     * PS：单独定义为一个方法，是担心当这个上下文类装载器满足不了要求时可以快速更换
     *
     * @return 当前类装载器
     */
    private ClassLoader getContextClassLoader() {
        //从类装载器机制而言，线程上下文的类转载器是最符合要求的
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 获取工作在沙箱内的枚举对象值
     *
     * @param enumValue 沙箱外的枚举对象值
     * @return 沙箱内的枚举对象值
     */
    public synchronized <T extends Enum> T getEnumValue(T enumValue) {
        if (!classLoader.isRedefinedClass(enumValue.getClass().getName())) {
            return enumValue;
        }

        try {
            String enumName = enumValue.getClass().getName();
            Class enumClzInSandbox = classLoader.loadClass(enumName);
            return (T) util.findEnumValue(enumValue, enumClzInSandbox);
        } catch (ClassNotFoundException e) {
            //类都传进来了，不可能会找不到类吧？多此一举的处理而已，忽略
            return null;
        }
    }

    /**
     * 在沙箱内创建指定Class的实例
     *
     * @param clz 待创建实例的Class
     * @return 跟clz功能相同并工作在沙箱内的类实例
     */
    public synchronized <T extends Object> T createObject(Class<T> clz) throws SandboxCannotCreateObjectException {
        try {
            final Class<?> clzInSandbox = classLoader.loadClass(clz.getName());
            final Object objectInSandbox = clzInSandbox.newInstance();

            //如果对象的类装载器和clz的类装载器一致，说明不是需要工作在沙箱内的对象，直接返回即可，无需代理
            if (objectInSandbox.getClass().getClassLoader() == clz.getClassLoader()) {
                return (T) objectInSandbox;
            }

            /*
            创建生产者的代理：由于沙箱内外的对象本质上属于不同的类，因此需要将两者能力桥接起来
                            这里采用了代理模式，通过创建沙箱外的对象实例，并将其所有方法调用通过代理转发到沙箱内执行
                            另外，由于沙箱内外的所有实例都属于不同的类，因此，对于参数和返回值还需要进行对象转换，将沙箱内外的对象进行对等克隆
             */

            //通过cglib创建对象的子类代理
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(clz);
            enhancer.setCallback((MethodInterceptor) (o, method, args, methodProxy) -> {
                Method targetMethod = clzInSandbox.getMethod(method.getName(), method.getParameterTypes());
                //调用前需对参数进行克隆，转换为沙箱内对象
                Object[] targetArgs = args == null ? null : util.cloneTo(args, classLoader);
                Object result = targetMethod.invoke(objectInSandbox, targetArgs);
                //调用后续对结果进行克隆，转换为沙箱外对象
                return util.cloneTo(result, getContextClassLoader());
            });
            return (T) enhancer.create();
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            throw new SandboxCannotCreateObjectException("无法在沙箱内创建对象", e);
        }
    }

    /**
     * 在沙箱内创建指定名称的类实例
     * <p>
     * 如该名称类不属于redefinedPackages所指定的包内，则直接返回外部类实例
     *
     * @param clzName 待创建实例的类名称
     * @return 指定类名称的实例对象
     */
    public <T extends Object> T createObject(String clzName) throws ClassNotFoundException, SandboxCannotCreateObjectException {
        Class clz = Class.forName(clzName);
        return (T) createObject(clz);
    }

    /**
     * 沙箱内工具
     * <p>
     * 对沙箱内外对象提供克隆转换服务
     */
    class SandboxUtil {
        /**
         * 在targetClassLoader中创建args的克隆对象并返回
         * <p>
         * 此克隆属于深度克隆：如果属性中存在对象且该对象也需要进行沙箱隔离的话，也将进行克隆（无论转入沙箱还是转出）
         *
         * @param args              待克隆的对象
         * @param targetClassLoader 目标类装载器
         * @return 目标对象
         */
        Object[] cloneTo(Object[] args, ClassLoader targetClassLoader) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, InstantiationException, NoSuchMethodException, InvocationTargetException {
            if (args.length == 0) return args;

            Object[] results = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                results[i] = cloneTo(args[i], targetClassLoader);
            }
            return results;
        }

        /**
         * 在targetClassLoader中创建对象obj的克隆版并返回
         * <p>
         * 此克隆属于深度克隆：如果属性中存在对象且该对象也需要进行沙箱隔离的话，也将进行克隆（无论转入沙箱还是转出）
         *
         * @param obj               待克隆对象
         * @param targetClassLoader 目标类转载器
         * @return 克隆结果
         */
        Object cloneTo(Object obj, ClassLoader targetClassLoader) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
            if (obj == null) return null;

            Class sourceClz = obj.getClass();
            //仅对需要运行在沙箱内的对象进行深度克隆
            if (classLoader.isRedefinedClass(sourceClz.getName())) {
                Class targetClz = targetClassLoader.loadClass(sourceClz.getName());  //先取得同名的目标类实例

                //枚举是特殊的类型，不能生成新对象，仅能寻找同名值进行引用，此处需留意
                if (Enum.class.isAssignableFrom(targetClz)) {
                    return findEnumValue((Enum) obj, targetClz);
                } else {
                    Object targetObj = targetClz.newInstance();
                    cloneFields(obj, targetObj); //对象的话，还需要对所有字段进行深度克隆
                    return targetObj;
                }
            } else {
                return obj;
            }
        }

        /**
         * 将枚举对象obj转换为另一个同名枚举值
         *
         * @param obj       待转换的枚举
         * @param targetClz 同名枚举类
         */
        Object findEnumValue(Enum obj, Class targetClz) {
            Object[] enumConstants = targetClz.getEnumConstants(); //取得枚举对象所有值
            for (Object o : enumConstants) {
                Enum sourceObj = obj;
                Enum targetObj = (Enum) o;

                //枚举名称相同则代表值相同，可以直接引用
                if (sourceObj.name().equals(targetObj.name())) {
                    return targetObj;
                }
            }
            return null; //找不到？什么情况会发生？如果版本不一致，那就让它错呗
        }

        /**
         * 深度克隆，将arg对象所有属性复制至targetObj中
         * <p>
         * 对于需要运行在沙箱内的对象，需要创建新对象
         *
         * @param arg       源对象
         * @param targetObj 目标对象
         */
        private void cloneFields(Object arg, Object targetObj) throws NoSuchFieldException, IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
            Class sourceClz = arg.getClass();
            Class targetClz = targetObj.getClass();

            Field[] fields = sourceClz.getDeclaredFields();
            //对于所有field进行逐个设置
            for (Field field : fields) {
                String fieldName = field.getName();
                Field targetField = targetClz.getDeclaredField(fieldName);

                //可能field是private或者其它的，都操作一下
                field.setAccessible(true);
                targetField.setAccessible(true);
                Object sourceValue = field.get(arg);
                targetField.set(targetObj, cloneTo(sourceValue, targetObj.getClass().getClassLoader())); //在目标字段设置时，先进行字段值克隆
            }
        }
    }

    /**
     * 沙箱隔离核心
     * <p>
     * 通过ClassLoader将进行类级别的运行时隔离
     * <p>
     * 此类本质上是代理了currentContextClassLoader对象，并增加了对部分需要在沙箱内运行的类处理能力
     */
    class SandboxClassLoader extends ClassLoader {
        //当前上下文的ClassLoader，用于寻找类实例并克隆进沙箱
        private final ClassLoader contextClassLoader;
        //缓存已经创建过的Class实例，避免重复定义
        private final Map<String, Class> cache = Maps.newHashMap();

        SandboxClassLoader(ClassLoader contextClassLoader) {
            this.contextClassLoader = contextClassLoader;
        }

        /**
         * 覆盖父类的转载类进内存的方法
         *
         * @param name 指定类名称
         * @return 已转载进内存的Class实例
         * @throws ClassNotFoundException
         */
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            return findClass(name);
        }

        /**
         * 重定义类转载逻辑
         * <p>
         * 1、对于需要运行在沙箱内的类（redefinedPackages中声明），通过复制contextClassLoader类定义的方式，直接运行在此ClassLoader下
         * 2、对于不需要运行在沙箱内的类，直接返回上下文类定义，以减少资源占用
         *
         * @param name 类名称（全路径）
         * @return 类定义
         */
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (isRedefinedClass(name)) {
                return getSandboxClass(name);
            } else {
                return contextClassLoader.loadClass(name);
            }
        }

        /**
         * 内部方法：获取需要在沙箱内运行的Class实例
         *
         * @param name 类名称
         * @return 沙箱内的类实例
         * @throws ClassNotFoundException
         */
        private synchronized Class<?> getSandboxClass(String name) throws ClassNotFoundException {
            //1、先从缓存中查找是否已经转载过该类，有则直接返回
            if (cache.containsKey(name)) {
                return cache.get(name);
            }
            //2、缓存不存在该类时，从currentContextClassLoader中复制一份到当前缓存中
            Class<?> clz = copyClass(name);
            cache.put(name, clz);
            return clz;
        }

        /**
         * 从currentContextClassLoader中复制一份类到本ClassLoader中
         * <p>
         * 此复制是将字节码copy到当前ClassLoader进行定义，因此与sandbox外部的Class已经完全不同实例，不能给外部直接赋值
         *
         * @param name 待复制的类名称
         * @return 工作在当前ClassLoader中的Class
         * @throws ClassNotFoundException
         */
        private synchronized Class<?> copyClass(String name) throws ClassNotFoundException {
            //取得.class文件所在路径
            String path = name.replace('.', '/') + ".class";
            //通过上下文类装载器获取资源句柄
            try (InputStream stream = contextClassLoader.getResourceAsStream(path)) {
                if (stream == null) {
                    throw new ClassNotFoundException(String.format("找不到类%s", name));
                }

                //读取所有字节内容
                byte[] content = readFromStream(stream);
                return defineClass(name, content, 0, content.length);
            } catch (IOException e) {
                throw new ClassNotFoundException("找不到指定的类", e);
            }
        }

        /**
         * 是否需要运行在沙箱内的类
         *
         * @param name 类名称
         */
        boolean isRedefinedClass(String name) {
            //校验是否沙箱约定的需要重定义的包
            for (String redefinedPackage : redefinedPackages) {
                if (name.startsWith(redefinedPackage)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 读取流内所有数据
         * <p>
         * 此处仅用于读取包内类定义原始数据，因此数据量比较小，可以直接byte数组返回
         * 如用于读取其它场景下的类信息就需要认真考虑，可能会爆内存
         *
         * @param stream 输入的类文件流
         * @return 类定义数据
         */
        private byte[] readFromStream(InputStream stream) throws IOException {
            byte[] buff = new byte[1024];
            byte[] content = new byte[0];

            int count;
            while ((count = stream.read(buff)) != -1) {
                int length = count + content.length;
                byte[] newContent = new byte[length];
                System.arraycopy(content, 0, newContent, 0, content.length);
                System.arraycopy(buff, 0, newContent, content.length, count);

                content = newContent;
            }
            return content;
        }
    }
}
