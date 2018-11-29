package com.wat.jannowakowski.systemobslugikina.activities.views;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.wat.jannowakowski.systemobslugikina.R;
import com.wat.jannowakowski.systemobslugikina.activities.presenters.SignupPresenter;

public class Signup extends AppCompatActivity implements SignupPresenter.View {

    private Activity thisActivity;

    private CheckBox userConsentCheckBox;
    private Button registerButton;
    private EditText nameField,surnameField,emailField,passwordField,confirmPasswordField;
    private Spinner discountTypeSpinner;
    private ProgressBar progressBar;
    private SignupPresenter presenter;

    private boolean consentGiven = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        thisActivity = this;
        presenter = new SignupPresenter(this);
        presenter.setSignupActivityRef(thisActivity);


        userConsentCheckBox = findViewById(R.id.user_consent);
        registerButton = findViewById(R.id.register);
        nameField = findViewById(R.id.name);
        surnameField = findViewById(R.id.surname);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        confirmPasswordField = findViewById(R.id.password_confirm);
        discountTypeSpinner = findViewById(R.id.discount_type);
        progressBar = findViewById(R.id.progress_bar);

        userConsentCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(consentGiven = isChecked){
                    presenter.setEnteredName(nameField.getText().toString());
                    presenter.setEnteredSurname(surnameField.getText().toString());
                    presenter.setEnteredEmail(emailField.getText().toString());
                    presenter.setEnteredPassword(passwordField.getText().toString());
                    presenter.setConfirmedEnteredPassword(confirmPasswordField.getText().toString());
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(consentGiven) {
                    presenter.beginSignInProcedure();
                }else
                    showConsentNotGrantedMessage();
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        navigateToLogin();
    }

    @Override
    public void showSigningInSuccessful(){
        hideLoadingIndicator();
        Toast.makeText(this.getBaseContext(), R.string.notice_account_created, Toast.LENGTH_SHORT).show();
        navigateToLogin();
    }

    @Override
    public void showSigningInFailedMessage(){
        Toast.makeText(this.getBaseContext(), R.string.error_signup_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setConsentToFalse(){
        userConsentCheckBox.setChecked(false);
    }

    @Override
    public void showConsentNotGrantedMessage(){
        Toast.makeText(this.getBaseContext(), R.string.error_consent_not_given, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void passwordDoesNotMatchMessage(){
        Toast.makeText(this.getBaseContext(), R.string.error_passwords_do_not_match, Toast.LENGTH_SHORT).show();
        setConsentToFalse();
        confirmPasswordField.setText("");
        confirmPasswordField.requestFocus();
    }

    @Override
    public void navigateToLogin(){
        startActivity(new Intent(Signup.this, Login.class));
        finish();
    }

    @Override
    public void hideLoadingIndicator(){
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showLoadingIndicator(){
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showInvalidEmailMessage(){
        Toast.makeText(this.getBaseContext(),R.string.error_enter_valid_email, Toast.LENGTH_SHORT).show();
        setConsentToFalse();
        emailField.setText("");
        emailField.requestFocus();
    }

    @Override
    public void showEmptyNameMessage(){
        Toast.makeText(this.getBaseContext(), R.string.error_enter_name, Toast.LENGTH_SHORT).show();
        setConsentToFalse();
        nameField.requestFocus();
    }

    @Override
    public void showEmptySurnameMessage(){
        Toast.makeText(this.getBaseContext(), R.string.error_enter_surname, Toast.LENGTH_SHORT).show();
        setConsentToFalse();
        surnameField.requestFocus();
    }

    @Override
    public void showEmptyEmailMessage(){
        Toast.makeText(this.getBaseContext(), R.string.error_enter_email, Toast.LENGTH_SHORT).show();
        setConsentToFalse();
        emailField.requestFocus();
    }

    @Override
    public void showEmptyPasswordMessage(){
        Toast.makeText(this.getBaseContext(), R.string.error_enter_new_password, Toast.LENGTH_SHORT).show();
        setConsentToFalse();
        passwordField.requestFocus();
    }

    @Override
    public void showEmptyConfirmedPasswordMessage(){
        Toast.makeText(this.getBaseContext(), R.string.error_enter_confirmed_password, Toast.LENGTH_SHORT).show();
        setConsentToFalse();
        confirmPasswordField.requestFocus();
    }


}
