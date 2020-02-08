package com.jameschamberlain.chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.FileUtils;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

public class ChatFragment extends Fragment {

    private String username;
    private View rootView;

    public ChatFragment() {
        // Required empty constructor.
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle data = this.getArguments();
        username = Objects.requireNonNull(data).getString("username");

        rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        setHasOptionsMenu(true);
        Toolbar myToolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");

        TextView usernameTextView = rootView.findViewById(R.id.username);
        usernameTextView.setText(username);

        final EditText messageEditText = rootView.findViewById(R.id.message_edit_text);
        messageEditText.requestFocus();
        messageEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (String.valueOf(messageEditText.getText()).equals("")) {
                    ImageView sendImageButton = rootView.findViewById(R.id.send_image_button);
                    sendImageButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorEditTextBg));
                }
                else {
                    ImageView sendImageButton = rootView.findViewById(R.id.send_image_button);
                    sendImageButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (String.valueOf(messageEditText.getText()).equals("")) {
                    ImageView sendImageButton = rootView.findViewById(R.id.send_image_button);
                    sendImageButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorEditTextBg));
                }
                else {
                    ImageView sendImageButton = rootView.findViewById(R.id.send_image_button);
                    sendImageButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (String.valueOf(messageEditText.getText()).equals("")) {
                    ImageView sendImageButton = rootView.findViewById(R.id.send_image_button);
                    sendImageButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorEditTextBg));
                }
                else {
                    ImageView sendImageButton = rootView.findViewById(R.id.send_image_button);
                    sendImageButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
                }
            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chat_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // User chose the "Back" item, go back.
                FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
                return true;
            case R.id.action_delete:
                // User chose the "Delete" action, delete the fixture.
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Delete chat?")
                        .setMessage("Are you sure you would like to delete this chat?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO
                                FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                                if (fm.getBackStackEntryCount() > 0) {
                                    fm.popBackStackImmediate();
                                }
                            }
                        })
                        .setNegativeButton("No", null);
                AlertDialog dialog = alert.create();
                dialog.show();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


}
