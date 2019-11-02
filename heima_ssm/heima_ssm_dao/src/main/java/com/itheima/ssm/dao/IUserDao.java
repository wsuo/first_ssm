package com.itheima.ssm.dao;

import com.itheima.ssm.domain.Role;
import com.itheima.ssm.domain.UserInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserDao {

    @Select("select * from users where username = #{username}")
    @ResultMap("userMap")
    public UserInfo findByUserName(String username) throws Exception;

    @Select("select * from users")
    public List<UserInfo> findAll() throws Exception;

    @Insert("insert into users(email,username,password,phoneNum,status) " +
            "values(#{email}, #{username}, #{password}, #{phoneNum}, #{status})")
    public void save(UserInfo userInfo);

    @Select("select * from users where id = #{userId}")
    @Results(
            id = "userMap",
            value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "email", column = "email"),
            @Result(property = "password", column = "password"),
            @Result(property = "phoneNum", column = "phoneNum"),
            @Result(property = "status", column = "status"),
            @Result(property = "roles", column = "id", javaType = java.util.List.class, many =
            @Many(select = "com.itheima.ssm.dao.IRoleDao.findRoleByUserId"))
    })
    public UserInfo findById(String userId);

    @Select("select * from role where id not in " +
            "(select roleId from users_role where userId=#{id})")
    List<Role> findOtherRoles(String id);

    @Insert("insert into users_role(userId,roleId) values(#{userId},#{roleId})")
    void addRoleToUser(
            @Param("userId") String userId,
            @Param("roleId") String roleId);
}
