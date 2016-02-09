import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Bank {

	/**
	 * The name of the bank.
	 */
	private String name;

	/**
	 * Create a new Bank object with empty lists of users and accounts.
	 */
	public Bank(String name) {

		this.name = name;
	} 

	/**
	 * Get the User object associated with a particular userID and pin, if they
	 * are valid.
	 * @param cardNumber	the user ID to log in
	 * @param pin		the associate pin of the user
	 * @return			the User card number, if login is successful, or "none", if 
	 * 					it is not
	 */
	public String userLogin(String cardNumber, String pin) {

		Connection connect = null;
		Statement statement = null;
//cardNumber="12334";

		try {
	
			Class.forName("com.mysql.jdbc.Driver");
			connect = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/atm?"+"user=root&password=1234"); //this will connect to the database
			statement = (Statement) connect.createStatement();
			ResultSet rs = statement.executeQuery("select name,savings_bal,current_bal from db where card="+cardNumber+" && pin="+pin+";");
			rs.next();
			
			String name=rs.getString("name");
			System.out.print(name+" >>	savings:"+rs.getInt("savings_bal")+"	current:"+rs.getInt("current_bal")+"\n");
    
			connect.close();
			statement.close();
    
			return cardNumber;

		}catch(Exception e){
			System.out.print("Username and PIN combination mismatch.\n");

		}
		
		return "none";
}
	
	
	/**
	 * Get the name of the bank.
	 * @return	the name
	 */
	public String getName() {
		return this.name;
	} 

}