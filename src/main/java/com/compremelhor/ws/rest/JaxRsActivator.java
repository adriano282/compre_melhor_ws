package com.compremelhor.ws.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/rest")
public class JaxRsActivator extends Application {
/*	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> resources = new HashSet<>();
		resources.add(JacksonFeature.class);
		resources.add(JacksonObjectMapperProvider.class);
		resources.add(ManufacturerResource.class);
		return resources;
	}
	*/
}
