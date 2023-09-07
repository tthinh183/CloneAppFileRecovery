package com.app.allfilerecovery.view.recovery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.allfilerecovery.R;
import com.app.allfilerecovery.config.Config;
import com.app.allfilerecovery.model.FileDataModel;
import com.app.allfilerecovery.model.PhotoRecoveryModel;
import com.app.allfilerecovery.utils.Const;
import com.app.allfilerecovery.utils.ConvertTime;
import com.app.allfilerecovery.view.others.RecoveryActivity;
import com.app.allfilerecovery.view.recovery.OtherRecoveryActivity;

import java.util.ArrayList;

public class FileRecoveryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ArrayList<FileDataModel> fileList;
    private Context context;
    private final String fileType;


    private int countFileToday = 0;
    private int countFileYesterday = 0;
    private int countFileOther = 0;

    private boolean isCheckAllToday = false;
    private boolean isCheckAllYesterday = false;
    private boolean isCheckAllOther = false;

    private int amountFileToday, amountFileYesterday, amountFileOther;

    public FileRecoveryAdapter(ArrayList<FileDataModel> fileList, Context context, String fileType) {
        this.fileList = fileList;
        this.context = context;
        this.fileType = fileType;

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
            View view = inflater.inflate(R.layout.item_file_recovery, parent, false);
            return new FileRecoveryViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return fileList.get(position).getPath().isEmpty() ? Const.VIEW_TYPE_HEADER : Const.VIEW_TYPE_ITEM;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FileDataModel fileRecoveryModel = fileList.get(position);

        if (fileRecoveryModel.getPath().isEmpty()) {
            HeaderRecoveryViewHolder headerViewHolder = (HeaderRecoveryViewHolder) holder;
            switch (fileRecoveryModel.getCategoryFile()) {
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
            FileRecoveryViewHolder fileHolder = (FileRecoveryViewHolder) holder;
            FileDataModel file = fileList.get(position);
            String path = file.getPath();

            boolean isChecked = file.isChecked();
            fileHolder.checkBox.setChecked(isChecked);

            // get name file from path
            int startIndex = file.getPath().lastIndexOf("/") + 1;
            String name = file.getPath().substring(startIndex);
            fileHolder.tvNameFile.setText(name);

            // set size file
            fileHolder.tvSizeFile.setText(file.getSize());

            // get duration audio and video file
            if (fileType.equals(Config.FILE_TYPE_VIDEO)) {
                // get duration of video
                try {
                    Uri uri = Uri.parse(path);
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(context, uri);
                    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    long timeInSecond = Long.parseLong(time);
                    int timeSecond = (int) (timeInSecond / 1000);
                    fileHolder.tvDuration.setText(ConvertTime.convertTime(timeSecond));
                } catch (Exception e) {
                    fileHolder.tvDuration.setText("00:00");
                    e.printStackTrace();
                }
            }

            // get duration of audio
            if (fileType.equals(Config.FILE_TYPE_AUDIO)) {
                try {
                    Uri uri = Uri.parse(path);
                    MediaPlayer mp = MediaPlayer.create(context, uri);
                    fileHolder.tvDuration.setText(ConvertTime.convertTime(mp.getDuration() / 1000));
                } catch (Exception e) {
                    fileHolder.tvDuration.setText("00:00");
                    e.printStackTrace();
                }
            }

            fileHolder.viewItem.setOnClickListener(view -> {
                ((OtherRecoveryActivity) context).setSelectedFiles(path, !fileHolder.checkBox.isChecked());
                file.setChecked(!fileHolder.checkBox.isChecked());
                countItemSelected();
                notifyDataSetChanged();
            });

            fileHolder.viewItem.setOnLongClickListener(view -> {
                ((OtherRecoveryActivity) context).goDetailFile(file);
                return false;
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

            for (int i = position + 1; i < fileList.size(); i++) {
                if (fileList.get(i).getPath().isEmpty()) {
                    notifyDataSetChanged();
                    return;
                } else {
                    String pathFileSelect = fileList.get(i).getPath();
                    ((OtherRecoveryActivity) context).setSelectedFiles(pathFileSelect, true);
                    fileList.get(i).setChecked(true);
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

            for (int i = position + 1; i < fileList.size(); i++) {
                if (fileList.get(i).getPath().isEmpty()) {
                    notifyDataSetChanged();
                    return;
                } else {
                    String pathFileSelect = fileList.get(i).getPath();
                    ((OtherRecoveryActivity) context).setSelectedFiles(pathFileSelect, false);
                    fileList.get(i).setChecked(false);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void selectAll(boolean isChecked) {
        for (int i = 0; i < fileList.size(); i++) {
            String pathFileSelect = fileList.get(i).getPath();
            if (!pathFileSelect.isEmpty()) {
                ((OtherRecoveryActivity) context).setSelectedFiles(pathFileSelect, isChecked);
                fileList.get(i).setChecked(isChecked);
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
        for (int i = 0; i < fileList.size(); i++) {
            if (fileList.get(i).isChecked()) {
                if (fileList.get(i).getCategoryFile() == Const.CATEGORY_TODAY) {
                    countFileToday++;
                } else if (fileList.get(i).getCategoryFile() == Const.CATEGORY_YESTERDAY) {
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
        for (int i = 0; i < fileList.size(); i++) {
            if (!fileList.get(i).getPath().isEmpty()) {
                if (fileList.get(i).getCategoryFile() == Const.CATEGORY_TODAY) {
                    amountFileToday++;
                } else if (fileList.get(i).getCategoryFile() == Const.CATEGORY_YESTERDAY) {
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

    @Override
    public int getItemCount() {
        if (fileList != null) {
            return fileList.size();
        } else {
            return 0;
        }
    }

    public void updateListFiles(ArrayList<FileDataModel> fileList) {
        this.fileList = fileList;
        notifyDataSetChanged();
    }

    public class FileRecoveryViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvNameFile;
        TextView tvSizeFile;
        TextView tvDuration;
        View viewItem;

        public FileRecoveryViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cb_file);
            tvNameFile = itemView.findViewById(R.id.tv_name_file);
            tvSizeFile = itemView.findViewById(R.id.tv_size_file);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            viewItem = itemView.findViewById(R.id.view_item_recovery);
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
