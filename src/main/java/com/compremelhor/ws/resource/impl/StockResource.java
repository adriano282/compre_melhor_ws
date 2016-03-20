package com.compremelhor.ws.resource.impl;

import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;

import javax.naming.NamingException;
import javax.ws.rs.Path;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import com.compremelhor.model.entity.Stock;
import com.compremelhor.model.exception.InvalidEntityException;
import com.compremelhor.model.remote.EJBRemote;
import com.compremelhor.ws.resource.AbstractResource;

@Path("/stock")
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

}
