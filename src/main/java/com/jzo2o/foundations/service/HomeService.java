package com.jzo2o.foundations.service;

import com.jzo2o.foundations.model.dto.response.ServeAggregationSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeAggregationTypeSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeCategoryResDTO;

import java.util.List;

/**
 * 门户查询接口
 */
public interface HomeService {

    /**
     * 根据区域id获取服务分类图标
     *
     * @param regionId 区域id
     * @return 服务分类列表
     */
    List<ServeCategoryResDTO> queryServeIconCategoryByRegionId(Long regionId);

    /**
     * 查询区域下所有服务分类
     *
     * @return 服务分类列表
     */
    List<ServeAggregationTypeSimpleResDTO> queryAllServeAtRegion(Long regionId);


    /**
     * 首页热门服务
     *
     * @param regionId 区域id
     * @return 热门服务聚合列表
     */
    List<ServeAggregationSimpleResDTO> findHotServeAggregationByRegionId(Long regionId);

    /**
     * 查询hot服务区域id
     * @return 热门服务区域id列表
     */
    List<Long> queryHotServeRegionId();

    /**
     * 根据serverid查询服务详情
     * @param serveId server主键
     * @return 服务详情
     */
    ServeAggregationSimpleResDTO queryServeDetailByServeId(Long serveId);

    /**
     * 查询所有在售服务
     *
     * @return 在售服务id列表
     */
    List<Long> queryServeOnSale();
}
