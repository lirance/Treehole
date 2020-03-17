package com.oregonstate.edu.treehole.data.model;

import java.util.Date;
import java.util.UUID;

public class Reply {
    public String message;
    public long time;
    public String replyId;

    public Reply() {
    }

    public Reply(String message) {
        this.replyId = UUID.randomUUID().toString();
        this.message = message;
        this.time = System.currentTimeMillis();

    }
}
