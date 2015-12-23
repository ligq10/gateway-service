package com.changhongit.loving.utils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class MessageUtils {
    final static char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /*
     * 16进制数字字符集
     */
    // private static String hexString = "0123456789abcdef";
    private static String hexString = "0123456789ABCDEF";

    private final static double PI = 3.14159265358979323; // 圆周率

    private final static double R = 6371229; // 地球的半径

    public static String toHexString(int i) {
        return toUnsignedString(i, 4);
    }

    public static String toVerifycodeString(int i) {
        return toVerifycodeAscii(i, 4);
    }

    private static String toUnsignedString(int i, int shift) {
        String string = null;
        char[] buf = new char[16];
        int charPos = 16;
        int radix = 1 << shift;
        int mask = radix - 1;
        do {
            buf[--charPos] = digits[i & mask];
            i >>>= shift;
        } while (i != 0);
        if (charPos == 15) {
            string = "0x000" + new String(buf, charPos, (16 - charPos));
        } else if (charPos == 14) {
            string = "0x00" + new String(buf, charPos, (16 - charPos));
        } else if (charPos == 13) {
            string = "0x0" + new String(buf, charPos, (16 - charPos));
        } else if (charPos == 12) {
            string = "0x" + new String(buf, charPos, (16 - charPos));
        }
        return string;
    }

    private static String toVerifycodeAscii(int i, int shift) {
        String string = null;
        char[] buf = new char[16];
        int charPos = 16;
        int radix = 1 << shift;
        int mask = radix - 1;
        do {
            buf[--charPos] = digits[i & mask];
            i >>>= shift;
        } while (i != 0);
        if (charPos == 15) {
            string = "0x000" + new String(buf, charPos, (16 - charPos));
        } else if (charPos == 14) {
            string = "0x00" + new String(buf, charPos, (16 - charPos));
        } else if (charPos == 13) {
            string = "0x0" + new String(buf, charPos, (16 - charPos));
        } else if (charPos == 12) {
            string = "0x" + new String(buf, charPos, (16 - charPos));
        }
        return string;
    }

    // 关护通VERIFYCODE字段-------校验和的16进制ASCII码
    public static String getVerifycodeAscii(String str) {
        // 根据默认编码获取字节数组
        byte[] bytes = null;
        try {
            bytes = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int sum = 0;
        for (int i = 0; i < bytes.length; i++) {
            String sb = "";
            sb = sb + (hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb = sb + (hexString.charAt((bytes[i] & 0x0f) >> 0));
            int b = Integer.parseInt(sb, 16);
            sum = sum + b;
        }
        return new String(toVerifycodeString(sum));
    }

    // 关护通length字段------指令长度的16进制ASCII码
    public static String getLengthAscii(String str) {
        byte[] bs = null;
        try {
            bs = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int length = bs.length;
        return String.valueOf(toHexString(length));
    }

    /*
     * 将字符串编码成16进制数字,适用于所有字符（包括中文）
     */
    public static String encode(String str, String charset)
            throws UnsupportedEncodingException {
        // 根据默认编码获取字节数组
        byte[] bytes = str.getBytes(charset);
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        // 将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    /*
     * 将16进制数字解码成字符串,适用于所有字符（包括中文）
     */
    public static String decode(String bytes, String charset)
            throws UnsupportedEncodingException {
        bytes = bytes.replace(" ", "");
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
                bytes.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write(hexString.indexOf(bytes.charAt(i)) << 4
                    | hexString.indexOf(bytes.charAt(i + 1)));
        return new String(baos.toByteArray(), charset);
    }

    public static byte[] change(byte[] bytes) {
        byte temp0x7e = 0x7e;
        byte temp0x7d = 0x7d;
        byte temp0x02 = 0x02;
        byte temp0x01 = 0x01;
        int length = 2;
        for (int i = 1; i < bytes.length - 1; i++) {
            if (bytes[i] == temp0x7e | bytes[i] == temp0x7d) {
                length = length + 2;
            } else {
                length = length + 1;
            }
        }
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.put(bytes[0]);

        for (int i = 1; i < bytes.length - 1; i++) {
            if (bytes[i] == temp0x7e) {
                buffer.put(temp0x7d);
                buffer.put(temp0x02);
            } else if (bytes[i] == temp0x7d) {
                buffer.put(temp0x7d);
                buffer.put(temp0x01);
            } else {
                buffer.put(bytes[i]);
            }

        }
        buffer.put(bytes[bytes.length - 1]);
        return buffer.array();
    }

    public static byte[] strToBcd(String asc) {
        int len = asc.length();
        int mod = len % 2;

        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }

        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }

        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;

        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }

            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }

            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    public static String getHex(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(hexString.charAt((b & 0xF0) >> 4)).append(
                    hexString.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    public static double getDistance(float longt1, float lat1, float longt2,
                                     float lat2) {
        double x, y, distance;
        x = (longt2 - longt1) * PI * R
                * Math.cos(((lat1 + lat2) / 2) * PI / 180) / 180;
        y = (lat2 - lat1) * PI * R / 180;
        distance = Math.hypot(x, y);
        return distance;
    }

}