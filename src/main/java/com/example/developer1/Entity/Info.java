package com.example.developer1.Entity;

public class Info {
    String code;
    Double version;



    public Info(String code, Double version) {
        this.code = code;
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }

    public Info() {
    }

    @Override
    public String toString() {
        return "Info{" +
                "code='" + code + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
