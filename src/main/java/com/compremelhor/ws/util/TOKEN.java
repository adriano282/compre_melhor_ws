package com.compremelhor.ws.util;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.jboss.resteasy.util.Base64;

public class TOKEN {
	public static String generateToken(String secret) {
		long minutes = System.currentTimeMillis() /1000/ 60;
		String concat = secret + minutes;
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		}
		byte[] hash = digest.digest(concat.getBytes(Charset.forName("UTF-8")));
		return Base64.encodeBytes(hash);
	}
	
	public static void main(String...args) {
		final String token = TOKEN.generateToken("app_compre_melhor_adriano");
		System.out.println(token);
	}
}

