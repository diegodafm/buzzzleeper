package br.com.dafm.android.buzzzleeper.views;
import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.View;

@SuppressLint({ "DrawAllocation", "ViewConstructor" })
public class DrawView extends View {

    Paint mPaint = new Paint();
    
    private Float percent;
    
    private Context context;

    public DrawView(Context context,Float percent) {
        super(context);            
        this.context = context;
        this.percent = percent;
    }

    @Override
    public void onDraw(Canvas canvas) {
    	
    	canvas.setViewport(getMeasuredWidth(), 150);
    	
    	Float defaultPxDistance = convertDpToPixel(15, this.context);
    	Float defaultPxArc = convertDpToPixel(18, this.context);
    	

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
        mPaint.setStrokeWidth(defaultPxDistance);
        RectF box = new RectF(defaultPxArc,defaultPxArc,getWidth()-defaultPxArc,getWidth()-defaultPxArc);
        
        float sweep = 360 * this.percent * 0.01f;
        
        Paint txtPctg = new Paint(); 
        txtPctg.setColor(Color.WHITE); 
        txtPctg.setTextSize(60);
        txtPctg.setTextAlign(Paint.Align.CENTER);
        txtPctg.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Signika-Semibold.ttf"));
        
        
        
        canvas.drawCircle(getWidth()/2, getHeight()/2, 100, circleCenter);
        canvas.drawArc(box, 270, sweep, false, mPaint);        
        
        DecimalFormat df = new DecimalFormat("#.##");
        canvas.drawText(df.format(this.percent.floatValue()), getWidth()/2, getHeight()/2+convertDpToPixel(10, this.context), txtPctg);
        
    }
    
    /**
     * This method converts dp unit to equivalent pixels, depending on device density. 
     * 
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     * 
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
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