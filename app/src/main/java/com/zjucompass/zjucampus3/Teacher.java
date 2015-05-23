package com.zjucompass.zjucampus3;

import com.zjucompass.zjucampus3.widget.ContactItemInterface;

public class Teacher implements ContactItemInterface {
    private String id;
    private String name;
    private String dept;
    private String title;
    private String phone;
    private String email;
    private String building;
    private String room;
    private String research;
    private String pinyinName;

    public Teacher(String id, String name, String dept, String title, String phone, String email,
                   String building, String room, String research, String pinyinName) {
        super();
        this.id = id;
        this.name = name;
        this.dept = dept;
        this.title = title;
        this.phone = phone;
        this.email = email;
        this.building = building;
        this.room = room;
        this.research = research;
        this.pinyinName = pinyinName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getResearch() {
        return research;
    }

    public void setResearch(String research) {
        this.research = research;
    }

    public String getPinyinName() {
        return pinyinName;
    }

    public void setPinyinName(String pinyinName) {
        this.pinyinName = pinyinName;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    @Override
    public String getItemForIndex() {
        return pinyinName;
    }

    @Override
    public String getDisplayInfo() {
        return name;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "dept='" + dept + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}' + '\n';
    }
}
