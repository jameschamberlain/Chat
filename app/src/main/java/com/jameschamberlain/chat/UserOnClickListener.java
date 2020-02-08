package com.jameschamberlain.chat;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.jameschamberlain.chat.database.AppDatabase;

public class UserOnClickListener implements View.OnClickListener {

    private String user;
    private AppCompatActivity parentActivity;


    public UserOnClickListener(String user, AppCompatActivity activity) {
        this.user = user;
        this.parentActivity = activity;
    }

    @Override
    public void onClick(View v) {
        loadFragment(new ChatFragment());
    }

    /**
     *
     * Helper function for loading new fragments correctly.
     *
     * @param fragment The fragment to be loaded.
     */
    private void loadFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("username", user);
        // set arguments
        fragment.setArguments(bundle);
        // load fragment
        FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}

