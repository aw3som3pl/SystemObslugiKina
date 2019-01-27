package com.wat.jannowakowski.systemobslugikina.activities.presenters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wat.jannowakowski.systemobslugikina.R;
import com.wat.jannowakowski.systemobslugikina.abstractClasses.EnumHandler;
import com.wat.jannowakowski.systemobslugikina.activities.models.Movie;
import com.wat.jannowakowski.systemobslugikina.activities.models.Repertoir;
import com.wat.jannowakowski.systemobslugikina.activities.models.Screening;
import com.wat.jannowakowski.systemobslugikina.activities.models.ScreeningRoom;
import com.wat.jannowakowski.systemobslugikina.global.CurrentAppSession;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnScreeningRoomsDataReload;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnScreeningsDataReload;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnSelectedMoviesDataReload;

import java.util.ArrayList;
import java.util.Calendar;

import static com.wat.jannowakowski.systemobslugikina.abstractClasses.PopupUtilities.dimBehind;

/**
 * Created by Jan Nowakowski on 06.12.2018.
 */

public class SearchRepertoirePresenter {

    private View view;

    private Activity customerMenuActivityRef = null;

    private DatabaseReference mDatabase;

    private OnScreeningsDataReload onScreeningsDataReloadedListener = null;
    private OnSelectedMoviesDataReload onSelectedMoviesDataReloadedListener = null;
    private OnScreeningRoomsDataReload onScreeningRoomsDataReloadedListener = null;


    private ArrayList<Screening> screeningsInRepertoire;
    private ArrayList<String> moviesBeingScreenedDbRef; //potrzebne do wyłapania filmów przeznaczonych do pobrania
    private ArrayList<Movie> moviesBeingScreened;
    private ArrayList<String> screeningRoomsBeingUsedDbRef;
    private ArrayList<ScreeningRoom> screeningRoomsBeingUsed;


    public void setCustomerMenuActivityRef(Activity customerMenuActivityRef) {
        this.customerMenuActivityRef = customerMenuActivityRef;
    }

    public SearchRepertoirePresenter(View v) {

        this.view = v;


        mDatabase = FirebaseDatabase.getInstance().getReference();

        setOnScreeningsDataReloadedListener(new OnScreeningsDataReload() {  // czekamy na załadowanie seansów będących w określonym repertuarze
            @Override
            public void OnScreeningsDataReloaded(boolean state) {
                if(state)
                    reloadMoviesBeingScreened(moviesBeingScreenedDbRef,screeningsInRepertoire);
            }
        });

        setOnSelectedMoviesDataReloadedListener(new OnSelectedMoviesDataReload() {  //czekamy na załadowanie obrazków oraz danych filmów wyświetlanych w repertuarze
            @Override
            public void OnSelectedMoviesDataReloaded(boolean state) {
                if(state)
                    reloadScreeningRoomsOfMovies(screeningRoomsBeingUsedDbRef,screeningsInRepertoire);
            }
        });

        setOnScreeningRoomsDataReloadedListener(new OnScreeningRoomsDataReload() {  //czekamy na załadowanie sal kinowych wyświetlanych filmów w repertuarze
            @Override
            public void OnScreeningRoomsDataReloaded(boolean state) {
                if(state) {
                    if(screeningsInRepertoire.size()==0)
                        view.showScreeningsDataMissing();
                    else
                        view.hideScreeningsDataMissing();
                    view.setScreeningsRecyclerViewAdapter(screeningsInRepertoire);
                }
            }
        });

    }

    public void reloadCurrentRepertoire(int dayOfYear){
        screeningsInRepertoire = new ArrayList<>();
        moviesBeingScreened = new ArrayList<>();
        screeningRoomsBeingUsed = new ArrayList<>();
        moviesBeingScreenedDbRef = new ArrayList<>();
        screeningRoomsBeingUsedDbRef = new ArrayList<>();

        view.showLoadingIndicator();
        Repertoir currentRepertoire = new Repertoir(dayOfYear);
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

        Query q = screeningsParentDbRef.orderByChild("timeOfScreening");

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot screening : dataSnapshot.getChildren()){
                    Screening newScreening = new Screening(screening.getRef().toString(),
                            screening.child("movie").getValue().toString(),
                            screening.child("screeningRoom").getValue().toString(),
                            Integer.parseInt(screening.child("screeningTechnology").getValue().toString()),
                            Double.parseDouble(screening.child("baseTicketPrice").getValue().toString()),
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
                        Movie newMovieBeingScreened = new Movie(EnumHandler.parseThumbnailToDrawable(movieData.child("thumbnail").getValue().toString(), customerMenuActivityRef),
                                Integer.parseInt(movieData.child("age_rating").getValue().toString()),
                                movieData.child("description").getValue().toString(),
                                movieData.child("title").getValue().toString(),
                                Integer.parseInt(movieData.child("screeningTechnology").getValue().toString()),
                                Integer.parseInt(movieData.child("duration").getValue().toString()),
                                Integer.parseInt(movieData.child("language").getValue().toString()),
                                movieData.getKey());
                        moviesBeingScreened.add(newMovieBeingScreened);
                        for(Screening screening : screeningsInRepertoireList){
                            if(screening.getMovieDbRef().equalsIgnoreCase(movieData.getKey())){
                                addMovieToScreening(newMovieBeingScreened,screening);
                            }
                        }
                    }
                }
                onSelectedMoviesDataReloadedListener.OnSelectedMoviesDataReloaded(true);
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
                                screeningRoomData.getKey());
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

    public void showMovieDetailsPopup(int screeningIndex, CoordinatorLayout popupContainer, LayoutInflater inflater){
        final android.view.View popupView = inflater.inflate(R.layout.movie_info_popup, null);

        final Screening thisScreening = screeningsInRepertoire.get(screeningIndex);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        ImageView premiereIcon = popupView.findViewById(R.id.premiere_icon);
        ImageView movieThumbnail = popupView.findViewById(R.id.movie_thumbnail);
        TextView movieTitle = popupView.findViewById(R.id.movie_title);
        TextView movieAgeRestriction = popupView.findViewById(R.id.movie_age_restriction);
        TextView movieTechnology = popupView.findViewById(R.id.movie_screening_technology);
        TextView movieLanguage = popupView.findViewById(R.id.movie_language);
        TextView movieScreeningDate = popupView.findViewById(R.id.movie_screening_date);
        TextView movieScreeningTime = popupView.findViewById(R.id.movie_screening_time);
        TextView movieLength = popupView.findViewById(R.id.movie_duration);
        TextView movieDescription = popupView.findViewById(R.id.movie_description);

        Button closeBtn = popupView.findViewById(R.id.close_button);
        Button buyTickets = popupView.findViewById(R.id.buy_button);

        closeBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                popupWindow.dismiss();
            }
        });

        buyTickets.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                view.navigateToBuyTickets(thisScreening);
                popupWindow.dismiss();
            }
        });

        if(thisScreening.isPremiere())
            premiereIcon.setVisibility(android.view.View.VISIBLE);
        movieThumbnail.setImageDrawable(thisScreening.getMovie().getThumbnail());
        movieTitle.setText(thisScreening.getMovie().getTitle());
        movieAgeRestriction.setText(EnumHandler.parseAgeRestriction(customerMenuActivityRef,thisScreening.getMovie().getAgeRating()));
        movieTechnology.setText(EnumHandler.parseScreeningTechnology(customerMenuActivityRef,thisScreening.getScreeningTechnology()));
        movieLanguage.setText(EnumHandler.parseLanguageMethod(customerMenuActivityRef,thisScreening.getMovie().getLanguageMode()));
        movieScreeningDate.setText(thisScreening.getDateOfScreening());
        movieScreeningTime.setText(thisScreening.getTimeOfScreening());
        movieLength.setText(String.valueOf(thisScreening.getMovie().getDuration()) + " min");
        movieDescription.setText(thisScreening.getMovie().getDescription());

        // show the popup window
        popupWindow.showAtLocation(popupContainer, Gravity.CENTER, 0, 0);
        dimBehind(popupWindow);

    }


    public interface View{
        void navigateToBuyTickets(Screening screening);

        void navigateToCustomerMenu();

        void showScreeningsDataMissing();

        void hideScreeningsDataMissing();

        void showLoadingIndicator();
        void hideLoadingIndicator();
        void setScreeningsRecyclerViewAdapter(ArrayList<Screening> screeningsList);
    }

    public void setOnSelectedMoviesDataReloadedListener(OnSelectedMoviesDataReload onSelectedMoviesDataReloadedListener) {
        this.onSelectedMoviesDataReloadedListener = onSelectedMoviesDataReloadedListener;
    }

    public void setOnScreeningsDataReloadedListener(OnScreeningsDataReload onScreeningsDataReloadedListener) {
        this.onScreeningsDataReloadedListener = onScreeningsDataReloadedListener;
    }

    public void setOnScreeningRoomsDataReloadedListener(OnScreeningRoomsDataReload onScreeningRoomsDataReloadedListener) {
        this.onScreeningRoomsDataReloadedListener = onScreeningRoomsDataReloadedListener;
    }

}
