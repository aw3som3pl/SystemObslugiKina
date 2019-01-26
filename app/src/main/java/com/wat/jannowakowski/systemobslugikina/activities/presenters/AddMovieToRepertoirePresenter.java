package com.wat.jannowakowski.systemobslugikina.activities.presenters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.annotation.NonNull;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wat.jannowakowski.systemobslugikina.abstractClasses.EnumHandler;
import com.wat.jannowakowski.systemobslugikina.activities.models.Movie;
import com.wat.jannowakowski.systemobslugikina.activities.models.Screening;
import com.wat.jannowakowski.systemobslugikina.activities.models.ScreeningRoom;
import com.wat.jannowakowski.systemobslugikina.activities.views.AddMovieToRepertoire;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnAllMoviesDataReload;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnAllScreeningsRoomsDataReload;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Jan Nowakowski on 25.01.2019.
 */

public class AddMovieToRepertoirePresenter {

    private View view;
    private Activity addMovieToRepertoireRef = null;
    private DatabaseReference mDatabase;
    private ArrayList<Movie> allMoviesList;
    private ArrayList<ScreeningRoom> allScreeningRoomsList;
    private OnAllMoviesDataReload onAllMoviesDataReloadListener;
    private OnAllScreeningsRoomsDataReload onAllScreeningsRoomsDataReloadListener;

    public int selectedMovieIndex = 0;
    public int selectedScreeningRoomIndex = 0;

    public Screening newScreening;

    public AddMovieToRepertoirePresenter(View v){

        this.view = v;

        allMoviesList = new ArrayList<>();
        allScreeningRoomsList = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        reloadAllMovies();
    }

    public void reloadAllMovies() {

        view.showLoadingIndicator();

        onAllMoviesDataReloadListener = null;

        allMoviesList = new ArrayList<>();

        DatabaseReference moviesParentDbRef = mDatabase.child("Movies").getRef();

        moviesParentDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot movieData : dataSnapshot.getChildren()) {
                    Movie movie = new Movie(EnumHandler.parseThumbnailToDrawable(movieData.child("thumbnail").getValue().toString(),addMovieToRepertoireRef ),
                            Integer.parseInt(movieData.child("age_rating").getValue().toString()),
                            movieData.child("description").getValue().toString(),
                            movieData.child("title").getValue().toString(),
                            Integer.parseInt(movieData.child("screeningTechnology").getValue().toString()),
                            Integer.parseInt(movieData.child("duration").getValue().toString()),
                            Integer.parseInt(movieData.child("language").getValue().toString()),
                            movieData.getKey());
                    allMoviesList.add(movie);
                }
                onAllMoviesDataReloadListener.OnAllMoviesDataReloaded(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                view.showToastMsg("Błąd pobierania filmów: "+databaseError.getMessage());
            }
        });

        setOnAllMoviesDataReloadedListener(new OnAllMoviesDataReload() {
            @Override
            public void OnAllMoviesDataReloaded(boolean state) {
                reloadAllScreeningRooms();
            }
        });
    }

    public void reloadAllScreeningRooms() {

        onAllScreeningsRoomsDataReloadListener = null;

        allScreeningRoomsList = new ArrayList<>();

        DatabaseReference moviesParentDbRef = mDatabase.child("ScreeningRooms").getRef();

        moviesParentDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot screeningRoomData : dataSnapshot.getChildren()) {
                    ScreeningRoom screeningRoom = new ScreeningRoom(Integer.parseInt(screeningRoomData.child("maxSeatCount").getValue().toString()),
                            Integer.parseInt(screeningRoomData.child("projectionTechnology").getValue().toString()),
                            Integer.parseInt(screeningRoomData.child("referenceNumber").getValue().toString()),
                            screeningRoomData.getKey());
                    allScreeningRoomsList.add(screeningRoom);
                }
                onAllScreeningsRoomsDataReloadListener.OnAllScreeningsRoomsDataReloaded(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                view.showToastMsg("Błąd pobierania sal kinowych: "+databaseError.getMessage());
            }
        });

        setOnAllScreeningsRoomsDataReloadListener(new OnAllScreeningsRoomsDataReload() {
            @Override
            public void OnAllScreeningsRoomsDataReloaded(boolean state) {
                view.initializeSpinners(allScreeningRoomsList,allMoviesList);
            }
        });
    }

    public void completeScreeningData(Screening screening){
        screening.setScreeningRoom(allScreeningRoomsList.get(selectedScreeningRoomIndex));
        screening.setMovie(allMoviesList.get(selectedMovieIndex));

        if(screening.getMovie().getScreeningTechnology()!=screening.getScreeningRoom().getProjectionTechnology()) {
            view.showToastMsg("Technologia wyświetlania filmu oraz możliwości danej sali muszą się zgadzać!");
            view.hideSendingIndicator();
        }
        else {
            sendScreeningData(screening);
        }
    }

    public void sendScreeningData(Screening screening){

        DatabaseReference newScreeningDbRef = null;
        try {
            newScreeningDbRef = mDatabase.child("Repertoire").child(String.valueOf(EnumHandler.encodeDayOfYearFromString(screening.getDateOfScreening()))).child("Screenings").push();
            newScreeningDbRef.child("baseTicketPrice").setValue(screening.getBaseTicketPrice());
            newScreeningDbRef.child("isPremiere").setValue(EnumHandler.encodePremiereFlagState(screening.isPremiere()));
            newScreeningDbRef.child("movie").setValue(screening.getMovie().getMovieDbRef());
            newScreeningDbRef.child("screeningRoom").setValue(screening.getScreeningRoom().getScreeningRoomDbRef());
            newScreeningDbRef.child("screeningTechnology").setValue(screening.getScreeningRoom().getProjectionTechnology());
            newScreeningDbRef.child("timeOfScreening").setValue(screening.getTimeOfScreening());
            newScreeningDbRef.child("seatsTaken").child("placeholder").setValue(0);
            view.showToastMsg("Pomyślnie dodano film do repertuaru: "+newScreeningDbRef.getKey());
            view.hideSendingIndicator();
        } catch (ParseException e) {
            view.showToastMsg("Błąd poczas konwersji daty");
            view.hideSendingIndicator();
        }
    }



    public void setOnAllMoviesDataReloadedListener(OnAllMoviesDataReload onAllMoviesDataReloadedListener){
        this.onAllMoviesDataReloadListener = onAllMoviesDataReloadedListener;
    }

    public void setOnAllScreeningsRoomsDataReloadListener(OnAllScreeningsRoomsDataReload onAllMoviesDataReloadedListener){
        this.onAllScreeningsRoomsDataReloadListener = onAllMoviesDataReloadedListener;
    }

    public void setAddMovieToRepertoireRef(Activity addMovieToRepertoireRef) {
        this.addMovieToRepertoireRef = addMovieToRepertoireRef;
    }

    public interface View{
        void initializeSpinners(ArrayList<ScreeningRoom> screeningRooms, ArrayList<Movie> movies);

        void gatherInputData();

        void showLoadingIndicator();

        void hideLoadingIndicator();

        void showSendingIndicator();

        void hideSendingIndicator();

        void navigateToStaffMenu();
        void showToastMsg(String msg);
    }
}
