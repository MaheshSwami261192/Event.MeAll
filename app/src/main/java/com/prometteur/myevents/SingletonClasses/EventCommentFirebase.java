package com.prometteur.myevents.SingletonClasses;


import java.io.Serializable;

public class EventCommentFirebase implements Serializable {
    private String strComment, commentorName, commentorUserName, commentorProfile,commentDateTime;

    public EventCommentFirebase() {
    }

    public EventCommentFirebase(String strComment, String commentorName, String commentorUserName, String commentorProfile, String commentDateTime) {
        this.strComment = strComment;
        this.commentorName = commentorName;
        this.commentorUserName = commentorUserName;
        this.commentorProfile = commentorProfile;
        this.commentDateTime = commentDateTime;
    }

    public String getStrComment() {
        return strComment;
    }

    public void setStrComment(String strComment) {
        this.strComment = strComment;
    }


    public String getCommentorUserName() {
        return commentorUserName;
    }

    public void setCommentorUserName(String commentorUserName) {
        this.commentorUserName = commentorUserName;
    }

    public String getCommentorName() {
        return commentorName;
    }

    public void setCommentorName(String commentorName) {
        this.commentorName = commentorName;
    }

    public String getCommentorProfile() {
        return commentorProfile;
    }

    public void setCommentorProfile(String commentorProfile) {
        this.commentorProfile = commentorProfile;
    }

    public String getCommentDateTime() {
        return commentDateTime;
    }

    public void setCommentDateTime(String commentDateTime) {
        this.commentDateTime = commentDateTime;
    }

}