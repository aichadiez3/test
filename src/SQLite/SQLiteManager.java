package SQLite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import pojos.MedicalRecord;
import pojos.Symptom;
import pojos.User;
import interfaces.Interface;

public class SQLiteManager implements Interface {
	private Connection sqlite_connection;
	
	public SQLiteManager() {
		super();
	}

	
	public boolean Connect() {
		// TODO Auto-generated method stub
		try {
			Class.forName("org.sqlite.JDBC");
			this.sqlite_connection = DriverManager.getConnection("jdbc:sqlite:./db/Clinicaltrials.db");//hay que poner nuestra database
			sqlite_connection.createStatement().execute("PRAGMA foreign_keys=ON");
			return true;
			// create Managers

		} catch (ClassNotFoundException | SQLException connection_error) {
			connection_error.printStackTrace();
			return false;
		}
	}

	protected Connection getConnection() {
		return sqlite_connection;
	}

	

	public boolean CreateTables() {
		
		try {
			Statement stmt0 = sqlite_connection.createStatement();
			String sql0 = "CREATE TABLE user " + "(user_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ " user_name TEXT NOT NULL UNIQUE, " + " password TEXT NOT NULL, "+" email TEXT NOT NULL UNIQUE)";
			stmt0.execute(sql0);
			stmt0.close();
			
			Statement stmt1 = sqlite_connection.createStatement();
			String sql1 = "CREATE TABLE patient " + "(patient_id INTEGER PRIMARY KEY AUTOINCREMENT, " + " name TEXT NOT NULL, "
					+ " surname TEXT NOT NULL, " + " birthdate DATETIME NOT NULL, " + " age INTEGER, " + " telephone INTEGER default NULL, "
					+ " height INTEGER default NULL, " + " weight INTEGER default NULL, "   + " doctor_id REFERENCES doctor(doctor_id), "
					+ " insurance_id REFERENCES insurance(insurance_id), " 
					+ " user_id FOREING KEY REFERENCES user(user_id) ON DELETE CASCADE)";
			stmt1.execute(sql1);
			stmt1.close();
			
			
			Statement stmt2 = sqlite_connection.createStatement();
			String sql2 = "CREATE TABLE symptom " + "(symptom_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ " name TEXT NOT NULL, " + " weight INTEGER default 0, "
					+ " record_id FOREING KEY REFERENCES medical_record(medicalRecord_id))";
			stmt2.execute(sql2);
			stmt2.close();
			
			Statement stmt3 = sqlite_connection.createStatement();
			String sql3 = "CREATE TABLE medical_record " + "(medicalRecord_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ " reference_number INTEGER UNIQUE, " + " record_date DATETIME NOT NULL, "
					+ " FOREING KEY (patient_id) REFERENCES patient (patient_id) ON UPDATE RESTRICT ON DELETE CASCADE)";
			stmt3.execute(sql3);
			stmt3.close();
			
			Statement stmt4 = sqlite_connection.createStatement();
			String sql4 = "CREATE TABLE bitalino_test " + "(test_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ " type TEXT NOT NULL, " + " test_date DATETIME NOT NULL, "
					+ " record_id FOREING KEY REFERENCES medical_record(medicalRecord_id))";
			stmt4.execute(sql4);
			stmt4.close();
			
			
			
			
			// ManyToMany relation tables go here
			Statement stmt8 = sqlite_connection.createStatement();
			String sql8 = "CREATE TABLE patient_doctor " + "(patient_id INTEGER REFERENCES patient(patient_id), "
					+ " doctor_id REFERENCES doctor(doctor_id),"
					+ " PRIMARY KEY (patient_id, doctor_id))";
			stmt8.execute(sql8);
			stmt8.close();
			
			
			return true;
		}catch (SQLException tables_error) {
			if (tables_error.getMessage().contains("already exists")) {
			} else {
				tables_error.printStackTrace();
				return false;
			}
			
			return false;
		}
	}
	
	
	
	
	
	// -----> INSERT METHODS <-----
	
	// -----> UPDATE METHODS <-----
	
	// -----> SEARCH STORED METHODS <-----
	public MedicalRecord Search_stored_record(User user) {
		try {
			String SQL_code = "SELECT * FROM medical_record WHERE medicalRecord_id LIKE ?";
			PreparedStatement template = this.sqlite_connection.prepareStatement(SQL_code);
			template.setInt(1, user.getUserId());
			ResultSet result_set = template.executeQuery();
			MedicalRecord record = new MedicalRecord();
			record.setMedicalRecord_id(result_set.getInt("medicalRecord_id"));
			record.setReferenceNumber(result_set.getInt("reference_number"));
			record.setRecordDate(result_set.getDate("record_date"));
			//record.setSymptoms_list(result_set.getArray(0));
			
			template.close();
			return record;
		} catch (SQLException search_record_error) {
			search_record_error.printStackTrace();
			return null;
		}
	}
	
	
	
	// -----> SEARCH BY ID METHODS <-----
	public MedicalRecord Search_record_by_id(Integer record_id) {
		try {
			String SQL_code = "SELECT * FROM medical_record WHERE medicalRecord_id LIKE ?";
			PreparedStatement template = this.sqlite_connection.prepareStatement(SQL_code);
			template.setInt(1, record_id);
			MedicalRecord record=new MedicalRecord();
			ResultSet result_set = template.executeQuery();
			record.setReferenceNumber(result_set.getInt("reference_number"));
			record.setRecordDate(result_set.getDate("record_date"));
			List<Symptom> symptoms_list = Search_all_symptoms_from_record(record_id);
			//record.setSymptoms_list(symptoms_list);
			template.close();
			return record;
		} catch (SQLException search_record_error) {
			search_record_error.printStackTrace();
		}
		return null;
	}
	
	
	
	// -----> LIST METHODS <-----
	public List<Symptom> Search_all_symptoms_from_record(Integer record_id) {
		try {
			String SQL_code = "SELECT symptom_id FROM transaction_biomaterial WHERE transaction_id LIKE ?";
			PreparedStatement template = this.sqlite_connection.prepareStatement(SQL_code);
			template.setInt(1, record_id);
			ResultSet result_set = template.executeQuery();
			List<Symptom> symptom_list = new LinkedList<Symptom>();
			while (result_set.next()) {
				//Symptom symptom = Search_symptom_by_id(result_set.getInt("symptom_id"));
				//symptom_list.add(symptom);
			}
			template.close();
			return symptom_list;
		} catch (SQLException list_symptom_record_error) {
			list_symptom_record_error.printStackTrace();
			return null;
		}
	}
	
	
	
	public List<User> List_all_users() {
		try {
			Statement statement = this.sqlite_connection.createStatement();
			String SQL_code = "SELECT * FROM user";
			List<User> users_list = new LinkedList<User>();
			ResultSet result_set = statement.executeQuery(SQL_code);
			while (result_set.next()) {
				User user = new User();
				user.setUserId(result_set.getInt("user_id"));
				user.setUserName(result_set.getString("user_name"));
				user.setPassword(result_set.getString("password"));
				user.setEmail(result_set.getString("email"));
				users_list.add(user);
			}
			statement.close();
			return users_list;
		} catch (SQLException list_users_error) {
			list_users_error.printStackTrace();
			return null;
		}
	}
	
	
	
	// -----> DELETE METHODS <-----
	
	
	
	
	
	public boolean Close_connection() {
		// TODO Auto-generated method stub
		try {
			this.sqlite_connection.close();
			return true;
		} catch (SQLException close_connection_error) {
			close_connection_error.printStackTrace();
			return false;
		}
	}

}