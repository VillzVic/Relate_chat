package com.example.appzonepc2.relate.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appzonepc2.relate.R;

/**
 * Created by appzonepc2 on 15/02/2018.
 */

public class FriendsFragment extends android.support.v4.app.Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.friendfragment,container,false);

        return view;
    }
}
