package com.example.loginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText as_id;
    private EditText as_pw;
    private EditText as_add;
    private Button as_ok;
    private EditText as_call;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign);
        as_id = findViewById(R.id.as_id);
        as_pw = findViewById(R.id.as_pw);
        as_add = findViewById(R.id.as_add);
        as_ok = findViewById(R.id.as_ok);
        as_call = findViewById(R.id.as_call);

        //회원가입 버튼 클릭시 수행
        as_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = as_id.getText().toString();
                String userPassword = as_pw.getText().toString();
                String userName = as_add.getText().toString();
                String userCall = as_call.getText().toString();

                Response.Listener<String> reponseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                Toast.makeText(getApplicationContext(), "회원 등록에 성공하였습니다", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, FirstActivity.class);
                                intent.putExtra("userName",userName);
                                //Intent intent4 = new Intent(AssignActivity.this, SubActivity.class);
                                intent.putExtra("userCall",userCall);
                                startActivity(intent);
                                //startActivity(intent4);

                            } else {

                                Toast.makeText(getApplicationContext(), "회원 등록에 실패하였습니다", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userName, userCall,reponseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });
    }
}