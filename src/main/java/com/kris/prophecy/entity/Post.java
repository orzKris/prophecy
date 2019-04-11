package com.kris.prophecy.entity;


public class Post {

    /**
     * 帖子id
     */
    private Integer id;

    /**
     * 发帖人uid
     */
    private String uid;

    private String title;

    private String content;

    private String createTime;

    /**
     * 点赞数
     */
    private Integer likes;

    /**
     * 回复人uid
     */
    private String replyUid;

    /**
     * 回复人名称
     */
    private String replyName;

    /**
     * 回复内容
     */
    private String replyContent;

    private String  replyTime;

    /**
     * 被回复人姓名
     */
    private String respondentName;

    /**
     * 回复id
     */
    private Integer rid;

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    public String getReplyName() {
        return replyName;
    }

    public void setReplyName(String replyName) {
        this.replyName = replyName;
    }

    public String getRespondentName() {
        return respondentName;
    }

    public void setRespondentName(String respondentName) {
        this.respondentName = respondentName;
    }

    public String getReplyUid() {
        return replyUid;
    }

    public void setReplyUid(String replyUid) {
        this.replyUid = replyUid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getReplyTime() {
        try {
            return replyTime.split("\\.")[0];
        }catch (Exception e){
            return replyTime;
        }
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getCreateTime() {
        try {
            return createTime.split("\\.")[0];
        } catch (Exception e) {
            return createTime;
        }
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }
}