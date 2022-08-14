package com.codefury.starthub;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ImageButton save_msg_btn;
    private EditText msg_input_text;
    private FirebaseAuth mAuth;
    private String messegeSenderid, messegeReceiverid, currentTime;
    DatabaseReference RootRef, RootRef1, sendmsg, statusRef;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;

    private String receiverUsername, senderUsername, status, typingStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        save_msg_btn = findViewById(R.id.send_msg);
        msg_input_text = findViewById(R.id.msg);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_chat_acionbar);

        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = (RecyclerView) findViewById(R.id.userMsg);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);


        sendmsg = FirebaseDatabase.getInstance().getReference().child("User-User-Messages");
        RootRef1 = FirebaseDatabase.getInstance().getReference();
        statusRef = FirebaseDatabase.getInstance().getReference();
        //getSupportActionBar().setElevation(0);
        View view = getSupportActionBar().getCustomView();
        TextView Username = view.findViewById(R.id.name);
        TextView Status = view.findViewById(R.id.status);
        final String UserTag = getIntent().getExtras().get("tag").toString();
        messegeReceiverid = getIntent().getExtras().get("uid").toString();

        mAuth = FirebaseAuth.getInstance();
        messegeSenderid = mAuth.getCurrentUser().getUid().toString();

        Username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "You have clicked tittle", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ChatActivity.this, displayprofile.class);
                intent.putExtra("tag", UserTag);
                intent.putExtra("uid", messegeReceiverid);
                startActivity(intent);
            }
        });

        // When there is change in state of edittext input
        msg_input_text.addTextChangedListener(new TextWatcher() {
            @Override
            // when there is no text added
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().trim().length() == 0) {
                    // set text to Not typing
                    //not typing
                    HashMap<String, String> statusMap = new HashMap<>();
                    statusMap.put(messegeSenderid, "ONLINE");
                    statusMap.put("isTyping", "false");
                    statusRef.child("Status").child(messegeSenderid).setValue(statusMap);

                } else {
                    // set text to typing
                    HashMap<String, String> statusMap = new HashMap<>();
                    statusMap.put(messegeSenderid, "ONLINE");
                    statusMap.put("isTyping", "true");
                    statusRef.child("Status").child(messegeSenderid).setValue(statusMap);

                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //typing
                HashMap<String, String> statusMap = new HashMap<>();
                statusMap.put(messegeSenderid, "ONLINE");
                statusMap.put("isTyping", "true");
                statusRef.child("Status").child(messegeSenderid).setValue(statusMap);

            }

            // after we input some text
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    // set text to Stopped typing
                    HashMap<String, String> statusMap = new HashMap<>();
                    statusMap.put(messegeSenderid, "ONLINE");
                    statusMap.put("isTyping", "false");
                    statusRef.child("Status").child(messegeSenderid).setValue(statusMap);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        HashMap<String, String> statusMap = new HashMap<>();
                        statusMap.put(messegeSenderid, "ONLINE");
                        statusMap.put("isTyping", "false");
                        statusRef.child("Status").child(messegeSenderid).setValue(statusMap);
                    }
                }, 5000);
            }
        });

        statusRef.child("Status").child(messegeReceiverid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                status = snapshot.child(messegeReceiverid).getValue().toString();
                typingStatus = snapshot.child("isTyping").getValue().toString();
                if (typingStatus.equals("true")) {
                    Status.setText("Typing...");
                } else {
                    Status.setText(status);
                    if (Status.getText().equals("ONLINE")) {
                        Status.setVisibility(View.VISIBLE);
                    } else {
                        Status.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (UserTag.equals("User")) {
            RootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(messegeReceiverid);
            RootRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    receiverUsername = snapshot.child("name").getValue().toString();

                    final String actionTitle = receiverUsername;
                    ActionBar actionBar = getSupportActionBar();
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    if (actionBar != null) {
                        //actionBar.setTitle(currUsername);
                        Username.setText(actionTitle);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            RootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(messegeSenderid);
            RootRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    senderUsername = snapshot.child("name").getValue().toString();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        save_msg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsgMethod();
            }


        });


    }

    private void sendMsgMethod() {
        String send1msg = msg_input_text.getText().toString();

        if (!TextUtils.isEmpty(send1msg)) {
            Calendar calforTime = Calendar.getInstance();
            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = simpleTimeFormat.format(calforTime.getTime());
            msg_input_text.setText("");

            String messageSenderRef = messegeSenderid + "/" + messegeReceiverid;
            String messageReceiverRef = messegeReceiverid + "/" + messegeSenderid;

            DatabaseReference userMessageKeyRef = RootRef.child("User-User-Messages").child(messegeSenderid).child(messegeReceiverid).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", send1msg);
            messageTextBody.put("username", senderUsername);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messegeSenderid);
            messageTextBody.put("to", messegeReceiverid);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", currentTime);


            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

            sendmsg.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {

                    } else {

                    }
                    msg_input_text.setText("");
                }
            });

        } else {
            msg_input_text.setText("");
        }


    }


    @Override
    protected void onStart() {
        super.onStart();
        userMessagesList.refreshDrawableState();

        HashMap<String, String> statusMap = new HashMap<>();
        statusMap.put(messegeSenderid, "ONLINE");
        statusMap.put("isTyping", "false");
        statusRef.child("Status").child(messegeSenderid).setValue(statusMap);

        RootRef1.child("User-User-Messages").child(messegeSenderid).child(messegeReceiverid)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        Messages messages = dataSnapshot.getValue(Messages.class);

                        messagesList.add(messages);

                        messageAdapter.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        messagesList.clear();

        HashMap<String, String> statusMap = new HashMap<>();
        statusMap.put(messegeSenderid, "ONLINE");
        statusMap.put("isTyping", "false");
        statusRef.child("Status").child(messegeSenderid).setValue(statusMap);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        HashMap<String, String> statusMap = new HashMap<>();
        statusMap.put(messegeSenderid, "ONLINE");
        statusMap.put("isTyping", "false");
        statusRef.child("Status").child(messegeSenderid).setValue(statusMap);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}