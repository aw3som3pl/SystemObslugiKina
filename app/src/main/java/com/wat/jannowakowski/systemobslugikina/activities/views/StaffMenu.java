package com.wat.jannowakowski.systemobslugikina.activities.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wat.jannowakowski.systemobslugikina.R;
import com.wat.jannowakowski.systemobslugikina.activities.models.Screening;
import com.wat.jannowakowski.systemobslugikina.activities.presenters.StaffMenuPresenter;
import com.wat.jannowakowski.systemobslugikina.adapters.RepertoirListAdapter;
import com.wat.jannowakowski.systemobslugikina.global.CurrentAppSession;

import java.util.ArrayList;

import static com.wat.jannowakowski.systemobslugikina.abstractClasses.PopupUtilities.dimBehind;

public class StaffMenu extends AppCompatActivity implements StaffMenuPresenter.View {


    private Activity thisActivity;

    private StaffMenuPresenter presenter;
    private RelativeLayout loadingOverlayLayout;
    private Button addNewMovieBtn, addNewScreeningRoomBtn, addNewScreeningToRepertoireBtn;
    private RecyclerView currentScreeningsList;
    private RecyclerView.Adapter repertoireAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private LayoutInflater inflater;
    private CoordinatorLayout mainLayout;

    private RepertoirListAdapter.RecyclerViewClickListener mRepertoirListListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_menu);

        loadingOverlayLayout = findViewById(R.id.loading_in_progress);

        mainLayout = findViewById(R.id.staff_main_layout);
        addNewMovieBtn = findViewById(R.id.add_movie);
        addNewScreeningRoomBtn = findViewById(R.id.add_screening_room);
        addNewScreeningToRepertoireBtn = findViewById(R.id.add_screening_repertoire);
        currentScreeningsList = findViewById(R.id.current_screenings);

        currentScreeningsList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(thisActivity);
        currentScreeningsList.setLayoutManager(mLayoutManager);

        thisActivity = this;

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        setTitle(CurrentAppSession.getINSTANCE().getUserFullName());

        presenter = new StaffMenuPresenter(this);

        presenter.setStaffMenuActivityRef(thisActivity);

        presenter.reloadCurrentRepertoire();

        addNewMovieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.showAddMoviePopupWindow(mainLayout,inflater);
            }
        });

        addNewScreeningRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.showAddScreeningRoomPopupWindow(mainLayout,inflater);
            }
        });

        addNewScreeningToRepertoireBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                break;
            case R.id.contact:
                break;
            case R.id.logout:
                presenter.commenceUserLogout();
                break;
        }
        return true;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

    }

    @Override
    public void navigateToLogin() {
        startActivity(new Intent(StaffMenu.this, Login.class));
        finish();
    }


    @Override
    public void showLoadingIndicator() {
        loadingOverlayLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingIndicator() {
        loadingOverlayLayout.setVisibility(View.GONE);
    }


    @Override
    public void setScreeningsRecyclerViewAdapter(ArrayList<Screening> screeningsList){

        mRepertoirListListener = new RepertoirListAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(StaffMenu.this, "Click "+ position, Toast.LENGTH_LONG).show();
            }
        };

        repertoireAdapter = new RepertoirListAdapter(screeningsList,mRepertoirListListener);
        currentScreeningsList.setAdapter(repertoireAdapter);

        hideLoadingIndicator();



    }

}

