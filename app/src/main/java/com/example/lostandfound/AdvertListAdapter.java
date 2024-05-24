package com.example.lostandfound;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class AdvertListAdapter extends RecyclerView.Adapter<AdvertListAdapter.AdvertViewHolder> {

    private List<String> advertList;
    private OnAdvertClickListener listener;

    public interface OnAdvertClickListener {
        void onAdvertClick(int position);
    }

    public AdvertListAdapter(List<String> advertList, OnAdvertClickListener listener) {
        this.advertList = advertList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdvertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_advert, parent, false);
        return new AdvertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdvertViewHolder holder, int position) {
        String advertName = advertList.get(position);
        holder.bind(advertName);
    }

    @Override
    public int getItemCount() {
        return advertList.size();
    }

    class AdvertViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textPost;

        public AdvertViewHolder(@NonNull View itemView) {
            super(itemView);
            textPost = itemView.findViewById(R.id.textPost);
            itemView.setOnClickListener(this);
        }

        public void bind(String advertName) {
            String[] advertDetails = advertName.split(",");
            if (advertDetails.length >= 3) {
                String post = advertDetails[1];
                String name = advertDetails[2];
                String displayText = post + ": " + name;
                textPost.setText(displayText);
            } else {
                textPost.setText("Invalid data");
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                listener.onAdvertClick(position);
            }
        }
    }
}

