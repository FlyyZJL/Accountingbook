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

public class AdminActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ListView usersListView;
    private Button addButton;
    private EditText usernameEditText, passwordEditText, roleEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dbHelper = new DatabaseHelper(this);

        usersListView = findViewById(R.id.usersListView);
        addButton = findViewById(R.id.addButton);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        roleEditText = findViewById(R.id.roleEditText);

        loadUsers();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String role = roleEditText.getText().toString();

                if (addUser(username, password, role)) {
                    Toast.makeText(AdminActivity.this, "用户添加成功", Toast.LENGTH_SHORT).show();
                    loadUsers();
                } else {
                    Toast.makeText(AdminActivity.this, "用户添加失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        usersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showEditDeleteDialog((int) id);
                return true;
            }
        });
    }

    private void loadUsers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id AS _id, username, role FROM users", null);
        String[] from = {"username", "role"};
        int[] to = {R.id.usernameTextView, R.id.roleTextView};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.user_item, cursor, from, to, 0);
        usersListView.setAdapter(adapter);
    }

    private boolean addUser(String username, String password, String role) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("role", role);
        long result = db.insert("users", null, values);
        return result != -1;
    }

    private boolean updateUser(int userId, String username, String password, String role) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("role", role);
        int result = db.update("users", values, "id = ?", new String[]{String.valueOf(userId)});
        return result > 0;
    }

    private boolean deleteUser(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("users", "id = ?", new String[]{String.valueOf(userId)});
        return result > 0;
    }

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

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("users", null, "id = ?", new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor.moveToFirst()) {
            editUsernameEditText.setText(cursor.getString(cursor.getColumnIndex("username")));
            editPasswordEditText.setText(cursor.getString(cursor.getColumnIndex("password")));
            editRoleEditText.setText(cursor.getString(cursor.getColumnIndex("role")));
        }
        cursor.close();

        AlertDialog dialog = builder.create();

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
