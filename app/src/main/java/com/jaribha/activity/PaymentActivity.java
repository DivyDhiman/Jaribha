package com.jaribha.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eyeem.recyclerviewtools.Log;
import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.fragments.PaymentConfirmation;
import com.jaribha.fragments.PaymentResponse;
import com.jaribha.fragments.PaymentView;
import com.jaribha.fragments.SetupFragment;
import com.jaribha.models.Rewards;
import com.jaribha.models.TestData;
import com.jaribha.utility.Constants;

import java.util.ArrayList;

public class PaymentActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private TextView textView0, textView1, textView2;

    private ArrayList<TestData> titles = new ArrayList<>();

    private ImageView imageView0, imageView1, imageView2;

    private ImageView iv_close;

    private TextView tv_title;

    private BezelImageView img_user_image;

    private Rewards rewards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_payment);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        rewards = (Rewards) getIntent().getSerializableExtra(Constants.DATA);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.project_payment));

        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setImageResource(R.drawable.btn_back);
        iv_close.setOnClickListener(this);

        img_user_image = (BezelImageView) findViewById(R.id.img_user_image);
        img_user_image.setVisibility(View.VISIBLE);
        img_user_image.setOnClickListener(this);

        displayUserImage(img_user_image);

        titles.add(new TestData(getString(R.string.step_1), 1));
        titles.add(new TestData(getString(R.string.step_2), 0));
        titles.add(new TestData(getString(R.string.step_3), 0));

        textView0 = (TextView) findViewById(R.id.textView0);
        textView0.setText(titles.get(0).getTitle());
        textView0.setTextColor(Color.WHITE);
        textView0.setOnClickListener(this);

        textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setText(titles.get(1).getTitle());
        textView1.setTextColor(Color.BLACK);
        textView1.setOnClickListener(this);

        textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setText(titles.get(2).getTitle());
        textView2.setTextColor(Color.BLACK);
        textView2.setOnClickListener(this);

        imageView0 = (ImageView) findViewById(R.id.imageView0);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);

        imageView1.setImageResource(R.drawable.img_blank);
        imageView2.setImageResource(R.drawable.img_blank);

        displayFragment(0, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void displayFragment(int position, boolean backStack) {
        //Fragment fragment = null;
        switch (position) {
            case 0:
                Constants.firse = true;
                textView0.setTextColor(Color.WHITE);
                imageView0.setImageResource(R.drawable.img_dot);
                fragment = SetupFragment.newInstance(rewards);
                break;
            case 1:
                Constants.second = true;
                textView1.setTextColor(Color.WHITE);
                imageView0.setImageResource(R.drawable.img_complete);
                imageView1.setImageResource(R.drawable.img_dot);
                fragment = PaymentConfirmation.newInstance(rewards);
                break;
            case 2:
                Constants.third = true;
                textView2.setTextColor(Color.WHITE);
                imageView0.setImageResource(R.drawable.img_complete);
                imageView1.setImageResource(R.drawable.img_complete);
                imageView2.setImageResource(R.drawable.img_dot);
                fragment = new PaymentView();
                break;
            case 12:
                Constants.third = true;
                textView2.setTextColor(Color.WHITE);
                imageView0.setImageResource(R.drawable.img_complete);
                imageView1.setImageResource(R.drawable.img_complete);
                imageView2.setImageResource(R.drawable.img_complete);
                fragment = PaymentResponse.newInstance(rewards);
                break;
            default:
                break;
        }

        if (fragment != null) {
            Fragment myFragment = getSupportFragmentManager().findFragmentByTag(fragment.getClass().getName());
            if (myFragment != null && myFragment.isVisible()) {
                Log.d(getClass().getName(), "Already on this fragment");
            } else {
                addFragment(fragment, fragment.getClass().getName(), backStack);
            }
        }
    }

    private void addFragment(Fragment fragment, String tag, Boolean addStack) {
        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(tag, 0);
        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(R.id.frameCointainer, fragment);
            //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            if (addStack) {
                ft.addToBackStack(tag);
            }
            ft.commit();
        }
    }

    Fragment fragment = null;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_close:
                finish();
                break;

            case R.id.img_user_image:
                startActivity(new Intent(getActivity(), MyPortfolioActivity.class));
                break;

            case R.id.textView0:
                if (Constants.firse && Constants.second && Constants.third)
                    fragment = SetupFragment.newInstance(rewards);
                backToFragment(fragment);
//                if (fragment != null) {
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.frameCointainer, fragment).commit();
//                }
                break;
            case R.id.textView1:
                if (Constants.firse && Constants.second && Constants.third)
                    fragment = PaymentConfirmation.newInstance(rewards);
                backToFragment(fragment);
//                if (fragment != null) {
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.frameCointainer, fragment).commit();
//                }
                break;
            case R.id.textView2:
                if (Constants.firse && Constants.second && Constants.third)
                    backToFragment(new PaymentView());
//                if (fragment != null) {
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.frameCointainer, fragment).commit();
//                }
                break;
            default:
                break;
        }
    }

    public void backToFragment(final Fragment fragment) {
        // go back to something that was added to the backstack
        getSupportFragmentManager().popBackStackImmediate(fragment.getClass().getName(), 0);
        // use 0 or the below constant as flag parameter
        // FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
