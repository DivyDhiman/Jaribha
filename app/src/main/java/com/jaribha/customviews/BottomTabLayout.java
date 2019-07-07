package com.jaribha.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaribha.R;


public class BottomTabLayout extends HorizontalScrollView {

    public interface BottomTabAdapter {
        int getCount();

        String getTitle(int pos);

        void onTabClick(int pos);
    }

    /**
     * Allows complete control over the colors drawn in the tab layout. Set with
     * {@link #setCustomTabColorizer(TabColorizer)}.
     */
    public interface TabColorizer {

        /**
         * @return return the color of the indicator used when {@code position} is selected.
         */
        int getIndicatorColor(int position);

    }

    private static final int TITLE_OFFSET_DIPS = 24;

    private int mTitleOffset;
    private boolean mDistributeEvenly;

    private BottomTabAdapter bottomTabAdapter;

    private final BottomTabStrip mTabStrip;

    public BottomTabLayout(Context context) {
        this(context, null);
    }

    public BottomTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Disable the Scroll Bar
        setHorizontalScrollBarEnabled(false);
        // Make sure that the Tab Strips fills this View
        setFillViewport(true);

        mTitleOffset = (int) (TITLE_OFFSET_DIPS * getResources().getDisplayMetrics().density);

        mTabStrip = new BottomTabStrip(context);
        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * Set the custom {@link TabColorizer} to be used.
     * <p/>
     * If you only require simple customisation then you can use
     * similar effects.
     */
    public void setCustomTabColorizer(TabColorizer tabColorizer) {
        mTabStrip.setCustomTabColorizer(tabColorizer);
    }

    public void setDistributeEvenly(boolean distributeEvenly) {
        mDistributeEvenly = distributeEvenly;
    }

    /**
     * Sets the colors to be used for indicating the selected tab. These colors are treated as a
     * circular array. Providing one color will mean that all tabs are indicated with the same color.
     */
    public void setSelectedIndicatorColors(int... colors) {
        mTabStrip.setSelectedIndicatorColors(colors);
    }

    public void setBottomTabAdapter(BottomTabAdapter adapter) {
        this.bottomTabAdapter = adapter;
        populateTabStrip();
    }

    /**
     * Create a default_icon view to be used for tabs. This is called if a custom tab view is not set via
     */
    protected View createTabView(Context context, String title) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_top_bar, null);

        TextView titleText = (TextView) view.findViewById(R.id.tv_title_text);

        titleText.setText(title);

        return view;
    }

    private void populateTabStrip() {
        for (int i = 0; i < bottomTabAdapter.getCount(); i++) {
            View tabView = createTabView(getContext(), bottomTabAdapter.getTitle(i));

            final int finalPos = i;
            tabView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomTabAdapter.onTabClick(finalPos);
                    scrollToTab(finalPos);
                }
            });


            mTabStrip.addView(tabView);
            if (mDistributeEvenly) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                lp.width = 0;
                lp.weight = 1;
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (bottomTabAdapter != null) {
            scrollToTab(0);
        }
    }

    private void scrollToTab(int tabIndex) {
        final int tabStripChildCount = mTabStrip.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        View selectedChild = mTabStrip.getChildAt(tabIndex);
        if (selectedChild != null) {
            int targetScrollX = selectedChild.getLeft();
            scrollTo(targetScrollX, 0);
            mTabStrip.onViewPagerPageChanged(tabIndex, 0);
        }
    }

}

