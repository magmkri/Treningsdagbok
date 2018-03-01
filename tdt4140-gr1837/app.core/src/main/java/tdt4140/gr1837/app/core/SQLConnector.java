package tdt4140.gr1837.app.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLConnector {
	private static String url = "jdbc:mysql://mysql.stud.ntnu.no/didris_test1";
	private static String username = "didris_test";
	private static String password = "1234";
	private static Connection connection;
	public static Connection getConnection() throws SQLException{
		if(connection==null){
			try {
				System.out.println("Connecting database...");
				connection = DriverManager.getConnection(url, username, password);
				System.out.println("Successfully connected to the database");
			}
			catch(SQLException e) {
				System.out.println("Could not connect to database");
				throw e;
			}
		}
		return connection;
	}
	public static void closeConnection() {
		if(connection!=null) {
			try {
				connection.close();
				System.out.println("Connection closed");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String args[]) throws SQLException {
		for(int i =1; i < 100; i++) {
			String[] user = getUser(i);
			System.out.println(String.format("%s, %s %s %s %s",i, user[0],user[1],user[2],user[3]));
		}
		//insert_client(connection, "\"Per Pålsen\"", 41010101, 20, "\"msakmdmkm\"");
	}
	public static String[] getUser(int id) throws SQLException {
		Connection connection = SQLConnector.getConnection();
		String query = "SELECT * FROM Klient WHERE K_ID="+id;
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()){
			String[] arr= {rs.getString("navn"), rs.getString("tlf"), rs.getString("alder"), rs.getString("motivasjon")};
			return arr;
		}
		return null;
	}
	
	public static ResultSet getResultSet(String query) throws SQLException {
		Connection connection;
		try {
			connection = SQLConnector.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw e1;
		}
		Statement stmt;
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			return rs;
		} catch(SQLException e) {
			throw e;
		}
	}
	public static List<User> getUsers()
	{
		try {
			ResultSet rs = getResultSet("SELECT * FROM Klient");
			List<User> users=new ArrayList<>();
			while(rs.next()){
				User user= new User(rs.getString("navn"), 
									rs.getString("tlf"), 
									rs.getInt("alder"), 
									rs.getString("motivasjon"), 
									rs.getInt("K_ID")
				);
				users.add(user);
			}
			return users;
		} catch (SQLException e1) {
			e1.printStackTrace();
			return UserDatabase.getOfflineUserDatabase();
		}
	}
	
	public static List<Session> getSessions(int id)
	{
		try {
			List<Session> sessions=new ArrayList<>();
			ResultSet rs =getResultSet("SELECT * FROM Økt WHERE K_ID="+id);
			while(rs.next()){
				Session session= new Session(rs.getString("Notat"), 
									rs.getString("Dato"), 
									rs.getInt("O_ID")
				);
				sessions.add(session);
			}
			return sessions;
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return new ArrayList<Session>();
		}
	}
	
	
	public static List<String> getMusclesTrained(int sessionID){
		try {
			
			List<String> musclesTrained = new ArrayList<>();
			ResultSet rs1 = getResultSet("SELECT * FROM Klient_styrke WHERE O_ID="+sessionID);
			while(rs1.next()){
					ResultSet rs2 = getResultSet("SELECT * FROM Muskel_trent WHERE Ø_ID="+ rs1.getInt("Ø_ID"));	
					while(rs2.next()){
						ResultSet rs3 = getResultSet("SELECT * FROM Muskel WHERE M_ID="+ rs2.getInt("M_ID"));	
						while(rs3.next()){
							musclesTrained.add(rs3.getString("M_navn"));
						}
				}

			}
			return musclesTrained;
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return new ArrayList<String>();
		}
	}
}
