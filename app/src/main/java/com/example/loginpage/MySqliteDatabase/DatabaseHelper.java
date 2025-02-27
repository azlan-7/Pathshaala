package com.example.loginpage.MySqliteDatabase;

import static net.sourceforge.jtds.jdbc.DefaultProperties.DATABASE_NAME;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper {
    private static final String IP = "199.79.62.22"; // Your SQL Server IP
    private static final String DB_NAME = "pathshaala";
    private static final String USERNAME = "pathshaala";
    private static final String PASSWORD = "Darshi@2025#";
    private static final String PORT = "1433"; // Default SQL Server Port

    public interface QueryResultListener {
        void onQueryResult(List<Map<String, String>> result);
    }

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

    /**
     * Existing method for loading data into AutoCompleteTextView (unchanged)
     */
    public static void loadDataFromDatabase(Context context, String query, AutoCompleteTextView autoCompleteTextView, TextView textView) {
        new AsyncTask<Void, Void, Map<String, String>>() {
            @Override
            protected Map<String, String> doInBackground(Void... voids) {
                Map<String, String> dataMap = new HashMap<>();
                try {
                    Log.d("DatabaseHelper", "Attempting to establish a connection...");
                    Connection connection = getConnection();
                    if (connection != null) {
                        Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        while (rs.next()) {
                            String id = rs.getString(1); // First column: ID
                            String name = rs.getString(2); // Second column: Name
                            dataMap.put(name, id);
                        }
                        rs.close();
                        stmt.close();
                        connection.close();
                    }
                } catch (Exception e) {
                    Log.e("DatabaseHelper", "Error fetching data: " + e.getMessage());
                }
                return dataMap;
            }

            @Override
            protected void onPostExecute(Map<String, String> data) {
                if (!data.isEmpty()) {
                    List<String> names = new ArrayList<>(data.keySet());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, names);
                    autoCompleteTextView.setAdapter(adapter);
                    autoCompleteTextView.setOnClickListener(v -> autoCompleteTextView.showDropDown());

                    autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                        String selectedName = (String) parent.getItemAtPosition(position);
                        textView.setText(data.get(selectedName)); // Show corresponding ID
                    });

                    Log.d("DatabaseHelper", "Data loaded successfully!");
                } else {
                    Log.e("DatabaseHelper", "No data fetched!");
                }
            }
        }.execute();
    }

    /**
     * New method for fetching data dynamically with a listener.
     */
    public static void loadDataFromDatabase(Context context, String query, QueryResultListener listener) {
        new AsyncTask<Void, Void, List<Map<String, String>>>() {
            @Override
            protected List<Map<String, String>> doInBackground(Void... voids) {
                List<Map<String, String>> resultList = new ArrayList<>();
                try {
                    Log.d("DatabaseHelper", "Attempting to fetch data...");
                    Connection connection = getConnection();
                    if (connection != null) {
                        Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        while (rs.next()) {
                            Map<String, String> row = new HashMap<>();
                            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                                row.put(rs.getMetaData().getColumnName(i), rs.getString(i));
                            }
                            resultList.add(row);
                        }
                        rs.close();
                        stmt.close();
                        connection.close();
                    }
                } catch (Exception e) {
                    Log.e("DatabaseHelper", "Error fetching data: " + e.getMessage());
                }
                return resultList;
            }

            @Override
            protected void onPostExecute(List<Map<String, String>> result) {
                listener.onQueryResult(result);
            }
        }.execute();
    }
}


























//package com.example.loginpage.MySqliteDatabase;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.os.StrictMode;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.TextView;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class DatabaseHelper {
//    private static final String IP = "199.79.62.22"; // Your SQL Server IP
//    private static final String DB_NAME = "pathshaala";
//    private static final String USERNAME = "pathshaala";
//    private static final String PASSWORD = "Darshi@2025#";
//    private static final String PORT = "1433"; // Default SQL Server Port
//
//    public static Connection getConnection() {
//        Connection connection = null;
//        try {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//
//            Class.forName("net.sourceforge.jtds.jdbc.Driver");
//
//            String connectionURL = "jdbc:jtds:sqlserver://" + IP + ":" + PORT + ";databaseName=" + DB_NAME + ";user=" + USERNAME + ";password=" + PASSWORD + ";";
//
//            connection = DriverManager.getConnection(connectionURL);
//            Log.d("DatabaseHelper", "Connected to SQL Server successfully!");
//
//        } catch (SQLException se) {
//            Log.e("DatabaseHelper", "SQL Exception: " + se.getMessage());
//        } catch (ClassNotFoundException e) {
//            Log.e("DatabaseHelper", "JDBC Driver Not Found: " + e.getMessage());
//        } catch (Exception e) {
//            Log.e("DatabaseHelper", "Unknown Error: " + e.getMessage());
//        }
//        return connection;
//    }
//
//    public static void loadDataFromDatabase(Context context, String query, AutoCompleteTextView autoCompleteTextView, TextView textView) {
//        new AsyncTask<Void, Void, Map<String, String>>() {
//            @Override
//            protected Map<String, String> doInBackground(Void... voids) {
//                Map<String, String> dataMap = new HashMap<>();
//                try {
//                    Log.d("DatabaseHelper", "Attempting to establish a connection...");
//                    Connection connection = getConnection();
//                    if (connection != null) {
//                        Statement stmt = connection.createStatement();
//                        ResultSet rs = stmt.executeQuery(query);
//
//                        while (rs.next()) {
//                            String id = rs.getString(1); // First column: ID
//                            String name = rs.getString(2); // Second column: Name
//                            dataMap.put(name, id);
//                        }
//                        rs.close();
//                        stmt.close();
//                        connection.close();
//                    }
//                } catch (Exception e) {
//                    Log.e("DatabaseHelper", "Error fetching data: " + e.getMessage());
//                }
//                return dataMap;
//            }
//
//            @Override
//            protected void onPostExecute(Map<String, String> data) {
//                if (!data.isEmpty()) {
//                    List<String> names = new ArrayList<>(data.keySet());
//                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, names);
//                    autoCompleteTextView.setAdapter(adapter);
//                    autoCompleteTextView.setOnClickListener(v -> autoCompleteTextView.showDropDown());
//
//                    autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
//                        String selectedName = (String) parent.getItemAtPosition(position);
//                        textView.setText(data.get(selectedName)); // Show corresponding ID
//                    });
//
//                    Log.d("DatabaseHelper", "Data loaded successfully!");
//                } else {
//                    Log.e("DatabaseHelper", "No data fetched!");
//                }
//            }
//        }.execute();
//    }
//
////    public static void loadDataFromDatabase(Context context, String query, AutoCompleteTextView autoCompleteTextView) {
////        new AsyncTask<Void, Void, List<String>>() {
////            @Override
////            protected List<String> doInBackground(Void... voids) {
////                List<String> dataList = new ArrayList<>();
////                try {
////                    Log.d("DatabaseHelper", "Attempting to establish a connection...");
////                    Connection_Class connectionClass = new Connection_Class();
////                    Connection connection = connectionClass.CONN();
////                    if (connection != null) {
////                        Statement stmt = connection.createStatement();
////                        ResultSet rs = stmt.executeQuery(query);
////
////                        while (rs.next()) {
////                            dataList.add(rs.getString(1)); // Assuming first column contains required data
////                        }
////                        rs.close();
////                        stmt.close();
////                        connection.close();
////                    }
////                } catch (Exception e) {
////                    Log.e("DatabaseHelper", "Error fetching data: " + e.getMessage());
////                }
////                return dataList;
////            }
////
////            @Override
////            protected void onPostExecute(List<String> data) {
////                if (!data.isEmpty()) {
////                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, data);
////                    autoCompleteTextView.setAdapter(adapter);
////                    autoCompleteTextView.setOnClickListener(v -> autoCompleteTextView.showDropDown());
////                    Log.d("DatabaseHelper", "Data loaded successfully!");
////                } else {
////                    Log.e("DatabaseHelper", "No data fetched!");
////                }
////            }
////        }.execute();
////    }
//
//}