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

/**
 * MainActivity类是应用程序的主活动，显示用户的记录列表，并提供添加新记录和查看统计信息的功能。
 */
public class MainActivity extends AppCompatActivity {
    // 视图组件
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabStatistics;
    // 数据库帮助类，用于访问数据库
    private DatabaseHelper dbHelper;
    // 记录适配器和记录列表
    private RecordAdapter adapter;
    private List<Record> recordList;
    // 当前用户ID
    private int currentUserId;

    /**
     * 在Activity创建时调用，初始化视图和数据
     *
     * @param savedInstanceState 保存的实例状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化数据库帮助类
        dbHelper = new DatabaseHelper(this);

        // 获取传递过来的当前用户ID
        currentUserId = getIntent().getIntExtra("currentUserId", -1);

        // 初始化列表组件
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(v -> {
            // 启动添加记录活动
            Intent intent = new Intent(MainActivity.this, AddRecordActivity.class);
            intent.putExtra("currentUserId", currentUserId);
            startActivity(intent);
        });

        fabStatistics = findViewById(R.id.fab_statistics);
        fabStatistics.setOnClickListener(v -> {
            // 启动统计信息活动
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            intent.putExtra("currentUserId", currentUserId);
            startActivity(intent);
        });

        // 加载记录
        loadRecords();
    }

    /**
     * 从数据库加载记录并显示在RecyclerView中
     */
    private void loadRecords() {
        // 获取当前用户的记录列表
        recordList = dbHelper.getRecordsByUserId(currentUserId);
        adapter = new RecordAdapter(recordList);

        // 设置记录点击事件监听器
        adapter.setOnItemClickListener(position -> {
            // 点击事件
            Record record = recordList.get(position);
            Toast.makeText(MainActivity.this, "长按可以进行修改或删除", Toast.LENGTH_SHORT).show();
        });

        // 设置记录长按事件监听器
        adapter.setOnItemLongClickListener(position -> {
            // 长按事件
            Record record = recordList.get(position);
            // 显示修改或删除选项
            showModifyDeleteDialog(record, position);
        });

        // 将适配器设置到RecyclerView
        recyclerView.setAdapter(adapter);
    }

    /**
     * 显示对话框以选择修改或删除记录
     *
     * @param record   要修改或删除的记录
     * @param position 记录在列表中的位置
     */
    private void showModifyDeleteDialog(Record record, int position) {
        new AlertDialog.Builder(this)
                .setTitle("选择要进行的操作")
                .setItems(new String[]{"修改", "删除"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // 修改记录
                            Intent intent = new Intent(MainActivity.this, ModifyRecordActivity.class);
                            intent.putExtra("recordId", record.getId());
                            startActivity(intent);
                            break;
                        case 1:
                            // 删除记录
                            dbHelper.deleteRecord(record.getId());
                            recordList.remove(position);
                            adapter.notifyItemRemoved(position);
                            break;
                    }
                })
                .show();
    }

    /**
     * 当Activity恢复时调用，重新加载记录
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadRecords();
    }
}

