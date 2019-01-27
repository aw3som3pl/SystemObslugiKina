package com.wat.jannowakowski.systemobslugikina.abstractClasses;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Base64;

import com.wat.jannowakowski.systemobslugikina.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jan Nowakowski on 24.11.2018.
 */

public abstract class EnumHandler {

    public static String parseAgeRestriction(Context c, int value) {
        final int infantsCathegory = 0;
        final int primarySchoolCathegory = 1;
        final int secondarySchoolCathegory = 2;
        final int matureCathegory = 3;

        switch (value) {
            case infantsCathegory:
                return c.getString(R.string.age_cathegory_infants);
            case primarySchoolCathegory:
                return c.getString(R.string.age_cathegory_primary);
            case secondarySchoolCathegory:
                return c.getString(R.string.age_cathegory_secondary);
            case matureCathegory:
                return c.getString(R.string.age_cathegory_adults);
            default:
                return c.getString(R.string.age_cathegory_infants);
        }
    }

    public static int encodeAgeRestriction(Context c, String value) {
        final int infantsCathegory = 0;
        final int primarySchoolCathegory = 1;
        final int secondarySchoolCathegory = 2;
        final int matureCathegory = 3;

        if (value.equalsIgnoreCase(c.getString(R.string.age_cathegory_infants)))
            return infantsCathegory;
        else if (value.equalsIgnoreCase(c.getString(R.string.age_cathegory_primary)))
            return primarySchoolCathegory;
        else if (value.equalsIgnoreCase(c.getString(R.string.age_cathegory_secondary)))
            return secondarySchoolCathegory;
        else
            return matureCathegory;
    }

    public static String parseLanguageMethod(Context c, int value) {
        final int dubbing = 0;
        final int subtitles = 1;
        final int lector = 2;

        switch (value) {
            case dubbing:
                return c.getString(R.string.language_method_dubbing);
            case subtitles:
                return c.getString(R.string.language_method_subtitles);
            case lector:
                return c.getString(R.string.language_method_lector);
            default:
                return c.getString(R.string.language_method_dubbing);
        }
    }

    public static int encodeLanguageMethod(Context c, String value) {
        final int dubbing = 0;
        final int subtitles = 1;
        final int lector = 2;

        if (value.equalsIgnoreCase(c.getString(R.string.language_method_dubbing)))
            return dubbing;
        else if (value.equalsIgnoreCase(c.getString(R.string.language_method_subtitles)))
            return subtitles;
        else
            return lector;
    }

    public static String parseScreeningTechnology(Context c, int value) {
        final int threeDimensional = 0;
        final int twoDimensional = 1;

        if (value == threeDimensional)
            return c.getString(R.string.screening_technology_3d);
        else
            return c.getString(R.string.screening_technology_2d);

    }

    public static int encodeScreeningTechnology(Context c, String value) {
        final int threeDimensional = 0;
        final int twoDimensional = 1;

        if (value.equalsIgnoreCase(c.getString(R.string.screening_technology_3d)))
            return threeDimensional;
        else
            return twoDimensional;

    }

    public static boolean parsePremiereFlagState(int value) {
        final int notPremiere = 0;
        int isPremiere = 1;

        return value == isPremiere;
    }

    public static int encodePremiereFlagState(boolean state) {
        final int notPremiere = 0;
        int isPremiere = 1;

        if (state)
            return isPremiere;
        else
            return notPremiere;
    }

    public static double calculateTicketPrice(double basePrice, double discount){
            return basePrice*discount;
    }
    public static String parseDiscountType(Context c, int discountType){
        final int student = 0;
        final int disabled = 1;
        final int adult = 2;
        final int child = 3;

        if (discountType==student)
            return c.getString(R.string.student);
        else if (discountType==disabled)
            return c.getString(R.string.disabled_person);
        else if (discountType==adult)
            return c.getString(R.string.adult);
        else
            return c.getString(R.string.child);
    }


    public static int encodeDiscountType(Context c, String discountType){
        final int student = 0;
        final int disabled = 1;
        final int adult = 2;
        final int child = 3;

        if (discountType.equalsIgnoreCase(c.getString(R.string.student)))
            return student;
        else if (discountType.equalsIgnoreCase(c.getString(R.string.disabled_person)))
            return disabled;
        else if (discountType.equalsIgnoreCase(c.getString(R.string.adult)))
            return adult;
        else
            return child;
    }

    public static String parseDayOfYearToDate(int dayOfYear) {

        String sdf = "dd.MM.yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(sdf);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
        Date date = new Date(calendar.getTimeInMillis());

        return dateFormat.format(date);

    }

    public static int encodeDayOfYearFromDate(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    public static int encodeDayOfYearFromString(String date) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        cal.setTime(sdf.parse(date));// all done
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    public static String parseRowToString(int row){
        String[] rowDictionary = {"A","B","C","D","E"};
        return rowDictionary[row];
    }


    public static Drawable parseThumbnailToDrawable(String rawByteArray,Context c){
        byte[] byteArray = Base64.decode(rawByteArray, Base64.DEFAULT);
        return RoundedBitmapDrawableFactory.create(c.getResources(),BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
    }
}
