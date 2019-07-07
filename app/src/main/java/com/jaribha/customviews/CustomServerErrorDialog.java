package com.jaribha.customviews;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.utility.Utils;

public class CustomServerErrorDialog extends Dialog implements View.OnClickListener {

    public Context c;

    public Dialog d;

    public Button btn_dialog;

    public TextView tv_dialog_title, tv_dialog_subtitle;

    public ImageView img_dialog;

    public LinearLayout dialog_main_container;

    public boolean isTab = false;

    public CustomServerErrorDialog(Context a, boolean tab) {
        super(a);
        this.c = a;
        this.isTab = tab;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.server_error_dialog_layout);

        btn_dialog = (Button) findViewById(R.id.btn_dialog);
        btn_dialog.setOnClickListener(this);

        tv_dialog_subtitle = (TextView) findViewById(R.id.tv_dialog_subtitle);

        tv_dialog_title = (TextView) findViewById(R.id.tv_dialog_title);

        img_dialog = (ImageView) findViewById(R.id.img_dialog);

        dialog_main_container = (LinearLayout) findViewById(R.id.dialog_main_container);

        if (isTab) {
            dialog_main_container.getLayoutParams().width = (Utils.getDeviceWidth(c) * 40) / 100;
        } /*else {
            dialog_main_container.getLayoutParams().width = (Utils.getDeviceWidth(c) * 80) / 100;
        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dialog:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
