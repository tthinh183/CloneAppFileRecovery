package com.app.allfilerecovery.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.allfilerecovery.R;

import java.util.List;

public class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.SlideViewHolder> {

    private final List<Integer> listSlide;

    public SlideAdapter(List<Integer> imageList) {
        this.listSlide = imageList;
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SlideViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_image_slide, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        holder.imageView.setImageResource(listSlide.get(position));
    }

    @Override
    public int getItemCount() {
        return listSlide.size();
    }

    class SlideViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        public SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_slide_image);
        }
    }
}
