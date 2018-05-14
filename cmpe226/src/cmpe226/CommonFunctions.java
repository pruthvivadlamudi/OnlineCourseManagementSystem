package cmpe226;

import java.sql.*;
import java.util.*;

public class CommonFunctions 
{
	String cond=null;
	
	String[] splitAddress(String address)
	{
		address= address.substring(1, address.length()-1);
		
		String[] split = address.split(",");
		
		return split;
		
	}
	
	void viewFollowup(Connection con, int c_id, int p_id, String cond, String cid) throws Exception 
	{
		this.cond = cond;
		@SuppressWarnings("resource")
		Scanner st = new Scanner(System.in);
		CommonFunctions c = new CommonFunctions();
		TreeMap<Integer,Integer> postIdMapping = new TreeMap<Integer,Integer>();
		String whoPosted=null;
		
		PreparedStatement viewPost = con.prepareStatement("select f.post as post, f.id as postid, p.name as name from forum f, person p "
				+ "where f.partof=? and f.askedby=p.id order by f.id desc;");
		
		PreparedStatement postedBy = con.prepareStatement("Select id as pid, designation as desig "
				+ "from professor where id=?");
		postedBy.setInt(1, p_id);
		ResultSet rs1 = postedBy.executeQuery();
		
		try
		{
			viewPost.setInt(1, c_id);
			int i=1;
			System.out.println("\nDiscussion Forums");
			ResultSet rs_viewPost = viewPost.executeQuery();			
			if(!rs_viewPost.isBeforeFirst())
			{
				System.out.println("\nThere are no posts added for this course!");
//				return;
			} else
			{
				while(rs_viewPost.next())
				{
					int postid = rs_viewPost.getInt("postid");
					System.out.print("\n"+i+". "+rs_viewPost.getString("post")+"\nPosted by  ");
					if(!rs1.isBeforeFirst())
					{
						whoPosted="Student";
					} else
					{
						while(rs1.next())
						{
							whoPosted = rs1.getString("desig");
						}
					}
						//System.out.println("\nThere are no posts added for this course!");
					System.out.println(rs_viewPost.getString("name")+"   "+whoPosted);				
					postIdMapping.put(i, postid);
					i++;
									
					System.out.println("\nFollow ups");
					String viewFol = "select fo.comments as comments, p.name as pname from followup fo, person p "
							+ "where exists(select fo.id from forum f where fo.partof=f.id "
							+ "and fo.partof=? and f.partof=?)"
							+ " and fo.askedby=p.id order by fo.id desc;";
					PreparedStatement viewFollowup = con.prepareStatement(viewFol);
					viewFollowup.setInt(1, postid);
					viewFollowup.setInt(2, c_id);
					
					ResultSet rs = viewFollowup.executeQuery();
					
					if(!rs.isBeforeFirst())
					{
						System.out.println("\nThere are no followups added for this course!");
						//return;
					} else
					{
						while(rs.next())
						{
							System.out.print(rs.getString("comments")+"\nReplied by ");
							System.out.println(rs.getString("pname")+"   "+whoPosted+"\n");
						}
					}
				}
			}
						
			while(true)
			{
			System.out.println("\nDo you want to \n1) Add new post \n2) Add comment to existing post \n3) Go Back");
			int choice = st.nextInt();
			switch(choice)
			{
			case 1: addpostForum(con,c_id,p_id);
				break;
			case 2: System.out.println("Enter the post id from above");
					int input = st.nextInt();
					
					if(postIdMapping.containsKey(input))
					{
						addpostFollowup(con,c_id,postIdMapping.get(input),p_id);
					} 
					else
					{						
						return;
					}					
				break;
			case 3:
				if(cond.equalsIgnoreCase("Student"))
				{
					StudentPage s = new StudentPage();
					s.courseHome(con, p_id, cid, c_id);;
				}
				else
				{
					ProfessorPage p = new ProfessorPage();
					p.start(con, p_id);
				}
//				return;
			default: System.out.println("You have entered the wrong option!");
//				return;
			}
			}
		}
		finally
		{
			viewPost.close();
		}	
	}
	
	void addpostFollowup(Connection con, int c_id, int post_id, int p_id) throws Exception 
	{
		int f_id=0;
		@SuppressWarnings("resource")
		Scanner st = new Scanner(System.in);
		PreparedStatement getID = con.prepareStatement("Select max(id) from followup");
		PreparedStatement insFollowup = con.prepareStatement("insert into followup values(?,?,?,?)");
		try
		{
			ResultSet rs_getID = getID.executeQuery();
			if(!rs_getID.isBeforeFirst())
			{
				f_id=0;
			}
			while(rs_getID.next())
			{
				f_id=rs_getID.getInt(1) + 1;
			}
			
			System.out.println("Enter your reply: ");
			String comment = st.nextLine();
			
			insFollowup.setInt(1, f_id);
			insFollowup.setString(2, comment);
			insFollowup.setInt(3, post_id);
			insFollowup.setInt(4, p_id);
			int rs_insFollowup = insFollowup.executeUpdate();
			if(rs_insFollowup<=0)
			{
				System.out.println("Follow up could not be added..!s");
				viewFollowup(con, c_id, p_id, cond, "");
			}
			else
			{
				System.out.println("\nYou have added your reply successfully.");
				viewFollowup(con, c_id, p_id, cond, "");
			}
		}
		finally
		{
			getID.close();
			insFollowup.close();
		}				
	}
	
	void addpostForum(Connection con, int c_id, int p_id) throws Exception 
	{
		int f_id=0, f_id2=0;
		@SuppressWarnings("resource")
		Scanner st = new Scanner(System.in);
		PreparedStatement getID = con.prepareStatement("Select max(id) from forum");
		PreparedStatement insForum = con.prepareStatement("insert into forum(id,post,askedby,partof) values(?,?,?,?)");

		try
		{
			ResultSet rs_getID = getID.executeQuery();
			if(!rs_getID.isBeforeFirst())
			{
				f_id=0;
			}
			while(rs_getID.next())
			{
				f_id=rs_getID.getInt(1) + 1;
			}
			
			System.out.println("Enter post: ");
			String post = st.nextLine();
			
			insForum.setInt(1, f_id);
			insForum.setString(2, post);
			insForum.setInt(3, p_id);
			insForum.setInt(4, c_id);
			
			int rs_insForum = insForum.executeUpdate();
			
			if(rs_insForum<=0)
			{
				System.out.println("post could not be added..!");
			}
			else
			{
				System.out.println("You have added post successfully.");
				viewFollowup(con, c_id, p_id, cond, "");
			}
		}
		finally
		{
			insForum.close();
		}				
	}
}
