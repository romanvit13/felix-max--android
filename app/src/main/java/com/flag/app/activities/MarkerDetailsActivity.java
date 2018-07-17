package com.flag.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flag.app.R;
import com.flag.app.model.Marker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

/**c
 * Created by Marvin on 2/16/2018.
 */

public class MarkerDetailsActivity extends AppCompatActivity {
    private static final String EXTRA_MARKER = "marker";

    public static Intent getStartIntent(Context context, Marker marker) {
        Intent startIntent = new Intent(context, MarkerDetailsActivity.class);
        startIntent.putExtra(EXTRA_MARKER, marker);
        return startIntent;
    }


    ImageView mImageView;

    TextView mNameTextView;

    TextView mAuthorTextView;

    TextView mDateTextView;

    TextView mDescriptionTextView;

    MapView mMapView;

    private GoogleMap mGoogleMap;

    Marker mMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_marker);
        mMarker = getIntent().getParcelableExtra(EXTRA_MARKER);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(mMarker.getName());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        mImageView = findViewById(R.id.image_view);
        mAuthorTextView = findViewById(R.id.text_view_author);
        mDateTextView = findViewById(R.id.text_view_date);
        mDescriptionTextView = findViewById(R.id.text_view_description);
        mMapView = findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        bindMarker(mMarker);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                // For dropping a marker at a point on the Map
                double lat = mMarker.location.lat;
                double lng = mMarker.location.lng;
                String title = mMarker.getTitle();
                String desc = mMarker.description;

                LatLng markerPosition = new LatLng(lat, lng);
                googleMap.addMarker(new MarkerOptions().position(markerPosition).title(title).snippet(desc));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(markerPosition).zoom(16).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                GoogleMapOptions options = new GoogleMapOptions().liteMode(true);
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        startActivity(MapActivity.getStartIntent(MarkerDetailsActivity.this, mMarker));
                    }
                });


            }
        });
    }

    private void bindMarker(Marker marker) {
        Picasso.with(mImageView.getContext())
                .load(marker.getImage().getUrl())
                .into(mImageView);
        mAuthorTextView.setText(marker.getLocation().getAuthor());
        mDescriptionTextView.setText(marker.getDescription());
        mDateTextView.setText(marker.getDate());

    }


}

