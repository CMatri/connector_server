package com.cmeet.remoter.ui.special;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.cmeet.remoter.R;

public class SpecialFragment extends Fragment {

    private SpecialViewModel specialViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        specialViewModel =
                ViewModelProviders.of(this).get(SpecialViewModel.class);
        View root = inflater.inflate(R.layout.fragment_special, container, false);
        return root;
    }
}