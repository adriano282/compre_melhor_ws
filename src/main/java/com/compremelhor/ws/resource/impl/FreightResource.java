package com.compremelhor.ws.resource.impl;

import static javax.naming.Context.URL_PKG_PREFIXES;

import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Scanner;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.compremelhor.model.entity.Freight;
import com.compremelhor.model.entity.Purchase;
import com.compremelhor.model.exception.InvalidEntityException;
import com.compremelhor.model.remote.EJBRemote;
import com.compremelhor.ws.resource.AbstractResource;
import com.google.gson.Gson;

@Path("/purchases/{id:[1-9][0-9]*}/freight")
public class FreightResource {
	private String jndiPurchase = 
			"ejb:/compre_melhor_ws/PurchaseEJB!com.compremelhor.model.remote.EJBRemote";
	private final String jndiFreight = 
			"ejb:/compre_melhor_ws/FreightEJB!com.compremelhor.model.remote.EJBRemote";
	
	private EJBRemote<Freight> freightService;
	private EJBRemote<Purchase> purchaseService;
		
	protected final javax.naming.Context context;
	
	@SuppressWarnings("unchecked")
	public FreightResource() throws NamingException {
		final Properties jndiProperties = new Properties();
		jndiProperties.setProperty(URL_PKG_PREFIXES, AbstractResource.JNDI_CLIENT);
		this.context = new InitialContext(jndiProperties);
		this.freightService = (EJBRemote<Freight>) getService(jndiFreight);
		this.purchaseService = (EJBRemote<Purchase>) getService(jndiPurchase);
	}
	
	public EJBRemote<?> getService(String jndi) throws NamingException {
		return (EJBRemote<?>) context.lookup(jndi);
	}
	
	@GET
	@Path("/{id:[1-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Freight getResource(@PathParam("id") int id, @Context UriInfo info) {
		
		int purchaseId = Integer.valueOf(info.getPathParameters().getFirst("id"));
		
		Purchase p = purchaseService.find(purchaseId);
		if (p == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		
		Freight freight = freightService.find(id);
				
		if (freight == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		return freight;
	}
	
	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createLine(InputStream is, @Context UriInfo info) {
		Freight freight = getEntityFromInputStream(is);
		int purchaseId = Integer.valueOf(info.getPathParameters().getFirst("id"));
		
		Purchase p = purchaseService.find(purchaseId);
		
		if (p == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		
		freight.setPurchase(p);
		freight.setDateCreated(LocalDateTime.now());
		freight.setLastUpdated(LocalDateTime.now());
		
		try {
			freight = freightService.create(freight);
			String uri = info.getPath() + "/" + freight.getId();
			return Response.created(URI.create(uri)).build();
		} catch (InvalidEntityException e) {
			return Response.status(406).entity(Entity.json(e.getMessage())).build();
		} catch (Exception e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PUT
	@Path("/{id:[1-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateLine(InputStream is, @PathParam("id") int id, @Context UriInfo info) {
		Freight freight = getEntityFromInputStream(is);
		int purchaseId = Integer.valueOf(info.getPathParameters().getFirst("id"));
				
		Purchase p = purchaseService.find(purchaseId);
		if (p == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		
		Freight current = freightService.find(id);
		if (current == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		
		freight.setId(id);
		freight.setDateCreated(current.getDateCreated());
		freight.setLastUpdated(LocalDateTime.now());
		
		try {
			freight = freightService.edit(freight);
			return Response.ok(Entity.entity(freight, MediaType.APPLICATION_JSON)).build();
		} catch (InvalidEntityException e) {
			return Response.status(406).entity(Entity.json(e.getMessage())).build();
		} catch (Exception e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id:[1-9][0-9]*}")
	public Freight deleteFreight(@PathParam("id") int id) {
		System.out.println("FREIGHT:: " + id);
		Freight t = freightService.find(id);
		if (t == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		freightService.delete(t);
		return t;
	}
	
	public Freight getEntityFromInputStream(InputStream is) {
		String json = "";
		try (Scanner s = new Scanner(is)) {
			json = s.useDelimiter("\\A").next();
		}
		return new Gson().fromJson(json, Freight.class);
	}
}
