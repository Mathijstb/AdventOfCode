package cryptUtils;

import org.apache.commons.codec.digest.DigestUtils;

public class Crypto {

    public static String hashMD5(String value) {
        return DigestUtils.md5Hex(value);
    }
}
