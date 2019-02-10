package com.fe.naturewallpaper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fe.naturewallpaper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private LoginButton loginButton;
    private FirebaseUser firebaseUser;
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    String role_id = "2";
    private FirebaseFirestore firestore;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppEventsLogger.activateApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        if(firebaseUser == null){
            setContentView(R.layout.activity_log_in);
            FacebookSdk.sdkInitialize(getApplicationContext());
            loginButton = findViewById(R.id.login_button);

            callbackManager = CallbackManager.Factory.create();
            loginButton.setReadPermissions(Arrays.asList(EMAIL));
        }
        else {
            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
            startActivity(intent);
        }


    }


    public void onFacebookLogInButtonClick(View view){
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),"Cancel",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getApplicationContext(),"Error:  "+exception.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("Tag", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    user = firebaseAuth.getCurrentUser();
                    String username = user.getDisplayName();
                    final String user_id = user.getUid();
                    String url = user.getPhotoUrl().toString();
                    String tokenId = FirebaseInstanceId.getInstance().getToken();
                    final Map<String,Object> userMap = new HashMap<>();
                    userMap.put("role_id", role_id);
                    userMap.put("username", username);
                    userMap.put("url", url);
                    userMap.put("token_id", tokenId);
                    firestore.collection("users").document(user_id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(),"Could not register to firebase"+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


//    private void printHashKey() {
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String hashKey = new String(Base64.encode(md.digest(), 0));
//                Log.i("Tag", "printHashKey() Hash Key: " + hashKey);
//            }
//        } catch (NoSuchAlgorithmException e) {
//            Log.e("Tag", "printHashKey()", e);
//        } catch (Exception e) {
//            Log.e("Tag", "printHashKey()", e);
//        }
//    }

}
