package com.gemini.toolkit.common.utils;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtUtil {
	private static Logger logger = LoggerFactory.getLogger(JwtUtil.class);

//	public static final long EXPIRATION_TIME = 30*60*1000;
	public static final String SECRET = "ThisIsASecret";
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String USER_NAME = "userId";

	 /**
     * 签名生成
     * @param user
     * @return
     */
	public static String generateToken(String userInfo, long expirationTime) {
		String jwt = JWT.create().withIssuer("auth0").withClaim("userInfo", userInfo)
				.withExpiresAt(new Date(System.currentTimeMillis() + expirationTime)).sign(Algorithm.HMAC256(SECRET));
		return TOKEN_PREFIX + jwt;
	}

	/**
	 * 签名验证
	 *
	 * @param token
	 * @return
	 */
	public static boolean verify(String token) {

		try {
			JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).withIssuer("auth0").build();
			verifier.verify(token.replace(TOKEN_PREFIX, ""));
			logger.debug("认证通过");
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获取登录用户
	 * @param token
	 * @return
	 */
	public static String getUserInfoFromToken(String token) {
		String username = "";
		try {
			JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).withIssuer("auth0").build();
			DecodedJWT jwt = verifier.verify(token.replace(TOKEN_PREFIX, ""));
			username = jwt.getClaim("userInfo").asString();
		} catch (Exception e) {
			return username;
		}
		return username;

	}



}
