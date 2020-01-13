package com.todasporuma.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.todasporuma.R;
import com.todasporuma.model.Angel;

import java.util.ArrayList;
import java.util.List;

public class AngelsAdapter extends RecyclerView.Adapter<AngelsAdapter.AngelViewHolder> {

    private List<Angel> angelList;

    public AngelsAdapter(List<Angel> angelList) {
        this.angelList = angelList;
    }

    @NonNull
    @Override
    public AngelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_angel_adapter_list, parent, false);
        return new AngelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AngelViewHolder holder, int position) {
        Angel angel = angelList.get(position);

        holder.angelName.setText(angel.getName());
        holder.angelPhone.setText(angel.getPhone());
    }

    @Override
    public int getItemCount() {
        return angelList.size();
    }

    public void setAngelList(List<Angel> angelList) {
        this.angelList = angelList;
    }

    class AngelViewHolder extends RecyclerView.ViewHolder {

        private ImageView angelPhoto;
        private TextView angelName;
        private TextView angelPhone;

        private AngelViewHolder(@NonNull View view) {
            super(view);
            angelName = view.findViewById(R.id.name);
            angelPhone = view.findViewById(R.id.phone);
            angelPhoto = view.findViewById(R.id.angelPhoto);
        }
    }
}
