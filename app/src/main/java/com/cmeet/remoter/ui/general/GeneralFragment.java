package com.cmeet.remoter.ui.general;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.cmeet.remoter.MainActivity;
import com.cmeet.remoter.R;
import com.cmeet.remoter.Server;
import com.cmeet.remoter.packet.ClickPacket;
import com.cmeet.remoter.packet.MousePacket;
import com.google.android.material.navigation.NavigationView;

public class GeneralFragment extends Fragment {

    private GeneralViewModel generalViewModel;
    private View mouseView;
    private MainActivity context;
    private boolean shouldClick = false;
    private Server server;
    final Handler handler = new Handler();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        generalViewModel = ViewModelProviders.of(this).get(GeneralViewModel.class);
        View root = inflater.inflate(R.layout.fragment_general, container, false);

        context = (MainActivity) getContext();
        server = context.getServer();

        final Runnable longPressed = new Runnable() {
            public void run() {
                shouldClick = false;
                server.packet(new ClickPacket((byte) 1));
            }
        };

        mouseView = root.findViewById(R.id.mouseView);
        mouseView.setOnTouchListener(new View.OnTouchListener() {
            float lastX = 0;
            float lastY = 0;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        shouldClick = true;
                        handler.postDelayed(longPressed, ViewConfiguration.getLongPressTimeout());
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacks(longPressed);
                        if (shouldClick) server.packet(new ClickPacket((byte) 0));
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float x = motionEvent.getX();
                        float y = motionEvent.getY();
                        float dx = x - lastX;
                        float dy = y - lastY;
                        if (Math.sqrt(dx * dx + dy * dy) < 50) {
                            server.packet(new MousePacket(dx * 0.6f, dy * 0.4f));
                        }
                        lastX = x;
                        lastY = y;
                        handler.removeCallbacks(longPressed);
                        shouldClick = false;
                        break;
                }
                return true;
            }
        });

        mouseView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                server.packet(new ClickPacket((byte) 1));
                shouldClick = false;
                return true;
            }
        });



        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}