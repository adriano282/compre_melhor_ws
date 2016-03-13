package com.compremelhor.ws.resource;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.compremelhor.model.entity.Manufacturer;
import com.compremelhor.model.exception.InvalidEntityException;
import com.compremelhor.model.remote.EJBRemote;

@Path("/manufacturer")
public class ManufacturerResource {
	
	private final Context context;
	private EJBRemote manufacturerService;
	
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
		return (Manufacturer) manufacturerService.get(id);
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createManufacturer(Manufacturer mfr) {
		
		System.out.println(mfr);
		try {
			Manufacturer m =  (Manufacturer) manufacturerService.create(mfr);
			return Response.ok(m).build();
		} catch (InvalidEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
		
	@DELETE
	@Path("/{id:[1-9][0-9]*}")
	public Manufacturer deleteManufacturer(@PathParam("id") int id) {
		Manufacturer m = (Manufacturer) manufacturerService.get(id);
		manufacturerService.delete(m);
		return m;
	}
	
}
