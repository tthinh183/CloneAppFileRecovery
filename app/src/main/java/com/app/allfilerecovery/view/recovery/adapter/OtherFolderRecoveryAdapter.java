package com.app.allfilerecovery.view.recovery.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.allfilerecovery.R;
import com.app.allfilerecovery.callback.IClickItemFolder;
import com.app.allfilerecovery.view.recovery.model.OtherFolderRecoveryModel;

import java.util.List;

public class OtherFolderRecoveryAdapter extends RecyclerView.Adapter<OtherFolderRecoveryAdapter.OtherFolderRecoveryViewHolder> {

    private final List<OtherFolderRecoveryModel> otherRecoveryList;
    private final IClickItemFolder iClickItemFolder;

    public OtherFolderRecoveryAdapter( List<OtherFolderRecoveryModel> otherRecoveryList, IClickItemFolder iClickItemFolder) {
        this.otherRecoveryList = otherRecoveryList;
        this.iClickItemFolder = iClickItemFolder;
    }

    @NonNull
    @Override
    public OtherFolderRecoveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_folder, parent, false);
        return new OtherFolderRecoveryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OtherFolderRecoveryViewHolder holder, int position) {
        OtherFolderRecoveryModel otherFolder = otherRecoveryList.get(position);

        int lastIndex = otherFolder.getNameFolder().lastIndexOf("/") + 1;
        String nameFolder = otherFolder.getNameFolder().substring(lastIndex);
        holder.itemView.setOnClickListener(view -> iClickItemFolder.onClickItemFolder(nameFolder));

        int startIndex = otherFolder.getNameFolder().lastIndexOf("/") + 1;
        int count = otherFolder.getAmountFiles();
        String name = otherFolder.getNameFolder().substring(startIndex);
        holder.tvNameFolder.setText(name + " (" + count + ")");
    }

    @Override
    public int getItemCount() {
        if (otherRecoveryList != null) {
            return otherRecoveryList.size();
        } else {
            return 0;
        }
    }

    public class OtherFolderRecoveryViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameFolder;
        ImageView imgNext;
        public OtherFolderRecoveryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameFolder = itemView.findViewById(R.id.tv_name_folder);
            imgNext = itemView.findViewById(R.id.img_go_to_detail);
        }
    }
}
