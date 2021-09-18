/**
 * (c) Hitachi, Ltd. 2018. All rights reserved.
 */
package com.gemini.toolkit.config;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;


/**
 * パスワードツール
 * 
 * @author BHH jiangtao
 *
 */
public class PasswordUtil {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PasswordUtil.class);
	/**
	 * ハッシュ化前のパスワードとソルトより、128桁のハッシュ化後のパスワードを生成する
	 * 
	 * @param passwordToHash
	 *            ハッシュ化前のパスワード
	 * @param salt
	 *            ソルト
	 * @return ハッシュ化後のパスワード
	 * @throws NoSuchAlgorithmException
	 */
	public static String getSecurePassword(String passwordToHash) throws NoSuchAlgorithmException {
		String generatedPassword = null;
		MessageDigest md = MessageDigest.getInstance("md5");
        try {
            //md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (Exception e) {
        }
		return generatedPassword;
	}
	
}
