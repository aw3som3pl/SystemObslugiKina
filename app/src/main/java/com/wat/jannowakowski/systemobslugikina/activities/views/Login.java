package com.wat.jannowakowski.systemobslugikina.activities.views;

import com.wat.jannowakowski.systemobslugikina.abstractClasses.PopupUtilities;
import com.wat.jannowakowski.systemobslugikina.activities.models.User;
import com.wat.jannowakowski.systemobslugikina.global.InternetConnectionListener;
import com.wat.jannowakowski.systemobslugikina.interfaces.*;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wat.jannowakowski.systemobslugikina.R;

public class Login extends AppCompatActivity {


    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private boolean backPressToExit = false;
    private DatabaseReference version_controll_node;

    private InternetConnectionListener mInternetConnectionActiveListener;

    public static User loggedin_user;

    private boolean manualLoginEnabled = false;

    private boolean maintenanceFlagState = false;

    private boolean versionErrorFlagState = false;

    public static int versionNumber;
    public static int maintenanceFlag;
    public static String maintenanceMessage;

    OnVersionCheck versionCheckResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        version_controll_node = mDatabase.child("VersionControll");
        DatabaseReference users_node_ref = mDatabase.child("Users");
        setContentView(R.layout.activity_login);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnReset = findViewById(R.id.btn_reset_password);

        mInternetConnectionActiveListener = new InternetConnectionListener(getApplicationContext());

        setOnVersionCheckedListener(new OnVersionCheck() {
            @Override
            public void onVersionChecked(int val) {
                if(val == 0){
                    checkAutoLogin();
                }
                if(val == 1) {
                    versionErrorFlagState = true;
                    show_message_popup(getString(R.string.notice_new_version_available), getString(R.string.notice_new_version_available_description), true);
                }
                if(val == 2) {
                    maintenanceFlagState = true;
                    show_message_popup(getString(R.string.notice_maintenance),maintenanceMessage,false);
                }
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manualLoginEnabled) {
                    if (mInternetConnectionActiveListener.isConnectingToInternet()) {

                        final String email = inputEmail.getText().toString();
                        final String password = inputPassword.getText().toString();
                        manualLoginEnabled = false;

                        if (TextUtils.isEmpty(email)) {
                            Toast.makeText(getApplicationContext(), R.string.notice_enter_email, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(password)) {
                            Toast.makeText(getApplicationContext(), getString(R.string.notice_enter_password), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        progressBar.setVisibility(View.VISIBLE);

                        //authenticate user
                        auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.

                                        if (!task.isSuccessful()) {
                                            // there was an error
                                            if (password.length() < 6) {
                                                inputPassword.setError(getString(R.string.error_short_password));
                                            } else {
                                                Toast.makeText(Login.this, R.string.error_invalid_credentials, Toast.LENGTH_LONG).show();
                                            }
                                            manualLoginEnabled = true;
                                        } else {
                                            loggedin_user = new User(auth.getCurrentUser());
                                            loggedin_user.setUserDataLoadedListener(new OnUserDataReload() {
                                                @Override
                                                public void onUserDataReloaded(int val) {
                                                    if (val == 2) {
                                                        progressBar.setVisibility(View.GONE);
                                                        startActivity(new Intent(Login.this, Signup.class));
                                                    }
                                                    if (val == 0 && loggedin_user.getRole()==0) {
                                                        progressBar.setVisibility(View.GONE);
                                                        startActivity(new Intent(Login.this, CustomerMenu.class));
                                                    }
                                                    else {
                                                        progressBar.setVisibility(View.GONE);
                                                        startActivity(new Intent(Login.this, StaffMenu.class));
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(v.getContext(), R.string.error_no_internet_connection, Toast.LENGTH_SHORT).show();
                    }
                }else
                if(versionErrorFlagState){
                    manualLoginEnabled = false;
                    show_message_popup(getString(R.string.notice_new_version_available),getString(R.string.notice_new_version_available_description),true);
                }else
                if(maintenanceFlagState) {
                    manualLoginEnabled = false;
                    show_message_popup(getString(R.string.notice_maintenance), maintenanceMessage, false);
                }
            }
        });

    }
    @Override
    public void onResume(){
        super.onResume();

        inputPassword.setText("");
        check_current_version(get_current_version());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mInternetConnectionActiveListener = null;
    }

    private void check_current_version(final int appVersion){

        version_controll_node.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                versionNumber = Integer.parseInt(dataSnapshot.child("currentVersion").getValue().toString());
                maintenanceFlag = Integer.parseInt(dataSnapshot.child("maintenanceFlag").getValue().toString());
                maintenanceMessage = dataSnapshot.child("maintenanceMsg").getValue().toString();

                if(versionNumber > appVersion){
                    versionCheckResult.onVersionChecked(1);
                }else
                if(maintenanceFlag == 1){
                    versionCheckResult.onVersionChecked(2);
                }else
                    versionCheckResult.onVersionChecked(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int get_current_version(){
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int versionCode = pInfo.versionCode;
            Log.d("MyApp", "Version Name : "+version + "\n Version Code : "+versionCode);
            return versionCode;
        }catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.d("MyApp", "PackageManager Catch : "+e.toString());
        }
        return 0;
    }

    private void checkAutoLogin(){

        if (auth.getCurrentUser() != null && mInternetConnectionActiveListener.isConnectingToInternet()) {
            progressBar.setVisibility(View.VISIBLE);
            loggedin_user = new User(auth.getCurrentUser());

            loggedin_user.setUserDataLoadedListener(new OnUserDataReload() {
                @Override
                public void onUserDataReloaded(int val) {
                    if (val == 0 && loggedin_user.getRole()==0) {
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(Login.this, CustomerMenu.class));
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(Login.this, StaffMenu.class));
                    }
                }
            });
        }
        else {
            if(versionErrorFlagState){
                manualLoginEnabled = false;
                show_message_popup(getString(R.string.notice_new_version_available),getString(R.string.notice_new_version_available_description),true);
            }else
            if(maintenanceFlagState) {
                manualLoginEnabled = false;
                show_message_popup(getString(R.string.notice_maintenance), maintenanceMessage, false);
            }else {
                manualLoginEnabled = true;
                Toast.makeText(this.getBaseContext(), R.string.notice_autologin_disabled, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void show_message_popup(String title,String description,boolean updateActive){
        LinearLayout mainLayout = findViewById(R.id.main_layout);

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
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

        popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
        PopupUtilities.dimBehind(popupWindow);

        if(updateActive){
            updateButton.setVisibility(View.VISIBLE);
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateButtonAction();
                }
            });
        }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                finishAndRemoveTask();
            }
        });
    }

    private void updateButtonAction(){
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void setOnVersionCheckedListener(OnVersionCheck versionCheckValue){
        this.versionCheckResult = versionCheckValue;

    }
}

