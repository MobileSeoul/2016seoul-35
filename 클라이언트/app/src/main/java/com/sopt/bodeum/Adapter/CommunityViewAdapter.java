package com.sopt.bodeum.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sopt.bodeum.Model.ReplyItem;
import com.sopt.bodeum.R;
import com.sopt.bodeum.ViewHolder.CommViewViewHolder;

import java.util.ArrayList;

/**
 * Created by jingyu on 16. 6. 29..
 */
public class CommunityViewAdapter extends BaseAdapter{
    private ArrayList<ReplyItem> commReplyItems;
    LayoutInflater layoutInflater;

    public CommunityViewAdapter(ArrayList<ReplyItem> commReplyItems, Context context) {
        this.commReplyItems = commReplyItems;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount() {
        return commReplyItems != null ? commReplyItems.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return (commReplyItems != null && (commReplyItems.size() > position && position >= 0) ? commReplyItems.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        CommViewViewHolder viewHolder = new CommViewViewHolder();

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_community_view, parent, false);
            viewHolder.txtCommViewReplyId = (TextView) convertView.findViewById(R.id.txtCommViewReplyId);
            viewHolder.txtCommViewReplyDate = (TextView) convertView.findViewById(R.id.txtCommViewReplyDate);
            viewHolder.txtViewReply = (TextView) convertView.findViewById(R.id.txtCommViewReply);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CommViewViewHolder) convertView.getTag();
        }

        viewHolder.txtCommViewReplyId.setText(commReplyItems.get(position).Member_nic);

        viewHolder.txtViewReply.setText(commReplyItems.get(position).Reply_text);

        return convertView;
    }


    public void setSource(ArrayList<ReplyItem> replyItems) {
        this.commReplyItems = replyItems;
        this.notifyDataSetChanged();
    }
}
