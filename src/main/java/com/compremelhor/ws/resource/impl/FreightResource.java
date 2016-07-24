package com.compremelhor.ws.resource.impl;

import static javax.naming.Context.URL_PKG_PREFIXES;

import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
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
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import com.compremelhor.model.entity.Address;
import com.compremelhor.model.entity.Freight;
import com.compremelhor.model.entity.FreightType;
import com.compremelhor.model.entity.Purchase;
import com.compremelhor.model.exception.InvalidEntityException;
import com.compremelhor.model.exception.UnknownAttributeException;
import com.compremelhor.model.remote.EJBRemote;
import com.compremelhor.ws.annotation.TokenAuthenticated;
import com.compremelhor.ws.resource.AbstractResource;
import com.google.gson.Gson;

@Path("/purchases/{id:[1-9][0-9]*}/freight")
@TokenAuthenticated
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
	
	@GET
	@Path("/find")
	@Produces(MediaType.APPLICATION_JSON)
	public Freight getResourceByAttribute(@javax.ws.rs.core.Context UriInfo info) {
		Map<String, Object> map = new HashMap<String,Object>();
		info.getQueryParameters(true).forEach((s, v) -> map.put(s, v.get(0)));
		Freight u = null;
		try {
			u  = freightService.find(map);
		} catch (UnknownAttributeException e) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		
		if (u == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
		return u;
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
			ResponseBuilder builder = Response.status(406);
			builder.header("errors", e.getMessage());
			return builder.build();

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
		
		freight.setPurchase(p);
		freight.setId(id);
		freight.setDateCreated(current.getDateCreated());
		freight.setLastUpdated(LocalDateTime.now());
		
		try {
			freight = freightService.edit(freight);
			return Response.ok(Entity.entity(freight, MediaType.APPLICATION_JSON)).build();
		} catch (InvalidEntityException e) {
			ResponseBuilder builder = Response.status(406);
			builder.header("errors", e.getMessage());
			return builder.build();

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
		try {
			return new Gson().fromJson(json, Freight.class);
		} catch(Exception e) {
			JSONObject jsonObject = new JSONObject(json);
			
			Freight freight = new Freight();
			
			try {
				freight.setRideValue(jsonObject.getDouble("rideValue"));
				freight.setId(jsonObject.getInt("id"));
			} catch (Exception e1) {}
			
//			for (FreightType type : FreightType.values()) {
//				if (jsonObject.getString("type") != null && jsonObject.getString("type").trim().toUpperCase().equals(type.toString())) {
//					freight.setType(FreightType.valueOf(jsonObject.getString("type")));
//				}
//			}
			
			if (jsonObject.getJSONObject("freightType") != null) {
				JSONObject freightTypeJson = jsonObject.getJSONObject("freightType");
				FreightType freightType = new FreightType();
				freightType.setId(freightTypeJson.getInt("id"));			
				freight.setFreightType(freightType);
			}
			
			if (jsonObject.getJSONObject("shipAddress") != null) {
				JSONObject addressJson = jsonObject.getJSONObject("shipAddress");
				
				Address address = new Address();
				address.setId(addressJson.getInt("id"));
				freight.setShipAddress(address);
			}
			
			JSONArray jsonArrayDate = jsonObject.getJSONArray("startingDate");
			if (jsonArrayDate != null) {
				freight.setStartingDate(LocalDate.of(jsonArrayDate.getInt(0),jsonArrayDate.getInt(1), jsonArrayDate.getInt(2)));
			}
			JSONArray jsonArrayTime = jsonObject.getJSONArray("startingTime");
			if (jsonArrayTime != null) {
				freight.setStartingTime(LocalTime.of(jsonArrayTime.getInt(0),jsonArrayTime.getInt(1), 0));
			}
			
			return freight;
		}
	}
}
