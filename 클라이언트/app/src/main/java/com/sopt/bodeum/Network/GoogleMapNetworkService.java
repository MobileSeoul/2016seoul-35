package com.sopt.bodeum.Network;

import java.util.HashMap;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.QueryMap;

public interface GoogleMapNetworkService {

    /**
     * Tourapi에서 제공해주는 API_KEY를 String 형으로 저장
     * GET 어노테이션과 메소드 구현
     * 서버에 요청할 디렉토리를 GET 어노테이션에 인자로 넣어줌
     * Call<받고자 하는 데이터 타입> (request에 추가될 사항들)
     * request에 추가될 사항들을 예로 들면
     * Body가 될 수도 있고(POST 방식의 경우)
     * Path가 될 수도 있고
     * QueryMap, Query가 될 수도 있고
     * Body, Path, Query가 다 들어갈 수도 있습니다.
     */
    String apiKey = "AIzaSyADEj6wgN_fZpysALMsbr0_Lqm9c8WZr9w";
//    String baseUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    String baseUrl = "https://maps.googleapis.com/maps/api/geocode/";

    // Get 방식으로 요청
    @GET("json")
    Call<Object> getLocation(@QueryMap HashMap<String, String> parameter);


}
