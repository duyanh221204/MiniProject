package com.duyanhnguyen.miniproject;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.view.View;

public class BarChartView extends View {

    private int rentedCount = 0;
    private int vacantCount = 0;
    private int maxCount = 1;
    private float animProgress = 1f;

    private Paint rentedPaint;
    private Paint vacantPaint;
    private Paint valuePaint;
    private Paint labelPaint;
    private Paint gridPaint;
    private Paint axisPaint;
    private Paint axisLabelPaint;
    private Paint bgPaint;

    public BarChartView(Context context) {
        super(context);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        rentedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rentedPaint.setStyle(Paint.Style.FILL);

        vacantPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        vacantPaint.setStyle(Paint.Style.FILL);

        valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valuePaint.setTextAlign(Paint.Align.CENTER);
        valuePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setColor(Color.parseColor("#718096"));

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#EDF2F7"));
        gridPaint.setStrokeWidth(dpToPx(1f));
        gridPaint.setStyle(Paint.Style.STROKE);

        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setColor(Color.parseColor("#CBD5E0"));
        axisPaint.setStrokeWidth(dpToPx(1.5f));
        axisPaint.setStyle(Paint.Style.STROKE);

        axisLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisLabelPaint.setTextAlign(Paint.Align.RIGHT);
        axisLabelPaint.setColor(Color.parseColor("#A0AEC0"));

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.TRANSPARENT);
    }

    public void setData(int rentedCount, int vacantCount) {
        this.rentedCount = rentedCount;
        this.vacantCount = vacantCount;
        this.maxCount = Math.max(Math.max(rentedCount, vacantCount), 1);

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(900);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(a -> {
            animProgress = (float) a.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();

        float padLeft  = dpToPx(52f);
        float padRight = dpToPx(16f);
        float padTop   = dpToPx(28f);
        float padBot   = dpToPx(44f);

        float chartW = w - padLeft - padRight;
        float chartH = h - padTop - padBot;

        float textSizeValue = spToPx(13f);
        float textSizeLabel = spToPx(12f);
        float textSizeAxis  = spToPx(10f);

        valuePaint.setTextSize(textSizeValue);
        labelPaint.setTextSize(textSizeLabel);
        axisLabelPaint.setTextSize(textSizeAxis);

        // Grid lines
        int gridLines = 5;
        for (int i = 0; i <= gridLines; i++) {
            float y = padTop + chartH - chartH * i / gridLines;
            canvas.drawLine(padLeft, y, padLeft + chartW, y, gridPaint);

            int val = maxCount * i / gridLines;
            canvas.drawText(String.valueOf(val), padLeft - dpToPx(6f), y + textSizeAxis / 3f, axisLabelPaint);
        }

        // Axes
        canvas.drawLine(padLeft, padTop, padLeft, padTop + chartH, axisPaint);
        canvas.drawLine(padLeft, padTop + chartH, padLeft + chartW, padTop + chartH, axisPaint);

        float barW    = chartW * 0.28f;
        float spacing = (chartW - 2 * barW) / 3f;
        float corner  = dpToPx(8f);

        // --- Rented bar ---
        float rx = padLeft + spacing;
        float rentedH = chartH * (rentedCount / (float) maxCount) * animProgress;
        float ry = padTop + chartH - rentedH;
        RectF rentedRect = new RectF(rx, ry, rx + barW, padTop + chartH);

        rentedPaint.setShader(new LinearGradient(
                rx, ry, rx, padTop + chartH,
                Color.parseColor("#FC8181"),
                Color.parseColor("#C53030"),
                Shader.TileMode.CLAMP));
        canvas.drawRoundRect(rentedRect, corner, corner, rentedPaint);

        // Rented value label
        valuePaint.setColor(Color.parseColor("#C53030"));
        if (animProgress > 0.6f) {
            canvas.drawText(String.valueOf(rentedCount), rx + barW / 2f, ry - dpToPx(6f), valuePaint);
        }

        // Rented bottom label
        canvas.drawText("Đã thuê", rx + barW / 2f, padTop + chartH + dpToPx(20f), labelPaint);

        // --- Vacant bar ---
        float vx = padLeft + spacing * 2 + barW;
        float vacantH = chartH * (vacantCount / (float) maxCount) * animProgress;
        float vy = padTop + chartH - vacantH;
        RectF vacantRect = new RectF(vx, vy, vx + barW, padTop + chartH);

        vacantPaint.setShader(new LinearGradient(
                vx, vy, vx, padTop + chartH,
                Color.parseColor("#68D391"),
                Color.parseColor("#276749"),
                Shader.TileMode.CLAMP));
        canvas.drawRoundRect(vacantRect, corner, corner, vacantPaint);

        // Vacant value label
        valuePaint.setColor(Color.parseColor("#276749"));
        if (animProgress > 0.6f) {
            canvas.drawText(String.valueOf(vacantCount), vx + barW / 2f, vy - dpToPx(6f), valuePaint);
        }

        // Vacant bottom label
        canvas.drawText("Còn trống", vx + barW / 2f, padTop + chartH + dpToPx(20f), labelPaint);
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    private float spToPx(float sp) {
        return sp * getResources().getDisplayMetrics().scaledDensity;
    }
}
