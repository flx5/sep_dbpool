package main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.flx5.sep.dbpool.DbConnection;
import com.flx5.sep.dbpool.DbPool;

public class test {

	private static final String dbDriver = "org.postgresql.Driver";
	private static final String connectionString = "jdbc:postgresql://localhost/test";
	
	public static void main(String[] args) throws SQLException, IOException {
		try {
            System.out.println("Loading JDBC Driver");
            Class.forName(dbDriver);
        }
        catch(ClassNotFoundException e) {
            System.err.println("JDBC Driver not found");
            return;
        }
        
		try (DbPool pool = new DbPool(5, 10, test::create)) {
			try(DbConnection dbCon = pool.getConnection()) {
				ResultSet res = dbCon.prepareStatement("SELECT 'OK';").executeQuery();
				res.next();
				System.out.println("DB: " + res.getString(1));
				
				
			}
			
			try(DbConnection dbCon2 = pool.getConnection()) {
				ResultSet res2 = dbCon2.prepareStatement("SELECT COUNT(*) FROM pg_catalog.pg_stat_activity WHERE usename = 'test';").executeQuery();
				res2.next();
				System.out.println("Active Connections for user: " + res2.getLong(1));
			}
		}
		
		System.out.println("OK");
	}
	
	private static Connection create() throws SQLException {
		Properties props = new Properties();
        props.setProperty("user", "test");
        props.setProperty("password", "changeme");
        props.setProperty("ssl", "true");   // necessary!

		return DriverManager.getConnection(connectionString, props);
	}

}
