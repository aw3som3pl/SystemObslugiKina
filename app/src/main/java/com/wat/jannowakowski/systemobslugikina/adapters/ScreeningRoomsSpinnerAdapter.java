package com.wat.jannowakowski.systemobslugikina.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wat.jannowakowski.systemobslugikina.R;
import com.wat.jannowakowski.systemobslugikina.abstractClasses.EnumHandler;
import com.wat.jannowakowski.systemobslugikina.activities.models.ScreeningRoom;

import java.util.ArrayList;

/**
 * Created by Jan Nowakowski on 24.01.2019.
 */

public class ScreeningRoomsSpinnerAdapter extends ArrayAdapter<String>{

    ArrayList<ScreeningRoom> screeningRoomsList;
    Context mContext;

    public ScreeningRoomsSpinnerAdapter(@NonNull Context context, ArrayList<ScreeningRoom> screeningRoomsList) {
        super(context, R.layout.screening_room_node);
        this.screeningRoomsList = screeningRoomsList;
        this.mContext = context;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return screeningRoomsList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.screening_room_node, parent, false);
            mViewHolder.mDesignation = convertView.findViewById(R.id.designation);
            mViewHolder.mSeatCount = convertView.findViewById(R.id.seat_count);
            mViewHolder.mScreeningTechnology = convertView.findViewById(R.id.movie_screening_technology);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.mDesignation.setText("Sala " + String.valueOf(screeningRoomsList.get(position).getReferenceNumber()));
        mViewHolder.mSeatCount.setText(String.valueOf(screeningRoomsList.get(position).getMaxSeatCount()));
        mViewHolder.mScreeningTechnology.setText(EnumHandler.parseScreeningTechnology(mContext,screeningRoomsList.get(position).getProjectionTechnology()));

        return convertView;
    }

    private static class ViewHolder {
        TextView mDesignation;
        TextView mSeatCount;
        TextView mScreeningTechnology;
    }
}
