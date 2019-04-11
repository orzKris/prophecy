package com.kris.prophecy.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author Kris
 * @date 2019/3/13
 */
@Data
@Document(collection = "dataCenter")
public class DataCenter {

    private String id;

    /**
     * 查询结果
     */
    private String result;

    /**
     * 创建时间
     */
    private Date createAt;

    public DataCenter() {
    }

    public DataCenter(String id, String result) {
        this.id = id;
        this.result = result;
        this.createAt = new Date();
    }

}
