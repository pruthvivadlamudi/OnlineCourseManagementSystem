package cmpe226;

import java.sql.*;
import java.util.*;

public class ProfessorPage 
{
	Connection con=null;
	int id = 0; 																// id of the Professor
	TreeMap<Integer, String> courseIDName = new TreeMap<Integer, String>();     // stores course id, name
	TreeMap<Integer, Integer> mapper = new TreeMap<Integer, Integer>();         // stores course no, course id
	
	TreeMap<Integer, String> lecIDName = new TreeMap<Integer, String>();        // stores lecture id, name
	TreeMap<Integer, Integer> l_mapper = new TreeMap<Integer, Integer>();       // stores lecture no, lecture id
	
	public void start(Connection con, int id) throws Exception 
	{
		this.con = con;
		this.id = id;		
		@SuppressWarnings("resource")
		Scanner st = new Scanner(System.in);		
		int temp = 0, counter = 0;
		
		// SQL statements
		PreparedStatement displayName = con.prepareStatement("Select distinct name from person,login "
				+ " where login.type = 'Professor' and person.id = ?");
		PreparedStatement getCourseIDName = con.prepareStatement("Select id, name from course, professorcourse"
				+ " where course.id = professorcourse.teaches"
				+ " and professorcourse.taughtby = ?");
		try
		{
			// set Prof id
			displayName.setInt(1,id);
			
			// execute SQL query
			ResultSet rs = displayName.executeQuery();			
			if(!rs.isBeforeFirst())
			{
				System.out.println("Welcome Professor!");
			}
			while(rs.next())
			{
				System.out.println("Welcome Professor "+rs.getString(1));
			}
			
			// set Prof id
			getCourseIDName.setInt(1,id);
			
			// execute SQL query
			ResultSet rs1 = getCourseIDName.executeQuery();			
			if(!rs1.isBeforeFirst())
			{
				System.out.println("There is no course registered");
				LoginPage l = new LoginPage();
				l.login(con);
			}
			while(rs1.next())
			{
				// storing courses in TreeMap
				temp = rs1.getInt(1);
				courseIDName.put(temp, rs1.getString(2));
				mapper.put(++counter, temp);
			}
			for(Integer i : mapper.keySet())
			{
				System.out.println(i+") "+courseIDName.get(mapper.get(i)));
			}
			
			// '0' takes back to login page, any other no chooses a course
			System.out.println("Press '0' to exit else select your course number: ");
			int c_id = st.nextInt();
			if(c_id == 0)
			{
				LoginPage l_obj = new LoginPage();
				l_obj.login(con);
				return;
			}
			else
			{
				System.out.println("You have selected course no: "+c_id);			
				while(true)
				{	
					System.out.println("Choose your option for course '"+courseIDName.get(mapper.get(c_id))+
							"': \n1) View Lectures \n2) Add Lectures \n3) Delete Lectures \n4) View Forum \n5) Go Back");
					int choice = st.nextInt();
					
					switch(choice)
					{
						case 1: viewLectures(c_id);
						break;
						case 2: addLectures(c_id);
						break;
						case 3: delLectures(c_id);
						break;
						case 4: CommonFunctions c = new CommonFunctions();
								c.viewFollowup(con,c_id,id, "Professor", "");
						break;
						case 5: start(con, id);
						break;
						default: System.out.println("You have entered the wrong option!");					 
					}
				}
			}
				
		}
		finally
		{			
			getCourseIDName.close();
		}		
	}
	
	private void delLectures(int c_id) throws Exception 
	{
		@SuppressWarnings("resource")
		Scanner st = new Scanner(System.in);
		
		System.out.println("You have added following lectures for this course("+courseIDName.get(mapper.get(c_id))+"): ");
		viewLectures(c_id);
		System.out.println("Enter the lecture number which is to be deleted: ");
		int l_id = st.nextInt();
		int del_l_id=l_mapper.get(l_id);
		System.out.println("You have selected lecture no: "+l_id);
		PreparedStatement delID = con.prepareStatement("delete from Lecture where id=?");	
		try
		{
			// set lec id
			delID.setInt(1, del_l_id);
			
			// execute SQL query
			int rs_delID = delID.executeUpdate();
			if(rs_delID<=0)
			{
				System.out.println("Lecture could not be deleted..!");
				return;
			}
			else
			{
				System.out.println("You have deleted lecture successfully.");
				l_mapper.remove(l_id);
				lecIDName.remove(del_l_id);
				return;
			}
		}
		finally
		{
			delID.close();
		}		
	}

	private void addLectures(int c_id) throws Exception 
	{
		@SuppressWarnings("resource")
		Scanner st = new Scanner(System.in);
		PreparedStatement getID = con.prepareStatement("Select max(id) from Lecture");
		PreparedStatement selectfrmt = con.prepareStatement("SELECT enum_range(NULL::filetype)");	
		PreparedStatement insLecture = con.prepareStatement("insert into Lecture values(?,?,?,?,CAST(? AS filetype),'Reading',?)");
		try
		{
			// execute SQL query to get max id
			ResultSet rs_getID = getID.executeQuery();
			if(!rs_getID.isBeforeFirst())
			{
				id=0;				
			}
			while(rs_getID.next())
			{
				 id=rs_getID.getInt(1) + 1;
			}
			
			System.out.println("Enter topic: ");
			String topic = st.nextLine();
			System.out.println("Enter filename: ");
			String filename = st.nextLine();
			
			// execute SQL query to get file types
			ResultSet rs_selectfrmt = selectfrmt.executeQuery();
			if(!rs_selectfrmt.isBeforeFirst())
			{
				System.out.println("ERROR!!! There are no pre-fed filetypes is the system. Please contact the system admin!");
			}
			String ftypes = "";
			while(rs_selectfrmt.next())
			{
				ftypes = rs_selectfrmt.getString(1);
				System.out.println(ftypes);
			}
			 
			System.out.println("Enter filetype from above: ");
			String filetype = st.nextLine();
			// setting default
			if(!ftypes.contains(filetype))
			{
				filetype = ".mkv";
			}
			System.out.println("Enter topic description: ");
			String topicdesc = st.nextLine();
			System.out.println();
			
			// set values in lecture table
			insLecture.setInt(1, id);
			insLecture.setInt(2, c_id);
			insLecture.setString(3, topic);
			insLecture.setString(4, filename);
			insLecture.setString(5, filetype.toString());
			insLecture.setString(6, topicdesc);
			// execute SQL query
			int rs_insLecture = insLecture.executeUpdate();
			if(rs_insLecture<=0)
			{
				System.out.println("Lecture could not be added..!");
				return;
			}
			else
			{
				System.out.println("You have added lecture successfully.");
				return;
			}
		}
		finally
		{
			getID.close();
			insLecture.close();
		}		
	}

	private void viewLectures(int c_id) throws Exception 
	{
		int temp = 0, counter = 0;
		PreparedStatement view = con.prepareStatement("Select id, topic, filename from lecture where partof=?");	
		try
		{
			// set course id
			view.setInt(1, c_id);
			// execute SQL query to get all lectures for a course
			ResultSet rs_view = view.executeQuery();			
			if(!rs_view.isBeforeFirst())
			{
				System.out.println("There are no lectures added for this course!");
				return;
			}
			while(rs_view.next())
			{
				temp = rs_view.getInt(1);
				lecIDName.put(temp, rs_view.getString(2)+"\t -> \t"+rs_view.getString(3));
				l_mapper.put(++counter, temp);				
			}
			for(Integer i : l_mapper.keySet())
			{
				System.out.println(i+") "+lecIDName.get(l_mapper.get(i)));
			}
		}
		finally
		{
			view.close();
		}
	}
}
