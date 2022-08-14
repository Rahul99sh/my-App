package com.codefury.starthub;

import android.content.Context;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class HomeFragment extends Fragment {

    private View privatechatlists;

    private RecyclerView userlist_rv;
    private FirebaseRecyclerAdapter<Users, MyViewHolder> adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    FirebaseDatabase database;
    DatabaseReference UserRef,currentuserRef,currentRef,RootRef;
    private final String UserTag = "User";
    private String currentUserId;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentuserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        currentUserId = mUser.getUid().toString();

        UserRef = FirebaseDatabase.getInstance().getReference();
        RootRef = FirebaseDatabase.getInstance().getReference();

        HashMap<String, String> statusMap = new HashMap<>();
        statusMap.put(currentUserId, "ONLINE");
        statusMap.put("isTyping", "false");
        RootRef.child("Status").child(currentUserId).setValue(statusMap);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        privatechatlists = inflater.inflate(R.layout.fragment_home, container, false);



        HashMap<String, String> statusMap = new HashMap<>();
        statusMap.put(currentUserId, "ONLINE");
        statusMap.put("isTyping", "false");
        RootRef.child("Status").child(currentUserId).setValue(statusMap);


        userlist_rv = (RecyclerView) privatechatlists.findViewById(R.id.userList1);
        userlist_rv.setHasFixedSize(true);
        userlist_rv.setLayoutManager(new LinearLayoutManager(getActivity()));



        return privatechatlists;
    }


    private static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView username;
        ImageButton menubtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.role);
            menubtn = itemView.findViewById(R.id.menubtn);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> statusMap = new HashMap<>();
                statusMap.put(currentUserId, "ONLINE");
                statusMap.put("isTyping", "false");
                RootRef.child("Status").child(currentUserId).setValue(statusMap);
            }
        }, 600);


        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(currentuserRef,Users.class).build();
        adapter = new FirebaseRecyclerAdapter<Users, MyViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Users model) {

                final String uid1 = getRef(position).getKey();
                currentRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid1);
                currentRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String retUserName = null;
                        String retName = null;
                        final String currUID = mAuth.getCurrentUser().getUid().toString();

                        final String retUserid =  snapshot.child("uid").getValue().toString();
                        if(snapshot.child("username").exists() ){
                            retUserName = snapshot.child("username").getValue().toString();
                        }
                        if(snapshot.child("name").exists()){
                            retName = snapshot.child("name").getValue().toString();
                        }
                        final String ckUserId=retUserid;
                        if(ckUserId.equals(currUID) || retName == null) {  //work from here
                            holder.itemView.setVisibility(View.GONE);
                            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                            params.height = 0;
                            params.width = 0;
                            holder.itemView.setLayoutParams(params);
                        }else{
                            holder.itemView.setVisibility(View.VISIBLE);
                            holder.username.setText(retName);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Opps...Trying Hard...Please Wait!!", Toast.LENGTH_SHORT).show();
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String currreceiverid = getRef(position).getKey();
                        Intent intent = new Intent(getActivity(),ChatActivity.class);
                        intent.putExtra("uid",currreceiverid);
                        intent.putExtra("tag",UserTag);
                        startActivity(intent);
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        PopupMenu popup = new PopupMenu(getActivity(), holder.menubtn);
                        popup.getMenuInflater().inflate(R.menu.recycler_group_longclick_menu, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch(menuItem.getItemId()) {
                                    case R.id.one:
                                        Toast.makeText(getActivity(), "Feature Comming Soon!!", Toast.LENGTH_SHORT).show();
                                        break;
                                    case R.id.two:
                                        Toast.makeText(getActivity(), "Hold It Man!!", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                                return true;
                            }
                        });
                        popup.show();
                        return true;

                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_view, parent,false);
                return new MyViewHolder(view);
            }


        };
        userlist_rv.setAdapter(adapter);
        adapter.startListening();




        if(checkConnection()) {
            HashMap<String, String> statusMap = new HashMap<>();
            statusMap.put(currentUserId, "ONLINE");
            statusMap.put("isTyping", "false");
            RootRef.child("Status").child(currentUserId).setValue(statusMap);
        }else{
            Snackbar noconnection = Snackbar.make(getActivity().findViewById(R.id.home_constlayout), "\tComing Soon!!", Snackbar.LENGTH_LONG);
            noconnection.setAction("Retry!!",new retrysnackbar());
            noconnection.show();
        }

    }
    private boolean checkConnection(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }else{
            Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
            connected= false;
        }
        return connected;
    }

    private class retrysnackbar implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            checkConnection();
            // Code to undo the user's last action
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        HashMap<String, String> statusMap = new HashMap<>();
        statusMap.put(currentUserId, "ONLINE");
        statusMap.put("isTyping", "false");
        RootRef.child("Status").child(currentUserId).setValue(statusMap);
    }







}