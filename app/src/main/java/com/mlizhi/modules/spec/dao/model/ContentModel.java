package com.mlizhi.modules.spec.dao.model;

import java.util.Date;

public class ContentModel {
    private String contentComment;
    private String contentId;
    private String contentImageUrl;
    private Date contentInsertTime;
    private String contentPraise;
    private String contentSummarize;
    private String contentTitle;
    private Integer contentType;
    private String contentView;
    private Long id;

    public ContentModel(Long id) {
        this.id = id;
    }

    public ContentModel(Long id, String contentTitle, String contentSummarize, String contentImageUrl, String contentPraise, String contentView, String contentComment, Date contentInsertTime, Integer contentType, String contentId) {
        this.id = id;
        this.contentTitle = contentTitle;
        this.contentSummarize = contentSummarize;
        this.contentImageUrl = contentImageUrl;
        this.contentPraise = contentPraise;
        this.contentView = contentView;
        this.contentComment = contentComment;
        this.contentInsertTime = contentInsertTime;
        this.contentType = contentType;
        this.contentId = contentId;
    }

    public ContentModel() {

    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContentTitle() {
        return this.contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getContentSummarize() {
        return this.contentSummarize;
    }

    public void setContentSummarize(String contentSummarize) {
        this.contentSummarize = contentSummarize;
    }

    public String getContentImageUrl() {
        return this.contentImageUrl;
    }

    public void setContentImageUrl(String contentImageUrl) {
        this.contentImageUrl = contentImageUrl;
    }

    public String getContentPraise() {
        return this.contentPraise;
    }

    public void setContentPraise(String contentPraise) {
        this.contentPraise = contentPraise;
    }

    public String getContentView() {
        return this.contentView;
    }

    public void setContentView(String contentView) {
        this.contentView = contentView;
    }

    public String getContentComment() {
        return this.contentComment;
    }

    public void setContentComment(String contentComment) {
        this.contentComment = contentComment;
    }

    public Date getContentInsertTime() {
        return this.contentInsertTime;
    }

    public void setContentInsertTime(Date contentInsertTime) {
        this.contentInsertTime = contentInsertTime;
    }

    public Integer getContentType() {
        return this.contentType;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    public String getContentId() {
        return this.contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String toString() {
        return "ContentModel [id=" + this.id + ", contentTitle=" + this.contentTitle + ", contentSummarize=" + this.contentSummarize + ", contentImageUrl=" + this.contentImageUrl + ", contentPraise=" + this.contentPraise + ", contentView=" + this.contentView + ", contentComment=" + this.contentComment + ", contentInsertTime=" + this.contentInsertTime + "]";
    }
}
