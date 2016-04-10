package com.compremelhor.ws.resource.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.compremelhor.model.entity.Partner;
import com.compremelhor.model.entity.Sku;
import com.compremelhor.model.entity.User;
import com.compremelhor.model.exception.UnknownAttributeException;
import com.compremelhor.model.remote.EJBRemote;
import com.compremelhor.ws.annotation.TokenAuthenticated;
import com.compremelhor.ws.resource.AbstractResource;

@Path("/skus")
@TokenAuthenticated
public class SkuResource extends AbstractResource<Sku> {
	private String jndiPartner = "ejb:/compre_melhor_ws/PartnerEJB!com.compremelhor.model.remote.EJBRemote";
	public SkuResource() throws NamingException {
		super(Sku.class, "skus");
	}

	@SuppressWarnings("unchecked")
	@Override
	public EJBRemote<Sku> lookupService() throws NamingException {
		return (EJBRemote<Sku>) context
				.lookup("ejb:/compre_melhor_ws/SkuEJB!com.compremelhor.model.remote.EJBRemote");
	}
	
	@Override
	public Sku getResource(@PathParam("id") int id) {
		Set<String> fetches = new HashSet<String>();
		fetches.add("categories");
		return service.find(id, fetches);
	}
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Sku getResourceByAttribute(@Context UriInfo info) {
		Map<String, String> map = new HashMap<String,String>();
		info.getQueryParameters(true).forEach((s, v) -> map.put(s, v.get(0)));
		Sku u = null;
		try {
			u  = service.find(map);
		} catch (UnknownAttributeException e) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		
		if (u == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		return u;
	}
}
