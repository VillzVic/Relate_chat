package com.example.appzonepc2.relate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView circleImageView;
    private TextView username, status;
    private Button changeImagebtn, changeStatusbtn;
    private final static int REQUEST_CODE = 1;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    StorageReference thumbStorageReference;
    ProgressDialog progressDialog;

    Bitmap thumb_bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        circleImageView = findViewById(R.id.profilepic);
        username = findViewById(R.id.username);
        status = findViewById(R.id.userstatus);
        changeImagebtn = findViewById(R.id.changeImage);
        changeStatusbtn = findViewById(R.id.changeStatus);
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        databaseReference.keepSynced(true);
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_image");

        thumbStorageReference = FirebaseStorage.getInstance().getReference().child("thumb_profile_image");

        databaseReference.addValueEventListener(new ValueEventListener() {
            //todo check if there is internet
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Address = dataSnapshot.child("Address").getValue().toString();
                String Email = dataSnapshot.child("Email").getValue().toString();
                String phone_number = dataSnapshot.child("phone_number").getValue().toString();
                String user_image = dataSnapshot.child("user_image").getValue().toString();
                String user_name = dataSnapshot.child("user_name").getValue().toString();
                String user_status = dataSnapshot.child("user_status").getValue().toString();
                final String user_thumb_image = dataSnapshot.child("user_thumb_image").getValue().toString();

                username.setText(user_name);
                status.setText(user_status);
                if(user_image.equals("default_profile")){
                    //set the account profile to be the dummy one
                }else{
//                    RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.profileview);
//                    Glide.with(SettingsActivity.this).load(user_thumb_image).apply(requestOptions).into(circleImageView);
                    Picasso.with(SettingsActivity.this).load(user_thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profileview)
                            .into(circleImageView, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(getApplicationContext()).load(user_thumb_image).placeholder(R.drawable.profileview).into(circleImageView);
                                }
                            });
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
            }
        });

        changeStatusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, StatusActivity.class);
                intent.putExtra("Status",status.getText().toString());
                startActivity(intent);
            }
        });

        changeImagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, REQUEST_CODE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data!=null){
//            Uri imageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data); //get the data that was selected-the cropped one
            if (resultCode == RESULT_OK) {

                progressDialog.setTitle("Updating Profile Image");
                progressDialog.setMessage("Please wait");
                progressDialog.show();

                String uid =mAuth.getCurrentUser().getUid();

                Uri resultUri = result.getUri(); //get cropped image

                //COMPRESS image

                File thumb_filePathUri = new File(resultUri.getPath()); //get the file
                //you have to save it in a bitmap so that it can be compressable
                try {
                    thumb_bitmap = new Compressor(this)
                                    .setMaxWidth(200)
                                    .setMaxHeight(200)
                                    .setQuality(70)
                                    .compressToBitmap(thumb_filePathUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,70,byteArrayOutputStream); //compress bitmap
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray(); //compressed image


                final StorageReference thumb_filePath = thumbStorageReference.child(uid +".jpg");

                StorageReference filePath = storageReference.child(uid +".jpg"); //get the path
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() { //put image in the path
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) { //put the  file in the db
                        if(task.isSuccessful()){
                            Toast.makeText(SettingsActivity.this,"Updating your profile image...",Toast.LENGTH_LONG).show();

                            final String imageDownloadUrl = task.getResult().getDownloadUrl().toString(); //url for normal image to store in the db

                            //upload the compressed image
                            UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte); //put the image into the filepath in firebase
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    String compressedImageUrl = thumb_task.getResult().getDownloadUrl().toString(); //url for compressed image

                                    if(task.isSuccessful()){
                                        Map update_user_data = new HashMap();
                                        update_user_data.put("user_image",imageDownloadUrl);
                                        update_user_data.put("user_thumb_image",compressedImageUrl);

                                        databaseReference.updateChildren(update_user_data)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                            Toast.makeText(SettingsActivity.this, "Image Uploaded successfully", Toast.LENGTH_SHORT).show();
                                                        else
                                                            Toast.makeText(SettingsActivity.this, "Error Uploading image", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    }
                                                });
                                    }
                                }
                            });


                            //saving image link to the database so that we can retrieve it later
                            //get the imageurl

                        }else{
                            Toast.makeText(getApplicationContext(),"Error Uploading image, please try again",Toast.LENGTH_LONG).show();

                            progressDialog.dismiss();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
