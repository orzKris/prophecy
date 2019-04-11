package com.kris.prophecy.entity;


/**
 * @author bu Kris on 9/29/2018
 * 帖子概览模型
 */
public class PostOverview {

    private Integer id;

    private String  uid;

    private String title;

    private String content;

    private String  createTime;

    private Integer likes;

    /**
     * 回复总数量
     */
    private Integer count;

    /**
     * 发帖人姓名
     */
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        try {
            return createTime.split("\\.")[0];
        }catch (Exception e){
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
