package com.jzo2o.foundations.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.enums.FoundationStatusEnum;
import com.jzo2o.foundations.enums.IsHotStatusEnum;
import com.jzo2o.foundations.mapper.RegionMapper;
import com.jzo2o.foundations.mapper.ServeItemMapper;
import com.jzo2o.foundations.model.domain.Region;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.domain.ServeItem;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import com.jzo2o.foundations.service.IServeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.mysql.utils.PageHelperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.List;

/**
 * <p>
 * 服务表 服务实现类
 * </p>
 *
 * @author itcast
 * @since 2025-07-17
 */
@Service
public class ServeServiceImpl extends ServiceImpl<ServeMapper, Serve> implements IServeService {

    @Resource
    private ServeItemMapper serveItemMapper;
    @Autowired
    private RegionMapper regionMapper;

    /**
     * 区域服务分页查询
     * @param servePageQueryReqDTO 分页参数对象
     * @return serveResDTO的分页结果
     */
    @Override
    public PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO) {
        PageResult<ServeResDTO> serveResDTOPageResult = PageHelperUtils.selectPage(servePageQueryReqDTO,
                () -> baseMapper.queryServeListByRegionId(servePageQueryReqDTO.getRegionId()));
        return serveResDTOPageResult;
    }

    /**
     * 添加区域服务
     * @param serveUpsertReqDTOList 服务添加请求参数列表
     */
    @Override
    public void batchAdd(List<ServeUpsertReqDTO> serveUpsertReqDTOList) {
        // 数据校验
        for(ServeUpsertReqDTO serveUpsertReqDTO : serveUpsertReqDTOList) {
            //1、 serve_item是否启用
            Long serveItemId = serveUpsertReqDTO.getServeItemId();
            ServeItem serveItem = serveItemMapper.selectById(serveItemId);
            if (ObjectUtil.isNull(serveItem) || serveItem.getActiveStatus()!= FoundationStatusEnum.ENABLE.getStatus()) {
                //抛出异常
                throw  new ForbiddenOperationException("服务项不存在，或未启用，不允许添加");
            }
            //2、 同一个区域不能添加相同的服务
            Integer count = lambdaQuery()//相当于 new LambdaQueryWrapper<Serve>()
                    .eq(Serve::getServeItemId, serveUpsertReqDTO.getServeItemId())
                    .eq(Serve::getRegionId, serveUpsertReqDTO.getServeItemId())
                    .count();
            if (count>0){
                throw new ForbiddenOperationException(serveItem.getName()+"服务已存在");
            }
            //3、 向serve插入函数

            Serve serve = BeanUtil.toBean(serveUpsertReqDTO, Serve.class);
            Region region = regionMapper.selectById(serveUpsertReqDTO.getRegionId());
            serve.setCityCode(region.getCityCode());
            baseMapper.insert(serve);
        }


    }

    /**
     * 修改区域服务价格
     * @param id 服务ID
     * @param price 服务价格
     */
    @Override
    public Serve updatePrice(Long id, BigDecimal price) {
        boolean update = lambdaUpdate()
        .eq(Serve::getId, id)
        .set(Serve::getPrice, price)
        .update();
        if (!update) {
            throw new ForbiddenOperationException("修改服务价格失败");
        }
        //查询serve数据
        Serve serve = baseMapper.selectById(id);
        return serve;
    }

    /**
     * 区域服务上架
     * @param id 服务ID
     */
    @Override
    public Serve onsale(Long id) {

        //根據id查詢serve信息
        Serve serve = baseMapper.selectById(id);
        if(ObjectUtil.isNull(serve)){
            throw new ForbiddenOperationException("区域服务不存在");
        }

        //如果serve的sale_tatus是0或1可以上架
        if((serve.getSaleStatus()==FoundationStatusEnum.INIT.getStatus() || serve.getSaleStatus()==FoundationStatusEnum.ENABLE.getStatus())){
            throw new ForbiddenOperationException("区域服务的状态是草稿或下架时方可上架");
        }

    
        //如果服务项没有启用便不能上架
        Long serveItemId = serve.getServeItemId();
        ServeItem serveItem = serveItemMapper.selectById(serveItemId);
        int activeStatus = serveItem.getActiveStatus();

        if(activeStatus!=FoundationStatusEnum.ENABLE.getStatus()){
            throw new ForbiddenOperationException("服务项未启用，不允许上架");
        }
        //更新sale_status
        boolean update = lambdaUpdate()
        .eq(Serve::getId, id)
        .set(Serve::getSaleStatus, FoundationStatusEnum.ENABLE.getStatus())
        .update();
        if(!update){
            throw new ForbiddenOperationException("服务上架失败");
        }
        return baseMapper.selectById(id);
    }

    /**
     * 删除区域服务开发
     * @param id 服务ID
     */
    @Override
    public void delete(Long id) {
        //删除区域服务前，先查询是否存在
        Serve serve = baseMapper.selectById(id);
        if (ObjectUtil.isNull(serve)) {
            throw new ForbiddenOperationException("区域服务不存在，无法删除");
        }
        int i = baseMapper.deleteById(id);
        if (i <= 0) {
            new ForbiddenOperationException("区域服务删除失败");
        }
    }


    /**
     * 区域服务下架
     * @param id 服务ID
     */
    @Override
    public void offSale(Long id) {
        //根據id查詢serve信息
        Serve serve = baseMapper.selectById(id);
        if(ObjectUtil.isNull(serve)){
            throw new ForbiddenOperationException("区域服务不存在");
        }

        //如果serve的sale_tatus 不等于2才可以下架，防止2次下架
        if(!(serve.getSaleStatus()==FoundationStatusEnum.INIT.getStatus() || serve.getSaleStatus()==FoundationStatusEnum.ENABLE.getStatus())){
            throw new ForbiddenOperationException("区域服务的状态是非草稿与下架方可下架");
        }

        //更新sale_status
        boolean update = lambdaUpdate()
                .eq(Serve::getId, id)
                .set(Serve::getSaleStatus, FoundationStatusEnum.DISABLE.getStatus())
                .update();
        if(!update){
            throw new ForbiddenOperationException("服务下架失败");
        }
    }


    /**
     * 设置服务为热门
     * @param id 服务ID
     */
    @Override
    public void onHot(Long id) {
        //根據id查詢serve信息
        Serve serve = baseMapper.selectById(id);
        if(ObjectUtil.isNull(serve)){
            throw new ForbiddenOperationException("区域服务不存在");
        }

        //IsHot0是非热门，1是热门
        if(serve.getIsHot() == IsHotStatusEnum.hot.getStatus()){
            throw new ForbiddenOperationException("区域服务已经是热门了");
        }
        //更新sale_status
        boolean update = lambdaUpdate()
                .eq(Serve::getId, id)
                .set(Serve::getIsHot,IsHotStatusEnum.hot.getStatus() )
                .update();
        if(!update){
            throw new ForbiddenOperationException("服务设置热门失败");
        }
    }

    /**
     * 取消服务热门状态
     * @param id 服务ID
     */
    @Override
    public void offHot(Long id) {
        //根據id查詢serve信息
        Serve serve = baseMapper.selectById(id);
        if(ObjectUtil.isNull(serve)){
            throw new ForbiddenOperationException("区域服务不存在");
        }

        //IsHot0是非热门，1是热门
        if(serve.getIsHot() == IsHotStatusEnum.notHot.getStatus()){
            throw new ForbiddenOperationException("区域服务已经非热门了");
        }

        //更新sale_status
        boolean update = lambdaUpdate()
                .eq(Serve::getId, id)
                .set(Serve::getIsHot, IsHotStatusEnum.notHot.getStatus() )
                .update();
        if(!update){
            throw new ForbiddenOperationException("服务设置热门失败");
        }
    }

    /**
     * 根据区域ID和销售状态查询服务数量
     * @param id 区域ID
     * @param status 销售状态
     * @return 服务数量
     */
    @Override
    public int queryServeCountByRegionIdAndSaleStatus(Long id, int status) {
        int count = lambdaQuery()
                .eq(Serve::getRegionId, id)
                .eq(Serve::getSaleStatus, status)
                .count();
        return count;
    }

    /**
     * 根据服务项ID和销售状态查询服务数量
     * @param id 服务项ID
     * @param status 销售状态
     * @return 服务数量
     */
    @Override
    public int queryServeCountByServeItemIdAndSaleStatus(Long id, int status) {
        int count = lambdaQuery()
                .eq(Serve::getServeItemId, id)
                .eq(Serve::getSaleStatus, status)
                .count();
        return count;
    }
}
