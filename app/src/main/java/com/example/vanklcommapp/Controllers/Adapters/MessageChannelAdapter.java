package com.example.vanklcommapp.Controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vanklcommapp.Models.DataTypes.Message;
import com.example.vanklcommapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.List;

public class MessageChannelAdapter extends RecyclerView.Adapter<MessageChannelAdapter.ViewHolder> {
    private List<Message> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public MessageChannelAdapter(Context context, List<Message> mData) {
        this.mData = mData;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Message message = mData.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(user.getEmail().equals(message.getAccountSend())){
            holder.messageTextRight.setText(message.getContent());
            holder.messageUserRight.setText(message.getAccountSend());
            holder.messageTimeRight.setText(dateFormat.format(message.getTimestamp()));
            holder.messageTextLeft.setText("");
            holder.messageUserLeft.setText("");
            holder.messageTimeLeft.setText("");
        } else {
            holder.messageTextLeft.setText(message.getContent());
            holder.messageUserLeft.setText(message.getAccountSend());
            holder.messageTimeLeft.setText(dateFormat.format(message.getTimestamp()));
            holder.messageTextRight.setText("");
            holder.messageUserRight.setText("");
            holder.messageTimeRight.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView messageUserLeft;
        TextView messageTextLeft;
        TextView messageTimeLeft;
        TextView messageUserRight;
        TextView messageTextRight;
        TextView messageTimeRight;

        ViewHolder(View itemView) {
            super(itemView);
            messageUserLeft = itemView.findViewById(R.id.message_user_left);
            messageTimeLeft = itemView.findViewById(R.id.message_time_left);
            messageTextLeft = itemView.findViewById(R.id.message_text_left);
            messageUserRight = itemView.findViewById(R.id.message_user_right);
            messageTimeRight = itemView.findViewById(R.id.message_time_right);
            messageTextRight = itemView.findViewById(R.id.message_text_right);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    Message getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
