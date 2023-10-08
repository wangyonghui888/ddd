package com.panda.sport.rcs.core.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.core.utils
 * @Description :  判断修改前和修改后的差异性
 * @Date: 2020-05-13 19:20
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
public class BeanChangeUtil{
    public static void main(String[] args) {
        User u1 = new User("1", true, "a");
        User u2 = new User("2", false, "b");



        Map<String, Map<String, Object>> map = BeanChangeUtil.contrastObj(u1,u2);
        System.out.println(map.get("old").toString());

        System.out.println(map.get("new").toString());
    }

    /**
     * 比较新旧二个东西修改内容
     * @param oldBean
     * @param newBean
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map<String, Map<String, Object>> contrastObj(Object oldBean, Object newBean) {

        /**
         *  创建两个个Map<String,Object>
         *  分别存储改动前 改动后的数据
         */
        Map<String, Object> beginMap = new HashMap<>(16);
        Map<String, Object> afterMap = new HashMap<>(16);
        Map<String, Map<String, Object>> list = new HashMap<>(16);


        StringBuilder str = new StringBuilder();

       /* T pojo1 = (T) oldBean;
        T pojo2 = (T) newBean;*/
        try {
            // 通过反射获取类的类类型及字段属性
            Class clazz = oldBean.getClass();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                // 排除序列化属性
                if ("serialVersionUID".equals(field.getName())||
                        "createTime".equals(field.getName())||
                        "updateTime".equals(field.getName())||
                        "crtTime".equals(field.getName())) {
                    continue;
                }
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                // 获取对应属性值
                Method getMethod = pd.getReadMethod();
                Object o1 = getMethod.invoke(oldBean);
                Object o2 = getMethod.invoke(newBean);
                if (o1 == null || o2 == null) {
                    continue;
                }
                if (!o1.toString().equals(o2.toString())) {
                    beginMap.put(field.getName(), o1);
                    afterMap.put(field.getName(), o2);
                }
            }

            list.put("old", beginMap);
            list.put("new", afterMap);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("比较新旧二个东西修改内容",e.getMessage(),e);
        }
        return list;
    }




    @Data
    static class User {
        private String about;

        private boolean lock;

        private String name;

        public User() {
        }

        public User(String about, boolean lock, String name) {
            super();
            this.about = about;
            this.lock = lock;
            this.name = name;
        }
        /*省略get、set方法*/
    }


}