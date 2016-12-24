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
@WebServlet("/registration")
public class Registration extends HttpServlet {

    private UserDao userDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext servletContext = config.getServletContext();
        userDao = (UserDao) servletContext.getAttribute(USER_DAO);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String login = req.getParameter("login");
        String email = req.getParameter("email");
        String pass = req.getParameter("pass");

        if (!userDao.isUserExist(login, email)) {
            userDao.addUser(login, email, pass);
            resp.sendRedirect("regpass.html");
        } else resp.sendRedirect("regfail.html");


    }
}
