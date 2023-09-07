package com.app.allfilerecovery.view.others.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.allfilerecovery.R;
import com.app.allfilerecovery.model.PhotoRecoveryModel;
import com.app.allfilerecovery.utils.Const;
import com.app.allfilerecovery.view.others.RecoveryActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PhotoRecoveryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ArrayList<PhotoRecoveryModel> imgPhotoList;
    public ArrayList<HeaderRecoveryViewHolder> headerList = new ArrayList<>();

    private Context context;

    private int countPhotoToday = 0;
    private int countPhotoYesterday = 0;
    private int countPhotoOther = 0;
    private boolean isCheckAllToday = false;
    private boolean isCheckAllYesterday = false;
    private boolean isCheckAllOther = false;
    private TextView tvSelectAlle;

    public PhotoRecoveryAdapter(ArrayList<PhotoRecoveryModel> imagePhotoList, Context context) {
        this.imgPhotoList = imagePhotoList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == Const.VIEW_TYPE_HEADER) {
            View headerView = inflater.inflate(R.layout.item_header_recovery, parent, false);
            return new HeaderRecoveryViewHolder((headerView));
        } else {
            View view = inflater.inflate(R.layout.item_photo_recovery, parent, false);
            return new PhotoRecoveryViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return imgPhotoList.get(position).getPhotoList().size() == 0 ? Const.VIEW_TYPE_HEADER : Const.VIEW_TYPE_ITEM;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PhotoRecoveryModel photoRecoveryModel = imgPhotoList.get(position);

        if (photoRecoveryModel.getPhotoList().size() == 0) {
            HeaderRecoveryViewHolder headerViewHolder = (HeaderRecoveryViewHolder) holder;
            headerList.add(headerViewHolder);

            headerList.get(0).tvSelectAll.setVisibility(View.VISIBLE);

            headerViewHolder.tvSelectAll.setOnClickListener(v -> {
                ((RecoveryActivity) context).selectedAllPhoto(headerViewHolder.tvSelectAll);
                notifyDataSetChanged();
            });

            tvSelectAlle = headerViewHolder.tvSelectAll;

            switch (photoRecoveryModel.getCategoryPhoto()) {
                case PhotoRecoveryModel.CATEGORY_TODAY:
                    headerViewHolder.tvHeader.setText(
                            context.getString(R.string.today) +
                                    photoRecoveryModel.getAmountItemOfCategory() +
                                    context.getString(R.string.bracket));

                    headerViewHolder.viewCheckBox.setOnClickListener(view -> {
                        isCheckAllToday = !isCheckAllToday;
                        headerViewHolder.checkBox.setChecked(isCheckAllToday);
                        checkedAllPhotoByDate(isCheckAllToday, position, PhotoRecoveryModel.CATEGORY_TODAY, photoRecoveryModel.getAmountItemOfCategory());
                    });

                    if (countPhotoToday == 0) {
                        isCheckAllToday = false;
                        headerViewHolder.checkBox.setButtonDrawable(R.drawable.ic_radio_button_unchecked_24dp);
                    } else if (countPhotoToday == photoRecoveryModel.getAmountItemOfCategory()) {
                        isCheckAllToday = true;
                        headerViewHolder.checkBox.setButtonDrawable(R.drawable.ic_check_circle_black_24dp);
                    } else {
                        isCheckAllToday = true;
                        headerViewHolder.checkBox.setButtonDrawable(R.drawable.ic_check_indeterminate);
                    }
                    break;
                case PhotoRecoveryModel.CATEGORY_YESTERDAY:
                    headerViewHolder.tvHeader.setText(context.getString(R.string.yesterday) +
                            photoRecoveryModel.getAmountItemOfCategory() +
                            context.getString(R.string.bracket));

                    headerViewHolder.viewCheckBox.setOnClickListener(view -> {
                        isCheckAllYesterday = !isCheckAllYesterday;
                        headerViewHolder.checkBox.setChecked(isCheckAllYesterday);
                        checkedAllPhotoByDate(isCheckAllYesterday, position, PhotoRecoveryModel.CATEGORY_YESTERDAY, photoRecoveryModel.getAmountItemOfCategory());
                    });

                    if (countPhotoYesterday == 0) {
                        isCheckAllYesterday = false;
                        headerViewHolder.checkBox.setButtonDrawable(R.drawable.ic_radio_button_unchecked_24dp);
                    } else if (countPhotoYesterday == photoRecoveryModel.getAmountItemOfCategory()) {
                        isCheckAllYesterday = true;
                        headerViewHolder.checkBox.setButtonDrawable(R.drawable.ic_check_circle_black_24dp);
                    } else {
                        isCheckAllYesterday = true;
                        headerViewHolder.checkBox.setButtonDrawable(R.drawable.ic_check_indeterminate);
                    }
                    break;
                case PhotoRecoveryModel.CATEGORY_OTHER:
                    headerViewHolder.tvHeader.setText(context.getString(R.string.other) +
                            photoRecoveryModel.getAmountItemOfCategory() +
                            context.getString(R.string.bracket));

                    headerViewHolder.viewCheckBox.setOnClickListener(view -> {
                        isCheckAllOther = !isCheckAllOther;
                        headerViewHolder.checkBox.setChecked(isCheckAllOther);
                        checkedAllPhotoByDate(isCheckAllOther, position, PhotoRecoveryModel.CATEGORY_OTHER, photoRecoveryModel.getAmountItemOfCategory());
                    });

                    if (countPhotoOther == 0) {
                        isCheckAllOther = false;
                        headerViewHolder.checkBox.setButtonDrawable(R.drawable.ic_radio_button_unchecked_24dp);
                    } else if (countPhotoOther == photoRecoveryModel.getAmountItemOfCategory()) {
                        isCheckAllOther = true;
                        headerViewHolder.checkBox.setButtonDrawable(R.drawable.ic_check_circle_black_24dp);
                    } else {
                        isCheckAllOther = true;
                        headerViewHolder.checkBox.setButtonDrawable(R.drawable.ic_check_indeterminate);
                    }
                    break;
            }
        } else {
            PhotoRecoveryViewHolder photoViewHolder = (PhotoRecoveryViewHolder) holder;

            if (photoRecoveryModel.getPhotoList().size() == 1) {
                String firstPath = photoRecoveryModel.getPhotoList().get(0).getPath();

                Glide.with(context).load(firstPath).into(photoViewHolder.imgFirstPhoto);

                photoViewHolder.secondParentLayout.setVisibility(View.GONE);
                photoViewHolder.thirdParentLayout.setVisibility(View.GONE);
            } else if (photoRecoveryModel.getPhotoList().size() == 2) {
                String firstPath = photoRecoveryModel.getPhotoList().get(0).getPath();
                String secondPath = photoRecoveryModel.getPhotoList().get(1).getPath();

                Glide.with(context).load(firstPath).into(photoViewHolder.imgFirstPhoto);
                Glide.with(context).load(secondPath).into(photoViewHolder.imgSecondPhoto);

                photoViewHolder.secondParentLayout.setVisibility(View.VISIBLE);
                photoViewHolder.thirdParentLayout.setVisibility(View.GONE);
            } else {
                String firstPath = photoRecoveryModel.getPhotoList().get(0).getPath();
                String secondPath = photoRecoveryModel.getPhotoList().get(1).getPath();
                String thirdPath = photoRecoveryModel.getPhotoList().get(2).getPath();

                photoViewHolder.secondParentLayout.setVisibility(View.VISIBLE);
                photoViewHolder.thirdParentLayout.setVisibility(View.VISIBLE);

                Glide.with(context).load(firstPath).into(photoViewHolder.imgFirstPhoto);
                Glide.with(context).load(secondPath).into(photoViewHolder.imgSecondPhoto);
                Glide.with(context).load(thirdPath).into(photoViewHolder.imgThirdPhoto);
            }

            //get isChecked of first item
            boolean isCheckedFirstPhoto = photoRecoveryModel.getPhotoList().get(0).isChecked();
            //set isChecked to checkbox of first item
            photoViewHolder.firstCheckBox.setChecked(isCheckedFirstPhoto);
            transperentImage(photoViewHolder.layoutTransperentFirst, isCheckedFirstPhoto);


            //set onClick for first item
            photoViewHolder.firstItemPhoto.setOnClickListener(view -> {
                resetCount();
                String firstPath = photoRecoveryModel.getPhotoList().get(0).getPath();
                ((RecoveryActivity) context).setSelectedImages(firstPath, !photoViewHolder.firstCheckBox.isChecked(), tvSelectAlle);
                photoRecoveryModel.getPhotoList().get(0).setChecked(!photoViewHolder.firstCheckBox.isChecked());
                transperentImage(photoViewHolder.layoutTransperentFirst, !photoViewHolder.firstCheckBox.isChecked());
                countItemSelected();
                notifyDataSetChanged();
            });

            //set longOnClick for first item
            photoViewHolder.firstItemPhoto.setOnLongClickListener(view -> {
                ((RecoveryActivity) context).goDetailPhoto(photoRecoveryModel.getPhotoList().get(0));
                return false;
            });

            if (photoRecoveryModel.getPhotoList().size() > 1) {
                //get isChecked of second item
                boolean isCheckedSecondPhoto = photoRecoveryModel.getPhotoList().get(1).isChecked();
                //set isChecked to checkbox of second item
                photoViewHolder.secondCheckBox.setChecked(isCheckedSecondPhoto);
                transperentImage(photoViewHolder.layoutTransperentSecond, isCheckedSecondPhoto);

                //set onClick for second item
                photoViewHolder.secondItemPhoto.setOnClickListener(view -> {
                    resetCount();
                    String secondPath = photoRecoveryModel.getPhotoList().get(1).getPath();
                    ((RecoveryActivity) context).setSelectedImages(secondPath, !photoViewHolder.secondCheckBox.isChecked(), tvSelectAlle);
                    photoRecoveryModel.getPhotoList().get(1).setChecked(!photoViewHolder.secondCheckBox.isChecked());
                    transperentImage(photoViewHolder.layoutTransperentSecond, !photoViewHolder.secondCheckBox.isChecked());
                    countItemSelected();
                    notifyDataSetChanged();
                });

                //set onLongClick for second item
                photoViewHolder.secondItemPhoto.setOnLongClickListener(view -> {
                    ((RecoveryActivity) context).goDetailPhoto(photoRecoveryModel.getPhotoList().get(1));
                    return false;
                });
            }

            if (photoRecoveryModel.getPhotoList().size() > 2) {
                //get isChecked of third item
                boolean isCheckedThirdPhoto = photoRecoveryModel.getPhotoList().get(2).isChecked();
                //set isChecked to checkbox of third item
                photoViewHolder.thirdCheckBox.setChecked(isCheckedThirdPhoto);
                transperentImage(photoViewHolder.layoutTransperentThird, isCheckedThirdPhoto);

                //set onClick for third item
                photoViewHolder.thirdItemPhoto.setOnClickListener(view -> {
                    resetCount();
                    String thirdPath = photoRecoveryModel.getPhotoList().get(2).getPath();
                    ((RecoveryActivity) context).setSelectedImages(thirdPath, !photoViewHolder.thirdCheckBox.isChecked(), tvSelectAlle);
                    photoRecoveryModel.getPhotoList().get(2).setChecked(!photoViewHolder.thirdCheckBox.isChecked());
                    transperentImage(photoViewHolder.layoutTransperentThird, !photoViewHolder.thirdCheckBox.isChecked());
                    countItemSelected();
                    notifyDataSetChanged();
                });


                //set onLongClick for third item
                photoViewHolder.thirdItemPhoto.setOnLongClickListener(view -> {
                    ((RecoveryActivity) context).goDetailPhoto(photoRecoveryModel.getPhotoList().get(2));
                    return false;
                });
            }
        }
    }

    private void transperentImage(View layoutTransperent, boolean b) {
        layoutTransperent.setBackgroundColor(Color.parseColor("#8C071A3F"));
        if(b){
            layoutTransperent.setVisibility(View.VISIBLE);
        } else {
            layoutTransperent.setVisibility(View.GONE);
        }
    }

    private void checkedAllPhotoByDate(boolean isChecked, int position, int category, int amountItem) {
        if (isChecked) {
            if (category == PhotoRecoveryModel.CATEGORY_TODAY) {
                countPhotoToday = amountItem;
            } else if (category == PhotoRecoveryModel.CATEGORY_YESTERDAY) {
                countPhotoYesterday = amountItem;
            } else {
                countPhotoOther = amountItem;
            }

            for (int i = position + 1; i < imgPhotoList.size(); i++) {
                if (imgPhotoList.get(i).getPhotoList().size() == 0) {
                    notifyDataSetChanged();
                    return;
                }
                for (int j = 0; j < imgPhotoList.get(i).getPhotoList().size(); j++) {
                    String pathFileSelect = imgPhotoList.get(i).getPhotoList().get(j).getPath();
                    ((RecoveryActivity) context).setSelectedImages(pathFileSelect, true, tvSelectAlle);
                    imgPhotoList.get(i).getPhotoList().get(j).setChecked(true);
                }
            }
        } else {

            if (category == PhotoRecoveryModel.CATEGORY_TODAY) {
                countPhotoToday = 0;
            } else if (category == PhotoRecoveryModel.CATEGORY_YESTERDAY) {
                countPhotoYesterday = 0;
            } else {
                countPhotoOther = 0;
            }

            for (int i = position + 1; i < imgPhotoList.size(); i++) {
                if (imgPhotoList.get(i).getPhotoList().size() == 0) {
                    notifyDataSetChanged();
                    return;
                }
                for (int j = 0; j < imgPhotoList.get(i).getPhotoList().size(); j++) {
                    String pathFileSelect = imgPhotoList.get(i).getPhotoList().get(j).getPath();
                    ((RecoveryActivity) context).setSelectedImages(pathFileSelect, false, tvSelectAlle);
                    imgPhotoList.get(i).getPhotoList().get(j).setChecked(false);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void selectAll(boolean isChecked) {
        for (int i = 0; i < imgPhotoList.size(); i++) {
            if (imgPhotoList.get(i).getPhotoList().size() == 0) {
                if (imgPhotoList.get(i).getCategoryPhoto() == PhotoRecoveryModel.CATEGORY_TODAY) {
                    countPhotoToday = imgPhotoList.get(i).getAmountItemOfCategory();
                } else if (imgPhotoList.get(i).getCategoryPhoto() == PhotoRecoveryModel.CATEGORY_YESTERDAY) {
                    countPhotoYesterday = imgPhotoList.get(i).getAmountItemOfCategory();
                } else {
                    countPhotoOther = imgPhotoList.get(i).getAmountItemOfCategory();
                }
            }

            for (int j = 0; j < imgPhotoList.get(i).getPhotoList().size(); j++) {
                String pathFileSelect = imgPhotoList.get(i).getPhotoList().get(j).getPath();
                ((RecoveryActivity) context).setSelectedImages(pathFileSelect, isChecked, tvSelectAlle);
                imgPhotoList.get(i).getPhotoList().get(j).setChecked(isChecked);
            }
        }

        if (!isChecked) {
            countPhotoToday = 0;
            countPhotoYesterday = 0;
            countPhotoOther = 0;
        }
        notifyDataSetChanged();
    }

    private void countItemSelected() {
        for (int i = 0; i < imgPhotoList.size(); i++) {
            for (int j = 0; j < imgPhotoList.get(i).getPhotoList().size(); j++) {
                if (imgPhotoList.get(i).getPhotoList().get(j).isChecked()) {
                    if (imgPhotoList.get(i).getCategoryPhoto() == PhotoRecoveryModel.CATEGORY_TODAY) {
                        countPhotoToday++;
                    } else if (imgPhotoList.get(i).getCategoryPhoto() == PhotoRecoveryModel.CATEGORY_YESTERDAY) {
                        countPhotoYesterday++;
                    } else {
                        countPhotoOther++;
                    }
                }
            }
        }
    }

    private void resetCount() {
        countPhotoToday = 0;
        countPhotoYesterday = 0;
        countPhotoOther = 0;
    }

    @Override
    public int getItemCount() {
        if (imgPhotoList != null) {
            return imgPhotoList.size();
        } else {
            return 0;
        }
    }

    public void updateListPhoto(ArrayList<PhotoRecoveryModel> imgPhotoList) {
        this.imgPhotoList = imgPhotoList;
        notifyDataSetChanged();
    }

    public class PhotoRecoveryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFirstPhoto, imgSecondPhoto, imgThirdPhoto;
        CheckBox firstCheckBox, secondCheckBox, thirdCheckBox;
        View firstItemPhoto, secondItemPhoto, thirdItemPhoto;
        View firstParentLayout, secondParentLayout, thirdParentLayout;
        View layoutTransperentFirst, layoutTransperentSecond, layoutTransperentThird;

        public PhotoRecoveryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFirstPhoto = itemView.findViewById(R.id.iv_photo_recovery_first);
            imgSecondPhoto = itemView.findViewById(R.id.iv_photo_recovery_second);
            imgThirdPhoto = itemView.findViewById(R.id.iv_photo_recovery_third);

            firstCheckBox = itemView.findViewById(R.id.checkbox_first);
            secondCheckBox = itemView.findViewById(R.id.checkbox_second);
            thirdCheckBox = itemView.findViewById(R.id.checkbox_third);

            firstItemPhoto = itemView.findViewById(R.id.view_item_recovery_first);
            secondItemPhoto = itemView.findViewById(R.id.view_item_recovery_second);
            thirdItemPhoto = itemView.findViewById(R.id.view_item_recovery_third);

            firstParentLayout = itemView.findViewById(R.id.parent_constrain_first);
            secondParentLayout = itemView.findViewById(R.id.parent_constrain_second);
            thirdParentLayout = itemView.findViewById(R.id.parent_constrain_third);

            layoutTransperentFirst = itemView.findViewById(R.id.layout_transperent_first);
            layoutTransperentSecond = itemView.findViewById(R.id.layout_transperent_second);
            layoutTransperentThird = itemView.findViewById(R.id.layout_transperent_third);
        }
    }

    public class HeaderRecoveryViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvHeader, tvSelectAll;
        View viewCheckBox;

        public HeaderRecoveryViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.check_all);
            tvHeader = itemView.findViewById(R.id.tv_files);
            viewCheckBox = itemView.findViewById(R.id.view_checkbox_all);
            tvSelectAll = itemView.findViewById(R.id.tv_select_all);
        }
    }
}