package com.itheima.ssm.dao;

import com.itheima.ssm.domain.Member;
import com.itheima.ssm.domain.Orders;
import com.itheima.ssm.domain.Product;
import com.itheima.ssm.domain.Traveller;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IOrdersDao {

    @Select("select * from orders")
    @Results(
            id = "ordersMap",
            value = {
                    @Result(id = true, property = "id", column = "id"),
                    @Result(property = "orderNum", column = "orderNum"),
                    @Result(property = "orderTime", column = "orderTime"),
                    @Result(property = "orderStatus", column = "orderStatus"),
                    @Result(property = "peopleCount", column = "peopleCount"),
                    @Result(property = "payType", column = "payType"),
                    @Result(property = "orderDesc", column = "orderDesc"),
                    @Result(property = "product", column = "productId", javaType = Product.class, one = @One(select = "com.itheima.ssm.dao.IProductDao.findById"))
            })
    public List<Orders> findAll() throws Exception;

    //多表操作
    @Select("select * from orders where id = #{ordersId}")
    @Results(
            {
                    @Result(id = true, property = "id", column = "id"),
                    @Result(property = "orderNum", column = "orderNum"),
                    @Result(property = "orderTime", column = "orderTime"),
                    @Result(property = "orderStatus", column = "orderStatus"),
                    @Result(property = "peopleCount", column = "peopleCount"),
                    @Result(property = "payType", column = "payType"),
                    @Result(property = "orderDesc", column = "orderDesc"),
                    @Result(property = "product", column = "productId", javaType = Product.class, one =
                    @One(select = "com.itheima.ssm.dao.IProductDao.findById")),
                    @Result(property = "member", column = "memberId", javaType = Member.class, one =
                    @One(select = "com.itheima.ssm.dao.IMemberDao.findById")),

                    //多对多,通过中间表查询,所以column应该是id, id 传给后面的select
                    @Result(property = "travellers", column = "id", javaType = java.util.List.class, many =
                    @Many(select = "com.itheima.ssm.dao.ITravellerDao.findByOrdersId"))
            })
    public Orders findById(String ordersId) throws Exception;
}
