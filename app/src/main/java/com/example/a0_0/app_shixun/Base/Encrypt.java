package com.example.a0_0.app_shixun.Base;

/**
 * Created by 樱满集0_0 on 2017/6/22.
 */

public class Encrypt {
    public synchronized static String md5(String str){
        try {
            java.security.MessageDigest md=
                    java.security.MessageDigest.getInstance("MD5");
            md.reset();
            md.update(str.getBytes("UTF-8"));
            byte[] hash=md.digest();
            int len=hash.length;
            StringBuffer sb=new StringBuffer();
            for(int i=0;i<len;i++){
                if(1==Integer.toHexString(0xff&hash[i]).length()){
                    sb.append(0);
                }
                sb.append(Integer.toHexString(0XFF&hash[i]));
            }
            return sb.toString();
        }
        catch (java.security.NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        catch (java.io.UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return null;
    }
}
