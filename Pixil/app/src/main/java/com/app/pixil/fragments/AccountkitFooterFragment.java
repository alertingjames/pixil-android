package com.app.pixil.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.pixil.R;

public class AccountkitFooterFragment extends Fragment {

    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.accountkit_footer, container, false);

        return view;
    }
}
