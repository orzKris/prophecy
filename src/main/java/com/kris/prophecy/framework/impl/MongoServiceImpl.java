package com.kris.prophecy.framework.impl;

import com.kris.prophecy.model.DataCenter;
import com.kris.prophecy.framework.DataCenterService;
import com.kris.prophecy.framework.MongoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * @author Kris
 * @date 2019/3/13
 */
@Service
@Log4j2
public class MongoServiceImpl implements MongoService {

    @Autowired
    DataCenterService dataCenterService;

    /**
     * 插入数据
     * 如果存在相同主键数据，则更新
     */
    @Override
    @Async
    public void asyncInsert(DataCenter dataCenter) {
        try {
            dataCenterService.save(dataCenter);
            log.info("Success to asyncInsert mongo, result: {}", dataCenter.toString());
        } catch (Exception e) {
            log.info("Fail to asyncInsert mongo");
        }
    }

    /**
     * 根据id查找数据
     */
    @Override
    public DataCenter findById(String id) {
        Optional<DataCenter> optional = dataCenterService.findById(id);
        return optional.orElseGet(DataCenter::new);
    }

    /**
     * 分页查询所有记录，按时间倒序输出
     */
    @Override
    public Page<DataCenter> findAll(Integer page, Integer pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createAt");
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        return dataCenterService.findAll(pageable);
    }

    /**
     * 根据Id 分页模糊查询
     */
    @Override
    public Page<DataCenter> findByIdLike(String id, Integer page, Integer pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createAt");
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        return dataCenterService.findByIdLike(id, pageable);
    }

    /**
     * 根据id删除数据
     */
    @Override
    public void deleteById(String id) {
        try {
            dataCenterService.deleteById(id);
            log.info("Success to delete from mongo, id: {}", id);
        } catch (Exception e) {
            log.info("Fail to delete from mongo");
        }
    }

    /**
     * 统计所有文档的数量
     */
    @Override
    public long count() {
        return dataCenterService.count();
    }

    /**
     * 根据Id 统计文档的数量
     */
    @Override
    public long countByIdLike(String id) {
        return dataCenterService.countByIdLike(id);
    }
}