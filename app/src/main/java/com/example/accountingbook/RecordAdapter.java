package com.example.accountingbook;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecordAdapter类是一个RecyclerView的适配器，用于显示记录列表。
 * 提供点击和长按事件的接口。
 */
public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {
    // 保存记录列表的数据
    private List<Record> recordList;
    // 点击事件的监听器接口
    private OnItemClickListener onItemClickListener;
    // 长按事件的监听器接口
    private OnItemLongClickListener onItemLongClickListener;

    /**
     * 定义点击事件的接口
     */
    public interface OnItemClickListener {
        /**
         * 当item被点击时调用
         *
         * @param position 被点击item的位置
         */
        void onItemClick(int position);
    }

    /**
     * 定义长按事件的接口
     */
    public interface OnItemLongClickListener {
        /**
         * 当item被长按时调用
         *
         * @param position 被长按item的位置
         */
        void onItemLongClick(int position);
    }

    /**
     * 设置点击事件监听器
     *
     * @param listener 点击事件监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    /**
     * 设置长按事件监听器
     *
     * @param listener 长按事件监听器
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    /**
     * 构造函数，初始化记录列表
     *
     * @param recordList 要显示的记录列表
     */
    public RecordAdapter(List<Record> recordList) {
        this.recordList = recordList;
    }

    /**
     * 创建ViewHolder实例，并将item布局膨胀为View对象
     *
     * @param parent   父级ViewGroup
     * @param viewType item的视图类型
     * @return 新创建的RecordViewHolder对象
     */
    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_record, parent, false);
        return new RecordViewHolder(itemView, onItemClickListener, onItemLongClickListener);
    }

    /**
     * 绑定ViewHolder，与数据绑定，设置每个item的显示内容
     *
     * @param holder   要绑定的ViewHolder
     * @param position 当前item的位置
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        Record record = recordList.get(position);
        holder.amountTextView.setText("金额：" + record.getAmount());
        holder.categoryTextView.setText("分类：" + record.getCategory());
        holder.dateTextView.setText("日期：" + record.getDate());
        holder.noteTextView.setText("备注：" + record.getNote());
    }

    /**
     * 返回记录列表的大小
     *
     * @return 记录列表的大小
     */
    @Override
    public int getItemCount() {
        return recordList.size();
    }

    /**
     * 静态内部类，表示RecyclerView的ViewHolder，用于复用itemView，提高性能
     */
    static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView amountTextView, categoryTextView, dateTextView, noteTextView;

        /**
         * 构造函数，初始化ViewHolder，并设置点击和长按事件
         *
         * @param itemView       item的视图对象
         * @param clickListener  点击事件监听器
         * @param longClickListener 长按事件监听器
         */
        public RecordViewHolder(@NonNull View itemView, OnItemClickListener clickListener, OnItemLongClickListener longClickListener) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            noteTextView = itemView.findViewById(R.id.noteTextView);

            // 设置点击事件监听
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onItemClick(position);
                    }
                }
            });

            // 设置长按事件监听
            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        longClickListener.onItemLongClick(position);
                    }
                }
                return true; // 返回true表示事件已处理
            });
        }
    }
}

