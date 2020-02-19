package org.unibl.etf.mdp.service;

import java.util.ArrayList;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.unibl.etf.mdp.setup.UserSetup;
import org.unibl.etf.mdp.model.User;
import org.unibl.etf.mdp.model.UserChangePassword;

@Path("/users")
public class UserService {

	private static UserSetup setup=new UserSetup();
	
	/*@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsers() {
		ArrayList<User> list=setup.getAllUsers();
		if(list.size()>0) return Response.status(200).entity(list).build();
		else return Response.status(404).entity("NULL").build();
	}
	
	@GET
	@Path("/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOne(@PathParam("username") String username) {
		return Response.status(200).entity(setup.getByUsername(username)).build();
	}*/
	
	@PUT
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response changePassword(UserChangePassword user) {
		if(setup.changeUserPassword(user)) {
			return Response.status(200).entity(user).build();
		}else
			return Response.status(404).build();
	}
	
	@PUT
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(User user) {
		if(setup.checkIfActive(user)) {
			return Response.status(200).entity(user).build();
		}
		else
			return Response.status(404).build();
	}
	
	@GET
	@Path("/getActivityLog/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActivityLog(@PathParam("username") String username) {
		Map<String,String> activity=setup.getActivityLog(username);
		//if(activity.size()>0)
			return Response.status(200).entity(activity).build();
		//else
			//return Response.status(404).build();
	}
	
	@POST
	@Path("/logout/{username}/timelogin/{timeLogin}/timelogout/{timeLogout}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(@PathParam("username") String username,@PathParam("timeLogin") String timelogin,@PathParam("timeLogout") String timelogout) {
		if(setup.addNewLogForUser(username, timelogin, timelogout)) {
			return Response.status(200).build();
		}
		else {
			return Response.status(404).build();
		}
	}
	
}
