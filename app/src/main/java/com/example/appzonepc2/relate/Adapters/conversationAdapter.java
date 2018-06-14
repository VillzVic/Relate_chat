package com.example.appzonepc2.relate.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeBounds;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.appzonepc2.relate.R;
import com.example.appzonepc2.relate.model.message_model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by appzonepc2 on 28/03/2018.
 */

public class conversationAdapter extends RecyclerView.Adapter {

    private ArrayList<message_model> usermessageList;
    private Context ctx;
    private FirebaseAuth mAuth;
    DatabaseReference userDatabaseReference;


    public conversationAdapter(ArrayList<message_model> usermessageList, Context ctx) {
        this.usermessageList = usermessageList;
        this.ctx = ctx;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        mAuth = FirebaseAuth.getInstance();

        switch (viewType){
            case message_model.TEXT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_model_layout, parent, false);
                return new textviewHolder(view);

                case message_model.IMAGE_TYPE:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_image_layout, parent, false);
                    return new imageViewHolder(view);


        }
        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {
        final message_model model = usermessageList.get(position);
        String message_sender_id = mAuth.getCurrentUser().getUid();

        String from_user_id = model.getFrom();
        long time = model.getTime(); //cast this time
        Boolean seen = model.getSeen();
        String message_type = model.getType();

        switch(message_type) {
            case "text":
                if (message_sender_id.equals(from_user_id)) {
                    ((textviewHolder) viewHolder).messagetext.setBackgroundResource(R.drawable.message_background);

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ((textviewHolder) viewHolder).messagetext.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);

                    ((textviewHolder) viewHolder).messagetext.setTextColor(Color.WHITE);
                } else {
                    ((textviewHolder) viewHolder).messagetext.setGravity(Gravity.START);
                }

                ((textviewHolder) viewHolder).messagetext.setText(model.getMessage());

                break;
            case "image":
                if (message_sender_id.equals(from_user_id)) {
         RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ((imageViewHolder)viewHolder).imageView.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        }

        Picasso.with(((imageViewHolder)viewHolder).imageView.getContext()).load(model.getMessage()).placeholder(R.drawable.image_placeholder).networkPolicy(NetworkPolicy.OFFLINE)
                .into(((imageViewHolder)viewHolder).imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(((imageViewHolder)viewHolder).imageView.getContext()).load(model.getMessage()).placeholder(R.drawable.image_placeholder).into(((imageViewHolder) viewHolder).imageView);
                    }
                });
                break;
        }


    }


    @Override
    public int getItemCount() {
        return usermessageList.size();
    }



    @Override
    public int getItemViewType(int position) {


        switch(usermessageList.get(position).getType()){
            case "text":
                return message_model.TEXT_TYPE;

            case "image":
                return message_model.IMAGE_TYPE;

                default:
                    return - 1;
        }
    }

    public class textviewHolder extends RecyclerView.ViewHolder {

        private TextView messagetext;

        public textviewHolder(View itemView) {
            super(itemView);

            messagetext = itemView.findViewById(R.id.message_text);

        }
    }

    public class imageViewHolder extends RecyclerView.ViewHolder{

        private AppCompatImageView imageView;

        public imageViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.message_img);
        }
    }
}
