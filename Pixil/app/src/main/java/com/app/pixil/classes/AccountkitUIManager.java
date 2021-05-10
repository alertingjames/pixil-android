package com.app.pixil.classes;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.app.pixil.fragments.AccountkitFooterFragment;
import com.facebook.accountkit.ui.BaseUIManager;
import com.facebook.accountkit.ui.ButtonType;
import com.facebook.accountkit.ui.LoginFlowState;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.TextPosition;

public class AccountkitUIManager extends BaseUIManager {
    public static final String LOGIN_TYPE_EXTRA = "loginType";
    public static final String SWITCH_LOGIN_TYPE_EVENT = "switch-login-type";

    public AccountkitUIManager(
            final int themeResourceId) {
        super(themeResourceId);
    }

    private AccountkitUIManager(final Parcel source) {
        super(source);
    }

    @Override
    @Nullable
    public Fragment getBodyFragment(final LoginFlowState state) {
        return null;
    }

    @Override
    @Nullable
    public Fragment getFooterFragment(final LoginFlowState state) {
        final int progress;
        switch (state) {
            case PHONE_NUMBER_INPUT:
            case EMAIL_INPUT:
                progress = 1;
                break;
            case SENDING_CODE:
            case SENT_CODE:
                progress = 2;
                break;
            case CODE_INPUT:
            case EMAIL_VERIFY:
            case CONFIRM_ACCOUNT_VERIFIED:
                progress = 3;
                break;
            case VERIFYING_CODE:
            case CONFIRM_INSTANT_VERIFICATION_LOGIN:
                progress = 4;
                break;
            case VERIFIED:
                progress = 5;
                break;
            case RESEND:
            case ERROR:
            case NONE:
            default:
                return null;
        }
        final AccountkitFooterFragment fragment = new AccountkitFooterFragment();
        return fragment;
    }

    @Override
    @Nullable
    public Fragment getHeaderFragment(final LoginFlowState state) {
        return null;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
    }

    public static final Creator<AccountkitUIManager> CREATOR
            = new Creator<AccountkitUIManager>() {
        @Override
        public AccountkitUIManager createFromParcel(final Parcel source) {
            return new AccountkitUIManager(source);
        }

        @Override
        public AccountkitUIManager[] newArray(final int size) {
            return new AccountkitUIManager[size];
        }
    };
}

