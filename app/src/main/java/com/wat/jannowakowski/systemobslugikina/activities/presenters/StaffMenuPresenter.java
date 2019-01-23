package com.wat.jannowakowski.systemobslugikina.activities.presenters;

import android.app.Activity;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wat.jannowakowski.systemobslugikina.R;
import com.wat.jannowakowski.systemobslugikina.abstractClasses.EnumHandler;
import com.wat.jannowakowski.systemobslugikina.activities.models.Movie;
import com.wat.jannowakowski.systemobslugikina.activities.models.Repertoir;
import com.wat.jannowakowski.systemobslugikina.activities.models.Screening;
import com.wat.jannowakowski.systemobslugikina.activities.models.ScreeningRoom;
import com.wat.jannowakowski.systemobslugikina.global.CurrentAppSession;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnMoviesDataReload;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnScreeningRoomsDataReload;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnScreeningsDataReload;

import java.util.ArrayList;

import kotlin.collections.FloatIterator;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.wat.jannowakowski.systemobslugikina.abstractClasses.PopupUtilities.dimBehind;

/**
 * Created by Jan Nowakowski on 24.11.2018.
 */

public class StaffMenuPresenter {

    private View view;

    private Activity staffMenuActivityRef = null;

    private DatabaseReference mDatabase;

    private OnScreeningsDataReload onScreeningsDataReloadedListener = null;
    private OnMoviesDataReload onMoviesDataReloadedListener = null;
    private OnScreeningRoomsDataReload onScreeningRoomsDataReloadedListener = null;

    private ArrayList<Screening> screeningsInRepertoire;
    private ArrayList<String> moviesBeingScreenedDbRef; //potrzebne do wyłapania filmów przeznaczonych do pobrania
    private ArrayList<Movie> moviesBeingScreened;
    private ArrayList<String> screeningRoomsBeingUsedDbRef;
    private ArrayList<ScreeningRoom> screeningRoomsBeingUsed;


    public void setStaffMenuActivityRef(Activity staffMenuActivityRef) {
        this.staffMenuActivityRef = staffMenuActivityRef;
    }

    public StaffMenuPresenter(View v) {

        this.view = v;
        screeningsInRepertoire = new ArrayList<>();
        moviesBeingScreened = new ArrayList<>();
        screeningRoomsBeingUsed = new ArrayList<>();
        moviesBeingScreenedDbRef = new ArrayList<>();
        screeningRoomsBeingUsedDbRef = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        setOnScreeningsDataReloadedListener(new OnScreeningsDataReload() {  // czekamy na załadowanie seansów będących w określonym repertuarze
            @Override
            public void OnScreeningsDataReloaded(boolean state) {
                if(state)
                    reloadMoviesBeingScreened(moviesBeingScreenedDbRef,screeningsInRepertoire);
            }
        });

        setOnMoviesDataReloadedListener(new OnMoviesDataReload() {  //czekamy na załadowanie obrazków oraz danych filmów wyświetlanych w repertuarze
            @Override
            public void OnMoviesDataReloaded(boolean state) {
                if(state)
                    reloadScreeningRoomsOfMovies(screeningRoomsBeingUsedDbRef,screeningsInRepertoire);
            }
        });

        setOnScreeningRoomsDataReloadedListener(new OnScreeningRoomsDataReload() {  //czekamy na załadowanie sal kinowych wyświetlanych filmów w repertuarze
            @Override
            public void OnScreeningRoomsDataReloaded(boolean state) {
                if(state)
                    view.setScreeningsRecyclerViewAdapter(screeningsInRepertoire);
            }
        });

    }

    public void reloadCurrentRepertoire(){
        view.showLoadingIndicator();
        Repertoir currentRepertoire = new Repertoir(327);
        reloadScreeningsInRepertoire(currentRepertoire);
    }

    private void addMovieToScreening(Movie movie,Screening screening){
        screening.setMovie(movie);
    }

    private void addScreeningRoomToScreening(ScreeningRoom screeningRoom, Screening screening){
        screening.setScreeningRoom(screeningRoom);
    }

    private void reloadScreeningsInRepertoire(final Repertoir repertoir){

        DatabaseReference screeningsParentDbRef = mDatabase.child("Repertoire").child(String.valueOf(repertoir.getDayOfYear())).child("Screenings").getRef();

        screeningsParentDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot screening : dataSnapshot.getChildren()){
                    Screening newScreening = new Screening(screening.getKey(),
                            screening.child("movie").getValue().toString(),
                            screening.child("screeningRoom").getValue().toString(),
                            Integer.parseInt(screening.child("screeningTechnology").getValue().toString()),
                            Integer.parseInt(screening.child("seatsTaken").getValue().toString()),
                            screening.child("shortDescription").getValue().toString(),
                            Long.parseLong(screening.child("baseTicketPrice").getValue().toString()),
                            EnumHandler.parseDayOfYearToDate(repertoir.getDayOfYear()),
                            screening.child("timeOfScreening").getValue().toString(),
                            Integer.parseInt(screening.child("isPremiere").getValue().toString()));
                    screeningsInRepertoire.add(newScreening);
                    moviesBeingScreenedDbRef.add(newScreening.getMovieDbRef());
                    screeningRoomsBeingUsedDbRef.add(newScreening.getScreeningRoomDbRef());
                }
                onScreeningsDataReloadedListener.OnScreeningsDataReloaded(true);    //czekamy aż skończy się ładowanie (czeka na wykonanie się pętli)
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void reloadMoviesBeingScreened(final ArrayList<String> moviesDbRefBeingScreened, final ArrayList<Screening> screeningsInRepertoireList){

        DatabaseReference moviesParentDbRef = mDatabase.child("Movies").getRef();

        moviesParentDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot movieData : dataSnapshot.getChildren()) {
                    if (moviesDbRefBeingScreened.contains(movieData.getKey())) {
                        Movie newMovieBeingScreened = new Movie(EnumHandler.parseThumbnailToDrawable(movieData.child("thumbnail").getValue().toString(), staffMenuActivityRef),
                                Integer.parseInt(movieData.child("age_rating").getValue().toString()),
                                movieData.child("description").getValue().toString(),
                                movieData.child("title").getValue().toString(),
                                Integer.parseInt(movieData.child("duration").getValue().toString()),
                                Integer.parseInt(movieData.child("language").getValue().toString()));
                        moviesBeingScreened.add(newMovieBeingScreened);
                        for(Screening screening : screeningsInRepertoireList){
                            if(screening.getMovieDbRef().equalsIgnoreCase(movieData.getKey())){
                                addMovieToScreening(newMovieBeingScreened,screening);
                            }
                        }
                    }
                }
                onMoviesDataReloadedListener.OnMoviesDataReloaded(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void reloadScreeningRoomsOfMovies(final ArrayList<String> screeningRoomsBeingUsedDbRef, final ArrayList<Screening> screeningsInRepertoireList){

        DatabaseReference screeningRoomsParentDbRef = mDatabase.child("ScreeningRooms").getRef();

        screeningRoomsParentDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot screeningRoomData : dataSnapshot.getChildren()) {
                    if (screeningRoomsBeingUsedDbRef.contains(screeningRoomData.getKey())) {
                        ScreeningRoom newScreeningRoomBeingUsed = new ScreeningRoom(Integer.parseInt(screeningRoomData.child("maxSeatCount").getValue().toString()),
                                Integer.parseInt(screeningRoomData.child("projectionTechnology").getValue().toString()),
                                Integer.parseInt(screeningRoomData.child("referenceNumber").getValue().toString()),
                                Integer.parseInt(screeningRoomData.child("status").getValue().toString()));
                        screeningRoomsBeingUsed.add(newScreeningRoomBeingUsed);
                        for (Screening screening : screeningsInRepertoireList) {
                            if (screening.getScreeningRoomDbRef().equalsIgnoreCase(screeningRoomData.getKey())) {
                                addScreeningRoomToScreening(newScreeningRoomBeingUsed, screening);
                            }
                        }
                    }
                }
                onScreeningRoomsDataReloadedListener.OnScreeningRoomsDataReloaded(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showAddMoviePopupWindow(CoordinatorLayout popupContainer, LayoutInflater inflater) {

        // get a reference to the already created main layout

        // inflate the layout of the popup window
        android.view.View popupView = inflater.inflate(R.layout.add_movie_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        Button saveMovieBtn = popupView.findViewById(R.id.save_button);
        Button cancelBtn = popupView.findViewById(R.id.close_button);
        FloatingActionButton addImageBtn = popupView.findViewById(R.id.add_image_button);

        EditText movieTitle = popupView.findViewById(R.id.title);
        EditText movieDescription = popupView.findViewById(R.id.description);
        EditText movieLength = popupView.findViewById(R.id.duration);

        RadioGroup languageOptionGroup = popupView.findViewById(R.id.language_radio_group);
        RadioGroup ageOptionGroup = popupView.findViewById(R.id.age_radio_group);




        addImageBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

            }
        });

        saveMovieBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

            }
        });

        cancelBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

            }
        });

        // show the popup window
        popupWindow.showAtLocation(popupContainer, Gravity.CENTER, 0, 0);
        dimBehind(popupWindow);


    }

    public void showAddScreeningRoomPopupWindow(CoordinatorLayout popupContainer, LayoutInflater inflater) {

        // get a reference to the already created main layout

        // inflate the layout of the popup window
        android.view.View popupView = inflater.inflate(R.layout.add_screening_room_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        // show the popup window
        popupWindow.showAtLocation(popupContainer, Gravity.CENTER, 0, 0);
        dimBehind(popupWindow);


    }

    public void commenceUserLogout(){

        CurrentAppSession.getINSTANCE().getCurrentUser().getUserAuth().signOut();
        CurrentAppSession.getINSTANCE().removeUserSession();
        view.navigateToLogin();
    }

    public interface View{
        void navigateToLogin();
        void showLoadingIndicator();
        void hideLoadingIndicator();
        void setScreeningsRecyclerViewAdapter(ArrayList<Screening> screeningsList);
    }

    public void setOnMoviesDataReloadedListener(OnMoviesDataReload onMoviesDataReloadedListener) {
        this.onMoviesDataReloadedListener = onMoviesDataReloadedListener;
    }

    public void setOnScreeningsDataReloadedListener(OnScreeningsDataReload onScreeningsDataReloadedListener) {
        this.onScreeningsDataReloadedListener = onScreeningsDataReloadedListener;
    }

    public void setOnScreeningRoomsDataReloadedListener(OnScreeningRoomsDataReload onScreeningRoomsDataReloadedListener) {
        this.onScreeningRoomsDataReloadedListener = onScreeningRoomsDataReloadedListener;
    }
}
