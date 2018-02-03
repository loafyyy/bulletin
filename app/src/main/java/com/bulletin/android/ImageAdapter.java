package com.bulletin.android;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Created by Jackie on 2018-02-03.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.CustomViewHolder> {

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private Context mContext;
    private List<String> mPaths;

    public ImageAdapter(Context context, List<String> paths) {
        mContext = context;
        mPaths = paths;
    }

    @Override
    public ImageAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bulletin_item, parent, false);
        return new CustomViewHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(final ImageAdapter.CustomViewHolder holder, int position) {

        final ImageView imageView = holder.imageView;
        String path = mPaths.get(position);
        StorageReference ref = storage.getReference(path);

        // get image
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(mContext).load(uri).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Glide.with(mContext).load(R.drawable.ic_launcher_background).into(imageView);
            }
        });

        // get metadata
        ref.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                String name = storageMetadata.getCustomMetadata("name");
                String location = storageMetadata.getCustomMetadata("location");
                holder.nameText.setText(name);
                holder.locationText.setText(location);
            }
        }).addOnFailureListener(new OnFailureListener() {
            // no metadata
            @Override
            public void onFailure(@NonNull Exception exception) {
                holder.nameText.setText("");
                holder.locationText.setText("");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPaths.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView locationText;
        private TextView nameText;

        public CustomViewHolder(View view, Context context) {
            super(view);
            this.imageView = view.findViewById(R.id.bulletin_image);
            this.locationText = view.findViewById(R.id.bulletin_location);
            this.nameText = view.findViewById(R.id.bulletin_name);
        }
    }
}
