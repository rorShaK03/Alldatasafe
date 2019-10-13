package com.piperStd.alldatasafe.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;

public class GifView extends View
{
    private Movie movie;
    private InputStream stream = null;
    private long movieStart = 0;

    public GifView(Context context)
    {
        super(context);
    }

    public GifView(Context context, AttributeSet a)
    {
        super(context, a);
    }

    public GifView(Context context, InputStream stream)
    {
        super(context);
        this.stream = stream;
        movie = Movie.decodeStream(this.stream);
    }

    public void setInputStream(InputStream stream)
    {
        this.stream = stream;
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        canvas.drawColor(Color.TRANSPARENT);
        super.onDraw(canvas);
        final long now = SystemClock.uptimeMillis();
        if (movieStart == 0) {
            movieStart = now;
        }
        final int relTime = (int) ((now - movieStart) % movie.duration());
        movie.setTime(relTime);
        movie.draw(canvas, 20, 20);
        this.invalidate();
    }

}
