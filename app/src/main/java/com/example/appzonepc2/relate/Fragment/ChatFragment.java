package com.example.appzonepc2.relate.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appzonepc2.relate.R;

/**
 * Created by appzonepc2 on 15/02/2018.
 */

public class ChatFragment extends Fragment {
    String message;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chatfragment,container,false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new messageReceiver(), new IntentFilter("last_message"));
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(new messageReceiver());
    }


    public class messageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            message = intent.getStringExtra("message");
        }}
}


