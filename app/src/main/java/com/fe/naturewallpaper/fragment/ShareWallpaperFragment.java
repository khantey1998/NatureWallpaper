package com.fe.naturewallpaper.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fe.naturewallpaper.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShareWallpaperFragment extends Fragment {

    public ShareWallpaperFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share_wallpaper, container, false);
        return view;
    }

}
