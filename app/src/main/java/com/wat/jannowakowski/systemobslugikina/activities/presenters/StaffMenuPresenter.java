package com.wat.jannowakowski.systemobslugikina.activities.presenters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.wat.jannowakowski.systemobslugikina.adapters.MoviesSpinnerAdapter;
import com.wat.jannowakowski.systemobslugikina.adapters.ScreeningRoomsSpinnerAdapter;
import com.wat.jannowakowski.systemobslugikina.global.CurrentAppSession;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnAllMoviesDataReload;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnAllScreeningsRoomsDataReload;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnSelectedMoviesDataReload;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnScreeningRoomsDataReload;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnScreeningsDataReload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.wat.jannowakowski.systemobslugikina.abstractClasses.PopupUtilities.dimBehind;

/**
 * Created by Jan Nowakowski on 24.11.2018.
 */

public class StaffMenuPresenter {

    private static final int THUMBNAIL_SIZE = 150;
    private static final int PHOTO_SIZE = 1000;
    private static final int IMAGE_QUALITY = 35;

    private View view;

    private Activity staffMenuActivityRef = null;

    private DatabaseReference mDatabase;

    private OnScreeningsDataReload onScreeningsDataReloadedListener = null;
    private OnSelectedMoviesDataReload onSelectedMoviesDataReloadedListener = null;
    private OnScreeningRoomsDataReload onScreeningRoomsDataReloadedListener = null;

    private ArrayList<Screening> screeningsInRepertoire;
    private ArrayList<String> moviesBeingScreenedDbRef; //potrzebne do wyłapania filmów przeznaczonych do pobrania
    private ArrayList<Movie> moviesBeingScreened;
    private ArrayList<String> screeningRoomsBeingUsedDbRef;
    private ArrayList<ScreeningRoom> screeningRoomsBeingUsed;




    private Uri popupThumbnailUri;
    private ImageView popupImageRef;


    public void setStaffMenuActivityRef(Activity staffMenuActivityRef) {
        this.staffMenuActivityRef = staffMenuActivityRef;
    }

    public StaffMenuPresenter(View v) {

        this.view = v;


        mDatabase = FirebaseDatabase.getInstance().getReference();

        setOnScreeningsDataReloadedListener(new OnScreeningsDataReload() {  // czekamy na załadowanie seansów będących w określonym repertuarze
            @Override
            public void OnScreeningsDataReloaded(boolean state) {
                if (state)
                    reloadMoviesBeingScreened(moviesBeingScreenedDbRef, screeningsInRepertoire);
            }
        });

        setOnSelectedMoviesDataReloadedListener(new OnSelectedMoviesDataReload() {  //czekamy na załadowanie obrazków oraz danych filmów wyświetlanych w repertuarze
            @Override
            public void OnSelectedMoviesDataReloaded(boolean state) {
                if (state)
                    reloadScreeningRoomsOfMovies(screeningRoomsBeingUsedDbRef, screeningsInRepertoire);
            }
        });

        setOnScreeningRoomsDataReloadedListener(new OnScreeningRoomsDataReload() {  //czekamy na załadowanie sal kinowych wyświetlanych filmów w repertuarze
            @Override
            public void OnScreeningRoomsDataReloaded(boolean state) {
                if (state)
                    view.setScreeningsRecyclerViewAdapter(screeningsInRepertoire);
            }
        });

    }

    public Uri getPopupThumbnailUri() {
        return popupThumbnailUri;
    }

    public void setPopupThumbnailUri(Uri popupThumbnailUri) {
        this.popupThumbnailUri = popupThumbnailUri;
    }


    public void reloadCurrentRepertoire() {

        screeningsInRepertoire = new ArrayList<>();
        moviesBeingScreened = new ArrayList<>();
        screeningRoomsBeingUsed = new ArrayList<>();
        moviesBeingScreenedDbRef = new ArrayList<>();
        screeningRoomsBeingUsedDbRef = new ArrayList<>();

        view.showLoadingIndicator();
        Repertoir currentRepertoire = new Repertoir(EnumHandler.encodeDayOfYearFromDate(Calendar.getInstance()));
        reloadScreeningsInRepertoire(currentRepertoire);
    }

    private void addMovieToScreening(Movie movie, Screening screening) {
        screening.setMovie(movie);
    }

    private void addScreeningRoomToScreening(ScreeningRoom screeningRoom, Screening screening) {
        screening.setScreeningRoom(screeningRoom);
    }

    private void reloadScreeningsInRepertoire(final Repertoir repertoir) {

        DatabaseReference screeningsParentDbRef = mDatabase.child("Repertoire").child(String.valueOf(repertoir.getDayOfYear())).child("Screenings").getRef();

        Query q = screeningsParentDbRef.orderByChild("timeOfScreening");

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot screening : dataSnapshot.getChildren()) {
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

    private void reloadMoviesBeingScreened(final ArrayList<String> moviesDbRefBeingScreened, final ArrayList<Screening> screeningsInRepertoireList) {

        DatabaseReference moviesParentDbRef = mDatabase.child("Movies").getRef();

        moviesParentDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot movieData : dataSnapshot.getChildren()) {
                    if (moviesDbRefBeingScreened.contains(movieData.getKey())) {
                        Movie newMovieBeingScreened = new Movie(EnumHandler.parseThumbnailToDrawable(movieData.child("thumbnail").getValue().toString(), staffMenuActivityRef),
                                Integer.parseInt(movieData.child("age_rating").getValue().toString()),
                                movieData.child("description").getValue().toString(),
                                movieData.child("title").getValue().toString(),
                                Integer.parseInt(movieData.child("screeningTechnology").getValue().toString()),
                                Integer.parseInt(movieData.child("duration").getValue().toString()),
                                Integer.parseInt(movieData.child("language").getValue().toString()),
                                movieData.getKey());
                        moviesBeingScreened.add(newMovieBeingScreened);
                        for (Screening screening : screeningsInRepertoireList) {
                            if (screening.getMovieDbRef().equalsIgnoreCase(movieData.getKey())) {
                                addMovieToScreening(newMovieBeingScreened, screening);
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

    private void reloadScreeningRoomsOfMovies(final ArrayList<String> screeningRoomsBeingUsedDbRef, final ArrayList<Screening> screeningsInRepertoireList) {

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

    public void showAddMoviePopupWindow(CoordinatorLayout popupContainer, LayoutInflater inflater) {

        final android.view.View popupView = inflater.inflate(R.layout.add_movie_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);



        final Button saveMovieBtn = popupView.findViewById(R.id.save_button);
        Button cancelBtn = popupView.findViewById(R.id.close_button);
        popupImageRef = popupView.findViewById(R.id.movie_thumbnail);
        FloatingActionButton addImageBtn = popupView.findViewById(R.id.add_image_button);

        final LinearLayout sendingInProgressLayout = popupView.findViewById(R.id.sending_layout);

        final EditText movieTitle = popupView.findViewById(R.id.title);
        final EditText movieDescription = popupView.findViewById(R.id.description);
        final EditText movieLength = popupView.findViewById(R.id.duration);

        final RadioGroup technologyOptionGroup = popupView.findViewById(R.id.technology_radio_group);
        final RadioGroup languageOptionGroup = popupView.findViewById(R.id.language_radio_group);
        final RadioGroup ageOptionGroup = popupView.findViewById(R.id.age_radio_group);


        addImageBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                showCamera();
            }
        });

        saveMovieBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                try {

                    sendingInProgressLayout.setVisibility(android.view.View.VISIBLE);
                    saveMovieBtn.setEnabled(false);

                    RadioButton ageChosenRadio = popupView.findViewById(ageOptionGroup.getCheckedRadioButtonId());
                    RadioButton languageChosenRadio = popupView.findViewById(languageOptionGroup.getCheckedRadioButtonId());
                    RadioButton technologyChosenRadio = popupView.findViewById(technologyOptionGroup.getCheckedRadioButtonId());

                    sendMovieData(new Movie(Base64.encodeToString(imageToBytearray(popupThumbnailUri), Base64.DEFAULT),
                            EnumHandler.encodeAgeRestriction(staffMenuActivityRef,ageChosenRadio.getText().toString()),
                            movieDescription.getText().toString(),
                            movieTitle.getText().toString(),
                            EnumHandler.encodeScreeningTechnology(staffMenuActivityRef,technologyChosenRadio.getText().toString()),
                            Integer.parseInt(movieLength.getText().toString()),
                            EnumHandler.encodeLanguageMethod(staffMenuActivityRef,languageChosenRadio.getText().toString())),
                            popupWindow);
                } catch (NullPointerException  n) {
                    sendingInProgressLayout.setVisibility(android.view.View.GONE);
                    saveMovieBtn.setEnabled(true);
                    view.showToastMsg("Błąd wejścia: " +n.getMessage());
                    n.printStackTrace();
                }
                  catch (NumberFormatException  e) {
                      sendingInProgressLayout.setVisibility(android.view.View.GONE);
                      saveMovieBtn.setEnabled(true);
                      view.showToastMsg("Błąd wejścia: " +e.getMessage());
                      e.printStackTrace();
                  }
            }
        });

        cancelBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                popupWindow.dismiss();
            }
        });

        // show the popup window
        popupWindow.showAtLocation(popupContainer, Gravity.CENTER, 0, 0);
        dimBehind(popupWindow);
    }

    public void showAddScreeningRoomPopupWindow(CoordinatorLayout popupContainer, LayoutInflater inflater) {

        // get a reference to the already created main layout

        // inflate the layout of the popup window
        final android.view.View popupView = inflater.inflate(R.layout.add_screening_hall_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        final LinearLayout sendingInProgressLayout = popupView.findViewById(R.id.sending_layout);

        final Button saveBtn = popupView.findViewById(R.id.save_button);
        Button cancelBtn = popupView.findViewById(R.id.close_button);

        final EditText screeningRoomDesignation = popupView.findViewById(R.id.designation);
        final RadioGroup screeningRoomTechnologyRadioGroup = popupView.findViewById(R.id.screening_technology_group);

        saveBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                try {
                    saveBtn.setEnabled(false);
                    sendingInProgressLayout.setVisibility(android.view.View.VISIBLE);

                    RadioButton chosenTechnologyRadioButton = popupView.findViewById(screeningRoomTechnologyRadioGroup.getCheckedRadioButtonId());

                    sendScreeningRoomData(new ScreeningRoom(50,
                            EnumHandler.encodeScreeningTechnology(staffMenuActivityRef, chosenTechnologyRadioButton.getText().toString()),
                            Integer.parseInt(screeningRoomDesignation.getText().toString())), popupWindow);
                } catch (NullPointerException e){
                    sendingInProgressLayout.setVisibility(android.view.View.GONE);
                    saveBtn.setEnabled(true);
                    view.showToastMsg("Błąd wejścia: " +e.getMessage());
                    e.printStackTrace();
                } catch (NumberFormatException n){
                    sendingInProgressLayout.setVisibility(android.view.View.GONE);
                    saveBtn.setEnabled(true);
                    view.showToastMsg("Błąd wejścia: " +n.getMessage());
                    n.printStackTrace();
                }
            }
        });

        cancelBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                popupWindow.dismiss();
            }
        });


        // show the popup window
        popupWindow.showAtLocation(popupContainer, Gravity.CENTER, 0, 0);
        dimBehind(popupWindow);


    }


    private void sendMovieData(Movie movie, final PopupWindow source){

        final DatabaseReference newMovieDbRef = mDatabase.child("Movies").push();

        newMovieDbRef.child("age_rating").setValue(movie.getAgeRating());
        newMovieDbRef.child("description").setValue(movie.getDescription());
        newMovieDbRef.child("duration").setValue(movie.getDuration());
        newMovieDbRef.child("language").setValue(movie.getLanguageMode());
        newMovieDbRef.child("screeningTechnology").setValue(movie.getScreeningTechnology());
        newMovieDbRef.child("title").setValue(movie.getTitle());
        newMovieDbRef.child("thumbnail").setValue(movie.getRawThumbnail()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                source.dismiss();
                view.showToastMsg("Dodano film: "+ newMovieDbRef.getKey());
            }
        });


    }
    private void sendScreeningRoomData(ScreeningRoom screeningRoom, final PopupWindow source){

        final DatabaseReference newScreeningRoomDbRef = mDatabase.child("ScreeningRooms").push();

        newScreeningRoomDbRef.child("maxSeatCount").setValue(screeningRoom.getMaxSeatCount());
        newScreeningRoomDbRef.child("projectionTechnology").setValue(screeningRoom.getProjectionTechnology());
        newScreeningRoomDbRef.child("referenceNumber").setValue(screeningRoom.getReferenceNumber()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                source.dismiss();
                view.showToastMsg("Dodano salę kinową: "+ newScreeningRoomDbRef.getKey());
            }
        });


    }

    public void showMovieDetailsPopup(int screeningIndex, CoordinatorLayout popupContainer, LayoutInflater inflater){
        final android.view.View popupView = inflater.inflate(R.layout.movie_info_popup, null);

        Screening thisScreening = screeningsInRepertoire.get(screeningIndex);

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

            }
        });

        if(thisScreening.isPremiere())
            premiereIcon.setVisibility(android.view.View.VISIBLE);
        movieThumbnail.setImageDrawable(thisScreening.getMovie().getThumbnail());
        movieTitle.setText(thisScreening.getMovie().getTitle());
        movieAgeRestriction.setText(EnumHandler.parseAgeRestriction(staffMenuActivityRef,thisScreening.getMovie().getAgeRating()));
        movieTechnology.setText(EnumHandler.parseScreeningTechnology(staffMenuActivityRef,thisScreening.getScreeningTechnology()));
        movieLanguage.setText(EnumHandler.parseLanguageMethod(staffMenuActivityRef,thisScreening.getMovie().getLanguageMode()));
        movieScreeningDate.setText(thisScreening.getDateOfScreening());
        movieScreeningTime.setText(thisScreening.getTimeOfScreening());
        movieLength.setText(String.valueOf(thisScreening.getMovie().getDuration()) + " min");
        movieDescription.setText(thisScreening.getMovie().getDescription());

        // show the popup window
        popupWindow.showAtLocation(popupContainer, Gravity.CENTER, 0, 0);
        dimBehind(popupWindow);

    }




    public void updateMovieThumbnail() {
        try {
            popupImageRef.setImageBitmap(getThumbnail(popupThumbnailUri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        }

    private void showCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(staffMenuActivityRef.getPackageManager()) != null) {
            File file = null;
            try {
                file = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            popupThumbnailUri = null;
            if (file != null) {
                popupThumbnailUri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, popupThumbnailUri);
                staffMenuActivityRef.startActivityForResult(intent,1);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = staffMenuActivityRef.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // file_entry storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(timeStamp, ".jpg", storageDir);
    }

    private byte[] imageToBytearray(Uri imageUri){


        byte[] byteArray;
        try {
            ByteArrayOutputStream btr = new ByteArrayOutputStream();
            getImage(popupThumbnailUri).compress(Bitmap.CompressFormat.JPEG,IMAGE_QUALITY,btr);
            byteArray = btr.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return byteArray;
    }

    private Bitmap getImage(Uri uri) throws IOException{
        InputStream input = staffMenuActivityRef.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > PHOTO_SIZE) ? (originalSize /  PHOTO_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//
        input = staffMenuActivityRef.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private Bitmap getThumbnail(Uri uri) throws IOException{
        InputStream input = staffMenuActivityRef.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//
        input = staffMenuActivityRef.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }

    public void commenceUserLogout(){

        CurrentAppSession.getINSTANCE().getCurrentUser().getUserAuth().signOut();
        CurrentAppSession.getINSTANCE().removeUserSession();
        view.navigateToLogin();
    }

    public interface View{
        void navigateToSearch();

        void showToastMsg(String msg);
        void navigateToLogin();
        void navigateToAddMovieToRepertoire();
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
