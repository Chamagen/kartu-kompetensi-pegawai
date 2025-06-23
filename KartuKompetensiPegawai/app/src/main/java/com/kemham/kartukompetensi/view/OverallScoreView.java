package com.kemham.kartukompetensi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.kemham.kartukompetensi.R;
import com.kemham.kartukompetensi.util.AppSettings;
import com.kemham.kartukompetensi.util.KompetensiUtil;

/**
 * Custom view that displays an overall competency score with visual feedback.
 */
public class OverallScoreView extends View {

    private static final float STROKE_WIDTH_RATIO = 0.1f;
    private static final float TEXT_SIZE_RATIO = 0.3f;
    private static final float LABEL_SIZE_RATIO = 0.15f;
    private static final float ANIMATION_DURATION = 1000f;

    private Paint arcPaint;
    private Paint backgroundPaint;
    private Paint scorePaint;
    private Paint labelPaint;
    private RectF arcBounds;

    private int score;
    private int targetScore;
    private float currentAngle;
    private long startTime;
    private boolean isAnimating;
    private int backgroundColor;
    private int progressColor;
    private int textColor;
    private String label;

    public OverallScoreView(Context context) {
        super(context);
        init(null);
    }

    public OverallScoreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public OverallScoreView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        // Default values
        score = 0;
        targetScore = AppSettings.MAX_SCORE;
        backgroundColor = getResources().getColor(R.color.score_background);
        progressColor = getResources().getColor(R.color.score_progress);
        textColor = getResources().getColor(R.color.score_text);
        label = getResources().getString(R.string.overall_score);

        // Get attributes
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.OverallScoreView);
            score = a.getInteger(R.styleable.OverallScoreView_score, score);
            targetScore = a.getInteger(R.styleable.OverallScoreView_targetScore, targetScore);
            backgroundColor = a.getColor(R.styleable.OverallScoreView_backgroundColor, backgroundColor);
            progressColor = a.getColor(R.styleable.OverallScoreView_progressColor, progressColor);
            textColor = a.getColor(R.styleable.OverallScoreView_textColor, textColor);
            label = a.getString(R.styleable.OverallScoreView_label);
            if (label == null) label = getResources().getString(R.string.overall_score);
            a.recycle();
        }

        // Initialize paints
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setColor(progressColor);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setColor(backgroundColor);

        scorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scorePaint.setColor(textColor);
        scorePaint.setTextAlign(Paint.Align.CENTER);
        scorePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(textColor);
        labelPaint.setTextAlign(Paint.Align.CENTER);

        arcBounds = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        );
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float strokeWidth = Math.min(w, h) * STROKE_WIDTH_RATIO;
        arcPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setStrokeWidth(strokeWidth);

        float textSize = Math.min(w, h) * TEXT_SIZE_RATIO;
        scorePaint.setTextSize(textSize);

        float labelSize = Math.min(w, h) * LABEL_SIZE_RATIO;
        labelPaint.setTextSize(labelSize);

        float padding = strokeWidth / 2;
        arcBounds.set(padding, padding, w - padding, h - padding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw background circle
        canvas.drawArc(arcBounds, 0, 360, false, backgroundPaint);

        // Calculate and draw progress arc
        float sweepAngle;
        if (isAnimating) {
            float elapsed = (System.currentTimeMillis() - startTime) / ANIMATION_DURATION;
            if (elapsed >= 1f) {
                elapsed = 1f;
                isAnimating = false;
            }
            sweepAngle = currentAngle * elapsed;
            invalidate();
        } else {
            sweepAngle = currentAngle;
        }
        canvas.drawArc(arcBounds, -90, sweepAngle, false, arcPaint);

        // Draw score text
        String scoreText = String.valueOf(score);
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        canvas.drawText(scoreText, centerX, centerY + scorePaint.getTextSize() / 3, scorePaint);

        // Draw label text
        canvas.drawText(label, centerX, centerY + scorePaint.getTextSize(), labelPaint);
    }

    public void setScore(int score, int targetScore) {
        this.score = score;
        this.targetScore = targetScore;
        this.currentAngle = 360f * score / targetScore;
        this.progressColor = KompetensiUtil.getProgressColor(getContext(), score, targetScore);
        arcPaint.setColor(progressColor);
        startAnimation();
    }

    private void startAnimation() {
        isAnimating = true;
        startTime = System.currentTimeMillis();
        invalidate();
    }

    public void setLabel(String label) {
        this.label = label;
        invalidate();
    }

    public void setColors(int backgroundColor, int progressColor, int textColor) {
        this.backgroundColor = backgroundColor;
        this.progressColor = progressColor;
        this.textColor = textColor;
        
        backgroundPaint.setColor(backgroundColor);
        arcPaint.setColor(progressColor);
        scorePaint.setColor(textColor);
        labelPaint.setColor(textColor);
        
        invalidate();
    }

    public int getScore() {
        return score;
    }

    public int getTargetScore() {
        return targetScore;
    }

    public String getLabel() {
        return label;
    }
}
