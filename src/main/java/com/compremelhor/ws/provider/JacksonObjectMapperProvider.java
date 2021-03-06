package com.compremelhor.ws.provider;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

@Provider
@Produces(MediaType.APPLICATION_JSON)
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
		//mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);		
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return mapper;
	}
}
