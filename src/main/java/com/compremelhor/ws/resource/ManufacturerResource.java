package com.compremelhor.ws.resource;

import java.time.LocalDateTime;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.compremelhor.model.entity.Manufacturer;

@Path("/manufacturer")
public class ManufacturerResource {
	
	
	@GET
	@Produces("application/json")
	public Manufacturer getGreeting() {
		Manufacturer m = new Manufacturer();
		m.setName("COCA-COLA");
		m.setDateCreated(LocalDateTime.now());
		m.setLastUpdated(LocalDateTime.now());
		return m;
	}
}
