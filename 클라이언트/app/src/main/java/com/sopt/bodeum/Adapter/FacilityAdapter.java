package com.sopt.bodeum.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sopt.bodeum.Activity.FaciliyInfo_Activity;
import com.sopt.bodeum.Component.GlobalApplication;
import com.sopt.bodeum.Model.FacilityInfo;
import com.sopt.bodeum.Network.AwsNetworkService;
import com.sopt.bodeum.R;
import com.sopt.bodeum.ViewHolder.FacilityViewHolder;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by 오민영 on 2016-06-27.
 *
 */
public class FacilityAdapter extends RecyclerView.Adapter<FacilityViewHolder> {

    private ArrayList<FacilityInfo> itemDatas;
    Context context; // Glide에서 필요한 Navigation_Activity Context
    AwsNetworkService awsNetworkService = GlobalApplication.getGlobalApplicationContext().getAwsNetwork();
    String Userid;

    public FacilityAdapter(ArrayList<FacilityInfo> itemDatas, Context context, String Userid){
        this.itemDatas = itemDatas;
        this.context = context;
        this.Userid = Userid;
    }


    public void setSource(ArrayList<FacilityInfo> facilityItems) {
        this.itemDatas = facilityItems;
        this.notifyDataSetChanged();
    }

    //FacilityViewHolder
    @Override
    public FacilityViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        FacilityInfo item = itemDatas.get(position);
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview,null);

//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_cardview, parent,false);
        FacilityViewHolder viewHolder = new FacilityViewHolder(itemView);

        System.out.println("@@@@@@@@@@@@name="+item.Facility_name);


        return viewHolder;

    }

    //ListView??getView()
    @Override
    public void onBindViewHolder(final FacilityViewHolder holder, final int position) {
//        holder.imageView.setImageResource(itemDatas.get(position).getImage());
        //glide
        String url = AwsNetworkService.baseUrl+ "images/"+itemDatas.get(position).Facility_photo;
        Glide.with(this.context)
                .load(url)
                .into(holder.imageView);
        holder.text1.setText(itemDatas.get(position).Facility_name);
        holder.text2.setText(itemDatas.get(position).Facility_kind);

        if("A".equals(itemDatas.get(position).Facility_avg)){
            holder.image_rank.setImageResource(R.mipmap.card_rank_a);
        }
        else if("B".equals(itemDatas.get(position).Facility_avg)){
            holder.image_rank.setImageResource(R.mipmap.card_rank_b);
        }
        else if("C".equals(itemDatas.get(position).Facility_avg)){
            holder.image_rank.setImageResource(R.mipmap.card_rank_c);
        }
        else if("D".equals(itemDatas.get(position).Facility_avg)){
            holder.image_rank.setImageResource(R.mipmap.card_rank_d);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,FaciliyInfo_Activity.class);
                i.putExtra("Facility_id", itemDatas.get(position).Facility_id);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

        if(itemDatas.get(position).Favorite_facil==null){
            holder.favoriteBtn_card.setImageResource(R.mipmap.card_heart);
        }else{
            holder.favoriteBtn_card.setImageResource(R.mipmap.card_heart_full);
        }

        //클릭 이벤트

        holder.favoriteBtn_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String facil_id =  itemDatas.get(position).Facility_id;

                System.out.println("@@@@@@@@facilityId : " + facil_id);

                //서버연동
                HashMap<String, String> param = new HashMap<>();
                param.put("Member", Userid);
                param.put("Facility", facil_id);

                Call<Object> addFavorite = awsNetworkService.addFavorite(param);
                addFavorite.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Response<Object> response, Retrofit retrofit) {
                        int statusCode = response.code();

                        System.out.println("@@@@@@ commWriteReply called");
                        Log.i("@@@@@@MyTag", "응답코드 : " + statusCode);
                        System.out.println("@@@@@@ response.body() : " +response.body());

                        if (itemDatas.get(position).Favorite_facil == null) {
                            Toast.makeText(context, "찜하기", Toast.LENGTH_SHORT).show();
                            holder.favoriteBtn_card.setImageResource(R.mipmap.card_heart_full);
                            itemDatas.get(position).Favorite_facil = "false";
                        } else {
                            Toast.makeText(context, "취소", Toast.LENGTH_SHORT).show();
                            holder.favoriteBtn_card.setImageResource(R.mipmap.card_heart);
                            itemDatas.get(position).Favorite_facil = null;
                        }

                        if (response.code() == 200) {
                            //Toast.makeText(context, "찜하기 완료", Toast.LENGTH_LONG).show();

                        } else if (response.code() == 503) {
                            Log.i("MyTag", "응답코드 : " + statusCode);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(context, "Failed to 찜하기", Toast.LENGTH_LONG).show();
                        Log.i("MyTag ", "에러내용 : " + t.getMessage());
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return (itemDatas != null) ? itemDatas.size() : 0;
    }


}
