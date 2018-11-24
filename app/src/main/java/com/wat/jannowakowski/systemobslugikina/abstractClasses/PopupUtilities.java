package com.wat.jannowakowski.systemobslugikina.abstractClasses;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wat.jannowakowski.systemobslugikina.R;



public abstract class PopupUtilities {

    public static void showMessagePopup(final Context context, View anchor, LayoutInflater inflater, int actionCode){

        String title = "";
        String description = "";
        boolean updateActive = false;
/*
        if(actionCode == presenter.maintenance){

        }else
        if(actionCode == presenter.updateRequired){

        }else
        if(actionCode == presenter.versionUpToDate){

        }*/


        final View popupView = inflater.inflate(R.layout.message_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        // show the popup window
        TextView messageTitle = popupView.findViewById(R.id.message_title);
        TextView messageDescription = popupView.findViewById(R.id.message_description);

        Button updateButton = popupView.findViewById(R.id.update_button);
        Button closeButton = popupView.findViewById(R.id.close_button);

        messageTitle.setText(title);
        messageDescription.setText(description);

        popupWindow.showAtLocation(anchor, Gravity.CENTER, 0, 0);
        dimBehind(popupWindow);

        if(updateActive){
            updateButton.setVisibility(View.VISIBLE);
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateButtonAction(context);
                }
            });
        }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }


    private static void updateButtonAction(Context context){
        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void dimBehind(PopupWindow popupWindow) {
        View container = popupWindow.getContentView().getRootView();
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.7f;
        wm.updateViewLayout(container, p);
    }

}
