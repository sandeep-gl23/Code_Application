package com.example.developer1.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "fields")

@CompoundIndex(name = "code_version_idx", def = "{'code': 1, 'version': 1}", unique = true)

public class Field {
    @Id
    String id;
//    @Indexed(unique = true)
    String code;
    String name;
    String desc;
    Date start_date;
    Date end_date;
    Boolean status=false;
    Double version;

    public Field() {
    }

    public Field(String id, String code, String name, String desc, Date start_date, Date end_date, Boolean status, Double version) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.desc = desc;
        this.start_date = start_date;
        this.end_date = end_date;
        this.status = status;
        this.version = version;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }


    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Date getStart_date() {
        return start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public Boolean getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Field{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                ", status=" + status +
                ", version='" + version + '\'' +
                '}';
    }
}
