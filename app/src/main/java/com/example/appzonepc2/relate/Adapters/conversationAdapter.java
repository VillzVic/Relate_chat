package com.example.appzonepc2.relate.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.appzonepc2.relate.R;
import com.example.appzonepc2.relate.model.message_model;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by appzonepc2 on 28/03/2018.
 */

public class conversationAdapter extends RecyclerView.Adapter<conversationAdapter.conversationViewHolder> {

    private ArrayList<message_model> usermessageList;
    private Context ctx;
    private FirebaseAuth mAuth;


    public conversationAdapter(ArrayList<message_model> usermessageList, Context ctx) {
        this.usermessageList = usermessageList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public conversationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
      View view  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_model_layout,viewGroup, false);
      mAuth = FirebaseAuth.getInstance();

      return new conversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull conversationViewHolder conversationViewHolder, int position) {
    message_model model = usermessageList.get(position);

    String message_sender_id = mAuth.getCurrentUser().getUid();

    String from_user_id = model.getFrom();
    long time = model.getTime(); //cast this time
    Boolean seen = model.getSeen();
    String type = model.getType();



    if(message_sender_id.equals(from_user_id)){
        conversationViewHolder.messagetext.setBackgroundResource(R.drawable.message_background);
        conversationViewHolder.messagetext.setGravity(Gravity.END);

        conversationViewHolder.messagetext.setTextColor(Color.WHITE);
    }else{
        conversationViewHolder.messagetext.setGravity(Gravity.START);
    }

    conversationViewHolder.messagetext.setText(model.getMessage());
//    conversationViewHolder.time.setText(String.valueOf());


    }

    @Override
    public int getItemCount() {
        return usermessageList.size();
    }


    public class conversationViewHolder extends RecyclerView.ViewHolder {

        private TextView messagetext;
        private CircleImageView imageView;c
        private TextView time;

        public conversationViewHolder(View itemView) {
            super(itemView);
            getAdapterPosition();

            messagetext = itemView.findViewById(R.id.message_text);
//            imageView = itemView.findViewById(R.id.message_image);
//            time = itemView.findViewById(R.id.message_time);
        }
    }
}
