package com.naga.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.naga.model.Emp;

public class DaoClass
{
	private static Logger log=Logger.getLogger(DaoClass.class);
	private final String url="jdbc:postgresql://localhost:5432/DemoDB";
	private final String username="postgres";
	private final String password="srinu534";
	private Connection connection =null;
	private Statement statement=null;
	private PreparedStatement prepStatementEmp=null;
	private PreparedStatement prepStatementAddr=null;
	private PreparedStatement prepStatementUpdate=null;
	
	public boolean connect() throws SQLException
	{
		return connectDataBase() && createStatement();
	}
	
	private boolean connectDataBase()
	{
		try
		{
			Class.forName("org.postgresql.Driver");
			connection=DriverManager.getConnection(url,username,password);
			if(connection!=null)
				return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	private boolean createStatement()
	{
		try
		{
			statement = connection.createStatement();
			String sql1="insert into employee(firstName,lastName,addrid,salary) values(?,?,?,?);";//change
			String sql2="insert into address values(?,?);";
			String sql3="update employee set firstName=?,lastName=?,addrid=?,salary=? where empid=?";
			prepStatementEmp=connection.prepareStatement(sql1);
			prepStatementAddr=connection.prepareStatement(sql2);
			prepStatementUpdate=connection.prepareStatement(sql3);
			if(statement!=null && prepStatementEmp!=null && prepStatementAddr!=null && prepStatementUpdate!=null)
			{
				log.info("Statements created successfully.");
				return true;
			}
			else
			{
				log.info("Statements  creation failed.");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public int insert(Emp emp)
	{
		int rows=0;
		try
		{
		if(!checkAddress(emp.getAddrid()) && !addToAddress(emp.getAddrid(),emp.getAddress()))	 //if addrid not present in addresstable or addtoaddress() fails then return false.
		{
			log.error("address insertion failed.");
			 return 0;
		}
//		prepStatementEmp.setInt(1,emp.getEmpid());         change
		prepStatementEmp.setString(1,emp.getFirstName());
		prepStatementEmp.setString(2,emp.getLastName());
		prepStatementEmp.setInt(3,emp.getAddrid());
		prepStatementEmp.setFloat(4,emp.getSalary());
		rows=prepStatementEmp.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(rows<=0)
			log.error("insertion failed at employee table.");
		else
			log.info("insertion successfull at employee table.");
		return rows;
	}
	private boolean addToAddress(int addrId,String address)
    {
    	int rows=0;
    	try
    	{
    	prepStatementAddr.setInt(1,addrId);
		prepStatementAddr.setString(2,address);
		rows=prepStatementAddr.executeUpdate();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	if(rows>0)
    	{
    		log.info("address added successfully.");
    		return true;
    	}
    	log.error("address insertion failed.");
		return false;
    }
	private boolean checkAddress(int addrId)
	{
		try
		{
		ResultSet rs=statement.executeQuery("select addrid from address where addrid="+addrId);
		if(!rs.next())
		{
			log.info("addrId not found in address table.");
			return false;  
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		log.info("address already exists in address table");
		return true;
	}
	public int delete(int empId)
	{
		int rows=0;
		try
    	{
    		rows=statement.executeUpdate("delete from employee where empid="+empId);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		if(rows>0)
    		log.info("deletion successfull.");
		else
			log.error("deletion failed.");
		return rows;
	}
	public int update(Emp emp)
	{
		if(!checkAddress(emp.getAddrid()))
			addToAddress(emp.getAddrid(),emp.getAddress());
		int rows=0;
		try
		{
			
			prepStatementUpdate.setString(1,emp.getFirstName());
			prepStatementUpdate.setString(2,emp.getLastName());
			prepStatementUpdate.setInt(3,emp.getAddrid());
			prepStatementUpdate.setFloat(4,emp.getSalary());
			prepStatementUpdate.setInt(5,emp.getEmpid());
			rows=prepStatementUpdate.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return rows;
	}
	public List<Emp> readData()
	{
		log.info("reading all employees.");
		List<Emp> employees=new ArrayList<Emp>();
		try
    	{
    	ResultSet rs=statement.executeQuery("select * from employee inner join address on employee.addrId=address.addrId;");
    	
    	while(rs.next())
    	{
    		Emp emp=new Emp();
    		emp.setEmpid(rs.getInt(1));
    		emp.setFirstName(rs.getString(2));
    		emp.setLastName(rs.getString(3)); 
    		emp.setAddrid((int)rs.getInt(4));
    		emp.setSalary(Float.parseFloat(rs.getString(5)));
    		emp.setAddress(rs.getString(7));
    		employees.add(emp);
    	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		return employees;
	}
	public List<Emp> readData(String name)
	{
		List<Emp> employees=new ArrayList<>();
		log.info("reading all employees with name: "+name);
		try
    	{
    	ResultSet rs=statement.executeQuery("select * from employee inner join address on employee.addrId=address.addrId where firstName='"+name+"' collate case_insensitive;");
    	while(rs.next())
    	{
    		 Emp emp=new Emp();
    		 emp.setEmpid(rs.getInt(1));
    		 emp.setFirstName(rs.getString(2));
    		 emp.setLastName(rs.getString(3));
    		 emp.setAddrid(rs.getInt(4));
    		 emp.setSalary(rs.getFloat(5));
    		 emp.setAddress(rs.getString(7));
    		 employees.add(emp);
    	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		return employees;
	}

	public Emp readData(int empid) {
		Emp emp=null;
		log.info("reading employee record with empId: "+empid);
		try
    	{
    	ResultSet rs=statement.executeQuery("select * from employee inner join address on employee.addrId=address.addrId where empid="+empid+";");
    	if(rs.next())
    	{
    		 emp=new Emp();
    		 emp.setEmpid(rs.getInt(1));
    		 emp.setFirstName(rs.getString(2));
    		 emp.setLastName(rs.getString(3));
    		 emp.setAddrid(rs.getInt(4));
    		 emp.setSalary(rs.getFloat(5));
    		 emp.setAddress(rs.getString(7));
    		 
    	}
    	else
    	{
    		log.error("Inside readData!!!!.............emp is nullll");
    	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		return emp;
	}
	 
	
	
}
