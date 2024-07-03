package com.example.accountingbook;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin,registerButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        etUsername = findViewById(R.id.usernameEditText);
        etPassword = findViewById(R.id.passwordEditText);
        btnLogin = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        // 如果有，获取注册页面传递过来的用户名和密码
        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");
        if (username != null && password != null) {
            etUsername.setText(username);
            etPassword.setText(password);
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                int userId = dbHelper.validateUser(username, password);
                if (userId != -1) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("currentUserId", userId);
                    startActivity(intent);
                    finish();
                } else {
                    // 处理登录失败
                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
