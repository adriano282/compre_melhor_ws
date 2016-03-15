package com.compremelhor.ws.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.compremelhor.ws.provider.JacksonObjectMapperProvider;
import com.compremelhor.ws.resource.ManufacturerResource;

@ApplicationPath("/rest")
public class JaxRsActivator extends Application {
	
	/*@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> resources = new HashSet<>();
		resources.add(JacksonObjectMapperProvider.class);
		resources.add(ManufacturerResource.class);
		return resources;
	}*/
	
}
