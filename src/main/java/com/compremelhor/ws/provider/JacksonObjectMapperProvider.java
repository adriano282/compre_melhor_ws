package com.compremelhor.ws.provider;

import javax.ejb.Stateful;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

@Provider
@Stateful
public class JacksonObjectMapperProvider implements ContextResolver<ObjectMapper> {
	ObjectMapper defaultObjectMapper;
	
	public JacksonObjectMapperProvider() {
		defaultObjectMapper = createDefaultMapper();
	}
	
	@Override
	public ObjectMapper getContext(Class<?> type) {
		return defaultObjectMapper;
	}
	
	private static ObjectMapper createDefaultMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JSR310Module());
		mapper.registerModule(new Jdk8Module());
		mapper.registerModule(new JodaModule());
		mapper.enable(SerializationFeature.INDENT_OUTPUT);		
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.findAndRegisterModules();				
		return mapper;
	}
}
