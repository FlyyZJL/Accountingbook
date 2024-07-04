package com.example.accountingbook;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 注册活动类，处理用户注册逻辑。
 */
public class RegisterActivity extends AppCompatActivity {
    // 用户名、密码和角色输入框
    private EditText usernameEditText, passwordEditText, roleEditText;
    // 注册按钮
    private Button registerButton;
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
        setContentView(R.layout.activity_register);

        // 初始化数据库帮助类
        dbHelper = new DatabaseHelper(this);

        // 通过ID找到布局中的组件
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        roleEditText = findViewById(R.id.roleEditText);
        registerButton = findViewById(R.id.registerButton);

        // 设置注册按钮的点击事件监听器
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取输入的用户名、密码和角色
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String role = roleEditText.getText().toString();
                // 调用注册用户的方法
                if (registerUser(username, password, role)) {
                    // 注册成功，显示提示信息并跳转到登录界面
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    startActivity(intent);
                } else {
                    // 注册失败，显示提示信息
                    Toast.makeText(RegisterActivity.this, "请填写所有信息", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 注册用户的方法。
     * @param username 用户名
     * @param password 密码
     * @param role 用户角色
     * @return 注册是否成功
     */
    private boolean registerUser(String username, String password, String role) {
        // 获取数据库的写入权限
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 创建ContentValues对象存储键值对
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("role", role);
        // 插入数据到数据库
        long result = db.insert("users", null, values);
        // 根据插入结果返回是否成功
        return result != -1;
    }
}