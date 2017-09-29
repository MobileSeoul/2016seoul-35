package com.sopt.bodeum.Model;

/**
 * Created by user on 2016-07-02.*/

public class FavoriteItem {
    int image_fav; //사진
    String name_fav; // 시설이름
    String review_fav; // 후기
    String num_fav; // 갯수
    String part_fav; //"개"
    String contents_fav; //시설종류
    int grade_fav; // 등급이미지

    public FavoriteItem(int image_fav, String name_fav, String review_fav,String num_fav, String part_fav, String contents_fav,int grade_fav){
        this.image_fav = image_fav;
        this.name_fav = name_fav;
        this.review_fav = review_fav;
        this.num_fav = num_fav;
        this.part_fav = part_fav;
        this.contents_fav = contents_fav;
        this.grade_fav = grade_fav;
    }

    public int getImage_fav() {
        return image_fav;
    }

    public String getName_fav() {
        return name_fav;
    }

    public String getReview_fav() {
        return review_fav;
    }

    public String getNum_fav() {
        return num_fav;
    }

    public String getPart_fav() {
        return part_fav;
    }

    public String getContents_fav() {
        return contents_fav;
    }

    public int getGrade_fav() {
        return grade_fav;
    }
}