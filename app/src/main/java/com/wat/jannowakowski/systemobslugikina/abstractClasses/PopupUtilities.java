package com.wat.jannowakowski.systemobslugikina.abstractClasses;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wat.jannowakowski.systemobslugikina.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Jan Nowakowski on 24.11.2018.
 */

public abstract class PopupUtilities {

    public static void show_message_popup(View anchor, LayoutInflater inflater, String title, String description, boolean updateActive){

        // inflate the layout of the popup window

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
