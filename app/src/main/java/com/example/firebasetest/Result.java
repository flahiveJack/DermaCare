package com.example.firebasetest;

import java.util.Date;

public class Result extends ResultId{
    private String image , user , result, question1, answer1, question2, answer2;
    private Date time;

    public String getImage() {
        return image;
    }

    public String getUser() {
        return user;
    }

    public String getResult() {
        return result;
    }

    public String getQuestion1() {
        return question1;
    }

    public String getAnswer1() {
        return answer1;
    }

    public String getQuestion2() {
        return question2;
    }


    public String getAnswer2() {
        return answer2;
    }



    public Date getTime() {
        return time;
    }
}
