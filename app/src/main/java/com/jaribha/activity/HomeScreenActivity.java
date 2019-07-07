package com.jaribha.activity;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.jaribha.R;
import com.jaribha.adapters.MenuAdapter;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.customviews.ExpandableHeightListView;
import com.jaribha.customviews.TextDrawable;
import com.jaribha.fragments.ChangePasswordFragment;
import com.jaribha.fragments.ContactusFragment;
import com.jaribha.fragments.CreateProjectFragment;
import com.jaribha.fragments.EditProfileFragment;
import com.jaribha.fragments.GuidelinesFragment;
import com.jaribha.fragments.HelpFragment;
import com.jaribha.fragments.HomeFragment;
import com.jaribha.fragments.HowItWorksFragment;
import com.jaribha.fragments.MyFavoritesFragment;
import com.jaribha.fragments.MyHistoryFragment;
import com.jaribha.fragments.MyMessagesFragment;
import com.jaribha.fragments.NewsLetterFragment;
import com.jaribha.fragments.NotificationFragment;
import com.jaribha.fragments.PaymentResponse;
import com.jaribha.fragments.ProjectsWithFilterFragment;
import com.jaribha.fragments.RewardsFragment;
import com.jaribha.fragments.SelectLanguageFragment;
import com.jaribha.fragments.SettingsFragment;
import com.jaribha.fragments.TabletSettingsFragment;
import com.jaribha.interfaces.HomeBadgeCount;
import com.jaribha.interfaces.OnCreateTabsListener;
import com.jaribha.models.NavDrawerItem;
import com.jaribha.reside_menu.ResideMenu;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.FragUtils;
import com.jaribha.utility.SessionManager;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

public class HomeScreenActivity extends BaseAppCompatActivity implements View.OnClickListener, HomeBadgeCount, ConnectionCallbacks, OnConnectionFailedListener, OnCreateTabsListener {

    private String[] titlesTopList, titlesBottomList;

    private TypedArray navMenuIconsTopList, navMenuIconsBottomList;

    private ResideMenu resideMenu;

    private ExpandableHeightListView topListView, bottomListView;

    private MenuAdapter adapterTopMenuItem;

    private MenuAdapter adapterBottomMenuItem;

    private ArrayList<NavDrawerItem> navDrawerItems;

    private ArrayList<NavDrawerItem> navDrawerItemsBottom;

    private TextView tv_menu_title, tv_right, tv_logout, tv_settings, tv_user_name, tv_edit_profile;

    private ImageView iv_search, backImage, iv_filter, footerLine, menuImg, vImage, backArrow;

    private TextDrawable.IBuilder mDrawableBuilder;

    private BezelImageView img_user_image;

    private LinearLayout DrawerLayout, layout_edit_profile, mainContainer;

    private FrameLayout container;

    private GoogleApiClient mGoogleApiClient;

    private BezelImageView profileImage;

    private View view;
    private Intent intent;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home_screen);
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("com.menu"));
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        menuImg = (ImageView) findViewById(R.id.menuImg);
        if (menuImg != null) {
            menuImg.setOnClickListener(this);
        }

        DrawerLayout = (LinearLayout) findViewById(R.id.DrawerLayout);
        if (DrawerLayout != null) {
            DrawerLayout.setOnClickListener(this);
        }
        DrawerLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyBoard(v);
                return true;
            }
        });

        container = (FrameLayout) findViewById(R.id.container);

        if (container != null) {
            container.setOnClickListener(this);
        }

        iv_search = (ImageView) findViewById(R.id.iv_search);

        if (iv_search != null) {
            iv_search.setOnClickListener(this);
        }

        backImage = (ImageView) findViewById(R.id.backImage);

        backArrow = (ImageView) findViewById(R.id.backArrow);

        if (backArrow != null) {
            backArrow.setOnClickListener(this);
        }

        vImage = (ImageView) findViewById(R.id.vImage);
        iv_filter = (ImageView) findViewById(R.id.iv_filter);

        if (iv_filter != null) {
            iv_filter.setOnClickListener(this);
        }

        tv_menu_title = (TextView) findViewById(R.id.tv_menu_title);
        tv_right = (TextView) findViewById(R.id.tv_right);

        if (tv_right != null) {
            tv_right.setVisibility(View.GONE);
            tv_right.setOnClickListener(this);
        }

        iv_search.setVisibility(View.VISIBLE);
        vImage.setVisibility(View.GONE);
        vImage.setOnClickListener(this);

        img_user_image = (BezelImageView) findViewById(R.id.img_user_image);
        if (img_user_image != null) {
            img_user_image.setOnClickListener(this);
        }

        displayUserImage(img_user_image);
        ////////

        int width = Utils.getDeviceWidth(this);
        double ratio = ((float) (width)) / 100.0;
        width = (int) (ratio * 65);

        // attach to current activity;
        resideMenu = new ResideMenu(HomeScreenActivity.this.getActivity(), R.layout.side_menu, -1);
        resideMenu.setShadowVisible(false);
        resideMenu.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        resideMenu.setUse3D(false);
        resideMenu.setBackground(R.drawable.menubg);
        resideMenu.attachToActivity(this);
        resideMenu.setScaleValue(0.8f);

        setupMenu();

        if (getIntent().getExtras() != null) {
            if (getIntent().getIntExtra("pos", 0) != 0)
                DisplayVIEW(getIntent().getIntExtra("pos", 0));
            else {
                DisplayVIEW(FragUtils.HOME_FRAGMENT);
            }
        } else {
            DisplayVIEW(FragUtils.HOME_FRAGMENT);
        }

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {

            @Override
            public void onBackStackChanged() {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
                if (f != null) {
                    JaribhaPrefrence.setPref(getActivity(),Constants.FILTER_SEARCH,false);
                    JaribhaPrefrence.setPref(HomeScreenActivity.this, Constants.isFilterProject, false);
                    JaribhaPrefrence.setPref(getActivity(), Constants.FEATURE_ID_SEARCH, "");
                    JaribhaPrefrence.setPref(getActivity(), Constants.CATEGORY_NAME, getString(R.string.all_cat));
                    JaribhaPrefrence.setPref(getActivity(), Constants.FEATURE_NAME, getString(R.string.all_features));
                    JaribhaPrefrence.setPref(getActivity(), Constants.COUNTRY_NAME, getString(R.string.all_country));
                    JaribhaPrefrence.setPref(getActivity(), Constants.COUNTRY_ID, "");
                    JaribhaPrefrence.setPref(getActivity(), Constants.CATEGORY_ID, "");
                   //
                    // method to update Title And Drawer
                    updateTitleAndDrawer(f);
                }
            }
        });
    }

    // method to update title and drawer
    private void updateTitleAndDrawer(Fragment fragment) {
        displayLoginUserData();
        tv_right.setVisibility(View.GONE);
        vImage.setVisibility(View.GONE);

        String fragClassName = fragment.getClass().getName();
        if (fragClassName.equals(HomeFragment.class.getName())) {
            tv_menu_title.setVisibility(View.GONE);
            backArrow.setVisibility(View.GONE);
            backImage.setVisibility(View.VISIBLE);
            iv_search.setVisibility(View.VISIBLE);
            img_user_image.setVisibility(View.VISIBLE);
            iv_filter.setVisibility(View.GONE);
            menuImg.setVisibility(View.VISIBLE);
            tv_menu_title.setVisibility(View.GONE);
            setSelectedTopList(0);
            clearBackStack();
        } else if (fragClassName.equals(ProjectsWithFilterFragment.class.getName())) {
            iv_filter.setVisibility(View.VISIBLE);
            backImage.setVisibility(View.GONE);
            iv_search.setVisibility(View.VISIBLE);
            img_user_image.setVisibility(View.VISIBLE);
            menuImg.setVisibility(View.GONE);
            backArrow.setVisibility(View.VISIBLE);
            tv_menu_title.setVisibility(View.GONE);
            setSelectedTopList(1);
        } else if (fragClassName.equals(HowItWorksFragment.class.getName())) {
            iv_filter.setVisibility(View.GONE);
            backImage.setVisibility(View.GONE);
            backArrow.setVisibility(View.GONE);
            menuImg.setVisibility(View.VISIBLE);
            iv_search.setVisibility(View.GONE);
            img_user_image.setVisibility(View.VISIBLE);
            tv_menu_title.setVisibility(View.VISIBLE);
            tv_menu_title.setText(getString(R.string.how_it_work));
            setSelectedTopList(3);
        } else if (fragClassName.equals(HelpFragment.class.getName())) {
            iv_filter.setVisibility(View.GONE);
            backImage.setVisibility(View.GONE);
            backArrow.setVisibility(View.GONE);
            iv_search.setVisibility(View.GONE);
            menuImg.setVisibility(View.VISIBLE);
            img_user_image.setVisibility(View.VISIBLE);
            tv_menu_title.setVisibility(View.VISIBLE);
            tv_menu_title.setText(getString(R.string.help));
            setSelectedTopList(4);
        } else if (fragClassName.equals(MyHistoryFragment.class.getName())) {
            iv_filter.setVisibility(View.GONE);
            backImage.setVisibility(View.GONE);
            backArrow.setVisibility(View.GONE);
            menuImg.setVisibility(View.VISIBLE);
            backArrow.setVisibility(View.GONE);
            iv_search.setVisibility(View.GONE);
            img_user_image.setVisibility(View.VISIBLE);
            tv_menu_title.setVisibility(View.VISIBLE);
            tv_menu_title.setText(getString(R.string.history));
            setSelectedTopList(7);
        } else if (fragClassName.equals(MyMessagesFragment.class.getName())) {
            iv_filter.setVisibility(View.GONE);
            backImage.setVisibility(View.GONE);
            backArrow.setVisibility(View.GONE);
            iv_search.setVisibility(View.GONE);
            menuImg.setVisibility(View.VISIBLE);
            img_user_image.setVisibility(View.GONE);
            tv_menu_title.setVisibility(View.VISIBLE);
            tv_menu_title.setText(getString(R.string.messages));
            setSelectedTopList(8);
        } else if (fragClassName.equals(MyFavoritesFragment.class.getName())) {
            iv_filter.setVisibility(View.GONE);
            backImage.setVisibility(View.GONE);
            backArrow.setVisibility(View.GONE);
            iv_search.setVisibility(View.GONE);
            menuImg.setVisibility(View.VISIBLE);
            backArrow.setVisibility(View.GONE);
            img_user_image.setVisibility(View.VISIBLE);
            tv_menu_title.setVisibility(View.VISIBLE);
            menuImg.setVisibility(View.VISIBLE);
            tv_menu_title.setText(getString(R.string.favourites));
            setSelectedTopList(9);
        } else if (fragClassName.equals(RewardsFragment.class.getName())) {
            iv_filter.setVisibility(View.GONE);
            backImage.setVisibility(View.GONE);
            backArrow.setVisibility(View.GONE);
            iv_search.setVisibility(View.GONE);
            img_user_image.setVisibility(View.GONE);
            menuImg.setVisibility(View.VISIBLE);
            tv_menu_title.setVisibility(View.VISIBLE);
            tv_menu_title.setText(getString(R.string.rewards));
            setSelectedBottomList(0);
        } else if (fragClassName.equals(GuidelinesFragment.class.getName())) {
            iv_filter.setVisibility(View.GONE);
            backImage.setVisibility(View.GONE);
            backArrow.setVisibility(View.GONE);
            iv_search.setVisibility(View.GONE);
            menuImg.setVisibility(View.VISIBLE);
            img_user_image.setVisibility(View.VISIBLE);
            tv_menu_title.setVisibility(View.VISIBLE);
            tv_menu_title.setText(getString(R.string.guideline));
            setSelectedBottomList(1);
        } else if (fragClassName.equals(ContactusFragment.class.getName())) {
            iv_filter.setVisibility(View.GONE);
            backImage.setVisibility(View.GONE);
            backArrow.setVisibility(View.GONE);
            iv_search.setVisibility(View.GONE);
            menuImg.setVisibility(View.VISIBLE);
            img_user_image.setVisibility(View.GONE);
            tv_menu_title.setVisibility(View.VISIBLE);
            tv_menu_title.setText(getString(R.string.contact_us));
            setSelectedBottomList(2);
        } else if (fragClassName.equals(NewsLetterFragment.class.getName())) {
            iv_filter.setVisibility(View.GONE);
            backImage.setVisibility(View.GONE);
            backArrow.setVisibility(View.GONE);
            iv_search.setVisibility(View.GONE);
            menuImg.setVisibility(View.VISIBLE);
            img_user_image.setVisibility(View.GONE);
            tv_menu_title.setVisibility(View.VISIBLE);
            tv_menu_title.setText(getString(R.string.jaribha_weekly_newsletter));
            setSelectedBottomList(3);
        } else if (fragClassName.equals(EditProfileFragment.class.getName())) {
            tv_menu_title.setVisibility(View.VISIBLE);
            tv_menu_title.setText(getString(R.string.edit_profile));
            iv_filter.setVisibility(View.GONE);
            backArrow.setVisibility(View.GONE);
            backImage.setVisibility(View.GONE);
            iv_search.setVisibility(View.GONE);
            menuImg.setVisibility(View.VISIBLE);
            backArrow.setVisibility(View.GONE);
            img_user_image.setVisibility(View.GONE);
        } else if (fragClassName.equals(SettingsFragment.class.getName()) || fragClassName.equals(TabletSettingsFragment.class.getName())) {
            tv_menu_title.setVisibility(View.VISIBLE);
            tv_menu_title.setText(getString(R.string.settings));
            iv_filter.setVisibility(View.GONE);
            backArrow.setVisibility(View.GONE);
            backImage.setVisibility(View.GONE);
            iv_search.setVisibility(View.GONE);
            menuImg.setVisibility(View.VISIBLE);
            img_user_image.setVisibility(View.VISIBLE);
            setSelectedTopList(-1);
            tv_settings.setTextColor(ContextCompat.getColor(this, R.color.menu_text_color));
            tv_settings.setTypeface(null, Typeface.BOLD);
            tv_settings.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_setting_sel, 0, 0, 0);

        }
    }

    private void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }


    private void setupMenu() {

        int width = Utils.getDeviceWidth(this);
        double ratio = ((float) (width)) / 100.0;
        width = (int) (ratio * 65);

        resideMenu.setMenuListener(new ResideMenu.OnMenuListener() {
            @Override
            public void openMenu() {
                Utils.hideKeyboard(HomeScreenActivity.this);
                displayLoginUserData();
                tv_edit_profile.setText(getString(R.string.edit_profile));
            }

            @Override
            public void closeMenu() {

            }
        });

        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);

        //View view = resideMenu.getLeftMenuView();
        view = resideMenu.getLeftMenuView();

        mainContainer = (LinearLayout) view.findViewById(R.id.mainContainer);
        mainContainer.getLayoutParams().width = width;

        layout_edit_profile = (LinearLayout) view.findViewById(R.id.layout_edit_profile);
        layout_edit_profile.setOnClickListener(this);

        profileImage = (BezelImageView) view.findViewById(R.id.profileImage);

        tv_logout = (TextView) view.findViewById(R.id.tv_logout);
        tv_logout.setTypeface(null, Typeface.NORMAL);
        tv_logout.setTextColor(ContextCompat.getColor(this, R.color.white));
        tv_logout.setOnClickListener(this);

        tv_settings = (TextView) view.findViewById(R.id.tv_settings);
        tv_settings.setTypeface(null, Typeface.NORMAL);
        tv_settings.setTextColor(ContextCompat.getColor(this, R.color.white));
        tv_settings.setOnClickListener(this);
        tv_logout.setText(getString(R.string.logout));
        tv_settings.setText(getString(R.string.settings));
        tv_settings.setVisibility(View.VISIBLE);

        tv_logout.setVisibility(View.VISIBLE);

        tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);

        tv_edit_profile = (TextView) view.findViewById(R.id.tv_edit_profile);

        footerLine = (ImageView) view.findViewById(R.id.footerLine);

        displayLoginUserData();

        // tip list view
        topListView = (ExpandableHeightListView) view.findViewById(R.id.listView_top);
        topListView.setExpanded(true);
        // get top menu item adapter
        adapterTopMenuItem = new MenuAdapter(this, getTopMenuItem(), true);
        // set top menu item adapter
        topListView.setAdapter(adapterTopMenuItem);
        topListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setSelectedTopList(position);
                DisplayVIEW(position);
            }
        });

        // bottom list view
        bottomListView = (ExpandableHeightListView) view.findViewById(R.id.listView_bottom);
        bottomListView.setExpanded(true);
        // get bottom menu item adapter
        adapterBottomMenuItem = new MenuAdapter(this, getBottomMenuItem(), false);
        // set bottom menu item adapter
        bottomListView.setAdapter(adapterBottomMenuItem);
        bottomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                resideMenu.closeMenu();
                setSelectedBottomList(position);

                switch (position) {
                    case 0:
                        tv_menu_title.setVisibility(View.VISIBLE);
                        tv_menu_title.setText(getString(R.string.rewards));
                        replaceFragment(new RewardsFragment(), RewardsFragment.class.getName(), true);
                        break;

                    case 1:
                        tv_menu_title.setVisibility(View.VISIBLE);
                        tv_menu_title.setText(getString(R.string.guideline));
                        displayUserImage(img_user_image);
                        replaceFragment(new GuidelinesFragment(), GuidelinesFragment.class.getName(), true);
                        break;

                    case 2:
                        intent = new Intent(HomeScreenActivity.this, TermsActivity.class);
                        startActivity(intent);
                        break;

                    case 3:
                        intent = new Intent(HomeScreenActivity.this, PrivacyActivity.class);
                        startActivity(intent);
                        break;

                    case 4:
                        tv_menu_title.setVisibility(View.VISIBLE);
                        tv_menu_title.setText(getString(R.string.contact_us));
                        replaceFragment(new ContactusFragment(), ContactusFragment.class.getName(), true);
                        break;

                    case 5:
                        tv_menu_title.setVisibility(View.VISIBLE);
                        tv_menu_title.setText(getString(R.string.jaribha_weekly_newsletter));
                        replaceFragment(new NewsLetterFragment(), NewsLetterFragment.class.getName(), true);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (resideMenu != null && resideMenu.isOpened())
            resideMenu.closeMenu();

        Intent intent = new Intent(HomeScreenActivity.this, HomeScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void displayLoginUserData() {
        if (getUser() != null) { // if user logged in
            String text = getUser().name.trim();
            text = String.valueOf(text.charAt(0));
            mDrawableBuilder = TextDrawable.builder().beginConfig()
                    .withBorder(4)
                    .endConfig()
                    .round();
            TextDrawable drawable = mDrawableBuilder.build(text, Color.parseColor("#EA5D4C"));

            tv_user_name.setText(getUser().name);

            tv_settings.setVisibility(View.VISIBLE);

            tv_logout.setVisibility(View.VISIBLE);

            footerLine.setVisibility(View.VISIBLE);

            if (navDrawerItems != null && adapterTopMenuItem != null) {
                NavDrawerItem item2 = navDrawerItems.get(9);
                item2.setCount(JaribhaPrefrence.getPref(this, Constants.FAV_COUNT, 0) + "");
                navDrawerItems.set(9, item2);
                adapterTopMenuItem.notifyDataSetChanged();
            }

            if (TextUtils.isNullOrEmpty(getUser().pictureurl)) {
                // slider user image
                profileImage.setImageDrawable(drawable);
                // action bar user image
                img_user_image.setImageDrawable(drawable);
            } else {
                // slider user image
                displayImage(HomeScreenActivity.this, profileImage, getUser().pictureurl, drawable);
                // action bar user image
                displayImage(HomeScreenActivity.this, img_user_image, getUser().pictureurl, drawable);
            }
        } else { // if user not logged in
            tv_user_name.setText(getString(R.string.hello));

            tv_edit_profile.setText(getString(R.string.sign_in_to_jaribha));

            tv_settings.setVisibility(View.GONE);

            tv_logout.setVisibility(View.GONE);

            footerLine.setVisibility(View.GONE);
            // slider user image
            profileImage.setImageResource(R.drawable.user_pic);
            // action bar user image
            img_user_image.setImageResource(R.drawable.user_pic);
        }
    }

    public void DisplayVIEW(int position) {
        if (resideMenu != null && resideMenu.isOpened())
            resideMenu.closeMenu();

        switch (position) {
            case 0:
                Constants.isCommunity = false;
                tv_menu_title.setVisibility(View.GONE);
                tv_right.setVisibility(View.GONE);
                vImage.setVisibility(View.GONE);
                backImage.setVisibility(View.VISIBLE);
                iv_search.setVisibility(View.VISIBLE);
                img_user_image.setVisibility(View.VISIBLE);
                iv_filter.setVisibility(View.GONE);
                tv_menu_title.setVisibility(View.GONE);
                Fragment fragment = new HomeFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("tab_pos", 0);
                fragment.setArguments(bundle);
                displayView2(fragment);
                break;

            case 12:
                tv_menu_title.setVisibility(View.GONE);
                replaceFragment(new PaymentResponse(), PaymentResponse.class.getName(), true);
                break;

            case 1:
                JaribhaPrefrence.setPref(getActivity(), Constants.FEATURE_ID_SEARCH, "");
                JaribhaPrefrence.setPref(getActivity(), Constants.CATEGORY_NAME, getString(R.string.all_cat));
                JaribhaPrefrence.setPref(getActivity(), Constants.FEATURE_NAME, getString(R.string.all_features));
                JaribhaPrefrence.setPref(getActivity(), Constants.COUNTRY_NAME, getString(R.string.all_country));
                JaribhaPrefrence.setPref(getActivity(), Constants.COUNTRY_ID, "");
                JaribhaPrefrence.setPref(getActivity(), Constants.CATEGORY_ID, "");
               // JaribhaPrefrence.deleteKey(HomeScreenActivity.this, Constants.SEARCH);
                vImage.setVisibility(View.GONE);
                tv_right.setVisibility(View.GONE);
                iv_filter.setVisibility(View.VISIBLE);
                backImage.setVisibility(View.GONE);
                vImage.setVisibility(View.GONE);
                iv_search.setVisibility(View.VISIBLE);
                img_user_image.setVisibility(View.VISIBLE);
                tv_menu_title.setVisibility(View.GONE);
                JaribhaPrefrence.setPref(HomeScreenActivity.this,Constants.HOME_SEARCH,false);
                replaceFragment(new ProjectsWithFilterFragment(), ProjectsWithFilterFragment.class.getName(), true);
                break;

            case 2:
                //Start Project
                if (getUser() != null) {
                    Constants.firse = false;
                    Constants.second = false;
                    Constants.third = false;
                    vImage.setVisibility(View.GONE);
                    iv_filter.setVisibility(View.GONE);
                    img_user_image.setVisibility(View.VISIBLE);
                    // backImage.setVisibility(View.GONE);
                    // replaceFragment(new CreateProjectFragment(), CreateProjectFragment.class.getName(), true);
                    startActivity(new Intent(HomeScreenActivity.this, CreateProjectActivity.class).putExtra(Constants.DATA, 0));
                } else {
                    startActivity(new Intent(HomeScreenActivity.this, LoginScreenActivity.class));
                    //overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                }
                break;

            case 3:
                vImage.setVisibility(View.GONE);
                tv_right.setVisibility(View.GONE);
                iv_filter.setVisibility(View.GONE);
                backImage.setVisibility(View.GONE);
                iv_search.setVisibility(View.GONE);
                img_user_image.setVisibility(View.VISIBLE);
                tv_menu_title.setVisibility(View.VISIBLE);
                tv_menu_title.setText(getString(R.string.how_it_work));
                replaceFragment(new HowItWorksFragment(), HowItWorksFragment.class.getName(), true);
                break;

            case 4:
                vImage.setVisibility(View.GONE);
                tv_right.setVisibility(View.GONE);
                iv_filter.setVisibility(View.GONE);
                backImage.setVisibility(View.GONE);
                iv_search.setVisibility(View.GONE);
                img_user_image.setVisibility(View.VISIBLE);
                tv_menu_title.setVisibility(View.VISIBLE);
                tv_menu_title.setText(getString(R.string.help));
                replaceFragment(new HelpFragment(), HelpFragment.class.getName(), true);
                break;

            case 5:
                // Portfolio
                if (getUser() != null) {
                    vImage.setVisibility(View.GONE);
                    iv_filter.setVisibility(View.GONE);
                    startActivity(new Intent(HomeScreenActivity.this, MyPortfolioActivity.class));
                    //finish();
                } else {
                    startActivity(new Intent(HomeScreenActivity.this, LoginScreenActivity.class));
                    // overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                }
                break;

            case 6:
                Constants.isCommunity = true;
                tv_menu_title.setVisibility(View.GONE);
                iv_filter.setVisibility(View.GONE);
                tv_right.setVisibility(View.GONE);
                vImage.setVisibility(View.GONE);
                backImage.setVisibility(View.VISIBLE);
                iv_search.setVisibility(View.VISIBLE);
                img_user_image.setVisibility(View.VISIBLE);
                tv_menu_title.setVisibility(View.GONE);

                Fragment fragment1 = new HomeFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putInt("tab_pos", 2);
                fragment1.setArguments(bundle1);
                displayView2(fragment1);
                break;

            case 7:
                if (getUser() != null) {
                    vImage.setVisibility(View.GONE);
                    tv_right.setVisibility(View.GONE);
                    iv_filter.setVisibility(View.GONE);
                    backImage.setVisibility(View.GONE);
                    iv_search.setVisibility(View.GONE);
                    img_user_image.setVisibility(View.VISIBLE);
                    tv_menu_title.setVisibility(View.VISIBLE);
                    tv_menu_title.setText(getString(R.string.history));
                    replaceFragment(new MyHistoryFragment(), MyHistoryFragment.class.getName(), true);
                } else {
                    startActivity(new Intent(HomeScreenActivity.this, LoginScreenActivity.class));
                    // overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                }
                break;

            case 8:
                if (getUser() != null) {
                    vImage.setVisibility(View.GONE);
                    tv_right.setVisibility(View.GONE);
                    iv_filter.setVisibility(View.GONE);
                    backImage.setVisibility(View.GONE);
                    iv_search.setVisibility(View.GONE);
                    img_user_image.setVisibility(View.GONE);
                    tv_menu_title.setVisibility(View.VISIBLE);
                    tv_menu_title.setText(getString(R.string.messages));
                    replaceFragment(new MyMessagesFragment(), MyMessagesFragment.class.getName(), true);
                } else {
                    startActivity(new Intent(HomeScreenActivity.this, LoginScreenActivity.class));
                    // overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                }
                break;

            case 9:
                if (getUser() != null) {
                    vImage.setVisibility(View.GONE);
                    tv_right.setVisibility(View.GONE);
                    iv_filter.setVisibility(View.GONE);
                    backImage.setVisibility(View.GONE);
                    iv_search.setVisibility(View.GONE);
                    img_user_image.setVisibility(View.VISIBLE);
                    tv_menu_title.setVisibility(View.VISIBLE);
                    tv_menu_title.setText(getString(R.string.favourites));

                    if (getIntent().getIntExtra("pos", 0) != 0) {
                        replaceFragment(new MyFavoritesFragment(), MyFavoritesFragment.class.getName(), false);
                    } else {
                        replaceFragment(new MyFavoritesFragment(), MyFavoritesFragment.class.getName(), true);
                    }

                } else {
                    startActivity(new Intent(HomeScreenActivity.this, LoginScreenActivity.class));
                    // overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                }
                break;


            case 555:
                if (getUser() != null) {
                    vImage.setVisibility(View.GONE);
                    tv_menu_title.setVisibility(View.VISIBLE);
                    tv_menu_title.setText(getString(R.string.edit_profile));
                    iv_filter.setVisibility(View.GONE);

                    backImage.setVisibility(View.GONE);
                    iv_search.setVisibility(View.GONE);
                    img_user_image.setVisibility(View.GONE);

                    tv_logout.setTextColor(ContextCompat.getColor(this, R.color.white));
                    tv_logout.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_logout_sel, 0, 0, 0);

                    tv_settings.setTextColor(ContextCompat.getColor(this, R.color.white));
                    //tv_settings.setTypeface(null, Typeface.NORMAL);
                    tv_settings.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_setting, 0, 0, 0);

                    for (int i = 0; i < navDrawerItems.size(); i++) {
                        NavDrawerItem navDrawerItem = navDrawerItems.get(i);
                        navDrawerItem.setSelected(false);
                    }
                    adapterTopMenuItem.notifyDataSetChanged();

                    for (int i = 0; i < navDrawerItemsBottom.size(); i++) {
                        NavDrawerItem navDrawerItem = navDrawerItemsBottom.get(i);
                        navDrawerItem.setSelected(false);
                    }
                    adapterBottomMenuItem.notifyDataSetChanged();
                    replaceFragment(new EditProfileFragment(), EditProfileFragment.class.getName(), true);

                } else {
                    startActivity(new Intent(HomeScreenActivity.this, LoginScreenActivity.class));
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        if (tv_menu_title.getText().toString().equalsIgnoreCase(getString(R.string.settings))) {
            tv_menu_title.setVisibility(View.GONE);
        }

        if (JaribhaPrefrence.getPref(this, Constants.SETTING_LANGUAGE, false)) {
            JaribhaPrefrence.setPref(this, Constants.SETTING_LANGUAGE, false);
            tv_menu_title.setText(getString(R.string.settings));
        }
    }

    public void setSelectedTopList(int posSlideMenuOption) {
        for (int i = 0; i < navDrawerItems.size(); i++) {
            NavDrawerItem navDrawerItem = navDrawerItems.get(i);
            if (i == posSlideMenuOption) {
                navDrawerItem.setSelected(true);
            } else {
                navDrawerItem.setSelected(false);
            }
        }
        adapterTopMenuItem.notifyDataSetChanged();

        for (int i = 0; i < navDrawerItemsBottom.size(); i++) {
            NavDrawerItem navDrawerItem = navDrawerItemsBottom.get(i);
            navDrawerItem.setSelected(false);
        }
        adapterBottomMenuItem.notifyDataSetChanged();

        tv_logout.setTextColor(ContextCompat.getColor(this, R.color.white));
        // tv_logout.setTypeface(null, Typeface.NORMAL);
        tv_logout.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_logout, 0, 0, 0);

        tv_settings.setTextColor(ContextCompat.getColor(this, R.color.white));
        // tv_settings.setTypeface(null, Typeface.NORMAL);
        tv_settings.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_setting, 0, 0, 0);

    }

    public void setSelectedBottomList(int posSlideMenuOption) {
        for (int i = 0; i < navDrawerItems.size(); i++) {
            NavDrawerItem navDrawerItem = navDrawerItems.get(i);
            navDrawerItem.setSelected(false);
        }
        adapterTopMenuItem.notifyDataSetChanged();

        for (int i = 0; i < navDrawerItemsBottom.size(); i++) {
            NavDrawerItem navDrawerItem = navDrawerItemsBottom.get(i);
            if (i == posSlideMenuOption) {
                navDrawerItem.setSelected(true);
            } else {
                navDrawerItem.setSelected(false);
            }
        }
        adapterBottomMenuItem.notifyDataSetChanged();

        tv_logout.setTextColor(ContextCompat.getColor(this, R.color.white));
        //tv_logout.setTypeface(null, Typeface.NORMAL);
        tv_logout.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_logout, 0, 0, 0);

        tv_settings.setTextColor(ContextCompat.getColor(this, R.color.white));
        //tv_settings.setTypeface(null, Typeface.NORMAL);
        tv_settings.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_setting, 0, 0, 0);

        vImage.setVisibility(View.GONE);
        tv_right.setVisibility(View.GONE);
        iv_filter.setVisibility(View.GONE);
        backImage.setVisibility(View.GONE);
        iv_search.setVisibility(View.GONE);

        if (posSlideMenuOption != 3)
            img_user_image.setVisibility(View.VISIBLE);

    }

    public void displayView2(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        //fragmentTransaction.addToBackStack(fragment.getClass().getName());
        fragmentTransaction.commit();
    }

    public void replaceFragment(Fragment fragment, String tag, Boolean addStack) {
        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(tag, 0);

        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.container, fragment);
            //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            if (addStack) {
                ft.addToBackStack(tag);
            }
            ft.commit();
        } else {
            String fragClassName = fragment.getClass().getName();
            if (fragClassName.equalsIgnoreCase(HomeFragment.class.getName())) {
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.container, fragment);
                //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                if (addStack) {
                    ft.addToBackStack(tag);
                }
                ft.commit();
            }
        }
    }

    private ArrayList<NavDrawerItem> getTopMenuItem() {
        titlesTopList = getResources().getStringArray(R.array.nav_drawer_items_top);

        // nav drawer icons from resources
        navMenuIconsTopList = getResources().obtainTypedArray(R.array.nav_drawer_icons_top_selected);

        navDrawerItems = new ArrayList<>();

        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(titlesTopList[0], navMenuIconsTopList.getResourceId(0, -1), true));
        // Browse project
        navDrawerItems.add(new NavDrawerItem(titlesTopList[1], navMenuIconsTopList.getResourceId(1, -1)));
        // Start Project
        navDrawerItems.add(new NavDrawerItem(titlesTopList[2], navMenuIconsTopList.getResourceId(2, -1)));
        // How it works
        navDrawerItems.add(new NavDrawerItem(titlesTopList[3], navMenuIconsTopList.getResourceId(3, -1)));
        // Help
        navDrawerItems.add(new NavDrawerItem(titlesTopList[4], navMenuIconsTopList.getResourceId(4, -1)));
        // Portfolio
        navDrawerItems.add(new NavDrawerItem(titlesTopList[5], navMenuIconsTopList.getResourceId(5, -1)));
        // Communities
        navDrawerItems.add(new NavDrawerItem(titlesTopList[6], navMenuIconsTopList.getResourceId(6, -1)));
        // My History
        navDrawerItems.add(new NavDrawerItem(titlesTopList[7], navMenuIconsTopList.getResourceId(7, -1)));
        // Messages
        navDrawerItems.add(new NavDrawerItem(titlesTopList[8], navMenuIconsTopList.getResourceId(8, -1), true, "0"));
        // Favourite
        navDrawerItems.add(new NavDrawerItem(titlesTopList[9], navMenuIconsTopList.getResourceId(9, -1), true, "0"));

        // Recycle the typed array
        navMenuIconsTopList.recycle();

        return navDrawerItems;
    }

    private ArrayList<NavDrawerItem> getBottomMenuItem() {
        titlesBottomList = getResources().getStringArray(R.array.nav_drawer_items_bottom);

        // nav drawer icons from resources
        navMenuIconsBottomList = getResources().obtainTypedArray(R.array.nav_drawer_icons_bottom_selected);

        navDrawerItemsBottom = new ArrayList<>();

        // adding nav drawer items to array
        // Rewards
        navDrawerItemsBottom.add(new NavDrawerItem(titlesBottomList[0], navMenuIconsBottomList.getResourceId(0, -1)));
        // Guidelines
        navDrawerItemsBottom.add(new NavDrawerItem(titlesBottomList[1], navMenuIconsBottomList.getResourceId(1, -1)));
        // Terms and Conditions
        navDrawerItemsBottom.add(new NavDrawerItem(titlesBottomList[2], navMenuIconsBottomList.getResourceId(2, -1)));
        // Privacy Policy
        navDrawerItemsBottom.add(new NavDrawerItem(titlesBottomList[3], navMenuIconsBottomList.getResourceId(3, -1)));
        // Contact us
        navDrawerItemsBottom.add(new NavDrawerItem(titlesBottomList[4], navMenuIconsBottomList.getResourceId(4, -1)));
        // Subscribe ti newsletter
        navDrawerItemsBottom.add(new NavDrawerItem(titlesBottomList[5], navMenuIconsBottomList.getResourceId(5, -1)));

        // Recycle the typed array
        navMenuIconsBottomList.recycle();

        return navDrawerItemsBottom;
    }

    // What good method is to access resideMenuï¼Ÿ
    public ResideMenu getResideMenu() {
        return resideMenu;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
       // JaribhaPrefrence.setPref(this,Constants.FILTER_SEARCH,false);
        JaribhaPrefrence.deleteKey(HomeScreenActivity.this, Constants.SEARCH);
        JaribhaPrefrence.setPref(HomeScreenActivity.this,Constants.HOME_SEARCH,false);

        if (resideMenu != null && resideMenu.isOpened()) {
            resideMenu.closeMenu();
        } else if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            // handle the back press check BackStack Entry
//            super.onBackPressed();
            if (JaribhaPrefrence.getPref(HomeScreenActivity.this, Constants.PORT_EDIT, false)) {
                JaribhaPrefrence.setPref(HomeScreenActivity.this, Constants.PORT_EDIT, false);
                DisplayVIEW(0);
            } else {
                getSupportFragmentManager().popBackStack();
            }
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            showToast(getString(R.string.please_press_back_button_again_to_exit));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_user_image:
                if (resideMenu.isOpened())
                    resideMenu.closeMenu();

                if (getUser() != null) {
                    startActivity(new Intent(HomeScreenActivity.this, MyPortfolioActivity.class));
                } else {
                    startActivity(new Intent(HomeScreenActivity.this, LoginScreenActivity.class));
                    //overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                }
                break;

            case R.id.iv_filter:
                if (resideMenu.isOpened())
                    resideMenu.closeMenu();

                Intent filterIntent = new Intent(this, FilterActivity.class);
                startActivityForResult(filterIntent, 1001);
                break;

            case R.id.menuImg:
                if (resideMenu.isOpened()) {
                    resideMenu.closeMenu();
                } else {
                    resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                }
                break;

            case R.id.tv_right:
                if (resideMenu.isOpened())
                    resideMenu.closeMenu();

                if (tv_right.getText().equals(getString(R.string.edit))) {
                    tv_right.setVisibility(View.GONE);
                    iv_filter.setVisibility(View.GONE);
                    img_user_image.setVisibility(View.GONE);
//                    displayView(new EditProfileFragment(), "");
                    DisplayVIEW(FragUtils.EDIT_PROFILE_FRAGMENT);
                }
                break;

            case R.id.layout_edit_profile:
                if (resideMenu.isOpened())
                    resideMenu.closeMenu();

                if (getUser() != null) {
                    vImage.setVisibility(View.GONE);
                    tv_menu_title.setVisibility(View.VISIBLE);
                    tv_menu_title.setText(getString(R.string.edit_profile));
                    iv_filter.setVisibility(View.GONE);

                    backImage.setVisibility(View.GONE);
                    iv_search.setVisibility(View.GONE);
                    img_user_image.setVisibility(View.GONE);

                    tv_logout.setTextColor(ContextCompat.getColor(this, R.color.white));
                    //tv_logout.setTypeface(null, Typeface.NORMAL);
                    tv_logout.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_logout_sel, 0, 0, 0);

                    tv_settings.setTextColor(ContextCompat.getColor(this, R.color.white));
                    //tv_settings.setTypeface(null, Typeface.NORMAL);
                    tv_settings.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_setting, 0, 0, 0);

                    for (int i = 0; i < navDrawerItems.size(); i++) {
                        NavDrawerItem navDrawerItem = navDrawerItems.get(i);
                        navDrawerItem.setSelected(false);
                    }
                    adapterTopMenuItem.notifyDataSetChanged();

                    for (int i = 0; i < navDrawerItemsBottom.size(); i++) {
                        NavDrawerItem navDrawerItem = navDrawerItemsBottom.get(i);
                        navDrawerItem.setSelected(false);
                    }
                    adapterBottomMenuItem.notifyDataSetChanged();

//                    displayView(new EditProfileFragment(), "Edit Profile");
                    DisplayVIEW(FragUtils.EDIT_PROFILE_FRAGMENT);
                } else {
                    startActivity(new Intent(HomeScreenActivity.this, LoginScreenActivity.class));
                    //overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                }
                break;

            case R.id.tv_logout:
                if (resideMenu.isOpened())
                    resideMenu.closeMenu();

                tv_logout.setTextColor(ContextCompat.getColor(this, R.color.menu_text_color));
                //tv_logout.setTypeface(null, Typeface.BOLD);
                tv_logout.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_logout_sel, 0, 0, 0);

                tv_settings.setTextColor(ContextCompat.getColor(this, R.color.white));
                // tv_settings.setTypeface(null, Typeface.NORMAL);
                tv_settings.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_setting, 0, 0, 0);

                for (int i = 0; i < navDrawerItems.size(); i++) {
                    NavDrawerItem navDrawerItem = navDrawerItems.get(i);
                    navDrawerItem.setSelected(false);
                }
                adapterTopMenuItem.notifyDataSetChanged();

                for (int i = 0; i < navDrawerItemsBottom.size(); i++) {
                    NavDrawerItem navDrawerItem = navDrawerItemsBottom.get(i);
                    navDrawerItem.setSelected(false);
                }
                adapterBottomMenuItem.notifyDataSetChanged();

                dialogWithTwoButton();
                break;

            case R.id.tv_settings:
                if (resideMenu.isOpened())
                    resideMenu.closeMenu();

                iv_filter.setVisibility(View.GONE);
                tv_right.setVisibility(View.GONE);
                backImage.setVisibility(View.GONE);
                iv_search.setVisibility(View.GONE);
                vImage.setVisibility(View.GONE);
                img_user_image.setVisibility(View.VISIBLE);

                tv_logout.setTextColor(ContextCompat.getColor(this, R.color.white));
                // tv_logout.setTypeface(null, Typeface.NORMAL);
                tv_logout.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_logout, 0, 0, 0);

                tv_settings.setTextColor(ContextCompat.getColor(this, R.color.menu_text_color));
                //tv_settings.setTypeface(null, Typeface.BOLD);
                tv_settings.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_setting_sel, 0, 0, 0);

                for (int i = 0; i < navDrawerItems.size(); i++) {
                    NavDrawerItem navDrawerItem = navDrawerItems.get(i);
                    navDrawerItem.setSelected(false);
                }
                adapterTopMenuItem.notifyDataSetChanged();

                for (int i = 0; i < navDrawerItemsBottom.size(); i++) {
                    NavDrawerItem navDrawerItem = navDrawerItemsBottom.get(i);
                    navDrawerItem.setSelected(false);
                }
                adapterBottomMenuItem.notifyDataSetChanged();

                tv_menu_title.setVisibility(View.VISIBLE);
                tv_menu_title.setText(getString(R.string.settings));

                boolean tabletSize = getActivity().getResources().getBoolean(R.bool.isTablet);
                if (!tabletSize) {
                    replaceFragment(new SettingsFragment(), SettingsFragment.class.getName(), true);

                } else {
                    replaceFragment(new TabletSettingsFragment(), TabletSettingsFragment.class.getName(), true);
                }
                break;

            case R.id.vImage:
                if (resideMenu.isOpened())
                    resideMenu.closeMenu();

                Intent intent = new Intent(getActivity(), TutorialVideoFromHomeActivity.class);
                startActivity(intent);
                break;

            case R.id.iv_search:
                initiatePopupWindow();
                break;

            case R.id.backArrow:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.isCommunity = false;
        setupMenu();
        NotificationManager mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.cancelAll();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 || requestCode == 2001) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent("com.load_projects");
                LocalBroadcastManager.getInstance(HomeScreenActivity.this).sendBroadcast(intent);
            }
        }

    }

    public void dialogWithTwoButton() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_with_two_btn);
        dialog.setCanceledOnTouchOutside(true);

        Button btnYes = (Button) dialog.findViewById(R.id.btn_yes);
        Button btnNO = (Button) dialog.findViewById(R.id.btn_no);

        ImageView imageView = (ImageView) dialog.findViewById(R.id.img_dialog);
        imageView.setImageResource(R.drawable.logo);

        TextView title = (TextView) dialog.findViewById(R.id.tv_dialog_title);
        TextView subTitle = (TextView) dialog.findViewById(R.id.tv_dialog_subtitle);

        title.setText(getResources().getString(R.string.logout));
        subTitle.setText(getResources().getString(R.string.do_you_want_to_logout));

        LinearLayout dialog_main_container = (LinearLayout) dialog.findViewById(R.id.dialog_main_container);

        if (isTablet()) {
            dialog_main_container.getLayoutParams().width = (Utils.getDeviceWidth(this) * 40) / 100;
        }/* else {
            dialog_main_container.getLayoutParams().width = (Utils.getDeviceWidth(this) * 80) / 100;
        }
*/
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (isInternetConnected()) {
                    logOut();
                } else {
                    showNetworkDialog();
                }
            }
        });

        btnNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void logOut() {
        try {
            showLoading();

            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);
            projectJson.put("user_id", getUser().id);
            projectJson.put("user_token", getUser().user_token);

            LogOutAPI mAuthTask = new LogOutAPI(projectJson);
            mAuthTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setTabCount(String fav, String msg) {

        NavDrawerItem item1 = navDrawerItems.get(8);
        item1.setCount(msg);

        NavDrawerItem item2 = navDrawerItems.get(9);
        item2.setCount(fav);

        navDrawerItems.set(8, item1);
        navDrawerItems.set(9, item2);
        adapterTopMenuItem.notifyDataSetChanged();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void changeTab(int pos) {

        CreateProjectFragment createProjectFragment = new CreateProjectFragment();

        if (!isTablet()) {
            createProjectFragment.displayTab(pos);
        } else {
            createProjectFragment.displayFragment(pos);
        }

    }


    public class LogOutAPI extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        LogOutAPI(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.LOG_OUT, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {

            if (responce != null) {
                try {
                    if (JaribhaPrefrence.getPref(HomeScreenActivity.this, Constants.SOCIAL_TYPE, "").equals("facebook")) {
                        LoginManager.getInstance().logOut();
                        clearData();
                    } else if (JaribhaPrefrence.getPref(HomeScreenActivity.this, Constants.SOCIAL_TYPE, "").equals("google")) {
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                new ResultCallback<com.google.android.gms.common.api.Status>() {
                                    @Override
                                    public void onResult(@NonNull com.google.android.gms.common.api.Status status) {
                                        hideLoading();
                                        clearData();
                                    }
                                });
                    } else {
                        hideLoading();
                        clearData();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    showServerDialogDialog();
                }
            } else {
                showServerDialogDialog();
            }
        }

        @Override
        protected void onCancelled() {

            hideLoading();
        }
    }

    public void clearData() {
        //JaribhaPrefrence.deletePrefs(this);
        SessionManager sessionManager = SessionManager.getInstance(HomeScreenActivity.this);
        sessionManager.deleteUser();
        JaribhaPrefrence.deleteKey(this,Constants.isSocial);
        JaribhaPrefrence.deleteKey(this,Constants.PASSWORD_CHECK);
        Intent loginIntent = new Intent(HomeScreenActivity.this, LoginScreenActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
        //overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        finish();
    }

    private PopupWindow pwindo;

    private void initiatePopupWindow() {
        try {
            LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.search_popup,
                    (ViewGroup) findViewById(R.id.popupElement));
            pwindo = new PopupWindow(this);
            pwindo.setContentView(layout);
            pwindo.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            pwindo.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            pwindo.setBackgroundDrawable(new BitmapDrawable());
            pwindo.setFocusable(true);
            pwindo.setOutsideTouchable(false);
            pwindo.showAtLocation(layout, Gravity.TOP, 0, 180);
            // Removes default background.
            final EditText btnClosePopup = (EditText) layout.findViewById(R.id.searchEt);


            btnClosePopup.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.searchproject || id == EditorInfo.IME_NULL) {
                        String searchStr = btnClosePopup.getText().toString().trim();
                        hideKeyBoard(textView);
                        if (TextUtils.isNullOrEmpty(searchStr)) {
                            showToast(getString(R.string.error_field_required));
                        } else {
                            pwindo.dismiss();
                            Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
                            JaribhaPrefrence.setPref(HomeScreenActivity.this, Constants.SEARCH, searchStr);
                            if (f.getClass().getName().equals(ProjectsWithFilterFragment.class.getName())) {
                                Intent intent = new Intent("com.load_projects");
                                LocalBroadcastManager.getInstance(HomeScreenActivity.this).sendBroadcast(intent);
                            } else {
                                JaribhaPrefrence.setPref(HomeScreenActivity.this,Constants.HOME_SEARCH,true);
                                DisplayVIEW(1);
                            }

                        }
                        return true;
                    }
                    return false;
                }
            });

            btnClosePopup.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        hideKeyBoard(v);
                }
            });

            final ImageView searchIv = (ImageView) layout.findViewById(R.id.searchIv);

            searchIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String searchStr = btnClosePopup.getText().toString().trim();
                    hideKeyBoard(searchIv);
                    if (TextUtils.isNullOrEmpty(searchStr)) {
                        showToast(getString(R.string.error_field_required));
                    } else {
                        pwindo.dismiss();
                        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
                        JaribhaPrefrence.setPref(HomeScreenActivity.this, Constants.SEARCH, searchStr);
                        if (f.getClass().getName().equals(ProjectsWithFilterFragment.class.getName())) {
                            Intent intent = new Intent("com.load_projects");
                            LocalBroadcastManager.getInstance(HomeScreenActivity.this).sendBroadcast(intent);
                        } else {
                            JaribhaPrefrence.setPref(HomeScreenActivity.this,Constants.HOME_SEARCH,true);
                            DisplayVIEW(1);
                        }

                    }

                }
            });

            pwindo.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                    );
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Utils.hideKeyboard(this);
        return false;
    }

    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (intent.getAction().equals("com.menu")) {
                tv_menu_title.setText(getString(R.string.settings));
                setupMenu();
            }
        }
    };

    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}
