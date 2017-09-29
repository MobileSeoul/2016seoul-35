package com.sopt.bodeum.Activity;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sopt.bodeum.Component.GlobalApplication;
import com.sopt.bodeum.Model.ReviewItem;
import com.sopt.bodeum.Network.AwsNetworkService;
import com.sopt.bodeum.R;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by USER on 2016-06-28.
 */
public class Writereview_Activity extends AppCompatActivity {


    //갤러리에서 사진 불러올 때 사용
    //private static final int PICK_FROM_ALBUM = 1;
    //private static final int CROP_FROM_CAMERA = 2;

    //private Uri mImageCaptureUri; //이미지 캡쳐했을 때의 uri저장
    //private ImageView imgReviewWritePhoto; //불러온 사진

    AwsNetworkService awsNetworkService = GlobalApplication.getGlobalApplicationContext().getAwsNetwork();
    SharedPreferences pref;

    float score;
    String Facility_ids;
    String Userid;
    String Userpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writereview_);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "yoon.mp3");
        ViewGroup root = (ViewGroup)findViewById(android.R.id.content);
        login_Activity.SetViewGroupFont(root, typeface);

        initValue();

        //상태바 색깔
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
        }

        //화면회전 금지
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        //imgReviewWritePhoto = (ImageView) findViewById(imgReviewWritePhoto); //갤러리에서 사진 불러오기 버튼

        ImageButton btnReviewWrite = (ImageButton) findViewById(R.id.btnReviewWriteFin); //완료 버튼
        final EditText editReviewWriteContent = (EditText) findViewById(R.id.editReviewWriteContent); // 글내용 에딧텍스트
        RatingBar ratBarReviewWrite = (RatingBar) findViewById(R.id.ratBarReviewWrite); //별점 레이팅바
        final TextView rateText = (TextView) findViewById(R.id.rateText); //별점 텍스트출력

        //하단바 숨기기
        btnReviewWrite.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        //액션바 디자인 바꾸기
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.write_review_actionbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_background_color)));

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.color_txt), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        //갤러리에서 사진불러오기
        /*imgReviewWritePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTakeAlbumAction();

            }
        });*/

        //별점 옆에 점수 텍스트로 띄우기
        ratBarReviewWrite.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rateText.setText(rating + "점");
                score = rating;
            }
        });


        //완료 버튼누르면 메인액티비티로
        btnReviewWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //데이터담기
                ReviewItem reviewItem = new ReviewItem();
                reviewItem.Review_score = score;
                reviewItem.Review_content = editReviewWriteContent.getText().toString();
                reviewItem.Member_emails = Userid;
                reviewItem.Facility_ids = Facility_ids;

                //이미지 직렬화
                //imgReviewWritePhoto.buildDrawingCache();
                //Bitmap bm = imgReviewWritePhoto.getDrawingCache();
                //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                //byte[] b = baos.toByteArray();

                //String encodedImage = Base64.encodeToString(b , Base64.DEFAULT);
//                reviewItem.Review_photo = encodedImage;
                //reviewItem.Review_photo = null;

                System.out.println("@@@@@@facilityIds : " + reviewItem.Facility_ids);

                // : 서버
                final Call<Object> addReviewWrite = awsNetworkService.facilReviewWrite(reviewItem);
                addReviewWrite.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Response<Object> response, Retrofit retrofit) {
                        System.out.println("@@@@@@@@@@@@@@ body : " + response.body());
                        if (response.code() == 200) {
                            Toast.makeText(getApplicationContext(), "리뷰 쓰기 완료", Toast.LENGTH_LONG).show();

                        } else if (response.code() == 503) {
                            int statusCode = response.code();
                            Log.i("MyTag", "응답코드 : " + statusCode);
                        }
                        finish();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(getApplicationContext(), "Failed to 리뷰", Toast.LENGTH_LONG).show();
                        Log.i("MyTag ", "에러내용 : " + t.getMessage());
                    }
                });

            }
        });


        // 액션바 뒤로가기 버튼
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

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


    private void initValue() {
        Intent intent = getIntent();
        Facility_ids = intent.getStringExtra("Facility_ids");

        pref = getSharedPreferences("login", 0);
        Userid = pref.getString("id", "null");              //SharedPreferences에서 아이디 가져옴
        Userpass = pref.getString("pass", "null");          //SharedPreferences에서 비밀번호 가져옴
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

        imgReviewWritePhoto = (ImageView) findViewById(imgReviewWritePhoto); //갤러리에서 불러온 이미지

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

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    imgReviewWritePhoto.setImageBitmap(getRoundedBitmap(photo));

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
}
