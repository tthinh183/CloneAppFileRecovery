package com.app.allfilerecovery.view.history.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.allfilerecovery.R;
import com.app.allfilerecovery.callback.IClickItemHistory;
import com.app.allfilerecovery.config.Config;
import com.app.allfilerecovery.model.HistoryFileModel;
import com.app.allfilerecovery.utils.Const;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryAdapterViewHolder> {

    public ArrayList<HistoryFileModel> historyFileList;
    private final Context context;
    private IClickItemHistory iClickItemHistory;
    Long date;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    public HistoryAdapter(ArrayList<HistoryFileModel> historyFileList, Context context, IClickItemHistory iClickItemHistory) {
        this.historyFileList = historyFileList;
        this.context = context;
        this.iClickItemHistory = iClickItemHistory;
    }

    @NonNull
    @Override
    public HistoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapterViewHolder holder, int position) {
        HistoryFileModel historyFile = historyFileList.get(position);
        String path = historyFile.getPath();
        String typeFile = historyFile.getTypeFile();
        date = historyFile.getLastModified();

        // get name file from path
        int startIndex = historyFile.getPath().lastIndexOf("/") + 1;
        String name = historyFile.getPath().substring(startIndex);
        holder.tvName.setText(name);

        // set path file
        holder.tvPath.setText(path.substring(0, startIndex));

        // set size file
        holder.tvSize.setText(historyFile.getSize());

        // set date restore
        Date photoDate = new Date(date);
        String strDate = dateFormat.format(photoDate);
        holder.tvTimeRestore.setText(strDate);

        setImgHistoryItem(typeFile, holder.imgItemHistory, path);

        holder.itemView.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString(Const.FILE_TYPE_HISTORY, typeFile);
            bundle.putString(Const.NAME_HISTORY, name);
            bundle.putString(Const.PATH_HISTORY, path);
            bundle.putString(Const.SIZE_HISTORY, historyFile.getSize());
            bundle.putString(Const.TIME_RESTORE_HISTORY, strDate);
            iClickItemHistory.onClickItemHistory(bundle);
        });
    }

    private void setImgHistoryItem(String typeFile, ImageView img, String path) {
        switch (typeFile) {
            case Config.FILE_TYPE_IMAGE:
                if (path.isEmpty()) {
                    img.setImageResource(R.drawable.image_intro1);
                } else {
                    Glide.with(context).load(path).into(img);
                }
                break;
            case Config.FILE_TYPE_VIDEO:
                img.setImageResource(R.drawable.ic_video_history);
                break;
            case Config.FILE_TYPE_AUDIO:
                img.setImageResource(R.drawable.ic_audio_history);
                break;
            case Config.FILE_TYPE_FILE:
                img.setImageResource(R.drawable.ic_file_history);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (historyFileList != null) {
            return historyFileList.size();
        } else {
            return 0;
        }
    }

    public void updateListFile(ArrayList<HistoryFileModel> newHistoryFileList) {
        this.historyFileList = newHistoryFileList;
        notifyDataSetChanged();
    }

    public class HistoryAdapterViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgItemHistory;
        TextView tvName, tvPath, tvSize, tvTimeRestore;

        public HistoryAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItemHistory = itemView.findViewById(R.id.img_item_history);
            tvName = itemView.findViewById(R.id.tv_title);
            tvPath = itemView.findViewById(R.id.tv_path);
            tvSize = itemView.findViewById(R.id.tv_size);
            tvTimeRestore = itemView.findViewById(R.id.tv_time_restore);
        }
    }
}
