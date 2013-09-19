package br.com.dafm.android.buzzzleeper.views;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.View;

@SuppressLint("DrawAllocation")
public class DrawView extends View {

    Paint mPaint = new Paint();
    
    private Integer percent;
    
    private Context context;

    public DrawView(Context context,Integer percent) {
        super(context);            
        this.context = context;
        this.percent = percent;
    }

    @Override
    public void onDraw(Canvas canvas) {
    	
    	canvas.setViewport(getMeasuredWidth(), 150);

        Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG |
                Paint.DITHER_FLAG |
                Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        
        Paint circleCenter = new Paint(Paint.FILTER_BITMAP_FLAG |
                Paint.DITHER_FLAG |
                Paint.ANTI_ALIAS_FLAG);
        circleCenter.setColor(Color.parseColor("#323a45"));
        circleCenter.setStyle(Paint.Style.FILL); 

        //Arc
        mPaint.setColor(Color.parseColor("#58c2cb"));
        mPaint.setStrokeWidth(20);
        RectF box = new RectF(20,20,getWidth()-20,getWidth()-20);
        
        float sweep = 360 * this.percent * 0.01f;
        
        Paint txtPctg = new Paint(); 
        txtPctg.setColor(Color.WHITE); 
        txtPctg.setTextSize(25);
        txtPctg.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Signika-Semibold.ttf"));
        
        
        canvas.drawCircle(getWidth()/2, getHeight()/2, 100, circleCenter);
        canvas.drawArc(box, 270, sweep, false, mPaint);        
        canvas.drawText(this.percent.toString() + "%", getWidth()/3, getWidth()/2, txtPctg);
        
    }
/*
  @Override
    public void onDraw(Canvas c) {
        fullRect.right = getWidth();
        fullRect.bottom = getHeight();

        c.drawBitmap(bgBitmap, null, fullRect, null);

        float angle = SystemClock.uptimeMillis() % 3600 / 10.0f;

        clipPath.reset();
        clipPath.setLastPoint(getWidth() / 2, getHeight() / 2);
        clipPath.lineTo(getWidth() / 2, getHeight());
        clipPath.arcTo(fullRect, -90, angle);
        clipPath.lineTo(getWidth() / 2, getHeight() / 2);
        c.clipPath(clipPath);

        c.drawBitmap(fgBitmap, null, fullRect, null);

        invalidate();
    }   
 * */
}