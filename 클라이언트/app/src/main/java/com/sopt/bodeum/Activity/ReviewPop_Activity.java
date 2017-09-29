package com.sopt.bodeum.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sopt.bodeum.Adapter.ReviewAdapter;
import com.sopt.bodeum.Component.GlobalApplication;
import com.sopt.bodeum.Model.ReviewItem;
import com.sopt.bodeum.Network.AwsNetworkService;
import com.sopt.bodeum.R;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ReviewPop_Activity extends AppCompatActivity {

    ListView reviewList;
    ImageView ivTotalScore;
    ArrayList<ReviewItem> reviewDatas;
    ReviewAdapter reviewAdapter;
    Button btnFinish;
    TextView txtScore;
    String Facility_id;
    String Score;

    AwsNetworkService awsNetworkService = GlobalApplication.getGlobalApplicationContext().getAwsNetwork();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_pop_);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "yoon.mp3");
        ViewGroup root = (ViewGroup)findViewById(android.R.id.content);
        login_Activity.SetViewGroupFont(root, typeface);

//화면회전 금지
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

//        txtReviewCnt = (TextView)findViewById(R.id.txtReviewCnt);
        ivTotalScore = (ImageView)findViewById(R.id.ivTotalScore);
        reviewList = (ListView) findViewById(R.id.reviewList);
        txtScore = (TextView) findViewById(R.id.txtScore);

        reviewAdapter = new ReviewAdapter(reviewDatas, getApplicationContext());
        reviewList.setAdapter(reviewAdapter);

        getValueFromIntent();
        getReviewFrowServer();

        btnFinish = (Button) findViewById(R.id.btnFinish);

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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


    private void getValueFromIntent() {
        // 후기개수랑 총점점수 가져오기

        //todo 후기 팝업창에서 후기 개수 보여야됨
            //intent사용
        try {
            Intent intent = getIntent();
            Score = intent.getStringExtra("reviewScore");
            Facility_id = intent.getStringExtra("Facility_ids");
            txtScore.setText(Score);
            System.out.println("@@@@@@@@@@facility_id"+Facility_id);
            System.out.println("@@@@@@@@@@INT" + Score);
            String totalScore = Score.substring(0,1);

            if (totalScore.equals("0")) {
                ivTotalScore.setImageResource(R.mipmap.star_ever_zero);
            } else if (totalScore.equals("1")) {
                ivTotalScore.setImageResource(R.mipmap.star_ever_one);
            } else if (totalScore.equals("2")) {
                ivTotalScore.setImageResource(R.mipmap.star_ever_two);
            } else if (totalScore.equals("3")) {
                ivTotalScore.setImageResource(R.mipmap.star_ever_three);
            } else if (totalScore.equals("4")) {
                ivTotalScore.setImageResource(R.mipmap.star_ever_four);
            } else if (totalScore.equals("5")) {
                ivTotalScore.setImageResource(R.mipmap.star_ever_five);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getReviewFrowServer() {
        reviewDatas = new ArrayList<ReviewItem>();

        Call<ArrayList<ReviewItem>> getReview = awsNetworkService.getReviewList(Facility_id);
        getReview.enqueue(new Callback<ArrayList<ReviewItem>>() {
            @Override
            public void onResponse(Response<ArrayList<ReviewItem>> response, Retrofit retrofit) {
                int statusCode = response.code();

                System.out.println("@@@@@@ getReviewList called");
                Log.i("@@@@@@MyTag", "응답코드 : " + statusCode);
                System.out.println("@@@@@@ response.body() : " +response.body());

                if (response.code() == 200) {
//                    Toast.makeText(getApplicationContext(), "review list 완료", Toast.LENGTH_LONG).show();
                    reviewDatas = response.body();
                    System.out.println("@@@@@@ reviewDatas  : " +reviewDatas.get(0).Review_score);

                    reviewAdapter.setSource(reviewDatas);

                } else if (response.code() == 503) {
                    Log.i("MyTag", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to review list", Toast.LENGTH_LONG).show();
                Log.i("MyTag ", "에러내용 : " + t.getMessage());
            }
        });
    }
}
