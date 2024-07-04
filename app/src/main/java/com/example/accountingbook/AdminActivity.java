package com.example.accountingbook;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * AdminActivity 类是一个管理员界面，用于管理用户。
 * 该界面允许管理员查看所有用户，添加新用户，并更新或删除现有用户。
 *
 */
public class AdminActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ListView usersListView;
    private Button addButton;
    private EditText usernameEditText, passwordEditText, roleEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // 初始化数据库助手类
        dbHelper = new DatabaseHelper(this);

        // 初始化UI组件
        usersListView = findViewById(R.id.usersListView);
        addButton = findViewById(R.id.addButton);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        roleEditText = findViewById(R.id.roleEditText);

        // 加载用户列表
        loadUsers();

        // 添加按钮点击事件
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String role = roleEditText.getText().toString();

                // 添加用户并更新列表
                if (addUser(username, password, role)) {
                    Toast.makeText(AdminActivity.this, "用户添加成功", Toast.LENGTH_SHORT).show();
                    loadUsers();
                } else {
                    Toast.makeText(AdminActivity.this, "用户添加失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 设置用户列表项长按事件
        usersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showEditDeleteDialog((int) id);
                return true;
            }
        });
    }

    /**
     * 加载所有用户并显示在 ListView 中
     */
    private void loadUsers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id AS _id, username, role FROM users", null);
        String[] from = {"username", "role"};
        int[] to = {R.id.usernameTextView, R.id.roleTextView};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.user_item, cursor, from, to, 0);
        usersListView.setAdapter(adapter);
    }

    /**
     * 添加新用户到数据库
     *
     * @param username 用户名
     * @param password 密码
     * @param role     角色
     * @return 添加成功返回 true，否则返回 false
     */
    private boolean addUser(String username, String password, String role) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("role", role);
        long result = db.insert("users", null, values);
        return result != -1;
    }

    /**
     * 更新现有用户的信息
     *
     * @param userId   用户ID
     * @param username 新用户名
     * @param password 新密码
     * @param role     新角色
     * @return 更新成功返回 true，否则返回 false
     */
    private boolean updateUser(int userId, String username, String password, String role) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("role", role);
        int result = db.update("users", values, "id = ?", new String[]{String.valueOf(userId)});
        return result > 0;
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 删除成功返回 true，否则返回 false
     */
    private boolean deleteUser(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("users", "id = ?", new String[]{String.valueOf(userId)});
        return result > 0;
    }

    /**
     * 显示编辑/删除用户的对话框
     *
     * @param userId 用户ID
     */
    @SuppressLint("Range")
    private void showEditDeleteDialog(final int userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_user, null);
        builder.setView(dialogView);

        final EditText editUsernameEditText = dialogView.findViewById(R.id.editUsernameEditText);
        final EditText editPasswordEditText = dialogView.findViewById(R.id.editPasswordEditText);
        final EditText editRoleEditText = dialogView.findViewById(R.id.editRoleEditText);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button deleteButton = dialogView.findViewById(R.id.deleteButton);

        // 获取用户信息并显示在对话框中
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("users", null, "id = ?", new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor.moveToFirst()) {
            editUsernameEditText.setText(cursor.getString(cursor.getColumnIndex("username")));
            editPasswordEditText.setText(cursor.getString(cursor.getColumnIndex("password")));
            editRoleEditText.setText(cursor.getString(cursor.getColumnIndex("role")));
        }
        cursor.close();

        AlertDialog dialog = builder.create();

        // 保存按钮点击事件
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = editUsernameEditText.getText().toString();
                String newPassword = editPasswordEditText.getText().toString();
                String newRole = editRoleEditText.getText().toString();

                if (updateUser(userId, newUsername, newPassword, newRole)) {
                    Toast.makeText(AdminActivity.this, "信息更新成功", Toast.LENGTH_SHORT).show();
                    loadUsers();
                    dialog.dismiss();
                } else {
                    Toast.makeText(AdminActivity.this, "信息更新失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 删除按钮点击事件
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteUser(userId)) {
                    Toast.makeText(AdminActivity.this, "用户删除成功", Toast.LENGTH_SHORT).show();
                    loadUsers();
                    dialog.dismiss();
                } else {
                    Toast.makeText(AdminActivity.this, "用户删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }
}
