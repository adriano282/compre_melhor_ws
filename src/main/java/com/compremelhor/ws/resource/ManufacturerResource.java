package com.compremelhor.ws.resource;

import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.compremelhor.model.entity.Manufacturer;
import com.compremelhor.model.exception.InvalidEntityException;
import com.compremelhor.model.remote.EJBRemote;
import com.google.gson.Gson;

@Path("/manufacturers")
public class ManufacturerResource {
	
	private final Context context;
	private EJBRemote manufacturerService;
	
	@Inject private Logger logger;
	
	public ManufacturerResource() throws NamingException {
		final Properties jndiProperties = new Properties();
		jndiProperties.setProperty(Context.URL_PKG_PREFIXES, 
				"org.jboss.ejb.client.naming");
		this.context = new InitialContext(jndiProperties);
		this.manufacturerService = lookupManufacturerService();
		
	}
	
	private EJBRemote lookupManufacturerService() throws NamingException {
		return (EJBRemote) 
				context
				.lookup("ejb:/compre_melhor_ws/"
						+ "ManufacturerEJB!com.compremelhor.model.remote.EJBRemote");
	}
	
	@GET
	@Path("/{id:[1-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Manufacturer getManufacturer(@PathParam("id") int id) {
		logger.log(Level.INFO, "GET /manufacturers/" + id);
		return (Manufacturer) manufacturerService.get(id);
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createManufacturer(InputStream is) {
		String json = "";
		try (Scanner s = new Scanner(is)) {
			json = s.useDelimiter("\\A").next();
		}		
		
		Manufacturer m = new Gson().fromJson(json, Manufacturer.class);
		m.setDateCreated(LocalDateTime.now());
		m.setLastUpdated(LocalDateTime.now());
		
		try {
			m = (Manufacturer) manufacturerService.create(m);
			logger.log(Level.INFO, "POST /manufacturers/");
			return Response.created(URI.create("/manufacturers/" + m.getId())).build();
		} catch (InvalidEntityException e) {
			return Response.status(406).entity(Entity.json(e.getMessage())).build();
		}
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id:[1-9][0-9]*}")
	public Manufacturer deleteManufacturer(@PathParam("id") int id) {
		Manufacturer m = (Manufacturer) manufacturerService.get(id);
		manufacturerService.delete(m);
		logger.log(Level.INFO, "DELETE /manufacturers/"+ id);
		return m;
	}
	
	@PUT
	@Path("/{id:[1-9][0-9]*}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateManufacturer(@PathParam("id") int id, InputStream is) {
		String json = "";
		try (Scanner s = new Scanner(is)) {
			json = s.useDelimiter("\\A").next();
		}
		
		Manufacturer m = new Gson().fromJson(json, Manufacturer.class);
				
		try {
			Manufacturer current = null;
			if ((current = (Manufacturer) manufacturerService.get(id)) == null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			};
			
			m.setDateCreated(current.getDateCreated());
			m.setLastUpdated(LocalDateTime.now());
			
			manufacturerService.edit(m);
			return Response.ok(Entity.entity(m, MediaType.APPLICATION_JSON)).build();
		} catch (InvalidEntityException e) {
			return Response.status(406).entity(Entity.json(e.getMessage())).build();
		}
	}
	
}
