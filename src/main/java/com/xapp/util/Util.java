package com.xapp.util;

/**
 * Created by shild on 2015/7/13.
 */

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Util {
    /**
     * gzip 解压的缓冲区域大�?
     */
    public static final int BUFFER = 1024;
    private static Logger logger = LoggerFactory.getLogger(Util.class);

    /**
     * 返回当前时间
     *
     * @return "yyyy-MM-dd HH:mm:ss"格式的时间字符串
     */
    public static String getTime() {
        Calendar cale = Calendar.getInstance();
        // 将Calendar类型转换成Date类型
        Date tasktime = cale.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(tasktime);
    }

    /**
     * 返回当前时间
     *
     * @return "yyyyMMddHHmmss"格式的时间字符串
     */
    public static String getTime2() {
        Calendar cale = Calendar.getInstance();
        // 将Calendar类型转换成Date类型
        Date tasktime = cale.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(tasktime);
    }

    /**
     * 解压zip格式的压缩包
     *
     * @param inPath  输入路径
     * @param outPath 输出路径
     * @return 解压成功或失败标�?
     */
    public static Boolean openZip(String inPath, String outPath) {
        String unzipfile = inPath; // 解压缩的文件�?
        try {
            ZipInputStream zin = new ZipInputStream(new FileInputStream(
                    unzipfile));
            ZipEntry entry;
            // 创建文件�?
            while ((entry = zin.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    File directory = new File(outPath, entry.getName());
                    if (!directory.exists()) {
                        if (!directory.mkdirs()) {
                            System.exit(0);
                        }
                    }
                    zin.closeEntry();
                } else {
                    File myFile = new File(entry.getName());
                    FileOutputStream fout = new FileOutputStream(outPath
                            + myFile.getPath());
                    DataOutputStream dout = new DataOutputStream(fout);
                    byte[] b = new byte[1024];
                    int len = 0;
                    while ((len = zin.read(b)) != -1) {
                        dout.write(b, 0, len);
                    }
                    dout.close();
                    fout.close();
                    zin.closeEntry();
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将字符串str按子字符串separatorChars 分割成数�?
     *
     * @param str            要拆分的字符�?
     * @param separatorChars 用来拆分的分割字�?
     * @return 拆分后的字符�?
     */
    public static String[] split2(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }

    /**
     * 拆分字符�?
     *
     * @param str               要拆分的字符�?
     * @param separatorChars    用来拆分的分割字�?
     * @param max               要拆分字符串的最大长�?
     * @param preserveAllTokens
     * @return 拆分后的字符�?
     */
    private static String[] splitWorker(String str, String separatorChars,
                                        int max, boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return new String[]{""};
        }
        Vector<String> vector = new Vector<String>();
        int sizePlus1 = 1;
        int i = 0;
        int start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            while (i < len) {
                if (str.charAt(i) == '\r' || str.charAt(i) == '\n'
                        || str.charAt(i) == '\t') {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        vector.addElement(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                } else {
                    lastMatch = false;
                    match = true;
                    i++;
                }
            }
        } else if (separatorChars.length() == 1) {
            char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        vector.addElement(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                } else {
                    lastMatch = false;
                    match = true;
                    i++;
                }
            }
        } else {
            while (i < len) {
                int id = i + separatorChars.length() < len ? i
                        + separatorChars.length() : len;
                if (separatorChars.indexOf(str.charAt(i)) >= 0
                        && separatorChars.equals(str.substring(i, id))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        vector.addElement(str.substring(start, i));
                        match = false;
                    }
                    i += separatorChars.length();
                    start = i;
                } else {
                    lastMatch = false;
                    match = true;
                    i++;
                }
            }
        }
        if (match || preserveAllTokens && lastMatch) {
            vector.addElement(str.substring(start, i));
        }
        String[] ret = new String[vector.size()];
        vector.copyInto(ret);
        return ret;
    }

    /**
     * ***********************************************************
     * 函数名称:gbk2utf8 函数描述:将gbk 转码为utf-8 输入参数：@param chenese 输入参数：@return
     * 输出参数：byte[] �?注：
     * ***********************************************************
     */
    public static byte[] gbk2utf8(String chenese) {
        char c[] = chenese.toCharArray();
        byte[] fullByte = new byte[3 * c.length];
        for (int i = 0; i < c.length; i++) {
            int m = (int) c[i];
            String word = Integer.toBinaryString(m);
            StringBuffer sb = new StringBuffer();
            int len = 16 - word.length();
            for (int j = 0; j < len; j++) {
                sb.append("0");
            }
            sb.append(word);
            sb.insert(0, "1110");
            sb.insert(8, "10");
            sb.insert(16, "10");
            String s1 = sb.substring(0, 8);
            String s2 = sb.substring(8, 16);
            String s3 = sb.substring(16);
            byte b0 = Integer.valueOf(s1, 2).byteValue();
            byte b1 = Integer.valueOf(s2, 2).byteValue();
            byte b2 = Integer.valueOf(s3, 2).byteValue();
            byte[] bf = new byte[3];
            bf[0] = b0;
            fullByte[i * 3] = bf[0];
            bf[1] = b1;
            fullByte[i * 3 + 1] = bf[1];
            bf[2] = b2;
            fullByte[i * 3 + 2] = bf[2];
        }
        return fullByte;
    }

    /**
     * ***********************************************************
     * 函数名称:utf8Togb2312 函数描述:将UTF-8编码转为GBK 编码 输入参数：@param str 输入参数：@return
     * 输出参数：String �?注：
     * ***********************************************************
     */
    public static String utf8Togb2312(String str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '+':
                    sb.append(' ');
                    break;
                case '%':
                    try {
                        sb.append((char) Integer.parseInt(
                                str.substring(i + 1, i + 3), 16));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException();
                    }
                    i += 2;
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        // Undo conversion to external encoding
        String result = sb.toString();
        String res = null;
        try {
            byte[] inputBytes = result.getBytes("8859_1");
            res = new String(inputBytes, "UTF-8");
        } catch (Exception e) {
        }
        return res;
    }

    public static boolean isValidUtf8(byte[] b, int aMaxCount) {
        int lLen = b.length, lCharCount = 0;
        for (int i = 0; i < lLen && lCharCount < aMaxCount; ++lCharCount) {
            byte lByte = b[i++];// to fast operation, ++ now, ready for the
            // following for(;;)
            if (lByte >= 0)
                continue;// >=0 is normal ascii
            if (lByte < (byte) 0xc0 || lByte > (byte) 0xfd)
                return false;
            int lCount = lByte > (byte) 0xfc ? 5 : lByte > (byte) 0xf8 ? 4
                    : lByte > (byte) 0xf0 ? 3 : lByte > (byte) 0xe0 ? 2 : 1;
            if (i + lCount > lLen)
                return false;
            for (int j = 0; j < lCount; ++j, ++i)
                if (b[i] >= (byte) 0xc0)
                    return false;
        }
        return true;
    }

    /**
     * �?请�?�?字符串转换为对应的十进制Unicode�?&#35831;&#36873;&#25321;"
     */
    public static String ConvertToUnicode(String testStr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < testStr.length(); i++) {
            char s1 = testStr.charAt(i);
            int i1 = (int) s1;
            sb.append(String.format("&#%1$d;", i1));
        }
        return sb.toString();
    }

    /**
     * 将Unicode�?&#35831;&#36873;&#25321; 转换�?"请�?�?
     *
     * @param str
     * @return
     */
    public static String UnicodeToConvert(String str) {
        str = str.replaceAll("&amp;", "&");
        StringBuffer sb = new StringBuffer();
        if (str != null && str.length() > 0) {
            int i = 0;
            int j = 0;
            String chStr;
            char ch;
            while (i < str.length()) {
                if (str.charAt(i) == '&' && (i + 1) < str.length()
                        && str.charAt(i + 1) == '#') {
                    j = str.indexOf(';', i);
                    if (j > i) {
                        chStr = str.substring(i + 2, j);
                        ch = (char) Integer.parseInt(chStr);
                        sb.append(ch);
                        i = j + 1;
                        continue;
                    }
                }
                sb.append(str.charAt(i++));
            }
        }
        logger.info("UnicodeToConvert", "sb=" + sb.toString());
        return sb.toString();
    }

    /**
     * *************************************************************************************
     * 函数名称：gzipdecompress 函数描述：gzip数据的解�?输入参数：@param data 输入参数：@return
     * 输入参数：@throws Exception 输出参数�?返回 值：byte[] �?注：
     * **************************************************************************************
     */
    public static byte[] gzipdecompress(byte[] data) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 解压�?
        gzipdecompress(bais, baos);
        data = baos.toByteArray();
        baos.flush();
        baos.close();
        bais.close();
        return data;
    }

    /**
     * *************************************************************************************
     * 函数名称：gzipdecompress 函数描述：数据解压缩 输入参数：@param is 输入参数：@param os 输入参数：@throws
     * Exception 输出参数�?返回 值：void �?注：
     * **************************************************************************************
     */
    private static void gzipdecompress(InputStream is, OutputStream os)
            throws Exception {
        GZIPInputStream gis = new GZIPInputStream(is);
        int count;
        byte data[] = new byte[BUFFER];
        while ((count = gis.read(data, 0, BUFFER)) != -1) {
            os.write(data, 0, count);
        }
        gis.close();
    }

    public static boolean isStringEmpty(String a) {
        if (a == null) {
            return true;
        } else {
            if ("".equals(a)) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * MD5加密
     */
    public static String getMD5Str(String sourceStr) {
        byte[] source = sourceStr.getBytes();
        String s = null;
        char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f'};
        java.security.MessageDigest md = null;
        try {
            md = java.security.MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // #debug
            e.printStackTrace();
        }
        if (md == null)
            return null;
        md.update(source);
        byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
        // 用字节表示就是 16 个字节
        char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
        // 所以表示成 16 进制需要 32 个字符
        int k = 0; // 表示转换结果中对应的字符位置
        for (int i = 0; i < 16; i++) {
            // 从第一个字节开始，对 MD5 的每一个字节
            // 转换成 16 进制字符的转换
            byte byte0 = tmp[i]; // 取第 i 个字节
            str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
            // >>> 为逻辑右移，将符号位一起右移
            str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
        }
        s = new String(str); // 换后的结果转换为字符串
        return s;
    }

    /**
     * 组装格式化的请求字符串
     *
     * @param map Map类型的参数
     * @return 组装好的请求串
    public static String setRequest(Map<String, Object> map) {
        try {
            map.put("dateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()).format(new Date()));
//            ObjectMapper mapper = new ObjectMapper();
//            String bodyJson = mapper.writeValueAsString(map);
            String bodyJson = JSON.toJSONString(map);
//
            Map<String, Object> totalMap = new HashMap<String, Object>();
            totalMap.put("uid", Variable.uid);
            totalMap.put("body", map);
            totalMap.put("sign", Util.getMD5Str(bodyJson + Variable.requestKey));

            String result = JSON.toJSONString(totalMap);
            logger.info(result);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }
     */

    public static String getSortedMadString(Map<String, String> map)
    {
        List<String> keys = new ArrayList<String>(map.keySet());
        Collections.sort(keys);

        String prestr = "{";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = map.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return "";
    }
    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }
}

