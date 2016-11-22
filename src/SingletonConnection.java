import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SingletonConnection{
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static final String DB_URL = "jdbc:mysql://localhost/feedinventory";

   //  Database credentials
   static final String USER = "root";
   static final String PASS = "barae";
   private static volatile Connection conn = null;
   
   
   private SingletonConnection()
   {
	   Logger.getLogger(SingletonConnection.class.getName()).log(Level.SEVERE,"Singleton connection object!!" );
   }
    public synchronized static Connection getConnection()
    {
        if(conn==null){
    	try {
        	//STEP 2: Register JDBC driver
	      Class.forName("com.mysql.jdbc.Driver");
	
	      //STEP 3: Open a connection
	      System.out.println("Connecting to database...");
	      conn = DriverManager.getConnection(DB_URL,USER,PASS);
             
        } catch (Exception ex) {
            Logger.getLogger(SingletonConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    	return conn;
        }
        else
        {
        	return conn;
        }
    
    }
}
