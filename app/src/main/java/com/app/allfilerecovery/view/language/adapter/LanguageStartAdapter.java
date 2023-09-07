package com.app.allfilerecovery.view.language.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.app.allfilerecovery.R;
import com.app.allfilerecovery.view.language.Interface.IClickItemLanguage;
import com.app.allfilerecovery.view.language.Model.LanguageModel;

import java.util.List;

public class LanguageStartAdapter extends RecyclerView.Adapter<LanguageStartAdapter.LangugeViewHolder> {
    private List<LanguageModel> languageModelList;
    private IClickItemLanguage iClickItemLanguage;
    private Context context;
    public LanguageStartAdapter(List<LanguageModel> languageModelList,IClickItemLanguage listener,Context context) {
        this.languageModelList = languageModelList;
        this.iClickItemLanguage = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public LangugeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language_start,parent,false);
        return new LangugeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LangugeViewHolder holder, int position) {
        LanguageModel languageModel=languageModelList.get(position);
        if(languageModel==null){
            return;
        }
        holder.tvLang.setText(languageModel.getName());
        if(languageModel.getActive()){
            holder.layoutItem.setBackground(
                    ContextCompat.getDrawable(context, R.drawable.bg_item_lang_selected)
            );
        }else{
            holder.layoutItem.setBackground(
                    ContextCompat.getDrawable(context, R.drawable.border_bottom_item_lang)
            );
            holder.tvLang.setTextColor(context.getResources().getColor(R.color.black,null));
        }

        switch (languageModel.getCode()) {
            case "fr":
                Glide.with(context).asBitmap()
                        .load(R.drawable.ic_lang_fran)
                        .into(holder.icLang);
                break;
            case "es":
                Glide.with(context).asBitmap()
                        .load(R.drawable.ic_lang_spain)
                        .into(holder.icLang);
                break;
            case "zh":
                Glide.with(context).asBitmap()
                        .load(R.drawable.ic_lang_china)
                        .into(holder.icLang);
                break;
            case "in":
                Glide.with(context).asBitmap()
                        .load(R.drawable.ic_lang_indo)
                        .into(holder.icLang);
                break;
            case "hi":
                Glide.with(context).asBitmap()
                        .load(R.drawable.ic_lang_hin)
                        .into(holder.icLang);
                break;
            case "de":
                Glide.with(context).asBitmap()
                        .load(R.drawable.ic_lang_ger)
                        .into(holder.icLang);
                break;
            case "pt":
                Glide.with(context).asBitmap()
                        .load(R.drawable.ic_lang_pot)
                        .into(holder.icLang);
                break;
            case "en":
                Glide.with(context).asBitmap()
                        .load(R.drawable.ic_lang_eng)
                        .into(holder.icLang);
                break;
        }

        holder.layoutItem.setOnClickListener(v -> {
            setCheck(languageModel.getCode());
            iClickItemLanguage.onClickItemLanguage(languageModel.getCode());
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        if(languageModelList!=null){
            return languageModelList.size();
        }else{
            return 0;
        }
    }

    public class LangugeViewHolder extends RecyclerView.ViewHolder{
        private TextView tvLang;
        private RelativeLayout layoutItem;
        private ImageView icLang;
        public LangugeViewHolder(@NonNull View itemView) {
            super(itemView);
            icLang = itemView.findViewById(R.id.icLang);
            tvLang = itemView.findViewById(R.id.tvLang);
            layoutItem = itemView.findViewById(R.id.layoutItemLanguage);
        }
    }
    public void setCheck(String code){
        for(LanguageModel item :languageModelList){
            if(item.getCode().equals(code)){
                item.setActive(true);
            }else{
                item.setActive(false);
            }

        }
        notifyDataSetChanged();
    }
}
