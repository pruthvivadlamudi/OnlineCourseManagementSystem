package cmpe226;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.swing.JOptionPane;

public class StudentPage 
{
	Connection con = null;
	int id;
	String studName;
	
	//Student Home Page
	public void start(Connection con, int id) throws Exception
	{
		this.id = id; //Student id
		
		studName = fetchName(con, id);//Fetches student name

		System.out.println("Welcome "+ studName);
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter your choice: \n1) View My Courses"
				+ " \n2) View All Courses"
				+ " \n3) Sign Out \n");
		int choice = sc.nextInt();
				
		switch(choice)
		{
		case 1:
			viewMyCourses(con, id);
			break;
		case 2:
			viewAllCourses(con, id);
			break;
		case 3:
			System.out.println("You have been signed out!");
			TestConnection tc = new TestConnection();
			tc.loginRegister(con);
			break;
		default:
			System.out.println("Sorry! you have entered the wrong choice!");
		}		
		sc.close();		
	}
	
	// Student's 'My course Page'
	public void viewMyCourses(Connection con, int id) throws Exception {
		Scanner sc = new Scanner(System.in);
		System.out.println("Which of your courses do you want to see?"
				+ " : \n1) View All My Courses"
				+ " \n2) View Active Courses"
				+ " \n3) View Completed Courses \n"
				+ "\n\n4) Go Back\n");
		int choice = sc.nextInt();
				
		switch(choice)
		{
		case 1:
			viewAllMyCourses(con, id);
			break;
		case 2:
			viewActiveCourses(con, id);
			break;
		case 3:
			viewCompletedCourses(con,id);
			break;
		case 4:
			start(con, id);
			break;
		default:
			System.out.println("Sorry! you have entered the wrong choice!/n Enter the correct"
					+ "option from below");
			viewMyCourses(con,id);
		}
		
		sc.close();
	}
	
	// Student's all courses page
	public void viewAllMyCourses(Connection con, int id) throws Exception 
	{	
		TreeMap<Integer, Integer> courseIdMapping = new TreeMap<Integer, Integer>();
		int i=1;
		
		System.out.println("Student " + studName +" courses");

		Scanner sc = new Scanner(System.in);
		
		// Select all the courses of user student
		String myCourse = "Select c.name as courseName, c.id as pcourseId, u.name as univName, sc.status as scStatus"
				+ " from course c, university u, student s, studentcourse sc "
				+ "where s.id = ? and sc.takenby = s.id and sc.takes = c.id and c.offeredby = u.id;";
		PreparedStatement ps = null;
		
		ps = con.prepareStatement(myCourse);
		ps.setInt(1,id);
		
		ResultSet rs = ps.executeQuery();		
				
		if(!rs.isBeforeFirst())
		{
			System.out.println("No courses ever enrolled.");
		}
		else
		{
			System.out.println("My courses:");
			
			while(rs.next())
			{				
				System.out.println(i+". "+rs.getString("courseName") + " offered by " + rs.getString("univName") +"\tstatus- "+ rs.getString("scStatus"));
				courseIdMapping.put(i, rs.getInt("pcourseId"));
				i++;
			}
			
			System.out.println("Do you want to go to any of your Course Home Page?");
			String ch = sc.next();
			
			if(ch.equals("y") || ch.equals("Y"))
			{
				System.out.println("Press Course Number to go to that Course Home Page");
				int cnum = sc.nextInt();
								
				if(courseIdMapping.containsKey(cnum))
				{
					// If student entered course is in his course database then select its course id
					PreparedStatement ps4=con.prepareStatement("Select c.cid as cid from course c "
							+ "where c.id=?;");
					ps4.setInt(1, courseIdMapping.get(cnum));
					ResultSet rs3 = ps4.executeQuery();
					
					if(!rs3.isBeforeFirst())
					{
						System.out.println("\nThere is no course of input course number!");
						return;
					} else
					{					
						while(rs3.next())
						{
							courseHome(con, id, rs3.getString("cid"), courseIdMapping.get(cnum));
						}
					}

				} else {
					System.out.println("Invalid course number");
					System.out.println("Please enter the valid course number from below");
					viewAllMyCourses(con, id);
				}
			} else if(ch.equals("n") || ch.equals("N"))
			{
				navigation(con,id);
				
			} else 
			{
				System.out.println("Invalid input");
				viewAllMyCourses(con, id);
			}
		}
		
		navigation(con,id);
		sc.close();
		}
	
	// Student's Active Courses page
	public void viewActiveCourses(Connection con, int id) throws Exception 
	{
		TreeMap<Integer, Integer> courseIdMapping = new TreeMap<Integer, Integer>();
		int i=1;
		String cid=null;
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Student " + studName +" courses");
		
		// Select all the active courses of user student
		String activeCourse = "Select c.name as courseName, c.id as pcourseId, c.cid as cid, u.name as univName, sc.status as scStatus"
				+ " from course c, university u, student s, studentcourse sc "
				+ "where s.id = ? and sc.takenby = s.id and sc.takes = c.id and c.offeredby = u.id and sc.status=?;";
		PreparedStatement ps = null;
		
		ps = con.prepareStatement(activeCourse);
		ps.setInt(1,id);
		ps.setString(2, "Active");
		
		ResultSet rs = ps.executeQuery();		
				
		if(!rs.isBeforeFirst())
		{
			System.out.println("No active courses.");
		}
		else
		{
			System.out.println("My Active Courses:");
			
			while(rs.next())
			{				
				System.out.println(i+". "+rs.getString("courseName") + " offered by " + rs.getString("univName") + "\tstatus- " + rs.getString("scStatus"));
				courseIdMapping.put(i, rs.getInt("pcourseId"));
				cid = rs.getString("cid");
				i++;
			}
			
			System.out.println("Do you want to go to any of your Course Home Page?[y/n]");
			String ch = sc.next();
			
			if(ch.equals("y") || ch.equals("Y"))
			{
				System.out.println("Press Course Number to go to that Course Home Page");
				int cnum = sc.nextInt();
								
				if(courseIdMapping.containsKey(cnum))
				{
					// If student entered course number is in his active courses database then select its course id
					PreparedStatement ps4=con.prepareStatement("Select c.cid as cid from course c "
							+ "where c.id=?;");
					ps4.setInt(1, courseIdMapping.get(cnum));
					ResultSet rs3 = ps4.executeQuery();
					
					if(!rs3.isBeforeFirst())
					{
						System.out.println("\nThere is no course of input course number!");
						return;
					} else
					{					
						while(rs3.next())
						{
							courseHome(con, id, rs3.getString("cid"), courseIdMapping.get(cnum));
						}
					}

				} else {
					System.out.println("Invalid option");
					System.out.println("Please enter the valid course number from below");
					viewAllMyCourses(con, id);
				}
			} else if(ch.equals("n") || ch.equals("N"))
			{
				navigation(con,id);
				
			} else 
			{
				System.out.println("Invalid input");
				viewAllMyCourses(con, id);
			}
		}
		
		navigation(con,id);
		sc.close();
	}

	// Student's Completed Courses page
	public void viewCompletedCourses(Connection con, int id) throws Exception 
	{
		TreeMap<Integer, Integer> courseIdMapping = new TreeMap<Integer, Integer>();
		int i=1;
		String cid=null;
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Student " + studName +" courses");
		
		// Select all the completed courses of user student
		String completedCourse = "Select c.name as courseName, c.id as pcourseId, u.name as univName, sc.status as scStatus"
				+ " from course c, university u, student s, studentcourse sc "
				+ "where s.id = ? and sc.takenby = s.id and sc.takes = c.id and c.offeredby = u.id and sc.status=?;";
		PreparedStatement ps = null;
		
		ps = con.prepareStatement(completedCourse);
		ps.setInt(1,id);
		ps.setString(2, "Completed");
		
		ResultSet rs = ps.executeQuery();		
				
		if(!rs.isBeforeFirst())
		{
			System.out.println("No completed courses.");
		}
		else
		{
			System.out.println("My Completed Courses:");
			
			while(rs.next())
			{		
				System.out.println(i+". "+rs.getString("courseName") + " offered by " + rs.getString("univName") + "\tstatus- " + rs.getString("scStatus"));
				courseIdMapping.put(i, rs.getInt("pcourseId"));
				i++;
			}
			
			System.out.println("Do you want to go to any of your Course Home Page?");
			String ch = sc.next();
			
			if(ch.equals("y") || ch.equals("Y"))
			{
				System.out.println("Press Course Number to go to that Course Home Page");
				int cnum = sc.nextInt();
				
				
				if(courseIdMapping.containsKey(cnum))
				{
					// If student entered course number is in his completed courses database then select its id
					PreparedStatement ps4=con.prepareStatement("Select c.cid as cid from course c "
							+ "where c.id=?;");
					ps4.setInt(1, courseIdMapping.get(cnum));
					ResultSet rs3 = ps4.executeQuery();
					
					if(!rs3.isBeforeFirst())
					{
						System.out.println("\nThere is no course of input course number!");
						return;
					} else
					{					
						while(rs3.next())
						{
							courseHome(con, id, rs3.getString("cid"), courseIdMapping.get(cnum));
						}
					}

				} else {
					System.out.println("Invalid option");
					System.out.println("Please enter the valid course number from below");
					viewAllMyCourses(con, id);
				}
			} else if(ch.equals("n") || ch.equals("N"))
			{
				navigation(con,id);
				
			} else 
			{
				System.out.println("Invalid input");
				viewAllMyCourses(con, id);
			}
		}
		
		navigation(con,id);
		sc.close();
	}
	
	// Display all the courses offered by OCMS
	public void viewAllCourses(Connection con, int id) throws Exception 
	{	
		Scanner sc = new Scanner(System.in);
		
		// Select all courses offered by OCMS
		String allCourses = "Select c.name as courseName, c.cid as courseId, u.name as univName, p.name as professorName "
				+ "from course c, university u, professor pr, professorcourse pc, person p "
				+ "where c.offeredby = u.id and pc.teaches=c.id and pc.taughtby=pr.id and pr.id=p.id;";
		PreparedStatement ps = null;		
		ps = con.prepareStatement(allCourses);

		ResultSet rs = ps.executeQuery();
				
		if(!rs.isBeforeFirst())
		{
			System.out.println("No courses to offer.");
		}
		else
		{
			System.out.println("All Courses:");
			
			while(rs.next())
			{				
				System.out.println("\n" + rs.getString("courseName") + "\nCourse Id - " +
						rs.getString("courseId") + "\noffered by " + rs.getString("univName")
						+ "\ntaught by" + rs.getString("professorName"));				
			}
		}
		System.out.println("\nDo you want to enroll or know more about any courses listed?[y/n]");
		String ans = sc.next();
		
		if(ans.equals("y") || ans.equals("Y"))
		{
			sc.nextLine();
			System.out.println("Enter the course id of the course to see its details");
			String cid = sc.nextLine();
			gotoCourse(con,id,cid);  //Go to the selected course page that displays the course details
		} else if(ans.equals("n") || ans.equals("N"))
			{
				System.out.println("\n1) Go Back"
						+ "\n2) Go to the Home Page \n"
						+ "3) Sign out\n");
				int choice = sc.nextInt();
				
				switch(choice)
				{
				case 1:
					viewAllCourses(con, id);
					break;
				case 2:
					start(con, id);
					break;
				case 3:
					System.out.println("You have been signed out!");
					TestConnection tc = new TestConnection();
					tc.loginRegister(con);
					break;
				default:
					System.out.println("Sorry! you have entered the wrong choice!");
					viewAllCourses(con, id);
				}
			} else
			{	
				System.out.println("Invalid input.");
			}

		sc.close();
	}
	
	// Select student name
	public String fetchName(Connection con, int id) throws Exception 
	{
		String sName = "Select p.name from person p, student s "
				+ "where s.id=p.id and s.id = ?;";
		PreparedStatement ps0 =con.prepareStatement(sName);
		ps0.setInt(1, id);
		
		ResultSet rs = ps0.executeQuery();
		
		if(!rs.isBeforeFirst())
		{
			System.out.println("You have been signed out.");
			TestConnection tc = new TestConnection();
			tc.loginRegister(con);
		}
		else
		{
			while (rs.next()) {
				studName = rs.getString(1);				
			}
		}
		return studName;
	}
	
	// Common navigation function for view AllMyCourses, viewActiveCourses and viewCompletedCourses
	public void navigation(Connection con, int id) throws Exception
	{
		Scanner sc = new Scanner(System.in);
		System.out.println("\n1) Go back\n"
				+ "2) Go to the Home Page \n"
				+ "3) Sign out\n");
		int choice = sc.nextInt();
				
		switch(choice)
		{
		case 1:
			viewMyCourses(con, id);
			break;
		case 2:
			start(con, id);
			break;
		case 3:
			System.out.println("You have been signed out!");
			TestConnection tc = new TestConnection();
			tc.loginRegister(con);
		default:
			System.out.println("Sorry! you have entered the wrong choice!");
			navigation(con,id);
		}
		sc.close();
	}
	
	// Course Details Page
	public void gotoCourse(Connection con, int id, String cid) throws Exception
	{
		Scanner sc = new Scanner(System.in);
		
		// Fetch selected course details
		String course = "Select c.name as courseName, c.cid as courseId, c.description as description, c.id as course, "
				+ "u.name as univName, p.name as profName, pr.designation as designation "
				+ "from course c, university u, professor pr, professorcourse pc, person p "
				+ "where c.offeredby = u.id and pc.teaches=c.id and pc.taughtby=pr.id and pr.id=p.id and c.cid=?;";
				
		PreparedStatement ps = null;		
		ps = con.prepareStatement(course);
		ps.setString(1, cid);

		ResultSet rs = ps.executeQuery();
		int pcourseId = 0;
		String courseName = null;
				
		if(!rs.isBeforeFirst())
		{
			System.out.println("No course of specified search found.");
		}
		else
		{			
			if(rs.next())
			{
				pcourseId = rs.getInt("course");
				courseName = rs.getString("courseName");
				
				System.out.println("\n" + courseName);
				System.out.println("About the course:  " + rs.getString("description"));
				System.out.println("Offered by:  " + rs.getString("univName"));
				System.out.println("Taught by:  " + rs.getString("profName") + " ," + rs.getString("designation"));
				
				while(rs.next())
				{
					System.out.println("Taught by:  " + rs.getString("profName") + " ," + rs.getString("designation"));
				}
				
				TreeMap<Integer, Integer> lectrsMapping = new TreeMap<Integer, Integer>(); //Mapping of lectures id to user's input 
				lectrsMapping = viewLectures(con,id,cid,pcourseId,"allLectrs");						
			}			
		}
		
		System.out.println("\nDo you want to enroll now to this course?[y/n]");
		String ans = sc.next();
		
		if(ans.equals("y") || ans.equals("Y"))
		{
			//Enroll selected course 
			String enroll = "Insert into studentcourse(takes,takenby,status) "
					+ "select ?,?,?"
					+ "where not exists (Select * from studentcourse where takes=? and takenby=?);";
			
			PreparedStatement ps3 = null;	
			ps3 = con.prepareStatement(enroll);
			ps3.setInt(1, pcourseId);
			ps3.setInt(2, id);
			ps3.setString(3, "Active");
			ps3.setInt(4, pcourseId);
			ps3.setInt(5, id);

			int count = ps3.executeUpdate();
			
			if(count>0)
			{
				System.out.println("\nWelcome to the course " + courseName
				+ ". You can now access the course materials.\n");
				System.out.println("1) Start Learning \n2) Go Back"
						+ "\n3) Go to the Home Page "
						+ "\n4) Sign Out");
				
				int ch = sc.nextInt();
				
				switch(ch)
				{
				case 1:
					courseHome(con, id, cid, pcourseId);
					break;
				case 2:
					viewAllMyCourses(con, pcourseId);
					break;
				case 3:
					start(con, id);
					break;
				case 4:
					System.out.println("You have been signed out!");
					TestConnection tc = new TestConnection();
					tc.loginRegister(con);
					break;
				default:
					System.out.println("Sorry! you have entered the wrong choice!");
					gotoCourse(con, pcourseId, cid);
				}
			} else
			{
				System.out.println("You are already enrolled for this course.");
				courseHome(con,id,cid,pcourseId);
			}
			
		} else if(ans.equals("n") || ans.equals("N"))
			{
				System.out.println("\n1) Go Back"
						+ "\n2) Go to the Home Page \n"
						+ "3) Sign out\n");
				int choice = sc.nextInt();
				
				switch(choice)
				{
				case 1:
					viewAllCourses(con, pcourseId);
					break;
				case 2:
					start(con, id);
					break;
				case 3:
					System.out.println("You have been signed out!");
					TestConnection tc = new TestConnection();
					tc.loginRegister(con);
				default:
					System.out.println("Sorry! you have entered the wrong choice!");
				}
			} else
			{
				System.out.println("Invalid input.");
				gotoCourse(con, pcourseId, cid);
			}
		sc.close();
	}

	// Course Home of selected course
	public void courseHome(Connection con, int id, String cid, int pcourseId) throws Exception
	{		
		Scanner sc = new Scanner(System.in);
		System.out.println("\nCourse Home");
		System.out.println(cid+"\n");
		System.out.println("1) View Lectures\n2) Discussion Forums\n3) Back\n4) Sign out");
		int ch = sc.nextInt();
		
		switch(ch)
		{
		case 1:
			TreeMap<Integer, Integer> lectrsMapping = new TreeMap<Integer, Integer>();
			lectrsMapping = viewLectures(con,id,cid,pcourseId,"studLectr");
			viewLectrsChoice(con, cid, id, pcourseId, lectrsMapping);
			break;
		case 2:
			CommonFunctions c = new CommonFunctions();
			c.viewFollowup(con, pcourseId, id, "Student", cid);
			break;
		case 3:
			viewMyCourses(con,id);
			break;
		case 4:
			System.out.println("You have been signed out!");
			TestConnection tc = new TestConnection();
			tc.loginRegister(con);
			break;
		default:
			System.out.println("Invalid input");
			System.out.println("Please choose from the below options");
			courseHome(con, id, cid, pcourseId);
			break;
		}
				
	}
	
	// Fetches lecture
 	public int fetchLecture(TreeMap<Integer,Integer> lectrsMapping)
	{
		Scanner sc = new Scanner(System.in);
		int lnum = sc.nextInt();
		int lid=0;
		if(lectrsMapping.containsKey(lnum))
		{
			lid = lectrsMapping.get(lnum);
		} else
		{
			System.out.println("You have entered the wrong lecture number.");
			System.out.println("Please choose the valid lecture number");
			fetchLecture(lectrsMapping);
		}
		return lid;
	}
	
 	// Display lectures of selected course
	public TreeMap<Integer, Integer> viewLectures(Connection con, int id, String cid, int pcourseId, String cond) throws SQLException
	{
		TreeMap<Integer, Integer> sViewsViewedby = new TreeMap<Integer, Integer>(); //Map lecture's views to lecture's viewedby
		TreeMap<Integer, Integer> lectrsMap = new TreeMap<Integer, Integer>();  //Map lecture's id to student's available input
		
		String lectr = "Select l.id as lid, l.topic as topic, l.lecturetype as type, l.topicdescription as desc "
				+ "from lecture l, course c "
				+ "where c.cid=? and l.partof=c.id order by l.id;";
		
		String lectrCount = "select count(l.id) from lecture l where l.partOf=?;";
		
		PreparedStatement ps1 = con.prepareStatement(lectr);
		ps1.setString(1, cid);
		
		PreparedStatement ps2 = con.prepareStatement(lectrCount);
		ps2.setInt(1, pcourseId);
		
		ResultSet rs1 = ps2.executeQuery();
		
		int count = 0;
		if(rs1.next())
		{
			count = rs1.getInt(1);
			System.out.print("\nTotal lectures: " + count);
		}
		
		rs1 = ps1.executeQuery();
		
		if(!rs1.isBeforeFirst())
		{
			System.out.println("\n\nThere are no lectures added in this course.");
		}
		else
		{
			int i=1;
			System.out.println("\n Syllabus\n");
			while(rs1.next())
			{
				for(int k=0; k<count; k++ )
				{
					lectrsMap.put(i, rs1.getInt("lid"));
				}
				
				if(cond.equals("studLectr"))
				{
					String slectr = "Select sl.views as views, sl.viewedby as viewedby from studentlecture sl "
							+ "where sl.viewedby=? and sl.views=?;";
					PreparedStatement ps3 = con.prepareStatement(slectr);
					ps3.setInt(1, id);
					ps3.setInt(2, rs1.getInt("lid"));
					
					ResultSet rs = ps3.executeQuery();
					
					while(rs.next())
					{
						sViewsViewedby.put(rs.getInt("views"), rs.getInt("viewedby"));
					}			
				}
				System.out.println(i +". " + rs1.getString("type") +": "
						+ rs1.getString("topic"));
				System.out.println(rs1.getString("desc"));
				
				if(cond.equals("studLectr"))
				{
					System.out.print("Status: ");
					if(sViewsViewedby.containsKey(rs1.getInt("lid")))
					{
						System.out.println("Completed.");
					} else
					{
						System.out.println("Pending");
					}
				}
				System.out.print("\n");
				i++;
			}
			
		}
		return lectrsMap;
	}
	
	// Insert viewed lecture in database
	public void viewLectrsChoice(Connection con, String cid, int id, int pcourseId, TreeMap<Integer, Integer> lectrsMapping) throws Exception
	{
		Scanner sc = new Scanner(System.in);
		System.out.println("Do you want to view lectures?[y/n]");
		String ch = sc.next();
		
		if(ch.equals("y") || ch.equals("Y"))
		{
			System.out.println("\nEnter the lecture number which you would want to watch/read:");
			
			int lid = fetchLecture(lectrsMapping);	
			
			// update lecture views data
			String lectrViewed = "Insert into studentlecture(views,viewedby)"
					+ "select ?,?"
					+ "where not exists (Select * from studentlecture where views=? and viewedby=?);";
			PreparedStatement ps = con.prepareStatement(lectrViewed);
			ps.setInt(1, lid);
			ps.setInt(2, id);
			ps.setInt(3, lid);
			ps.setInt(4, id);
			
			int count = ps.executeUpdate();
			
			if(count>0)
			{
				System.out.println("Thank you for viewing this lecture");
				
			} else
			{
				System.out.println("Thank you for viewing this lecture again");
			}
			courseHome(con, id, cid, pcourseId);
		} else if(ch.equals("n") || ch.equals("N"))
		{
			courseHome(con, id, cid, pcourseId);
		} else
		{
			System.out.println("Invalid input");
			System.out.println("Enter the correct input");
			viewLectrsChoice(con, cid, id, pcourseId, lectrsMapping);
		}
		sc.close();
	}
}

