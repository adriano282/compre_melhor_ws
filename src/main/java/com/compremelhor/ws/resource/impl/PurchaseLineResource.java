package com.compremelhor.ws.resource.impl;

import static javax.naming.Context.URL_PKG_PREFIXES;

import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.inject.Inject;
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

import com.compremelhor.model.entity.Purchase;
import com.compremelhor.model.entity.PurchaseLine;
import com.compremelhor.model.exception.InvalidEntityException;
import com.compremelhor.model.remote.EJBRemote;
import com.compremelhor.ws.annotation.TokenAuthenticated;
import com.compremelhor.ws.resource.AbstractResource;
import com.google.gson.Gson;

@Path("/purchases/{id:[1-9][0-9]*}/lines")
@TokenAuthenticated
public class PurchaseLineResource {
	private String jndiLine = 
			"ejb:/compre_melhor_ws/PurchaseLineEJB!com.compremelhor.model.remote.EJBRemote";
	private String jndiPurchase = 
			"ejb:/compre_melhor_ws/PurchaseEJB!com.compremelhor.model.remote.EJBRemote";
	
	private EJBRemote<PurchaseLine> lineService;
	private EJBRemote<Purchase> purchaseService;
	
	@Inject private Logger logger;
	
	protected final javax.naming.Context context;
	
	@SuppressWarnings("unchecked")
	public PurchaseLineResource() throws NamingException {
		final Properties jndiProperties = new Properties();
		jndiProperties.setProperty(URL_PKG_PREFIXES, AbstractResource.JNDI_CLIENT);
		this.context = new InitialContext(jndiProperties);
		this.lineService = (EJBRemote<PurchaseLine>) getService(jndiLine);
		this.purchaseService = (EJBRemote<Purchase>) getService(jndiPurchase);
	}
		
	public EJBRemote<?> getService(String jndi) throws NamingException {
		return (EJBRemote<?>) context.lookup(jndi);
	}
	
	@GET
	@Path("/{id:[1-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public PurchaseLine getResource(@PathParam("id") int id, @Context UriInfo info) {
		
		int purchaseId = Integer.valueOf(info.getPathParameters().getFirst("id"));
				
		Purchase p = purchaseService.find(purchaseId);
		if (p == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		
		PurchaseLine line = lineService.find(id);
		
		if (line == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		
		return line;
	}
	
	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createLine(InputStream is, @Context UriInfo info) {
		PurchaseLine line = getEntityFromInputStream(is);
		
		int purchaseId = Integer.valueOf(info.getPathParameters().getFirst("id"));
		
		Purchase p = purchaseService.find(purchaseId);
		
		if (p == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		
		line.setPurchase(p);
		line.setDateCreated(LocalDateTime.now());
		line.setLastUpdated(LocalDateTime.now());
		
		try {
			line = lineService.create(line);
			String uri = info.getPath() + "/" +line.getId();
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
		PurchaseLine line = getEntityFromInputStream(is);
		int purchaseId = Integer.valueOf(info.getPathParameters().getFirst("id"));
				
		Purchase p = purchaseService.find(purchaseId);
		if (p == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		
		PurchaseLine current = lineService.find(id);
		if (current == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		
		line.setId(id);
		line.setDateCreated(current.getDateCreated());
		line.setLastUpdated(LocalDateTime.now());
		
		try {
			line = lineService.edit(line);
			return Response.ok(Entity.entity(line, MediaType.APPLICATION_JSON)).build();
		} catch (InvalidEntityException e) {
			return Response.status(406).entity(Entity.json(e.getMessage())).build();
		} catch (Exception e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}
	
	@DELETE
	@Path("/{id:[1-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public PurchaseLine deleteLine(@PathParam("id") int id) {
		PurchaseLine t = lineService.find(id);
		if (t == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		lineService.delete(t);
		return t;
	}
	
	public PurchaseLine getEntityFromInputStream(InputStream is) {
		String json = "";
		try (Scanner s = new Scanner(is)) {
			json = s.useDelimiter("\\A").next();
		}
		return new Gson().fromJson(json, PurchaseLine.class);
	}
}
