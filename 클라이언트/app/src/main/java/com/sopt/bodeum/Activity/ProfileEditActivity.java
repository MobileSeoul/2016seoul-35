package com.sopt.bodeum.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sopt.bodeum.Component.GlobalApplication;
import com.sopt.bodeum.Model.Person;
import com.sopt.bodeum.Network.AwsNetworkService;
import com.sopt.bodeum.R;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by USER on 2016-10-30.
 */

public class ProfileEditActivity extends AppCompatActivity {

    Person person;
    AwsNetworkService awsNetworkService = GlobalApplication.getGlobalApplicationContext().getAwsNetwork();
    SharedPreferences pref, pref1;
    SharedPreferences.Editor edit;
    private String email, password, birthday, nickname, name;
    private TextView text_email, text_birthday, text_name;
    private EditText edit_password, edit_pwcheck, edit_nick;
    private ImageButton btnPhoto; // 사진 가져오기 버튼
    private ImageView imgPhoto; // 사진 이미지
    private ImageButton btnNicknameCheck; // 닉네임 중복 확인 버튼
    private ImageButton btnDone, btnCancel; // 가입, 취소 버튼
    private boolean isNick = false;
    private Button finish;

    // 사진 가져오기
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private Uri mImageCaptureUri;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        text_email = (TextView) findViewById(R.id.profileEmail);
        text_birthday = (TextView) findViewById(R.id.profileBirth);
        text_name = (TextView) findViewById(R.id.profileName);
        edit_password = (EditText) findViewById(R.id.profilePassword);
        edit_pwcheck = (EditText) findViewById(R.id.profilePasswordCheck);
        edit_nick = (EditText) findViewById(R.id.profileNickname);
        btnNicknameCheck = (ImageButton) findViewById(R.id.btnNicknameCheck);
        btnDone = (ImageButton) findViewById(R.id.btnDone);
        btnCancel = (ImageButton) findViewById(R.id.btnCancel);
        person = new Person();


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_actionbar);
        setSupportActionBar(toolbar);

        name = "이름";
        birthday = "생일";
        getUserFromSharedPreference();
        text_email.setText(email);
        if (name != "이름") {
            text_name.setText(name);
            text_birthday.setText(birthday);

        }
        person.setEmail(email);


        //뒤로가기 버튼
        finish = (Button) findViewById(R.id.finish_edit);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //상태바 색깔
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
        }
        // 비밀번호 일치 검사
        edit_pwcheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String password = edit_password.getText().toString();
                String check = edit_pwcheck.getText().toString();

                if (password.equals(check)) {
                    edit_password.setTextColor(Color.GREEN);
                    edit_pwcheck.setTextColor(Color.GREEN);
                } else {
                    edit_password.setTextColor(Color.RED);
                    edit_pwcheck.setTextColor(Color.RED);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnClickEvent();
    }

    private void getUserFromSharedPreference() {
        pref = getSharedPreferences("login", 0);
        email = pref.getString("id", null);              //SharedPreferences에서 아이디 가져옴
        password = pref.getString("pass", null);          //SharedPreferences에서 비밀번호 가져옴

        pref1 = getSharedPreferences("memberInfo", 0);
        String a = pref1.getString("email", null);
        edit=pref1.edit();

        if (email.equals(a)) {
            nickname = pref1.getString("nick", null);
            birthday = pref1.getString("birth", null);
            name = pref1.getString("name", null);
        }
        System.out.println("@@@@@@ Userid:" + email);


    }

    public void btnClickEvent() {

        // 확인 버튼 눌렀을 때
        btnDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int a = edit_password.getText().toString().length();
                int b = edit_pwcheck.getText().toString().length();
                int c = edit_nick.getText().toString().length();

                if (a == 0 && b == 0 && c == 0) {
                    Toast.makeText(ProfileEditActivity.this, "수정 정보를 빠짐없이 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    if (a == 0 && b != 0) {
                        Toast.makeText(ProfileEditActivity.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                        edit_password.requestFocus();
                        return;
                    }

                    // 비밀번호 확인칸 입력 확인
                    if (a != 0 && b == 0) {
                        Toast.makeText(ProfileEditActivity.this, "비밀번호 확인을 입력하세요", Toast.LENGTH_SHORT).show();
                        edit_pwcheck.requestFocus();
                        return;
                    }

                    // 비밀번호 일치 확인
                    if (!edit_password.getText().toString().equals(edit_pwcheck.getText().toString())) {
                        Toast.makeText(ProfileEditActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                        edit_password.setText("");
                        edit_pwcheck.setText("");
                        edit_password.requestFocus();
                        return;
                    }
                    if (c != 0 && isNick == false) {
                        Toast.makeText(ProfileEditActivity.this, "닉네임을 중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
                        edit_nick.requestFocus();
                        return;
                    }
                    if (a != 0) {
                        person.setMember_pass(edit_password.getText().toString());
                        edit.putString("pass", edit_password.getText().toString());
                        edit.commit();
                    }
                    if (c != 0) {
                        person.setMember_nick(edit_nick.getText().toString());
                        edit.putString("nick", edit_nick.getText().toString());
                        edit.commit();
                    }else{ person.setMember_nick(pref1.getString("nick",null));}
                    update_complete(person);
                }
            }
        });

        // 취소 버튼 누르면 이전 화면으로 돌아감
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnNicknameCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Call<Object> checkNick = awsNetworkService.checkNicname(edit_nick.getText().toString());
                checkNick.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Response<Object> response, Retrofit retrofit) {
                        if (response.code() == 200) {
                            Toast.makeText(getApplicationContext(), "사용 가능한 닉네임 입니다.", Toast.LENGTH_LONG).show();
                            System.out.println("@@@@@@" + response.body());
                            isNick = true;
                        } else if (response.code() == 503) {
                            int statusCode = response.code();
                            Toast.makeText(getApplicationContext(), "이미 사용하고 있는 닉네임 입니다.", Toast.LENGTH_LONG).show();
                            edit_nick.setText("");
                            isNick = false;
                            Log.i("MyTag", "응답코드 : " + statusCode);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        System.out.println("@@@@@@ checkNicname onFailure called" + t.getMessage());
                        Log.i("MyTag", "에러내용 : " + t.getMessage());
                    }
                });
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
    }

    public void update_complete(final Person person) {
        //서버연동
        Call<Object> update_complete = awsNetworkService.update_complete(person);
        update_complete.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Response<Object> response, Retrofit retrofit) {
                if (response.code() == 200) {
                    Toast.makeText(getApplicationContext(), "회원정보 수정이 완료되었습니다", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                    intent.putExtra("email", person.getEmail());
                    intent.putExtra("nic", person.getMember_nick());
                    startActivity(intent);
                } else if (response.code() == 503) {
                    int statusCode = response.code();
                    Log.i("MyTag", "응답코드 : " + statusCode);
                    finish();
                }
            }
            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "회원정보 수정 실패", Toast.LENGTH_LONG).show();
                finish();
                System.out.println("@@@@@@ update onFailure called" + t.getMessage());
                Log.i("MyTag", "에러내용 : " + t.getMessage());
            }
        });
    }

    public void update_request(String email) {

        //서버연동
        Call<Object> update_request = awsNetworkService.update_request(email);
        update_request.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Response<Object> response, Retrofit retrofit) {
                System.out.println("^^^^^^^^^" + response.code());

                if (response.code() == 200) {
                    Toast.makeText(getApplicationContext(), "회원정보 수정이 완료되었습니다", Toast.LENGTH_LONG).show();

                } else if (response.code() == 503) {
                    int statusCode = response.code();
                    Log.i("MyTag", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "회원정보 수정 실패", Toast.LENGTH_LONG).show();
                finish();
                System.out.println("@@@@@@ update onFailure called" + t.getMessage());
                Log.i("MyTag", "에러내용 : " + t.getMessage());
            }
        });
    }
}