package com.example.uhfxintong.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class MyInnerGridView extends GridView {
	 public MyInnerGridView(Context context, AttributeSet attrs) { 
	        super(context, attrs); 
	    } 

	    public MyInnerGridView(Context context) { 
	        super(context); 
	    } 

	    public MyInnerGridView(Context context, AttributeSet attrs, int defStyle) { 
	        super(context, attrs, defStyle); 
	    } 

	    @Override 
	    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

	        int expandSpec = MeasureSpec.makeMeasureSpec( 
	                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST); 
	        super.onMeasure(widthMeasureSpec, expandSpec); 
	        
	    } 

}
