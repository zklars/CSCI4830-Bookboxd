package com.csci4830.bookboxd;

import java.io.IOException;

import javax.persistence.NoResultException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;

import com.csci4830.datamodel.User;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//logic
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String passwordconf = request.getParameter("passwordconf");
		if (!password.equals(passwordconf)) {

			// Passwords don't match, set message
			String destination = "register.jsp";
			String message = "Your passwords don't match.";
			request.setAttribute("errorMessage", message);
			
			// Forward request
			RequestDispatcher dispatcher = request.getRequestDispatcher(destination);
            dispatcher.forward(request, response);
            //I don't remember if I need to return and I am not taking chances
            return;
		}

		if (password.contains("'")||username.contains("'")) {

			// Sql injection or dumbness
			String destination = "register.jsp";
			String message = "Bad username or password.";
			request.setAttribute("errorMessage", message);
			
			// Forward request
			RequestDispatcher dispatcher = request.getRequestDispatcher(destination);
            dispatcher.forward(request, response);
            //I don't remember if I need to return and I am not taking chances
            return;
		}
		
		try {
			//insert new user
			User user = Utility.createUser(username, password);
			
			
			// Log the user in
			HttpSession session = request.getSession();
            session.setAttribute("user", user);
            request.removeAttribute("errorMessage");
            
            String destination = "ViewProfile?user_id=" + user.getUser_id();
            
            // Forward request
			RequestDispatcher dispatcher = request.getRequestDispatcher(destination);
            dispatcher.forward(request, response);			
		} catch (ConstraintViolationException e) {
			// Failed to login, set message
			String destination = "register.jsp";
			String message = "That username is already taken.";
			request.setAttribute("errorMessage", message);
			
			// Forward request
			RequestDispatcher dispatcher = request.getRequestDispatcher(destination);
            dispatcher.forward(request, response);
		}
	}

}
