package smart.budget.diary.ui.signin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import smart.budget.diary.R;
import smart.budget.diary.base.BaseActivity;
import smart.budget.diary.models.IncomeExpenseRecord;
import smart.budget.diary.models.User;
import smart.budget.diary.ui.main.MainActivity;
import smart.budget.diary.ui.main.history.HistoryFragment;
import smart.budget.diary.ui.main.home.HomeFragment;
import smart.budget.diary.util.CalendarHelper;
import smart.budget.diary.util.CurrencyHelper;
// THIS FILE WILL LOAD WHEN USER OPENS APPLICATION - how it will load - it will load based on "app/src/main/AndroidManifest.xml" file.
// There you can search LoginActivity in that file. or see code below
// code from AndroidManifest.xml file indicating that Login page is LAUNCHER and Main page of our app.
//<activity
//            android:name=".LoginActivity"
//                    android:theme="@style/Theme.AppCompat.DayNight.NoActionBar" // indicate that this page has no appbar
//                    android:exported="true">
//<intent-filter>
//<action android:name="android.intent.action.MAIN" />
//
//<category android:name="android.intent.category.LAUNCHER" />
//</intent-filter>
//</activity>

public class LoginActivity extends BaseActivity {
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private final int REQ_ONE_TAP=100;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private View progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        HomeFragment.walletEntryListDataSet=new ArrayList<>();
        HistoryFragment.walletEntryListDataSetCZ=new ArrayList<>();
        HomeFragment.user=null;
        HomeFragment.isListLoaded=false;

        progressView = findViewById(R.id.login_progress_bar);

        // Request necessary permissions at runtime
        getRequiredPermissions();

        // Initialize Firebase authentication
        mAuth=FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Check if user is already signed in
        if(currentUser!=null) {
            LoadLoggedUserDataFromFirebase();
        }
    }

    private void LoadLoggedUserDataFromFirebase() {
        showProgressView();
        DatabaseReference loggedUserData=FirebaseDatabase.getInstance().getReference()
                .child("users").child(getUid());
        Query loggedUserExpenseRecord=FirebaseDatabase.getInstance().getReference()
                .child("wallet-entries").child(getUid()).child("default").orderByChild("date")
                .startAt(-CalendarHelper.getCurrentMonthEnd().getTimeInMillis()).endAt(-CalendarHelper.getCurrentMonthStart().getTimeInMillis());

        loggedUserExpenseRecord.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                List<IncomeExpenseRecord> incomeExpenseRecordList = new ArrayList<>();
                for (DataSnapshot snapshot : snapshots.getChildren()) {
                    IncomeExpenseRecord incomeExpenseRecord = snapshot.getValue(IncomeExpenseRecord.class);
                    if(incomeExpenseRecord!=null){
                        incomeExpenseRecord.key= snapshot.getKey();
                        incomeExpenseRecordList.add(incomeExpenseRecord);
                    }
                }
                if(!incomeExpenseRecordList.isEmpty()){
                    HomeFragment.walletEntryListDataSet =incomeExpenseRecordList;
                }else {
                    HomeFragment.walletEntryListDataSet=new ArrayList<>();
                }
                HomeFragment.isListLoaded=true;
                dataUpdated();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FirebaseAuth.getInstance().signOut();
                hideProgressView();
            }
        });
        loggedUserData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue(User.class)==null){
                    FirebaseAuth.getInstance().signOut();
                    hideProgressView();
                }
                else{
                    HomeFragment.user = snapshot.getValue(User.class);
                    dataUpdated();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FirebaseAuth.getInstance().signOut();
                hideProgressView();
            }
        });
    }

    void dataUpdated(){
        if (HomeFragment.user == null || !HomeFragment.isListLoaded) return;
        openHomePage(); // Open the HomeActivity if user is signed in
    }

    // Request necessary permissions for the app to function properly
    private void getRequiredPermissions() {
        FirebaseMessaging.getInstance().subscribeToTopic("app")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

        // Check Android version and request notification access if it's Android 13 or greater
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= 33) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    // Handle Google sign-in button click
    public void loginWithGoogle(View view) {
        // Initialize Google Identity API
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId("925846579501-snaa3ua9i18it27rpc5btmuqesk5ki7n.apps.googleusercontent.com")
                        .setFilterByAuthorizedAccounts(false) // keep it false to show all accounts saved in your mobile
                        .build())
                .build();
        showProgressView();
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        try {
                            startIntentSenderForResult(
                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                    null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            hideProgressView();
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(LoginActivity.this, "Login Failed. - "+ e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressView();
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(LoginActivity.this, "Login Failed. - "+ e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    if (idToken !=  null) {
                        // Got an ID token from Google. Use it to authenticate
                        // with Firebase.
                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                        mAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            currentUser=mAuth.getCurrentUser();
                                            final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
                                            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    User user = dataSnapshot.getValue(User.class);
                                                    if (user != null) {
                                                        // Sign in success, open HomeActivity
                                                        LoadLoggedUserDataFromFirebase();
                                                    } else {
                                                        // create new User record in db
                                                        User newUser=new User();
                                                        FirebaseDatabase.getInstance().getReference()
                                                                .child("users").child(getUid()).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        // Sign in success, open HomeActivity
                                                                        LoadLoggedUserDataFromFirebase();
                                                                    }
                                                                });
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    hideProgressView();
                                                    FirebaseAuth.getInstance().signOut();
                                                    Toast.makeText(LoginActivity.this, "Login Failed: "+ databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });

                                        } else {
                                            // If sign in fails, display an error message
                                            String msg="Please try again";
                                            if(task.getException().getMessage()!=null){
                                                msg=task.getException().getLocalizedMessage();
                                            }
                                            hideProgressView();
                                            FirebaseAuth.getInstance().signOut();
                                            Toast.makeText(LoginActivity.this, "Login Failed: "+ msg, Toast.LENGTH_LONG).show();

                                        }
                                    }
                                });
                    }
                } catch (ApiException e) {
                    hideProgressView();
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(LoginActivity.this, "Login Failed: "+ e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void showProgressView() {
        progressView.setVisibility(View.VISIBLE);
        findViewById(R.id.saveButton).setVisibility(View.GONE);

    }

    private void hideProgressView() {
        progressView.setVisibility(View.GONE);
        findViewById(R.id.saveButton).setVisibility(View.VISIBLE);

    }

    void openHomePage(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Finish the LoginActivity so the user can't go back to it
    }
}