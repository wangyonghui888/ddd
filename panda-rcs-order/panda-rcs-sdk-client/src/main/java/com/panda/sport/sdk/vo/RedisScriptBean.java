package com.panda.sport.sdk.vo;




public class RedisScriptBean<T>  {

    /**
     * 脚本摘要
     */
    private volatile String sha1;
    /**
     * 脚本KEY
     */
    private String scriptKey;
    
    private String scriptSource;
    
    private Class<T> resultType;


    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public String getScriptKey() {
        return scriptKey;
    }

    public void setScriptKey(String scriptKey) {
        this.scriptKey = scriptKey;
    }

    public String getScriptSource() {
        return scriptSource;
    }

    public void setScriptSource(String scriptSource) {
        this.scriptSource = scriptSource;
    }

    public Class<T> getResultType() {
        return resultType;
    }

    public void setResultType(Class<T> resultType) {
        this.resultType = resultType;
    }
}
