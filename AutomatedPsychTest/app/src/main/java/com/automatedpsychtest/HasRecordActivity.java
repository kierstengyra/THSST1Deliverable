package com.automatedpsychtest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by gyra on 10/30/2017.
 */
public class HasRecordActivity extends AppCompatActivity {

    ImageButton goBack;
    ListView recordList;

    ArrayList<String> stringArray = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasrecord);

        this.goBack = (ImageButton) findViewById(R.id.goBack);

        //TODO: Retrieve names from DB, 0th element first
        this.recordList = (ListView) findViewById(R.id.recordList);

        this.stringArray.add("Ebe Dancel");
        this.stringArray.add("Clara Benin");
        this.stringArray.add("Therese Lansangan");
        this.stringArray.add("Bea Patricia Valenzuela");
        this.stringArray.add("Jensen Gomez");
        this.stringArray.add("Josh Villanueva");
        this.stringArray.add("Ebe Dancel");
        this.stringArray.add("Clara Benin");
        this.stringArray.add("Therese Lansangan");
        this.stringArray.add("Bea Patricia Valenzuela");
        this.stringArray.add("Jensen Gomez");
        this.stringArray.add("Josh Villanueva");
        this.stringArray.add("Ebe Dancel");
        this.stringArray.add("Clara Benin");
        this.stringArray.add("Therese Lansangan");
        this.stringArray.add("Bea Patricia Valenzuela");
        this.stringArray.add("Jensen Gomez");
        this.stringArray.add("Josh Villanueva");
        this.stringArray.add("Ebe Dancel");
        this.stringArray.add("Clara Benin");
        this.stringArray.add("Therese Lansangan");
        this.stringArray.add("Bea Patricia Valenzuela");
        this.stringArray.add("Jensen Gomez");
        this.stringArray.add("Josh Villanueva");

        this.recordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HasRecordActivity.this, ConfirmStudentActivity.class);
                intent.putExtra("Name", stringArray.get(position)); //TODO: Replace with comment below once DB is done
//                intent.putExtra("ID", position);
                startActivity(intent);
            }
        });

        this.adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stringArray);
        this.recordList.setAdapter(this.adapter);
    }

    public void prev(View view) {
        this.finish();
    }
}
