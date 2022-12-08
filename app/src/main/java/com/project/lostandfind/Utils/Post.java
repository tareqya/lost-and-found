package com.project.lostandfind.Utils;

public class Post extends Uid implements Comparable<Post>{
    private String title;
    private ItemLocation location;
    private String imgUrl;
    private long createTime;
    private String userUid;

    public Post(){}

    public String getUserUid() {
        return userUid;
    }

    public Post setUserUid(String userUid) {
        this.userUid = userUid;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Post setTitle(String title) {
        this.title = title;
        return this;
    }

    public ItemLocation getLocation() {
        return location;
    }

    public Post setLocation(ItemLocation location) {
        this.location = location;
        return this;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public Post setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
        return this;
    }

    public long getCreateTime() {
        return createTime;
    }

    public Post setCreate_time(long createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public int compareTo(Post o) {
        if(this.createTime > o.getCreateTime())
            return 1;
        if(this.createTime < o.getCreateTime())
            return -1;
        return 0;

    }
}
