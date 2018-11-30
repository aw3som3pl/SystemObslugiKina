package com.wat.jannowakowski.systemobslugikina.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.wat.jannowakowski.systemobslugikina.R;
import com.wat.jannowakowski.systemobslugikina.abstractClasses.EnumHandler;
import com.wat.jannowakowski.systemobslugikina.activities.models.Screening;

import java.util.ArrayList;


public class RepertoirListAdapter extends RecyclerView.Adapter<RepertoirListAdapter.MyViewHolder> {

    private ArrayList<Screening> screeningsList;
    private static ClickListener clickListener;


        public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

            public View screeningView;
            public MyViewHolder(View v) {
                super(v);
                v.setOnClickListener(this);
                v.setOnLongClickListener(this);
                screeningView = v;
            }

            @Override
            public void onClick(View v) {
                clickListener.onItemClick(getAdapterPosition(), v);
            }

            @Override
            public boolean onLongClick(View v) {
                clickListener.onItemLongClick(getAdapterPosition(), v);
                return true;
            }
        }

    public void setOnItemClickListener(ClickListener clickListener) {
        RepertoirListAdapter.clickListener = clickListener;
    }


    public RepertoirListAdapter(ArrayList<Screening> myScreeningsList) {
        screeningsList = myScreeningsList;
    }


        @Override
        public RepertoirListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            // create a new view
            View newScreeningView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_node, parent, false);
            MyViewHolder vh = new MyViewHolder(newScreeningView);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            ImageView screeningThumbnail = holder.screeningView.findViewById(R.id.movie_thumbnail);
            ImageView premiereIcon = holder.screeningView.findViewById(R.id.premiere_icon);
            TextView screeningTitle = holder.screeningView.findViewById(R.id.movie_title);
            TextView screeningTechnology = holder.screeningView.findViewById(R.id.movie_screening_technology);
            TextView screeningAgeRestriction = holder.screeningView.findViewById(R.id.movie_age_restriction);
            TextView screeningLanguageMode = holder.screeningView.findViewById(R.id.movie_language);
            TextView screeningDate = holder.screeningView.findViewById(R.id.movie_screening_date);
            TextView screeningTime = holder.screeningView.findViewById(R.id.movie_screening_time);

            if(screeningsList.get(position).isPremiere())
                premiereIcon.setVisibility(View.VISIBLE);
            //screeningThumbnail.setImageDrawable(screeningsList.get(position).getMovie().getThumbnail());
            screeningTitle.setText(screeningsList.get(position).getMovie().getTitle());
            screeningTechnology.setText(EnumHandler.parseScreeningTechnology(holder.screeningView.getContext(),screeningsList.get(position).getScreeningTechnology()));
            screeningAgeRestriction.setText(EnumHandler.parseAgeRestriction(holder.screeningView.getContext(),screeningsList.get(position).getMovie().getAgeRating()));
            screeningLanguageMode.setText(EnumHandler.parseLanguageMethod(holder.screeningView.getContext(),screeningsList.get(position).getMovie().getLanguageMode()));
            screeningDate.setText(screeningsList.get(position).getDateOfScreening());
            screeningTime.setText(screeningsList.get(position).getTimeOfScreening());

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return screeningsList.size();
        }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

}
