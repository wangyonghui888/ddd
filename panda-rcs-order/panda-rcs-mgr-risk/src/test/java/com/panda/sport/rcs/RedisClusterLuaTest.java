package com.panda.sport.rcs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
 
public class RedisClusterLuaTest {
 
    private static Jedis jedis;		//单实例[]
    
    private static JedisCluster jedisCluster;
 
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        String[] nodes = "127.0.0.1:6477,127.0.0.1:6478,127.0.0.1:6479".split(",");
        Set<HostAndPort> hostAndPortSet = new HashSet<>();
        for (String node : nodes) {
            String[] nodeAttrs = node.trim().split(":");
            if(nodeAttrs!=null && nodeAttrs.length>1){
                HostAndPort hap = new HostAndPort(nodeAttrs[0], Integer.parseInt(nodeAttrs[1]));
                hostAndPortSet.add(hap);
            }
        }

        /**
         * 集群模式下jedisPool 采用默认的配置
         */
        jedisCluster = new JedisCluster(hostAndPortSet,10,10,10,"8:GCy-ghc.Vnsh1WD)uxkX699hXw7o%eq7-#}+e",new GenericObjectPoolConfig());
    }
 
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    	jedisCluster.close();
    }
    
    public String scriptLoad(String text) {
		String sha = null;
		Collection<JedisPool> pool = jedisCluster.getClusterNodes().values();
		for(JedisPool node : pool) {
			sha = node.getResource().scriptLoad(text);
		}
		return sha;
	}
 
//    @Test
//    public void  testSaveLua() throws IOException {
//    	String text = readFileContent("lua/orderSave_new.lua");
//    	String shakey = scriptLoad(text);
//    	System.out.println("shakey: " + shakey);
//
//    	List<String> keys = new ArrayList<>();
//    	keys.add("A_{2_690414}");
//
//    	String valStr = "[\"2020-05-27\",\"2\",\"1\",\"174329543499653121\",\"690414\",\"2\",\"1265527505856544770\",\"0\",\"3\",\"11265132617729\",\"1000\",\"1\",\"1265527505890099202\",\"1040.0\",\"D,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,d\",\"13\",\"15000000\",\"60000000\",\"90000000\",\"50000000\",\"1.04\"]";
//    	//传入的参数
//    	List<String> args = JSONObject.parseArray(valStr,String.class);
//    	Object ret = evalsha(shakey, keys, args);
//    	System.out.println(JSONObject.toJSONString(ret)  + "         " + System.currentTimeMillis());
//    	System.out.println("=============");
//
//    	try {
//			System.in.read();
//		} catch (IOException e) {
//			throw e;
//		}
//    	jedis.close();
//    }
//
////    @Test
//    public void  testRollBackLua() throws IOException {
//    	String text = readFileContent("lua/orderSaveRollback_new.lua");
//    	String shakey = scriptLoad(text);
//    	System.out.println("shakey: " + shakey);
//
//    	List<String> keys = new ArrayList<>();
//    	keys.add("A_{2_690414}");
//
//    	String valStr = "[\"2020-05-27\",\"2\",\"1\",\"174329543499653121\",\"690414\",\"2\",\"1265527505856544770\",\"0\",\"3\",\"11265132617729\",\"1000\",\"1\",\"1265527505890099202\",\"1040.0\",\"D,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,d\",\"13\",\"15000000\",\"60000000\",\"90000000\",\"50000000\",\"1.04\"]";
//    	//传入的参数
//    	List<String> args = JSONObject.parseArray(valStr,String.class);
//    	args.add("1");
//    	Object ret = evalsha(shakey, keys, args);
//    	System.out.println(JSONObject.toJSONString(ret)  + "         " + System.currentTimeMillis());
//    	System.out.println("=============");
//
//    	try {
//			System.in.read();
//		} catch (IOException e) {
//			throw e;
//		}
//    	jedis.close();
//    }
//
//    @Test
//    public void  testPrizeckLua() throws IOException {
//    	String text = readFileContent("lua/orderPrize_new.lua");
//    	String shakey = scriptLoad(text);
//    	System.out.println("shakey: " + shakey);
//
//    	List<String> keys = new ArrayList<>();
//    	keys.add("A_{2_690414}");
//
//    	String valStr = "[\"2020-05-27\",\"2\",\"1\",\"174329543499653121\",\"690414\",\"2\",\"1265527505856544770\",\"0\",\"3\",\"11265132617729\",\"1000\",\"1\",\"1265527505890099202\",\"1040.0\",\"D,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,d\",\"13\",\"15000000\",\"60000000\",\"90000000\",\"50000000\",\"1.04\"]";
//    	//传入的参数
//    	List<String> args = JSONObject.parseArray(valStr,String.class);
//    	args.add("-1000");//输
//    	Object ret = evalsha(shakey, keys, args);
//    	System.out.println(JSONObject.toJSONString(ret)  + "         " + System.currentTimeMillis());
//    	System.out.println("=============");
//
//    	try {
//			System.in.read();
//		} catch (IOException e) {
//			throw e;
//		}
//    	jedis.close();
//    }
//
//    public static String readFileContent(String fileName)  throws IOException {
//    	ClassLoader classLoader = RedisClusterLuaTest.class.getClassLoader();
//    	URL url = classLoader.getResource(fileName);
//        File file = new File(url.getFile());
//        BufferedReader reader = null;
//        StringBuffer sbf = new StringBuffer();
//        try {
//            reader = new BufferedReader(new FileReader(file));
//            String tempStr;
//            while ((tempStr = reader.readLine()) != null) {
//                sbf.append(tempStr);
//            }
//            reader.close();
//            return sbf.toString();
//        } catch (IOException e) {
//           throw e;
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e1) {
//                    ethrow e1;
//                }
//            }
//        }
//        return sbf.toString();
//    }
//
//	public Object evalsha(String shakey, List<String> keys, List<String> args) {
//		return jedisCluster.evalsha(shakey, keys, args);
//	}

}
