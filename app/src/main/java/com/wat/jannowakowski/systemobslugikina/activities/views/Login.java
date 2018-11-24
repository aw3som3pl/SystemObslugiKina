package com.wat.jannowakowski.systemobslugikina.activities.views;

import com.wat.jannowakowski.systemobslugikina.abstractClasses.PopupUtilities;
import com.wat.jannowakowski.systemobslugikina.activities.presenters.LoginPresenter;
import com.wat.jannowakowski.systemobslugikina.global.CurrentAppSession;
import com.wat.jannowakowski.systemobslugikina.global.InternetConnectionListener;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.wat.jannowakowski.systemobslugikina.R;

public class Login extends AppCompatActivity implements LoginPresenter.View {

    private Activity thisActivity;
    private Button btnRegister,btnLogin;
    private LoginPresenter presenter;
    private EditText email, password;
    private ProgressBar progressBar;

    private LinearLayout mainLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InternetConnectionListener.getINSTANCE();   //inicjalizacja singletona odpowiedzialnego za sprawdzanie dostępności internetu w kontekście całej aplikacji
        InternetConnectionListener.setContext(getApplicationContext());

        CurrentAppSession.getINSTANCE();        //inicjalizacja singletona odpowiedzialnego za przetrzymywanie informacji o sesji aplikacji

        thisActivity = this;
        presenter = new LoginPresenter(this);

        presenter.checkVersionCoherence(presenter.getCurrentVersionCode(thisActivity));


        mainLayout = findViewById(R.id.main_layout);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.commenceUserLogin(thisActivity, email.getText().toString(), password.getText().toString());
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showLoadingIndicator(){
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingIndicator(){
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void displayAutoLoginDisabledToast(){
        Toast.makeText(Login.this, R.string.notice_autologin_disabled, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayInternetConnectionErrorToast(){
        Toast.makeText(Login.this, R.string.error_no_internet_connection, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayInvalidCredentialsError(){
        Toast.makeText(Login.this, R.string.error_invalid_credentials, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displaySystemStatusMessagePopup(int actionCode){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        PopupUtilities.showMessagePopup(thisActivity.getBaseContext(),mainLayout,inflater,actionCode);
    }

    @Override
    public void navigateToCustomerMenu(){
        startActivity(new Intent(Login.this, CustomerMenu.class));
        finish();
    }

    @Override
    public void navigateToStaffMenu(){
        startActivity(new Intent(Login.this, StaffMenu.class));
        finish();
    }

    @Override
    public void navigateToSignupForm(){
        startActivity(new Intent(Login.this, Signup.class));
        finish();
    }




}

