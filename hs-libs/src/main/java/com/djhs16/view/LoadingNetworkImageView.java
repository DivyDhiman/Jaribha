package com.djhs16.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.djhs16.HSLibs;

public class LoadingNetworkImageView extends FrameLayout {

	private NetworkScaleImageView mNetworkImageView;
	private ProgressBar mProgressBar;

	public LoadingNetworkImageView(Context context) {
		super(context);
		init();
	}

	public LoadingNetworkImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	// public LoadingNetworkImageView(Context context, AttributeSet attrs, int
	// defStyle) {
	// super(context, attrs, defStyle);
	// init();
	// }

	private void init() {
		mNetworkImageView = new NetworkScaleImageView(getContext());
		mNetworkImageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		mProgressBar = new ProgressBar(getContext());
		mProgressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mProgressBar.setIndeterminate(true);

		addView(mNetworkImageView);
		addView(mProgressBar);

		String imageUrl = "http://upload.wikimedia.org/wikipedia/commons/d/d1/Brick_wall_close-up_view.jpg";
		mNetworkImageView.setImageUrl(imageUrl, HSLibs.getImageLoader());
		mNetworkImageView.setProrgess(mProgressBar);

	}
}
