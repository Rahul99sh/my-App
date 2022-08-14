package com.codefury.starthub;

public class post {
    String uid;
    String desc;
    String uri;

    public post(){}

    public post(String uid, String desc, String uri) {
        this.uid = uid;
        this.desc = desc;
        this.uri = uri;
    }



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }


}
