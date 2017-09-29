package com.sopt.bodeum.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sopt.bodeum.Component.GlobalApplication;
import com.sopt.bodeum.Model.CommItem;
import com.sopt.bodeum.Network.AwsNetworkService;
import com.sopt.bodeum.R;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class CommWriteActivity extends AppCompatActivity {

    //갤러리에서 사진 불러올 때 사용
    //private static final int PICK_FROM_ALBUM = 1;
   // private static final int CROP_FROM_CAMERA = 2;

    EditText editCommWriteTitle;
    EditText editCommWriteContent;
    //ImageView btnCommWritePhoto;
    ImageButton btnCommWriteFin;

    //private Uri mImageCaptureUri; //이미지 캡쳐했을 때의 uri저장

    CommItem commItem = new CommItem();

    AwsNetworkService awsNetworkService;

    SharedPreferences pref;
    String Userid;
    String Userpass;
    String Facility_ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm_write);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "yoon.mp3");
        ViewGroup root = (ViewGroup)findViewById(android.R.id.content);
        CommWriteActivity.SetViewGroupFont(root, typeface);


        initNetworkService();

        initView();
        btnClickEvent();

        getUserFromSharedPreference();

        //상태바 색깔
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
        }

        //액션바 디자인 바꾸기
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.comm_write_actionbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_background_color)));

        // 액션바 뒤로가기 버튼
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.color_txt), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        //화면회전 금지
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        // comm_id 넘겨주기(CommunityView로)
        Intent intent = new Intent(CommWriteActivity.this, CommunityViewActivity.class);
        intent.putExtra("comm_id",commItem.Comm_id);
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

    private void initNetworkService() {
        awsNetworkService = GlobalApplication.getGlobalApplicationContext().getAwsNetwork();
    }

    private void getUserFromSharedPreference() {
        pref = getSharedPreferences("login", 0);
        Userid = pref.getString("id", "null");              //SharedPreferences에서 아이디 가져옴
        Userpass = pref.getString("pass", "null");          //SharedPreferences에서 비밀번호 가져옴
        System.out.println("@@@@@@ Userid:" + Userid);

    }

    private void btnClickEvent() {

        //사진불러오기 버튼
        /*btnCommWritePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTakeAlbumAction();

            }
        });*/

        //글쓰기 완료 버튼클릭
        btnCommWriteFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //데이터담기
                CommItem commItem = new CommItem();
                commItem.Comm_title = editCommWriteTitle.getText().toString();
                commItem.Comm_content = editCommWriteContent.getText().toString();
                commItem.Member_email = Userid;

                //이미지 직렬화
                //btnCommWritePhoto.buildDrawingCache();
                //Bitmap bm = btnCommWritePhoto.getDrawingCache();
                //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                //byte[] b = baos.toByteArray();

                //String encodedImage = Base64.encodeToString(b , Base64.DEFAULT);
               //commItem.Comm_photo = null;

                // : 서버
                final Call<Object> addCommWrite = awsNetworkService.commWrite(commItem);
                addCommWrite.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Response<Object> response, Retrofit retrofit) {
                        if (response.code() == 200) {
                            Toast.makeText(getApplicationContext(), "글쓰기 완료", Toast.LENGTH_LONG).show();

                        } else if (response.code() == 503) {
                            int statusCode = response.code();
                            Log.i("MyTag", "응답코드 : " + statusCode);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(getApplicationContext(), "Failed to comm write", Toast.LENGTH_LONG).show();
                        Log.i("MyTag ", "에러내용 : " + t.getMessage());
                    }
                });

//                Intent intent = new Intent(getApplicationContext(), CommunityViewActivity.class);
//                intent.putExtra("commTitle", editCommWriteTitle.getText().toString());
//                intent.putExtra("commContent", editCommWriteContent.getText().toString());
//                startActivity(intent);
                finish();
            }
        });

    }

    /*
    private void doTakeAlbumAction() {
        //1. 갤러리 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    //2. 갤러리에서 사진 클릭한 후
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {

            case PICK_FROM_ALBUM: {
                mImageCaptureUri = data.getData();  //3. 이미지를 가져옴

                //4. 이미지를 가져온 이후의 리사이즈
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 90);
                intent.putExtra("outputY", 90);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA);

                break;
            }

            case CROP_FROM_CAMERA: {
                //5. 크롭 된 후의 이미지를 넘겨 받음. 이미지뷰에 이미지 보내준 후 임시 파일은 삭제


                final Bundle extras = data.getExtras();


                File f = new File(mImageCaptureUri.getPath());
                String a = f.getAbsolutePath(); //파일 f의 절대경로
                String GetFileName = new File(a).getName() + ".jpg"; //경로에서 파일명만 가져오기

                System.out.println("@@@@@@@GetFileName:"+GetFileName);

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    btnCommWritePhoto.setImageBitmap(getRoundedBitmap(photo));
//                    fileName.setText(GetFileName);

                }

                // 임시 파일 삭제
                if (f.exists()) {
                    f.delete();
                }

                break;
            }

        }
    }

    // 사진 라운딩 처리
    public static Bitmap getRoundedBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.GRAY;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }
    */

    //액션바 뒤로가기 버튼 눌렀을 때 실행
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

    private void initView() {
        editCommWriteTitle = (EditText) findViewById(R.id.editCommWriteTitle);
        editCommWriteContent = (EditText) findViewById(R.id.editCommWriteContent);
//        ivCommPhoto = (ImageView) findViewById(R.id.ivCommPhoto);
        //btnCommWritePhoto = (ImageView) findViewById(btnCommWritePhoto);
        btnCommWriteFin = (ImageButton) findViewById(R.id.btnCommWriteFin);
    }
}
