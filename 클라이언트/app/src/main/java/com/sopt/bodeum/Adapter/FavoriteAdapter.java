package com.sopt.bodeum.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sopt.bodeum.Model.FavoriteItem;
import com.sopt.bodeum.R;
import com.sopt.bodeum.ViewHolder.FavoriteViewHolder;
import java.util.ArrayList;


public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteViewHolder> {

    private ArrayList<FavoriteItem> itemData_fav;
    public FavoriteAdapter(ArrayList<FavoriteItem> itemData){
        this.itemData_fav = itemData;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cardview_favorite, parent,false);
        FavoriteViewHolder viewHolder_fav = new FavoriteViewHolder(itemView);
        return viewHolder_fav;

    }
    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        holder.imageView_fav.setImageResource(itemData_fav.get(position).getImage_fav());
        holder.text1_fav.setText(itemData_fav.get(position).getName_fav());
        holder.text2_fav.setText(itemData_fav.get(position).getReview_fav());
        holder.text3_fav.setText(itemData_fav.get(position).getNum_fav());
        holder.text4_fav.setText(itemData_fav.get(position).getPart_fav());
        holder.text5_fav.setText(itemData_fav.get(position).getContents_fav());
        holder.image_rank_fav.setImageResource(itemData_fav.get(position).getGrade_fav());
    }

    @Override
    public int getItemCount() {
        return (itemData_fav != null) ? itemData_fav.size() : 0;
    }
}