package com.example.accountingbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 登录活动类，处理用户登录逻辑。
 */
public class LoginActivity extends AppCompatActivity {
    // 用户名和密码输入框
    private EditText etUsername, etPassword;
    // 登录和注册按钮
    private Button btnLogin, registerButton;
    // 数据库帮助类实例，用于访问数据库
    private DatabaseHelper dbHelper;

    /**
     * 创建活动时调用的方法。
     * @param savedInstanceState 如果活动被重新创建，则包含之前保存的状态。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置使用的布局文件
        setContentView(R.layout.activity_login);

        // 初始化数据库帮助类
        dbHelper = new DatabaseHelper(this);

        // 通过ID找到布局中的组件
        etUsername = findViewById(R.id.usernameEditText);
        etPassword = findViewById(R.id.passwordEditText);
        btnLogin = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // 获取从注册页面传递过来的用户名和密码
        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");
        if (username != null && password != null) {
            etUsername.setText(username);
            etPassword.setText(password);
        }

        // 设置登录按钮的点击事件监听器
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取输入的用户名和密码
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                // 非空校验
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "请填写所有信息", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 验证用户并获取用户ID
                int userId = dbHelper.validateUser(username, password);
                if (userId != -1) {
                    // 获取用户角色
                    String role = getUserRole(username);
                    if ("admin".equals(role)) {
                        // 如果是管理员，跳转到管理员界面
                        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    } else {
                        // 如果是普通用户，跳转到主界面
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        Toast.makeText(LoginActivity.this,  username + "欢迎登录", Toast.LENGTH_SHORT).show();
                        intent.putExtra("currentUserId", userId);
                        startActivity(intent);
                    }
                } else {
                    // 登录失败，显示提示信息
                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 设置注册按钮的点击事件监听器
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到注册界面
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 根据用户名获取用户角色。
     * @param username 用户名
     * @return 用户角色
     */
    @SuppressLint("Range")
    private String getUserRole(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 查询数据库获取用户角色
        Cursor cursor = db.rawQuery("SELECT role FROM users WHERE username = ?", new String[]{username});
        String role = null;
        if (cursor.moveToFirst()) {
            role = cursor.getString(cursor.getColumnIndex("role"));
        }
        cursor.close();
        return role;
    }
}