package com.sopt.bodeum.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.sopt.bodeum.Component.Constants;
import com.sopt.bodeum.Component.GlobalApplication;
import com.sopt.bodeum.Model.FacilityInfo;
import com.sopt.bodeum.Network.AwsNetworkService;
import com.sopt.bodeum.Network.GoogleMapNetworkService;
import com.sopt.bodeum.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class FaciliyInfo_Activity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView txtFacilName, txtFacilType, txtFacilAddr, txtFacilCnt1,
            txtFacilCnt2, txtFacilReviewCnt, txtFacilRewviewTop, txtScore;
    private ImageButton btnPopreview; // 더보기 부분
    private ImageButton btnCall, btnWriteReview, favoriteBtn; // 전화연결/후기쓰기/찜하기 버튼
    private ImageView ivFacility; //시설이미지
    private ImageView ivGrade;  // 등급 이미지
    private ImageView evalue1, evalue2, evalue3, evalue4, evalue5, evalue6; // 공단평가 이미지
    private ImageView ivFacilStar1, ivFacilStar2, ivFacilStar3, ivFacilStar4, ivFacilStar5; // 별점
    private Button finish_facility;

    private AwsNetworkService awsNetworkService;
    private FacilityInfo facilityInfo = new FacilityInfo();

    private SharedPreferences pref;
    private String Userid;
    private String Userpass;

    private GoogleMap mMap;
    private GoogleMapNetworkService googleMapNetworkService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faciliy_info_);

//        Typeface typeface = Typeface.createFromAsset(getAssets(), "yoon.mp3");
//        ViewGroup root = (ViewGroup)findViewById(android.R.id.content);
//        CommWriteActivity.SetViewGroupFont(root, typeface);

        Toolbar toolbar = (Toolbar) findViewById(R.id.customtoorbar2);
        setSupportActionBar(toolbar);

        //상태바 색깔
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
        }
        //화면회전 금지
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.facilityMap);
        mapFragment.getMapAsync(this);

        initWidget();
        initSharedPreference();
        initNetworkService();

//        InitFont();

        getFacilInfoFromServer(); //AWS에서 데이터 가져옴
        initEventListener();
    }

    private void initEventListener() {
        //****************************************************************************************
        // 버튼리스너
        // ***************************************************************************************/

        // 뒤로가기 버튼 클릭 시 액티비티 종료
        finish_facility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 전화하기 클릭
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + facilityInfo.Facility_call));
                startActivity(intent);
            }
        });

        // 후기쓰기 클릭
        btnWriteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Writereview_Activity.class);
                System.out.println("@@@@@@인텐트 facilityIds : " + facilityInfo.Facility_id);

                intent.putExtra("Facility_ids", facilityInfo.Facility_id);
                startActivity(intent);
            }
        });

        // 더 읽어보기 클릭메서드
        btnPopreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"눌림",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ReviewPop_Activity.class);
                intent.putExtra("Facility_ids", facilityInfo.Facility_id);
                System.out.println("@@@@@@@@@Facility를 넘기자 : " + facilityInfo.Facility_id);

                String test = String.valueOf(facilityInfo.Facility_score);
                // 후기개수 ReviewPop 액티비티로 넘겨주기
                intent.putExtra("reviewnum", facilityInfo.Facility_reviewnum);
                intent.putExtra("reviewScore", test);
                System.out.println("@@@@@@@@@@INT11 " + facilityInfo.Facility_score);
                startActivity(intent);
            }
        });

        //찜하기 버튼
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("@@@@@@@@facilityId : " + facilityInfo.Facility_id);

                //서버연동
                HashMap<String, String> param = new HashMap<>();
                param.put("Member", Userid);
                param.put("Facility", facilityInfo.Facility_id);

                Call<Object> addFavorite = awsNetworkService.addFavorite(param);
                addFavorite.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Response<Object> response, Retrofit retrofit) {
                        int statusCode = response.code();

                        System.out.println("@@@@@@ commWriteReply called");
                        Log.i("@@@@@@MyTag", "응답코드 : " + statusCode);
                        System.out.println("@@@@@@ response.body() : " + response.body());

                        if (facilityInfo.Favorite_facil == null) {
                            Toast.makeText(getApplicationContext(), "찜하기", Toast.LENGTH_SHORT).show();
                            favoriteBtn.setImageResource(R.mipmap.card_heart_full);
                            facilityInfo.Favorite_facil = "false";
                        } else {
                            Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_SHORT).show();
                            favoriteBtn.setImageResource(R.mipmap.card_heart);
                            facilityInfo.Favorite_facil = null;
                        }

                        if (response.code() == 200) {
                            //Toast.makeText(context, "찜하기 완료", Toast.LENGTH_LONG).show();

                        } else if (response.code() == 503) {
                            Log.i("MyTag", "응답코드 : " + statusCode);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(getApplicationContext(), "Failed to 찜하기", Toast.LENGTH_LONG).show();
                        Log.i("MyTag ", "에러내용 : " + t.getMessage());
                    }
                });

            }
        });
        //*******************************************************************************************
    }

    public static void SetViewGroupFont(ViewGroup root, Typeface mTypeface) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTypeface(mTypeface);
            } else if (child instanceof ViewGroup) SetViewGroupFont((ViewGroup) child, mTypeface);

        }

    }

    public void InitFont() {
        Typeface nanumBarunpen = Typeface.createFromAsset(getAssets(), "NanumBarunpenB.mp3");
        Constants.InitFont(nanumBarunpen, (ViewGroup) findViewById(android.R.id.content));
    }

    private void getFacilInfoFromServer() {

        Intent intent = getIntent();
        String Facility_id = intent.getStringExtra("Facility_id");
        Call<Object> getFacilInfo;
        getFacilInfo = awsNetworkService.getFacilityInfo(Facility_id, Userid);

        getFacilInfo.enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Response<Object> response, Retrofit retrofit) {
                int statusCode = response.code();

//                System.out.println("@@@@@@ getFacilInfoFromServer onResponse called");
//                Log.i("@@@@@@MyTag", "응답코드 : " + statusCode);
//                System.out.println("@@@@@@ response.body() : " + response.body());

                if (response.isSuccess()) {
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(response.body());
//                    System.out.println("@@@@@@ facilityInfo : " + jsonString);

                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);

                        //facility 정보
                        JSONArray jsonArray = jsonObject.getJSONArray("facil");
//                        System.out.println("@@@@@@ jsonArray : " + jsonArray);

                        JSONArray jsonArray2 = jsonArray.getJSONArray(0);
//                        System.out.println("@@@@@@ jsonArray2 : " + jsonArray2);
                        JSONObject jsonObject1 = jsonArray2.getJSONObject(0);
//                        System.out.println("@@@@@@ jsonObject1 : " + jsonObject1);

                        facilityInfo = gson.fromJson(jsonObject1.toString(), FacilityInfo.class);

                        //후기가 없을 수도 있으므로 예외처리!
                        try {
                            //후기정보
                            JSONArray jsonArrayReview = jsonObject.getJSONArray("review");
//                        System.out.println("@@@@@@ jsonArrayReview : " + jsonArrayReview);

                            JSONArray jsonArrayReview2 = jsonArrayReview.getJSONArray(0);
//                        System.out.println("@@@@@@ jsonArrayReview2 : " + jsonArrayReview2);

                            JSONObject jsonObjectReview = jsonArrayReview2.getJSONObject(0);
//                            System.out.println("@@@@@@ jsonObjectReview : " + jsonObjectReview);

                            String review_title = jsonObjectReview.getString("Review_content");

                            //String facil_score = jsonObjectReview.getString("Facility_score");


                            //후기는 따로 넣어줌
                            facilityInfo.Review_content = review_title;
//                            System.out.println("@@@@@@@@@@@@@@@Review_content : " + facilityInfo.Review_content);
//                            facilityInfo.Facility_score = facil_score;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //받아온 정보 뿌려주기
                        dispFacilityDatas();
                        getLocationByAddress(facilityInfo.Facility_addr); //구글 지오코드 API로 주소를 좌표로 전환
                    }


                } else if (response.code() == 500) {
                    Log.i("MyTag", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("@@@@@@ getFacilInfoFromServer onFailure called" + t.getMessage());
//                Toast.makeText(getApplicationContext(), "Failed to load thumbnails", Toast.LENGTH_LONG).show();
                Log.i("MyTag", "에러내용 : " + t.getMessage());
            }
        });

    }

    private void dispFacilityDatas() {
        txtFacilName.setText(facilityInfo.Facility_name);
        txtFacilType.setText(facilityInfo.Facility_kind);
        txtFacilAddr.setText(facilityInfo.Facility_addr);
        txtFacilCnt1.setText(facilityInfo.Facility_count);
        txtFacilCnt2.setText(facilityInfo.Facility_ecount);
        txtFacilReviewCnt.setText(facilityInfo.Facility_reviewnum);
        txtFacilRewviewTop.setText(facilityInfo.Review_content); // 밑줄

        // 시설 좋아요 하트 이미지 세팅
        if (facilityInfo.Favorite_facil == null) {
            favoriteBtn.setImageResource(R.mipmap.card_heart);
        } else {
            favoriteBtn.setImageResource(R.mipmap.card_heart_full);
        }

        //Glide api로 이미지로딩
        String url = awsNetworkService.baseUrl + "images/" + facilityInfo.Facility_photo;
        Glide.with(this)
                .load(url)
                .into(ivFacility);

        //시설등급
        if ("A".equals(facilityInfo.Facility_avg)) {
            ivGrade.setImageResource(R.mipmap.card_rank_a);
        } else if ("B".equals(facilityInfo.Facility_avg)) {
            ivGrade.setImageResource(R.mipmap.card_rank_b);
        } else if ("C".equals(facilityInfo.Facility_avg)) {
            ivGrade.setImageResource(R.mipmap.card_rank_c);
        } else if ("D".equals(facilityInfo.Facility_avg)) {
            ivGrade.setImageResource(R.mipmap.card_rank_d);
        }

        try {
            txtScore.setText(String.valueOf(facilityInfo.Facility_score).substring(0, 3));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (facilityInfo.Facility_score == 0) {
            ivFacilStar1.setImageResource(R.mipmap.star_ever);
            ivFacilStar2.setImageResource(R.mipmap.star_ever);
            ivFacilStar3.setImageResource(R.mipmap.star_ever);
            ivFacilStar4.setImageResource(R.mipmap.star_ever);
            ivFacilStar5.setImageResource(R.mipmap.star_ever);
        } else if (facilityInfo.Facility_score == 1) {
            ivFacilStar1.setImageResource(R.mipmap.star_ever_full);
            ivFacilStar2.setImageResource(R.mipmap.star_ever);
            ivFacilStar3.setImageResource(R.mipmap.star_ever);
            ivFacilStar4.setImageResource(R.mipmap.star_ever);
            ivFacilStar5.setImageResource(R.mipmap.star_ever);

        } else if (facilityInfo.Facility_score == 2) {
            ivFacilStar1.setImageResource(R.mipmap.star_ever_full);
            ivFacilStar2.setImageResource(R.mipmap.star_ever_full);
            ivFacilStar3.setImageResource(R.mipmap.star_ever);
            ivFacilStar4.setImageResource(R.mipmap.star_ever);
            ivFacilStar5.setImageResource(R.mipmap.star_ever);
        } else if (facilityInfo.Facility_score == 3) {
            ivFacilStar1.setImageResource(R.mipmap.star_ever_full);
            ivFacilStar2.setImageResource(R.mipmap.star_ever_full);
            ivFacilStar3.setImageResource(R.mipmap.star_ever_full);
            ivFacilStar4.setImageResource(R.mipmap.star_ever);
            ivFacilStar5.setImageResource(R.mipmap.star_ever);
        } else if (facilityInfo.Facility_score == 4) {
            ivFacilStar1.setImageResource(R.mipmap.star_ever_full);
            ivFacilStar2.setImageResource(R.mipmap.star_ever_full);
            ivFacilStar3.setImageResource(R.mipmap.star_ever_full);
            ivFacilStar4.setImageResource(R.mipmap.star_ever_full);
            ivFacilStar5.setImageResource(R.mipmap.star_ever);
        } else if (facilityInfo.Facility_score == 5) {
            ivFacilStar1.setImageResource(R.mipmap.star_ever_full);
            ivFacilStar2.setImageResource(R.mipmap.star_ever_full);
            ivFacilStar3.setImageResource(R.mipmap.star_ever_full);
            ivFacilStar4.setImageResource(R.mipmap.star_ever_full);
            ivFacilStar5.setImageResource(R.mipmap.star_ever_full);
        }

        //공단평가
        ImageView[] imageViews = {evalue1, evalue2, evalue3, evalue4, evalue5, evalue6};
        String[] eval = {facilityInfo.Facility_env, facilityInfo.Facility_mang,
                facilityInfo.Facility_sour, facilityInfo.Facility_pro,
                facilityInfo.Facility_chil, facilityInfo.Facility_aut};

        for (int i = 0; i < 6; i++) {
            if ("A".equals(eval[i])) {
                imageViews[i].setImageResource(R.mipmap.inform_rank_a);
            } else if ("B".equals(eval[i])) {
                imageViews[i].setImageResource(R.mipmap.inform_rank_b);
            } else if ("C".equals(eval[i])) {
                imageViews[i].setImageResource(R.mipmap.inform_rank_c);
            } else if ("D".equals(eval[i])) {
                imageViews[i].setImageResource(R.mipmap.inform_rank_d);
            }
        }


    }

    //**********************************************************************************************
//* 초기화
// ********************************************************************************************/
    private void initWidget() {
        txtFacilRewviewTop = (TextView) findViewById(R.id.txtFacilRewviewTop);
        btnCall = (ImageButton) findViewById(R.id.btnCall); // 전화연결 버튼
        btnWriteReview = (ImageButton) findViewById(R.id.btnWriteReview);// 후기쓰기 버튼
        favoriteBtn = (ImageButton) findViewById(R.id.favoriteBtn); // 찜하기
        ivFacility = (ImageView) findViewById(R.id.ivFacility);
        ivGrade = (ImageView) findViewById(R.id.ivGrade); // 등급 이미지
        evalue1 = (ImageView) findViewById(R.id.evalue1); //시설환경
        evalue2 = (ImageView) findViewById(R.id.evalue2); //제정조직운영
        evalue3 = (ImageView) findViewById(R.id.evalue3); //인적자원관리
        evalue4 = (ImageView) findViewById(R.id.evalue4); //서비스프로그램
        evalue5 = (ImageView) findViewById(R.id.evalue5); //아동의 권리
        evalue6 = (ImageView) findViewById(R.id.evalue6); //지역사회 관계
        btnPopreview = (ImageButton) findViewById(R.id.btnPopreview); // 더 보기
        txtFacilName = (TextView) findViewById(R.id.txtFacilName);
        txtFacilType = (TextView) findViewById(R.id.txtFacilType);
        txtFacilAddr = (TextView) findViewById(R.id.txtFacilAddr);
        txtFacilCnt1 = (TextView) findViewById(R.id.txtFacilCnt1);
        txtFacilCnt2 = (TextView) findViewById(R.id.txtFacilCnt2);
        txtFacilReviewCnt = (TextView) findViewById(R.id.txtFacilReviewCnt);
        txtScore = (TextView) findViewById(R.id.txtScore);
        ivFacilStar1 = (ImageView) findViewById(R.id.ivFacilStar1);
        ivFacilStar2 = (ImageView) findViewById(R.id.ivFacilStar2);
        ivFacilStar3 = (ImageView) findViewById(R.id.ivFacilStar3);
        ivFacilStar4 = (ImageView) findViewById(R.id.ivFacilStar4);
        ivFacilStar5 = (ImageView) findViewById(R.id.ivFacilStar5);
        finish_facility = (Button) findViewById(R.id.finish_facility);

    }

    private void initSharedPreference() {
        pref = getSharedPreferences("login", 0);
        Userid = pref.getString("id", "null");              //SharedPreferences에서 아이디 가져옴
        Userpass = pref.getString("pass", "null");          //SharedPreferences에서 비밀번호 가져옴
    }

    private void initNetworkService() {
        awsNetworkService = GlobalApplication.getGlobalApplicationContext().getAwsNetwork();
        googleMapNetworkService = GlobalApplication.getGlobalApplicationContext().getGoogleMapNetwork();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void getLocationByAddress(String facility_addr) {
        System.out.println("@@@@@@@@getLocation called");
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("address", facility_addr);
        Call<Object> dataCall = googleMapNetworkService.getLocation(parameters);
        dataCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Response<Object> response, Retrofit retrofit) {
                int statusCode = response.code();
                if (response.isSuccess()) {
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(response.body());

                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonString);

                        JSONArray jsonArray1 = jsonObject1.getJSONArray("results");
                        JSONObject jsonObject2 = jsonArray1.getJSONObject(0);
                        JSONObject jsonObject3 = jsonObject2.getJSONObject("geometry");
                        JSONObject jsonObject4 = jsonObject3.getJSONObject("location");
                        String lat = jsonObject4.getString("lat");
                        String lng = jsonObject4.getString("lng");
//                        System.out.println("@@@@@@@@@jsonObject1->" + jsonObject1);
//                        System.out.println("@@@@@@@@@jsonArray1->" + jsonArray1);
//                        System.out.println("@@@@@@@@@jsonObject2->" + jsonObject2);
//                        System.out.println("@@@@@@@@@jsonObject3->" + jsonObject3);
//                        System.out.println("@@@@@@@@@lat->" + lat);
//                        System.out.println("@@@@@@@@@lon->" + lng);

                        //지도에 띄우기
                        showPlaceOnMap(lat, lng);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("@@@@@@@@@실패 에러코드 : " + statusCode);

                }
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("@@@@@@@@@onFailure");

            }
        });
    }

    private void showPlaceOnMap(String lat, String lng) {
        LatLng facilityLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        try {
            mMap.addMarker(new MarkerOptions().position(facilityLocation).title(facilityInfo.Facility_name));
            //마커로 이동하고 줌레벨 조정
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(facilityLocation, 16);
            mMap.moveCamera(cameraUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //새로고침
    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
//        System.out.println("@@@@@@@@@@@FacilityInfo 새로고침");
        //Refresh your stuff here
        getFacilInfoFromServer();
    }
}
