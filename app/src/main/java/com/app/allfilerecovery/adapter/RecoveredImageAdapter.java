package com.app.allfilerecovery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.app.allfilerecovery.R;
import com.app.allfilerecovery.model.ImageDataModel;

import java.util.ArrayList;

public class RecoveredImageAdapter extends RecyclerView.Adapter<RecoveredImageAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ImageDataModel> imageList;

    public RecoveredImageAdapter(Context context, ArrayList<ImageDataModel> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_scanner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String path = imageList.get(position).getPath();
        Glide.with(context).load(path).into(holder.ivPhoto);

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPhoto;
        CheckBox checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPhoto = itemView.findViewById(R.id.iv_photo);
            checkbox = itemView.findViewById(R.id.checkbox);
            checkbox.setVisibility(View.GONE);
        }
    }

}
