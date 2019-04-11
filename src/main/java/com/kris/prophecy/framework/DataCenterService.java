package com.kris.prophecy.framework;

import com.kris.prophecy.model.DataCenter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Kris
 * @date 2019/3/13
 */
@Repository
public interface DataCenterService extends MongoRepository<DataCenter, String> {

    /**
     * 根据Id 进行模糊分页查询，MongoRepository接口通过解析方法名实现
     */
    Page<DataCenter> findByIdLike(String id, Pageable pageable);

    long countByIdLike(String id);
}