package com.kris.prophecy.utils;

import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author by Kris on 8/28/2018.
 * 自定义分页数据
 */
public class PageData<T> extends PageInfo<T> {
    private Map other = new ConcurrentHashMap();

    public PageData() {
    }

    public PageData(List<T> list) {
        super(list, 8);
    }

    public PageData(List<T> list, int navigatePages) {
        super(list, navigatePages);
    }

    public Map getOther() {
        return other;
    }

    public void addOther(String key,Object object) {
        other.put(key,object);
    }
}
