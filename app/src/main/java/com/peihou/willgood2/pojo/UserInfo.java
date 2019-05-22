package com.peihou.willgood2.pojo;

public class UserInfo {
    private int userId;
    private String userName;
    private String phone;
    private String password;
    private String address;
    private String headImgUrl;
    private int creatorId;
    private String sharerName;

    public String getSharerName() {
        return sharerName;
    }

    public void setSharerName(String sharerName) {
        this.sharerName = sharerName;
    }

    private int role;

    public UserInfo() {
    }

    public UserInfo(int userId, String userName, String phone, String password, String address, String headImgUrl, int creatorId, int role) {
        this.userId = userId;
        this.userName = userName;
        this.phone = phone;
        this.password = password;
        this.address = address;
        this.headImgUrl = headImgUrl;
        this.creatorId = creatorId;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String username) {
        this.userName = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
