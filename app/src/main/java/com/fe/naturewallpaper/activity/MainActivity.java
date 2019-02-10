package com.fe.naturewallpaper.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.fe.naturewallpaper.R;
import com.fe.naturewallpaper.fragment.GalleryFragment;
import com.fe.naturewallpaper.fragment.SettingFragment;
import com.fe.naturewallpaper.fragment.ShareWallpaperFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView imgProfile;
    private TextView txtHeader;
    private TextView  txtSubHeader;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);
        imgProfile = header.findViewById(R.id.image_profile);
        txtHeader = header.findViewById(R.id.header_title);
        txtSubHeader = header.findViewById(R.id.header_subtitle);

        auth = FirebaseAuth.getInstance();
        String  email = auth.getCurrentUser().getEmail();
        String username = auth.getCurrentUser().getDisplayName();
        String profileUri = auth.getCurrentUser().getPhotoUrl().toString();
        txtHeader.setText(username);
        txtSubHeader.setText(email);
        Picasso.get().load(profileUri).into(imgProfile);
        onGalleryClick();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_upload) {
            chooseImageFromGallery();
        } else if (id == R.id.nav_gallery) {
            onGalleryClick();
        } else if (id == R.id.nav_manage) {
            onSettingClick();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void chooseImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 1);
    }

    private void onGalleryClick(){
        GalleryFragment galleryFragment = new GalleryFragment();
        FragmentTransaction fragmentTransaction =  getSupportFragmentManager().beginTransaction().replace(R.id.layout_content, galleryFragment);
        fragmentTransaction.commit();
    }

    private void onSettingClick(){
        SettingFragment settingFragment = new SettingFragment();
        FragmentTransaction fragmentTransaction =  getSupportFragmentManager().beginTransaction().replace(R.id.layout_content, settingFragment);
        fragmentTransaction.commit();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            Bundle imgBundle = new Bundle();
            Uri imgUri = data.getData();
            imgBundle.putString("imagePath", imgUri.toString());
            ShareWallpaperFragment wallpaperFragment = new ShareWallpaperFragment();
            wallpaperFragment.setArguments(imgBundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_content, wallpaperFragment);
            fragmentTransaction.commit();
        }
    }
}
