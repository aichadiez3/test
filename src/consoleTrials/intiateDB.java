package consoleTrials;

import java.sql.SQLException;

import SQLite.SQLiteManager;
import SQLite.SQLiteMethods;
import pojos.User;

public class intiateDB {
	
	public static void main(String[] args) throws SQLException {
		
		SQLiteManager manager = new SQLiteManager();
		boolean everything_ok = manager.Connect();
		//boolean tables_ok = manager.CreateTables();
		//System.out.println(manager.getSqlite_connection().getWarnings());
		
		System.out.println(everything_ok + " ");
		
		
		SQLiteMethods methods = manager.getMethods();
		User user = methods.Insert_new_user("name", "123", "aa@aa");
		
		System.out.println(methods.List_all_users());
		
		manager.Close_connection();
		
		
	}
}