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
    private Context ctx;
    private String[] contentArray;
    private Integer[] imageArray;

    public SendTokensSpinnerAdapter(Context context, int resource, String[] objects,
                                    Integer[] imageArray) {
        super(context,  R.layout.send_token_select_spinner_entry, R.id.send_token_select_spinner_entry_text, objects);
        this.ctx = context;
        this.contentArray = objects;
        this.imageArray = imageArray;
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
        View row = inflater.inflate(R.layout.send_token_select_spinner_entry, parent, false);

        TextView textView = (TextView) row.findViewById(R.id.send_token_select_spinner_entry_text);
        textView.setText(contentArray[position]);

        ImageView imageView = (ImageView)row.findViewById(R.id.send_token_select_spinner_entry_image);
        imageView.setImageResource(imageArray[position]);
        return row;
    }
}
