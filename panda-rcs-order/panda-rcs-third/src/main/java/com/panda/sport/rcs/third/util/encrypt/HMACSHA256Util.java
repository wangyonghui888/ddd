package com.panda.sport.rcs.third.util.encrypt;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMACSHA256Util {

    public static final String ALGORITHM = "HmacSHA256";

    public static String calculateHMac(String key, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance(ALGORITHM);
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("US-ASCII"), ALGORITHM);
        sha256_HMAC.init(secret_key);
        return byteArrayToHex(sha256_HMAC.doFinal(data.getBytes("US-ASCII")));
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static void main(String [] args) throws Exception {
        System.out.println(calculateHMac("49d1083cd7f69711b29bbff06b1918c4", "{\"AcceptTypeId\":1,\"Amount\":10.00,\"AuthToken\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNjgxODEzODM5LCJleHAiOjE2ODE5MDAyMzksImlkIjoi55So5oi3aWQiLCJuYW1lIjoi55So5oi35ZCNIiwiYnVzSWQiOiLllYbmiLdpZCJ9.K6pp_YdzU--b9mzBdulyt_3GPsByKOSJVMAWKIxOoEw\",\"BetType\":1,\"Currency\":\"USD\",\"Selections\":rage:{\"Price\":1.97,\"SelectionId\":3025298350}],\"RequestHash\":\"f46cdfbe42edc69463d499d6e0b7ef1157b7d8ebcd46edfb62e852f21516c26b\"}"));
    }
}