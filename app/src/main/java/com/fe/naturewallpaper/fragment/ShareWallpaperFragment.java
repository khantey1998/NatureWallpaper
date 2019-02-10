package com.fe.naturewallpaper.fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fe.naturewallpaper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareWallpaperFragment extends Fragment {

    public ShareWallpaperFragment() {
        // Required empty public constructor
    }
    private EditText etxtTitle;
    private EditText etxtContent;
    private EditText etxtTag;
    private Button uploadButton;
    private ImageView img;
    private Uri imgUri;
    private FirebaseFirestore firestore;
    private StorageReference storage;
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share_wallpaper, container, false);

        etxtContent = view.findViewById(R.id.img_content);
        etxtTag = view.findViewById(R.id.img_tag);
        etxtTitle = view.findViewById(R.id.img_title);
        uploadButton =view.findViewById(R.id.upload_button);
        img = view.findViewById(R.id.uploaded_img);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();


        Bundle bundle = this.getArguments();
        String path = bundle.getString("imagePath");
        imgUri = Uri.parse(path);
        img.setImageURI(imgUri);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImageToFirebase();
            }
        });
        return view;
    }

    public void saveImageToFirebase(){
        final StorageReference storageReference = storage.child(etxtTitle.getText().toString()+auth.getCurrentUser().getUid()+".jpg");
        storageReference.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    final Map<String, Object> wallpaper = new HashMap<>();
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            wallpaper.put("url", uri.toString());
                            wallpaper.put("title", etxtTitle.getText().toString());
                            wallpaper.put("content",etxtContent.getText().toString());
                            wallpaper.put("tag", etxtTag.getText().toString());
                            firestore.collection("image_url").document().set(wallpaper).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    GalleryFragment galleryFragment = new GalleryFragment();
                                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.layout_content,galleryFragment);
                                    fragmentTransaction.commit();
                                }
                            });
                        }
                    });
                }
                else{
                    Toast.makeText(getContext(),"Error Sign up:"+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("Tag", "Error: "+task.getException().toString());
                }
            }
        });
    }
}
