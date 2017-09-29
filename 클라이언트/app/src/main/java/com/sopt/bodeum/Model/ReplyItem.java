package com.sopt.bodeum.Model;

/**
 * Created by jingyu on 16. 7. 5..
 */
public class ReplyItem {
    public String Reply_text;
    public String Comm_Comm_id;
    public String Member_Member_email;
    public String Member_nic;
    public String Comm_time;

    public ReplyItem(String reply, String comm_id, String email) {
        this.Reply_text = reply;
        this.Comm_Comm_id = comm_id;
        this.Member_Member_email = email;
    }


    public ReplyItem(String reply, String comm_id, String email, String nick) {
        this.Member_nic = nick;
        this.Reply_text = reply;
        this.Comm_Comm_id = comm_id;
        this.Member_Member_email = email;
    }

}
