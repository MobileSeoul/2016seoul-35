package com.sopt.bodeum.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.sopt.bodeum.Component.GlobalApplication;
import com.sopt.bodeum.Model.Person;
import com.sopt.bodeum.Network.AwsNetworkService;
import com.sopt.bodeum.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static android.app.AlertDialog.*;
/**
 * Created by HOME on 2016-06-27.
 */
public class RegistActivity extends AppCompatActivity {
    SharedPreferences pref;
    SharedPreferences.Editor edit;
    AwsNetworkService awsNetworkService = GlobalApplication.getGlobalApplicationContext().getAwsNetwork();

    //private ImageButton btnPhoto; // 사진 가져오기 버튼
    //private ImageView imgPhoto; // 사진 이미지
    private EditText editEmail, editPassword, editPasswordCheck; // 이메일, 비밀번호, 비밀번호 확인
    private EditText editName, editNickname, editBirth; // 이름, 닉네임, 생일 입력
    private EditText editPhone; // 전화번호 입력
    private ImageButton btnNicknameCheck; // 닉네임 중복 확인 버튼
    private RadioGroup RbtnGroup; // 성별 라디오 버튼 그룹
    private ImageButton btnDone, btnCancel; // 가입, 취소 버튼

    // datePicker 사용
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog dialog;

    /*
    // 사진 가져오기
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private Uri mImageCaptureUri;
    */

    // 서버 연동
    private String Member_id;
    private String Member_pass;
    private String Member_name;
    private String Member_email;
    private String Member_gender;
    private String Member_birth;
    private String Member_phone;
    private String Member_nick;

    private boolean isNick = false;

    //join task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        //화면회전 금지
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        //btnPhoto = (ImageButton) findViewById(btnPhoto);
        //imgPhoto = (ImageView) findViewById(imgPhoto);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editPasswordCheck = (EditText) findViewById(R.id.editPasswordCheck);

        editName = (EditText) findViewById(R.id.editName);
        editNickname = (EditText) findViewById(R.id.editNickname);

        // 생년월일 입력 시 필요한 것들
        editBirth = (EditText) findViewById(R.id.editBirth);
        editBirth.setInputType(InputType.TYPE_NULL);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

        btnDone = (ImageButton) findViewById(R.id.btnDone);
        btnCancel = (ImageButton) findViewById(R.id.btnCancel);
        btnNicknameCheck = (ImageButton) findViewById(R.id.btnNicknameCheck);
        btnClickEvent();

        // 생년월일 눌렀을 때 datePicker 띄우기
        Calendar newCalendar = Calendar.getInstance();
        dialog = new DatePickerDialog(RegistActivity.this, THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                editBirth.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        editBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        // 비밀번호 일치 검사
        editPasswordCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String password = editPassword.getText().toString();
                String check = editPasswordCheck.getText().toString();

                if (password.equals(check)) {
                    editPassword.setTextColor(Color.GREEN);
                    editPasswordCheck.setTextColor(Color.GREEN);
                } else {
                    editPassword.setTextColor(Color.RED);
                    editPasswordCheck.setTextColor(Color.RED);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void btnClickEvent(){

        /*
        // 사진 선택 버튼 눌렀을 때
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        doTakePhotoAction();
                    }
                };

                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        doTakeAlbumAction();
                    }
                };

                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                };

                new Builder(RegistActivity.this)
                        .setTitle("업로드할 이미지 선택")
                        .setNeutralButton("앨범선택", albumListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();
            }
        });
        */


        // 가입 버튼 눌렀을 때
        btnDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // 아이디(이메일) 입력 확인
                if (editEmail.getText().toString().length() == 0) {
                    Toast.makeText(RegistActivity.this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
                    editEmail.requestFocus();
                    return;
                }

                // 비밀번호 입력 확인
                if (editPassword.getText().toString().length() == 0) {
                    Toast.makeText(RegistActivity.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    editPassword.requestFocus();
                    return;
                }

                // 비밀번호 확인칸 입력 확인
                if (editPassword.getText().toString().length() == 0) {
                    Toast.makeText(RegistActivity.this, "비밀번호 확인을 입력하세요", Toast.LENGTH_SHORT).show();
                    editPasswordCheck.requestFocus();
                    return;
                }

                // 비밀번호 일치 확인
                if (!editPassword.getText().toString().equals(editPasswordCheck.getText().toString())) {
                    Toast.makeText(RegistActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    editPassword.setText("");
                    editPasswordCheck.setText("");
                    editPassword.requestFocus();
                    return;
                }

                if (!checkEmailForm(editEmail.getText().toString())) {               //이메일 형식 검사
                    Toast.makeText(getApplicationContext(), "email형식을 올바르게 입력하세요.", Toast.LENGTH_SHORT).show();
                    editEmail.requestFocus();
                    return;
                }
                if(isNick == false){
                    Toast.makeText(RegistActivity.this, "닉네임을 중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
                    editNickname.requestFocus();
                    return;
                }

                Person person = new Person();

                person.Member_email = editEmail.getText().toString();
                person.Member_pass = editPassword.getText().toString();
                person.Member_name = editName.getText().toString();
                person.Member_birth = editBirth.getText().toString();
                person.Member_nick = editNickname.getText().toString();
//                person.Member_phone = editPhone.getText().toString();
//                person.Member_gender = RbtnGroup.toString();
                //task = new join();
                //task.execute();


                pref= getSharedPreferences("memberInfo",0);
                edit=pref.edit();
                edit.putString("email",editEmail.getText().toString());
                edit.putString("pass",editPassword.getText().toString());
                edit.putString("name",editName.getText().toString());
                edit.putString("birth",editBirth.getText().toString());
                edit.putString("nick",editNickname.getText().toString());
                edit.commit();


                //서버연동
                Call<Object> memberJoin = awsNetworkService.memberJoin(person);
                memberJoin.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Response<Object> response, Retrofit retrofit) {
                        if (response.code() == 200) {
                            Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다", Toast.LENGTH_LONG).show();
                            Intent result = new Intent();
                            result.putExtra("아이디", editEmail.getText().toString());
                            result.putExtra("비밀번호", editPassword.getText().toString());
                            // LoginActivity로 데이터를 보낸다.
                            setResult(RESULT_OK, result);
                            finish();

                        } else if (response.code() == 503) {
                            int statusCode = response.code();
                            Toast.makeText(RegistActivity.this, "이미 가입된 이메일 주소입니다.", Toast.LENGTH_SHORT).show();
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

                Call<Object> checkNic = awsNetworkService.checkNicname(editNickname.getText().toString());
                checkNic.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Response<Object> response, Retrofit retrofit) {
                        if (response.code() == 200) {
                            Toast.makeText(getApplicationContext(), "사용 가능한 닉네임 입니다.", Toast.LENGTH_LONG).show();
                            System.out.println("@@@@@@" + response.body());
                            isNick = true;
                        } else if (response.code() == 503) {
                            int statusCode = response.code();
                            Toast.makeText(getApplicationContext(), "이미 사용하고 있는 닉네임 입니다.", Toast.LENGTH_LONG).show();
                            editNickname.setText("");
                            isNick = false;
                            Log.i("MyTag", "응답코드 : " + statusCode);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        System.out.println("@@@@@@ checkNicname onFailure called" + t.getMessage());

//                Toast.makeText(getApplicationContext(), "Failed to load thumbnails", Toast.LENGTH_LONG).show();
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
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public boolean checkEmailForm(String src){              //이메일 형식검사
        String emailRegex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        return Pattern.matches(emailRegex, src);
    }

    /*
    //카메라에서 이미지가져오기
    private void doTakePhotoAction() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    //앨범에서 이미지가져오기
    private void doTakeAlbumAction() {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
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

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case CROP_FROM_CAMERA: {
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제합니다.
                final Bundle extras = data.getExtras();

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    imgPhoto.setImageBitmap( getRoundedBitmap(photo));
                }

                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if (f.exists()) {
                    f.delete();
                }

                break;
            }

            case PICK_FROM_ALBUM: {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.

                mImageCaptureUri = data.getData();
            }

            case PICK_FROM_CAMERA: {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

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
        }
    } */
}

