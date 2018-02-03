package com.bulletin.android;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

                List<String> paths = new ArrayList<>();

                // loop through images to add all the paths
                for (int i = 1; i <= numBulletins; i++) {
                    String path = "images/" + "bulletin" + i + ".png";
                    paths.add(path);
                }

                adapter = new ImageAdapter(mContext, paths);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            }
        });
        return view;
    }
}
