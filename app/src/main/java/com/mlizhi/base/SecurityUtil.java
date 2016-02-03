package com.mlizhi.base;

import android.support.v4.view.MotionEventCompat;
import android.text.TextUtils;
import com.mlizhi.base.codec.binary.Base64;
import com.mlizhi.base.codec.digest.DigestUtils;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import p016u.aly.bq;

public class SecurityUtil {
    public static final String KEY_HTTP_CHARGE_MLIZHI = "h9sEVED84X81u9ev";
    private static final byte[] SECRET_KEY_HTTP;
    private static final byte[] SECRET_KEY_HTTP_CHARGE;
    public static final byte[] SECRET_KEY_HTTP_CHARGE_MLIZHI;
    public static final String SECRET_KEY_HTTP_PARAM = "MLZ2ADMIN Co.Ltd";
    private static final byte[] SECRET_KEY_NORMAL;

    static {
        SECRET_KEY_NORMAL = DigestUtils.md5(DigestUtils.md5("7U727ALEWH8".getBytes()));
        SECRET_KEY_HTTP = "sdk_mapmlz_201503".getBytes();
        SECRET_KEY_HTTP_CHARGE = "MAPMLZ-ANDY-XIAN-".getBytes();
        SECRET_KEY_HTTP_CHARGE_MLIZHI = KEY_HTTP_CHARGE_MLIZHI.getBytes();
    }

    public static String decrypt(String encValue) {
        if (TextUtils.isEmpty(encValue)) {
            return bq.f888b;
        }
        byte[] bytes = Base64.decodeBase64(Utils.getUTF8Bytes(encValue));
        if (bytes == null) {
            return bq.f888b;
        }
        bytes = new Crypter().decrypt(bytes, SECRET_KEY_NORMAL);
        if (bytes == null) {
            return bq.f888b;
        }
        return Utils.getUTF8String(bytes);
    }

    public static String encrypt(String value) {
        if (value == null) {
            return null;
        }
        return Utils.getUTF8String(Base64.encodeBase64(new Crypter().encrypt(Utils.getUTF8Bytes(value), SECRET_KEY_NORMAL)));
    }

    public static String encryptPassword(String targetText, String publicKey) {
        byte[] key = DigestUtils.md5(Utils.getUTF8Bytes(publicKey));
        swapBytes(key);
        reverseBits(key);
        return Utils.getUTF8String(Base64.encodeBase64(new Crypter().encrypt(Utils.getUTF8Bytes(targetText), key)));
    }

    public static byte[] encryptHttpBody(String body) {
        return Base64.encodeBase64(new Crypter().encrypt(Utils.getUTF8Bytes(body), SECRET_KEY_HTTP));
    }

    public static byte[] encryptHttpChargeBody(String body) {
        return new Crypter().encrypt(Utils.getUTF8Bytes(body), SECRET_KEY_HTTP_CHARGE);
    }

    public static byte[] encryptHttpChargePalipayBody(String body) {
        return Base64.encodeBase64(new Crypter().encrypt(Utils.getUTF8Bytes(body), SECRET_KEY_HTTP_CHARGE_MLIZHI));
    }

    public static byte[] decryptHttpEntity(HttpEntity entity) {
        byte[] buffer = null;
        try {
            buffer = EntityUtils.toByteArray(entity);
        } catch (IOException e) {
            Utils.m13E("\u89e3\u5bc6HttpEntity", e);
        }
        if (buffer != null) {
            return new Crypter().decrypt(buffer, SECRET_KEY_HTTP);
        }
        return buffer;
    }

    private static void swapBytes(byte[] b) {
        for (int i = 0; i < b.length; i += 2) {
            byte tmp = b[i];
            b[i] = b[i + 1];
            b[i + 1] = tmp;
        }
    }

    private static void reverseBits(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) (b[i] ^ MotionEventCompat.ACTION_MASK);
        }
    }

    public static String getTimestamp() {
        return String.valueOf((int) (System.currentTimeMillis() / 1000));
    }

    public static String getMd5String(String timestamp) {
        String key = null;
        try {
            key = md5(new StringBuilder(String.valueOf(timestamp)).append(SECRET_KEY_HTTP_PARAM).toString());
        } catch (NoSuchAlgorithmException e) {
            Utils.m13E("encrypt error!!!", e);
        }
        return key;
    }

    private static final String md5(String str) throws NoSuchAlgorithmException {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        byte[] btInput = str.getBytes();
        MessageDigest md5Inst = MessageDigest.getInstance("MD5");
        md5Inst.update(btInput);
        byte[] bytes = md5Inst.digest();
        StringBuffer strResult = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            strResult.append(hexDigits[(bytes[i] >> 4) & 15]);
            strResult.append(hexDigits[bytes[i] & 15]);
        }
        return strResult.toString();
    }
}
