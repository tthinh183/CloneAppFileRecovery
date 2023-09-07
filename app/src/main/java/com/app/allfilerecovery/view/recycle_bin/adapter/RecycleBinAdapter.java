package com.app.allfilerecovery.view.recycle_bin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.allfilerecovery.R;
import com.app.allfilerecovery.config.Config;
import com.app.allfilerecovery.model.RecycleBinModel;
import com.app.allfilerecovery.utils.Const;
import com.app.allfilerecovery.view.recycle_bin.RecycleBinFragment;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecycleBinAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ArrayList<RecycleBinModel> recycleBinFileList;
    private final Context context;
    private RecycleBinFragment recycleBinFragment;

    private int countFileToday = 0;
    private int countFileYesterday = 0;
    private int countFileOther = 0;

    private boolean isCheckAllToday = false;
    private boolean isCheckAllYesterday = false;
    private boolean isCheckAllOther = false;

    private int amountFileToday, amountFileYesterday, amountFileOther;

    Long date;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    public RecycleBinAdapter(ArrayList<RecycleBinModel> recycleBinFileList, Context context, RecycleBinFragment recycleBinFragment) {
        this.recycleBinFileList = recycleBinFileList;
        this.recycleBinFragment = recycleBinFragment;
        this.context = context;

        getAmountFile();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == Const.VIEW_TYPE_HEADER) {
            View headerView = inflater.inflate(R.layout.item_header_recovery, parent, false);
            return new HeaderRecoveryViewHolder((headerView));
        } else {
            View view = inflater.inflate(R.layout.item_recycle_bin, parent, false);
            return new RecycleBinViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return recycleBinFileList.get(position).getPath().isEmpty() ? Const.VIEW_TYPE_HEADER : Const.VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecycleBinModel recycleBinModel = recycleBinFileList.get(position);

        if (recycleBinModel.getPath().isEmpty()) {
            HeaderRecoveryViewHolder headerViewHolder = (HeaderRecoveryViewHolder) holder;
            switch (recycleBinModel.getCategoryFile()) {
                case Const.CATEGORY_TODAY:
                    headerViewHolder.tvHeader.setText(
                            context.getString(R.string.today) +
                                    amountFileToday +
                                    context.getString(R.string.bracket));

                    headerViewHolder.viewCheckBox.setOnClickListener(view -> {
                        isCheckAllToday = !isCheckAllToday;
                        headerViewHolder.checkBox.setChecked(isCheckAllToday);
                        checkedAllFileByDate(isCheckAllToday, position, Const.CATEGORY_TODAY, amountFileToday);
                    });

                    if (countFileToday == 0) {
                        isCheckAllToday = false;
                        headerViewHolder.checkBox.setButtonDrawable(R.drawable.ic_radio_button_unchecked_24dp);
                    } else if (countFileToday == amountFileToday) {
                        isCheckAllToday = true;
                        headerViewHolder.checkBox.setButtonDrawable(R.drawable.ic_check_circle_black_24dp);
                    } else {
                        isCheckAllToday = true;
                        headerViewHolder.checkBox.setButtonDrawable(R.drawable.ic_check_indeterminate);
                    }
                    break;
                case Const.CATEGORY_YESTERDAY:
                    headerViewHolder.tvHeader.setText(context.getString(R.string.yesterday) +
                            amountFileYesterday +
                            context.getString(R.string.bracket));

                    headerViewHolder.viewCheckBox.setOnClickListener(view -> {
                        isCheckAllYesterday = !isCheckAllYesterday;
                        headerViewHolder.checkBox.setChecked(isCheckAllYesterday);
                        checkedAllFileByDate(isCheckAllYesterday, position, Const.CATEGORY_YESTERDAY, amountFileYesterday);
                    });

                    if (countFileYesterday == 0) {
                        isCheckAllYesterday = false;
                        headerViewHolder.checkBox.setButtonDrawable(R.drawable.ic_radio_button_unchecked_24dp);
                    } else if (countFileYesterday == amountFileYesterday) {
                        isCheckAllYesterday = true;
                        headerViewHolder.checkBox.setButtonDrawable(R.drawable.ic_check_circle_black_24dp);
                    } else {
                        isCheckAllYesterday = true;
                        headerViewHolder.checkBox.setButtonDrawable(R.drawable.ic_check_indeterminate);
                    }
                    break;
                case Const.CATEGORY_OTHER:
                    headerViewHolder.tvHeader.setText(context.getString(R.string.other) +
                            amountFileOther +
                            context.getString(R.string.bracket));

                    headerViewHolder.viewCheckBox.setOnClickListener(view -> {
                        isCheckAllOther = !isCheckAllOther;
                        headerViewHolder.checkBox.setChecked(isCheckAllOther);
                        checkedAllFileByDate(isCheckAllOther, position, Const.CATEGORY_OTHER, amountFileOther);
                    });

                    if (countFileOther == 0) {
                        isCheckAllOther = false;
                        headerViewHolder.checkBox.setButtonDrawable(R.drawable.ic_radio_button_unchecked_24dp);
                    } else if (countFileOther == amountFileOther) {
                        isCheckAllOther = true;
                        headerViewHolder.checkBox.setButtonDrawable(R.drawable.ic_check_circle_black_24dp);
                    } else {
                        isCheckAllOther = true;
                        headerViewHolder.checkBox.setButtonDrawable(R.drawable.ic_check_indeterminate);
                    }
                    break;
            }
        } else {
            RecycleBinViewHolder fileHolder = (RecycleBinViewHolder) holder;

            String path = recycleBinModel.getPath();
            String typeFile = recycleBinModel.getTypeFile();
            date = recycleBinModel.getLastModified();

            boolean isChecked = recycleBinModel.isChecked();
            fileHolder.checkBox.setChecked(isChecked);

            // get name file from path
            int startIndex = recycleBinModel.getPath().lastIndexOf("/") + 1;
            String name = recycleBinModel.getPath().substring(startIndex);
            fileHolder.tvName.setText(name);

            // set size file
            fileHolder.tvSize.setText(recycleBinModel.getSize());

            // set date restore
            Date restoreDate = new Date(date);
            String strDate = dateFormat.format(restoreDate);
            fileHolder.tvTimeRestore.setText(strDate);

            setImgHistoryItem(typeFile, fileHolder.imgItemRecycleBin, path);

            if(recycleBinModel.getPath().equals("empty")){
                fileHolder.checkBox.setChecked(false);
                fileHolder.tvName.setText(R.string.sample);
                fileHolder.tvSize.setText("0 B");
                fileHolder.tvTimeRestore.setText("dd/MM/yyyy");

                fileHolder.itemRecycleBin.setEnabled(false);
            }

            fileHolder.itemRecycleBin.setOnClickListener(view -> {
                recycleBinFragment.setSelectedFiles(path, !fileHolder.checkBox.isChecked());
                recycleBinModel.setChecked(!fileHolder.checkBox.isChecked());
                countItemSelected();
                notifyDataSetChanged();
            });
        }
    }

    private void checkedAllFileByDate(boolean isChecked, int position, int category, int amountItem) {
        if (isChecked) {
            if (category == Const.CATEGORY_TODAY) {
                countFileToday = amountItem;
            } else if (category == Const.CATEGORY_YESTERDAY) {
                countFileYesterday = amountItem;
            } else {
                countFileOther = amountItem;
            }

            for (int i = position + 1; i < recycleBinFileList.size(); i++) {
                if (recycleBinFileList.get(i).getPath().isEmpty()) {
                    notifyDataSetChanged();
                    return;
                } else {
                    String pathFileSelect = recycleBinFileList.get(i).getPath();
                    recycleBinFragment.setSelectedFiles(pathFileSelect, true);
                    recycleBinFileList.get(i).setChecked(true);
                }
            }
        } else {

            if (category == Const.CATEGORY_TODAY) {
                countFileToday = 0;
            } else if (category == Const.CATEGORY_YESTERDAY) {
                countFileYesterday = 0;
            } else {
                countFileOther = 0;
            }

            for (int i = position + 1; i < recycleBinFileList.size(); i++) {
                if (recycleBinFileList.get(i).getPath().isEmpty()) {
                    notifyDataSetChanged();
                    return;
                } else {
                    String pathFileSelect = recycleBinFileList.get(i).getPath();
                    recycleBinFragment.setSelectedFiles(pathFileSelect, false);
                    recycleBinFileList.get(i).setChecked(false);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void selectAll(boolean isChecked) {
        for (int i = 0; i < recycleBinFileList.size(); i++) {
            String pathFileSelect = recycleBinFileList.get(i).getPath();
            if (!pathFileSelect.isEmpty()) {
                recycleBinFragment.setSelectedFiles(pathFileSelect, isChecked);
                recycleBinFileList.get(i).setChecked(isChecked);
            }
        }

        if (isChecked) {
            countFileToday = amountFileToday;
            countFileYesterday = amountFileYesterday;
            countFileOther = amountFileOther;
        } else {
            countFileToday = 0;
            countFileYesterday = 0;
            countFileOther = 0;
        }
        notifyDataSetChanged();
    }

    private void countItemSelected() {
        resetCount();
        for (int i = 0; i < recycleBinFileList.size(); i++) {
            if (recycleBinFileList.get(i).isChecked()) {
                if (recycleBinFileList.get(i).getCategoryFile() == Const.CATEGORY_TODAY) {
                    countFileToday++;
                } else if (recycleBinFileList.get(i).getCategoryFile() == Const.CATEGORY_YESTERDAY) {
                    countFileYesterday++;
                } else {
                    countFileOther++;
                }
            }
        }
    }

    private void getAmountFile() {
        amountFileToday = 0;
        amountFileYesterday = 0;
        amountFileOther = 0;
        for (int i = 0; i < recycleBinFileList.size(); i++) {
            if (!recycleBinFileList.get(i).getPath().isEmpty()) {
                if (recycleBinFileList.get(i).getCategoryFile() == Const.CATEGORY_TODAY) {
                    amountFileToday++;
                } else if (recycleBinFileList.get(i).getCategoryFile() == Const.CATEGORY_YESTERDAY) {
                    amountFileYesterday++;
                } else {
                    amountFileOther++;
                }
            }
        }
    }

    private void resetCount() {
        countFileToday = 0;
        countFileYesterday = 0;
        countFileOther = 0;
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
        if (recycleBinFileList != null) {
            return recycleBinFileList.size();
        } else {
            return 0;
        }
    }

    public void updateListFile(ArrayList<RecycleBinModel> newRecycleBinFileList) {
        this.recycleBinFileList = newRecycleBinFileList;
        notifyDataSetChanged();
    }

    public class RecycleBinViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgItemRecycleBin;
        TextView tvName, tvSize, tvTimeRestore;
        CheckBox checkBox;
        View itemRecycleBin;

        public RecycleBinViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItemRecycleBin = itemView.findViewById(R.id.img_item_recycle_bin);
            tvName = itemView.findViewById(R.id.tv_title);
            tvSize = itemView.findViewById(R.id.tv_size);
            tvTimeRestore = itemView.findViewById(R.id.tv_time_restore);
            checkBox = itemView.findViewById(R.id.cb_file_recycle_bin);
            itemRecycleBin = itemView.findViewById(R.id.view_item_recycle_bin);
        }
    }

    public class HeaderRecoveryViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvHeader;
        View viewCheckBox;

        public HeaderRecoveryViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.check_all);
            tvHeader = itemView.findViewById(R.id.tv_files);
            viewCheckBox = itemView.findViewById(R.id.view_checkbox_all);
        }
    }
}
