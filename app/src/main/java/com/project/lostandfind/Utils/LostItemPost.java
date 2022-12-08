package com.project.lostandfind.Utils;

public class LostItemPost implements Comparable<LostItemPost>{

    private Post post;
    private User user;

    public LostItemPost(){}

    public Post getPost() {
        return post;
    }

    public LostItemPost setPost(Post post) {
        this.post = post;
        return this;
    }

    public User getUser() {
        return user;
    }

    public LostItemPost setUser(User user) {
        this.user = user;
        return this;
    }

    @Override
    public int compareTo(LostItemPost o) {
        return this.getPost().compareTo(o.getPost());
    }
}
