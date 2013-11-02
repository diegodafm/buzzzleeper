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
import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.entity.BlrAddress;

@SuppressLint({ "DrawAllocation", "ViewConstructor" })
public class PctgDistanceView extends View{


    Paint mPaint = new Paint();
    
    private Float percent;
    
    private Context context;
    
    private Double distance;
    
    private BlrAddress blrAddress;
    
    private String textCircle;
    
	public PctgDistanceView(Context context, Float percent, Double distance,
			BlrAddress blrAddress) {
		
        super(context);            
        this.context = context;
        this.percent = percent;
        this.distance = distance;
        this.blrAddress = blrAddress;
    }
    
	public PctgDistanceView(Context context, Float percent, Double distance,
			BlrAddress blrAddress, String textCircle) {
		
    	super(context);            
    	this.context = context;
    	this.percent = percent;
    	this.distance = distance;
    	this.blrAddress = blrAddress;
    	this.textCircle = textCircle;
    }

    @Override
    public void onDraw(Canvas canvas) {
    	
    	//canvas.setViewport(getMeasuredWidth(), getMeasuredHeight());
    	

        Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        if(this.distance > blrAddress.getBuffer() && this.percent >= 0){
        	mPaint.setColor(Color.parseColor("#58c2cb"));
        }else if(this.percent < 0){
        	mPaint.setColor(Color.parseColor("#ffd200"));
        }else{        	
        	mPaint.setColor(Color.parseColor("#d64d4d"));
        }
        mPaint.setStrokeWidth(convertDpToPixel(16));    
        
        Float defaultPxArc = convertDpToPixel(8);
        RectF box = new RectF(defaultPxArc,defaultPxArc,getWidth()-defaultPxArc,getWidth()-defaultPxArc);
        Float sweep = 360 * this.percent * 0.01f;
        
        
        canvas.drawArc(box, 270, sweep, false, mPaint);
        
    	
        Paint circleCenter = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        circleCenter.setColor(Color.parseColor("#323a45"));
        circleCenter.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getWidth()/2, getWidth()/2, getWidth() / 2  - convertDpToPixel(15) , circleCenter);
        
        if(textCircle == null){
        	drawPctgDistance(canvas);        	
        }else{
        	drawTextCircle(canvas);
        }
    }
    
    private void drawTextCircle(Canvas canvas) {
    	
    	canvas.drawText(textCircle, getWidth() / 2, (getHeight() / 2 + convertDpToPixel(10)), textPaint(30));
    	
	}

	private void drawPctgDistance(Canvas canvas){
        
        DecimalFormat df = new DecimalFormat("#");
        	
    	canvas.drawText(df.format(this.percent.floatValue()), getWidth()/2, getHeight()/2, textPaint(40));
    	
    	Float leftPos = null;
    	if(this.percent.floatValue()<10){
    		leftPos = getWidth()/2+convertDpToPixel(25);
    	}else if(this.percent.floatValue() <= 99){
    		leftPos = getWidth()/2+convertDpToPixel(30);
    	}else{
    		leftPos = getWidth()/2+convertDpToPixel(40);
    	}
    	canvas.drawText("%", leftPos , getHeight()/2 - convertDpToPixel(15), textPaint(15));
    	
    	df = new DecimalFormat("#.##");
    	if(distance > 2000){
    		canvas.drawText(df.format(this.distance/1000), getWidth()/2, getHeight()/2+convertDpToPixel(25), textPaint(15));
    		canvas.drawText(this.context.getString(R.string.km), getWidth()/2, getHeight()/2+convertDpToPixel(40), textPaint(15));
    	}else{
    		df = new DecimalFormat("#");
    		canvas.drawText(df.format(this.distance), getWidth()/2, getHeight()/2+convertDpToPixel(25), textPaint(15));
    		canvas.drawText(this.context.getString(R.string.meters), getWidth()/2, getHeight()/2+convertDpToPixel(40), textPaint(15));
    	}
    	
    }
    
	private Paint textPaint(Integer fontSize) {
		Paint text = new Paint();
		text.setColor(Color.WHITE);
		text.setTextSize(convertDpToPixel(fontSize));
		text.setTextAlign(Paint.Align.CENTER);
		text.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Signika-Semibold.ttf"));
		return text;
	}
    
    /**
     * This method converts dp unit to equivalent pixels, depending on device density. 
     * 
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public Float convertDpToPixel(float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Float px = dp * (metrics.densityDpi / 160f);
        
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     * 
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public Float convertPixelsToDp(float px){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }
}