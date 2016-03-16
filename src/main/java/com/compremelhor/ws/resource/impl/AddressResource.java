package com.compremelhor.ws.resource.impl;

import java.util.HashSet;
import java.util.List;
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

import com.compremelhor.model.entity.Address;
import com.compremelhor.model.entity.EntityModel;
import com.compremelhor.model.entity.Partner;
import com.compremelhor.model.entity.User;
import com.compremelhor.model.remote.EJBRemote;
import com.compremelhor.ws.resource.AbstractResource;

@Path("{resource}/{id:[1-9][0-9]*}")
public class AddressResource extends AbstractResource<EntityModel>{

	private String jndiPartner = "ejb:/compre_melhor_ws/PartnerEJB!com.compremelhor.model.remote.EJBRemote";
	private String jndiUser = "ejb:/compre_melhor_ws/UserEJB!com.compremelhor.model.remote.EJBRemote";
	private String jndiAddress = "ejb:/compre_melhor_ws/AddressEJB!com.compremelhor.model.remote.EJBRemote";
	
	public AddressResource() throws NamingException {
		super(EntityModel.class, "");
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings("unchecked")
	public EJBRemote<EntityModel> lookupService() throws NamingException {
		return (EJBRemote<EntityModel>) context
				.lookup("ejb:/compre_melhor_ws/PartnerEJB!com.compremelhor.model.remote.EJBRemote");
	}

	@SuppressWarnings("unchecked")
	public EJBRemote<EntityModel> lookupService(String jndi) throws NamingException {
		return (EJBRemote<EntityModel>) context.lookup(jndi);
	}
	
	@GET
	@Path("/addresses")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Address> getAllAddresses(@Context UriInfo info) {
		String resource = info.getPathParameters().getFirst("resource");
		String resourceId = info.getPathParameters().getFirst("id");
				
		try {
			
			Set<String> fetches = new HashSet<>();
			fetches.add("addresses");
			
			if (resource.equals("user")) { 
				this.service = lookupService(jndiUser);
				return ((User)service.get(Integer.valueOf(resourceId), fetches)).getAddresses();
			}
			if (resource.equals("partner")) { 
				this.service = lookupService(jndiPartner); 
				return ((Partner)service.get(Integer.valueOf(resourceId),fetches)).getAddresses();				
			} 
			
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		} catch (NamingException e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GET
	@Path("/addresses/{id: [1-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Address getResource(@PathParam("id") int id) {
		try {
			this.service = (EJBRemote<EntityModel>)lookupService(jndiAddress);
			return (Address) service.get(id);
		} catch (NamingException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
	}

}
