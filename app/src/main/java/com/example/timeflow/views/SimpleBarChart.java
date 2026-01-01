package com.example.timeflow.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.timeflow.R;

import java.util.ArrayList;
import java.util.List;

public class SimpleBarChart extends View {

    private List<BarData> data = new ArrayList<>();
    private Paint barPaint;
    private Paint textPaint;
    private Paint labelPaint;
    private int maxValue = 100;

    public SimpleBarChart(Context context) {
        super(context);
        init();
    }

    public SimpleBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        float density = getContext().getResources().getDisplayMetrics().density;
        
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setStyle(Paint.Style.FILL);
        
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.textDark));
        textPaint.setTextSize(14 * density);
        textPaint.setTextAlign(Paint.Align.CENTER);
        
        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(ContextCompat.getColor(getContext(), R.color.textLight));
        labelPaint.setTextSize(12 * density);
        labelPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setData(List<BarData> data) {
        this.data = data;
        if (data != null && !data.isEmpty()) {
            maxValue = 0;
            for (BarData bar : data) {
                if (bar.value > maxValue) {
                    maxValue = (int) bar.value;
                }
            }
            if (maxValue == 0) maxValue = 100;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (data == null || data.isEmpty()) {
            return;
        }

        int width = getWidth();
        int height = getHeight();
        int padding = 40;
        int chartWidth = width - (padding * 2);
        int chartHeight = height - (padding * 2) - 60; // Space for labels
        
        int barCount = data.size();
        int barWidth = chartWidth / (barCount * 2 + 1);
        int spacing = barWidth;

        for (int i = 0; i < barCount; i++) {
            BarData bar = data.get(i);
            float barHeight = (bar.value / (float) maxValue) * chartHeight;
            
            float left = padding + spacing + (i * (barWidth + spacing));
            float top = padding + chartHeight - barHeight;
            float right = left + barWidth;
            float bottom = padding + chartHeight;
            
            barPaint.setColor(bar.color);
            canvas.drawRoundRect(new RectF(left, top, right, bottom), 8, 8, barPaint);
            
            // Draw value on top
            if (barHeight > 30) {
                canvas.drawText(String.valueOf((int)bar.value), left + barWidth / 2, top - 10, textPaint);
            }
            
            // Draw label
            canvas.drawText(bar.label, left + barWidth / 2, height - 20, labelPaint);
        }
    }

    public static class BarData {
        public String label;
        public float value;
        public int color;

        public BarData(String label, float value, int color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }
    }
}

