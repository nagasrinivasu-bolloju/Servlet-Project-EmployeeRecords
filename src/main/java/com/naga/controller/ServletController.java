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
import java.util.ArrayList;
import java.util.List;

import com.naga.Dao.DaoClass;
import com.naga.service.Emp;

@WebServlet("/")
public class ServletController extends HttpServlet {
	 
	DaoClass dbClass;
    public ServletController() {
        super();
        // TODO Auto-generated constructor stub
    }
 
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
		 case "/read":
			 readEmployee(request,response);
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
		 case "/temp":
			 tempFun(request,response);
			 break;
		 case "/login":
			 loginAdmin(request,response);
			 break;
		 case "logout":
			 logoutAdmin(request,response);
			 break;
		 default:
			 reDirect(request,response);
			 
		 } 
		 System.out.println("getContextPath:"+request.getContextPath());
		 System.out.println("getServletPath:"+request.getServletPath());
//		response.getWriter().append("Served at: ").append(request.getContextPath()).append(request.getServletPath());
	}
	
 
	private void logoutAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		 
		HttpSession session=request.getSession();
		session.removeAttribute("admin");
		session.invalidate();
		response.sendRedirect("login.jsp");
	}

	private void reDirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
		}
		else
		{
			response.sendRedirect("login.jsp");
		}
	}

	private void tempFun(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		int val=Integer.parseInt(request.getParameter("action"));
		System.out.println("string printing!!!!"+val);
		request.setAttribute("action",val);
		RequestDispatcher rd=request.getRequestDispatcher("empid-form.jsp");
		rd.forward(request, response);
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
		com.naga.service.Emp emp=new com.naga.service.Emp(empid,firstName,lastName,salary,addrid,address);
		dbClass.update(emp);
		response.sendRedirect("home.jsp");
		
	}

	private void readEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		int empid=Integer.parseInt(request.getParameter("id"));		
		List<Emp> employees=new ArrayList<>();
		employees.add(dbClass.readData(empid));
		request.setAttribute("employees", employees);
		int length=1;
		if(employees.get(0)==null)
			length=0;
		request.setAttribute("length",length);
		RequestDispatcher rd=request.getRequestDispatcher("employee-list.jsp");
		rd.forward(request, response);
	}

	 
	private void insertIntoTable(HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		int empid=Integer.parseInt(request.getParameter("empid"));
		String firstName=request.getParameter("firstName");
		String lastName=request.getParameter("lastName");
		String address=request.getParameter("address");
		float salary=Float.parseFloat(request.getParameter("salary"));
		int addrid=Integer.parseInt(request.getParameter("addrid"));
		com.naga.service.Emp emp=new com.naga.service.Emp(empid,firstName,lastName,salary,addrid,address);
		dbClass.insert(emp);
		response.sendRedirect("list");
	}

	

}