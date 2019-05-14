package com.company;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;





public class AESUtil {



    /**
     * @Description: AES加密
     * @author wang
     * @date 2017-7-30 下午03:42:47
     * @param content 需要加密的内容
     * @param AESKey  加密密钥
     * @return
     */
    public static String encrypt(String content, String AESKey){
        try{

            // KeyGenerator提供（对称）密钥生成器的功能
            // KeyGenerator.getInstance返回生成指定算法的秘密密钥的 KeyGenerator 对象。
            KeyGenerator kgen = KeyGenerator.getInstance("AES");

            // SecureRandom此类提供强加密随机数生成器 (RNG)。
            // SecureRandom.getInstance返回实现指定随机数生成器 (RNG) 算法的 SecureRandom 对象。
            SecureRandom random=SecureRandom.getInstance("SHA1PRNG");

            // SecureRandom.setSeed重新设置此随机对象的种子。
            random.setSeed(AESKey.getBytes());

            // KeyGenerator.init使用用户提供的随机源初始化此密钥生成器，使其具有确定的密钥大小。
            kgen.init(128, random);

            // SecretKey秘密（对称）密钥。
            // KeyGenerator.generateKey生成一个密钥。
            SecretKey secretKey = kgen.generateKey();

            // 从接口 java.security.Key 继承的方法getFormat()，返回基本编码格式的密钥，如果此密钥不支持编码，则返回 null。
            byte[] enCodeFormat = secretKey.getEncoded();

            // SecretKeySpec此类以与 provider 无关的方式指定一个密钥。
            // 根据给定的字节数组构造一个密钥。
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");

            // Cipher此类为加密和解密提供密码功能
            // Cipher.getInstance返回实现指定转换的 Cipher 对象。
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器

            // 将需要加密的内容以utf-8的编码获得其字节数组
            byte[] byteContent = content.getBytes("utf-8");

            //用密钥初始化此 Cipher。
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化

            //doFinal(byte[] input) 按单部分操作加密或解密数据，或者结束一个多部分操作。
            byte[] encryptResult = cipher.doFinal(byteContent);



            //使用base64将字节数组编码成文本
            /**
             * 标准的Base64并不适合直接放在URL里传输，因为URL编码器会把标准Base64中的“/”和“+”字符变为形如“%XX”的形式，而这些“%”号在存入数据库时还需要再进行转换
             * 为解决此问题，可采用一种用于URL的改进Base64编码，它不仅在末尾去掉填充的'='号，并将标准Base64中的“+”和“/”分别改成了“-”和“_”，
             * 这样就免去了在URL编解码和数据库存储时所要作的转换，避免了编码信息长度在此过程中的增加，并统一了数据库、表单等处对象标识符的格式。
             */
//            String encode = Base64.getUrlEncoder().encodeToString(encryptResult);
            String encode = Base64.getEncoder().encodeToString(encryptResult);

            return encode;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @Description: AES解密
     * @author wang
     * @date 2017-7-30 下午04:05:32
     * @param content  待解密内容
     * @param AESKey 解密密钥
     * @return
     */
    public static String decrypt(String content, String AESKey){
        try{
            //使用Base64解码为二进制字节数组
            byte[] contentByte = Base64.getDecoder().decode(content);
//            byte[] contentByte = Base64.getUrlDecoder().decode(content);

            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom random=SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(AESKey.getBytes());

            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(contentByte);
            return new String(result,"utf-8"); // 解密
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


    //test
    public static void main(String[] args) throws IOException {
        String a = "http://localhost/index.html";
        String aesKey="密钥";

        //加密
        String encodeStr = encrypt(a,aesKey);
        System.out.println(encodeStr);

        //解密
        String decodeStr = decrypt(encodeStr,aesKey);
        System.out.println(decodeStr);
    }
}