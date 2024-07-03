package com.example.accountingbook;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CustomValueFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        // 使用BigDecimal进行精确的小数处理，避免浮点数的精度问题
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}