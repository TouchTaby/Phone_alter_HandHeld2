package com.gsyong.ny.nyapp;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017-11-22.
 */

public class helper {
    public static Integer _loginProvinceID = 0;
    public static Integer _loginCityID = 0;
    public static Integer _loginCountyID = 0;
    public static Integer _loginTownshipID = 0;

    public static com.gsyong.ny.nyapp.Model.dt_qiye login_qiye = null;
    public static com.gsyong.ny.nyapp.Model.dt_zhucema login_zhucema = null;


    public static String getMD5(String val) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(val.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }
}
