package com.panda.rcs.push.utils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

public class NettySslUtils {

    public static SSLContext createSSLContext(String type, String path, String password) throws Exception{
        KeyStore ks = KeyStore.getInstance(type);
        InputStream is = new FileInputStream(path);
        ks.load(is, password.toCharArray());
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(ks, password.toCharArray());
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
        return sslContext;
    }

}
