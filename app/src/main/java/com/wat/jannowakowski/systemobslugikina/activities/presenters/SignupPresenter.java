package com.wat.jannowakowski.systemobslugikina.activities.presenters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Patterns;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.wat.jannowakowski.systemobslugikina.activities.models.User;
import com.wat.jannowakowski.systemobslugikina.global.CurrentAppSession;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnUserRegistered;

import java.util.regex.Pattern;

/**
 * Created by Jan Nowakowski on 24.11.2018.
 */

public class SignupPresenter {


    private FirebaseAuth userAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference usersParentRef;
    private Activity signupActivityRef;
    private View view;

    private User newUser = null;

    private String enteredName = "";
    private String enteredSurname = "";
    private String enteredEmail = "";
    private String enteredPassword = "";
    private String confirmedEnteredPassword = "";
    private int chosenDiscountType = 0;

    public FirebaseAuth getUserAuth() {
        return userAuth;
    }

    public void setSignupActivityRef(Activity signupActivity) {
        signupActivityRef = signupActivity;
    }

    public Activity getSignupActivityRef(){return this.signupActivityRef;}

    public void setEnteredName(String enteredName) {
        this.enteredName = enteredName;
    }

    public String getEnteredPassword() {
        return enteredPassword;
    }

    public void setEnteredPassword(String enteredPassword) {
        this.enteredPassword = enteredPassword;
    }

    public String getConfirmedEnteredPassword() {
        return confirmedEnteredPassword;
    }

    public void setConfirmedEnteredPassword(String confirmedEnteredPassword) {
        this.confirmedEnteredPassword = confirmedEnteredPassword;
    }

    public void setEnteredSurname(String enteredSurname) {
        this.enteredSurname = enteredSurname;
    }

    public void setEnteredEmail(String email) {

        if (email.length() > 0) {

            if (isValidEmail(email))
                this.enteredEmail = email;
            else
                view.showInvalidEmailMessage();
        }
    }

    public void setChosenDiscountType(int chosenDiscountType) {
        this.chosenDiscountType = chosenDiscountType;
    }

    public String getEnteredName() {
        return enteredName;
    }

    public String getEnteredSurname() {
        return enteredSurname;
    }

    public String getEnteredEmail() {
        return enteredEmail;
    }

    public int getChosenDiscountType() {
        return chosenDiscountType;
    }


    public SignupPresenter(View v){
        this.view = v;
        this.userAuth = CurrentAppSession.getINSTANCE().getCurrentUserAuth();
    }

    public void beginSignInProcedure(){

        if(checkUserInputData()) {
            registerUser(getSignupActivityRef(),
                    getUserAuth(),
                    getEnteredEmail(),
                    getEnteredPassword());
        }
    }

    private boolean checkUserInputData(){
            if(getEnteredEmail().isEmpty()){
            view.showEmptyEmailMessage();
            return false;
            }else
                if(getEnteredName().isEmpty()){
                view.showEmptyNameMessage();
                return false;
                }else
                    if(getEnteredSurname().isEmpty()){
                    view.showEmptySurnameMessage();
                    return false;
                    }else
                        if(getEnteredPassword().isEmpty() || getEnteredPassword().length() < 6) {
                        view.showEmptyPasswordMessage();
                        return false;
                        }else
                            if(getConfirmedEnteredPassword().isEmpty() || getConfirmedEnteredPassword().length() < 6){
                            view.showEmptyConfirmedPasswordMessage();
                            return false;
                            }else
                                if(!getConfirmedEnteredPassword().equals(getEnteredPassword())){
                                view.passwordDoesNotMatchMessage();
                                return false;
                                }else
                                    return true;
    }

    private void registerUser(Activity signupActivity, FirebaseAuth auth,String email,String password){

        view.showLoadingIndicator();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(signupActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            view.showSigningInFailedMessage();
                        } else {
                            newUser = new User(getUserAuth(),      //rozpoczęcie dodania danych użytkownika do bazy (po poprawnej rejestracji)
                                    getEnteredName(),
                                    getEnteredSurname(),
                                    getEnteredEmail(),
                                    getChosenDiscountType(),
                                    CurrentAppSession.getCustomerLoginCode());

                            newUser.setNewUserRegisteredListener(new OnUserRegistered() {
                                @Override
                                public void onNewUserRegistered(boolean state) {
                                    if(state) {
                                        newUser = null;       //usuwamy instancję roboczą użytkownika
                                        view.showSigningInSuccessful();
                                    }
                                }
                            });
                        }
                    }
                });

    }




    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }


    public interface View{
        void showConsentNotGrantedMessage();
        void showSigningInSuccessful();
        void setConsentToFalse();
        void showSigningInFailedMessage();
        void passwordDoesNotMatchMessage();
        void showInvalidEmailMessage();
        void showEmptyEmailMessage();
        void showEmptyNameMessage();
        void showEmptySurnameMessage();
        void showEmptyPasswordMessage();
        void showEmptyConfirmedPasswordMessage();
        void showLoadingIndicator();
        void hideLoadingIndicator();
        void navigateToLogin();
    }
}
