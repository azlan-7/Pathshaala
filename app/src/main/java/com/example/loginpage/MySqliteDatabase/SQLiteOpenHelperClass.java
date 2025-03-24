package com.example.loginpage.MySqliteDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteOpenHelperClass extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyDatabase.db"; // Change database name if needed
    private static final int DATABASE_VERSION = 1; // Database version

    public SQLiteOpenHelperClass(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Use DatabaseHelper.getConnection() if it provides a valid SQL connection
        String createTableQuery = "CREATE TABLE IF NOT EXISTS Users (ID INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT, Age INTEGER);";
        db.execSQL(createTableQuery);
        Log.d("SQLiteOpenHelper", "Table Created: " + createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Users");
        onCreate(db);
    }

    public SQLiteDatabase getDatabaseInstance() {
        return this.getWritableDatabase();
    }
}
