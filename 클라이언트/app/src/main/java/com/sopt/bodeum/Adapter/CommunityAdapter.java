package com.sopt.bodeum.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sopt.bodeum.Model.CommItem;
import com.sopt.bodeum.Network.AwsNetworkService;
import com.sopt.bodeum.R;
import com.sopt.bodeum.ViewHolder.CommViewHolder;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by jingyu on 16. 6. 28..
 */
public class CommunityAdapter extends BaseAdapter {
    Context context; // Glide에서 필요한 Navigation_Activity Context

    private ArrayList<CommItem> commItems;
    LayoutInflater layoutInflater;

    public CommunityAdapter(ArrayList<CommItem> commItems, Context context) {
        this.commItems = commItems;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    public void setSource(ArrayList<CommItem> CommDatas) {
        this.commItems = CommDatas;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return commItems != null ? commItems.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return (commItems != null && (commItems.size() > position && position >= 0) ? commItems.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        CommViewHolder viewHolder = new CommViewHolder();

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_community, parent, false);
            viewHolder.Comm_profile = (ImageView) convertView.findViewById(R.id.ivCommProfile);
            viewHolder.Comm_nic = (TextView) convertView.findViewById(R.id.txtCommListName);
            viewHolder.Comm_title = (TextView) convertView.findViewById(R.id.txtCommListTitle);
            viewHolder.Comm_time = (TextView) convertView.findViewById(R.id.txtCommListDate);
            //viewHolder.Comm_like = (TextView) convertView.findViewById(R.id.txtLikeCnt);
            viewHolder.Comm_reply = (TextView) convertView.findViewById(R.id.txtReplyCnt);
            //viewHolder.ivHeart = (ImageView) convertView.findViewById(R.id.ivHeart);
            viewHolder.ivReply = (ImageView) convertView.findViewById(R.id.ivReply);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CommViewHolder) convertView.getTag();
        }

        viewHolder.Comm_nic.setText(commItems.get(position).getMember_nick());
        viewHolder.Comm_title.setText(commItems.get(position).getComm_title());
        //time 추출
        String time = commItems.get(position).getComm_time();
        try {
            viewHolder.Comm_time.setText(time.substring(0, 10));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //viewHolder.Comm_like.setText(commItems.get(position).Comm_like);
        viewHolder.Comm_reply.setText(commItems.get(position).getComm_reply());
        viewHolder.ivReply.setImageResource(R.mipmap.community_reply);
        //viewHolder.ivHeart.setImageResource(R.mipmap.community_heart);

        //Glide api로 이미지로딩

        int len=0;
        try {
            len = commItems.get(position).Member_email.length();
        }catch (Exception e){
            e.printStackTrace();
        }
        String url="";
        try {
            if (commItems.get(position).Member_email.substring(len - 9, len).equals("kakao.com")) { //카카오 로그인일 때
                url = commItems.get(position).Member_photo;
            } else {
                url = AwsNetworkService.baseUrl + "images/" + commItems.get(position).Member_photo;
            }

//            System.out.println("@@@@@@@@@@@kakao 계정인가 ? " + commItems.get(position).Member_email.substring(len - 9, len));


//            System.out.println("@@@@@@@@@@@유알엘" + url);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        Glide.with(this.context)
                .load(url)
                .bitmapTransform(new CropCircleTransformation(this.context))
                .into(viewHolder.Comm_profile);
        //--------------//
        return convertView;
    }
}
