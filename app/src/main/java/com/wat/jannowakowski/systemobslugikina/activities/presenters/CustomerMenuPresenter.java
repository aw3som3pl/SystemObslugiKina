package com.wat.jannowakowski.systemobslugikina.activities.presenters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.wat.jannowakowski.systemobslugikina.global.CurrentAppSession;


public class CustomerMenuPresenter {

    private View view;

    private Button userTickets,showRepertoire;
    private RecyclerView currentRepertoireList;


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
