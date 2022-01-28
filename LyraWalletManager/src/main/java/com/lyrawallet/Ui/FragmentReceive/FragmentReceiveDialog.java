package com.lyrawallet.Ui.FragmentReceive;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Ui.TokensSpinnerAdapter;
import com.lyrawallet.Ui.UiHelpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentReceiveDialog extends DialogFragment {

    public FragmentReceiveDialog() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.GeneralDialogTheme);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_request_token, container, false);

        Activity activity = getActivity();
        if (activity == null) {
            Snackbar.make(v, "Activity = null.", Snackbar.LENGTH_LONG)
                    .setAction("", null).show();
            return v;
        }

        List<String> tickerList = new ArrayList<>();
        for (int i = 2; i < GlobalLyra.TokenIconList.length; i++) {
            tickerList.add(GlobalLyra.TokenIconList[i].first);
        }
        Spinner tokenSpinner = (Spinner) v.findViewById(R.id.receiveTokenSelectSpinner);
        TokensSpinnerAdapter adapter = new TokensSpinnerAdapter(v.getContext(), R.layout.send_token_select_spinner_entry,
                tickerList.toArray(new String[0]), UiHelpers.tickerToImage(tickerList).toArray(new Integer[0]));
        adapter.setDropDownViewResource(R.layout.send_token_select_spinner_entry_first);
        tokenSpinner.setAdapter(adapter);
        tokenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(view != null) {
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        EditText amountEditText = v.findViewById(R.id.receiveTokenAmountValue);
        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() > 0) {
                    try {
                        Double.parseDouble(s.toString());
                    } catch (NumberFormatException e) {
                        amountEditText.setText(s.subSequence(0, before - 1));
                    }
                }
            }
        });

        Button bookAccountButton = (Button) v.findViewById(R.id.dialogReceiveOkButton);
        bookAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(getActivity() == null) {
                    MainActivity.goBack();
                    return;
                }
                if(amountEditText.getText().length() == 0) {
                    MainActivity.goBack();
                    return;
                }
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("id", Global.getSelectedAccountId());
                    obj.put("amount", amountEditText.getText().toString());
                    obj.put("ticker", adapter.getItem(tokenSpinner.getSelectedItemPosition()));
                    FragmentReceive.setRequestQrStr(obj.toString());
                    //UiHelpers.textToQrEncode(getContext(), obj.toString(), qrImageView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MainActivity.goBack();
            }
        });
        return v;
    }
}
