package com.sopt.bodeum.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sopt.bodeum.Model.ReviewItem;
import com.sopt.bodeum.Network.AwsNetworkService;
import com.sopt.bodeum.R;
import com.sopt.bodeum.ViewHolder.ReviewViewHolder;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by USER on 2016-06-30.
 */
public class ReviewAdapter extends BaseAdapter {
    private ArrayList<ReviewItem> reviewItems;
    LayoutInflater layoutInflater;
    Context context;

    public ReviewAdapter(ArrayList<ReviewItem> reviewItems, Context context) {
        this.reviewItems = reviewItems;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return reviewItems != null ? reviewItems.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return (reviewItems != null && (reviewItems.size() > position && position >= 0) ? reviewItems.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ReviewViewHolder reviewHolder = new ReviewViewHolder();

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.reviewitem, parent, false);
            reviewHolder.imageProfile = (ImageView) convertView.findViewById(R.id.imageProfile);
            reviewHolder.imageGrade = (ImageView) convertView.findViewById(R.id.imageGrade);
            reviewHolder.ivReview = (ImageView)convertView.findViewById(R.id.ivReview);
            reviewHolder.textName = (TextView) convertView.findViewById(R.id.textName);
            reviewHolder.textTime = (TextView) convertView.findViewById(R.id.textTime);
            reviewHolder.textContent = (TextView) convertView.findViewById(R.id.textContent);
            reviewHolder.txtGrade = (TextView)convertView.findViewById(R.id.txtGrade);
            convertView.setTag(reviewHolder);
        } else {
            reviewHolder = (ReviewViewHolder) convertView.getTag();
        }

        //glide
        //프로필사진
        String url = AwsNetworkService.baseUrl+ "images/"+reviewItems.get(position).Member_photo;
        Glide.with(this.context)
                .load(url)
                .bitmapTransform(new CropCircleTransformation(this.context))
                .into(reviewHolder.imageProfile);

        //glide
        //콘텐트 사진
        String ContentUrl = AwsNetworkService.baseUrl+ "images/"+reviewItems.get(position).Review_photo;
        Glide.with(this.context)
                .load(ContentUrl)
                .into(reviewHolder.ivReview);

        //후기리스트에서 점수에 따라 이미지 다르게 넣기
        if(reviewItems.get(position).Review_score == 0){
            reviewHolder.imageGrade.setImageResource(R.mipmap.star_zero);
        }
        else if(reviewItems.get(position).Review_score == 1){
            reviewHolder.imageGrade.setImageResource(R.mipmap.star_one);
        }
        else if(reviewItems.get(position).Review_score == 2){
            reviewHolder.imageGrade.setImageResource(R.mipmap.star_two);
        }
        else if(reviewItems.get(position).Review_score == 3){
            reviewHolder.imageGrade.setImageResource(R.mipmap.star_three);
        }
        else if(reviewItems.get(position).Review_score == 4){
            reviewHolder.imageGrade.setImageResource(R.mipmap.star_four);
        }
        else if(reviewItems.get(position).Review_score == 5){
            reviewHolder.imageGrade.setImageResource(R.mipmap.star_five);
        }

        reviewHolder.textName.setText(reviewItems.get(position).Member_nick); //닉네임
//        reviewHolder.textTime.setText(reviewItems.get(position).Review_date); //시간
        String time = reviewItems.get(position).Review_date;
        try {
            reviewHolder.textTime.setText(time.substring(0, 10));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        reviewHolder.textContent.setText(reviewItems.get(position).Review_content); //내용
        reviewHolder.txtGrade.setText(String.valueOf(reviewItems.get(position).Review_score).substring(0,3));


        return convertView;
    }

    public void setSource(ArrayList<ReviewItem> reviewDatas) {
        this.reviewItems = reviewDatas;
        this.notifyDataSetChanged();
    }
}
