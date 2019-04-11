package com.kris.prophecy.callable;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.enums.ResponseConstant;
import com.kris.prophecy.model.Result;
import com.kris.prophecy.framework.DispatchService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class AddressResolutionCallableTest {

    private JSONObject param = new JSONObject();

    @Mock
    DispatchService dispatchService;

    @InjectMocks
    AddressResolutionCallable addressResolutionCallable;

    @Before
    public void init() {
        param.put("longitude", "119.1");
        param.put("latitude", "39.9");
        ReflectionTestUtils.setField(addressResolutionCallable, "url", "http://apis.juhe.cn/geo");
        ReflectionTestUtils.setField(addressResolutionCallable, "key", "xxx");
        ReflectionTestUtils.setField(addressResolutionCallable, "isEnable", true);
        addressResolutionCallable.init(param);
    }

    @Test
    public void checkParamSuccess() {
        Result result = addressResolutionCallable.checkParam(param);
        Assert.assertEquals("00", result.getStatus());
    }

    @Test
    public void checkParamFail1() {
        param.put("longitude", "asd");
        Result result = addressResolutionCallable.checkParam(param);
        Assert.assertEquals("02", result.getStatus());
    }

    @Test
    public void checkParamFail2() {
        param.put("latitude", "asd");
        Result result = addressResolutionCallable.checkParam(param);
        Assert.assertEquals("02", result.getStatus());
    }

    @Test
    public void callSuccess1() throws IOException {
        JSONObject jsonObject = new JSONObject();
        JSONObject json = new JSONObject();
        jsonObject.put("result", json);
        Result result = new Result(ResponseConstant.SUCCESS);
        result.setJsonResult(jsonObject);
        when(dispatchService.dispatch(any(), any())).thenReturn(result);
        Result resultActual = addressResolutionCallable.call();
        Assert.assertEquals("00", resultActual.getStatus());
    }

    @Test
    public void callSuccess2() throws IOException {
        JSONObject jsonObject = new JSONObject();
        JSONObject json = new JSONObject();
        json.put("address", "New York");
        jsonObject.put("result", json);
        Result result = new Result(ResponseConstant.SUCCESS);
        result.setJsonResult(jsonObject);
        when(dispatchService.dispatch(any(), any())).thenReturn(result);
        Result resultActual = addressResolutionCallable.call();
        Assert.assertEquals("00", resultActual.getStatus());
    }

    @Test
    public void callWhenThrowException() throws IOException {
        JSONObject jsonObject = new JSONObject();
        JSONObject json = new JSONObject();
        json.put("address", "New York");
        jsonObject.put("result", json);
        Result result = new Result(ResponseConstant.SUCCESS);
        result.setJsonResult(jsonObject);
        doThrow(new RuntimeException()).when(dispatchService).dispatch(any(), any());
        Result resultActual = addressResolutionCallable.call();
        Assert.assertEquals("01", resultActual.getStatus());
    }
}