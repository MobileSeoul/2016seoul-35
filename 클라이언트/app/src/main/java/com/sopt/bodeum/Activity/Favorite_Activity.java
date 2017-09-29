package com.sopt.bodeum.Activity;


import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.sopt.bodeum.Adapter.FacilityAdapter;
import com.sopt.bodeum.Component.GlobalApplication;
import com.sopt.bodeum.Model.FacilityInfo;
import com.sopt.bodeum.Network.AwsNetworkService;
import com.sopt.bodeum.R;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by user on 2016-07-02.
 */
public class Favorite_Activity extends AppCompatActivity {
    RecyclerView recyclerView_fav;
    RecyclerView.Adapter mAdapter_fav;
    LinearLayoutManager mLayoutManager_fav;

    AwsNetworkService awsNetworkService = GlobalApplication.getGlobalApplicationContext().getAwsNetwork();

    ArrayList<FacilityInfo> facilityDatas;

    SharedPreferences pref;
    String Userid;
    String Userpass;

    Button favorite_finish;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "yoon.mp3");
        ViewGroup root = (ViewGroup)findViewById(android.R.id.content);
        CommWriteActivity.SetViewGroupFont(root, typeface);

        Toolbar toolbar = (Toolbar) findViewById(R.id.favorite_actionbar);
        setSupportActionBar(toolbar);


        init();
        //화면회전 금지
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        //상태바 색깔
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
        }


        recyclerView_fav.setHasFixedSize(true);

        mLayoutManager_fav = new LinearLayoutManager(this);
        mLayoutManager_fav.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView_fav.setLayoutManager(mLayoutManager_fav);


//        //뒤로가기
//        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);

        getUserFromSharedPreference();
        getFacilityItemFromServer();
    }

    public static void SetViewGroupFont(ViewGroup root, Typeface mTypeface)
    {
        for(int i=0; i<root.getChildCount(); i++){
            View child = root.getChildAt(i);
            if(child instanceof TextView){
                ((TextView)child).setTypeface(mTypeface);
            }
            else if(child instanceof ViewGroup)SetViewGroupFont((ViewGroup)child,mTypeface);

        }

    }

    private void init() {
        recyclerView_fav = (RecyclerView) findViewById(R.id.recycler_view_fav);
        favorite_finish = (Button) findViewById(R.id.finish_favorite);
        favorite_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getUserFromSharedPreference() {
        pref = getSharedPreferences("login", 0);
        Userid = pref.getString("id", "null");              //SharedPreferences에서 아이디 가져옴
        Userpass = pref.getString("pass", "null");          //SharedPreferences에서 비밀번호 가져옴
        System.out.println("@@@@@@ Userid:" + Userid);

    }

    //시설정보받기
    private void getFacilityItemFromServer() {
        Call<ArrayList<FacilityInfo>> getFacilList = awsNetworkService.getFavorite(Userid);
        getFacilList.enqueue(new Callback<ArrayList<FacilityInfo>>() {
            @Override
            public void onResponse(Response<ArrayList<FacilityInfo>> response, Retrofit retrofit) {
                System.out.println("@@@@@@ getFavorite  called");
                System.out.println("@@@@@@ response.body() : " + response.body());

                if (response.code() == 200) {
                    facilityDatas = response.body();
                    System.out.println("@@@@@@@@@facilityDatas : " + facilityDatas);

                    mAdapter_fav = new FacilityAdapter(facilityDatas, getApplicationContext(), Userid);
                    recyclerView_fav.setAdapter(mAdapter_fav);
                    mAdapter_fav.notifyDataSetChanged();
                } else if (response.code() == 500) {
                    int statusCode = response.code();
                    Log.i("MyTag", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
//                System.out.println("@@@@@@ getCommItemFromServer onFailure called" + t.getMessage());
//                Toast.makeText(getApplicationContext(), "Failed to load thumbnails", Toast.LENGTH_LONG).show();
                Log.i("MyTag", "에러내용 : " + t.getMessage());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("@@@@@@@@@@@Favorite Activity 새로고침");
        getFacilityItemFromServer();
    }
}