package com.wat.jannowakowski.systemobslugikina.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wat.jannowakowski.systemobslugikina.R;
import com.wat.jannowakowski.systemobslugikina.abstractClasses.EnumHandler;
import com.wat.jannowakowski.systemobslugikina.activities.models.Movie;
import com.wat.jannowakowski.systemobslugikina.activities.models.ScreeningRoom;

import java.util.ArrayList;

/**
 * Created by Jan Nowakowski on 24.01.2019.
 */

public class MoviesSpinnerAdapter extends ArrayAdapter<String>{

    private ArrayList<Movie> MoviesList;
    private Context mContext;

    public MoviesSpinnerAdapter(@NonNull Context context, ArrayList<Movie> moviesList) {
        super(context, R.layout.movie_node);
        this.MoviesList = moviesList;
        this.mContext = context;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return MoviesList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.movie_node, parent, false);
            mViewHolder.mTitle = convertView.findViewById(R.id.movie_title);
            mViewHolder.mScreeningTechnology = convertView.findViewById(R.id.movie_screening_technology);
            mViewHolder.mAgeRestriction = convertView.findViewById(R.id.movie_age_restriction);
            mViewHolder.mLanguage = convertView.findViewById(R.id.movie_language);
            mViewHolder.mFooter = convertView.findViewById(R.id.movie_footer);
            mViewHolder.mThumbnail = convertView.findViewById(R.id.movie_thumbnail);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.mTitle.setText(MoviesList.get(position).getTitle());
        mViewHolder.mScreeningTechnology.setText(EnumHandler.parseScreeningTechnology(mContext,MoviesList.get(position).getScreeningTechnology()));
        mViewHolder.mAgeRestriction.setText(EnumHandler.parseAgeRestriction(mContext,MoviesList.get(position).getAgeRating()));
        mViewHolder.mLanguage.setText(EnumHandler.parseLanguageMethod(mContext,MoviesList.get(position).getLanguageMode()));
        mViewHolder.mFooter.setVisibility(View.GONE);
        mViewHolder.mThumbnail.setVisibility(View.GONE);

        return convertView;
    }

    private static class ViewHolder {
        TextView mTitle;
        TextView mScreeningTechnology;
        TextView mAgeRestriction;
        TextView mLanguage;
        LinearLayout mFooter;
        ImageView mThumbnail;
    }
}
