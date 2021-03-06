package servlets;

import dao.UserDao;
import lombok.extern.java.Log;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static listeners.Initer.USER_DAO;

@Log
@WebServlet("/add")
public class AddFriend extends HttpServlet {

    private UserDao userDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext servletContext = config.getServletContext();
        userDao = (UserDao) servletContext.getAttribute(USER_DAO);
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        int targetId = Integer.parseInt(req.getParameter("id"));
        int requesterId = userDao.getByEmail(req.getUserPrincipal().getName()).get().getId();
        if (!userDao.isUsersFriendsRequestExist(requesterId,targetId)){
            userDao.addFriend(requesterId,targetId);
            resp.sendRedirect("friendadd.html");
        } else resp.sendRedirect("friendfail.html");


    }
}
