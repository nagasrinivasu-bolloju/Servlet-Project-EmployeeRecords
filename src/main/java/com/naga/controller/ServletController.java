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

import com.naga.dao.DaoClass;
import com.naga.model.Emp;

@WebServlet("/")
public class ServletController extends HttpServlet
{
	private static final long serialVersionUID = 1L;
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
			System.out.println("-------------Not connetion in init()!!!");
		else
			System.out.println("---------Connected successfully in init()------------------");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
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
		 System.out.println("getContextPath:"+request.getContextPath());
		 System.out.println("getServletPath:"+request.getServletPath());
//		response.getWriter().append("Served at: ").append(request.getContextPath()).append(request.getServletPath());
	}
	
 
	private void reDirectToEmpidForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		int action=Integer.parseInt(request.getParameter("action"));
		request.setAttribute("action",action);
		RequestDispatcher rd=request.getRequestDispatcher("empid-form.jsp");
		rd.forward(request, response);
	}

	private void logoutAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		 
		HttpSession session=request.getSession();
		session.removeAttribute("admin");
		session.invalidate();
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
		// TODO Auto-generated method stub
		response.sendRedirect("login.jsp");
	}

	private void loginAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException
	{

		String name=request.getParameter("admin");
		String pass=request.getParameter("pass");
		if(name.equals("naga") && pass.equals("123"))
		{
			HttpSession session=request.getSession();
			session.setAttribute("admin","activeState");
			response.sendRedirect("home.jsp");
//			Cookie cookie=new Cookie("admin","activeState");
//			response.addCookie(cookie);
//			response.sendRedirect("home.jsp");

		}
		else
		{
			response.sendRedirect("login.jsp");
		}
	}

	private void redirectToFormWithEmp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		int empid=Integer.parseInt(request.getParameter("id"));
		Emp emp=dbClass.readData(empid);
		request.setAttribute("emp", emp);
		RequestDispatcher rd=request.getRequestDispatcher("employee-form.jsp");
		rd.forward(request, response);
	}

	private void redirectToForm(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException 
	{
//response.sendRedirect("employee-form.jsp");
		RequestDispatcher rd=request.getRequestDispatcher("employee-form.jsp");
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doGet(request, response);
	}
	private void list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException 
	{
		List<Emp> employees=dbClass.readData();		
		request.setAttribute("employees", employees);
		RequestDispatcher rd=request.getRequestDispatcher("employee-list.jsp");
		rd.forward(request, response);
	}

	private void deleteEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		int empid=Integer.parseInt(request.getParameter("id"));
		if(dbClass.delete(empid)==0)
		{
			request.setAttribute("msg","Deletion failed!!!");
			RequestDispatcher rd=request.getRequestDispatcher("error.jsp");
			rd.forward(request, response);
		}
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
		response.sendRedirect("list");
	}

	

}