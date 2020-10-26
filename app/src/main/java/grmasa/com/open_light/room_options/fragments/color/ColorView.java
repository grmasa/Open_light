package grmasa.com.open_light.room_options.fragments.color;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import grmasa.com.open_light.R;

public class ColorView extends View {

    private Paint paintObj;
    private int cursorSize = 40;
    private int touchX;
    private int touchY;
    private Bitmap cursor;
    private Bitmap background;
    private int mCursorResourceId, mBackgroundResourceId;
    private int pixel;
    private ColorListener colorListener;

    public ColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public void init(@Nullable AttributeSet set) {
        paintObj = new Paint(Paint.ANTI_ALIAS_FLAG);
        getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            cursor = BitmapFactory.decodeResource(getResources(), mCursorResourceId);
            if (cursor != null) {
                cursor = BitmapFactory.decodeResource(getResources(), mCursorResourceId);
            }
            background = BitmapFactory.decodeResource(getResources(), mBackgroundResourceId);
            if (background != null) {
                background = Bitmap.createScaledBitmap(background, getWidth(), getHeight(), false);
            }
        });

        if (set != null) {
            @SuppressLint("CustomViewStyleable") TypedArray typedArray = getContext().obtainStyledAttributes(set, R.styleable.colorView);
            cursorSize = typedArray.getDimensionPixelSize(R.styleable.colorView_circle_radius, cursorSize);
            mCursorResourceId = typedArray.getResourceId(R.styleable.colorView_circle_image, R.drawable.baseline_add_black_18dp);
            mBackgroundResourceId = typedArray.getResourceId(R.styleable.colorView_backGround_image, R.drawable.icon_refresh_device);
            setBackgroundResource(mBackgroundResourceId);
            paintObj.setStyle(Paint.Style.FILL);
            typedArray.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (cursor != null && background != null && touchX > 0 && touchY > 0 && touchX < background.getWidth() && touchY < background.getHeight() && background.getPixel(touchX, touchY) != Color.TRANSPARENT) {
            paintObj.setStyle(Paint.Style.FILL);
            paintObj.setColor(getColorSelected());
            canvas.drawCircle(touchX, touchY, cursorSize, paintObj);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        touchX = (int) event.getX();
        touchY = (int) event.getY();
        //Listen only inside the view
        if (touchX > getWidth() || touchY > getHeight() || touchX < 0 || touchY < 0) {
            return false;
        }

        if (background != null && cursor != null) {
            setColorSelected(touchX, touchY);
            if (colorListener != null) {
                colorListener.onEvent(getColorSelected(), xPercentage(touchX), yPercentage(touchY));
            }
            invalidate();
            return true;
        }
        return false;
    }

    private void setColorSelected(float x, float y) {
        if (x > 0 && y > 0 && x < background.getWidth() && y < background.getHeight()) {
            pixel = background.getPixel((int)x, (int)y);
        }
    }

    public int getColorSelected() {
        return pixel;
    }

    public void listenToColorPallet(@NonNull ColorListener obj) {
        colorListener = obj;
    }

    public int xPercentage(int touchX) {
        return Math.min(100, Math.round((touchX * 100 / getWidth())));
    }

    public int yPercentage(int touchY) {
        return Math.min(100, Math.round(((touchY * 100) / getHeight())));
    }
}