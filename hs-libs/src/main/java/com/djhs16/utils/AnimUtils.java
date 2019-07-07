package com.djhs16.utils;

import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;

public class AnimUtils {
	// To animate view slide out from left to right
	public static void slideToRight(View view) {
		TranslateAnimation animate = new TranslateAnimation(0, view.getWidth(), 0, 0);
		animate.setDuration(500);
		// animate.setFillAfter(true);
		view.startAnimation(animate);
		view.setVisibility(View.GONE);
	}

	// To animate view slide out from right to left
	public static void slideToLeft(View view) {
		TranslateAnimation animate = new TranslateAnimation(0, -view.getWidth(), 0, 0);
		animate.setDuration(500);
		// animate.setFillAfter(true);
		view.startAnimation(animate);
		view.setVisibility(View.GONE);
	}

	// To animate view slide out from top to bottom
	public static void slideToBottom(View view) {
		view.setVisibility(View.VISIBLE);
		TranslateAnimation animate = new TranslateAnimation(0, 0, -view.getHeight(), 0);
		animate.setDuration(300);
		// animate.setFillAfter(true);
		animate.setInterpolator(new BounceInterpolator());
		// animate.setInterpolator(new AnticipateOvershootInterpolator());
		// animate.setInterpolator(new AnticipateInterpolator());
		// animate.setInterpolator(new CycleInterpolator(2));

		// animate.setInterpolator(new OvershootInterpolator());
		// animate.setInterpolator(new AccelerateDecelerateInterpolator());
		view.startAnimation(animate);
	}

	// To animate view slide out from bottom to top
	public static void slideToTop(View view) {
		TranslateAnimation animate = new TranslateAnimation(0, 0, 0, -view.getHeight());
		animate.setDuration(300);
		// animate.setFillAfter(true);
		view.startAnimation(animate);
		// animate.setInterpolator(new BounceInterpolator());
		view.setVisibility(View.GONE);
	}
}
