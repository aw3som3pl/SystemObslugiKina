package com.wat.jannowakowski.systemobslugikina.activities.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wat.jannowakowski.systemobslugikina.R;
import com.wat.jannowakowski.systemobslugikina.activities.presenters.SearchRepertoirePresenter;

public class SearchRepertoire extends AppCompatActivity implements SearchRepertoirePresenter.View {

    private SearchRepertoirePresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_repertoire);

        presenter = new SearchRepertoirePresenter(this);


    }
}
