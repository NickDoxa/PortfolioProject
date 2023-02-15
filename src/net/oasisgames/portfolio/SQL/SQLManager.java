package net.oasisgames.portfolio.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class SQLManager {
	
	/*
	 * MySQL API created by Nick Doxa for free use by developers every where!
	 * Current Spigot/Bukkit version: 1.19
	 */
	
	private final JavaPlugin plugin;
	private final String tableName;
	private final Map<Integer, Map<String, String>> creationKeyValues;
	private final Map<Integer, String> keyValues;
	private final String primaryKey;
	private final boolean printStatements;
	
    /*Variable Definitions: 
     * main = JavaPlugin class
     * name = table name within databse, 
     * primaryKeyValue = the primary key for the table A.K.A. what piece of data you will use to access the rest,
     * showStatements = boolean determining whether to print write/read information to console
     * createValueArray() = A method for defining an array containing a key name followed by a SQL data type,
     * Example:	String[] columns = new String[]{"EXAMPLE1 VARCHAR(100)", "EXAMPLE2 INT(100)", "EXAMPLE3 BOOL"}
     * SQL DATA TYPES: https://www.w3schools.com/sql/sql_datatypes.asp
     * The outcome of this array goes directly into your SQL Table and Database through this class.
     */
	
	protected abstract String[] createKeyValueArray();
	
	public SQLManager(JavaPlugin main, String name, String primaryKeyValue, boolean showStatements) {
		plugin = main;
		tableName = name;
		creationKeyValues = createKeyValueMap();
		primaryKey = primaryKeyValue;
		printStatements = showStatements;
		keyValues = new HashMap<Integer, String>();
		for (int i=0;i<creationKeyValues.size();i++) {
			for (String s : creationKeyValues.get(i).keySet()) {
				keyValues.put(i, s);
			}
		}
		try {
			connect();
		} catch (SQLException e) {
			printToConsole("The database did not connect!");
		}
		if (isConnected()) {
			this.createTable();
		}
	}

	//Connection protocol
    private String host, port, database, username, password;
    private static Connection connection;

    public void connect() throws SQLException {
    	printToConsole("Connecting to plugin: " + plugin.getName());
	    host = plugin.getConfig().getString("MySQL.host");
	    port = plugin.getConfig().getString("MySQL.port");
	    database = plugin.getConfig().getString("MySQL.database");
	    username = plugin.getConfig().getString("MySQL.username");
	    password = plugin.getConfig().getString("MySQL.password");
	    connection = DriverManager.getConnection("jdbc:mysql://" +
	    	     host + ":" + port + "/" + database + "?useSSL=false",
	    	     username, password);
	    printToConsole("Connected successfully to MySQL Database!");
    }
    
    //Disconnect method
    public void disconnect() {
    	if (isConnected()) {
    		try {
    			connection.close();
    			printToConsole("Connected successfully to MySQL Database!");
    			printToConsole("Disconnected successfully from MySQL Database!");
    		} catch(SQLException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    public static Connection getConnection() {
    	return connection;
    }
    
    public boolean isConnected() {
    	return (connection == null ? false : true);
    }
    
    /*
     * LOCAL METHODS
     */
    
    private boolean printToConsole(String msg) {
    	if (printStatements) {
    		System.out.println("[MySQL API] " + msg);
    	}
    	return printStatements;
    }
    
    private Map<Integer, Map<String, String>> createKeyValueMap() {
    	Map<Integer, Map<String, String>> numericKeys = new HashMap<Integer, Map<String, String>>();
    	Map<String, String> keys = new HashMap<String, String>();
    	int count = 0;
    	for (String s : this.createKeyValueArray()) {
    		String[] keyArray = s.split(" ", 2);
    		keys.put(keyArray[0], keyArray[1]);
    		numericKeys.put((count+1), keys);
    		keys.clear();
    		count++;
    	}
    	printToConsole("Key Value Map created with a total of: " + count + " keys.");
    	return numericKeys;
    }
    
    /*
     * CREATION METHODS
     */
    
    //Creates a data table with the specified key values if one does not already exist
    private void createTable() {
    	PreparedStatement ps;
    	String statement = "CREATE TABLE IF NOT EXISTS " + tableName + " (";
    	for (int i=0;i<creationKeyValues.size();i++) {
    		for (String s : creationKeyValues.get(i).keySet()) {
    			statement = statement + s + " " + creationKeyValues.get(i).get(s) + ",";
    		}
    	}
    	statement = statement + "PRIMARY KEY (" + primaryKey + "))";
    	try {
    		ps = getConnection().prepareStatement(statement);
    		ps.executeUpdate();
    	} catch (SQLException | NullPointerException e) {
    		printToConsole("Table: " + tableName + ". Table either already exists, or an error was thrown! (Most likely not an issue)");
    	}
    }
    
    //Create value key set without defined arguments
    public void createNewKeyValue(String key) {
		try {
			if (!keyExists(key)) {
				String statement = "INSERT IGNORE INTO " + tableName + " (" + primaryKey;
				int q = 1;
				for (int i=1;i<keyValues.size();i++) {
					statement = statement + keyValues.get(i) + ",";
					q++;
				}
				statement = statement.substring(0, statement.length()) + ") VALUES (";
				for (int i=0;i<q;i++) {
					statement = statement + "?,";
				}
				statement = statement.substring(0, statement.length()) + ")";
				PreparedStatement ps = getConnection().prepareStatement(statement);
				ps.setString(1, key);
				for (int i=2;i<q;i++) {
					ps.setObject(i, null);
				}
				ps.executeUpdate();
				return;
			}
		} catch (SQLException | NullPointerException e) {
			return;
		}
	}
    
    //Create value key set with defined arguemnts
    public void createNewKeyValue(String key, String[] args) {
		try {
			if (!keyExists(key)) {
				String statement = "INSERT IGNORE INTO " + tableName + " (" + primaryKey;
				int q = 1;
				for (int i=1;i<keyValues.size();i++) {
					statement = statement + keyValues.get(i) + ",";
					q++;
				}
				statement = statement.substring(0, statement.length()) + ") VALUES (";
				for (int i=0;i<q;i++) {
					statement = statement + "?,";
				}
				statement = statement.substring(0, statement.length()) + ")";
				PreparedStatement ps = getConnection().prepareStatement(statement);
				ps.setObject(1, key);
				for (int i=2;i<q;i++) {
					ps.setObject(i, args[i-1]);
				}
				ps.setObject(q, args[q]);
				ps.executeUpdate();
				return;
			}
		} catch (SQLException | NullPointerException e) {
			return;
		}
	}
    
    //Update a key values information
    public void setValue(String key, Object value, String primary) {
		try {
			PreparedStatement ps = getConnection().prepareStatement("UPDATE " + tableName + " SET " + key + "=? WHERE " + primaryKey + "=?");
			ps.setObject(1, value);
			ps.setObject(2, primary);
			ps.executeUpdate();
		} catch (SQLException e) {
			printToConsole("Error executing value update method!");
		}
    }
    
	//Checking if a key value is within the primaryKey
	public boolean keyExists(String key) {
		try {
			PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM " + tableName + " WHERE " + primaryKey + "=?");
			ps.setString(1, key);
			ResultSet results = ps.executeQuery();
			if (results.next()) {
				//Key found
				return true;
			}
			//Key not found
			return false;
		} catch (SQLException | NullPointerException e) {
			return false;
		}
	}
	
	//Returns a ResultSet from grabbing whatever value is saved under the specified valueKey based on the primaryKey
	public ResultSet getValueByKey(String key, String valueKey) {
		try {
			PreparedStatement ps = getConnection().prepareStatement("SELECT " + valueKey + " FROM " + tableName + " WHERE " + primaryKey + "=?");
			ps.setString(1, key);
			ResultSet rs = ps.executeQuery();
			return rs;
		} catch (SQLException | NullPointerException e) {
			return null;
		}
	}
	
	/*
	 * DELETION METHODS
	 */
	
	public void emptyTable() {
		try {
			PreparedStatement ps = getConnection().prepareStatement("TRUNCATE " + tableName);
			ps.executeUpdate();
		} catch (SQLException e) {
			printToConsole("Error truncating table!");
		}
	}
	
	public void removeKey(String key) {
		try {
			PreparedStatement ps = getConnection().prepareStatement("DELETE FROM " + tableName + " WHERE " + primaryKey + "=?");
			ps.setString(1, key);
			ps.executeUpdate();
		} catch (SQLException e) {
			printToConsole("Error removing key set from table!");
		}
	}
}