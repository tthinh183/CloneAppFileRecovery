package com.app.allfilerecovery.view.others.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.allfilerecovery.R;
import com.app.allfilerecovery.callback.IClickItemFolder;
import com.app.allfilerecovery.view.others.model.PhotoFolderRecoveryModel;
import com.bumptech.glide.Glide;

import java.util.List;

public class PhotoFolderRecoveryAdapter extends RecyclerView.Adapter<PhotoFolderRecoveryAdapter.PhotoFolderRecoveryViewHolder> {

    private final List<PhotoFolderRecoveryModel> photoRecoveryModelList;
    private final IClickItemFolder iClickItemFolder;
    private final Context context;

    public PhotoFolderRecoveryAdapter(Context context, List<PhotoFolderRecoveryModel> photoRecoveryModelList, IClickItemFolder iClickItemFolder) {
        this.context = context;
        this.photoRecoveryModelList = photoRecoveryModelList;
        this.iClickItemFolder = iClickItemFolder;
    }

    @NonNull
    @Override
    public PhotoFolderRecoveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder_photo, parent, false);
        return new PhotoFolderRecoveryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoFolderRecoveryViewHolder holder, int position) {
        PhotoFolderRecoveryModel photoFolder = photoRecoveryModelList.get(position);
        int startIndex = photoFolder.getNameFolder().lastIndexOf("/") + 1;
        String name = photoFolder.getNameFolder().substring(startIndex);
        holder.tvFolderName.setText(name);

        if (photoFolder.getListImages().size()  == 1) {
            Glide.with(context).load(photoFolder.getListImages().get(0).getPath()).into(holder.imgFirstPhoto);
            holder.imgSecondPhoto.setVisibility(View.GONE);
            holder.imgThirdPhoto.setVisibility(View.GONE);
        } else if (photoFolder.getListImages().size() == 2) {
            Glide.with(context).load(photoFolder.getListImages().get(0).getPath()).into(holder.imgFirstPhoto);
            Glide.with(context).load(photoFolder.getListImages().get(1).getPath()).into(holder.imgSecondPhoto);
            holder.imgThirdPhoto.setVisibility(View.GONE);
        } else {
            Glide.with(context).load(photoFolder.getListImages().get(0).getPath()).into(holder.imgFirstPhoto);
            Glide.with(context).load(photoFolder.getListImages().get(1).getPath()).into(holder.imgSecondPhoto);
            Glide.with(context).load(photoFolder.getListImages().get(2).getPath()).into(holder.imgThirdPhoto);
        }

        holder.tvAmountOfPhotos.setText(" (" + photoFolder.getAmountPhotos() + ")");

        int lastIndex = photoFolder.getNameFolder().lastIndexOf("/") + 1;
        String nameFolder = photoFolder.getNameFolder().substring(lastIndex);
        holder.viewNext.setOnClickListener(v -> iClickItemFolder.onClickItemFolder(nameFolder));
    }

    @Override
    public int getItemCount() {
        if (photoRecoveryModelList != null) {
            return photoRecoveryModelList.size();
        } else {
            return 0;
        }
    }

    public class PhotoFolderRecoveryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFolderName;
        private final TextView tvAmountOfPhotos;
        private final ImageView imgFirstPhoto;
        private final ImageView imgSecondPhoto;
        private final ImageView imgThirdPhoto;
        private final View viewNext;

        public PhotoFolderRecoveryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFolderName = itemView.findViewById(R.id.tv_name_folder);
            tvAmountOfPhotos = itemView.findViewById(R.id.tv_count_photo_in_folder);
            imgFirstPhoto = itemView.findViewById(R.id.img_first);
            imgSecondPhoto = itemView.findViewById(R.id.img_second);
            imgThirdPhoto = itemView.findViewById(R.id.img_third);
            viewNext = itemView.findViewById(R.id.layout_go_detail_folder);
        }
    }
}
