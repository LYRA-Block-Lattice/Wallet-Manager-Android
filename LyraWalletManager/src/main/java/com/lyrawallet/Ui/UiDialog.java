package com.lyrawallet.Ui;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.lyrawallet.MainActivity;
import com.lyrawallet.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UiDialog extends MainActivity {
    public static AlertDialog dialogWindow;
    public static void showDialogStatus(int title, int message) {
        if(dialogWindow != null)
            dialogWindow.dismiss();
        dialogWindow = new AlertDialog.Builder(getInstance())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        dialogWindow.show();
    }

    public static void showDialogStatus(int title, int message, Method onClickEvent) {
        if(dialogWindow != null)
            dialogWindow.dismiss();
        dialogWindow = new AlertDialog.Builder(getInstance())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            onClickEvent.invoke(null);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .create();
        dialogWindow.show();
    }

    public static void showDialogStatus(int title, String message) {
        if(dialogWindow != null)
            dialogWindow.dismiss();
        dialogWindow = new AlertDialog.Builder(getInstance())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        dialogWindow.show();
    }

    public static void showDialogStatus(int title, String message, Method onClickEvent) {
        if(dialogWindow != null)
            dialogWindow.dismiss();
        dialogWindow = new AlertDialog.Builder(getInstance())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            onClickEvent.invoke(null);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .create();
        dialogWindow.show();
    }

    public static void showDialogStatus(int title) {
        if(dialogWindow != null)
            dialogWindow.dismiss();
        dialogWindow = new AlertDialog.Builder(getInstance())
                .setTitle(title)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        dialogWindow.show();
    }

    public static void showDialogStatus(String title) {
        if(dialogWindow != null)
            dialogWindow.dismiss();
        dialogWindow = new AlertDialog.Builder(getInstance())
                .setTitle(title)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        dialogWindow.show();
    }

    public static void showDialogStatus(int title, Method onClickEvent) {
        if(dialogWindow != null)
            dialogWindow.dismiss();
        dialogWindow = new AlertDialog.Builder(getInstance())
                .setTitle(title)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            onClickEvent.invoke(null);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .create();
        dialogWindow.show();
    }

}
