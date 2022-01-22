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
    private Context ctx;
    private String[] contentArray;
    private Integer[] imageArray;
    private int DisabledItem = -1;

    public TokensSpinnerAdapter(Context context, int resource, String[] objects,
                                Integer[] imageArray) {
        super(context,  R.layout.send_token_select_spinner_entry, R.id.send_token_select_spinner_entry_text, objects);
        this.ctx = context;
        this.contentArray = objects;
        this.imageArray = imageArray;
    }

    public TokensSpinnerAdapter(Context context, int resource, String[] objects,
                                Integer[] imageArray, int disabledItem) {
        super(context,  R.layout.send_token_select_spinner_entry, R.id.send_token_select_spinner_entry_text, objects);
        this.ctx = context;
        this.contentArray = objects;
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
            convertView = inflater.inflate(R.layout.send_token_select_spinner_entry, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.send_token_select_spinner_entry_text);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.send_token_select_spinner_entry_image);
        textView.setText(contentArray[position]);
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
