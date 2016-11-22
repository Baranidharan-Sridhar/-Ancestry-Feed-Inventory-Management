import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


public class FIMMain {
	public static void main(String args[]) throws Exception
	{
		BufferedReader br=null;
		Connection conn=null;
		while(true)
		{
			System.out.println("Ancestry Inventory Management System\n\n");
			System.out.println("Enter your option\n");
			System.out.println("" +
				"1.Add New Feed Inventory\n " +
				"2.Record feed\n" +
				"3.View Priority requirements list .\n" +
				"4.Exit ");
			br=new BufferedReader(new InputStreamReader(System.in));
			String input=br.readLine();
			switch(input)
			{
				case "1":
					conn=SingletonConnection.getConnection();
					
					System.out.println("Enter Zoo ID: \n");
					Integer zooId=Integer.parseInt(br.readLine());
					
					System.out.println("Enter Animal ID: \n");
					Integer animalId=Integer.parseInt(br.readLine());
					
					System.out.println("Enter quantity:\n");
					double newQuantity=Double.parseDouble(br.readLine());
					
					Statement stat=conn.createStatement();
					ResultSet rs=stat.executeQuery("select feedid, quantity from feed where zooid='"+zooId+"' and animalid='"+animalId+"'");
					
					double quantity=Double.MIN_VALUE;
					int feedid=Integer.MIN_VALUE;
					while(rs.next())
					{
						
						feedid= rs.getInt(1);
						quantity=rs.getDouble(2);
						System.out.println(feedid);
						System.out.println(quantity);
					}
					if(quantity>0.0)
					{
						System.out.println(quantity);
						String sqlupdate ="update waste set quantity = ? "
				                  + "where zooid='"+zooId+"' and feedid='"+feedid+"'";
						PreparedStatement psupdate=conn.prepareStatement(sqlupdate);
						psupdate.setDouble(1, quantity);
						int updateInd =psupdate.executeUpdate();
						System.out.println(updateInd);
						
						if(updateInd==0){
							String sql="insert into waste(quantity, zooid, feedid) values (?,?,?)";
							PreparedStatement ps=conn.prepareStatement(sql);
							//ps.setInt(1, ++wasteId);
							ps.setDouble(1, quantity);
							ps.setInt(2, zooId);
							ps.setInt(3, feedid);
							ps.executeUpdate();
						
						}
						String sql1="update feed set quantity = ? "
				                  + "where zooid='"+zooId+"' and animalid='"+animalId+"'";
						PreparedStatement ps1=conn.prepareStatement(sql1);
						ps1.setDouble(1, newQuantity);
						

						// execute update SQL stetement
						ps1.executeUpdate();
					}
					else if(quantity==Integer.MIN_VALUE)
					{
						//Not exists!! Insert query
						
						String sql="insert into feed(zooid, animalid, quantity) values (?,?,?)";
						PreparedStatement ps=conn.prepareStatement(sql);
						ps.setInt(1, zooId);
						ps.setInt(2, animalId);
						ps.setDouble(3, newQuantity);
						ps.executeUpdate();
					}
					else
					{
						//exists and not waste!! update query
						String sql1="update feed set quantity = ? "
				                  + "where zooid='"+zooId+"' and animalid='"+animalId+"'";
						PreparedStatement ps1=conn.prepareStatement(sql1);
						ps1.setDouble(1, newQuantity);
						

						// execute update SQL stetement
						ps1.executeUpdate();
						
					}
					break;
				case "2":
					conn=SingletonConnection.getConnection();
					
					System.out.println("Enter Animal to be Fed: \n");
					String animal=br.readLine().toString();
					Statement stat1=conn.createStatement();
					ResultSet rs1=stat1.executeQuery("select animalID, count(animalID) from animal where animalSpecies='"+animal+"'");
					int animalID=Integer.MIN_VALUE;
					int count=-1;
					while(rs1.next())
					{
						animalID= rs1.getInt(1);
						count= rs1.getInt(2);
						
					}
					if(count==0){
						System.err.println("Enter valid animal name to feed");
						break;
						
					}
					
					System.out.println("Enter quantity to be fed:\n");
					double quantityFed=Double.parseDouble(br.readLine());
					java.sql.Timestamp tstamp= getCurrentTimeStamp();
					
					String sql="insert into recordfeeding(animalId, quantiyfed, timeFed) values (?,?,?)";
					PreparedStatement ps=conn.prepareStatement(sql);
					ps.setInt(1, animalID);
					ps.setDouble(2, quantityFed);
					ps.setTimestamp(3, tstamp);
					ps.executeUpdate();
					
				
					break;
				case "3":
					break;
				case "4":
					System.exit(0);
				
			}
		}
	}
	private static java.sql.Timestamp getCurrentTimeStamp() {

		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());

	}
}
