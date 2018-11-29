package com.wat.jannowakowski.systemobslugikina.activities.presenters;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
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
import com.wat.jannowakowski.systemobslugikina.activities.models.User;
import com.wat.jannowakowski.systemobslugikina.global.CurrentAppSession;
import com.wat.jannowakowski.systemobslugikina.global.InternetConnectionListener;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnUserDataReload;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnVersionCheck;


public class LoginPresenter {

    private FirebaseAuth auth;
    private DatabaseReference versionControlDbNodeRef;
    private OnVersionCheck versionCheckResult = null;
    private View view;
    private int versionNumber;
    private int maintenanceFlag;
    private boolean manualLoginEnabled = false;

    private User user;

    public LoginPresenter(View v) {
        this.view = v;


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        versionControlDbNodeRef = mDatabase.child("VersionControll");
        DatabaseReference users_node_ref = mDatabase.child("Users");
        auth = FirebaseAuth.getInstance();

        CurrentAppSession.getINSTANCE().setCurrentUserAuth(auth);

        FirebaseUser firebaseUser = auth.getCurrentUser();

        setOnVersionCheckedListener(new OnVersionCheck() {
            @Override
            public void onVersionChecked(int action) {
                if (action == CurrentAppSession.getVersionUpToDateCode()) {
                    tryAutoLogin();
                }
                if (action == CurrentAppSession.getUpdateRequiredCode()) {
                    view.displaySystemStatusMessagePopup(action);
                }
                if (action == CurrentAppSession.getMaintenanceCode()) {
                    view.displaySystemStatusMessagePopup(action);
                }
            }
        });

    }

    public void commenceUserLogin(Activity activity, final String email, final String password) {
        if (manualLoginEnabled) {
            if (InternetConnectionListener.getINSTANCE().isConnectedToInternet()) {
                manualLoginEnabled = false;

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(activity.getBaseContext(), R.string.notice_enter_email, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(activity.getBaseContext(), R.string.notice_enter_password, Toast.LENGTH_SHORT).show();
                    return;
                }

                view.showLoadingIndicator();

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (!task.isSuccessful()) {
                                    view.displayInvalidCredentialsError();
                                } else {
                                    user = new User(auth.getCurrentUser());
                                    user.setUserDataLoadedListener(new OnUserDataReload() {
                                        @Override
                                        public void onUserDataReloaded(int action) {
                                            if (action == CurrentAppSession.getCustomerLoginCode()) {
                                                CurrentAppSession.getINSTANCE().setCurrentUser(user);
                                                view.hideLoadingIndicator();
                                                view.navigateToCustomerMenu();
                                            } else {
                                                CurrentAppSession.getINSTANCE().setCurrentUser(user);
                                                view.hideLoadingIndicator();
                                                view.navigateToStaffMenu();
                                            }
                                        }
                                    });
                                }
                            }
                        });
            } else {
                view.displayInternetConnectionErrorToast();
            }
        }
        else
            view.checkVersion();
    }


    public void checkVersionCoherence(final int appVersion){

        versionControlDbNodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                versionNumber = Integer.parseInt(dataSnapshot.child("currentVersion").getValue().toString());
                maintenanceFlag = Integer.parseInt(dataSnapshot.child("maintenanceFlag").getValue().toString());

                if(versionNumber > appVersion){
                    versionCheckResult.onVersionChecked(CurrentAppSession.getUpdateRequiredCode());
                }else
                if(maintenanceFlag == CurrentAppSession.getMaintenanceEnabledValue()){
                    versionCheckResult.onVersionChecked(CurrentAppSession.getMaintenanceCode());
                }else
                    versionCheckResult.onVersionChecked(CurrentAppSession.getVersionUpToDateCode());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public int getCurrentVersionCode(Context context){
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
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

    private void tryAutoLogin(){

        if(InternetConnectionListener.getINSTANCE().isConnectedToInternet()) {
            if (auth.getCurrentUser() != null) {        //jeśli użytkownik był uprzednio zalogowany oraz się nie wylogował (auth z pamięci aplikacji)
                view.showLoadingIndicator();
                user = new User(auth.getCurrentUser());

                user.setUserDataLoadedListener(new OnUserDataReload() {
                    @Override
                    public void onUserDataReloaded(int val) {
                        if (val == 0 && user.getRole() == 0) {
                            CurrentAppSession.getINSTANCE().setCurrentUser(user);
                            view.hideLoadingIndicator();
                            view.navigateToCustomerMenu();
                        } else {
                            CurrentAppSession.getINSTANCE().setCurrentUser(user);
                            view.hideLoadingIndicator();
                            view.navigateToStaffMenu();
                        }
                    }
                });
            }
            else {
                view.displayAutoLoginDisabledToast();
                manualLoginEnabled = true;
            }
        }
        else
            view.displayInternetConnectionErrorToast();
    }


    private void setOnVersionCheckedListener(OnVersionCheck versionCheckValue){
        this.versionCheckResult = versionCheckValue;

    }

    public interface View{
        void checkVersion();
        void displayInvalidCredentialsError();
        void displayInternetConnectionErrorToast();
        void displayAutoLoginDisabledToast();
        void showLoadingIndicator();
        void hideLoadingIndicator();
        void navigateToSignupForm();
        void navigateToCustomerMenu();
        void navigateToStaffMenu();
        void displaySystemStatusMessagePopup(int actionCode);
    }


}
