package com.kris.prophecy.script;

import redis.clients.jedis.Jedis;

/**
 * @author Kris
 * @date 2019/4/4
 */
public class InsertRedisScript {

    private final static String redisHost = "xxx";

    private final static String passwd = "xxx";

    public static void main(String[] args) {
        //实例化一个客户端
        Jedis jedis = new Jedis(redisHost);
        jedis.auth(passwd);
        //ping下，看看是否通的
        System.out.println("Server is running: " + jedis.ping());

        jedis.set("test", "Insert successfully");
        String value = jedis.get("test");
        System.out.println("插入Redis成功: " + "test" + ": ");
        System.out.println(value);
    }

}
