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
    private RecyclerViewClickListener mListener = null;


        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            private View screeningView;

            private MyViewHolder(View v,RecyclerViewClickListener listener) {
                super(v);
                screeningView = v;
                mListener = listener;
                v.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {
                mListener.onClick(v, getAdapterPosition());
            }
        }



    public RepertoirListAdapter(ArrayList<Screening> myScreeningsList,RecyclerViewClickListener listener) {
        mListener = listener;
        screeningsList = myScreeningsList;
    }


        @Override
        public RepertoirListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            Context context = parent.getContext();
            View newScreeningView = LayoutInflater.from(context).inflate(R.layout.movie_node, parent, false);
            return new MyViewHolder(newScreeningView,mListener);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            if (holder instanceof MyViewHolder) {
                MyViewHolder myViewHolder = holder;

                ImageView screeningThumbnail = holder.screeningView.findViewById(R.id.movie_thumbnail);
                ImageView premiereIcon = holder.screeningView.findViewById(R.id.premiere_icon);
                TextView screeningTitle = holder.screeningView.findViewById(R.id.movie_title);
                TextView screeningTechnology = holder.screeningView.findViewById(R.id.movie_screening_technology);
                TextView screeningAgeRestriction = holder.screeningView.findViewById(R.id.movie_age_restriction);
                TextView screeningLanguageMode = holder.screeningView.findViewById(R.id.movie_language);
                TextView screeningDate = holder.screeningView.findViewById(R.id.movie_screening_date);
                TextView screeningTime = holder.screeningView.findViewById(R.id.movie_screening_time);

                if (screeningsList.get(position).isPremiere())
                    premiereIcon.setVisibility(View.VISIBLE);
                screeningThumbnail.setImageDrawable(screeningsList.get(position).getMovie().getThumbnail());
                screeningTitle.setText(screeningsList.get(position).getMovie().getTitle());
                screeningTechnology.setText(EnumHandler.parseScreeningTechnology(holder.screeningView.getContext(), screeningsList.get(position).getScreeningTechnology()));
                screeningAgeRestriction.setText(EnumHandler.parseAgeRestriction(holder.screeningView.getContext(), screeningsList.get(position).getMovie().getAgeRating()));
                screeningLanguageMode.setText(EnumHandler.parseLanguageMethod(holder.screeningView.getContext(), screeningsList.get(position).getMovie().getLanguageMode()));
                screeningDate.setText(screeningsList.get(position).getDateOfScreening());
                screeningTime.setText(screeningsList.get(position).getTimeOfScreening());
            }

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return screeningsList.size();
        }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }

}
