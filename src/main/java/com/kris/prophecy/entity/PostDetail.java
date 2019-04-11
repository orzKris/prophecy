package com.kris.prophecy.entity;

/**
 * @author by Kris on 9/29/2018.
 * 帖子详情模型
 */
public class PostDetail {

    private Integer id;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReplyName() {
        return replyName;
    }

    public void setReplyName(String replyName) {
        this.replyName = replyName;
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

    public String getRespondentName() {
        return respondentName;
    }

    public void setRespondentName(String respondentName) {
        this.respondentName = respondentName;
    }

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }
}
