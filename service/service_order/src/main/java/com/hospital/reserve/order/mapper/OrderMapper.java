package com.hospital.reserve.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.reserve.model.order.OrderInfo;
import com.hospital.reserve.vo.order.OrderCountQueryVo;
import com.hospital.reserve.vo.order.OrderCountVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper extends BaseMapper <OrderInfo> {

    List<OrderCountVo> selectOrderCount(@Param("vo") OrderCountQueryVo orderCountQueryVo);
}
