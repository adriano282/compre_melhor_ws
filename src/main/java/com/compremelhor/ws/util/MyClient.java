package com.compremelhor.ws.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class MyClient {
	public static SSLSocketFactory 
		getFactory(InputStream keyInput, String pKeyPassword) throws Exception {
		KeyManagerFactory keyManagerFactory =
				KeyManagerFactory.getInstance("SunX509");
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		
		keyStore.load(keyInput, pKeyPassword.toCharArray());
		keyInput.close();
		
		keyManagerFactory.init(keyStore, pKeyPassword.toCharArray());
		
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());
		
		return context.getSocketFactory();
		
	}
	
	public static void main(String args[]) throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL url = new URL("https://localhost:8080/compre_melhor_ws/rest/users/180");
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		con.setSSLSocketFactory(getFactory(classLoader.getResourceAsStream("cacerts.jks"),
		"270320151415pikachu"));
		}
}
