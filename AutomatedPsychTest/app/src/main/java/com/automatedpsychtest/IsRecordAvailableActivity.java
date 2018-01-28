package com.automatedpsychtest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class IsRecordAvailableActivity extends AppCompatActivity {

    TextView txtHasRecord;
    Button btnYes;
    Button btnNo;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isrecordavailable);

        this.txtHasRecord = (TextView) findViewById(R.id.txtHasRecord);
        this.btnYes = (Button) findViewById(R.id.btnYes);
        this.btnNo = (Button) findViewById(R.id.btnNo);
    }

    public void clickedYes(View view) {
        Intent intent = new Intent(this, HasRecordActivity.class);
        startActivity(intent);
    }

    public void clickedNo(View view) {
        Intent intent = new Intent(this, HasNoRecordActivity.class);
        startActivity(intent);
    }
}
