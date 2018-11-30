package com.wat.jannowakowski.systemobslugikina.abstractClasses;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Base64;

import com.wat.jannowakowski.systemobslugikina.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jan Nowakowski on 24.11.2018.
 */

public abstract class EnumHandler {

    public static String parseAgeRestriction(Context c, int value){
        int infantsCathegory = 0;
        int primarySchoolCathegory = 1;
        int secondarySchoolCathegory = 2;
        int matureCathegory = 3;

        if(value == infantsCathegory)
            return c.getString(R.string.age_cathegory_infants);
        else
        if(value == primarySchoolCathegory)
            return c.getString(R.string.age_cathegory_primary);
        else
        if(value == secondarySchoolCathegory)
            return c.getString(R.string.age_cathegory_secondary);
        else
            return c.getString(R.string.age_cathegory_adults);
    }

    public static String parseLanguageMethod(Context c, int value) {
        int dubbing = 0;
        int subtitles = 1;
        int lector = 2;

        if (value == dubbing)
            return c.getString(R.string.language_method_dubbing);
        else if (value == subtitles)
            return c.getString(R.string.language_method_subtitles);
        else
            return c.getString(R.string.language_method_lector);
    }

    public static String parseScreeningTechnology(Context c, int value) {
        int threeDimensional = 0;
        int twoDimensional = 1;

        if (value == threeDimensional)
            return c.getString(R.string.screening_technology_3d);
        else
            return c.getString(R.string.screening_technology_2d);

    }

    public static boolean parsePremiereFlagState(int value){
        int notPremiere = 0;
        int isPremiere = 1;

        return value == isPremiere;
    }

    public static String parseDayOfYearToDate(int dayOfYear){

        String sdf = "dd.mm.yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(sdf);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
        Date date = new Date(calendar.getTimeInMillis());

        return dateFormat.format(date);

    }

    public static Drawable parseThumbnailToDrawable(String rawByteArray,Context c){
        byte[] byteArray = Base64.decode(rawByteArray, Base64.DEFAULT);
        return RoundedBitmapDrawableFactory.create(c.getResources(),BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
    }
}
