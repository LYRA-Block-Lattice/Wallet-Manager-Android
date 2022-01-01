package com.lyrawallet.UserActions;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Api.Network.NetworkRpc;
import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Ui.UiHelpers;

public class UserActionsRpcNode extends MainActivity implements NetworkRpc.RpcTaskInformer {

    public String receiveResult = null;
    public String sendResult = null;

    public UserActionsRpcNode() {

    }

    public void actionHistory() {
        new NetworkRpc(this).execute(Global.getCurrentNetworkName() + ";" + Global.getSelectedAccountName(),
                "History",
                Accounts.getAccount(),
                "0", String.valueOf(System.currentTimeMillis()), "0");
    }

    public void actionReceive() {
        NetworkRpc.RpcTaskInformer activity = this;
        final EditText passEditText = new EditText(this);
        // Put EditText in password mode
        passEditText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        passEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        View v = new View(this);
        UiHelpers.showKeyboard(v.getRootView(), passEditText);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.str_dialog_title)
                .setMessage(R.string.str_dialog_message)
                .setView(passEditText)
                .setPositiveButton(R.string.str_dialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = String.valueOf(passEditText.getText());
                        new NetworkRpc(activity, password).execute(Global.getCurrentNetworkName() + ";" + Global.getSelectedAccountName(),
                                "Receive",
                                Accounts.getAccount());
                    }
                })
                .setNegativeButton(R.string.str_dialog_cancel, null)
                .create();
        dialog.show();
    }

    public void actionSend(String amount, String token, String destinationId) {
        NetworkRpc.RpcTaskInformer activity = this;
        final EditText passEditText = new EditText(this);
        // Put EditText in password mode
        passEditText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        passEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        View v = new View(this);
        UiHelpers.showKeyboard(v.getRootView(), passEditText);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.str_dialog_title)
                .setMessage(R.string.str_dialog_message)
                .setView(passEditText)
                .setPositiveButton(R.string.str_dialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = String.valueOf(passEditText.getText());
                        new NetworkRpc(activity, password).execute(Global.getCurrentNetworkName() + ";" + Global.getSelectedAccountName() + ";" + token + ";" + destinationId + ";" + amount,
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
        if(output[0].equals("Receive")) {
            receiveResult = output[0] + "^" + output[1] + "^" + output[2];
            System.out.println(output[0] + "^" + output[1] + "^" + output[2]);
        } else if(output[0].equals("Send")) {
            receiveResult = output[0] + "^" + output[1] + "^" + output[2];
            System.out.println(output[0] + "^" + output[1] + "^" + output[2]);
        } else if(output[0].equals("History")) {
            receiveResult = output[0] + "^" + output[1] + "^" + output[2];
            System.out.println(output[0] + "^" + output[1] + "^" + output[2]);
        }
    }
    @Override
    public void onRpcNewEvent(String[] output) {
        if(output[0].equals("Receive")) {
            sendResult = output[0] + "^" + output[1] + "^" + output[2];
        } else if(output[0].equals("Send")) {
            sendResult = output[0] + "^" + output[1] + "^" + output[2];
        } else if(output[0].equals("History")) {
            sendResult = output[0] + "^" + output[1] + "^" + output[2];
        }
    }
}
