package com.kris.prophecy.entity;


public class User {
    private Integer id;

    private String name;

    private Integer age;

    private String password;

    private String sex;

    private String school;

    private String subordinateClass;

    private Integer graduationTime;

    private String  registerTime;

    private String uid;

    private Integer balance;

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school == null ? null : school.trim();
    }

    public String getSubordinateClass() {
        return subordinateClass;
    }

    public void setSubordinateClass(String subordinateClass) {
        this.subordinateClass = subordinateClass == null ? null : subordinateClass.trim();
    }

    public Integer getGraduationTime() {
        return graduationTime;
    }

    public void setGraduationTime(Integer graduationTime) {
        this.graduationTime = graduationTime;
    }

    public String getRegisterTime() {
        try {
            return registerTime.split("\\.")[0];
        }catch(Exception e){
            return registerTime;
        }
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
    }
}