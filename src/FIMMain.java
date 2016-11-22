import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


public class FIMMain {

	//static int wasteId=0;
	
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
				
				}
				if(quantity>0.0)
				{
					//System.out.println(quantity);
					// if previous quantity is there when the new inventory feed comes
					// update the waste
					
					String sqlupdate ="update waste set quantity = ? "
			                  + "where zooid='"+zooId+"' and feedid='"+feedid+"'";
					PreparedStatement psupdate=conn.prepareStatement(sqlupdate);
					psupdate.setDouble(1, quantity);
					int updateInd =psupdate.executeUpdate();
					System.out.println(updateInd);
					// if the feed is not already present, then insert the quantity of waste for the new feed
					// or just updatet the quantity of waste for the feed
					if(updateInd==0){
						String sql="insert into waste(quantity, zooid, feedid) values (?,?,?)";
						PreparedStatement ps=conn.prepareStatement(sql);
						ps.setDouble(1, quantity);
						ps.setInt(2, zooId);
						ps.setInt(3, feedid);
						ps.executeUpdate();
					
					}
					// update new feed
					String sql1="update feed set quantity = ? "
			                  + "where zooid='"+zooId+"' and animalid='"+animalId+"'";
					PreparedStatement ps1=conn.prepareStatement(sql1);
					ps1.setDouble(1, newQuantity);
										ps1.executeUpdate();
				}
				else if(quantity==Integer.MIN_VALUE)
				{
					//new feed 
					
					String sql="insert into feed(zooid, animalid, quantity) values (?,?,?)";
					PreparedStatement ps=conn.prepareStatement(sql);
					ps.setInt(1, zooId);
					ps.setInt(2, animalId);
					ps.setDouble(3, newQuantity);
					ps.executeUpdate();
				}
				else
				{
					//feed exists and no waste is there !! update feed query
					 // and waste need not be updated
					String sql1="update feed set quantity = ? "
			                  + "where zooid='"+zooId+"' and animalid='"+animalId+"'";
					PreparedStatement ps1=conn.prepareStatement(sql1);
					ps1.setDouble(1, newQuantity);
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
				// if user enters an animal that is not exist in the zoo
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
				
				
				// update the quantity in the current feed
				ResultSet rset=stat1.executeQuery("select quantity from feed where animalid='"+animalID+"'");
				Double currQuantity=Double.MIN_VALUE;
				while(rset.next())
				{
					currQuantity= rs1.getDouble(1);
										
				}
				String sql1="update feed set quantity = ? "
		                  + "where animalid='"+animalID+"'";
				PreparedStatement psupdate=conn.prepareStatement(sql1);
				psupdate.setDouble(1, currQuantity-quantityFed);
				psupdate.executeUpdate();
				break;
			
			case "3":
				// queries for the IZI's prioritized requirement-list-for-phase-I
				conn=SingletonConnection.getConnection();
				stat= conn.createStatement();
				
				
				ResultSet result=stat.executeQuery("SELECT AnimalSpecies, timeFed, quantiyfed as avgQtyFed from recordfeeding, animal where animal.animalID= recordfeeding.animalId group by AnimalSpecies, timeFed");
				System.out.println("\nAnimalSpecies \t timeFed \t quantiyfed");
				while(result.next())
				{
					System.out.println(result.getString(1)+ "\t" + result.getTimestamp(2)+ "\t" +result.getDouble(3));
					
				}
				
				
				ResultSet result1=stat.executeQuery("SELECT count(animalID)/count(distinct(timeFed)) from recordfeeding where animalID IN (select distinct (animalID) from recordfeeding) group by animalID");
				System.out.println("\nAvg times per day the animals are fed");
				while(result1.next())
				{
					System.out.println(result1.getDouble(1)+ "\t" );
					
				}
				
				
				ResultSet result2=stat.executeQuery("select sum(quantity), zooName from waste, zoo where waste.zooid= zoo.zooId group by waste.zooid");
				System.out.println("\nsum(quantity)of food \t wasted by zoo \t");
				while(result2.next())
				{						
					System.out.println(result2.getDouble(1)+ "\t" + result2.getString(2));
							
				}
				
				
				ResultSet result3=stat.executeQuery("select AnimalSpecies, zooName, avg(quantiyfed) from animal, zoo, recordfeeding where animal.animalID=recordfeeding.animalId and zoo.zooId=recordfeeding.zooid group by AnimalSpecies, zooName");
				System.out.println("\n AnimalSpecies \t zooname \tavg(quantiyfed)");
				while(result3.next())
				{							
					System.out.println(result3.getString(1)+ "\t\t" + result3.getString(2)+ "\t\t"+ result3.getDouble(3));
							
				}
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
