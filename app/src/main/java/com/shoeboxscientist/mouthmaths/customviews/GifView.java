package com.shoeboxscientist.mouthmaths.customviews;

/**
 * Created by adamconnors on 11/9/17.
 */

import java.io.InputStream;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

import com.shoeboxscientist.mouthmaths.R;

public class GifView extends View {
    public Movie mMovie;
    public long movieStart;
    public int mSrc;

    public GifView(Context context) {
        super(context);
        initializeView(null);
    }

    public GifView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView(attrs);
    }

    public GifView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeView(attrs);
    }

    private void initializeView(AttributeSet attrs) {
        mSrc = attrs.getIdAttributeResourceValue(R.raw.listening);
        InputStream is = getContext().getResources().openRawResource(mSrc);
        mMovie = Movie.decodeStream(is);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(mMovie != null) {
            setMeasuredDimension(mMovie.width(), mMovie.height());
        } else {
            setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        super.onDraw(canvas);
        long now = android.os.SystemClock.uptimeMillis();
        if (movieStart == 0) {
            movieStart = now;
        }
        if (mMovie != null) {
            int relTime = (int) ((now - movieStart) % mMovie.duration());
            mMovie.setTime(relTime);
            mMovie.draw(canvas, getWidth() - mMovie.width(), getHeight() - mMovie.height());
            this.invalidate();
        }
    }
}