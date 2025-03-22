package Y1SHUI.common.weixin;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
@Slf4j

public class SignatureUtil {

    /*
    验证签名：
    原理是开发者和微信之间约定了一个令牌
    微信发送请求时，包含token/timestap/nonce三个参数
    同时包含用这三个参数加密后的字符串
    开发者收到后用同样的令牌和同样的加密算法进行加密
    如果一致就能说明请求来自服务器，因为：
    1.令牌只有我们知道
    2.加密算法无法被破解
     */

    public static boolean check(String token, String signature, String timestamp, String nonce){
        String[] arr = new String[]{token, timestamp, nonce};

        log.info("arr={}", Arrays.toString(arr));
        sort(arr);
        log.info("arr={}", Arrays.toString(arr));
        StringBuilder content = new StringBuilder();
        for(String str : arr){
            content.append(str);
        }
        log.info("content={}", content);
        MessageDigest md = null;
        String tmpStr = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = byteToStr(digest);
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        log.info("tmpStr= {}",tmpStr);
        log.info("signature= {}",signature.toUpperCase());
        return tmpStr != null && tmpStr.equals(signature.toUpperCase());//统一大写
    }

    private static String byteToStr(byte[] byteArray) {
        StringBuilder strDigest = new StringBuilder();
        for(byte b : byteArray){
            strDigest.append(byteToHexStr(b));
        }
        return strDigest.toString();
    }

    private static String byteToHexStr(byte mByte) {
        char[] digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int high = (mByte >>> 4) & 0x0F; // 取高 4 位
        int low = mByte & 0x0F;          // 取低 4 位
        char[] temp = new char[2];
        temp[0] = digit[high];
        temp[1] = digit[low];
        return new String(temp);
    }

    private static void sort(String[] arr){
        for(int i = 0; i < arr.length - 1; i++){
            for(int j = i + 1; j < arr.length; j++){
                if(arr[j].compareTo(arr[i]) < 0){
                    String temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }
    }
}
