package com.angelosoft.angelo_romel.weatherquote;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.widget.ImageView;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by angelo_romel on 06/06/2016.
 */
public class SunSpinProgressDialog extends Dialog {
    public ImageView imageView;
    AnimationDrawable animation;


    public SunSpinProgressDialog(Context context) {
        super(context, R.style.AnimatedProgressDialog);
        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
        windowManager.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(windowManager);
        setTitle(null);
        setCancelable(true);
        setOnCancelListener(null);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
        //        WindowManager.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
        imageView = new ImageView(context);
        imageView.setBackgroundResource(R.drawable.progress_animation);
        linearLayout.addView(imageView, params);
        addContentView(linearLayout, params);
        animation = (AnimationDrawable) imageView.getBackground();
    }

    @Override
    public void show() {
        super.show();
        animation.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        animation.stop();
    }

}
