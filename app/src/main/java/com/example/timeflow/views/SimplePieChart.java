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

public class SimplePieChart extends View {

    private List<SliceData> data = new ArrayList<>();
    private Paint slicePaint;
    private Paint textPaint;

    public SimplePieChart(Context context) {
        super(context);
        init();
    }

    public SimplePieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        slicePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        slicePaint.setStyle(Paint.Style.FILL);
        
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.textDark));
        textPaint.setTextSize(dpToPx(14));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
    }
    
    private float dpToPx(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }

    public void setData(List<SliceData> data) {
        this.data = data;
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
        int size = Math.min(width, height) - 40;
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = size / 2;
        
        RectF rect = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        
        float total = 0;
        for (SliceData slice : data) {
            total += slice.value;
        }
        
        if (total == 0) return;
        
        float startAngle = -90; // Start from top
        
        for (SliceData slice : data) {
            float sweepAngle = (slice.value / total) * 360;
            
            slicePaint.setColor(slice.color);
            canvas.drawArc(rect, startAngle, sweepAngle, true, slicePaint);
            
            // Draw label in center if significant
            if (sweepAngle > 30) {
                float labelAngle = startAngle + sweepAngle / 2;
                float labelX = (float) (centerX + (radius * 0.6) * Math.cos(Math.toRadians(labelAngle)));
                float labelY = (float) (centerY + (radius * 0.6) * Math.sin(Math.toRadians(labelAngle)));
                
                textPaint.setColor(slice.textColor);
                canvas.drawText(slice.label, labelX, labelY, textPaint);
            }
            
            startAngle += sweepAngle;
        }
    }

    public static class SliceData {
        public String label;
        public float value;
        public int color;
        public int textColor;

        public SliceData(String label, float value, int color, int textColor) {
            this.label = label;
            this.value = value;
            this.color = color;
            this.textColor = textColor;
        }
    }
}

