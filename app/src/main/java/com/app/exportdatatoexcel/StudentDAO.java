package com.app.exportdatatoexcel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class StudentDAO extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StudentDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table and columns for student details
    public static final String TABLE_STUDENT = "students";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DEP = "department";
    public static final String COLUMN_ROLE_NO = "role_no";

    // SQL command to create the student table
    private static final String CREATE_TABLE_STUDENT = "CREATE TABLE " + TABLE_STUDENT + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_DEP + " TEXT, "
            + COLUMN_ROLE_NO + " TEXT);";

    public StudentDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(CREATE_TABLE_STUDENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade logic
    }

    // Open the database for writing
    public void open() {
        SQLiteDatabase db = this.getWritableDatabase();
    }

    // Close the database
    public void close() {
        if (getWritableDatabase() != null && getWritableDatabase().isOpen()) {
            getWritableDatabase().close();
        }
    }

    // Insert a new student record
    public long insertStudent(String name, String dep, String roleNo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DEP, dep);
        values.put(COLUMN_ROLE_NO, roleNo);

        return db.insert(TABLE_STUDENT, null, values);
    }

    // Retrieve all students from the database
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENT, null, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                int depIndex = cursor.getColumnIndex(COLUMN_DEP);
                int roleNoIndex = cursor.getColumnIndex(COLUMN_ROLE_NO);

                if (idIndex != -1 && nameIndex != -1 && depIndex != -1 && roleNoIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    String dep = cursor.getString(depIndex);
                    String roleNo = cursor.getString(roleNoIndex);

                    students.add(new Student(id, name, dep, roleNo));
                } else {
                    // Handle the case where a column is not found
                    // You can log an error or take appropriate action
                }
            }

            cursor.close();
        }

        return students;
    }

    public int getStudentsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_STUDENT, null);
        int count = 0;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }

            cursor.close();
        }

        return count;
    }

}
