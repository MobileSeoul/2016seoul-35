package com.sopt.bodeum.Network;

import com.sopt.bodeum.Model.CommItem;
import com.sopt.bodeum.Model.FacilityInfo;
import com.sopt.bodeum.Model.Person;
import com.sopt.bodeum.Model.ReplyItem;
import com.sopt.bodeum.Model.ReviewItem;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;

public interface AwsNetworkService {

    // Aws_URL
    //String baseUrl = "192.168.0.19:3000";
//    String baseUrl = "http://192.168.43.251:3000";
//    String baseUrl = "http://117.16.198.47:3000";
//    String baseUrl = "http://117.16.197.247:3000/";

//    String baseUrl = "http://54.197.11.172:3000/"; //aws 현기
//    String baseUrl = "http://52.78.50.28:3000/"; //aws 정근
String baseUrl = "http://52.211.113.116:3000/"; // 아마여기서 계속

    /**
     * GET 방식과 POST 방식의 사용법을 잘 이해하셔야 합니다.
     * GET("/경로") 경로는 서버 파트에게 물어보세요. (※baseUrl 뒤에 붙는 경로입니다.ex) http://baseUrl/경로)
     * ("/경로/{식별자}) ~~(@Path{"식별자"} String value) 어떤 식별자를 통해 리소스를 구분하여 요청합니다. uri의 정의 기억나시죠? ex) http://baseUrl/경로/value
     * POST 방식은 @Body 에 뭔가를 담아서 보내야하죠?
     */
//    @GET("comm/list")
//    Call<ArrayList<CommItem>> getCommList();


//    @POST("/join")
//    Call<Object> memberJoin(@Body Person person);

    //회원가입
    @POST("join")
    Call<Object> memberJoin(@Body Person person);

    //닉네임중복검사
    @GET("join/nic/{nic}")
    Call<Object> checkNicname(@Path("nic") String nic);

    //로그인
    @POST("join/login")
    Call<Object> memberLogin(@Body Person person);

    //회원탈퇴
    @GET("join/out/{id}")
    Call<Object> memberout(@Path("id") String email);

    @POST("member/update_complete")
    Call<Object> update_complete(@Body Person person);

    @POST("member/update_request")
    Call<Object> update_request(@Body String email);



    /***************************시설****************************/
    //시설 모두 조회
    @GET("facil/totallist/{id}")
    Call<ArrayList<FacilityInfo>> getFacilityList(@Path("id") String email);

    //시설 모두 조회
//    @GET("facil/list/{search}/{id}")
//    Call<ArrayList<FacilityInfo>> getFacilityList();


    @POST("facil/search/where")
    Call<ArrayList<FacilityInfo>> getFacilityList(@QueryMap HashMap<String, String> param);

    //시설 상세 정보 조회
//    @GET("facil/list/{Facil_id}")
//    Call<Object> getFacilityInfo(@Path("Facil_id") int Facil_id);

    //시설 상세 정보 조회
    @GET("facil/list/{id}/{email}")
    Call<Object> getFacilityInfo(@Path("id") String id, @Path("email") String email);

    @GET("review/list/{Facility_id}")
    Call<ArrayList<ReviewItem>> getReviewList(@Path("Facility_id") String Facil_id);

    //시설 후기쓰기
    @POST("review")
    Call<Object> facilReviewWrite(@Body ReviewItem reviewItem);

    //시설 찜하기
    @POST("join/favorite")
    Call<Object> addFavorite(@QueryMap HashMap<String, String> param);

    @GET("/join/favorite/{id}")
    Call<ArrayList<FacilityInfo>> getFavorite(@Path("id") String id);

    /***************************커뮤니티****************************/

    //커뮤니티리스트 모두 조회
//    @GET("comm/mlist")
//    Call<ArrayList<CommItem>> getCommList();

    @GET("comm/totallists/{id}")
    Call<ArrayList<CommItem>> getCommList(@Path("id") String id);

    //검색어로 커뮤니티리스트 조회
    @GET("comm/mlist/{title}")
    Call<ArrayList<CommItem>> getCommListBySearch(@Path("title") String title);

//    @GET("comm/lists/{Comm_id}")
//    Call<CommItem> getCommContents(@Path("Comm_id") String id);

    @GET("comm/lists/{id}/{email}")
    Call<CommItem> getCommContents(@Path("id") String id, @Path("email") String email);

    //커뮤니티글쓰기
    @POST("comm")
    Call<Object> commWrite(@Body CommItem commItem);

    //커뮤니티 댓글쓰기
    @POST("reply")
    Call<Object> commWriteReply(@Body ReplyItem replyItem);

    //커뮤니티 리플 조회하기
    @GET("reply/list/{Comm_Comm_id}")
    Call<ArrayList<ReplyItem>> getReply(@Path("Comm_Comm_id") String id);

    @POST("like")
    Call<Object> sendLike(@QueryMap HashMap<String, String> param);

}

