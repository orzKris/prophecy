package com.kris.prophecy.script;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bson.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Kris
 * @date 2019/5/22
 */
public class InsertMongoScript {

    private static final String COLLECTION_NAME = "xxx";

    private static final String API_NAME = "xxx";

    public static Request getRequest(JSONObject param) throws UnsupportedEncodingException {
        return new Request.Builder()
                .url("xxx?param=" + URLEncoder.encode(param.toJSONString(), "UTF-8"))
                .header("uid", "xxx")
                .header("clientId", "personal")
                .build();
    }

    public static void main(String[] args) throws IOException {
        JSONObject param = JSONObject.parseObject("{\"card\":\"440825197010150016\",\"id_num\":\"371423198008144496\",\"mobile\":\"15164919490\",\"entity_name\":\"张一\"}");
        Request request = getRequest(JSONObject.parseObject("{\"bank_card\":\"440825197010150016\",\"id\":\"371423198008144496\",\"mobile\":\"15164919490\",\"name\":\"张一\"}"));

        Call call = new OkHttpClient.Builder().callTimeout(10000, TimeUnit.MILLISECONDS).build().newCall(request);
        Response response = call.execute();
        String responseBody = response.body().string();
        JSONObject jsonResult = JSONObject.parseObject(responseBody).getJSONObject("result").getJSONObject(API_NAME);
        jsonResult.put("api_name", COLLECTION_NAME);
        jsonResult.put("data_src", "wecash");
        jsonResult.put("_id", UUID.randomUUID().toString().replace("-", ""));
        jsonResult.put("create_at", new Date());
        jsonResult.putAll(param);
        MongoClient client;
        MongoDatabase mongoDatabase;
        try {
            ServerAddress serverurl = new ServerAddress("192.168.4.131", 27017);
            List<ServerAddress> lists = new ArrayList<>();
            lists.add(serverurl);
            MongoCredential credential = MongoCredential.createCredential("xxx", "xxx", "xxx".toCharArray());
            client = new MongoClient(lists, credential, new MongoClientOptions.Builder().build());

            // 连接到数据库
            mongoDatabase = client.getDatabase("xxx");
            MongoCollection<Document> collection = mongoDatabase.getCollection(COLLECTION_NAME);
            System.out.println(collection.count());
            collection.insertOne(Document.parse(jsonResult.toJSONString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}