package com.oregonstate.edu.treehole.data.model;

import java.io.Serializable;
import java.util.UUID;

public class Secret implements Serializable {
    public String secretId;
    public String userId;
    public long time;
    public String content;
    public int likes;
    public int comments;

    public Secret() {
    }

    public Secret(String content, String userId) {
        this.userId = userId;
        this.secretId = UUID.randomUUID().toString();
        this.time = System.currentTimeMillis();
        this.content = content;
        this.likes = 0;
        this.comments = 0;
    }
}
