package com.lyrawallet.Actions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Api.Rpc;
import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Ui.Helpers;

public class UserRpcActions extends MainActivity implements Rpc.RpcTaskInformer {

    public String receiveResult = null;
    public String sendResult = null;

    public UserRpcActions() {

    }

    public void actionHistory() {
        new Rpc(this).execute(Global.getCurrentNetworkName() + ";History;" + Global.selectedAccountName,
                "History",
                Accounts.getAccount(),
                "0", String.valueOf(System.currentTimeMillis()), "0");
    }

    public void actionReceive() {
        MainActivity activity = this;
        final EditText passEditText = new EditText(this);
        // Put EditText in password mode
        passEditText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        passEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        View v = new View(this);
        Helpers.showKeyboard(v.getRootView(), passEditText);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.str_dialog_title)
                .setMessage(R.string.str_dialog_message)
                .setView(passEditText)
                .setPositiveButton(R.string.str_dialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = String.valueOf(passEditText.getText());
                        new Rpc(activity, password).execute(Global.getCurrentNetworkName() + ";Receive:" + ";" + Global.selectedAccountName,
                                "Receive",
                                Accounts.getAccount());
                    }
                })
                .setNegativeButton(R.string.str_dialog_cancel, null)
                .create();
        dialog.show();
    }

    public void actionSend(String amount, String token, String destinationId) {
        MainActivity activity = this;
        final EditText passEditText = new EditText(this);
        // Put EditText in password mode
        passEditText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        passEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        View v = new View(this);
        Helpers.showKeyboard(v.getRootView(), passEditText);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.str_dialog_title)
                .setMessage(R.string.str_dialog_message)
                .setView(passEditText)
                .setPositiveButton(R.string.str_dialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = String.valueOf(passEditText.getText());
                        new Rpc(activity, password).execute(Global.getCurrentNetworkName() + ";Send;" + Global.selectedAccountName + ";" + token + ";" + destinationId + ";" + amount,
                                "Send",
                                Accounts.getAccount());
                    }
                })
                .setNegativeButton(R.string.str_dialog_cancel, null)
                .create();
        dialog.show();
    }

    @Override
    public void onRpcTaskDone(String[] output) {
        if(output[1].equals("Receive")) {
            receiveResult = output[0] + "^" + output[1] + "^" + output[2];
            System.out.println(output[0] + "^" + output[1] + "^" + output[2]);
        } else if(output[1].equals("Send")) {
            sendResult = output[0] + "^" + output[1] + "^" + output[2];
            System.out.println(output[0] + "^" + output[1] + "^" + output[2]);
        } else if(output[1].equals("History")) {
            sendResult = output[0] + "^" + output[1] + "^" + output[2];
            System.out.println(output[0] + "^" + output[1] + "^" + output[2]);
        }
    }
    @Override
    public void onRpcNewEvent(String[] output) {
        if(output[0].split(";")[1].equals("Receive")) {
            receiveResult = output[0] + "^" + output[1];
        } else if(output[0].split(";")[1].equals("Send")) {
            sendResult = output[0] + "^" + output[1];
        } else if(output[0].split(";")[1].equals("History")) {
            sendResult = output[0] + "^" + output[1];
        }
    }
}
