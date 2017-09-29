package com.sopt.bodeum.Model;

/**
 * Created by jingyu on 16. 7. 4..
 */
public class Person {
    public String Member_email;
    public String Member_pass;
    public String Member_name;
    public String Member_gender;
    public String Member_birth;
    public String Member_phone;
    public String Member_nick;
    public String Member_photo;
    public String email;


    public Person(String member_email, String member_pass) {
        Member_email = member_email;
        Member_pass = member_pass;

    }

    public Person() {

    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMember_email() {
        return Member_email;
    }

    public void setMember_email(String member_email) {
        Member_email = member_email;
    }

    public String getMember_pass() {
        return Member_pass;
    }

    public void setMember_pass(String member_pass) {
        Member_pass = member_pass;
    }

    public String getMember_name() {
        return Member_name;
    }

    public void setMember_name(String member_name) {
        Member_name = member_name;
    }

    public String getMember_gender() {
        return Member_gender;
    }

    public void setMember_gender(String member_gender) {
        Member_gender = member_gender;
    }

    public String getMember_birth() {
        return Member_birth;
    }

    public void setMember_birth(String member_birth) {
        Member_birth = member_birth;
    }

    public String getMember_phone() {
        return Member_phone;
    }

    public void setMember_phone(String member_phone) {
        Member_phone = member_phone;
    }

    public String getMember_nick() {
        return Member_nick;
    }

    public void setMember_nick(String member_nick) {
        Member_nick = member_nick;
    }
}
