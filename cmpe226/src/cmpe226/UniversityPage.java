package cmpe226;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class UniversityPage 
{
	
	int id;
	String address;
	String name;
	public void start(Connection con, int id) throws Exception 
	{
		Scanner sc = new Scanner(System.in);
		
		buildUniversity(con, id);
		while(true)
		{
		System.out.println("\nEnter a choice 1) To hire a new professor\n 2) Add a new course\n 3) List Courses\n 4) Sign Out");
		int choice = sc.nextInt();
		
		switch(choice)
		{
		case 2: 
			addCourse(con);
			break;
		case 1: 
			addProfessor(con);
			break;
		case 3:
			listCourses(con);
			break;
		case 4:
			System.out.println("You have been signed out!");
			TestConnection tc = new TestConnection();
			tc.loginRegister(con);
			//return;
				
		default: System.out.println("You have entered the wrong option!");
		}  
		}
		
	}
	
	public void buildUniversity(Connection con, int id) throws SQLException
	{
		
		Scanner sc = new Scanner(System.in);
		
		String query = "Select name from university where id = ?";
		PreparedStatement p = con.prepareStatement(query);
		p.setInt(1, id);
		
		ResultSet rs = p.executeQuery();
		
		if(!rs.isBeforeFirst())
		{
			System.out.println("Invalid id");
			return;
		}
		else
		{
			while(rs.next())
			{
				this.id=id;
				String name = rs.getString(1);
				//String address= rs.getString(2);
				
				this.name=name;
				CommonFunctions cmn = new CommonFunctions();
				//String addressSplit[] = cmn.splitAddress(address);
				//int zip = Integer.parseInt(addressSplit[3]);
				//this.address = new String(addressSplit[0], addressSplit[1], addressSplit[2],zip);
				//this.address = address;
				
			}
		}
		System.out.println("Welcome "+this.name);
	}

	public void listCourses(Connection con) throws SQLException
	{
		String query="Select c.id, c.name from course c where c.offeredby=?  order by c.id";
		PreparedStatement p = con.prepareStatement(query);
		p.setInt(1, this.id);
		
		ResultSet rs = p.executeQuery();
		
		if(!rs.isBeforeFirst())
		{
			System.out.println("No courses offered by "+this.name+" at this time");
			return;
		}
		else
		{
			System.out.println(this.name+" offers the following courses at this time ");
			System.out.println("id\t"+"name\t"+"\tprofessor");
			while(rs.next())
			{
				int cid = rs.getInt(1);
				System.out.print(cid+"\t");
				System.out.print(rs.getString(2)+"\t\t");
				
				PreparedStatement getProfessors = con.prepareStatement("Select taughtby from professorcourse where teaches=?");
				getProfessors.setInt(1, cid);
				ResultSet rpc = getProfessors.executeQuery();
				
				while(rpc.next())
				{
					
				int pid = rpc.getInt(1);
			    PreparedStatement p2 = con.prepareStatement("Select name from person where id=?" );
			    p2.setInt(1, pid);
			    ResultSet r2 = p2.executeQuery();
			    while(r2.next())
			    {
			    	System.out.print(r2.getString(1)+"\t");
			    	
			    }
				}
				System.out.println();
			}
			
		}
		
	}
	
	
	public void addProfessor(Connection conn) throws Exception
	{
		Scanner sc = new Scanner(System.in);
		
		System.out.println("You are about to hire a new Professor for "+this.name);
		System.out.println("Here is the list of all existing professors in the database ");
		
		ArrayList<Integer> allProfessors = new ArrayList<>();
		
		String query= "Select p.id , p1.name, p.designation from professor p , person p1 where p.id = p1.id";
		
		PreparedStatement getProfName = conn.prepareStatement(query);
		
		ResultSet rs = getProfName.executeQuery();
		
		while(rs.next())
		{
			int pid = rs.getInt(1);
			
			allProfessors.add(pid);
			
			System.out.println(pid+") "+rs.getString(2)+"\t"+rs.getString(3));
//			System.out.println(rs.getString(2));
//			System.out.println(rs.getString(3));
		}
		
		
		while(true)
		{
			System.out.println("Press 1 to hire existing professor Press 2 to add a new Professor press 3 to exit");
			int choice;
			choice = sc.nextInt();
			
			if(choice==1)
			{
				addExistingProfessor(conn, sc, allProfessors);
			}
			else if(choice==2)
			{
				addNewProfessor(conn);
			}
			else if(choice==3)
			{
				return;
			}
			else
			{
				System.out.println("Invalid Choice");
			}
		}
		
		
	}
	
	private void addNewProfessor(Connection conn) throws Exception
	{
		System.out.println("You are about to add a new Professor");
		
		RegisterPage rp = new RegisterPage();
		
		registerProfessor(conn, this.id);
		
	}
	
	private void addExistingProfessor(Connection conn , Scanner sc, ArrayList<Integer> allProf) throws SQLException
	{
		String query= "Select p.id , p1.name, p.designation from professor p , person p1 where p.id = p1.id and p.worksfor=?";
		
		Scanner s = new Scanner(System.in);
		
		ArrayList<Integer> currentProfessor= new ArrayList<>();
		
		PreparedStatement getProfName = conn.prepareStatement(query);
		getProfName.setInt(1, this.id);
		ResultSet rs = getProfName.executeQuery();
		
		
		while(rs.next())
		{
			int pid = rs.getInt(1);
			
			currentProfessor.add(pid);	
		}
		
		System.out.println("Enter Professor id you want to hire");
		int pid = sc.nextInt();
		
		if(currentProfessor.contains(pid))
		{
			System.out.println("This professor already works for "+this.name);
			return;
		}
		
		if(!allProf.contains(pid))
		{
			System.out.println("No such Professor in the database");
			return;
		}
		else
		{
			//System.out.println(this.id);
			Scanner tre = new Scanner(System.in);
			System.out.println("Assign a designation for a professor, Enter one of ");
			System.out.println("Dean,Professor,Associate_Professor,Lecturer,Visiting Scholar,Director,Associate Director,TA,RA,GA,Part time faculty,Contractor");
			
			//String designtion = "Professor";
			 String designtion = tre.next();
			
			//System.out.println("Designation is "+ design);
			
			PreparedStatement p = conn.prepareStatement("INSERT INTO public.professor( id, worksfor, designation) VALUES (?, ?, CAST(? AS designation));");
			
			p.setInt(1, pid);
			p.setInt(2, this.id);
			p.setString(3, designtion);
			
			p.executeUpdate();
			
			System.out.println("Professor inserted successfully");
		}
		
	}
	
	/*public void addProfessor(Connection conn, int id) throws SQLException
	{
		
		
		Scanner sc = new Scanner(System.in);
		
		PreparedStatement getProfId = conn.prepareStatement("select max(id) from professor");
		ResultSet profid = getProfId.executeQuery();
		
		int pid=0;
		while(profid.next())
		{
			pid=profid.getInt(1) + 1;
		}
		
		System.out.println("Enter Designation ");
		String designantion = sc.nextLine();
		
		PreparedStatement p = conn.prepareStatement("INSERT INTO public.professor( id, worksfor, designation) VALUES (11, ?, CAST(? AS designation));");
		
	//	p.setInt(1, pid);
		p.setInt(1, id);
		p.setString(2, designantion.toString());
		p.executeUpdate();
	}
	*/
	public void addCourse(Connection conn) throws SQLException
	{
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter name for course");
		
		
		
		String name = sc.nextLine();
		System.out.println("Enter description for course");
		
		String description = sc.nextLine();

		System.out.println("\nSelect a professor to teach this course ");
		System.out.println("\nHere are a list of professors who are available ");
				
		String query="Select id from professor where worksfor=? order by id";
		PreparedStatement p = conn.prepareStatement(query);
		p.setInt(1, this.id);
		
		ArrayList<Integer> professors = new ArrayList<>();
		
		ResultSet rs = p.executeQuery();
		if(!rs.isBeforeFirst())
		{
			System.out.println("No Professors available to take up new course");
			return;
		}
		else
		{
			while(rs.next())
			{
				professors.add(rs.getInt(1));
		
			}
		}
		
		for(int i=0;i<professors.size(); i++)
		{
			PreparedStatement p2 = conn.prepareStatement("Select distinct name, designation from person p, professor p1 where p.id=? and p.id = p1.id and p1.worksfor=?");
			p2.setInt(1, professors.get(i));
			p2.setInt(2, this.id);
			ResultSet r2 = p2.executeQuery();
			
			while(r2.next())
			{
				System.out.println("\n"+professors.get(i)+"\t"+r2.getString(1)+"\t"+r2.getString(2));
				//System.out.println();
			}
		}	
			int cid = insertCourse(conn, this.id, name, description, professors);
			if(cid==0)
			{
				return;
			}
				
				while(true)
				{
					int z=0;
					System.out.println("Press 1 to assign 1 more professor for this course , Any other key to exit");
					 z=sc.nextInt();
					if(z==1)
					{	
						System.out.println("Enter Professor id ");
					 int pid= sc.nextInt();
					 if(validateProfessor(professors, pid))
					 	{	
						 assignProfessor(conn, professors, cid,pid);
					 	}
					 else
					 {
						 System.out.println("Invalid Professor id");
					 }
					}
					else
					{
						return;
					}
				}
			
			
		
		
	}
	
	private static int insertCourse(Connection con, int id, String name, String description, ArrayList<Integer> professors) throws SQLException
	{
		String query = "Select max(id) from course";
		PreparedStatement getId = con.prepareStatement(query);
		
		ResultSet rs = getId.executeQuery();
		int cid=0;
		if(!rs.isBeforeFirst())
		{
			cid=0;
		}
		while(rs.next())
		{
			 cid=rs.getInt(1) + 1;
		}
		
		//System.out.println("Max cid is "+cid);
		System.out.println("\n Enter Professor id to assign a professor for this course");
		Scanner sc = new Scanner(System.in);
		int pid = sc.nextInt();
		Scanner sc2 = new Scanner(System.in);
		System.out.println("Enter course id ");
		String c_id = sc2.nextLine();
		
		if(validateProfessor(professors, pid))
		{	
		
		query="INSERT INTO course( id, name, description, offeredby, cid) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement insert = con.prepareCall(query);
		insert.setInt(1, cid);
		insert.setString(2, name);
		insert.setString(3, description);
		insert.setInt(4, id);
		insert.setString(5, c_id);
		
		insert.executeUpdate();
		System.out.println("Course Successfully added");
		assignProfessor(con, professors, cid, pid);
		
		return cid;
		}
		else
		{
			System.out.println("Invalid Professor id");
			return 0;
		}
	}
	
	private static void assignProfessor(Connection con, ArrayList<Integer> professors, int cid, int pid) throws SQLException
	{
		
		{
			String query="INSERT INTO public.professorcourse( teaches, taughtby) VALUES (?, ?);";
			PreparedStatement insertprof = con.prepareStatement(query);
			insertprof.setInt(1, cid);
			insertprof.setInt(2, pid);
			
			insertprof.executeUpdate();
			
			System.out.println("Professor Successfully added");
		}
	}
	
	private static boolean validateProfessor(ArrayList<Integer> professor , int pid)
	{
		return (professor.contains(pid));
	}
	
	
	public int registerProfessor(Connection con, int uniID) throws Exception 
	{
		
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		int id=0;
		PreparedStatement getID = con.prepareStatement("Select max(id) from login");
		PreparedStatement insLogin = con.prepareStatement("insert into Login values(?,?,?,'Professor')");
		PreparedStatement insPerson = con.prepareStatement("insert into Person values(?,?,?,(?,?,?,?))");
		PreparedStatement selectDesg = con.prepareStatement("SELECT enum_range(NULL::designation)");
		PreparedStatement insProf = con.prepareStatement("INSERT INTO public.professor( id, worksfor, designation) VALUES (?, ?, CAST(? AS designation));");

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
			
			System.out.println("Enter name: ");
			String name = sc.nextLine();
			System.out.println("Enter email: ");
			String email = sc.nextLine();
			System.out.println("Enter Street: ");
			String street = sc.nextLine();
			System.out.println("Enter City: ");
			String city = sc.nextLine();
			System.out.println("Enter State: ");
			String state = sc.nextLine();
			System.out.println("Enter Zipcode: ");
			int zp = sc.nextInt();
			System.out.println();
			
			insPerson.setInt(1, id);
			insPerson.setString(2, name);
			insPerson.setString(3, email);
			insPerson.setString(4, street);
			insPerson.setString(5, city);
			insPerson.setString(6, state);
			insPerson.setInt(7, zp);
			int rs_insPerson = insPerson.executeUpdate();
			if(rs_insPerson<=0)
			{
				System.out.println("Person insert failed!");
			}
			else
			{
				System.out.println("Enter more details..");
			}
			
			//System.out.println("Enter University id that you work for: ");
			//int univ_id = sc.nextInt();
			
			ResultSet rs_selectDesg = selectDesg.executeQuery();
			if(!rs_selectDesg.isBeforeFirst())
			{
				System.out.println("ERROR!!! There are no pre-fed designations is the system. Please contact the system admin!");
			}
			while(rs_selectDesg.next())
			{
				System.out.println(rs_selectDesg.getString(1));
			}
			System.out.println("Enter designation: ");
			
//			@SuppressWarnings("resource")
//			Scanner sc1 = new Scanner(System.in);
			
			Scanner sccc = new Scanner(System.in);
			
			String desg = sccc.nextLine();
			System.out.println();
			
			insProf.setInt(1, id);
			insProf.setInt(2, uniID);
//			insProf.setString(3, "Dean");
			insProf.setString(3, desg);
			
			System.out.println("Desg ids "+desg);
			int rs_insProf = insProf.executeUpdate();
			if(rs_insProf<=0)
			{
				System.out.println("Professor insert failed!");
			}
			else
			{
				System.out.println("You have registered successfully!");
			}
			
			//ProfessorPage pPage = new ProfessorPage();
        	//pPage.start(con, id);
		}
		finally
		{
			getID.close();
			insLogin.close();
			insPerson.close();
			selectDesg.close();
			insProf.close();
		}
		return id;		
	}

	
}