package com.compremelhor.ws.resource;

import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import com.compremelhor.model.entity.EntityModel;
import com.compremelhor.model.exception.InvalidEntityException;
import com.compremelhor.model.exception.UnknownAttributeException;
import com.compremelhor.model.remote.EJBRemote;
import com.google.gson.Gson;

public abstract class AbstractResource<T extends EntityModel> implements Resource<T> {
	public static final String JNDI_CLIENT = "org.jboss.ejb.client.naming";
	private Class<T> clazz;
	protected String rootPath;
	protected EJBRemote<T> service;
	protected final Context context;
	
	@Inject Logger logger;
	
	public AbstractResource(Class<T> clazz, String rootPath) throws NamingException {
		final Properties jndiProperties = new Properties();
		jndiProperties.setProperty(Context.URL_PKG_PREFIXES, JNDI_CLIENT);
		this.context = new InitialContext(jndiProperties);
		this.service = lookupService();
		this.clazz = clazz;
		this.rootPath = rootPath;
	}
	
	abstract protected EJBRemote<T> lookupService() throws NamingException;
	
	public T getResource(@PathParam("id") int id) {
		T t = service.find(id);
		if (t == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		return t; 
	}
	
	public List<T> getAllResources() {
		return service.findAll();
	}
	
	public List<T> getAllResources(@QueryParam("start") int start,
											 @QueryParam("size") int size) {
		return service.findAll(start, size);
	}
	
	public Response createResource(InputStream is) {
		T t = getEntityFromInputStream(is);
		
		t.setDateCreated(LocalDateTime.now());
		t.setLastUpdated(LocalDateTime.now());
		
		try {
			t = service.create(t);
			return Response.created(URI.create("/"+rootPath+"/" + t.getId())).build();
		} catch (InvalidEntityException e) {
			ResponseBuilder builder = Response.status(406);
			builder.header("errors", e.getMessage());
			return builder.build();
			
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage());
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	public T deleteResource(@PathParam("id") int id) {
		T t = service.find(id);
		if (t == null) throw new WebApplicationException(Response.Status.GONE);
		service.delete(t);
		return t;
	}
	
	public Response updateResource(@PathParam("id") int id, InputStream is) {
		T t = getEntityFromInputStream(is);
				
		T current = service.find(id);		
		if (current == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		t.setId(id);
		t.setDateCreated(current.getDateCreated());
		t.setLastUpdated(LocalDateTime.now());
		
		try {
			service.edit(t);
			return Response.ok(Entity.entity(t, MediaType.APPLICATION_JSON)).build();
		} catch (InvalidEntityException e) {
			ResponseBuilder builder = Response.status(406);
			builder.header("errors", e.getMessage());
			return builder.build();
			
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage());
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("/findAll")
	@Produces(MediaType.APPLICATION_JSON)
	public List<T> getAllResourceByAttribute(@javax.ws.rs.core.Context UriInfo info) {
		Map<String, Object> map = new HashMap<String,Object>();
		info.getQueryParameters(true).forEach((s, v) -> map.put(s, v.get(0)));
		List<T> u = null;
		try {
			u  = service.findAll(map);
		} catch (UnknownAttributeException e) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		
		if (u == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		return u;
	}
	
	@GET
	@Path("/find")
	@Produces(MediaType.APPLICATION_JSON)
	public T getResourceByAttribute(@javax.ws.rs.core.Context UriInfo info) {
		Map<String, Object> map = new HashMap<String,Object>();
		info.getQueryParameters(true).forEach((s, v) -> map.put(s, v.get(0)));
		T u = null;
		try {
			u  = service.find(map);
		} catch (UnknownAttributeException e) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		
		if (u == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		return u;
	}

	
	@SuppressWarnings("unchecked")
	public EJBRemote<EntityModel> lookupService(String jndi) throws NamingException {
		return (EJBRemote<EntityModel>) context.lookup(jndi);
	}
	
	public T getEntityFromInputStream(InputStream is) {
		String json = "";
		try (Scanner s = new Scanner(is)) {
			json = s.useDelimiter("\\A").next();
		}
		return new Gson().fromJson(json, clazz);
	}
}
