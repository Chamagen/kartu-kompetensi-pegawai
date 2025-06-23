package com.kemham.kartukompetensi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.kemham.kartukompetensi.R;

/**
 * Custom view that displays a circular avatar with initials.
 */
public class InitialsAvatarView extends View {

    private Paint backgroundPaint;
    private Paint textPaint;
    private String initials = "";
    private int backgroundColor;
    private int textColor;
    private float textSize;
    private boolean isCircular;
    private int borderWidth;
    private int borderColor;

    public InitialsAvatarView(Context context) {
        super(context);
        init(null);
    }

    public InitialsAvatarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public InitialsAvatarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        // Default values
        backgroundColor = Color.GRAY;
        textColor = Color.WHITE;
        textSize = getResources().getDimensionPixelSize(R.dimen.avatar_text_size);
        isCircular = true;
        borderWidth = 0;
        borderColor = Color.TRANSPARENT;

        // Get attributes
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.InitialsAvatarView);
            backgroundColor = a.getColor(R.styleable.InitialsAvatarView_avatarBackgroundColor, backgroundColor);
            textColor = a.getColor(R.styleable.InitialsAvatarView_avatarTextColor, textColor);
            textSize = a.getDimension(R.styleable.InitialsAvatarView_avatarTextSize, textSize);
            isCircular = a.getBoolean(R.styleable.InitialsAvatarView_isCircular, isCircular);
            borderWidth = a.getDimensionPixelSize(R.styleable.InitialsAvatarView_avatarBorderWidth, borderWidth);
            borderColor = a.getColor(R.styleable.InitialsAvatarView_avatarBorderColor, borderColor);
            String initialText = a.getString(R.styleable.InitialsAvatarView_initials);
            if (initialText != null) {
                setInitials(initialText);
            }
            a.recycle();
        }

        // Initialize paints
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(backgroundColor);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        if (borderWidth > 0) {
            backgroundPaint.setStrokeWidth(borderWidth);
            backgroundPaint.setStyle(Paint.Style.STROKE);
            backgroundPaint.setColor(borderColor);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = resolveSize(getDesiredWidth(), widthMeasureSpec);
        int height = resolveSize(getDesiredHeight(), heightMeasureSpec);
        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }

    private int getDesiredWidth() {
        return getResources().getDimensionPixelSize(R.dimen.avatar_size);
    }

    private int getDesiredHeight() {
        return getResources().getDimensionPixelSize(R.dimen.avatar_size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        float radius = Math.min(width, height) / 2f;
        float centerX = width / 2f;
        float centerY = height / 2f;

        // Draw background
        if (isCircular) {
            canvas.drawCircle(centerX, centerY, radius - borderWidth, backgroundPaint);
            if (borderWidth > 0) {
                canvas.drawCircle(centerX, centerY, radius - borderWidth / 2f, backgroundPaint);
            }
        } else {
            canvas.drawRect(borderWidth, borderWidth, width - borderWidth, height - borderWidth, backgroundPaint);
            if (borderWidth > 0) {
                canvas.drawRect(borderWidth / 2f, borderWidth / 2f, 
                    width - borderWidth / 2f, height - borderWidth / 2f, backgroundPaint);
            }
        }

        // Draw text
        if (!initials.isEmpty()) {
            // Center text vertically
            Rect textBounds = new Rect();
            textPaint.getTextBounds(initials, 0, initials.length(), textBounds);
            float textHeight = textBounds.height();
            float textOffset = textHeight / 2f;

            canvas.drawText(initials, centerX, centerY + textOffset, textPaint);
        }
    }

    public void setInitials(String name) {
        if (name == null || name.isEmpty()) {
            this.initials = "";
        } else {
            StringBuilder initialsBuilder = new StringBuilder();
            String[] parts = name.split("\\s+");
            for (String part : parts) {
                if (!part.isEmpty()) {
                    initialsBuilder.append(part.charAt(0));
                    if (initialsBuilder.length() >= 2) break;
                }
            }
            this.initials = initialsBuilder.toString().toUpperCase();
        }
        invalidate();
    }

    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
        backgroundPaint.setColor(color);
        invalidate();
    }

    public void setTextColor(int color) {
        this.textColor = color;
        textPaint.setColor(color);
        invalidate();
    }

    public void setTextSize(float size) {
        this.textSize = size;
        textPaint.setTextSize(size);
        invalidate();
    }

    public void setCircular(boolean circular) {
        this.isCircular = circular;
        invalidate();
    }

    public void setBorder(int width, int color) {
        this.borderWidth = width;
        this.borderColor = color;
        if (width > 0) {
            backgroundPaint.setStrokeWidth(width);
            backgroundPaint.setStyle(Paint.Style.STROKE);
            backgroundPaint.setColor(color);
        } else {
            backgroundPaint.setStyle(Paint.Style.FILL);
            backgroundPaint.setColor(backgroundColor);
        }
        invalidate();
    }

    public String getInitials() {
        return initials;
    }

    public int getAvatarBackgroundColor() {
        return backgroundColor;
    }

    public int getAvatarTextColor() {
        return textColor;
    }

    public float getAvatarTextSize() {
        return textSize;
    }

    public boolean isCircular() {
        return isCircular;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public int getBorderColor() {
        return borderColor;
    }
}
