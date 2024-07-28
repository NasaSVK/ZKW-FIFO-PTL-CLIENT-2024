package com.symbol.kepzetclient;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class HorizontalNumberPicker extends LinearLayout {
    private EditText et_number;
    public int min, max;

    private EditText etxValue;
    public EditText getEtxValue() {
        return etxValue;
    }


    public HorizontalNumberPicker(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        inflate(context, R.layout.numberpicker_horizontal, this);

        et_number = findViewById(R.id.et_number);
        etxValue = et_number;

        final Button btn_less = findViewById(R.id.btn_less);
        btn_less.setOnClickListener(new AddHandler(-1));

        final Button btn_more = findViewById(R.id.btn_more);
        btn_more.setOnClickListener(new AddHandler(1));


    }

    @Override
    public void setForeground(Drawable foreground) {
        super.setForeground(foreground);
        //et_number.setForeground(foreground);

    }

    public void setTextColor(int pColor){
        et_number.setTextColor(pColor);
    }

    public int getTexColor() {
        return et_number.getCurrentTextColor();
    }



    /***
     * HANDLERS
     **/

    private class AddHandler implements OnClickListener {
        final int diff;

        public AddHandler(int diff) {
            this.diff = diff;
        }

        @Override
        public void onClick(View v) {
            int newValue = getValue() + diff;
            if (newValue < min) {
                newValue = min;
            } else if (newValue > max) {
                newValue = max;
            }
            et_number.setText(String.valueOf(newValue));
        }
    }

    /***
     * GETTERS & SETTERS
     */

    public int getValue() {
        if (et_number != null) {
            try {
                final String value = et_number.getText().toString();
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                Log.e("HorizontalNumberPicker", ex.toString());
            }
        }
        return 0;
    }

    public void setValue(final int value) {
        if (et_number != null) {
            et_number.setText(String.valueOf(value));
        }
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}