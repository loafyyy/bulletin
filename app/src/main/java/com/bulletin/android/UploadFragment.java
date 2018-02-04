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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
    private EditText nameET;
    private EditText locationET;
    private ProgressBar uploadProgressBar;
    private Spinner spinner;

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
        uploadProgressBar = view.findViewById(R.id.upload_progress_bar);
        spinner = (Spinner) view.findViewById(R.id.bulletin_num_spinner);
        nameET = (EditText) view.findViewById(R.id.name_edit_text);
        locationET = (EditText) view.findViewById(R.id.location_edit_text);

        String[] spinnerValues = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

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

                String bulletin_num = spinner.getSelectedItem().toString();
                String path = "images/" + "bulletin" + bulletin_num + ".png";
                final StorageReference storageRef = storage.getReference(path);

                String name = nameET.getText().toString();
                String location = locationET.getText().toString();

                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setCustomMetadata("name", name)
                        .setCustomMetadata("location", location)
                        .build();

                uploadProgressBar.setVisibility(View.VISIBLE);
                uploadButton.setEnabled(false);

                UploadTask uploadTask = storageRef.putBytes(data, metadata);
                uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadProgressBar.setVisibility(View.GONE);
                        uploadButton.setEnabled(true);
                        Toast.makeText(mContext, "Uploaded to " + storageRef.toString(), Toast.LENGTH_LONG).show();
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
