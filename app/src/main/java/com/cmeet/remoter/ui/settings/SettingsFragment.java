package com.cmeet.remoter.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.cmeet.remoter.MainActivity;
import com.cmeet.remoter.R;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;
    private TextView msg;
    private CheckBox serverButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        MainActivity context = (MainActivity) getContext();
        msg = root.findViewById(R.id.msg);
        msg.setText(context.getServer().getIpAddress());
        context.msg = msg;
        serverButton = root.findViewById(R.id.serverCheckBox);
        serverButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (MainActivity.server.isRunning() && !isChecked) {
                    MainActivity.server.kill();
                } else if(isChecked) {
                    MainActivity.server.start();
                }
            }
        });

        /*final TextView textView = root.findViewById(R.id.text_settings);
        settingsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }
}