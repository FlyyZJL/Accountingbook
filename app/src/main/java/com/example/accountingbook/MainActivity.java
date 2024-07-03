package com.example.accountingbook;



import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabStatistics;
    private DatabaseHelper dbHelper;
    private RecordAdapter adapter;
    private List<Record> recordList;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        // 假设 currentUserId 从登录活动中传递过来
        currentUserId = getIntent().getIntExtra("currentUserId", -1);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddRecordActivity.class);
            intent.putExtra("currentUserId", currentUserId);
            startActivity(intent);
        });

        fabStatistics = findViewById(R.id.fab_statistics);
        fabStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            intent.putExtra("currentUserId", currentUserId);
            startActivity(intent);
        });

        loadRecords();
    }

    private void loadRecords() {
        recordList = dbHelper.getRecordsByUserId(currentUserId);
        adapter = new RecordAdapter(recordList);

        adapter.setOnItemClickListener(position -> {
            // 点击事件
            Record record = recordList.get(position);
            Toast.makeText(MainActivity.this, "长按可以进行修改或删除", Toast.LENGTH_SHORT).show();
        });

        adapter.setOnItemLongClickListener(position -> {
            // 长按事件
            Record record = recordList.get(position);
            // 显示修改或删除选项
            showModifyDeleteDialog(record, position);
        });

        recyclerView.setAdapter(adapter);
    }

    private void showModifyDeleteDialog(Record record, int position) {
        // 显示对话框以选择修改或删除
        new AlertDialog.Builder(this)
                .setTitle("选择要进行的操作")
                .setItems(new String[]{"修改", "删除"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // 修改
                            Intent intent = new Intent(MainActivity.this, ModifyRecordActivity.class);
                            intent.putExtra("recordId", record.getId());
                            startActivity(intent);
                            break;
                        case 1:
                            // 删除
                            dbHelper.deleteRecord(record.getId());
                            recordList.remove(position);
                            adapter.notifyItemRemoved(position);
                            break;
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecords();
    }
}


