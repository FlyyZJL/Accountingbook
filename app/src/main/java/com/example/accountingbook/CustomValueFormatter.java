package com.example.accountingbook;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 自定义值格式化器，用于图表中数值的格式化。
 */
public class CustomValueFormatter extends ValueFormatter {
    /**
     * 格式化图表中的数值。
     * @param value 图表中原始的浮点数值。
     * @return 格式化后的字符串，保留两位小数。
     */
    @Override
    public String getFormattedValue(float value) {
        // 使用BigDecimal进行精确的小数处理，避免浮点数的精度问题
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}