package com.sopt.bodeum.Model;

import java.util.ArrayList;

/**
 * Created by jingyu on 16. 7. 4..
 */
public class CommContents {
    String Comm_nic;
    String Comm_id;
    String Comm_title;
    String Comm_profile;
    String Comm_photo;
    String Comm_time;
    String Comm_like;
    String Comm_reply;

    ArrayList<CommReplyItem> commReplyItems;

    public CommContents(String comm_nic, String comm_id, String comm_title, String comm_profile,
                        String comm_photo, String comm_time, String comm_like, String comm_reply,
                        ArrayList<CommReplyItem> commReplyItems)
    {
        Comm_nic = comm_nic;
        Comm_id = comm_id;
        Comm_title = comm_title;
        Comm_profile = comm_profile;
        Comm_photo = comm_photo;
        Comm_time = comm_time;
        Comm_like = comm_like;
        Comm_reply = comm_reply;
        this.commReplyItems = commReplyItems;
    }

    public String getComm_nic() {
        return Comm_nic;
    }

    public void setComm_nic(String comm_nic) {
        Comm_nic = comm_nic;
    }

    public String getComm_id() {
        return Comm_id;
    }

    public void setComm_id(String comm_id) {
        Comm_id = comm_id;
    }

    public String getComm_title() {
        return Comm_title;
    }

    public void setComm_title(String comm_title) {
        Comm_title = comm_title;
    }

    public String getComm_profile() {
        return Comm_profile;
    }

    public void setComm_profile(String comm_profile) {
        Comm_profile = comm_profile;
    }

    public String getComm_photo() {
        return Comm_photo;
    }

    public void setComm_photo(String comm_photo) {
        Comm_photo = comm_photo;
    }

    public String getComm_time() {
        return Comm_time;
    }

    public void setComm_time(String comm_time) {
        Comm_time = comm_time;
    }

    public String getComm_like() {
        return Comm_like;
    }

    public void setComm_like(String comm_like) {
        Comm_like = comm_like;
    }

    public String getComm_reply() {
        return Comm_reply;
    }

    public void setComm_reply(String comm_reply) {
        Comm_reply = comm_reply;
    }

    public ArrayList<CommReplyItem> getCommReplyItems() {
        return commReplyItems;
    }

    public void setCommReplyItems(ArrayList<CommReplyItem> commReplyItems) {
        this.commReplyItems = commReplyItems;
    }
}
