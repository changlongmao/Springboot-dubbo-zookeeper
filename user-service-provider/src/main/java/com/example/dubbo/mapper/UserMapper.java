package com.example.dubbo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dubbo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 查询list集合
     *
     * @return
     */
    List<User> queryUserList();

    void batchInsert(List<User> list);

    void updateUserById(User user);

    void updateUserByName(User user);

    User getById(String id);

    List<Map<String, Object>> getTableName(Map<String, Object> params);

    void optimizeTable(@Param("tableName") String tableName);

    List<Map<String, Object>> getDatabaseName(Map<String, Object> params);
}
