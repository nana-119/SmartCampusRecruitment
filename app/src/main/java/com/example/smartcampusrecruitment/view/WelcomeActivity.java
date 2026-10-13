package com.example.smartcampusrecruitment.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcampusrecruitment.R;

public class WelcomeActivity extends AppCompatActivity {

    TextView text_qiuzhi,text_zhaopin;
    ImageView img_qiuzhi,img_zhaopin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        initView();
        Onclicke();
    }
    private void initView(){
        text_qiuzhi=findViewById(R.id.text_qiuzhi);
        text_zhaopin=findViewById(R.id.text_zhaopin);
        img_qiuzhi=findViewById(R.id.image_qiuzhi);
        img_zhaopin=findViewById(R.id.image_zhaopin);
    }
    private void Onclicke(){
        img_qiuzhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WelcomeActivity.this, LoginActivity.class);
                intent.putExtra("role",1);
                startActivity(intent);
                finish();
            }
        });
        img_zhaopin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WelcomeActivity.this, LoginActivity.class);
                intent.putExtra("role",2);
                startActivity(intent);
                finish();
            }
        });
        text_qiuzhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WelcomeActivity.this, LoginActivity.class);
                intent.putExtra("role",1);
                startActivity(intent);
                finish();
            }
        });
        text_zhaopin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WelcomeActivity.this, LoginActivity.class);
                intent.putExtra("role",2);
                startActivity(intent);
                finish();
            }
        });
    }
}