package com.sopt.bodeum.Activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.sopt.bodeum.Component.GlobalApplication;
import com.sopt.bodeum.Model.Person;
import com.sopt.bodeum.Network.AwsNetworkService;
import com.sopt.bodeum.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by HOME on 2016-06-27.
 */
public class login_Activity extends AppCompatActivity {

    EditText editId, editPassword; // 아이디, 비밀번호 입력
    Button btnLogin, btnRegist; // 로그인 버튼, 회원가입 버튼

    SessionCallback callback; // 콜백 선언

    AwsNetworkService awsNetworkService;
    ProgressBar progressBar; // 로딩화면을 위한 변수

    InputMethodManager imm; // 키보드 사라지기

    SharedPreferences pref;
    SharedPreferences.Editor edit;
    String Userid;                      //사용자 아이디
    String Userpass;                    //사용자 비밀번호

    Person p = new Person();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_);
        editId = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);

        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnRegist = (Button) findViewById(R.id.btnRegist);

//        Typeface typeface = Typeface.createFromAsset(getAssets(), "BMJUA.mp3");
//        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
//        login_Activity.SetViewGroupFont(root, typeface);

        initSharedPre();    //SharedPreferences 초기화
        loginTest(); //로그인 된적이 있는지를 검사하여서 바로 로그인 시킴

        //화면회전 금지
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        initNetworkService();
        btnRegist.setOnClickListener(new View.OnClickListener() { // 회원가입 버튼 클릭 시
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegistActivity.class);
                startActivityForResult(intent, 1000);
            }
        });

        // 로그인 버튼 눌렀을 때
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 아이디 입력 확인
                if (editId.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                    editId.requestFocus();
                    return;
                }
                // 비밀번호 입력 확인
                else if (editPassword.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    editPassword.requestFocus();
                    return;
                } else if (!checkEmailForm(editId.getText().toString())) {               //이메일 형시 검사
                    Toast.makeText(getApplicationContext(), "email형식을 올바르게 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    ActivateProgressbar(); //로딩화면 활성화
                    checkFromServer(); //서버에서 로그인 확인
                }
            }
        });

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
    }

    public static void SetViewGroupFont(ViewGroup root, Typeface mTypeface) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            int length = child.toString().length();
            String tmp = child.toString().substring(length - 13, length - 1);
            if (tmp.equals("editPassword")) {
                continue;
            }

            if (child instanceof TextView) {
                ((TextView) child).setTypeface(mTypeface);
            } else if (child instanceof ViewGroup) SetViewGroupFont((ViewGroup) child, mTypeface);
        }
    }

    private void initSharedPre() {
        pref = getSharedPreferences("login", 0);
        edit = pref.edit();
    }

    private void loginTest() {                               //로그인한적이 있는 검사하는 함수
        Userid = pref.getString("id", "null");              //SharedPreferences에서 아이디 가져옴
        Userpass = pref.getString("pass", "null");          //SharedPreferences에서 비밀번호 가져옴
//        System.out.println("@@@@@@ Userid:" + Userid);

        if (!(Userid.equals("null"))) {                       //null이 아니라면
//            Toast.makeText(LoginActivity.this, "로그인 기록이 있다."+ Userid + Userpass, Toast.LENGTH_SHORT).show();
            editId.setText(Userid);                         //아이디 설정
            editPassword.setText(Userpass);                     //비밀번호 설정
//            Toast.makeText(LoginActivity.this, ""+btnLogin.getText()+editID.getText()+editPass.getText(), Toast.LENGTH_SHORT).show();

            btnLogin.post(new Runnable() {
                @Override
                public void run() {
                    btnLogin.performClick();            //스레드 로그인 버튼 클릭
                }
            });

        } else {
//            Toast.makeText(login_Activity.this, "로그인 기록이 없다.", Toast.LENGTH_SHORT).show();
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

    private void initNetworkService() {
        awsNetworkService = GlobalApplication.getGlobalApplicationContext().getAwsNetwork();
    }

    private void checkFromServer() {
        System.out.println("checkFromServer called");

        final String Member_email = editId.getText().toString();
        final String Member_pass = editPassword.getText().toString();

//                Toast.makeText(getApplicationContext(), "로그인 버튼이 눌렸습니다.\n"
//                        + "입력하신   아이디 : " + id + "\n"
//                        + "입력하신 비밀번호 : " + pass, Toast.LENGTH_SHORT).show();

        //todo 암호화해서 보내기
//        String passwd = makeMD5(Member_pass);
        //temp 임시 암호화 변수
        final Person person = new Person(Member_email, Member_pass);
//        final Person person = new Person(Member_email, passwd);
        //Person 객체에 아이디, 비밀번호 넣어서 객체를 서버에 전송
        Call<Object> memberLogin = awsNetworkService.memberLogin(person);

        memberLogin.enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Response<Object> response, Retrofit retrofit) {
                int statusCode = response.code();
                System.out.println("@@@@@@@statusCode : " + response.body());

                if (response.code() == 200) {
//                    Toast.makeText(getApplicationContext(), "로그인 OK", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);

                    Gson gson = new Gson();
                    String jsonString = gson.toJson(response.body());
                    try {
                        String tmpNic="";
                        String tmpPhoto="";
                        JSONObject obj = new JSONObject(jsonString);

                        try{
                            System.out.println("@@@@@@@@@@@@Member_nick" + tmpNic);
                            tmpNic = obj.getString("Member_nick");
                            intent.putExtra("nic", tmpNic);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        try {
                            System.out.println("@@@@@@@@@@@@Member_photo" + tmpPhoto);
                            System.out.println("@@@@@@@@@@@@Member_photo" + tmpPhoto);
                            tmpPhoto=obj.getString("Member_photo");
                            intent.putExtra("profile", tmpPhoto);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                        final String id = editId.getText().toString();
                        final String pass = editPassword.getText().toString();
                        //SharedPreferences 아이디 비밀번호 저장
                        edit.putString("id", id);
                        edit.putString("pass", pass);
                        edit.commit();

                        intent.putExtra("email", id);

                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(getApplicationContext(), "로그인 되었습니다", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 503) {
                    Toast.makeText(getApplicationContext(), "로그인하지 못했습니다", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {

//                Toast.makeText(getApplicationContext(), "완전 오류!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public String makeMD5(String str) {              //암호화 함수
        String MD5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte byteData[] = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            MD5 = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            MD5 = null;
        }
        return MD5;
    }


    private void ActivateProgressbar() {                     //로그인시 화면 생성 함수
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 500); // see this max value coming back here, we animale towards that value
        animation.setDuration(5000); //in milliseconds
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    public boolean checkEmailForm(String src) {              //이메일 형식검사
        String emailRegex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        return Pattern.matches(emailRegex, src);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // setResult를 통해 받아온 요청번호, 상태, 데이터
        Log.d("RESULT", requestCode + "");
        Log.d("RESULT", resultCode + "");
        Log.d("RESULT", data + "");

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            Toast.makeText(getApplicationContext(), "회원가입을 완료했습니다", Toast.LENGTH_SHORT).show();
            editId.setText(data.getStringExtra("아이디"));
            editPassword.setText(data.getStringExtra("비밀번호"));

        }

        //간편로그인시 호출 ,없으면 간편로그인시 로그인 성공화면으로 넘어가지 않음
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {

            UserManagement.requestMe(new MeResponseCallback() {

                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;
                    Logger.d(message);

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        finish();
                    } else {
                        //redirectMainActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                }

                @Override
                public void onNotSignedUp() {
                }

                @Override
                public void onSuccess(final UserProfile userProfile) {
                    //로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url등을 리턴합니다.
                    //사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.
                    Log.e("UserProfile", userProfile.toString());


                    /******회원가입처리***********/
                    if (!(pref.getString("kakao", "null").equals("kakao"))) { //카카오 로그인이 처음일때 가입시킴
                        final String email_id = Long.toString(userProfile.getId()) + "@kakao.com";

                        Person person = new Person();

                        person.Member_email = email_id;
                        person.Member_pass = editPassword.getText().toString();
                        person.Member_name = userProfile.getNickname();
                        person.Member_nick = userProfile.getNickname();
                        person.Member_photo = userProfile.getThumbnailImagePath();

                        //서버연동
                        Call<Object> memberJoin = awsNetworkService.memberJoin(person);
                        memberJoin.enqueue(new Callback<Object>() {
                            @Override
                            public void onResponse(Response<Object> response, Retrofit retrofit) {
                                if (response.code() == 200) {
                                    System.out.println("@@@@@@@@@@@@카카오톡으로 회원가입 완료");

                                    System.out.println("@@@@@@ 카톡카톡카톡 : " + response.body());

                                    edit.putString("kakao", "yes");
                                    edit.putString("profile", userProfile.getThumbnailImagePath());
                                    edit.putString("id", email_id);
                                    edit.putString("nic", userProfile.getNickname());
                                    edit.commit();

                                } else if (response.code() == 503) {
                                    int statusCode = response.code();
                                    Log.i("MyTag", "응답코드 : " + statusCode);
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_LONG).show();
                                System.out.println("@@@@@@ memberJoin onFailure called" + t.getMessage());
                                Log.i("MyTag", "에러내용 : " + t.getMessage());
                            }
                        });
                    }

                    /******************************/

                    Intent intent = new Intent(login_Activity.this, NavigationActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
        }
    }
}
