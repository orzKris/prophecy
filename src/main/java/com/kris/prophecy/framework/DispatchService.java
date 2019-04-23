package com.kris.prophecy.framework;

import com.kris.prophecy.model.Result;
import com.kris.prophecy.model.DispatchRequest;

import java.io.IOException;

/**
 * @author Kris
 * @date 2019/2/1
 */
public interface DispatchService {

    /**
     * 服务调度
     *
     * @param req
     * @return
     */
    Result dispatch(DispatchRequest req, boolean isParsed) throws IOException;

    Result dispatchDatasource(DispatchRequest req, boolean isParsed) throws IOException;
}
