package com.example.vanklcommapp.Controllers.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vanklcommapp.Models.ContactModel;
import com.example.vanklcommapp.Models.DataTypes.User;
import com.example.vanklcommapp.R;

import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements Observer{

    private List<User> userList;
    public ContactModel contactModel;
    public User contact;
    public String userRole;
    public ContactAdapter(List<User> userList, ContactModel contactModel, String userRole) {
        this.userList = userList;
        this.contactModel = contactModel;
        this.userRole = userRole;
        contactModel.addObserver(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view, this.userList, this.contactModel, this.userRole);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        System.out.println(userList);
        holder.contactUser = userList.get(0);
        User contactUser = userList.get(position);
        holder.textViewName.setText(contactUser.getUsername() + ": " + contactUser.getEmail());

        if(contactModel.contacts.contains(contactUser.getEmail())){
            holder.buttonAdd.setEnabled(false);
            holder.buttonAdd.setText("Added");
        } else {
            holder.buttonAdd.setEnabled(true);
            holder.buttonAdd.setText("Add Contact");
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg.equals("ContactAdapter")){
            System.out.println(contactModel.contacts);
            this.notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView textViewName;
        public Button buttonAdd;
        public Button buttonAdmin;
        User contactUser;

        ContactModel contactModel;
        public ViewHolder(@NonNull View itemView, List<User> userList, ContactModel contactModel, String userRole) {
            super(itemView);
            //Init View componenets
            System.out.println("In View Holder");
            textViewName = itemView.findViewById(R.id.textViewName);
            buttonAdd = itemView.findViewById(R.id.buttonAdd);
            buttonAdmin = itemView.findViewById(R.id.buttonAdmin);
            this.contactModel = contactModel;
            this.contactUser = userList.get(0);
            System.out.println(userRole);
            if(!Objects.equals(userRole, "admin")){
                buttonAdmin.setVisibility(View.GONE);
                buttonAdmin.setEnabled(false);
            }
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(contactUser.getEmail());
                    contactModel.addContact(contactUser);
                }
            });
            buttonAdmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(contactUser.getEmail());
                    contactModel.makeAdmin(contactUser);
                }
            });
        }
    }
}
