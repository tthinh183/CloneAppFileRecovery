package com.app.allfilerecovery.view.others.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.allfilerecovery.R;
import com.app.allfilerecovery.model.ImageDataModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AllPhotoFolderRecoveryAdapter extends RecyclerView.Adapter<AllPhotoFolderRecoveryAdapter.AllPhotoFolderRecoveryViewHolder> {

    private ArrayList<ImageDataModel> imgPhotoList;
    private Context context;

    public AllPhotoFolderRecoveryAdapter(ArrayList<ImageDataModel> imagePhotoList, Context context) {
        this.imgPhotoList = imagePhotoList;
        this.context = context;
    }

    @NonNull
    @Override
    public AllPhotoFolderRecoveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_folder_photo, parent, false);
        return new AllPhotoFolderRecoveryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllPhotoFolderRecoveryAdapter.AllPhotoFolderRecoveryViewHolder holder, int position) {
        String path = imgPhotoList.get(position).getPath();

        Log.d("AllPhotoFolderAdapter",
                "onBindViewHolder: " + imgPhotoList.get(position).getLastModified() +
                        " path: " + imgPhotoList.get(position).getPath());

        if (path.contains("WhatsApp/Media/.Statuses")) {
            Log.d("AllPhotoFolderAdapter", "onBindViewHolder: WhatsApp_Status: " + path);
        }

        Glide.with(context).load(path).into(holder.imgPhotoAllFolder);
    }

    @Override
    public int getItemCount() {
        if (imgPhotoList != null) {
            if (imgPhotoList.size() <= 9) {
                return imgPhotoList.size();
            }
            else {
                return 9;
            }
        } else {
            return 0;
        }
    }

    public class AllPhotoFolderRecoveryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgPhotoAllFolder;

        public AllPhotoFolderRecoveryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhotoAllFolder = itemView.findViewById(R.id.img_photo_all_folder);
        }
    }
}
