package com.compremelhor.ws.resource.impl;

import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

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

import com.compremelhor.model.entity.Address;
import com.compremelhor.model.entity.User;
import com.compremelhor.model.exception.InvalidEntityException;
import com.compremelhor.model.remote.EJBRemote;
import com.compremelhor.ws.annotation.TokenAuthenticated;
import com.compremelhor.ws.resource.AbstractResource;
import com.google.gson.Gson;

@Path("/users")
@TokenAuthenticated
public class UserResource extends AbstractResource<User>{
	private String jndiAddress = "ejb:/compre_melhor_ws/AddressEJB!com.compremelhor.model.remote.EJBRemote";
	public UserResource() throws NamingException {
		super(User.class, "users");
	}

	@SuppressWarnings("unchecked")
	public EJBRemote<User> lookupService() throws NamingException {
		return (EJBRemote<User>) context
				.lookup("ejb:/compre_melhor_ws/UserEJB!com.compremelhor.model.remote.EJBRemote");
	}	

	@GET
	@Path("/{id: [1-9][0-9]*}/addresses/{id: [1-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON) 
	public Address getAddress(@Context UriInfo info) {
		int userId = Integer.valueOf(info.getPathSegments().get(1).toString());
		int addressId = Integer.valueOf(info.getPathSegments().get(3).toString());
		
		try {
			if (lookupService().find(userId) == null) throw new WebApplicationException(Response.Status.NOT_FOUND);			
			Address address = (Address) lookupService(jndiAddress).find(addressId);
			if (address == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
			return address;
		} catch (NamingException e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DELETE
	@Path("/{id: [1-9][0-9]*}/addresses/{id: [1-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Address removeAddress(@Context UriInfo info) {
		int userId = Integer.valueOf(info.getPathSegments().get(1).toString());
		int addressId = Integer.valueOf(info.getPathSegments().get(3).toString());
		
		try {
			if (lookupService().find(userId) == null) throw new WebApplicationException(Response.Status.NOT_FOUND);			
			Address address = (Address) lookupService(jndiAddress).find(addressId);
			if (address == null) throw new WebApplicationException(Response.Status.GONE);
			lookupService(jndiAddress).delete(address);
			return address;
		} catch (NamingException e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GET
	@Path("/{id: [1-9][0-9]*}/addresses")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Address> getAddresses(@PathParam("id") int id) {
		Set<String> fetches = new HashSet<>();
		fetches.add("addresses");
		User u = (User) service.find(id, fetches);
		return u.getAddresses();
	}
	
	@POST
	@Path("/{id:[1-9][0-9]*}/addresses/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAddress(InputStream is, @Context UriInfo info) {		
		Address ad = getAddressFromInputStream(is);
		ad.setDateCreated(LocalDateTime.now());
		ad.setLastUpdated(LocalDateTime.now());
				
		int userId = Integer.valueOf(info.getPathSegments().get(1).toString());
		
		User u = service.find(userId);
		
		if (u == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		
		ad.setUser(u);
		
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
				
		int userId = Integer.valueOf(info.getPathSegments().get(1).toString());
		int addressId = Integer.valueOf(info.getPathSegments().get(3).toString());
		
		User u = service.find(userId);
		ad.setUser(u);
		
		if (u == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		
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
