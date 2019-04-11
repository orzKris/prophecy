package com.kris.prophecy.callable;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.model.Result;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;


@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class LanguageDetectCallableTest {

    private JSONObject param = new JSONObject();

    @InjectMocks
    LanguageDetectCallable languageDetectCallable;

    @Before
    public void init() {
        param.put("text", "ILoveYou");
        ReflectionTestUtils.setField(languageDetectCallable, "url", "https://fanyi.baidu.com/langdetect");
        languageDetectCallable.init(param);
    }

    @Test
    public void checkParamSuccess() {
        Result result = languageDetectCallable.checkParam(param);
        Assert.assertEquals("00", result.getStatus());
    }

    @Test
    public void checkParamFail() {
        param.put("text", 123);
        Result result = languageDetectCallable.checkParam(param);
        Assert.assertEquals("02", result.getStatus());
    }

    @Test
    public void callEnglish() {
        Result result = languageDetectCallable.call();
        System.out.println(result.getJsonResult());
        Assert.assertEquals("00", result.getStatus());
    }

    @Test
    public void callChinese() {
        param.put("text", "我爱你");
        languageDetectCallable.init(param);
        Result result = languageDetectCallable.call();
        System.out.println(result.getJsonResult());
        Assert.assertEquals("00", result.getStatus());
    }

    @Test
    public void callJapanese() {
        param.put("text", "あなたのことが好きです");
        languageDetectCallable.init(param);
        Result result = languageDetectCallable.call();
        System.out.println(result.getJsonResult());
        Assert.assertEquals("00", result.getStatus());
    }

    @Test
    public void callFrench() {
        param.put("text", "Comté");
        languageDetectCallable.init(param);
        Result result = languageDetectCallable.call();
        System.out.println(result.getJsonResult());
        Assert.assertEquals("00", result.getStatus());
    }

    @Test
    public void callRussian() {
        param.put("text", "Ялюблютебя");
        languageDetectCallable.init(param);
        Result result = languageDetectCallable.call();
        System.out.println(result.getJsonResult());
        Assert.assertEquals("00", result.getStatus());
    }

    @Test
    public void callSpanish() {
        param.put("text", "Español");
        languageDetectCallable.init(param);
        Result result = languageDetectCallable.call();
        System.out.println(result.getJsonResult());
        Assert.assertEquals("00", result.getStatus());
    }

    @Test
    public void callKorean() {
        param.put("text", "사랑해");
        languageDetectCallable.init(param);
        Result result = languageDetectCallable.call();
        System.out.println(result.getJsonResult());
        Assert.assertEquals("00", result.getStatus());
    }

    @Test
    public void callArabic() {
        param.put("text", "أناأحبك");
        languageDetectCallable.init(param);
        Result result = languageDetectCallable.call();
        System.out.println(result.getJsonResult());
        Assert.assertEquals("00", result.getStatus());
    }

    @Test
    public void callUnknown() {
        param.put("text", "Αραβικά");
        languageDetectCallable.init(param);
        Result result = languageDetectCallable.call();
        System.out.println(result.getJsonResult());
        Assert.assertEquals("00", result.getStatus());
    }

    @Test
    public void callException() {
        ReflectionTestUtils.setField(languageDetectCallable, "url", "https://fanyi.baidu.com/langdetec");
        param.put("text", "hello");
        languageDetectCallable.init(param);
        Result result = languageDetectCallable.call();
        System.out.println(result.getJsonResult());
        Assert.assertEquals("01", result.getStatus());
    }
}