package com.wat.jannowakowski.systemobslugikina.activities.presenters;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wat.jannowakowski.systemobslugikina.abstractClasses.EnumHandler;
import com.wat.jannowakowski.systemobslugikina.activities.models.Movie;
import com.wat.jannowakowski.systemobslugikina.activities.models.Repertoir;
import com.wat.jannowakowski.systemobslugikina.activities.models.Screening;
import com.wat.jannowakowski.systemobslugikina.activities.models.ScreeningRoom;
import com.wat.jannowakowski.systemobslugikina.adapters.RepertoirListAdapter;
import com.wat.jannowakowski.systemobslugikina.global.CurrentAppSession;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnMoviesDataReload;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnScreeningRoomsDataReload;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnScreeningsDataReload;

import java.util.ArrayList;


public class CustomerMenuPresenter {

    private View view;

    private Activity customerMenuActivityRef = null;

    private DatabaseReference mDatabase;
    private DatabaseReference moviesParentDbRef;
    private DatabaseReference screeningsParentDbRef;
    private DatabaseReference screeningRoomsParentDbRef;

    private OnScreeningsDataReload onScreeningsDataReloadedListener = null;
    private OnMoviesDataReload onMoviesDataReloadedListener = null;
    private OnScreeningRoomsDataReload onScreeningRoomsDataReloadedListener = null;

    private ArrayList<Screening> screeningsInRepertoire;
    private ArrayList<Movie> moviesBeingScreened;
    private ArrayList<ScreeningRoom> screeningRoomsBeingUsed;


    public void setCustomerMenuActivityRef(Activity customerMenuActivityRef) {
        this.customerMenuActivityRef = customerMenuActivityRef;
    }


    public CustomerMenuPresenter(View v) {

        this.view = v;
        screeningsInRepertoire = new ArrayList<>();
        moviesBeingScreened = new ArrayList<>();
        screeningRoomsBeingUsed = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setOnScreeningsDataReloadedListener(new OnScreeningsDataReload() {  // czekamy na załadowanie seansów będących w określonym repertuarze
            @Override
            public void OnScreeningsDataReloaded(boolean state) {
                if(state)
                reloadMoviesBeingScreened(screeningsInRepertoire);
            }
        });

        setOnMoviesDataReloadedListener(new OnMoviesDataReload() {  //czekamy na załadowanie obrazków oraz danych filmów wyświetlanych w repertuarze
            @Override
            public void OnMoviesDataReloaded(boolean state) {
                if(state)
                reloadScreeningRoomsOfMovies(screeningsInRepertoire);
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
        Repertoir currentRepertoire = new Repertoir(327);
        reloadScreeningsInRepertoire(currentRepertoire);
    }

    public void reloadScreeningsInRepertoire(final Repertoir repertoir){

        screeningsParentDbRef = mDatabase.child("Repertoire").child(String.valueOf(repertoir.getDayOfYear())).child("Screenings").getRef();

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
                }
                onScreeningsDataReloadedListener.OnScreeningsDataReloaded(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void reloadMoviesBeingScreened(ArrayList<Screening> screeningsList){

        moviesParentDbRef = mDatabase.child("Movies").getRef();

        for(final Screening screening : screeningsList){

            DatabaseReference movieBeingScreened = moviesParentDbRef.child(screening.getMovieDbRef()).getRef();

            movieBeingScreened.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Movie newMovieBeingScreened = new Movie(EnumHandler.parseThumbnailToDrawable(dataSnapshot.child("thumbnail").getValue().toString(),customerMenuActivityRef),
                            Integer.parseInt(dataSnapshot.child("age_rating").getValue().toString()),
                            dataSnapshot.child("description").getValue().toString(),
                            dataSnapshot.child("title").getValue().toString(),
                            Integer.parseInt(dataSnapshot.child("duration").getValue().toString()),
                            Integer.parseInt(dataSnapshot.child("language").getValue().toString()));
                    moviesBeingScreened.add(newMovieBeingScreened);
                    screening.setMovie(newMovieBeingScreened);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        onMoviesDataReloadedListener.OnMoviesDataReloaded(true);
    }

    private void reloadScreeningRoomsOfMovies(ArrayList<Screening> screeningsList){

        screeningRoomsParentDbRef = mDatabase.child("ScreeningRooms").getRef();

        for(final Screening screening : screeningsList){

            DatabaseReference screeningRoomBeingUsed = screeningRoomsParentDbRef.child(screening.getScreeningRoomDbRef()).getRef();

            screeningRoomBeingUsed.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   ScreeningRoom newScreeningRoomBeingUsed = new ScreeningRoom(Integer.parseInt(dataSnapshot.child("maxSeatCount").getValue().toString()),
                           Integer.parseInt(dataSnapshot.child("projectionTechnology").getValue().toString()),
                           Integer.parseInt(dataSnapshot.child("referenceNumber").getValue().toString()),
                           Integer.parseInt(dataSnapshot.child("status").getValue().toString()));
                   screeningRoomsBeingUsed.add(newScreeningRoomBeingUsed);
                   screening.setScreeningRoom(newScreeningRoomBeingUsed);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        onScreeningRoomsDataReloadedListener.OnScreeningRoomsDataReloaded(true);
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
