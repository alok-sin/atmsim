import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.mysql.jdbc.PreparedStatement;

public class ATM {

	public DataOutputStream out;
	public DataInputStream ind;
	public BufferedReader inb; 
	
	//MySQL username:
	String user = "root";
	
	//MySQL password:
	String pwd = "1234"; 
	
	ATM(InputStream inputStream,DataOutputStream out){
		this.ind = new DataInputStream(inputStream);
		this.out = out;
		inb = new BufferedReader(new InputStreamReader(inputStream));

	}
	
	 int begin() {

		// init Scanner
//		Scanner sc = new Scanner(System.in);

		// init Bank
		Bank theBank = new Bank("ABC Bank");
		int i = 0;
		// add a user, which also creates a Savings account
//		User aUser = theBank.addUser("John", "Doe", "1234");

		// add a checking account for our user
//		Account newAccount = new Account("Checking", aUser, theBank);
	//	aUser.addAccount(newAccount);
		//theBank.addAccount(newAccount);

		
		String cardNumber;

		// continue looping forever
		while (true) {

			// stay in login prompt until successful login
			cardNumber = mainMenuPrompt(theBank);

			// stay in main menu until user quits
			i = printUserMenu(cardNumber);
			if(i == -1) 
				break;

		}
		return i;	

	}

	/**
	 * Print the ATM's login menu.
	 * @param theBank	the Bank object whose accounts to use
	 */
	public String mainMenuPrompt(Bank theBank) {

	
		
		// inits
		String userID;
		String pin;
		String authUser = null;
try{
		// prompt user for user ID/pin combo until a correct one is reached
		do {

			out.writeUTF("\nWelcome to "+ theBank.getName()+"\n\n");
			
			out.writeUTF("Enter user ID: ");
			
	//		userID = sc.nextLine();
			userID = ind.readUTF();
			System.out.println(userID);
			
			out.writeUTF("Enter pin: ");
	//		pin = sc.nextLine();
			pin = ind.readUTF();
			
			// try to get user object corresponding to ID and pin combo
			authUser = theBank.userLogin(userID, pin);
			if (authUser == "none") {
				out.writeUTF("Incorrect user ID/pin combination. " + 
						"Please try again");
			}

		} while(authUser == "none"); 	// continue looping until we have a  
									// successful login
}catch(Exception e){}
		return authUser;

	}

	/**
	 * Print the ATM's menu for user actions.
	 * @param cardNumber	the logged-in User 
	 */
	public int printUserMenu(String cardNumber) {

		// print a summary of the user's accounts
//		theUser.printAccountsSummary();

		int choice;
		
		try{
		// user menu
		do {

			out.writeUTF("What would you like to do?");
			out.writeUTF("  1) Check Balance");
			out.writeUTF("  2) Withdraw");
			out.writeUTF("  3) Deposit");
			out.writeUTF("  4) Transfer");
			out.writeUTF("  5) Quit");
			out.writeUTF("\n");
			out.writeUTF("Enter choice: ");
		//	choice = sc.nextInt();
			choice = ind.readInt();

			if (choice < 1 || choice > 5) {
				out.writeUTF("Invalid choice. Please choose 1-5.");
			}

		} while (choice < 1 || choice > 5);

		// process the choice
		switch (choice) {

		case 1:
			showBal(cardNumber);
			break;
		case 2:
			withdrawFunds(cardNumber);
			break;
		case 3:
			depositCheque(cardNumber);
			break;
		case 4:
			transferFunds(cardNumber);
			break;
		case 5:
			
			// gobble up rest of previous input
			inb.readLine();
			return -1;
		}

		// redisplay this menu unless the user wants to quit
		if (choice != 5) {
			printUserMenu(cardNumber);
		}
}catch(Exception e){}
return 0;
	}

	private void showBal(String cardNumber) {
		// TODO Auto-generated method stub
		int sav_bal=0,cur_bal=0;
		Connection connect = null;
		Statement statement = null;
//cardNumber="12334";

		try {
	
			Class.forName("com.mysql.jdbc.Driver");
			connect = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/atm?"+"user=%s&password=%s",user,pwd); //this will connect to the database
			statement = (Statement) connect.createStatement();
			ResultSet rs = statement.executeQuery("select savings_bal,current_bal from db where card="+cardNumber+";");
			rs.next();
			
			sav_bal=rs.getInt("savings_bal");
			cur_bal=rs.getInt("current_bal");
			out.writeUTF("Savings balance="+sav_bal+"	Current Balance="+cur_bal);
			connect.close();
			statement.close();
		} catch(Exception e){
	
			try {
				out.writeUTF("Please enter correct details");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}		
	}

	/**
	 * Process transferring funds from one account to another.
	 * @param CardNumber	the logged-in User 
	 */
	public void transferFunds(String cardNumber) {

		int fromAcct;
		int amount;
		int sav_bal=0,cur_bal=0;

		try{
		// get account to transfer from
		
			do {
				out.writeUTF("Enter the number (1-2) of the account to " + 
					"transfer from: ");
			  //fromAcct = sc.nextInt();
				fromAcct = ind.readInt();
			
				if (fromAcct <= 0 || fromAcct >= 3) {
				
					out.writeUTF("Invalid account. Please try again.");
				}
			} while (fromAcct <= 0 || fromAcct >= 3);

		// get amount to transfer
		Connection connect = null;
		Statement statement = null;

		//cardNumber="12334";
		PreparedStatement preparedStatement = null;
		try {
	
			Class.forName("com.mysql.jdbc.Driver");
			connect = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/atm?"+"user=%s&password=%s",user,pwd); //this will connect to the database
			statement = (Statement) connect.createStatement();
			ResultSet rs = statement.executeQuery("select savings_bal,current_bal from db where card="+cardNumber+";");
			rs.next();
			sav_bal=rs.getInt("savings_bal");
			cur_bal=rs.getInt("current_bal");
    
		}catch(Exception e){
	
			out.writeUTF("Database connection failed");
		}

		if(fromAcct==1){
		// get amount to transfer
		
			do {
			
				out.writeUTF("Enter the amount to transfer (max $"+sav_bal+"): $");
		  //	amount = sc.nextInt();
				amount = ind.readInt();
			
				if (amount < 0) {
				
					out.writeUTF("Amount must be greater than zero.");
				} else if (amount > sav_bal) {
				
					out.writeUTF("Amount must not be greater than balance " +
						"of $"+sav_bal+"\n");
				}
			} while (amount < 0 || amount > sav_bal);
		
			try {
				
				preparedStatement = (PreparedStatement) connect.prepareStatement("update db set savings_bal="+(sav_bal-amount)+" , current_bal="+(cur_bal+amount)+" where card = "+cardNumber+" ;");
				preparedStatement.execute();
				connect.close();
				statement.close();
			
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			}	
		}

		if(fromAcct==2){
			// get amount to transfer
			do {
				out.writeUTF("Enter the amount to transfer (max $"+cur_bal+"): $");
			//	amount = sc.nextInt();
				amount = ind.readInt();
		
				if (amount < 0) {
			
					out.writeUTF("Amount must be greater than zero.");
		
				} else if (amount > cur_bal) {
			
					out.writeUTF("Amount must not be greater than balance " +
					"of $"+cur_bal+"\n");
		
				}
			} while (amount < 0 || amount > cur_bal);
	
			try {
		
				preparedStatement = (PreparedStatement) connect.prepareStatement("update db set current_bal="+(cur_bal-amount)+" ,savings_bal="+(sav_bal+amount)+" where card="+cardNumber+";");
				preparedStatement.execute();
				connect.close();
				statement.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}catch(Exception e){}

	}

	public void withdrawFunds(String cardNumber) {

		int fromAcct;
		int amount;
		int sav_bal = 0;
		int cur_bal=0;;

		try{
		// get account to withdraw from
			do {
				out.writeUTF("Enter the number 1 for savings and 2 for current account to " + 
					"withdraw from: ");
			//	fromAcct = sc.nextInt();
				fromAcct = ind.readInt();
				if (fromAcct <= 0 || fromAcct > 2) {
					
					out.writeUTF("Invalid account. Please try again.");
				}
			} while (fromAcct <= 0 || fromAcct >= 3);
		
			Connection connect = null;
			Statement statement = null;
			
			//cardNumber="12334";
			PreparedStatement preparedStatement = null;
			try {
				
				Class.forName("com.mysql.jdbc.Driver");
				connect = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/atm?"+"user=%s&password=%s",user,pwd); //this will connect to the database
				statement = (Statement) connect.createStatement();
				ResultSet rs = statement.executeQuery("select savings_bal,current_bal from db where card="+cardNumber+";");
				rs.next();
				sav_bal=rs.getInt("savings_bal");
				cur_bal=rs.getInt("current_bal");
				
			}catch(Exception e){
				out.writeUTF("Failed to connect with database");
			}
			
			if(fromAcct==1){
				// get amount to transfer
				do {
					out.writeUTF("Enter the amount to withdraw (max $"+sav_bal+"): $");
		//			amount = sc.nextInt();
					amount = ind.readInt();
					if (amount < 0) {
				
						out.writeUTF("Amount must be greater than zero.");
					} else if (amount > sav_bal) {
				
						out.writeUTF("Amount must not be greater than balance " +
						"of $"+sav_bal+"\n");
					}
				} while (amount < 0 || amount > sav_bal);
		
				try {
					preparedStatement = (PreparedStatement) connect.prepareStatement("update db set savings_bal="+(sav_bal-amount)+" where card="+cardNumber+";");
					preparedStatement.execute();
					connect.close();
					statement.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
				
			}

			if(fromAcct==2){
	
				// get amount to transfer
				do {
					out.writeUTF("Enter the amount to withdraw (max $"+cur_bal+"): $");
				//	amount = sc.nextInt();
					amount = ind.readInt();
					if (amount < 0) {
			
						out.writeUTF("Amount must be greater than zero.");
					} else if (amount > cur_bal) {
			
						out.writeUTF("Amount must not be greater than balance " +
					"of $"+cur_bal+"\n");
					}
	
				} while (amount < 0 || amount > cur_bal);
	
				try {
					
					preparedStatement = (PreparedStatement) connect.prepareStatement("update db set current_bal="+(cur_bal-amount)+" where card="+cardNumber+";");
					preparedStatement.execute();
					connect.close();
					statement.close();
	
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
			}
		}catch(Exception e){}
		// gobble up rest of previous input
		
		try {
			
			inb.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	/**
	 * Process a cheque deposit to an account.
	 * @param cardNumber	the logged-in User 
	 */
	public void depositCheque(String cardNumber) {

		int toAcct = 0;
		int amount = 0;
		int sav_bal=0,cur_bal=0;
		
		Connection connect = null;
		Statement statement = null;
		
		//cardNumber="12334";
		PreparedStatement preparedStatement = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/atm?"+"user=%s&password=%s",user,pwd); //this will connect to the database
			statement = (Statement) connect.createStatement();
		    ResultSet rs = statement.executeQuery("select savings_bal,current_bal from db where card="+cardNumber+";");
		    rs.next();
		    sav_bal=rs.getInt("savings_bal");
		    cur_bal=rs.getInt("current_bal");
		    }catch(Exception e){
			try {
				
				out.writeUTF("Failed to connect with database");
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
		}
		// get account to withdraw from
	
		    try{
		
		    	do {
			
		    		out.writeUTF("Enter the number (1-2) of the account to " + 
					"deposit to: ");
		
		 //			toAcct = sc.nextInt();
		    		toAcct = ind.readInt();
			
		    		if (toAcct <= 0 || toAcct >2) {
				
		    			out.writeUTF("Invalid account. Please try again.");
		    		}
		    	} while (toAcct <= 0 || toAcct >2);

		    	// get amount to transfer
		    	do {
			
		    		out.writeUTF("Enter the amount to deposit: $");
		    	  //amount = sc.nextInt();
		    		amount = ind.readInt();
			
		    		if (amount < 0) {
				
		    			out.writeUTF("Amount must be greater than zero.");
		    		} 
		    	} while (amount < 0);
		    
		    }catch(Exception e){}
	
		    if(toAcct==1){
		    	try {
		    		
		    		preparedStatement = (PreparedStatement) connect.prepareStatement("update db set savings_bal="+(sav_bal+amount)+" where card="+cardNumber+";");
		    		preparedStatement.execute();
		    		connect.close();
		    		statement.close();
		    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	else {		
		try {
			preparedStatement = (PreparedStatement) connect.prepareStatement("update db set current_bal="+(cur_bal+amount)+" where card="+cardNumber+";");
			preparedStatement.execute();
			connect.close();
			statement.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// gobble up rest of previous input
		try {
			inb.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}