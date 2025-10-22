package com.example.fetchbeebackend.mapper;

import com.example.fetchbeebackend.entity.BalanceRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 余额记录Mapper接口
 */
@Mapper
public interface BalanceRecordMapper {
    
    /**
     * 插入余额记录
     */
    int insert(BalanceRecord record);
    
    /**
     * 根据用户ID查询余额记录列表
     */
    List<BalanceRecord> findByUserId(@Param("userId") Long userId);
}

