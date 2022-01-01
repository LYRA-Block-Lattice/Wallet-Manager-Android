package com.lyrawallet.Util;

import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

//Accept Characters Only

//walletNameEditText.setFilters(new InputFilter[]{TextFilters.getOnlyCharactersFilter()});
//Accept Digits and Characters
// walletNameEditText.setFilters(new InputFilter[]{TextFilters.getCharactersAndDigitsFilter()});
//Accept Digits and Characters and SpaceBar
// walletNameEditText.setFilters(new InputFilter[]{TextFilters.getCustomInputFilter(true,true,true)});

public class UtilTextFilters {
    public static InputFilter getOnlyCharactersFilter() {
        return getCustomInputFilter(true, false, false);
    }

    public static InputFilter getCharactersAndDigitsFilter() {
        return getCustomInputFilter(true, true, false);
    }

    public static InputFilter getCharactersDigitsAndSpaceFilter() {
        return getCustomInputFilter(true, true, true);
    }

    public static InputFilter getCustomInputFilter(final boolean allowCharacters, final boolean allowDigits, final boolean allowSpaceChar) {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c)) {
                        sb.append(c);
                    } else {
                        keepOriginal = false;
                    }
                }
                if (keepOriginal) {
                    return null;
                } else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c) {
                if (Character.isLetter(c) && allowCharacters) {
                    return true;
                }
                if (Character.isDigit(c) && allowDigits) {
                    return true;
                }
                if (Character.isSpaceChar(c) && allowSpaceChar) {
                    return true;
                }
                return false;
            }
        };
    }
}
