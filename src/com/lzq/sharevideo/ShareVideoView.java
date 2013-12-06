package com.lzq.sharevideo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;
/**
 * Extends the VideoView in order to make this component full screen
 * @author LZQ
 *
 */
public class ShareVideoView extends VideoView {

	public ShareVideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ShareVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ShareVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(0, widthMeasureSpec);
		int height = getDefaultSize(0, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

}