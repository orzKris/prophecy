package com.kris.prophecy.callable;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.constant.*;
import com.kris.prophecy.model.Result;
import com.kris.prophecy.enums.*;
import com.kris.prophecy.model.DispatchRequest;
import com.kris.prophecy.framework.ConcurrentCallable;
import com.kris.prophecy.framework.DispatchService;
import com.kris.prophecy.utils.AppendUrlUtil;
import com.kris.prophecy.utils.KeyUtil;
import com.kris.prophecy.utils.LogUtil;
import lombok.Data;
import okhttp3.*;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Kris
 * @date 2019/2/1
 */
@Component(ServiceCode.FACE_DETECTION)
@Scope("prototype")
@ConfigurationProperties(prefix = "face")
@Data
public class FaceDetectionCallable implements ConcurrentCallable {

    private JSONObject paramJson;

    private String url;

    private String apiKey;

    private String apiSecret;

    private String returnAttributes;

    private boolean isEnable = true;

    @Autowired
    DispatchService dispatchService;

    @Override
    public void init(JSONObject paramJson) {
        this.paramJson = paramJson;
    }

    @Override
    public Result checkParam(JSONObject paramJson) {
        if (!paramJson.containsKey(RequestConstant.FILE)) {
            return Result.fail("parameter file not present !");
        }
        return Result.success();
    }

    @Override
    public Result call() throws IOException {
        DateFormat df = new SimpleDateFormat(RequestConstant.DATE_FORMAT_DEFAULT);
        String requestTime = df.format(new Date());
        InputStream inputStream = (InputStream) paramJson.get(RequestConstant.FILE);
        String base64data = fileToBase64(inputStream, requestTime);
        try {
            return getData(base64data);
        } catch (Exception e) {
            LogUtil.logError(paramJson.getString(RequestConstant.UID), requestTime, base64data, "请求旷视人脸检测接口失败", e);
            return new Result(ResponseConstant.FAIL);
        }
    }

    private Result getData(String base64data) throws IOException {
        JSONObject queryString = new JSONObject();
        queryString.put(FaceDetectionConstant.API_KEY, apiKey);
        queryString.put(FaceDetectionConstant.API_SECRET, apiSecret);
        queryString.put(FaceDetectionConstant.SOURCE_ATTRIBUTES, returnAttributes);
        DispatchRequest dispatchRequest = DispatchRequest.builder()
                .key(KeyUtil.structureKeyString(base64data, ServiceIdEnum.D003.getId()))
                .request(getRequest(queryString, base64data))
                .requestParam(queryString)
                .dsId(ServiceIdEnum.D003.getId())
                .okHttpClient(new OkHttpClient())
                .isEnable(isEnable)
                .build();
        Result result = dispatchService.dispatchDatasource(dispatchRequest, true);
        JSONObject jsonResult = result.getJsonResult();
        jsonResult.remove(FaceDetectionConstant.IMAGE_ID);
        jsonResult.remove(FaceDetectionConstant.REQUEST_ID);
        int count = jsonResult.getJSONArray(FaceDetectionConstant.FACES).size();
        jsonResult.put(FaceDetectionConstant.COUNT, count);
        result.setJsonResult(jsonResult);
        return result;
    }

    private Request getRequest(JSONObject queryString, String base64data) {
        String datasourceUrl = AppendUrlUtil.getURL(queryString, url);
        Request request = new Request.Builder().url(datasourceUrl).post(new FormBody.Builder()
                .add(FaceDetectionConstant.IMAGE_BASE64, base64data)
                .build())
                .build();
        return request;
    }

    private String fileToBase64(InputStream inputStream, String requestTime) throws IOException {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        // 读取图片字节数组
        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = inputStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();
        } catch (IOException e) {
            LogUtil.logError(paramJson.getString(RequestConstant.UID), requestTime, inputStream.toString(), "base64 encode exception", e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return new String(Base64.encodeBase64(data));
    }
}

