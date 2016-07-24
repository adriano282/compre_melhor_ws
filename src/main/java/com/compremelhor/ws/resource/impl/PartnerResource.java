package com.compremelhor.ws.resource.impl;

import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.naming.NamingException;
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

import com.compremelhor.model.entity.Address;
import com.compremelhor.model.entity.FreightType;
import com.compremelhor.model.entity.Partner;
import com.compremelhor.model.exception.InvalidEntityException;
import com.compremelhor.model.remote.EJBRemote;
import com.compremelhor.ws.annotation.TokenAuthenticated;
import com.compremelhor.ws.resource.AbstractResource;
import com.google.gson.Gson;

@Path("/partners")
@TokenAuthenticated
public class PartnerResource extends AbstractResource<Partner>{
	private String jndiAddress = "ejb:/compre_melhor_ws/AddressEJB!com.compremelhor.model.remote.EJBRemote";
	public PartnerResource() throws NamingException { super(Partner.class, "partners"); }
	
	@SuppressWarnings("unchecked")
	public EJBRemote<Partner> lookupService() throws NamingException {
		return (EJBRemote<Partner>) context
				.lookup("ejb:/compre_melhor_ws/PartnerEJB!com.compremelhor.model.remote.EJBRemote");
	}
	
	@GET
	@Path("/{id: [1-9][0-9]*}/freight_types")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FreightType> getFreightTypes(@PathParam("id") int id) {
		Set<String> fecthes = new HashSet<>();
		fecthes.add("freightTypes");
		Partner p = (Partner) service.find(id, fecthes);
		return p.getFreightTypes();
	}
	
	@GET
	@Path("/{id: [1-9][0-9]*}/addresses")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Address> getAddresses(@PathParam("id") int id) {
		Set<String> fetches = new HashSet<>();
		fetches.add("addresses");
		Partner p = (Partner) service.find(id, fetches);
		return p.getAddresses();
	}
	
	@POST
	@Path("/{id:[1-9][0-9]*}/addresses/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAddress(InputStream is, @Context UriInfo info) {		
		Address ad = getAddressFromInputStream(is);
		ad.setDateCreated(LocalDateTime.now());
		ad.setLastUpdated(LocalDateTime.now());
				
		int partnerId = Integer.valueOf(info.getPathSegments().get(1).toString());
		
		Partner p = service.find(partnerId);
		
		if (p == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		
		ad.setPartner(p);
		
		try {
			ad = (Address) lookupService(jndiAddress).create(ad);
			return Response.created(URI.create("/addresses/" + ad.getId())).build();
		} catch (InvalidEntityException e) { 
			return Response.status(406).entity(Entity.json(e.getMessage())).build();
		} catch (NamingException e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}	
	
	@PUT
	@Path("/{id:[1-9][0-9]*}/addresses/{id:[1-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateAddress(InputStream is, @Context UriInfo info) {		
		Address ad = getAddressFromInputStream(is);
		ad.setLastUpdated(LocalDateTime.now());
				
		int partnerId = Integer.valueOf(info.getPathSegments().get(1).toString());
		int addressId = Integer.valueOf(info.getPathSegments().get(3).toString());
		
		Partner p = service.find(partnerId);
		ad.setPartner(p);
		
		if (p == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		ad.setPartner(p);
		try {
			Address current = (Address) lookupService(jndiAddress).find(addressId);
			
			if (current == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
			ad.setId(addressId);
			ad.setDateCreated(current.getDateCreated());
			ad = (Address) lookupService(jndiAddress).edit(ad);
			return Response.ok(Entity.entity(ad, MediaType.APPLICATION_JSON)).build();
		} catch (InvalidEntityException e) { 
			return Response.status(406).entity(Entity.json(e.getMessage())).build();
		} catch (NamingException e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}	

	private Address getAddressFromInputStream(InputStream is) {
		String json = "";
		try (Scanner s = new Scanner(is)) {
			json = s.useDelimiter("\\A").next();
		}
		return new Gson().fromJson(json, Address.class);
	}
}
