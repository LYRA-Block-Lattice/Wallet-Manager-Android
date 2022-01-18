package com.lyrawallet.Ui;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lyrawallet.Crypto.CryptoSignatures;
import com.lyrawallet.MainActivity;

import java.util.Locale;

public class UiHelpers {
    public static void showKeyboard(View view, EditText text) {
        /*InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!imm.isAcceptingText()) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    text.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0f, 0f, 0));
                    text.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0f, 0f, 0));
                }
            }, 500);
        }*/
    }

    public static void showKeyboard(View view){
        //InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        /*InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!imm.isAcceptingText()) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }*/

    }

    public static void closeKeyboard(View view){
        /*InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }*/
       /*if(UiHelpers.keyboardIsVisible(view)) {
            InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }*/
    }

    public static boolean keyboardIsVisible(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return inputMethodManager.isActive();
    }

    public static String getShortAccountId(String id, int len) {
        if(!CryptoSignatures.validateAccountId(id))
            return "";
        return String.format(Locale.US, "%s...%s", id.substring(0, len), id.substring(id.length() - 1 - len));
    }
}
