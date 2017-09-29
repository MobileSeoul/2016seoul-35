package com.sopt.bodeum.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sopt.bodeum.R;

public class FacilityViewHolder extends RecyclerView.ViewHolder {


    public ImageButton favoriteBtn_card;
    public ImageView imageView,image_rank;
    public TextView text1, text2;

    public FacilityViewHolder(final View itemView) {
        super(itemView);

        favoriteBtn_card = (ImageButton) itemView.findViewById(R.id.favoriteBtn_card);
        imageView = (ImageView)itemView.findViewById(R.id.imageView);
        text1 = (TextView)itemView.findViewById(R.id.textView1);
        text2 = (TextView)itemView.findViewById(R.id.textView2);
        image_rank = (ImageView)itemView.findViewById(R.id.image_rank);
    }
}
