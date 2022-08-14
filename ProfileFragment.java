package com.codefury.starthub;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private View profileView;
    private DatabaseReference RootRef;
    private Users users;
    private String username,name;
    private TextView Username;
    private TextView Name;
    private Button edit;
    private SQLiteDatabase db;
    private final String UserTag = "User";
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        users = new Users();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();
        Retrivedata();


    }

    private void sendUserToUpdateProfile() {
//        Intent intent = new Intent(getActivity(), updateProfile.class);
//        intent.putExtra("tag",UserTag);
//        startActivity(intent);

    }

    private void Retrivedata() {
//        && (snapshot.hasChild("username"))
        String CurrentUserId = mAuth.getCurrentUser().getUid();
        RootRef.child("Users").child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.exists()) ){
//                    username = snapshot.child("username").getValue().toString();
                    name = snapshot.child("name").getValue().toString();
                    username = snapshot.child("mobile").getValue().toString();
                    Username.setText(username);
                    Name.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Cursor c=db.rawQuery("SELECT * FROM student WHERE UID='"+CurrentUserId+"'", null);
                if(c.moveToFirst())
                {
                    Name.setText(c.getString(1));
                    Username.setText(c.getString(2));
                }

            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Username = (TextView) view.findViewById(R.id.phone);
        Name = (TextView) view.findViewById(R.id.p_retreive_name);
//        edit = (Button) view.findViewById(R.id.addtocontact_btn);
//        edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sendUserToUpdateProfile();
//            }
//        });
        return view;
    }
}