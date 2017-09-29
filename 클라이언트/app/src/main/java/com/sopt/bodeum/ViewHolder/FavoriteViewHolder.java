package com.sopt.bodeum.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sopt.bodeum.R;

/**
 * Created by user on 2016-07-02.
 */
public class FavoriteViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView_fav, image_rank_fav;
    public TextView text1_fav, text2_fav, text3_fav,text4_fav,text5_fav;

    public FavoriteViewHolder(final View itemView_fav) {
        super(itemView_fav);

        imageView_fav = (ImageView)itemView_fav.findViewById(R.id.imageView_fav);
        text1_fav = (TextView)itemView_fav.findViewById(R.id.textView1_fav);
        text2_fav = (TextView)itemView_fav.findViewById(R.id.textView2_fav);
        text3_fav = (TextView)itemView_fav.findViewById(R.id.textView3_fav);
        text4_fav = (TextView)itemView_fav.findViewById(R.id.textView4_fav);
        text5_fav = (TextView)itemView_fav.findViewById(R.id.textView5_fav);
        image_rank_fav = (ImageView)itemView_fav.findViewById(R.id.image_rank_fav);
    }
}