package com.flag.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flag.app.OnLoadMoreListener;
import com.flag.app.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import model.Marker;

/**
 * Created by Marvin on 2/16/2018.
 */

public class MarkerAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private final LayoutInflater mLayoutInflater;
    private final List<Marker> mMarkerList;
    private OnItemClickListener mOnItemClickListener;

    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public MarkerAdapter(Context MARVIN, RecyclerView recyclerView) {
        mLayoutInflater = LayoutInflater.from(MARVIN);
        mMarkerList = new ArrayList<>();

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            onLoadMoreListener.onLoadMore();
                        }
                    });
        }

    }



    @Override
    public int getItemViewType(int position) {
        return mMarkerList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MarkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View itemMarker = mLayoutInflater.inflate(R.layout.item_marker, parent, false);
        // return new MarkerViewHolder(itemMarker);

        View v;

            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar, parent, false);

        return new MarkerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MarkerViewHolder) {
            Marker marker = mMarkerList.get(position);
            ((MarkerViewHolder) holder).bindMarker(marker);
            ((MarkerViewHolder) holder).loadImageFromStorage(position);

//        holder.cacheMarkers(position);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return mMarkerList.size();
    }

    public void refreshMarkers(List<Marker> markers) {
        mMarkerList.clear();
        mMarkerList.addAll(markers);
        notifyDataSetChanged();
    }

    public List<Marker> getMarkerList() {
        return mMarkerList;
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Marker marker);
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar1);
        }
    }

    public class MarkerViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mImageView;
        private final TextView mNameTextView;
        private final TextView mAuthorTextView;

        private Marker mMarker;

        public MarkerViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.photo_image_view);
            mNameTextView = itemView.findViewById(R.id.text_view_name);
            mAuthorTextView = itemView.findViewById(R.id.text_view_author);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mMarker);
                    }
                }
            });
        }

        void bindMarker(Marker marker) {
            mMarker = marker;
            mNameTextView.setText(mMarker.getName());

            if (mMarker.getLocation() != null && mMarker.getLocation().getAuthor() != null) {
                mAuthorTextView.setText(mMarker.getLocation().getAuthor());
            }

            if (mImageView.getContext() != null) {
                if (mMarker.getImage() != null) {
                    Picasso.with(mImageView.getContext())
                            .load(mMarker.getImage().getUrl())
                            .placeholder(R.color.grey_400)
                            .into(mImageView);
                }
            }
        }

        private void loadImageFromStorage(int i) {
            File path = Environment.getExternalStorageDirectory();
            File dir = new File(path + "/save/");
            File f = new File(dir, String.valueOf(i) + "photo.jpg");
            Log.d("loaded from storage ", f.getPath());

        }

        void cacheMarkers(final int i) {
            Picasso.with(mImageView.getContext())
                    .load(mMarker.getImage().getUrl())
                    .into(new Target() {
                              @Override
                              public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                  File path = Environment.getExternalStorageDirectory();
                                  File dir = new File(path + "/save/");
                                  dir.mkdirs();
                                  File file = new File(dir, String.valueOf(i) + "photo.jpg");
                                  Log.d("cache image", file.getPath());
                                  OutputStream out = null;
                                  try {
                                      out = new FileOutputStream(file);
                                      bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                                      out.flush();
                                      out.close();
                                  } catch (IOException e) {
                                      e.printStackTrace();
                                  }

                              }

                              @Override
                              public void onBitmapFailed(Drawable errorDrawable) {
                              }

                              @Override
                              public void onPrepareLoad(Drawable placeHolderDrawable) {
                              }
                          }
                    );
        }
    }
}
