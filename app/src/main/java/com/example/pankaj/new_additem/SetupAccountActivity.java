package com.example.pankaj.new_additem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

public class SetupAccountActivity extends AppCompatActivity {

    private ImageView userProfileButton;
    private Button Finish;
    private EditText userName;
    final static int GALLERY_REQUEST=1;
    private static int MAX_LENGTH=10;
    private Uri imageuri;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageprofile_pics,imagePath;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account);

        //checkUserExist();

        userProfileButton=findViewById(R.id.imageButton);
        userName=findViewById(R.id.editTextSetName);
        Finish=findViewById(R.id.finishButton);
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("students");
        storageprofile_pics= FirebaseStorage.getInstance().getReference().child("profile_pics");
        progressDialog=new ProgressDialog(this);

        Finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SetUpAccount();
            }
        });

        userProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent GalleryIntend=new Intent(Intent.ACTION_GET_CONTENT);
                GalleryIntend.setType("image/*");
                startActivityForResult(GalleryIntend,GALLERY_REQUEST);
            }
        });
    }

    private void SetUpAccount() {

        final String name=userName.getText().toString().trim();
        final String user_id=firebaseAuth.getCurrentUser().getUid().toString();

        if(!TextUtils.isEmpty(name) && imageuri!=null){


            progressDialog.setMessage("Setup is in progress....");
            progressDialog.show();

            String random_name=random();

            imagePath=storageprofile_pics.child(random_name);
            System.out.println(random_name);
            imagePath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloaduri=taskSnapshot.getDownloadUrl();

                    databaseReference.child(user_id).child("name").setValue(name);
                    databaseReference.child(user_id).child("image").setValue(downloaduri.toString());
                    databaseReference.child(user_id).child("imageId").setValue(downloaduri.toString());
                    databaseReference.child(user_id).child("status").setValue("hey there!! I'am using LendIt App.Its cool");
                    databaseReference.child(user_id).child("address").setValue("Mnnit Allahabad");
                   // System.out.println("hheldlwlad");
                    progressDialog.dismiss();
                    startActivity(new Intent(SetupAccountActivity.this,MainPageNActivity.class));
                }
            });

        }
        else
        {
            Toast.makeText(SetupAccountActivity.this,"Fill all the field",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK)
        {
            imageuri=data.getData();
            userProfileButton.setImageURI(imageuri);
        }
    }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString()+"kk";
    }
    public void checkUserExist() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        } else {
            final String userId = firebaseAuth.getCurrentUser().getUid().toString();
            DatabaseReference databaseStudents = FirebaseDatabase.getInstance().getReference().child("students");
            databaseStudents.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(userId)) {

                    } else {


                        Toast.makeText(getApplicationContext(),"getting some error",Toast.LENGTH_LONG).show();

                        startActivity(new Intent(getApplicationContext(), MainPageNActivity.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
