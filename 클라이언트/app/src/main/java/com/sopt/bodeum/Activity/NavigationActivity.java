package com.sopt.bodeum.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sopt.bodeum.Adapter.CommunityAdapter;
import com.sopt.bodeum.Adapter.FacilityAdapter;
import com.sopt.bodeum.Component.BackPressCloseSystem;
import com.sopt.bodeum.Component.GlobalApplication;
import com.sopt.bodeum.Model.CommItem;
import com.sopt.bodeum.Model.FacilityInfo;
import com.sopt.bodeum.Network.AwsNetworkService;
import com.sopt.bodeum.R;

import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    AwsNetworkService awsNetworkService;
    private BackPressCloseSystem backPressCloseSystem;

    public static LinearLayout popupTabs;
    ImageButton favoriteBtn1; // 찜하기

    //---------------------------------------------------------------------------------------------
    RecyclerView recyclerView;
    RecyclerView.Adapter facilityAdapter;
    LinearLayoutManager mLayoutManager;

    //Spinner s1;
    Spinner s2;

    //---------------------------------------------------------------------------------------------
    TabHost tabHost;
    //jingyu----------------------------------------------------------------------------------------
    SharedPreferences pref;
    String Userid;
    String Userpass;
    String Usernic;
    String Userprofile;

    Button btnCommSearch;
    EditText editCommSearch;
    ListView lvCommnity;
    ArrayList<CommItem> commDatas; //받아올 데이터 저장
    ArrayList<FacilityInfo> facilityDatas;
    Button btnFacilSeartch; //시군 시설검색
    String city, dong;

    TextView nav_nic; //// TODO: 16. 7. 7. 인텐트롤 닉네임 프로필사진 받기
    TextView nav_email;
    ImageView nav_profile;
    TextView txtReplyCnt;

    CommunityAdapter communityAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    //----------------------------------------------------------------------------------------------
    String favoriteBL1 = "true";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);


        nav_nic = (TextView) view.findViewById(R.id.nav_nic);
        nav_email = (TextView) view.findViewById(R.id.nav_email);
        nav_profile = (ImageView) view.findViewById(R.id.nav_profile);


        getUserFromSharedPreference();
        //네비게이션 드로어 아이콘 색
        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        nav_view.setItemIconTintList(null);
        initNetworkService();
        //화면회전 금지
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        //상태바 색깔
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#bd81b4e5"));
        }
        popupTabs = (LinearLayout) findViewById(R.id.popupTabs); //탭 레이아웃으로 감싸주기
        popupTabs.setVisibility(ViewGroup.VISIBLE); //처음 액티비티 실행 시 탭 보이게 하는 코드
        backPressCloseSystem = new BackPressCloseSystem(this); // 뒤로 가기 버튼 이벤트

        //----------------------------------------------------------------찜하기
        favoriteBtn1 = (ImageButton) findViewById(R.id.favoriteBtn1);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //각 item의 크기가 일정할 경우 고정
        recyclerView.setHasFixedSize(true);


        // 초기화
        // layoutManager 설정
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        commDatas = new ArrayList<CommItem>(); //서버에서받아오는 커뮤니티리스트
        facilityDatas = new ArrayList<FacilityInfo>(); //서버에서 받아오는 시설 총 정보리스트


        /******************************************************************************************
         커뮤니티
         *****************************************************************************************/
        //jingyu

        editCommSearch = (EditText) findViewById(R.id.editCommSearch);
        btnCommSearch = (Button) findViewById(R.id.btnCommSearch);
        lvCommnity = (ListView) findViewById(R.id.lvCommunity);
        btnFacilSeartch = (Button) findViewById(R.id.btnFacilSearch);


        initButton();
        makeCommAdapter();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabCommWrite);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CommWriteActivity.class);
                intent.putExtra("Userid", Userid);

                startActivity(intent);
            }
        });

        editCommSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editCommSearch.getWindowToken(), 0);
            }
        });

        //"시설"탭 툴바 숨기기
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int mLastFirstVisibleItem = 0;


            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final int currentFirstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (currentFirstVisibleItem > this.mLastFirstVisibleItem) {
                    // NavigationActivity.this.getSupportActionBar().hide();
                    hideTabs();
                } else if (currentFirstVisibleItem < this.mLastFirstVisibleItem) {
                    // NavigationActivity.this.getSupportActionBar().show();
                    showTabs();
                }

                this.mLastFirstVisibleItem = currentFirstVisibleItem;
            }
        });
        ////"커뮤니티"탭 툴바 숨기기
        lvCommnity.setOnScrollListener(new AbsListView.OnScrollListener() {
            int mLastFirstVisibleItem = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editCommSearch.getWindowToken(), 0);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view.getId() == lvCommnity.getId()) {
                    final int currentFirstVisibleItem = lvCommnity.getFirstVisiblePosition();

                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        // getSherlockActivity().getSupportActionBar().hide();
                        // getSupportActionBar().hide();
                        hideTabs();
                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                        // getSherlockActivity().getSupportActionBar().show();
                        //getSupportActionBar().show();
                        showTabs();
                    }

                    mLastFirstVisibleItem = currentFirstVisibleItem;


                    mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
                    boolean enable = false;
                    if (lvCommnity != null && lvCommnity.getChildCount() > 0) {
                        // check if the first item of the list is visible
                        boolean firstItemVisible = lvCommnity.getFirstVisiblePosition() == 0;
                        // check if the top of the first item is visible
                        boolean topOfFirstItemVisible = lvCommnity.getChildAt(0).getTop() == 0;
                        // enabling or disabling the refresh layout
                        enable = firstItemVisible && topOfFirstItemVisible;
                    }
                    mSwipeRefreshLayout.setEnabled(enable);
                }

            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //handling swipe refresh
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        getCommItemFromServer("all");

                    }
                }, 1000);
            }
        });


        /******************************************************************************************
         *
         안드로이드 툴바 사용 부분
         *****************************************************************************************/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /******************************************************************************************
         탭 위젯 사용 부분
         *****************************************************************************************/
        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        // tabHost.setOnTabChangedListener((TabHost.OnTabChangeListener) this);

        TabHost.TabSpec spec = tabHost.newTabSpec("tag1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("", getResources().getDrawable(R.mipmap.tab_inform));
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("", getResources().getDrawable(R.mipmap.tab_community));
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                //커뮤니티 탭 클릭할 경우에
                if ("tag2".equals(tabId)) {
                    editCommSearch.setText("");
                    //서버에서 커뮤니티 정보 받기
                    getCommItemFromServer("all");
                } else {
                    //서버에서 시설 정보 받기
                    //s1.setSelection(0);
                    s2.setSelection(0);
                    getFacilityItemFromServer("all");
                }
            }
        });

        /******************************************************************************************
         *****************************************************************************************/


        /******************************************************************************************
         스피너 사용 부분
         *****************************************************************************************/
        //s1 = (Spinner) findViewById(R.id.spinner1);
        s2 = (Spinner) findViewById(R.id.spinner2);

        /*
        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                city = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        */

        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // Toast.makeText(getApplicationContext(),position,Toast.LENGTH_SHORT).show();
                dong = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        /******************************************************************************************
         *****************************************************************************************/


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);


        //--------------//

        getFacilityItemFromServer("all");


    }

    private void getUserFromSharedPreference() {

        pref = getSharedPreferences("login", 0);

        System.out.println("@@@@@@@@@닉넴 : " + pref.getString("nic", "null"));
        System.out.println("@@@@@@@@@프로필 : " + pref.getString("profile", "null"));

        System.out.println("@@@@@@@@@kakao : " + pref.getString("kakao", "null"));

        if ((pref.getString("kakao", "null").equals("yes"))) { //카카오 로그인일 때
            System.out.println("@@@@@@@@@@@@카톡으로 사용자 받기!!!");

            Userid = pref.getString("id", "null");              //SharedPreferences에서 아이디 가져옴
            Usernic = pref.getString("nic", "null");          //SharedPreferences에서 비밀번호 가져옴
            Userprofile = pref.getString("profile", "null");          //SharedPreferences에서 비밀번호 가져옴

            System.out.println("kakao_id = " + Userid);
            System.out.println("kakao_nic = " + Usernic);
            System.out.println("kakao_profile = " + Userprofile);

            nav_email.setText(Userid);
            nav_nic.setText(Usernic);
            Glide.with(this)
                    .load(Userprofile)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(nav_profile);

        } else {
//        아니면 인텐트로 받기
            try {
                Intent intent1 = getIntent();
                Userid = intent1.getStringExtra("email");
                Usernic = intent1.getStringExtra("nic");
                Userprofile = intent1.getStringExtra("profile");
                String nav_emailStr = intent1.getStringExtra("email");

                System.out.println("@@@@@@@@@@@@인텐트로 사용자 받기!!!");
                System.out.println("id = " + Userid);
                System.out.println("nic = " + Usernic);
                System.out.println("profile = " + Userprofile);

                nav_nic.setText(Usernic);
                nav_email.setText(Userid);

                //프로파일이 있으면 로딩, 없으면 디폴트로
                if (!TextUtils.isEmpty(Userprofile)) {
                    System.out.println("@@@@@@@@@@@@프로필 로딩!!!");
                    String url = AwsNetworkService.baseUrl + "images/" + Userprofile;
                    Glide.with(this)
                            .load(url)
                            .bitmapTransform(new CropCircleTransformation(this))
                            .into(nav_profile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void initButton() {
        //검색버튼
        btnCommSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCommItemFromServer("search");
            }
        });

        //시/군 별 시설검색
        btnFacilSeartch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFacilityItemFromServer("detail");
            }
        });
    }

    private void initNetworkService() {
        awsNetworkService = GlobalApplication.getGlobalApplicationContext().getAwsNetwork();
    }


    //시설정보받기
    private void getFacilityItemFromServer(String type) {
        Call<ArrayList<FacilityInfo>> getFacilList;
        if ("all".equals(type)) {
            getFacilList = awsNetworkService.getFacilityList(Userid);
        } else {
            HashMap<String, String> param = new HashMap<>();
            param.put("city", city);
            param.put("dong", dong);
            getFacilList = awsNetworkService.getFacilityList(param);
        }
        getFacilList.enqueue(new Callback<ArrayList<FacilityInfo>>() {
            @Override
            public void onResponse(Response<ArrayList<FacilityInfo>> response, Retrofit retrofit) {
                System.out.println("@@@@@@ getFacilityItemFromServer onResponse called");
                System.out.println("@@@@@@ response.body() : " + response.body());

                if (response.code() == 200) {
                    facilityDatas = response.body();
                    System.out.println("@@@@@@@@@facilityDatas : " + facilityDatas);

                    // 시군 으로 검색했을때 체인지
                    facilityAdapter = new FacilityAdapter(facilityDatas, getApplicationContext(), Userid);
                    recyclerView.setAdapter(facilityAdapter);
                    facilityAdapter.notifyDataSetChanged();
                } else if (response.code() == 500) {
                    int statusCode = response.code();
                    Log.i("MyTag", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("@@@@@@ getFacilityItemFromServer onFailure called" + t.getMessage());

//                Toast.makeText(getApplicationContext(), "Failed to load thumbnails", Toast.LENGTH_LONG).show();
                Log.i("MyTag", "에러내용 : " + t.getMessage());
            }
        });


    }

    //커뮤니티정보받기
    private void getCommItemFromServer(String type) {

        final Call<ArrayList<CommItem>> getCommList;

        if ("search".equals(type)) { //검색한 글만 불러오기
            System.out.println("@@@@@@ getCommItemFromServer Search called");

            String keyWord = editCommSearch.getText().toString();
            getCommList = awsNetworkService.getCommListBySearch(keyWord);
        } else { //모든 글 불러오기
            System.out.println("@@@@@@ getCommItemFromServer All called");

            getCommList = awsNetworkService.getCommList(Userid);
        }

        getCommList.enqueue(new Callback<ArrayList<CommItem>>() {

            @Override
            public void onResponse(Response<ArrayList<CommItem>> response, Retrofit retrofit) {
                System.out.println("@@@@@@ getCommItemFromServer onResponse called");
                System.out.println("@@@@@@ response.body() : " + response.body());

                if (response.code() == 200) {
                    commDatas = response.body();
                    System.out.println("@@@@@@@@@commDatas : " + commDatas.get(0).getComm_id());
                    communityAdapter.setSource(commDatas);


                } else if (response.code() == 500) {
                    int statusCode = response.code();
                    Log.i("MyTag", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("@@@@@@ getCommItemFromServer onFailure called" + t.getMessage());

//                Toast.makeText(getApplicationContext(), "Failed to load thumbnails", Toast.LENGTH_LONG).show();
                Log.i("MyTag", "에러내용 : " + t.getMessage());
            }
        });

    }

    //새로고침
    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
        getFacilityItemFromServer("all");
        getCommItemFromServer("all");

        //s1.setSelection(0);
        s2.setSelection(0);


        communityAdapter.setSource(commDatas);
        System.out.println("@@@@@@@@@@@@@@@ 새로고침!!!");
    }

    /******************************************************************************************
     * 뒤로가기 버튼 눌렸을 때 동작
     *****************************************************************************************/
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            backPressCloseSystem.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")


    /******************************************************************************************
     네비게이션 드로어 아이템 선택했을 때 페이지 이동 부분
     *****************************************************************************************/
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.favorite) {
            Intent intent1 = new Intent(getApplicationContext(), Favorite_Activity.class);
            startActivity(intent1);
        } else if (id == R.id.edit_profile) {
            Intent intent = new Intent(getApplicationContext(), ProfileEditActivity.class);
            startActivity(intent);
        } else if (id == R.id.logout) {
            logout();
            Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_LONG).show();
        } else if (id == R.id.leave) {

            ViewDialog alert = new ViewDialog();
            alert.showDialog(this);

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /******************************************************************************************
     *****************************************************************************************/


    //jingyu
    private void makeCommAdapter() {
        communityAdapter = new CommunityAdapter(commDatas, getApplicationContext());
        lvCommnity.setAdapter(communityAdapter);
        lvCommnity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Toast.makeText(getApplicationContext(), "아이템클릭.", Toast.LENGTH_LONG).show();
                System.out.println("완료로이동");
                Intent intent = new Intent(getApplicationContext(), CommunityViewActivity.class);
                intent.putExtra("comm_id", commDatas.get(position).getComm_id());
                intent.putExtra("nick",Usernic );
                startActivity(intent);

            }
        });
    }

    public static void showTabs() {
        popupTabs.setVisibility(ViewGroup.VISIBLE);
    }

    // Hide Tabs method
    public static void hideTabs() {
        popupTabs.setVisibility(ViewGroup.GONE);
    }

    public void logout() {
        Intent i = new Intent(getApplicationContext(), login_Activity.class);
        SharedPreferences logout = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = logout.edit();
        editor.clear();
        editor.commit();
        startActivity(i);
        finish();
    }

    //탈퇴하기 다이얼로그
    public class ViewDialog {

        public void showDialog(Activity activity) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            Button dialogButton1 = (Button) dialog.findViewById(R.id.btn_dialog_yes);
            dialogButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Call<Object> memberout = awsNetworkService.memberout(pref.getString("id", null));
                    memberout.enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Response<Object> response, Retrofit retrofit) {
                            if (response.code() == 200) {
                                logout();
                                Toast.makeText(getApplicationContext(), "탈퇴되었습니다.", Toast.LENGTH_LONG).show();
                                System.out.println("@@@@@@" + response.body());

                            } else if (response.code() == 503) {
//                            TODO 토스트
                                int statusCode = response.code();
                                Toast.makeText(getApplicationContext(), "실패!!", Toast.LENGTH_LONG).show();
                                Log.i("MyTag", "응답코드 : " + statusCode);
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            System.out.println("@@@@@@ memberout onFailure called" + t.getMessage());
                            Log.i("MyTag", "에러내용 : " + t.getMessage());
                        }
                    });
                }
            });

            Button dialogButton2 = (Button) dialog.findViewById(R.id.btn_dialog_no);
            dialogButton2.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
    public void linearOnClick(View v) { // 키보드 사라지기
        //키보드 내리기 -> 액션바 쓰면 자동으로 사라짐
        try {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}