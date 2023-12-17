package com.app.exportdatatoexcel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;

public class MainActivity extends AppCompatActivity {
    EditText editTextName, editTextDep, editTextRoleNo;
    Button btnSubmit;
    private StudentDAO studentDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = findViewById(R.id.editTextName);
        editTextDep = findViewById(R.id.editTextDep);
        editTextRoleNo = findViewById(R.id.editTextRoleNo);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save data to SQLite database and move to the student details screen

                String name = editTextName.getText().toString();
                String dep = editTextDep.getText().toString();
                String roleNo = editTextRoleNo.getText().toString();

                studentDAO = new StudentDAO(MainActivity.this);
                studentDAO.open();
                long result = studentDAO.insertStudent(name, dep, roleNo);

                if (result != -1) {
                    Intent intent = new Intent(MainActivity.this, StudentDetailsActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Handle insertion failure
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        studentDAO.close();
    }
}


