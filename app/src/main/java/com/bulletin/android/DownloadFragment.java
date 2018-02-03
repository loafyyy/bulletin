package com.bulletin.android;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;


public class DownloadFragment extends Fragment {

    private Button downloadButton;
    private Context mContext;

    private RecyclerView recyclerView;
    private ImageAdapter adapter;

    // TODO don't hardcode
    private static final int numBulletins = 10;

    public DownloadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_download, container, false);

        // find views
        downloadButton = (Button) view.findViewById(R.id.download_button);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        // load image in Firebase into imageview
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add geofences
                ((NavigationActivity) getActivity()).addGeofencesHandler();
            }
        });
        return view;
    }

    public void downloadPics(String folder) {
        List<String> paths = new ArrayList<>();

        // loop through images to add all the paths
        for (int i = 1; i <= numBulletins; i++) {
            String path = folder + "/" + "bulletin" + i + ".png";
            paths.add(path);
        }

        adapter = new ImageAdapter(mContext, paths);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    @Override
    public void onResume() {
        super.onResume();

        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("download"));
    }

    // handler for received Intents for the "download" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String folder = intent.getStringExtra("folder");

            if (folder.equals("images")) {
                Toast.makeText(getContext(), "Getting bulletins from Pennovation Center", Toast.LENGTH_LONG).show();
            }

            // download pics
            downloadPics(folder);
        }
    };

    @Override
    public void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }
}
