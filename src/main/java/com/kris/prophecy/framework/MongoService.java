package com.kris.prophecy.framework;

import com.kris.prophecy.model.DataCenter;
import org.springframework.data.domain.Page;


/**
 * @author Kris
 * @date 2019/3/13
 */
public interface MongoService {

    void asyncInsert(DataCenter dataCenter);

    DataCenter findById(String id);

    Page<DataCenter> findByIdLike(String id, Integer page, Integer pageSize);

    void deleteById(String id);

    Page<DataCenter> findAll(Integer page, Integer pageSize);

    long count();

    long countByIdLike(String id);
}
