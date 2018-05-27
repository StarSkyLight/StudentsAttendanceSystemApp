package com.sas.ziyi.studentsattendancesystemapp.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utility {

    /**
     * 对密码进行哈希加密的专用工具
     */
    public static String HashCode(String code){
        //String hashCode = code;

        try {
            // 得到一个信息摘要器
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(code.getBytes("utf-8"));
            StringBuffer buffer = new StringBuffer();
            // 把每一个byte 做一个与运算 0xff;
            for (byte b : result) {
                // 与运算
                int number = b & 0xff;// 加盐
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }

            // 标准的md5加密后的结果
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        //return hashCode;
    }

}
