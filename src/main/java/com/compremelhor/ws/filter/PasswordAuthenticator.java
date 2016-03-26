package com.compremelhor.ws.filter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import com.compremelhor.ws.annotation.TokenAuthenticated;

@TokenAuthenticated
@Provider
@Priority(Priorities.AUTHENTICATION)
public class PasswordAuthenticator implements ContainerRequestFilter {
	
	protected Map<String, String> userSecretMap;
	
	public PasswordAuthenticator() {		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Properties props = new Properties();
		try {
			props.load(classLoader.getResourceAsStream("tokens.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, String> map = new HashMap<>();
		props.forEach((k, v) -> map.put((String)k, (String)v));
		
		this.userSecretMap = map;
	}
	
	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		String authorization = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		if (authorization == null) throw new NotAuthorizedException("TOKEN IS REQUIRED");
		
		String[] split = authorization.split(" ");
		final String user = split[0];
		String token = split[1];
		
		String secret = userSecretMap.get(user);
		if (secret == null) throw new NotAuthorizedException("TOKEN IS REQUIRED");
				
		if (!secret.equals(token))  throw new NotAuthorizedException("TOKEN IS REQUIRED");
		
		final SecurityContext securityContext =
				requestContext.getSecurityContext();
		requestContext.setSecurityContext(new SecurityContext() {
			
			@Override
			public boolean isUserInRole(String role) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isSecure() {
				return securityContext.isSecure();
			}
			
			@Override
			public Principal getUserPrincipal() {
				return new Principal() {
					
					@Override
					public String getName() {
						return user;
					}
				};
			}
			
			@Override
			public String getAuthenticationScheme() {
				return "TOKEN";
			}
		});
	}
	

}
