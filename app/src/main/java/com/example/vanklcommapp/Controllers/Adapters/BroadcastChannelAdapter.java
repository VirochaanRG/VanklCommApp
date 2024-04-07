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

public class BroadcastChannelAdapter extends RecyclerView.Adapter<BroadcastChannelAdapter.ViewHolder> {
    private List<Broadcast> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public BroadcastChannelAdapter(Context context, List<Broadcast> mData) {
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
        Broadcast bc = mData.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        holder.messageTextLeft.setText(bc.getContent());
        holder.messageUserLeft.setText(bc.getAccountSend());
        holder.messageTimeLeft.setText(dateFormat.format(bc.getTimestamp()));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView messageUserLeft;
        TextView messageTextLeft;
        TextView messageTimeLeft;


        ViewHolder(View itemView) {
            super(itemView);
            messageUserLeft = itemView.findViewById(R.id.message_user_left);
            messageTimeLeft = itemView.findViewById(R.id.message_time_left);
            messageTextLeft = itemView.findViewById(R.id.message_text_left);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    Broadcast getItem(int id) {
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
