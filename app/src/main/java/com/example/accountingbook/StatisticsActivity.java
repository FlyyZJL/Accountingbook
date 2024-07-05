package com.example.accountingbook;



import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * StatisticsActivity类用于显示用户支出的统计数据，包括各类别支出的饼图和总支出。
 * 用户可以通过选择日期和范围来查看特定时间段内的支出情况。
 */
public class StatisticsActivity extends AppCompatActivity {
    private PieChart pieChart;
    private DatabaseHelper dbHelper;
    private Button selectDateButton, selectRangeButton;
    private String selectedDate;
    private String selectedRange = "日"; // 默认选择“日”范围

    /**
     * 在Activity创建时调用，初始化视图和数据
     *
     * @param savedInstanceState 保存的实例状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // 初始化数据库帮助类
        dbHelper = new DatabaseHelper(this);
        // 获取饼图视图
        pieChart = findViewById(R.id.pieChart);
        // 设置没有数据时的提示文本
        pieChart.setNoDataText("请先选择日期和范围");
        pieChart.setNoDataTextColor(Color.BLACK);
        Paint paint = pieChart.getPaint(Chart.PAINT_INFO);
        paint.setTextSize(60f);

        // 获取选择日期和范围的按钮
        selectDateButton = findViewById(R.id.selectDateButton);
        selectRangeButton = findViewById(R.id.selectRangeButton);

        // 设置选择日期按钮的点击事件
        selectDateButton.setOnClickListener(v -> showDatePickerDialog());

        // 设置选择范围按钮的点击事件
        selectRangeButton.setOnClickListener(v -> showRangeDialog());
    }

    /**
     * 显示日期选择对话框，用户选择日期后加载统计数据
     */
    private void showDatePickerDialog() {
        // 获取当前日期
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // 创建日期选择对话框
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
            loadStatistics();
            updateTotalExpense();
        }, year, month, day);

        datePickerDialog.show();
    }

    /**
     * 显示范围选择对话框，用户选择范围后加载统计数据
     */
    private void showRangeDialog() {
        final String[] items = {"日", "月", "年"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择范围");
        builder.setItems(items, (dialog, which) -> {
            selectedRange = items[which];
            loadStatistics();
            updateTotalExpense();
        });
        builder.show();
    }

    /**
     * 从数据库加载统计数据，并显示在饼图中
     */
    private void loadStatistics() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String userId = String.valueOf(getIntent().getIntExtra("currentUserId", -1));
        Cursor cursor = null;
        String query = "";
        String[] queryArgs = null;

        // 根据用户选择的日期和范围生成查询语句
        if (selectedDate != null) {
            switch (selectedRange) {
                case "日":
                    query = "SELECT category, SUM(amount) AS total FROM records WHERE user_id = ? AND date = ? GROUP BY category";
                    queryArgs = new String[]{userId, selectedDate};
                    break;
                case "月":
                    String monthPrefix = selectedDate.substring(0, 7); // yyyy-MM
                    query = "SELECT category, SUM(amount) AS total FROM records WHERE user_id = ? AND date LIKE ? GROUP BY category";
                    queryArgs = new String[]{userId, monthPrefix + "%"};
                    break;
                case "年":
                    String yearPrefix = selectedDate.substring(0, 4); // yyyy
                    query = "SELECT category, SUM(amount) AS total FROM records WHERE user_id = ? AND date LIKE ? GROUP BY category";
                    queryArgs = new String[]{userId, yearPrefix + "%"};
                    break;
            }

            cursor = db.rawQuery(query, queryArgs);
        }

        if (cursor == null) {
            Toast.makeText(this, "请选择日期", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建饼图数据项和颜色列表
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category"));
            @SuppressLint("Range") float total = cursor.getFloat(cursor.getColumnIndex("total"));
            entries.add(new PieEntry(total, category));
            colors.add(ColorTemplate.COLORFUL_COLORS[entries.size() % ColorTemplate.COLORFUL_COLORS.length]);
        }
        cursor.close();

        // 设置饼图数据和属性
        PieDataSet dataSet = new PieDataSet(entries, "支出类别");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new CustomValueFormatter());
        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        // 设置饼图描述和中心文本
        Description description = new Description();
        description.setTextSize(15);
        description.setText("支出类别");
        pieChart.setCenterTextSize(20);
        pieChart.setDescription(description);
        pieChart.setCenterText("支出类别");

        // 设置图例的位置
        Legend legend = pieChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        // 刷新饼图
        pieChart.invalidate();
    }

    /**
     * 更新总支出的文本视图
     */
    private void updateTotalExpense() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String userId = String.valueOf(getIntent().getIntExtra("currentUserId", -1));
        Cursor cursor = null;
        String query = "";
        String[] queryArgs = null;

        // 根据用户选择的日期和范围生成查询语句
        if (selectedDate != null) {
            switch (selectedRange) {
                case "日":
                    query = "SELECT SUM(amount) AS totalExpense FROM records WHERE user_id = ? AND date = ?";
                    queryArgs = new String[]{userId, selectedDate};
                    break;
                case "月":
                    String monthPrefix = selectedDate.substring(0, 7);
                    query = "SELECT SUM(amount) AS totalExpense FROM records WHERE user_id = ? AND date LIKE ?";
                    queryArgs = new String[]{userId, monthPrefix + "%"};
                    break;
                case "年":
                    String yearPrefix = selectedDate.substring(0, 4);
                    query = "SELECT SUM(amount) AS totalExpense FROM records WHERE user_id = ? AND date LIKE ?";
                    queryArgs = new String[]{userId, yearPrefix + "%"};
                    break;
            }

            cursor = db.rawQuery(query, queryArgs);
        }

        if (cursor == null) {
            Toast.makeText(this, "请选择日期", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String totalExpense = cursor.getString(cursor.getColumnIndex("totalExpense"));
            TextView totalExpenseTextView = findViewById(R.id.totalExpenseTextView);
            totalExpenseTextView.setText("总支出: " + totalExpense);
        }
        cursor.close();
    }
}