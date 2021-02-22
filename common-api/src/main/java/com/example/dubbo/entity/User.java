package com.example.dubbo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class User implements Serializable {

    private String id;
    private String username;
    /**
     * 真实姓名
     */
    private String rearName;
    private String password;
    private Date createTime;

    @TableLogic
    @JsonIgnore
    private Integer isDelete;

    public User() {
    }

    public User(String id) {
        this.id = id;
    }

    public User(String id, String username, String rearName) {
        this.id = id;
        this.username = username;
        this.rearName = rearName;
    }


}
