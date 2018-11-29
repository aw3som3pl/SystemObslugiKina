package com.wat.jannowakowski.systemobslugikina.activities.presenters;

import android.view.View;

import com.wat.jannowakowski.systemobslugikina.activities.models.User;
import com.wat.jannowakowski.systemobslugikina.global.CurrentAppSession;

/**
 * Created by Jan Nowakowski on 24.11.2018.
 */

public class CustomerMenuPresenter {

    private View view;

    public CustomerMenuPresenter(View v) {
        this.view = v;
    }

    public void commenceUserLogout(){

        CurrentAppSession.getINSTANCE().getCurrentUser().getUserAuth().signOut();
        CurrentAppSession.getINSTANCE().removeUserSession();
        view.navigateToLogin();
    }

    public interface View{
        void navigateToLogin();
    }
}
