package com.example.vanklcommapp.Controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vanklcommapp.KDC.Decrypter;
import com.example.vanklcommapp.Models.DataTypes.Broadcast;
import com.example.vanklcommapp.Models.DataTypes.EncryptedMessage;
import com.example.vanklcommapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.List;

/*
* This Adapter binds the Data from the Broadcast Model to the Recycler View
*/

public class BroadcastChannelAdapter extends RecyclerView.Adapter<BroadcastChannelAdapter.ViewHolder> {
    private List<Broadcast> mData; // List of broadcast messages
    private LayoutInflater mInflater; // Layout inflater to inflate views
    private ItemClickListener mClickListener; // Click listener for item clicks

    // Constructor to initialize the adapter with data and context
    public BroadcastChannelAdapter(Context context, List<Broadcast> mData) {
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
        Broadcast bc = mData.get(position); // Get the current broadcast message
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Date format for timestamp
        holder.messageTextLeft.setText(bc.getContent()); // Set content of the message
        holder.messageUserLeft.setText(bc.getAccountSend()); // Set sender's account name
        holder.messageTimeLeft.setText(dateFormat.format(bc.getTimestamp())); // Set timestamp
    }

    // Returns the total number of items in the data set
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // ViewHolder class to hold references to the views within the item view layout
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView messageUserLeft; // TextView for user name
        TextView messageTextLeft; // TextView for message content
        TextView messageTimeLeft; // TextView for message timestamp

        // Constructor to initialize the views and set click listener
        ViewHolder(View itemView) {
            super(itemView);

            //Setting components by ID
            messageUserLeft = itemView.findViewById(R.id.message_user_left);
            messageTimeLeft = itemView.findViewById(R.id.message_time_left);
            messageTextLeft = itemView.findViewById(R.id.message_text_left);
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
    Broadcast getItem(int id) {
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
