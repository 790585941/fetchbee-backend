package com.example.fetchbeebackend.mapper;

import com.example.fetchbeebackend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据用户名查询用户
     */
    User findByUsername(@Param("username") String username);
    
    /**
     * 根据ID查询用户
     */
    User findById(@Param("id") Long id);
    
    /**
     * 插入用户
     */
    int insert(User user);
    
    /**
     * 更新用户信息
     */
    int update(User user);
    
    /**
     * 更新密码
     */
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    
    /**
     * 更新余额
     */
    int updateBalance(@Param("id") Long id, @Param("balance") BigDecimal balance);

    /**
     * 提交学生认证
     */
    int updateVerificationSubmit(@Param("id") Long id, @Param("verificationImage") String verificationImage);

    /**
     * 审核学生认证
     */
    int updateVerificationReview(@Param("id") Long id, @Param("verificationStatus") Integer verificationStatus,
                                  @Param("verificationRemark") String verificationRemark);

    /**
     * 根据认证状态查询用户列表
     */
    java.util.List<User> findByVerificationStatus(@Param("verificationStatus") Integer verificationStatus);
}

