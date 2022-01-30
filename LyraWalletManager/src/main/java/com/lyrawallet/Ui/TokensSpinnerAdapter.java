package com.lyrawallet.Ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lyrawallet.R;

public class TokensSpinnerAdapter extends ArrayAdapter<String> {
    private int Resource = 0;
    private final Context ctx;
    private final String[] TickerName;
    private final Integer[] imageArray;
    private int DisabledItem = -1;

    public TokensSpinnerAdapter(Context context, int resource, String[] tickerName,
                                Integer[] imageArray) {
        super(context, resource, R.id.sendTokenSelectSpinnerEntryText, tickerName);
        Resource = resource;
        this.ctx = context;
        this.TickerName = tickerName;
        this.imageArray = imageArray;
    }

    public TokensSpinnerAdapter(Context context, int resource, String[] tickerName,
                                Integer[] imageArray, int disabledItem) {
        super(context, resource, R.id.sendTokenSelectSpinnerEntryText, tickerName);
        Resource = resource;
        this.ctx = context;
        this.TickerName = tickerName;
        this.imageArray = imageArray;
        this.DisabledItem = disabledItem;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null) {
            convertView = inflater.inflate(Resource, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.sendTokenSelectSpinnerEntryText);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.sendTokenSelectSpinnerEntryImage);
        textView.setText(TickerName[position]);
        imageView.setImageResource(imageArray[position]);
        if(position == this.DisabledItem) {
            convertView.setEnabled(false);
            convertView.setClickable(false);
            textView.setClickable(false);
            imageView.setClickable(false);
        }
        return convertView;
    }
}
