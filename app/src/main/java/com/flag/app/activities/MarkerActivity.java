package com.flag.app.activities;

/**
 * Created by Marvin on 2/16/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.flag.app.PaginationScrollListener;
import com.flag.app.R;
import com.flag.app.User;
import com.flag.app.adapter.MarkerAdapter;
import com.flag.app.model.Marker;
import com.flag.app.pref.UserPref;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MarkerActivity extends AppCompatActivity {

    private static final String TAG = "MarkerActivity";

    User mUser;

    private static final String EXTRA_USER = "EXTRA_USER";

    private DatabaseReference mDatabaseReference;
    CallbackManager callbackManager;

    private FloatingActionButton mMapButton;

    private MarkerAdapter mMarkerAdapter;

    private List<Marker> mMarkers;

    RecyclerView mRecyclerView;
    ProgressBar mProgressBar;

    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 3;
    private int currentPage = PAGE_START;


    public static Intent getStartIntent(Context context, User user) {
        Intent intent = new Intent(context,MarkerActivity.class);
        intent.putExtra(EXTRA_USER, user);
        return intent;
    }

    private final ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mMarkers = new ArrayList<>();
            Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
            Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
            int i=0;
            while (i<10) {
                i++;
                Marker marker = iterator.next().getValue(Marker.class);
                mMarkers.add(marker);
                loadFirstPage();
            }
            //mMarkerAdapter.refreshMarkers(mMarkers);
            //mRecyclerView.setVisibility(View.VISIBLE);
            //mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d(TAG, databaseError.toString());

        }
    } ;

    @Override
    protected void onStart() {
        super.onStart();
        mDatabaseReference.addValueEventListener(mValueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDatabaseReference.removeEventListener(mValueEventListener);
    }

    //Викликає метод Main Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markers);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final String PREFS_NAME = "MyPrefsUser";
        final String PREF_VERSION_CODE_KEY = "version_code";

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, 1).apply();

        ActivityCompat.requestPermissions(MarkerActivity.this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        mUser = getIntent().getParcelableExtra(EXTRA_USER);

        Toast.makeText(MarkerActivity.this,"Вітаємо, "+
                UserPref.get(MarkerActivity.this).getUser().getName(), Toast.LENGTH_SHORT).show();

        callbackManager = CallbackManager.Factory.create();

        mMapButton = findViewById(R.id.map_button);
        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(MapActivity.getStartIntent(MarkerActivity.this, mMarkerAdapter.getMarkers()));
            }
        });



        mRecyclerView = findViewById(R.id.recycler_view);
        mProgressBar = findViewById(R.id.main_progress);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mMarkerAdapter = new MarkerAdapter(this);

        mRecyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

//        mMarkerAdapter.setOnItemClickListener(new MarkerAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(Marker marker) {
//                startActivity(MarkerDetailsActivity.getStartIntent(MarkerActivity.this, marker));
//            }
//        });
        mRecyclerView.setAdapter(mMarkerAdapter);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("markers");
    }

    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");

        mProgressBar.setVisibility(View.GONE);
        mMarkerAdapter.refreshMarkers(mMarkers);

        if (currentPage <= TOTAL_PAGES) mMarkerAdapter.addLoadingFooter();
        else isLastPage = true;

    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);


        mMarkerAdapter.refreshMarkers(mMarkers);
        mMarkerAdapter.removeLoadingFooter();
        isLoading = false;

        if (currentPage != TOTAL_PAGES) mMarkerAdapter.addLoadingFooter();
        else isLastPage = true;
    }

}