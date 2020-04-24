package com.kris.prophecy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.kris.prophecy.entity.Post;
import com.kris.prophecy.service.impl.PostServiceImpl;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.kris.prophecy.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProphecyApplicationTests {

    private static final Integer MONTH = 30;

    /**
     * 连接linux服务器，并让服务器执行linux命令，启动logstash，并读取logstash运行日志。
     */
    @Test
    public void contextLoads() throws Exception {
        JSch jsch = new JSch();
        String userName = "xxx";
        String password = "xxx";
        String host = "192.168.3.4";
        int port = 22;
        String cmd = "cd /usr/share/logstash/bin/ && ./logstash -f /usr/share/logstash/bin/input-config/court-annocement/sync-mysql.conf";
        // 根据用户名，主机ip，端口获取一个Session对象
        Session session = jsch.getSession(userName, host, port);
        session.setPassword(password);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        // 为Session对象设置properties
        session.setConfig(config);
        int timeout = 7200000;
        session.setTimeout(timeout);
        // 通过Session建立链接
        session.connect();
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        channelExec.setCommand(cmd);
        channelExec.setInputStream(null);
        channelExec.setErrStream(System.err);
        channelExec.connect();
        InputStream in = channelExec.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
        String buf = null;
        //同步失败则微信告警
        while ((buf = reader.readLine()) != null) {
            if (buf.contains("FATAL") || buf.contains("ERROR")) {
                System.out.println("easy doing things");
            }
        }
        reader.close();
        channelExec.disconnect();
        session.disconnect();
    }

    /**
     * 理解 Class 类,Class类更像是一面镜子，用于反射,每个类只有一个class对象
     */
    @Test
    public void reflectionTest() {
        Class<Post> clazz = Post.class;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            System.out.println(field);
        }
    }

    /**
     * 利用反射获取一个类中的所有方法和该方法的参数
     */
    @Test
    public void methodTest() {
        Class<?> clazz = PostServiceImpl.class;
        //获取本类的所有方法，存放入数组
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            System.out.println("方法名：" + method.getName());
            //获取本方法所有参数类型，存入数组
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 0) {
                System.out.println("此方法无参数");
            }
            for (Class<?> c : parameterTypes) {
                String typeName = c.getName();
                System.out.println("参数类型：" + typeName);
            }
            System.out.println("****************************");
        }
    }

    /**
     * 像一段写在静态方法中的代码一样，只是，static中的代码在类加载时就会被执行，而静态方法是需要显示调用，并可以重复再次调用。
     */
    private static Map<String, String> map = new HashMap<>();

    static {
        map.put("1", "first");
        map.put("2", "second");
        map.put("3", "third");
        map.put("4", "fourth");
        map.put("5", "fifth");
        map.put("6", "sixth");
        map.put("7", "seventh");
    }

    @Test
    public void staticTest() {
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        Iterator<Map.Entry<String, String>> iterator1 = map.entrySet().iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while (iterator1.hasNext()) {
            Map.Entry<String, String> entry = iterator1.next();
            stringBuilder.append(entry.getValue() + ",");
        }
        System.out.println(stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1));

        List<String> list = new ArrayList<>();
        Iterator<Map.Entry<String, String>> iterator2 = map.entrySet().iterator();
        while (iterator2.hasNext()) {
            list.add(iterator2.next().toString());
        }
        /**
         * 很方便的把一个list的值拼成一个字符串
         */
        System.out.println(StringUtils.join(list, ";"));
    }

    /**
     * Thread线程测试sleep(),sleep()不会释放对象锁
     * synchronized 块是这样一个代码块，其中的代码必须获得对象 object （可以是类实例或类）的锁方能执行
     */
    public static class ThreadTest {

        private int i = 10;
        private Object object = new Object();

        class MyThread extends Thread {
            @Override
            public void run() {
                //参数改成this，结果不一样了，研究一下synchronized用法
                synchronized (object) {
                    i++;
                    System.out.println("i:" + i);
                    try {
                        System.out.println("线程" + Thread.currentThread().getName() + "进入睡眠状态");
                        Thread.currentThread().sleep(10000);
                    } catch (InterruptedException e) {
                        // TODO: handle exception
                    }
                    System.out.println("线程" + Thread.currentThread().getName() + "睡眠结束");
                    i++;
                    System.out.println("i:" + i);
                }
            }
        }

        public static void main(String[] args) {
            ThreadTest threadTest = new ThreadTest();
            MyThread thread1 = threadTest.new MyThread();
            MyThread thread2 = threadTest.new MyThread();
            thread1.start();
            thread2.start();
        }
    }

    @Test
    public void play() {
        Thread thread = new Thread();
        System.out.println(thread.getName());
        System.out.println(thread.getId());
        System.out.println(thread.getPriority());
        System.out.println(thread.getThreadGroup());
    }

    @Test
    public void format() {
        String entityName = "kris";
        String id = null;
        String m2 = String.format("entityName=%s,id=%s", entityName, id);
        System.out.println(m2);
    }

    @Test
    public void test1() {
        Random random = new Random();
        int math = random.nextInt(500);
        System.out.println(math);
        int num = (int) (Math.random() * 8 + 17);
        System.out.println(num);

    }

    /**
     * 关于try{...}catch(Exception e){...};
     * 如果只是捕获异常，不抛出异常，那么try catch将不会带来性能影响，而如果throw异常，则会带来很大的性能影响；
     * try catch的原理是（推测）：在编译的时候，try catch会在一个JDK的异常表里面插入捕获异常的起始位置，结束位置以及一些其他信息。
     * 而在程序抛出异常后，会用这个抛出异常的位置作为参数去异常表里查询，看是否需要捕获异常，如果没有，则去查看其父类引用是否需要捕获异常，依此类推。
     * 所以如果程序在运行过程中没有抛出异常，则不会去查询异常表，也不会带来性能消耗，try catch的范围大小同样不会成为影响性能的因素，try catch并不是会被执行的代码，
     * try catch更像是位置的标记，被标记的位置会被异常表存储起来。所以try catch的意义在哪呢？
     */
    @Test
    public void tryCatchTest() {

        /**
         * 这样try catch就完全没有起到catch的作用，而且还增加了性能消耗
         */
        try {
            String test = null;
            test.contains("aaa");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        /**
         * 这样try catch就使异常变得易于理解，降低了排查问题的成本
         */
        try {
            String test = null;
            test.contains("aaa");
        } catch (NullPointerException e) {
            LogUtil.logError("", "", "调用空对象的实例方法抛出空指针异常", e);
        }
    }
    /**
     * 以上总结
     * try catch的作用在于：付出（增加性能消耗）的代价，换得（使异常易于理解，降低排查问题成本）的好处
     */

    /**
     * 去掉一个字符串开头和结尾的空格
     */
    @Test
    public void trimToEmpty() {
        String s = "  su cc ess";
        System.out.println(s);
        System.out.println(s.trim());
    }

    @Test
    public void mongoTest() throws IOException {
        long start = System.currentTimeMillis();

        //新建Excel对象
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("调用量统计");
        HSSFRow beginRow = sheet.createRow(0);
        HSSFCell beginCell0 = beginRow.createCell(0);
        beginCell0.setCellValue("Collection");
        for (int i = 1; i <= MONTH; i++) {
            HSSFCell beginCell1 = beginRow.createCell(i);
            beginCell1.setCellValue("11月" + i + "日");
        }

        // 准备连接参数
        MongoClient client = null;
        MongoDatabase mongoDatabase = null;
        try {
            ServerAddress serverurl = new ServerAddress("114.55.10.121", 27020);
            List<ServerAddress> lists = new ArrayList<>();
            lists.add(serverurl);
            MongoCredential credential = MongoCredential.createCredential("crawler", "admin", "xxx".toCharArray());

            List<MongoCredential> listm = new ArrayList<MongoCredential>();
            listm.add(credential);
            client = new MongoClient(lists, listm);

            // 连接到数据库
            mongoDatabase = client.getDatabase("xxx");
            MongoIterable<String> collections = mongoDatabase.listCollectionNames();
            int index = 1;

            for (String collection : collections) {
                if ("system.profile".equals(collection)) {
                    continue;
                }
                //填写Excel第一列的Collection名
                HSSFRow row = sheet.createRow(index);
                HSSFCell cell = row.createCell(0);
                cell.setCellValue(collection);
                index = index + 1;
                long count = 0;

                for (int i = 1; i <= MONTH; i++) {
                    try {
                        System.out.print("11月" + i + "日" + collection + ":");
                        MongoCollection<Document> co = mongoDatabase.getCollection(collection);
                        Bson bson = Filters.and(Filters.gte("create_at.time", 1541001600000L + (i - 1) * 24 * 3600 * 1000),
                                Filters.lt("create_at.time", 1541088000000L + (i - 1) * 24 * 3600 * 1000));
                        count = co.count(bson);
                        HSSFCell countCell = row.createCell(i);
                        countCell.setCellValue(count);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println(count);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
        FileOutputStream output = null;
        try {
            output = new FileOutputStream("Mongo调用统计.xls");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        workbook.write(output);
        output.flush();
        System.out.println("耗费时间: " + (System.currentTimeMillis() - start) + " ms");
    }

    @Test
    public void excelTest() throws IOException {
        //新建Excel对象
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("调用量统计");
        HSSFRow beginRow = sheet.createRow(0);
        HSSFCell beginCell0 = beginRow.createCell(0);
        beginCell0.setCellValue("Collection");
        for (int i = 1; i <= MONTH; i++) {
            HSSFCell beginCell1 = beginRow.createCell(i);
            beginCell1.setCellValue("11月" + i + "日");
        }
        int index = 1;
        for (int j = 1; j <= MONTH; j++) {
            //填写Excel第一列的Collection名
            HSSFRow row = sheet.createRow(index);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(j);
            index = index + 1;
            for (int i = 1; i <= MONTH; i++) {
                try {
                    System.out.print("11月" + i + "日" + j + ":");
                    HSSFCell countCell = row.createCell(i);
                    countCell.setCellValue(j);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(j);
            }
        }
        FileOutputStream output = null;
        try {
            output = new FileOutputStream("Mongo调用统计.xls");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        workbook.write(output);
        output.flush();
    }

    /**
     * 注意在任何情况下，都要注意：调用一个对象的实例方法，一定要去考虑该对象为null的情况，这是会出现空指针异常的
     */
    @Test
    public void test() {
        JSONObject test = JSONObject.parseObject("{\"text\":\"胡琰\"}");
        System.out.println(test.toString());
        try {
            JSONObject jsonObject = null;
            System.out.println(jsonObject.toString());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        String s = null;
        System.out.println("123".equals(s));
    }

    /**
     * JSONArray格式验证
     */
    @Test
    public void jsonArrayTest() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add("125315");
        jsonArray.add("912462946");
        System.out.println(jsonArray.toJSONString());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("kris", "ok");
        jsonArray.add(jsonObject);
        System.out.println(jsonArray.toJSONString());
    }

    /**
     * fastjson在toString的时候会把值为null的key丢失,如果要保留则需要用到fastjson的序列化属性
     * QuoteFieldNames—输出key时是否使用双引号,默认为true
     * WriteMapNullValue—是否输出值为null的字段,默认为false
     * WriteNullNumberAsZero—数值字段如果为null,输出为0,而非null
     * WriteNullListAsEmpty—List字段如果为null,输出为[],而非null
     * WriteNullStringAsEmpty—字符类型字段如果为null,输出为”“,而非null
     * WriteNullBooleanAsFalse—Boolean字段如果为null,输出为false,而非null
     */
    @Test
    public void toStringTest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", null);
        jsonObject.put("lock", "history");
        String json = JSON.toJSONString(jsonObject, SerializerFeature.WriteMapNullValue);
        System.out.println(jsonObject.toString());
        System.out.println(json);
        System.out.println(JSON.toJSONString(JSON.parseObject(json), SerializerFeature.WriteMapNullValue));
    }

    @Test
    public void JsonToMap() {
        JSONObject obj = new JSONObject();
        {
            obj.put("key1", "value1");
            obj.put("key2", "value2");
            obj.put("key3", "value3");
        }
        Map<String, String> params = JSONObject.parseObject(obj.toJSONString(), new TypeReference<Map<String, String>>() {
        });
        System.out.println(params);
    }
    /*------------------------------------------------------------*/
    /* exam tests */

    /**
     * 计算字符串最后一个单词的长度，单词以空格隔开。
     */
    @Test
    public void lengthOfWord() {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        String[] array = s.split(" ");
        int length = array[array.length - 1].length();
        System.out.println(length);
    }

    /**
     * 写出一个程序，接受一个由字母和数字组成的字符串，和一个字符，然后输出输入字符串中含有该字符的个数。不区分大小写。
     */
    @Test
    public void charCount() {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine().toLowerCase();
        Character c = Character.toLowerCase(scanner.nextLine().charAt(0));
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (c.equals(s.charAt(i))) {
                count = count + 1;
            }
        }
        System.out.println(count);
    }

    /**
     * 明明想在学校中请一些同学一起做一项问卷调查，为了实验的客观性，
     * 他先用计算机生成了N个1到1000之间的随机整数（N≤1000），对于其中重复的数字，只保留一个，
     * 把其余相同的数去掉，不同的数对应着不同的学生的学号。然后再把这些数从小到大排序，
     * 按照排好的顺序去找同学做调查。请你协助明明完成“去重”与“排序”的工作。
     */
    @Test
    public void numberSort() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            TreeSet<Integer> treeSet = new TreeSet<>();
            int count = scanner.nextInt();
            for (int i = 0; i < count; i++) {
                treeSet.add(scanner.nextInt());
            }
            treeSet.forEach(System.out::println);
        }
    }

    /**
     * 连续输入字符串，请按长度为8拆分每个字符串后输出到新的字符串数组；
     * 长度不是8整数倍的字符串请在后面补数字0，空字符串不处理。
     */
    @Test
    public void cutWords() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String s = scanner.nextLine();
            while (s.length() > 8) {
                System.out.println(s.substring(0, 8));
                s = s.substring(8);
            }
            if (s.length() != 0) {
                s = s + "00000000";
                System.out.println(s.substring(0, 8));
            }
        }
    }

    /**
     * 写出一个程序，接受一个十六进制的数，输出该数值的十进制表示。（多组同时输入 ）
     */
    @Test
    public void hexToTen() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String hex = scanner.nextLine().substring(2);
            int count = 0;
            while (hex.length() > 0) {
                int decimal = hexToDecimal1(hex.substring(0, 1));
                int power = new Double(Math.pow(16, hex.length() - 1)).intValue();
                count = count + decimal * power;
                hex = hex.substring(1);
            }
            System.out.println(count);
        }
    }

    private static int hexToDecimal1(String s) {
        switch (s) {
            case "A":
                return 10;
            case "B":
                return 11;
            case "C":
                return 12;
            case "D":
                return 13;
            case "E":
                return 14;
            case "F":
                return 15;
            default:
                return Integer.parseInt(s);
        }
    }

    /**
     * 功能:输入一个正整数，按照从小到大的顺序输出它的所有质因子（如180的质因子为2 2 3 3 5 ）
     * 最后一个数后面也要有空格
     */
    @Test
    public void primeFactor() {
        Scanner scanner = new Scanner(System.in);
        long number = scanner.nextLong();
        StringBuilder stringBuilder = new StringBuilder();
        int i = 2;
        while (i <= Math.sqrt(number)) {
            if (number % i == 0) {
                stringBuilder.append(i).append(" ");
                number = number / i;
            } else {
                i++;
            }
        }
        stringBuilder.append(number).append(" ");
        System.out.println(stringBuilder.toString());
    }

    /**
     * 写出一个程序，接受一个正浮点数值，输出该数值的近似整数值。如果小数点后数值大于等于5,向上取整；小于5，则向下取整
     */
    @Test
    public void getSimilar() {
        Scanner scanner = new Scanner(System.in);
        double d = scanner.nextDouble();
        String[] numbers = String.valueOf(d).split("\\.");
        if (numbers[1].charAt(0) - '0' >= 5) {
            System.out.println(Integer.parseInt(numbers[0]) + 1);
        } else {
            System.out.println(Integer.parseInt(numbers[0]));
        }
    }

    /**
     * 先输入键值对的个数
     * 然后输入成对的index和value值，以空格隔开
     * 输出合并后的键值对（多行）
     */
    @Test
    public void mapSort() {
        Scanner scanner = new Scanner(System.in);
        int count = Integer.parseInt(scanner.nextLine());
        TreeMap<Integer, Integer> map = new TreeMap<>();
        int loopTimes = 0;
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] numbers = line.split(" ");
            int key = Integer.parseInt(numbers[0]);
            int value = Integer.parseInt(numbers[1]);
            if (map.containsKey(key)) {
                map.put(key, map.get(key) + value);
            } else {
                map.put(key, value);
            }
            loopTimes++;
            if (loopTimes == count) {
                break;
            }
        }
        map.forEach((key, value) -> System.out.println(key + " " + value));
        scanner.close();
    }

    /**
     * 输入一个int型整数，按照从右向左的阅读顺序，返回一个不含重复数字的新的整数
     */
    @Test
    public void reverse1() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String s = scanner.nextLine();
            int len = s.length();
            int[] arr1 = new int[10];
            for (int i = len - 1; i >= 0; i--) {
                if (arr1[s.charAt(i) - '0'] == 0) {
                    System.out.print(s.charAt(i) - '0');
                    arr1[s.charAt(i) - '0']++;
                }
            }
        }
    }

    @Test
    public void reverse2() {
        Scanner scan = new Scanner(System.in);
        String str = scan.nextLine();
        StringBuilder sb = new StringBuilder(str);
        Set<String> s = new HashSet<String>();
        sb.reverse();//字符串反转
        for (int i = 0; i < sb.length(); i++) {
            if (s.add(sb.substring(i, i + 1))) {//set不允许重复添加相同的元素
                System.out.print(sb.substring(i, i + 1));
            }
        }
    }

    /**
     * 强迫症卖家
     */
    @Test
    public void sold() {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        BigDecimal decimal = new BigDecimal(s);
        int N = 1;
        BigDecimal distance = getDistance(1, decimal);
        for (int i = 2; i <= 10000; i++) {
            BigDecimal newDistance = getDistance(i, decimal);
            if (newDistance.compareTo(distance) < 0) {
                N = i;
                distance = newDistance;
            }
        }
        BigDecimal nDecimal = new BigDecimal(String.valueOf(N));
        int M = nDecimal.multiply(decimal).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        System.out.println(M + " " + N);
    }

    private static BigDecimal getDistance(int i, BigDecimal bigDecimal) {
        BigDecimal finalResult;
        BigDecimal iDecimal = new BigDecimal(String.valueOf(i));
        BigDecimal mulResult = iDecimal.multiply(bigDecimal);
        BigDecimal integralPart = mulResult.setScale(0, BigDecimal.ROUND_DOWN);
        BigDecimal factionalPart = mulResult.subtract(integralPart);
        BigDecimal flag = new BigDecimal("0.5");
        BigDecimal full = new BigDecimal("1");
        if (factionalPart.compareTo(flag) > 0) {
            finalResult = full.subtract(factionalPart);
        } else {
            finalResult = factionalPart;
        }
        return finalResult;
    }

    /**
     * 报文转义
     */
    @Test
    public void messageTransfer() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        List<String> list = new ArrayList<>(Arrays.asList(input.split(" ")));
        for (int i = 1; i < list.size(); i++) {
            if ("A".equals(list.get(i))) {
                list.set(i, "12 34");
                int count = hexToDecimal(list.get(0));
                list.set(0, intToHex(count + 1));
            } else if ("B".equals(list.get(i))) {
                list.set(i, "AB CD");
                int count = hexToDecimal(list.get(0));
                list.set(0, intToHex(count + 1));
            }
        }
        list.forEach(i -> System.out.print(i + " "));
    }

    private static int hexToDecimal(String hex) {
        int outcome = 0;
        for (int i = 0; i < hex.length(); i++) {
            char hexChar = hex.charAt(i);
            outcome = outcome * 16 + charToDecimal(hexChar);
        }
        return outcome;
    }

    private static int charToDecimal(char c) {
        if (c >= 'A' && c <= 'F')
            return 10 + c - 'A';
        else
            return c - '0';
    }

    private static String intToHex(int n) {
        StringBuilder sb = new StringBuilder(8);
        String a;
        char[] b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        while (n != 0) {
            sb.append(b[n % 16]);
            n = n / 16;
        }
        a = sb.reverse().toString();
        return a;
    }

    /**
     * 判断数字序列是否合法
     */
    @Test
    public void judge() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String s = scanner.nextLine();
            if ("".equals(s)) {
                continue;
            }
            List<String> list = new ArrayList<>(Arrays.asList(s.split(" ")));
            if (list.get(0).length() == 1 && list.get(list.size() - 1).length() == 1) {
                int flag1 = 0;
                for (int i = 1; i < list.size() - 1; i++) {
                    if (list.get(i).length() == 1) {
                        flag1++;
                        break;
                    }
                }
                if (flag1 == 0) {
                    System.out.print("true ");
                } else {
                    System.out.print("false ");
                }
            } else if (list.get(0).length() == 2 && list.get(list.size() - 1).length() == 2) {
                int flag2 = 0;
                for (int i = 1; i < list.size() - 1; i++) {
                    if (list.get(i).length() == 2) {
                        flag2++;
                        break;
                    }
                }
                if (flag2 == 0) {
                    System.out.print("true ");
                } else {
                    System.out.print("false ");
                }
            } else {
                int flag3 = 0;
                for (int i = 0; i < list.size() - 1; i++) {
                    if ((i + list.get(i).length()) % 2 != (i + 1 + list.get(i + 1).length()) % 2) {
                        flag3++;
                        break;
                    }
                }
                if (flag3 == 0) {
                    System.out.print("true ");
                } else {
                    System.out.print("false ");
                }
            }
        }
    }

    /**
     * 从数组中取出n个数的所有组合
     * 递归
     */
    @Test
    public void combine() {
        int[] array = {1, 2, 3};
        int k = 2;
        combine(0, k, array);
    }

    private static ArrayList<Integer> tmpArr = new ArrayList<>();

    private static void combine(int index, int k, int[] arr) {
        if (k == 1) {
            for (int i = index; i < arr.length; i++) {
                tmpArr.add(arr[i]);
                System.out.println(tmpArr.toString());
                tmpArr.remove((Object) arr[i]);
            }
        } else if (k > 1) {
            for (int i = index; i <= arr.length - k; i++) {
                tmpArr.add(arr[i]);
                combine(i + 1, k - 1, arr);
                tmpArr.remove((Object) arr[i]);
            }
        }
    }
}





