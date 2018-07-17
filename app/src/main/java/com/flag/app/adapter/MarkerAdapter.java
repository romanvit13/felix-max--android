package com.flag.app.adapter;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flag.app.R;
import com.flag.app.model.Marker;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Suleiman on 19/10/16.
 */

public class MarkerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Marker> mMarkers;
    private Context context;

    private boolean isLoadingAdded = false;

    public MarkerAdapter(Context context) {
        this.context = context;
        mMarkers = new ArrayList<>();
    }
    public interface OnItemClickListener {
        void onItemClick(Marker marker);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public List<Marker> getMarkers() {
        return mMarkers;
    }

    public void setMarkers(List<Marker> markers) {
        this.mMarkers = markers;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View itemMarker = inflater.inflate(R.layout.item_marker, parent, false);
                viewHolder = new MarkerVH(itemMarker);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.activity_markers, parent, false);
        viewHolder = new MarkerVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Marker marker = mMarkers.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                MarkerVH markerVH = (MarkerVH) holder;
                markerVH.mAuthorTextView.setText(marker.getLocation().getAuthor());
                Picasso.with(markerVH.mImageView.getContext())
                        .load(marker.getImage().getUrl())
                        .placeholder(R.color.grey_400).into(markerVH.mImageView);
                markerVH.bindMarker(marker);
                break;
            case LOADING:
//                Do nothing
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mMarkers == null ? 0 : mMarkers.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mMarkers.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(Marker marker) {
        mMarkers.add(marker);
        notifyItemInserted(mMarkers.size() - 1);
    }

    public void addAll(List<Marker> mkList) {
        for (Marker marker : mkList) {
            add(marker);
        }
    }

    public void remove(Marker city) {
        int position = mMarkers.indexOf(city);
        if (position > -1) {
            mMarkers.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Marker());
    }

    public void refreshMarkers(List<Marker> markers) {
        mMarkers.clear();
        mMarkers.addAll(markers);
        notifyDataSetChanged();
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mMarkers.size() - 1;
        Marker item = getItem(position);

        if (item != null) {
            mMarkers.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Marker getItem(int position) {
        return mMarkers.get(position);
    }





   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    public class MarkerVH extends RecyclerView.ViewHolder {

        private  ImageView mImageView;
        private  TextView mNameTextView;
        private  TextView mAuthorTextView;

        private Marker mMarker;

        public MarkerVH(final View itemView) {

            super(itemView);
            mImageView = itemView.findViewById(R.id.photo_image_view);
            mNameTextView = itemView.findViewById(R.id.text_view_name1);
            mAuthorTextView = itemView.findViewById(R.id.text_view_author);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(mMarker);
                }
            });
        }

        void bindMarker(Marker marker) {
            mMarker = marker;
            mNameTextView = itemView.findViewById(R.id.text_view_name1);
            mNameTextView.setText(mMarker.getName());
//            mAuthorTextView.setText(mMarker.getLocation().getAuthor());
//            Picasso.with(mImageView.getContext())
//                    .load(mMarker.getImage().getUrl())
//                    .placeholder(R.color.grey_400)
//                    .into(mImageView);
        }

        private void loadImageFromStorage(int i) {
            File path = Environment.getExternalStorageDirectory();
            File dir = new File(path + "/save/");
            File f = new File(dir, String.valueOf(i)+"photo.jpg");
            Log.d("loaded from storage ", f.getPath());

        }

    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }


}
