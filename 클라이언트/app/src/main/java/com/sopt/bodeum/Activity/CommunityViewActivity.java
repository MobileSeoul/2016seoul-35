package com.sopt.bodeum.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sopt.bodeum.Adapter.CommunityViewAdapter;
import com.sopt.bodeum.Component.GlobalApplication;
import com.sopt.bodeum.Model.CommItem;
import com.sopt.bodeum.Model.ReplyItem;
import com.sopt.bodeum.Network.AwsNetworkService;
import com.sopt.bodeum.R;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class CommunityViewActivity extends AppCompatActivity {

    ScrollView scrollViewCommView;
    Button communityview_finish;
    TextView txtMainName;
    TextView txtMainDate;
    TextView txtMainTitle;
    TextView txtMainContent;

    //ImageView ivCommMainContent;

    Button btnReply;
    ImageButton btnWrite;

    EditText editWrite;

    ListView lvReply; //덧글리스트

    ArrayList<ReplyItem> commReplyItems;

    CommItem commItem;

    CommunityViewAdapter communityViewAdapter;

    AwsNetworkService awsNetworkService = GlobalApplication.getGlobalApplicationContext().getAwsNetwork();

    //덧글을 쓸때 함께 넘겨줘야하는 변수
    String comm_id, UserNick, Nickname;

    SharedPreferences pref;
    String Userid;
    String Userpass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_view);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "yoon.mp3");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        login_Activity.SetViewGroupFont(root, typeface);

        initView();
        getUserFromSharedPreference();
        getFromIntent();
        initData();
        makeCommViewAdapter();
        btnClickEvent();
        SharedPreferences preferences = getSharedPreferences("memberInfo",0);
        Nickname = preferences.getString("nick",null);


        //상태바 색깔
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#bd81b4e5"));
        }

        //화면회전 금지
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }

    public static void SetViewGroupFont(ViewGroup root, Typeface mTypeface) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTypeface(mTypeface);
            } else if (child instanceof ViewGroup) SetViewGroupFont((ViewGroup) child, mTypeface);
        }
    }

    private void getFromIntent() {
        Intent intent = getIntent();
        comm_id = intent.getStringExtra("comm_id");
        UserNick = intent.getStringExtra("nick");
//        System.out.println("@@@@@@ comm_id:" + comm_id);
    }

    private void getUserFromSharedPreference() {
        pref = getSharedPreferences("login", 0);
        Userid = pref.getString("id", "null");              //SharedPreferences에서 아이디 가져옴
        Userpass = pref.getString("pass", "null");          //SharedPreferences에서 비밀번호 가져옴
        System.out.println("@@@@@@ Userid:" + Userid);

    }

    private void btnClickEvent() {
        //글쓰기 버튼 클릭시 에디트텍스트 내용 저장
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("".equals(editWrite.getText().toString())) { //아무거도 입력 안하면
                    Toast.makeText(getApplicationContext(), "댓글을 입력해 주세요", Toast.LENGTH_LONG).show();
                } else {
                    ReplyItem replyItem = new ReplyItem(
                            editWrite.getText().toString(),
                            comm_id,
                            Userid, UserNick);


                    commReplyItems.add(replyItem);
                    communityViewAdapter.setSource(commReplyItems);

                    editWrite.setText(""); //입력창 비우기
                    setListViewHeightBasedOnChildren(lvReply); //리스트의 개수에 따라서 리스트뷰의 크기를 변화시킴


                    // 서버연동 댓글보내기
                    Call<Object> addReplyToServer = awsNetworkService.commWriteReply(replyItem);
                    addReplyToServer.enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Response<Object> response, Retrofit retrofit) {
                            int statusCode = response.code();

                            System.out.println("@@@@@@ commWriteReply called");
                            Log.i("@@@@@@MyTag", "응답코드 : " + statusCode);

                            if (response.code() == 200) {
                                //Toast.makeText(getApplicationContext(), "댓글작성 완료", Toast.LENGTH_LONG).show();
                                scrollViewCommView.fullScroll(View.FOCUS_DOWN);

                            } else if (response.code() == 503) {
                                Log.i("MyTag", "응답코드 : " + statusCode);
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Toast.makeText(getApplicationContext(), "Failed to comm reply write", Toast.LENGTH_LONG).show();
                            Log.i("MyTag ", "에러내용 : " + t.getMessage());
                        }
                    });
                }
            }
        });

        communityview_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition

            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void makeCommViewAdapter() {
        communityViewAdapter = new CommunityViewAdapter(commReplyItems, getApplicationContext());
        lvReply.setAdapter(communityViewAdapter);
        lvReply.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(), "아이템클릭.", Toast.LENGTH_LONG).show();

            }
        });
    }

    //리스트뷰에 스크롤바 안생기게 하는 함수
    public void justifyListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
        System.out.println("@@@@@@@@@@@adapter.getCount() = " + adapter.getCount());
    }

    private void initData() {

        commItem = new CommItem();
        commReplyItems = new ArrayList<ReplyItem>();

        // 서버연동 글내용 빼오기
        Call<CommItem> getCommContents = awsNetworkService.getCommContents(comm_id, Userid);
        getCommContents.enqueue(new Callback<CommItem>() {
            @Override
            public void onResponse(Response<CommItem> response, Retrofit retrofit) {
                int statusCode = response.code();

                Log.i("@@@@@@MyTag", "응답코드 : " + statusCode);
                System.out.println("@@@@@@ getCommContents called");

                System.out.println("@@@@@@ response.body() : " + response.body());

                if (response.code() == 200) {
//                    Toast.makeText(getApplicationContext(), "글 내용 로딩 완료", Toast.LENGTH_LONG).show();
                    commItem = response.body();

                    txtMainName.setText(commItem.getMember_nick());
                    try {
                        String time = commItem.getComm_time().substring(0, 10);
                        txtMainDate.setText(time);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    txtMainTitle.setText(commItem.getComm_title());
                    txtMainContent.setText(commItem.getComm_content());
                    //txtCommViewLikeNum.setText(commItem.getComm_like());

                    //Glide api로 이미지로딩
                    String url = awsNetworkService.baseUrl + "images/" + commItem.Comm_photo;
                    Glide.with(getApplicationContext())
                            .load(url);
                            //.into(ivCommMainContent);

                } else if (response.code() == 503) {
                    Log.i("MyTag", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to comm reply write", Toast.LENGTH_LONG).show();
                Log.i("MyTag ", "에러내용 : " + t.getMessage());
            }
        });


        // 서버연동 리플 리스트
        Call<ArrayList<ReplyItem>> getReply = awsNetworkService.getReply(comm_id);
        getReply.enqueue(new Callback<ArrayList<ReplyItem>>() {
            @Override
            public void onResponse(Response<ArrayList<ReplyItem>> response, Retrofit retrofit) {
                int statusCode = response.code();

                System.out.println("@@@@@@ commWriteReply called");
                Log.i("@@@@@@MyTag", "응답코드 : " + statusCode);
//                System.out.println("@@@@@@ comm_id : " + );

                if (response.code() == 200) {
//                    Toast.makeText(getApplicationContext(), "댓글 로딩 완료", Toast.LENGTH_LONG).show();
                    commReplyItems = response.body();
                    System.out.println("@@@@@@ commReplyItems : " + commReplyItems.size());

                    communityViewAdapter.setSource(commReplyItems);
                    justifyListViewHeightBasedOnChildren(lvReply);
                    lvReply.setFocusable(false);

                } else if (response.code() == 503) {
                    Log.i("MyTag", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to comm reply write", Toast.LENGTH_LONG).show();
                Log.i("MyTag ", "에러내용 : " + t.getMessage());
            }
        });

    }

    private void initView() {
        scrollViewCommView = (ScrollView) findViewById(R.id.scrollViewCommView);
        communityview_finish = (Button) findViewById(R.id.communityview_finish);
        txtMainName = (TextView) findViewById(R.id.txtCommViewName);
        txtMainDate = (TextView) findViewById(R.id.txtCommViewDate);
        txtMainTitle = (TextView) findViewById(R.id.txtCommViewTitle);
        txtMainContent = (TextView) findViewById(R.id.txtCommViewContent);
        //ivCommMainContent = (ImageView) findViewById(ivCommMainContent);
        btnWrite = (ImageButton) findViewById(R.id.btnCommViewWrite);
        editWrite = (EditText) findViewById(R.id.editCommViewWrite);
        lvReply = (ListView) findViewById(R.id.lvCommViewReply); //덧글리스트
    }
}
