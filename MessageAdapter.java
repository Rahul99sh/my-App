package com.codefury.starthub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter
{
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference usersRef;


    public MessageAdapter (List<Messages> userMessagesList)
    {
        userMessagesList.clear();
        this.userMessagesList = userMessagesList;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String currUserid = mUser.getUid().toString();
        if(userMessagesList.get(position).getFrom().equals(currUserid)){
            return 0;
        }else{
            return 1;
        }

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //holder.setIsRecyclable(false);
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        if(viewType==0){
            view = layoutInflater.inflate(R.layout.outgoing_msg,parent,false);
            return new ViewHolder1(view);
        }else{
            view = layoutInflater.inflate(R.layout.incoming_msg,parent,false);
            return new ViewHolder2(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String currUserid = mUser.getUid().toString();

        if(userMessagesList.get(position).getFrom().equals(currUserid)){
            //bind 1
            ViewHolder1 viewHolder1 = (ViewHolder1) holder;
            //viewHolder1.username_sender.setText(userMessagesList.get(position).getUsername());
            viewHolder1.msg_sender.setText(userMessagesList.get(position).getMessage());
            viewHolder1.time_sender.setText(userMessagesList.get(position).getTime());
        }else{
            //bind 2
            ViewHolder2 viewHolder2 = (ViewHolder2) holder;
            viewHolder2.username_receiver.setText(userMessagesList.get(position).getUsername());
            viewHolder2.msg_receiver.setText(userMessagesList.get(position).getMessage());
            viewHolder2.time_receiver.setText(userMessagesList.get(position).getTime());

        }

    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }



    class ViewHolder1 extends RecyclerView.ViewHolder{

        TextView msg_sender,time_sender;

        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            msg_sender = itemView.findViewById(R.id.message_text_sender);
            time_sender = itemView.findViewById(R.id.date_text_sender);
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder{
        TextView username_receiver,msg_receiver,time_receiver;
        ImageView readStatus;
        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            username_receiver = itemView.findViewById(R.id.username_sender);
            msg_receiver = itemView.findViewById(R.id.message_text_sender);
            time_receiver = itemView.findViewById(R.id.date_text_sender);
            readStatus = itemView.findViewById(R.id.read_status);
        }
    }
}

