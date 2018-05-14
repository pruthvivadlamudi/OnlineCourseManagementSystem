package cmpe226;

import java.sql.*;
import java.util.Scanner;

public class LoginPage 
{
	Scanner sc = new Scanner(System.in);
	void login(Connection con) throws Exception
	{
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		String user="";
		String pass="";
		String type = "";
		int id=0;
		
		System.out.println("Do you want to Continue?(Y/N)");
		String ch = sc.nextLine();
		if(ch.equalsIgnoreCase("N"))
		{
			TestConnection tc = new TestConnection();
			tc.loginRegister(con);		
		}
		
			System.out.println("Enter Username: ");
			user=sc.next();
			System.out.println("Enter Password: ");
			pass=sc.next();
			System.out.println();
			PreparedStatement p = con.prepareStatement("Select id, type from login where username=? AND password=?");
			try
			{
				p.setString(1, user);
				p.setString(2, pass);
				ResultSet rs = p.executeQuery();
				if(!rs.isBeforeFirst())
				{
					System.out.println("Invalid Login");
					TestConnection tc = new TestConnection();
					tc.loginRegister(con);
				}
				while(rs.next())
				{
					id = rs.getInt(1);
					type=rs.getString(2);
				}
			}
			finally
			{
				p.close();
			}
			if(type.equalsIgnoreCase("Student"))
			{				
				StudentPage sPage = new StudentPage();
				sPage.start(con, id);
			}
			else if(type.equalsIgnoreCase("Professor"))
			{
				ProfessorPage pPage = new ProfessorPage();
	        	pPage.start(con, id);
			}
			else if(type.equalsIgnoreCase("University"))
			{
				UniversityPage uPage = new UniversityPage();
	        	uPage.start(con, id);
			}
			else
			{
				AdminPage ap =new AdminPage ();
				ap.start(con);
			}
	}
}

