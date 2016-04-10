package com.compremelhor.ws.resource.impl;

import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.compremelhor.model.entity.Sku;
import com.compremelhor.model.entity.Stock;
import com.compremelhor.model.exception.InvalidEntityException;
import com.compremelhor.model.exception.UnknownAttributeException;
import com.compremelhor.model.remote.EJBRemote;
import com.compremelhor.ws.annotation.TokenAuthenticated;
import com.compremelhor.ws.resource.AbstractResource;

@Path("/stock")
@TokenAuthenticated
public class StockResource extends AbstractResource<Stock> {

	public StockResource()
			throws NamingException {
		super(Stock.class, "stock");
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
	protected EJBRemote<Stock> lookupService() throws NamingException {
		return (EJBRemote<Stock>) context
				.lookup("ejb:/compre_melhor_ws/StockEJB!com.compremelhor.model.remote.EJBRemote");

	}
	
	public Response createResource(InputStream is) {
		Stock t = getEntityFromInputStream(is);
		t.setDateCreated(LocalDateTime.now());
		t.setLastUpdated(LocalDateTime.now());
		
		try {
			t = service.create(t);
			return Response.created(URI.create("/"+rootPath+"/" + t.getId())).build();
		} catch (InvalidEntityException e) { 
			return Response.status(406).entity(Entity.json(e.getMessage())).build();
		} catch (Exception e) {
			return Response.status(406).entity(Entity.json(e.getMessage())).build();
		}
	}
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Stock getResourceByAttribute(@Context UriInfo info) {
		Map<String, String> map = new HashMap<String,String>();
		info.getQueryParameters(true).forEach((s, v) -> map.put(s, v.get(0)));
		Stock u = null;
		try {
			u  = service.find(map);
		} catch (UnknownAttributeException e) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		
		if (u == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		return u;
	}


}
