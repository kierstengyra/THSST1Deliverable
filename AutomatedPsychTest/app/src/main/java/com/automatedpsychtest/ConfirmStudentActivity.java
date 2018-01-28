package com.automatedpsychtest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ConfirmStudentActivity extends AppCompatActivity {

    TextView txtConfirmStudent;
    Button btnConfirm;
    Button btnBack;

    String studentName;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isrecordavailable);

        Intent intent = getIntent();
        this.studentName = intent.getStringExtra("Name");
        String placeholder = getString(R.string.txtConfirmStudent)+this.studentName;

        this.txtConfirmStudent = (TextView) findViewById(R.id.txtHasRecord);
        this.txtConfirmStudent.setText(placeholder);

        this.btnConfirm = (Button) findViewById(R.id.btnYes);
        this.btnConfirm.setText(R.string.btnConfirm);

        this.btnBack = (Button) findViewById(R.id.btnNo);
        this.btnBack.setText(R.string.btnBack);
    }

    public void clickedYes(View view) {
        Intent intent = new Intent(this, ChecklistActivity.class);
        intent.putExtra("Name", this.studentName);
        startActivity(intent);
    }

    public void clickedNo(View view) {
        this.finish();
    }

}
