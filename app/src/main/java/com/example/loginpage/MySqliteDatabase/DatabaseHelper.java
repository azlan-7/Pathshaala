package com.example.loginpage.MySqliteDatabase;

import android.os.StrictMode;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {
    private static final String IP = "199.79.62.22"; // Your SQL Server IP
    private static final String DB_NAME = "pathshaala";
    private static final String USERNAME = "pathshaala";
    private static final String PASSWORD = "Darshi@2025#";
    private static final String PORT = "1433"; // Default SQL Server Port

    public static Connection getConnection() {
        Connection connection = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            String connectionURL = "jdbc:jtds:sqlserver://" + IP + ":" + PORT + ";databaseName=" + DB_NAME + ";user=" + USERNAME + ";password=" + PASSWORD + ";";

            connection = DriverManager.getConnection(connectionURL);
            Log.d("DatabaseHelper", "Connected to SQL Server successfully!");

        } catch (SQLException se) {
            Log.e("DatabaseHelper", "SQL Exception: " + se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("DatabaseHelper", "JDBC Driver Not Found: " + e.getMessage());
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Unknown Error: " + e.getMessage());
        }
        return connection;
    }
}
