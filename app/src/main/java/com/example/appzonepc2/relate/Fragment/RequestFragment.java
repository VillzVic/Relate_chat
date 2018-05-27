package com.example.appzonepc2.relate.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appzonepc2.relate.R;

import java.util.zip.Inflater;

/**
 * Created by appzonepc2 on 15/02/2018.
 */

public class RequestFragment extends android.support.v4.app.Fragment {
    private RecyclerView requestList;
    View view;

    public RequestFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.requestfragment,container,false);
//
//        requestList = view.findViewById(R.id.request_list);
//
//        requestList.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        linearLayoutManager.setStackFromEnd(true);
//        linearLayoutManager.setReverseLayout(true);
//
//        requestList.setLayoutManager(linearLayoutManager);
        return view;
    }
}
