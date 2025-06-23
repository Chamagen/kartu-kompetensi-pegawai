package com.kemham.kartukompetensi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.kemham.kartukompetensi.R;

/**
 * Custom view that displays a progress bar with score visualization.
 */
public class ScoreProgressBar extends View {

    private static final float CORNER_RADIUS_RATIO = 0.25f;
    private static final long ANIMATION_DURATION = 500;

    private Paint progressPaint;
    private Paint backgroundPaint;
    private RectF progressBounds;
    private Interpolator interpolator;

    private int max;
    private int progress;
    private float currentProgress;
    private long animationStartTime;
    private boolean isAnimating;
    private int progressColor;
    private int backgroundColor;
    private float cornerRadius;
    private boolean rounded;

    public ScoreProgressBar(Context context) {
        super(context);
        init(null);
    }

    public ScoreProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ScoreProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        // Default values
        max = 100;
        progress = 0;
        currentProgress = 0;
        progressColor = getResources().getColor(R.color.progress_default);
        backgroundColor = getResources().getColor(R.color.progress_background);
        rounded = true;

        // Get attributes
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ScoreProgressBar);
            max = a.getInteger(R.styleable.ScoreProgressBar_max, max);
            progress = a.getInteger(R.styleable.ScoreProgressBar_progress, progress);
            progressColor = a.getColor(R.styleable.ScoreProgressBar_progressColor, progressColor);
            backgroundColor = a.getColor(R.styleable.ScoreProgressBar_backgroundColor, backgroundColor);
            rounded = a.getBoolean(R.styleable.ScoreProgressBar_rounded, rounded);
            a.recycle();
        }

        // Initialize paints
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.FILL);
        progressPaint.setColor(progressColor);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(backgroundColor);

        // Initialize bounds
        progressBounds = new RectF();

        // Initialize interpolator
        interpolator = new AccelerateDecelerateInterpolator();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cornerRadius = rounded ? h * CORNER_RADIUS_RATIO : 0;
        progressBounds.set(0, 0, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw background
        canvas.drawRoundRect(progressBounds, cornerRadius, cornerRadius, backgroundPaint);

        // Calculate progress width
        float progressWidth;
        if (isAnimating) {
            float elapsed = (System.currentTimeMillis() - animationStartTime) / (float) ANIMATION_DURATION;
            if (elapsed >= 1f) {
                elapsed = 1f;
                isAnimating = false;
                currentProgress = progress;
            } else {
                float interpolation = interpolator.getInterpolation(elapsed);
                currentProgress = progress * interpolation;
                invalidate();
            }
            progressWidth = getWidth() * (currentProgress / max);
        } else {
            progressWidth = getWidth() * (progress / (float) max);
        }

        // Draw progress
        if (progressWidth > 0) {
            RectF progressRect = new RectF(0, 0, progressWidth, getHeight());
            canvas.drawRoundRect(progressRect, cornerRadius, cornerRadius, progressPaint);
        }
    }

    public void setProgress(int progress) {
        if (progress != this.progress) {
            this.progress = Math.min(Math.max(0, progress), max);
            startAnimation();
        }
    }

    public void setMax(int max) {
        if (max > 0 && max != this.max) {
            this.max = max;
            this.progress = Math.min(progress, max);
            invalidate();
        }
    }

    public void setProgressColor(@ColorInt int color) {
        if (color != progressColor) {
            progressColor = color;
            progressPaint.setColor(color);
            invalidate();
        }
    }

    public void setBackgroundColor(@ColorInt int color) {
        if (color != backgroundColor) {
            backgroundColor = color;
            backgroundPaint.setColor(color);
            invalidate();
        }
    }

    public void setRounded(boolean rounded) {
        if (this.rounded != rounded) {
            this.rounded = rounded;
            cornerRadius = rounded ? getHeight() * CORNER_RADIUS_RATIO : 0;
            invalidate();
        }
    }

    private void startAnimation() {
        isAnimating = true;
        animationStartTime = System.currentTimeMillis();
        invalidate();
    }

    public void setProgressImmediately(int progress) {
        this.progress = Math.min(Math.max(0, progress), max);
        this.currentProgress = this.progress;
        isAnimating = false;
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public int getMax() {
        return max;
    }

    @ColorInt
    public int getProgressColor() {
        return progressColor;
    }

    @ColorInt
    public int getBackgroundColor() {
        return backgroundColor;
    }

    public boolean isRounded() {
        return rounded;
    }

    public boolean isAnimating() {
        return isAnimating;
    }
}
