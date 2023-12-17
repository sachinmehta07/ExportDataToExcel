package com.app.exportdatatoexcel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import android.Manifest;




import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class StudentDetailsActivity extends AppCompatActivity {
    TableLayout tableLayout;
    Button btnAddStudent, btnGenerateExcel;
    private StudentDAO studentDAO;

    private static final int PICK_DIRECTORY_REQUEST_CODE = 124;


    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);
        tableLayout = findViewById(R.id.tableLayout);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnGenerateExcel = findViewById(R.id.btnGenerateExcel);

        studentDAO = new StudentDAO(this);
        studentDAO.open();

        displayStudents();


        // Check if there are more than 3 students to show/hide "Generate Excel" button
        if (studentDAO.getStudentsCount() > 3) {
            btnGenerateExcel.setVisibility(View.VISIBLE);
        } else {
            btnGenerateExcel.setVisibility(View.GONE);
        }

        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to the registration form for the next student
                Intent intent = new Intent(StudentDetailsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnGenerateExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                generateExcel();
            }
        });
    }

    private void displayStudents() {
        List<Student> students = studentDAO.getAllStudents();

        for (Student student : students) {
            TableRow row = new TableRow(this);

            TextView tvName = new TextView(this);
            tvName.setText(student.getName());

            TextView tvDep = new TextView(this);
            tvDep.setText(student.getDep());

            TextView tvRoleNo = new TextView(this);
            tvRoleNo.setText(student.getRoleNo());

            row.addView(tvName);
            row.addView(tvDep);
            row.addView(tvRoleNo);

            tableLayout.addView(row);
        }
    }

    private void generateExcel() {
        // Check if permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            writeCSV();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

//    private void writeCSV() {
//        List<Student> students = studentDAO.getAllStudents();
//
//        // Create a CSV file
//        //   File csvFile = new File(Environment.getExternalStorageDirectory(), "StudentDetails.csv");
//
//        File csvFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "StudentDetails.csv");
//
//        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
//
//            // Dynamically fetch header based on database columns
//            String[] header = getDatabaseColumns();
//            writer.writeNext(header);
//
//            // Write data
//            for (Student student : students) {
//                // Dynamically fetch data based on database columns
//                String[] data = {student.getName(), student.getDep(), student.getRoleNo()};
//                writer.writeNext(data);
//            }
//
//            Toast.makeText(this, "CSV file generated successfully", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Error generating CSV file", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Open document tree picker for user to select a directory
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, PICK_DIRECTORY_REQUEST_CODE);
            } else {
                Toast.makeText(this, "Storage permission required to generate CSV. Please choose a " + "location to save the file.", Toast.LENGTH_SHORT).show();

                // Open document tree picker for user to select a directory
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, PICK_DIRECTORY_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_DIRECTORY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri directoryUri = data.getData(); // Now 'data' is accessible
                writeCSVToSelectedDirectory(directoryUri);
            } else {
                Toast.makeText(this, "No directory selected. CSV generation cancelled.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    // Function to dynamically fetch database columns
    private String[] getDatabaseColumns() {
        SQLiteDatabase db = studentDAO.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_STUDENT, null, null, null, null, null, null);

        String[] columns = cursor.getColumnNames();

        cursor.close();
        return columns;
    }

//    private void writeCSVToSelectedDirectory(Uri directoryUri) {
//        // Get the actual file path from the chosen directory URI
//        DocumentFile documentTree = DocumentFile.fromTreeUri(this, directoryUri);
//        DocumentFile documentFile = documentTree.findFile("StudentDetails.csv");
//
//        if (documentFile != null) {
//            String directoryPath = documentFile.getUri().toString(); // Use getUri() to obtain the URI
//            File csvFile = new File(directoryPath, "StudentDetails.csv");
//
//            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
//
//                // Dynamically fetch header based on database columns
//                String[] header = getDatabaseColumns();
//                writer.writeNext(header);
//
//                // Write data
//                for (Student student : studentDAO.getAllStudents()) {
//                    // Dynamically fetch data based on database columns
//                    String[] data = {student.getName(), student.getDep(), student.getRoleNo()};
//                    writer.writeNext(data);
//                }
//
//                Toast.makeText(this, "CSV file saved successfully to " + directoryPath, Toast.LENGTH_SHORT).show();
//            } catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(this, "Error generating CSV file", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(this, "Error finding StudentDetails.csv in the chosen directory", Toast.LENGTH_SHORT).show();
//        }
//    }


    private void writeCSVToSelectedDirectory(Uri directoryUri) {
        DocumentFile documentTree = DocumentFile.fromTreeUri(this, directoryUri);

        // Log the directory URI for debugging
        Log.d("DirectoryURI", "Directory URI: " + directoryUri.toString());

        // Check if "StudentDetails.csv" already exists in the chosen directory
        DocumentFile documentFile = documentTree.findFile("StudentDetails.csv");

        if (documentFile == null) {
            // If the file doesn't exist, create it
            documentFile = documentTree.createFile("text/csv", "StudentDetails.csv");

            if (documentFile == null) {
                Toast.makeText(this, "Error creating StudentDetails.csv in the chosen directory", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Get the actual CSV file from the documentFile
        File csvFile = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "StudentDetails.csv");

        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
            // Dynamically fetch header based on database columns
            String[] header = getDatabaseColumns();
            writer.writeNext(header);

            // Write data
            List<Student> students = studentDAO.getAllStudents();
            for (Student student : students) {
                // Dynamically fetch data based on database columns
                String[] data = {student.getName(), student.getDep(), student.getRoleNo()};
                writer.writeNext(data);
            }

            // Convert CSV to Excel using Apache POI
            convertCsvToExcel(csvFile, documentTree, "StudentDetails.xlsx");

            Toast.makeText(this, "Excel file saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating CSV file", Toast.LENGTH_SHORT).show();
        }
    }

    private void convertCsvToExcel(File csvFile, DocumentFile documentTree, String excelFileName) {
        try {
            // Create a new Excel workbook and sheet
            WritableWorkbook workbook = Workbook.createWorkbook(new File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), excelFileName));
            WritableSheet sheet = workbook.createSheet("Sheet1", 0);

            // Read CSV data and write it to the Excel sheet
            try (CSVReader csvReader = new CSVReader(new FileReader(csvFile))) {
                String[] nextLine;
                int rowNum = 0;
                while ((nextLine = csvReader.readNext()) != null) {
                    for (int i = 0; i < nextLine.length; i++) {
                        // Add cell data to the sheet
                        sheet.addCell(new Label(i, rowNum, nextLine[i]));
                    }
                    rowNum++;
                }
            } catch (CsvValidationException | WriteException e) {
                throw new RuntimeException(e);
            }

            // Write the Excel workbook to the chosen directory
            workbook.write();
            workbook.close();
        } catch (IOException | WriteException e) {
            e.printStackTrace();
        }
    }
}