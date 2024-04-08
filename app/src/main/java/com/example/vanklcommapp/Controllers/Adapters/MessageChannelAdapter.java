package com.example.vanklcommapp.Controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vanklcommapp.KDC.Decrypter;
import com.example.vanklcommapp.Models.DataTypes.EncryptedMessage;
import com.example.vanklcommapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.List;

/*
 * This Adapter binds the Data from the Message Model from the list of messages to the Recycler View.
 */

public class MessageChannelAdapter extends RecyclerView.Adapter<MessageChannelAdapter.ViewHolder> {

    // List of encrypted messages
    private List<EncryptedMessage> mData;

    // Layout inflater to inflate views
    private LayoutInflater mInflater;

    // Click listener for item clicks
    private ItemClickListener mClickListener;

    // Constructor to initialize the adapter with data and context
    public MessageChannelAdapter(Context context, List<EncryptedMessage> mData) {
        this.mData = mData;
        this.mInflater = LayoutInflater.from(context);
    }

    // Inflates the item view layout and returns a new ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.message, parent, false);
        return new ViewHolder(view);
    }

    // Binds data to the views within the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Get user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        //Get Current message
        EncryptedMessage message = mData.get(position); // Get the current encrypted message
        //Decrypt the contents of the message from the session Key
        String content = Decrypter.decrypt(message.getSessionKey(), message.getContent());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Check if the message is sent by the current user
        if (user.getEmail().equals(message.getAccountSend())) {
            // Set message content, sender's account name, and timestamp on the right side of the layout
            holder.messageTextRight.setText(content);
            holder.messageUserRight.setText(message.getAccountSend());
            holder.messageTimeRight.setText(dateFormat.format(message.getTimestamp()));
            // Clear left side views
            holder.messageTextLeft.setText("");
            holder.messageUserLeft.setText("");
            holder.messageTimeLeft.setText("");
        } else {
            // Set message content, sender's account name, and timestamp on the left side of the layout
            holder.messageTextLeft.setText(content);
            holder.messageUserLeft.setText(message.getAccountSend());
            holder.messageTimeLeft.setText(dateFormat.format(message.getTimestamp()));
            // Clear right side views
            holder.messageTextRight.setText("");
            holder.messageUserRight.setText("");
            holder.messageTimeRight.setText("");
        }
    }

    // Returns the total number of items in the data set
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // ViewHolder class to hold references to the views within the item view layout
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // TextView for messages sent by the users
        TextView messageUserLeft;
        TextView messageTextLeft;
        TextView messageTimeLeft;

        // TextView for the message received by the current user
        TextView messageUserRight;
        TextView messageTextRight;
        TextView messageTimeRight;

        // Constructor to initialize the views and set click listener
        ViewHolder(View itemView) {
            super(itemView);

            //Set all views by ID
            messageUserLeft = itemView.findViewById(R.id.message_user_left);
            messageTimeLeft = itemView.findViewById(R.id.message_time_left);
            messageTextLeft = itemView.findViewById(R.id.message_text_left);
            messageUserRight = itemView.findViewById(R.id.message_user_right);
            messageTimeRight = itemView.findViewById(R.id.message_time_right);
            messageTextRight = itemView.findViewById(R.id.message_text_right);
            itemView.setOnClickListener(this);
        }

        // Handle item clicks
        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // Method to get a specific item from the data set
    EncryptedMessage getItem(int id) {
        return mData.get(id);
    }

    // Method to set the click listener
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // Interface for defining click listener
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}