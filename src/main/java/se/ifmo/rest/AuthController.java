package se.ifmo.rest;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import se.ifmo.ejb.UserService;
import se.ifmo.model.Users;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @EJB
    private UserService userService;

    @GET
    @Path("/{username}")
    public Response getUserByUsername(@PathParam("username") String username) {
        Users user = userService.findUserByUsername(username);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"User not found\"}")
                    .build();
        }
        return Response.ok(user).build();
    }

    @POST
    @Path("/login")
    public Response loginUser(UserRequest request) {
        Users user = userService.authenticate(request.getUsername(), request.getPasswordHash());
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                           .entity("{\"message\":\"Invalid credentials\"}")
                           .build();
        }
        return Response.ok("{\"message\":\"Login successful\"}").build();
    }

    @POST
    @Path("/register")
    public Response registerUser(UserRequest request) {
        boolean success = userService.registerUser(request.getUsername(), request.getPasswordHash());
        if (!success) {
            return Response.status(Response.Status.CONFLICT)
                           .entity("{\"message\":\"Username already taken\"}")
                           .build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    public static class UserRequest {
        private String username;
        private String passwordHash;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPasswordHash() {
            return passwordHash;
        }

        public void setPasswordHash(String passwordHash) {
            this.passwordHash = passwordHash;
        }
    }
}
