package cmpe226;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class AdminPage 
{

	public void start(Connection con) throws Exception
	{
		while(true)
		{
			System.out.println("Press 1) to add new University \n2) Logout");
			
			Scanner sc = new Scanner(System.in);

			int choice = sc.nextInt();
			
			switch(choice)
			{
			case 1:
				registerUniversity(con);
				break;
				
			case 2:
				TestConnection tc = new TestConnection();
				tc.loginRegister(con);
			}
			
		}
	}
	
	private int registerUniversity(Connection con) throws Exception 
	{
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		int id=0;
		PreparedStatement getID = con.prepareStatement("Select max(id) from login");
		PreparedStatement insLogin = con.prepareStatement("insert into Login values(?,?,?,'University')");
		PreparedStatement insUniv = con.prepareStatement("insert into University values(?,?,(?,?,?,?))");
		try
		{
			ResultSet rs_getID = getID.executeQuery();
			if(!rs_getID.isBeforeFirst())
			{
				id=0;
			}
			while(rs_getID.next())
			{
				 id=rs_getID.getInt(1) + 1;
			}
			
			System.out.println("Enter Username: ");
			String uname = sc.nextLine();
			System.out.println("Enter password: ");
			String pswd = sc.nextLine();
			System.out.println();
			
			insLogin.setInt(1, id);
			insLogin.setString(2, uname);
			insLogin.setString(3, pswd);
			int rs_insLogin = insLogin.executeUpdate();
			if(rs_insLogin<=0)
			{
				System.out.println("Login registeration failed!");
			}
			else
			{
				System.out.println("You have created your Login information.");
			}
			
			System.out.println("Enter name of Univerity");
			String name = sc.nextLine();
			System.out.println("Enter Street: ");
			String street = sc.nextLine();
			System.out.println("Enter City: ");
			String city = sc.nextLine();
			System.out.println("Enter State: ");
			String state = sc.nextLine();
			System.out.println("Enter Zipcode: ");
			int zp = sc.nextInt();
			System.out.println();
			
			insUniv.setInt(1, id);
			insUniv.setString(2, name);
			insUniv.setString(3, street);
			insUniv.setString(4, city);
			insUniv.setString(5, state);
			insUniv.setInt(6, zp);
			int rs_insUniv = insUniv.executeUpdate();
			if(rs_insUniv<=0)
			{
				System.out.println("University registeration failed!");
			}
			else
			{
				System.out.println("You have registered successfully!");
			}
			
			UniversityPage uPage = new UniversityPage();
        	uPage.start(con, id);
		}
		finally
		{
			getID.close();
			insLogin.close();
			insUniv.close();
//			sc.close();
		}
		return id;		
	}

	
}
