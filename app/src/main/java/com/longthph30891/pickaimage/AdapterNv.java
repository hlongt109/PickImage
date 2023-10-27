package com.longthph30891.pickaimage;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.FirebaseFirestore;
import com.longthph30891.pickaimage.Model.NhanVat;

import java.util.ArrayList;

public class AdapterNv extends RecyclerView.Adapter<AdapterNv.ViewHolder> {
    private final ArrayList<NhanVat>list;
    private final Context context;
    FirebaseFirestore database;

    public AdapterNv(ArrayList<NhanVat> list, Context context,FirebaseFirestore database) {
        this.list = list;
        this.context = context;
        this.database = database;
    }

    @NonNull
    @Override
    public AdapterNv.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_nv,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNv.ViewHolder holder, int position) {
        NhanVat nv = list.get(position);
        // hien du lieu len view
        holder.tvTen.setText(list.get(position).getTenNv());
        Glide.with(context).load(nv.getImg())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.imgNv);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgNv;
        TextView tvTen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgNv = itemView.findViewById(R.id.img_hinh_item);
            tvTen = itemView.findViewById(R.id.tv_ten_item);
        }
    }
}
