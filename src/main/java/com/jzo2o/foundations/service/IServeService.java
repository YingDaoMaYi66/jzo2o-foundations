package com.jzo2o.foundations.service;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.model.domain.Serve;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 服务表 服务类
 * </p>
 *
 * @author itcast
 * @since 2025-07-17
 */
public interface IServeService extends IService<Serve> {
    /**
     * 区域服务分页查询
     * @param servePageQueryReqDTO
     * @return
     */
    PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO);

    /**
     * 批量添加区域服务
     * @param serveUpsertReqDTOList
     */
    void batchAdd(List<ServeUpsertReqDTO> serveUpsertReqDTOList);

    /**
     * 修改区域服务价格
     * @param id 服务ID
     * @param price 服务价格
     */
    Serve updatePrice(Long id, BigDecimal price);


    /**
     * 商品上架
     * @param id 服务ID
     */
    Serve onsale(Long id);

    /**
     * 删除区域服务开发
     * @param id 服务ID
     */
    void delete(Long id);


    /**
     * 区域服务下架
     * @param id 服务ID
     */
    void offSale(Long id);

    /**
     * 设置服务为热门
     * @param id 服务ID
     */
    void onHot(Long id);


    /**
     * 取消服务热门状态
     * @param id 服务ID
     */
    void offHot(Long id);
}
