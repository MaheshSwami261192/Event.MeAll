package com.prometteur.myevents.SingletonClasses;


import com.prometteur.myevents.Adapters.EventImagesLikes;

import java.io.Serializable;
import java.security.PrivateKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class EventImageFirebase implements Serializable {

    private String imgUploaderName,imgUploaderUserName,imgUploaderPhoto,imgPath, imgUploadTime,imgKey;

    public Map<String, EventCommentFirebase> getImgComments() {
        return imgComments;
    }

    public void setImgComments(Map<String, EventCommentFirebase> comments) {
        this.imgComments = comments;
    }

    private Map<String,EventCommentFirebase> imgComments;

    private Map<String, EventImagesLikes> imgLikes;

    public EventImageFirebase() {
    }

    public EventImageFirebase(String imgUploaderName,String imgUploaderUserName, String imgUploaderPhoto,
                              String imgPath, String imgUploadTime,String imgKey,Map<String,EventCommentFirebase> comments,Map<String,EventImagesLikes> imgLikes) {

        this.imgUploaderName = imgUploaderName;
        this.imgUploaderUserName = imgUploaderUserName;
        this.imgUploaderPhoto = imgUploaderPhoto;
        this.imgPath = imgPath;
        this.imgUploadTime = imgUploadTime;
        this.imgKey = imgKey;
        this.imgComments =comments;
        this.imgLikes =imgLikes;
    }

    public String getImgUploaderName() {
        return imgUploaderName;
    }

    public void setImgUploaderName(String imgUploaderName) {
        this.imgUploaderName = imgUploaderName;
    }

    public String getImgUploaderPhoto() {
        return imgUploaderPhoto;
    }

    public void setImgUploaderPhoto(String imgUploaderPhoto) {
        this.imgUploaderPhoto = imgUploaderPhoto;
    }

    public String getImgUploaderUserName() {
        return imgUploaderUserName;
    }

    public void setImgUploaderUserName(String imgUploaderUserName) {
        this.imgUploaderUserName = imgUploaderUserName;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getImgUploadTime() {

        return imgUploadTime;
    }
    public String getFormattedImgUploadTime() {

        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat format2 = new SimpleDateFormat("hh:mm a");
        Date date = null;
        try {
            date = format1.parse(imgUploadTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return format2.format(date);
    }

    public void setImgUploadTime(String imgUploadTime) {
        this.imgUploadTime = imgUploadTime;
    }

    public String getImgKey() {
        return imgKey;
    }

    public void setImgKey(String imgKey) {
        this.imgKey = imgKey;
    }

    public Map<String, EventImagesLikes> getImgLikes() {
        return imgLikes;
    }

    public void setImgLikes(Map<String, EventImagesLikes> imgLikes) {
        this.imgLikes = imgLikes;
    }
}