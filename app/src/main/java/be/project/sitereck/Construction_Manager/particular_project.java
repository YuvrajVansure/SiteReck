package be.project.sitereck.Construction_Manager;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import be.project.sitereck.R;


public class particular_project extends AppCompatActivity implements View.OnClickListener,AsyncResult{
    TextView title, start_date, end_date, completed_work, ongoing_work, blocked_work;
    Button btn_location;
    private String JSON_URL="site";
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particular_project);

        title = (TextView) findViewById(R.id.text_title);
        start_date = (TextView) findViewById(R.id.text_startdate);
        end_date = (TextView) findViewById(R.id.tv_enddate);
        btn_location = (Button) findViewById(R.id.btn_location);
        completed_work = (TextView) findViewById(R.id.complete_text);
        ongoing_work = (TextView) findViewById(R.id.ongoing_text);
        blocked_work = (TextView) findViewById(R.id.blocked_text);
        btn_location.setOnClickListener((View.OnClickListener) this);

//        ProjectDataClass projectDataClass = SetSharedPrefrences.getInstance(this).getprojectinfo();
//
//        title.setText(String.valueOf(projectDataClass.getTitle()));
//        start_date.setText(String.valueOf(projectDataClass.getStartDate()));
//        end_date.setText(String.valueOf(projectDataClass.getEndDate()));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_location:
                Intent intent = new Intent(particular_project.this, profile_cm.class);
                startActivity(intent);
        }
    }

    @Override
    public void AsyncResultListener(int responseCode, String result) {

        int TOTAL_ONGOING = 0;
        int TOTAL_BLOCKED = 0;
        int TOTAL_COMPLETED = 0;


        completed_work.setText(String.valueOf(TOTAL_COMPLETED));
        ongoing_work.setText(String.valueOf(TOTAL_ONGOING));
        blocked_work.setText(String.valueOf(TOTAL_BLOCKED));

    }
}
