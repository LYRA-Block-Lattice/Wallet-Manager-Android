package com.lyrawallet.Ui.FragmentSend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lyrawallet.R;

public class SendTokensSpinnerAdapter extends ArrayAdapter<String> {
    private int Resource = 0;
    private final Context ctx;
    private final String[] TickerNameArray;
    private final Integer[] ImageArray;

    public SendTokensSpinnerAdapter(Context context, int resource, String[] tickerName,
                                    Integer[] imageArray) {
        super(context,  resource, R.id.sendTokenSelectSpinnerEntryText, tickerName);
        Resource = resource;
        this.ctx = context;
        this.TickerNameArray = tickerName;
        this.ImageArray = imageArray;
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
        View row = inflater.inflate(Resource, parent, false);

        TextView textView = (TextView) row.findViewById(R.id.sendTokenSelectSpinnerEntryText);
        ImageView imageView = (ImageView)row.findViewById(R.id.sendTokenSelectSpinnerEntryImage);

        textView.setText(TickerNameArray[position]);
        imageView.setImageResource(ImageArray[position]);
        return row;
    }
}
