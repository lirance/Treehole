package com.oregonstate.edu.treehole.data.model;

import java.util.UUID;

public class Secret {
    public String secretId;
    public long time;
    public String content;
    public int likes;
    public int comments;

    public Secret() {
    }

    public Secret(String content) {
        this.secretId = UUID.randomUUID().toString();
        this.time = System.currentTimeMillis();
        this.content = content;
        this.likes = 0;
        this.comments = 0;
    }
}
