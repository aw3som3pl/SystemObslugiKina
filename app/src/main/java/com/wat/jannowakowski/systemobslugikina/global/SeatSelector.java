package com.wat.jannowakowski.systemobslugikina.global;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;

import com.wat.jannowakowski.systemobslugikina.R;


public class SeatSelector extends android.support.v7.widget.AppCompatCheckBox {
    static private final int SELECTED = 1;
    static private final int OCCUPIED = 2;
    static private final int FREE = 0;
    private int state;

    public SeatSelector(Context context) {
        super(context);
        init();
    }

    public SeatSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SeatSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        state = FREE;
        updateBtn();

        setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            // checkbox status is changed from uncheck to checked.
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (state) {
                    case SELECTED:
                        state = OCCUPIED;
                        break;
                    case OCCUPIED:
                        state = FREE;
                        break;
                    case FREE:
                        state = SELECTED;
                        break;
                }
                updateBtn();
            }
        });

    }

    private void updateBtn() {
        int btnDrawable = R.drawable.seat_free;
        switch (state) {
            case SELECTED:
                btnDrawable = R.drawable.seat_chosen;
                break;
            case OCCUPIED:
                btnDrawable = R.drawable.seat_occupied;
                break;
            case FREE:
                btnDrawable = R.drawable.seat_free;
                break;
        }
        setButtonDrawable(btnDrawable);

    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        updateBtn();
    }
}
