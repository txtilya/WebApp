package webapi;

import dao.UserDao;
import listeners.Initer;
import model.User;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Collection;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("users")
@Produces(APPLICATION_JSON)
public class UserResource implements JsonRestfulWebResource {

    private UserDao userDao;

    @Context
    public void init(ServletContext context) {
        userDao = (UserDao) context.getAttribute(Initer.USER_DAO);
    }

    @GET
    public Response getAll() {
        final Collection<User> users = userDao.getAll();
        return users.size() > 0 ? ok(users): noContent();
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") int id) {
        return userDao.getById(id)
                .map(this::ok)
                .orElse(noContent());
    }
}