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

/*
 * This class is an Adapter for a RecyclerView used to display contacts.
 * It binds data from the Contact model to the corresponding views in the RecyclerView.
 * It also implements the Observer interface to observe changes in the ContactModel to dynamically update view.
 */


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements Observer{

    // List of users to display as contacts
    private List<User> userList;

    // ContactModel instance to manage contacts
    public ContactModel contactModel;

    // Role of the current user
    public String userRole;

    // Constructor to initialize the adapter with data, ContactModel, and user role
    public ContactAdapter(List<User> userList, ContactModel contactModel, String userRole) {
        this.userList = userList;
        this.contactModel = contactModel;
        this.userRole = userRole;

        // Register the adapter as an observer of the ContactModel
        contactModel.addObserver(this);
    }

    // Inflates the item view layout and returns a new ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view, this.userList, this.contactModel, this.userRole);
    }

    // Binds data to the views within the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Get the current contact based on the position
        holder.contactUser = userList.get(0);
        User contactUser = userList.get(position);
        holder.textViewName.setText(contactUser.getUsername() + ": " + contactUser.getEmail());

        // Enable or disable the add button based on whether the contact is already added
        if(contactModel.contacts.contains(contactUser.getEmail())){
            holder.buttonAdd.setEnabled(false);
            holder.buttonAdd.setText("Added");
        } else {
            holder.buttonAdd.setEnabled(true);
            holder.buttonAdd.setText("Add Contact");
        }
    }

    // Returns the total number of items in the data set
    @Override
    public int getItemCount() {
        return userList.size();
    }

    // Update method from the Observer interface to observe changes in the ContactModel
    @Override
    public void update(Observable o, Object arg) {
        if(arg.equals("ContactAdapter")){
            this.notifyDataSetChanged();
        }
    }

    // ViewHolder class to hold references to the views within the item view layout
    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView textViewName;
        public Button buttonAdd;
        public Button buttonAdmin;
        User contactUser;
        ContactModel contactModel;

        // Constructor to initialize the views and set click listeners
        public ViewHolder(@NonNull View itemView, List<User> userList, ContactModel contactModel, String userRole) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            buttonAdd = itemView.findViewById(R.id.buttonAdd);
            buttonAdmin = itemView.findViewById(R.id.buttonAdmin);
            this.contactModel = contactModel;
            this.contactUser = userList.get(0);

            // Set visibility and enable state of admin button based on user role
            if(!Objects.equals(userRole, "admin")){
                buttonAdmin.setVisibility(View.GONE);
                buttonAdmin.setEnabled(false);
            }

            // Click listener for adding a contact
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contactModel.addContact(contactUser);
                }
            });

            // Click listener for making a user admin
            buttonAdmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contactModel.makeAdmin(contactUser);
                }
            });
        }
    }
}
