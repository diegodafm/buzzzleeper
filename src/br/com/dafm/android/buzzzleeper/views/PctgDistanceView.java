package br.com.dafm.android.buzzzleeper.views;
import java.text.DecimalFormat;

import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.entity.BlrAddress;

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
public class PctgDistanceView extends View {

    Paint mPaint = new Paint();
    
    private Float percent;
    
    private Context context;
    
    private Double distance;
    
    private BlrAddress blrAddress;

    public PctgDistanceView(Context context,Float percent, Double distance, BlrAddress blrAddress) {
        super(context);            
        this.context = context;
        this.percent = percent;
        this.distance = distance;
        this.blrAddress = blrAddress;
    }

    @Override
    public void onDraw(Canvas canvas) {
    	
    	canvas.setViewport(getMeasuredWidth(), getMeasuredHeight());
    	
    	Float defaultPxArc = convertDpToPixel(8, this.context);

        Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        
        Paint circleCenter = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        circleCenter.setColor(Color.parseColor("#323a45"));
        circleCenter.setStyle(Paint.Style.FILL); 

        //Arc
        if(this.distance > blrAddress.getBuffer()){
        	mPaint.setColor(Color.parseColor("#58c2cb"));
        }else{        	
        	mPaint.setColor(Color.parseColor("#d64d4d"));
        }
        mPaint.setStrokeWidth(convertDpToPixel(15, this.context));
        RectF box = new RectF(defaultPxArc,defaultPxArc,getWidth()-defaultPxArc,getWidth()-defaultPxArc);
        
        float sweep = 360 * this.percent * 0.01f;
        
        Paint txtPctg = new Paint(); 
        txtPctg.setColor(Color.WHITE); 
        txtPctg.setTextSize(convertDpToPixel(40, this.context));
        txtPctg.setTextAlign(Paint.Align.CENTER);
        txtPctg.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Signika-Semibold.ttf"));
        
        Paint txtDistance = new Paint(); 
        txtDistance.setColor(Color.WHITE); 
        txtDistance.setTextSize(convertDpToPixel(15, this.context));
        txtDistance.setTextAlign(Paint.Align.CENTER);
        txtDistance.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Signika-Semibold.ttf"));
        
        canvas.drawCircle(getWidth()/2, getWidth()/2, getWidth() / 2  - convertDpToPixel(15, this.context) , circleCenter);
        canvas.drawArc(box, 270, sweep, false, mPaint);        
        
        DecimalFormat df = new DecimalFormat("#");
        if(this.distance > blrAddress.getBuffer()){
        	canvas.drawText(df.format(this.percent.floatValue()), getWidth()/2, getHeight()/2, txtPctg);
        	canvas.drawText("%", getWidth()/2+convertDpToPixel((this.percent.floatValue()<10)?20:30, this.context), getHeight()/2 - convertDpToPixel(15, this.context), txtDistance);
        }else{        	
        	canvas.drawText(this.context.getString(R.string.stop), getWidth()/2, getHeight()/2, txtPctg);
        }
        
        if(distance > 2000){
        	df = new DecimalFormat("#.##");
        	canvas.drawText(df.format(this.distance/1000), getWidth()/2, getHeight()/2+convertDpToPixel(25, this.context), txtDistance);
        	canvas.drawText(this.context.getString(R.string.km), getWidth()/2, getHeight()/2+convertDpToPixel(40, this.context), txtDistance);
        }else{
        	df = new DecimalFormat("#");
        	canvas.drawText(df.format(this.distance), getWidth()/2, getHeight()/2+convertDpToPixel(25, this.context), txtDistance);
        	canvas.drawText(this.context.getString(R.string.meters), getWidth()/2, getHeight()/2+convertDpToPixel(40, this.context), txtDistance);
        }
        
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
}