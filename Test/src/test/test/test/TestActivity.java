package test.test.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class TestActivity extends Activity {
	private int screenWidth = 0;
	private int screenHeight = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.main);
		screenWidth = getResources().getDisplayMetrics().widthPixels;
		screenHeight = getResources().getDisplayMetrics().heightPixels;
		// 设置View的Touch事件
		MyImageView btn = (MyImageView) findViewById(R.id.btn);
		btn.setOnTouchListener(onDragTouchListener);
	}

	OnTouchListener onDragTouchListener = new OnTouchListener()
	{
	 private float startX = 0;
	 private float startY = 0;
	 
	 @Override
	 public boolean onTouch(View v, MotionEvent event)
	 {
		 System.out.println("onEvent "+event.getAction());
	  switch (event.getAction())
	  {
	   case MotionEvent.ACTION_DOWN:
	   {
	    startX = event.getRawX();
	    startY = event.getRawY();
	    break;
	   }
	   case MotionEvent.ACTION_MOVE:
	   {
	    // 计算偏移量
	    int dx = (int) (event.getRawX() - startX);
	    int dy = (int) (event.getRawY() - startY);
		 System.out.println("onTouched dx = "+dx+" dy = "+dy);
	    // 计算控件的区域
	    int left = v.getLeft() + dx;
	    int right = v.getRight() + dx;
	    int top = v.getTop() + dy;
	    int bottom = v.getBottom() + dy;
//	    // 超出屏幕检测
//	    if (left < 0)
//	    {
//	     left = 0;
//	     right = v.getWidth();
//	    }
//	    if (right > screenWidth)
//	    {
//	     right = screenWidth;
//	     left = screenWidth - v.getWidth();
//	    }
//	    if (top < 0)
//	    {
//	     top = 0;
//	     bottom = v.getHeight();
//	    }
//	    if (bottom > screenHeight)
//	    {
//	     bottom = screenHeight;
//	     top = screenHeight - v.getHeight();
//	    }
	    v.layout(left, top, right, bottom);
	    startX = event.getRawX();
	    startY = event.getRawY();
	    break;
	   }
	  }
	  return false;
	 }
	};
}
