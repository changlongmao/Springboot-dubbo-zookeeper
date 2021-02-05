package com.example.dubbo.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SystemLog implements Serializable {

    private String id;

    private String module;

    private String userName;

    private String userId;

    private String method;

    private String operation;

    private String args;

    private Integer time;

    private String ip;

    private Date createTime;

}