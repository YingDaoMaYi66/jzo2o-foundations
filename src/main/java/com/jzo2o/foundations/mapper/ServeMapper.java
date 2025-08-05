package com.jzo2o.foundations.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jzo2o.foundations.model.domain.Serve;;
import com.jzo2o.foundations.model.dto.response.ServeAggregationSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeAggregationTypeSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeCategoryResDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author itcast
 * @since 2023-07-03
 */
public interface ServeMapper extends BaseMapper<Serve> {
    /**
     * 根据区域查询服务列表
     * @param regionId
     * @return
     */
    List<ServeResDTO> queryServeListByRegionId(@Param("regionId") Long regionId);
    /**
     * 首页服务列表
     * @param regionId 区域id
     * @return 服务分类列表
     */
    List<ServeCategoryResDTO> findServeIconCategoryByRegionId(@Param("regionId") Long regionId);

    /**
     * 查询服务聚合信息
     * @param regionId 区域id
     * @return 服务聚合信息列表
     */
    List<ServeAggregationTypeSimpleResDTO>  queryServeTypeAllAtRegion(@Param("regionId") Long regionId);


    /**
     * 根据区域id查询热门服务聚合信息
     * @param regionId 区域id
     * @return 热门服务聚合信息列表
     */
    List<ServeAggregationSimpleResDTO> queryHotServeAggregationByRegionId(Long regionId);


    /**
     * 查询所有地区的热门上架服务id
     * @return 热门服务区域id列表
     */
    List<Long>  queryHotServeRegionId();


    /**
     * 根据serverid查询服务详情
     * @param serveId 服务id
     * @return List<ServeAggregationSimpleResDTO>
     */
    ServeAggregationSimpleResDTO queryServeDetailByServeId(Long serveId);

    /**
     * 查询所有上架的服务id
     * @return
     */
    List<Long> queryServeIdOnSale();
}
