import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;


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
					if(quantity>0.0)
					{
				// execute update SQL stetement
				
					}
					else if(quantity==Integer.MIN_VALUE)
					{
						//Not exists!! Insert query
					}
					else
					{
						//exists and not waste!! update query
						// execute update SQL stetement
					}
			}
		}
	}
}
