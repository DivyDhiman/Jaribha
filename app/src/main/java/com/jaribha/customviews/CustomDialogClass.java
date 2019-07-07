package com.jaribha.customviews;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.activity.LoginScreenActivity;
import com.jaribha.utility.SessionManager;
import com.jaribha.utility.Utils;

public class CustomDialogClass extends Dialog implements View.OnClickListener {

    public Activity c;

    public Dialog d;

    public Button btn_dialog;

    public TextView tv_dialog_title, tv_dialog_subtitle;

    public ImageView img_dialog;

    public int dialogResourceID;

    public int buttonResourceID;

    public String dialogTitle;

    public String dialogSubTitle, dialogBtnText;

    public LinearLayout dialog_main_container;

    public boolean isTab = false;

    public CustomDialogClass(Activity a, int resourceID, String title, String subTitle, String btnText, int btnResourceId, boolean tab) {
        super(a);
        this.c = a;
        this.dialogResourceID = resourceID;
        this.dialogTitle = title;
        this.isTab = tab;
        this.dialogSubTitle = subTitle;
        this.dialogBtnText = btnText;
        this.buttonResourceID = btnResourceId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_layout);

        btn_dialog = (Button) findViewById(R.id.btn_dialog);
        btn_dialog.setBackgroundResource(buttonResourceID);
        btn_dialog.setText(dialogBtnText);
        btn_dialog.setOnClickListener(this);

        tv_dialog_subtitle = (TextView) findViewById(R.id.tv_dialog_subtitle);
        tv_dialog_subtitle.setText(dialogSubTitle);

        tv_dialog_title = (TextView) findViewById(R.id.tv_dialog_title);
        tv_dialog_title.setText(dialogTitle);

        img_dialog = (ImageView) findViewById(R.id.img_dialog);
        img_dialog.setImageResource(dialogResourceID);

        dialog_main_container = (LinearLayout) findViewById(R.id.dialog_main_container);

        if (isTab) {
            dialog_main_container.getLayoutParams().width = (Utils.getDeviceWidth(c) * 40) / 100;
        }/*else{
            dialog_main_container.getLayoutParams().width = (Utils.getDeviceWidth(c) * 80) / 100;
        }*/

        //dialog_main_container.getLayoutParams().width = (Utils.getDeviceWidth(c) * 80) / 100;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dialog:
                if (dialogBtnText.equalsIgnoreCase("Logout")) {
                    SessionManager sessionManager = SessionManager.getInstance(c);
                    sessionManager.deleteUser();
                    Intent loginIntent = new Intent(c, LoginScreenActivity.class);
                    c.startActivity(loginIntent);
                    //c.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    c.finish();
                }
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
