package com.codefury.starthub;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class GroupFragment extends Fragment {

    private View privatechatlists;

    private RecyclerView post_rv;
    private FirebaseRecyclerAdapter<post, GroupFragment.MyViewHolder> adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    FirebaseDatabase database;
    DatabaseReference UserRef,currentuserRef,currentRef,RootRef;
    private final String UserTag = "User";
    private String currentUserId;
    private FloatingActionButton create_post;

    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentuserRef = FirebaseDatabase.getInstance().getReference().child("post");

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
        // Inflate the layout for this fragment
        privatechatlists = inflater.inflate(R.layout.fragment_group, container, false);
        HashMap<String, String> statusMap = new HashMap<>();
        statusMap.put(currentUserId, "ONLINE");
        statusMap.put("isTyping", "false");
        RootRef.child("Status").child(currentUserId).setValue(statusMap);

        post_rv = (RecyclerView) privatechatlists.findViewById(R.id.timeline_feed_rv);
        create_post = privatechatlists.findViewById(R.id.createPost);
        post_rv.setHasFixedSize(true);
        post_rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        create_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),CreatePost.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
                getActivity().finish();
            }
        });
        return privatechatlists;
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name,role, desc;
        ImageButton menubtn;
        RelativeLayout like;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            menubtn = itemView.findViewById(R.id.menubtn);
            like = itemView.findViewById(R.id.like_lay);
            desc = itemView.findViewById(R.id.desc);
            role = itemView.findViewById(R.id.role);
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


        FirebaseRecyclerOptions<post> options = new FirebaseRecyclerOptions.Builder<post>().setQuery(currentuserRef,post.class).build();

        adapter = new FirebaseRecyclerAdapter<post, GroupFragment.MyViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull GroupFragment.MyViewHolder holder, int position, @NonNull post model) {

                final String uid1 = getRef(position).getKey();
                currentRef = FirebaseDatabase.getInstance().getReference().child("post").child(uid1);
                currentRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String retdesc = null;
                        String retName = null;
                        String retrole = null;
                        final String currUID = mAuth.getCurrentUser().getUid().toString();

//                        final String retUserid =  snapshot.child("uid").getValue().toString();
                        if(snapshot.child("username").exists() ){
                            retdesc = snapshot.child("desc").getValue().toString();
                        }
                        if(snapshot.child("name").exists()){
                            retName = snapshot.child("name").getValue().toString();
                        }
                        if(snapshot.child("role").exists() ){
                            retrole = snapshot.child("role").getValue().toString();
                        }
                        holder.itemView.setVisibility(View.VISIBLE);
                        holder.name.setText(retName);
                        holder.desc.setText(retdesc);
                        holder.role.setText(retrole);


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
            }

            @NonNull
            @Override
            public GroupFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_feed_view, parent,false);
                return new GroupFragment.MyViewHolder(view);
            }


        };
        post_rv.setAdapter(adapter);
        adapter.startListening();




        if(checkConnection()) {
            HashMap<String, String> statusMap = new HashMap<>();
            statusMap.put(currentUserId, "ONLINE");
            statusMap.put("isTyping", "false");
            RootRef.child("Status").child(currentUserId).setValue(statusMap);
        }else{
            Snackbar noconnection = Snackbar.make(getActivity().findViewById(R.id.home_constlayout), "\tComing Soon!!", Snackbar.LENGTH_LONG);
            noconnection.setAction("Retry!!",new GroupFragment.retrysnackbar());
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
