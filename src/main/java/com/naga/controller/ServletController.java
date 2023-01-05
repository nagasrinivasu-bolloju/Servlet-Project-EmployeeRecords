package com.naga.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.naga.dao.DaoClass;
import com.naga.model.Emp;

@WebServlet("/")
public class ServletController extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	private static Logger log=Logger.getLogger(ServletController.class);
	DaoClass dbClass;
	public void init()
	{
		dbClass=new DaoClass();
		boolean temp=false;
		try
		{
			temp=dbClass.connect();
		}
		catch (SQLException e)
		{
			e.getMessage();
		}
		if(temp==false)
			log.info("Not DB connection failed.");
		else
			log.info("DB connection successfull");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		log.info("doGet method involked.");
		 String action=request.getServletPath();
		 switch(action)
		 {
		 case "/new":
			 redirectToForm(request,response);
			 break;
		 case "/insert":
			 insertIntoTable(request,response);
			 break;
		 case "/update":
			 updateTable(request,response);
			 break;
		 case "/delete":
			 deleteEmployee(request,response);
			 break;
		 case "/edit":
			 redirectToFormWithEmp(request,response);
			 break;
		 case "/temp":
			 reDirectToEmpidForm(request,response);
			 break;
		 case "/read-employees-with-similar-names":
			 readEmployeeNames(request,response);
			 break;
		 case "/list":
			 try
			 {
				list(request,response);
			 }
			 catch (ServletException | IOException | SQLException e)
			 {
				e.printStackTrace();
			 }
			 break;
		 case "/login":
			 loginAdmin(request,response);
			 break;
		 case "/logout":
			 logoutAdmin(request,response);
			 break;
		 default:
			 reDirectToHome(request,response);
			 
		 } 
		 log.info("getContextPath:"+request.getContextPath());
		 log.info("getServletPath:"+request.getServletPath());
//		response.getWriter().append("Served at: ").append(request.getContextPath()).append(request.getServletPath());
	}
	
	
	private void reDirectToEmpidForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		int action=Integer.parseInt(request.getParameter("action"));
		request.setAttribute("action",action);
		RequestDispatcher rd=request.getRequestDispatcher("empid-form.jsp");
		log.info("Redirecting to empid-form.jsp");
		rd.forward(request, response);
	}

	private void logoutAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		 
		HttpSession session=request.getSession();
		session.removeAttribute("admin");
		session.invalidate();
		log.info("loggout out from appication.");
		log.info("redirecting to login.jsp");
		response.sendRedirect("login.jsp");
//		System.out.println("In log out!!!");
//		Cookie cookie[] = request.getCookies();
//		for (int i = 0; i <cookie.length; i++)
//	        cookie[i] = null;
//		
//		for(Cookie cook:cookie)
//			System.out.println("cook value:"+cook);
//		response.sendRedirect("login.jsp");
	}

	private void reDirectToHome(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub\
		log.info("Redirecting to login page.");
		response.sendRedirect("login.jsp");
	}

	private void loginAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		log.info("In login servlet");
		log.info("checking login credentials");
		String name=request.getParameter("admin");
		String pass=request.getParameter("pass");
		if(name.equals("naga") && pass.equals("123"))
		{
			log.info("credentials matched.\n Redirecting to home.jsp");
			HttpSession session=request.getSession();
			session.setAttribute("admin","activeState");
			response.sendRedirect("home.jsp");
//			Cookie cookie=new Cookie("admin","activeState");
//			response.addCookie(cookie);
//			response.sendRedirect("home.jsp");

		}
		else
		{
			log.error("credentials mis-matched!!.\n Redirecting to login page.");
			response.sendRedirect("login.jsp");
		}
	}

	private void redirectToFormWithEmp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		log.info("Retrieving empid");
		int empid=Integer.parseInt(request.getParameter("id"));
		System.out.println("empid at edit:"+empid);
		Emp emp=dbClass.readData(empid);
		request.setAttribute("emp", emp);
		RequestDispatcher rd=request.getRequestDispatcher("employee-form.jsp");
		log.info("Redirecting to emploee-form.jsp");
		rd.forward(request, response);
	}

	private void redirectToForm(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException 
	{
//response.sendRedirect("employee-form.jsp");
		log.info("Redirecting to employee-form.jsp");
		RequestDispatcher rd=request.getRequestDispatcher("employee-form.jsp");
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		log.info("Calling doGet method.");
		doGet(request, response);
	}
	private void list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException 
	{
		log.info("retreiving all employees.");
		List<Emp> employees=dbClass.readData();		
		request.setAttribute("employees", employees);
		RequestDispatcher rd=request.getRequestDispatcher("employee-list.jsp");
		log.info("Redirecting to employee-list.jsp");
		rd.forward(request, response);
	}

	private void deleteEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		int empid=Integer.parseInt(request.getParameter("id"));
		if(dbClass.delete(empid)==0)
		{
			log.error("deletion failed.\n redirecting to error page.");
			request.setAttribute("msg","Deletion failed!!!");
			RequestDispatcher rd=request.getRequestDispatcher("error.jsp");
			rd.forward(request, response);
		}
		log.info("deletion successfull\n redirecting to home page.");
		response.sendRedirect("home.jsp");
	}

	private void updateTable(HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		int empid=Integer.parseInt(request.getParameter("empid"));
		String firstName=request.getParameter("firstName");
		String lastName=request.getParameter("lastName");
		String address=request.getParameter("address");
		float salary=Float.parseFloat(request.getParameter("salary"));
		int addrid=Integer.parseInt(request.getParameter("addrid"));
		Emp emp=new  Emp(empid,firstName,lastName,salary,addrid,address);
		dbClass.update(emp);
		log.info("redirecting to home.jsp");
		response.sendRedirect("home.jsp");
		
	}

	private void readEmployeeNames(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String name=request.getParameter("name");	
		int action=Integer.parseInt(request.getParameter("actionState"));
		List<Emp> employees=dbClass.readData(name);
		System.out.println("printing selected employees through names:");
		request.setAttribute("employees", employees);
		request.setAttribute("length",employees.size());
		request.setAttribute("action",action);			//check action name check
		log.info("redirecting to emp-names.jsp");
		RequestDispatcher rd=request.getRequestDispatcher("emp-names.jsp");
		rd.forward(request, response);
	}

	 
	private void insertIntoTable(HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		String firstName=request.getParameter("firstName");
		String lastName=request.getParameter("lastName");
		String address=request.getParameter("address");
		float salary=Float.parseFloat(request.getParameter("salary"));
		int addrid=Integer.parseInt(request.getParameter("addrid"));
		Emp emp=new Emp(0,firstName,lastName,salary,addrid,address);
		dbClass.insert(emp);
		log.info("redirecting to list servlet");
		response.sendRedirect("list");
	}

	

}