package com.compremelhor.ws.resource;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.compremelhor.model.entity.EntityModel;

public interface Resource<T extends EntityModel> {
	
	@GET
	@Path("/{id:[1-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public T getResource(@PathParam("id") int id);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<T> getAllResources();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<T> getAllResources(	@QueryParam("start") int start,
								 	@QueryParam("size") int size);
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createResource(InputStream is);
	
	@DELETE
	@Path("/{id:[1-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)	
	public T deleteResource(@PathParam("id") int id);
	
	@PUT
	@Path("/{id:[1-9][0-9]*}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateResource(@PathParam("id") int id, InputStream is);
}
