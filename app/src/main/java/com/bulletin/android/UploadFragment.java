package com.bulletin.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;


public class UploadFragment extends Fragment {

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    // Views
    private Button uploadButton, imagePickerButton;
    private ImageView imageView;
    private EditText editText;
    private ProgressBar uploadProgressBar;
    private TextView downloadUrl;

    // id for selecting image
    private static int SELECT_IMAGE = 1;

    private Context mContext;

    public UploadFragment() {
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
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        // find views
        imagePickerButton = (Button) view.findViewById(R.id.image_button);
        uploadButton = (Button) view.findViewById(R.id.upload_button);
        imageView = (ImageView) view.findViewById(R.id.image_view);
        editText = (EditText) view.findViewById(R.id.edit_text);
        uploadProgressBar = view.findViewById(R.id.upload_progress_bar);
        downloadUrl = (TextView) view.findViewById(R.id.download_url);

        // button that allows user to pick image
        imagePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_IMAGE);
            }
        });

        // button that uploads image to Firebase
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();
                Bitmap bitmap = imageView.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                imageView.setDrawingCacheEnabled(false);
                byte[] data = baos.toByteArray();

                String path = "images/" + "bulletin" + ".png";
                StorageReference storageRef = storage.getReference(path);

                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setCustomMetadata("text", editText.getText().toString())
                        .build();

                uploadProgressBar.setVisibility(View.VISIBLE);
                uploadButton.setEnabled(false);

                UploadTask uploadTask = storageRef.putBytes(data, metadata);
                uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadProgressBar.setVisibility(View.GONE);
                        uploadButton.setEnabled(true);
                        Toast.makeText(mContext, "Upload successful!", Toast.LENGTH_SHORT);

                        Uri url = taskSnapshot.getDownloadUrl();
                        downloadUrl.setText(url.toString());
                    }
                });
            }
        });

        return view;
    }

    // after user selects image from gallery, loads it into imageview
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            Glide.with(this).load(selectedImage).into(imageView);
        }
    }
}
