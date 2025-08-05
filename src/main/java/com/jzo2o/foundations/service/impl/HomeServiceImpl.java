package com.jzo2o.foundations.service.impl;

import com.jzo2o.common.utils.CollUtils;
import com.jzo2o.common.utils.ObjectUtils;
import com.jzo2o.foundations.constants.RedisConstants;
import com.jzo2o.foundations.enums.FoundationStatusEnum;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.domain.Region;
import com.jzo2o.foundations.model.dto.response.ServeAggregationSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeAggregationTypeSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeCategoryResDTO;
import com.jzo2o.foundations.model.dto.response.ServeSimpleResDTO;
import com.jzo2o.foundations.service.HomeService;
import com.jzo2o.foundations.service.IRegionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Service
@Slf4j
public class HomeServiceImpl implements HomeService {
    @Resource
    private ServeMapper serveMapper;
    @Resource
    private IRegionService regionService;
//condition不认方法的返回值 只有unless才能或取方法的返回值   （指定一个spel表达式，用于决定是否要缓存，
// condition只有当条件true 才缓存，unless 当条件为false才缓存）
    @Caching(
            cacheable = {
            @Cacheable(value = RedisConstants.CacheName.SERVE_ICON,
                    key = "#regionId",cacheManager = RedisConstants.CacheManager.THIRTY_MINUTES,
                    unless = "#result.size()!=0"),//定义查询数据为空处理缓存穿透
            @Cacheable(value = RedisConstants.CacheName.SERVE_ICON,
                    key = "#regionId",cacheManager = RedisConstants.CacheManager.FOREVER,
                    unless = "#result.size()==0")//缓存数据
     })

    @Override
    public List<ServeCategoryResDTO> queryServeIconCategoryByRegionId(Long regionId) {
        //查询区域信息
        Region region = regionService.getById(regionId);
        if (ObjectUtils.isNull(region)||region.getActiveStatus()!= FoundationStatusEnum.ENABLE.getStatus()){
            return Collections.emptyList();
        }

        //查询首页服务列表
        List<ServeCategoryResDTO> list = serveMapper.findServeIconCategoryByRegionId(regionId);
        if(CollUtils.isEmpty(list)){
            return Collections.emptyList();
        }
        //对查询到的数据进行格式化处理
        int endIndex = list.size() >=2?2:list.size();
        //最多两个服务类型
        List<ServeCategoryResDTO> serveCategoryResDTOS = new ArrayList<>(list.subList(0, endIndex));
        serveCategoryResDTOS.forEach( item -> {
            //取出最多四个服务项
            List<ServeSimpleResDTO> serveResDTOList = item.getServeResDTOList();//服务项
            int endIndex2 = serveResDTOList.size() >= 4 ? 4 : serveResDTOList.size();
            List<ServeSimpleResDTO> serveSimpleResDTOS = new ArrayList<>(serveResDTOList.subList(0, endIndex2));
            item.setServeResDTOList(serveSimpleResDTOS);
        });

        return serveCategoryResDTOS;
    }

    /**
     * 查询区域下所有服务类型
     * @param regionId 区域id
     * @return 服务类型列表
     */
    @Caching(
            cacheable = {
                    //缓存穿透解决 缓存空值 缓存时间 30分钟
                    @Cacheable(value = RedisConstants.CacheName.SERVE_TYPE,
                            key = "#regionId",cacheManager = RedisConstants.CacheManager.THIRTY_MINUTES,
                            unless = "#result.size()!=0"),//定义查询数据为空处理缓存穿透
                    //缓存数据 永久缓存
                    @Cacheable(value = RedisConstants.CacheName.SERVE_TYPE,
                            key = "#regionId",cacheManager = RedisConstants.CacheManager.FOREVER,
                            unless = "#result.size()==0")//缓存数据
            })
    @Override
    public List<ServeAggregationTypeSimpleResDTO> queryAllServeAtRegion(Long regionId) {
        List<ServeAggregationTypeSimpleResDTO> serveAggregationTypeSimpleResDTOS = serveMapper.queryServeTypeAllAtRegion(regionId);
        return serveAggregationTypeSimpleResDTOS;
    }


    /**
     * 根据区域id查询首页热门服务
     * @param regionId 区域id
     * @return 热门服务
     */
    @Caching(
            cacheable = {
                    @Cacheable(value = RedisConstants.CacheName.HOT_SERVE,
                            key = "#regionId",cacheManager = RedisConstants.CacheManager.THIRTY_MINUTES,
                            unless = "#result.size()!=0"),//定义查询数据为空处理缓存穿透
                    @Cacheable(value = RedisConstants.CacheName.HOT_SERVE,
                            key = "#regionId",cacheManager = RedisConstants.CacheManager.FOREVER,
                            unless = "#result.size()==0")//缓存数据
            })
    @Override
    public List<ServeAggregationSimpleResDTO> findHotServeAggregationByRegionId(Long regionId) {
        return serveMapper.queryHotServeAggregationByRegionId(regionId);
    }

    /**
     * 查询所有地区的热门上架服务
     * @return 热门服务区域id
     */
    @Override
    public List<Long> queryHotServeRegionId() {
        serveMapper.queryHotServeRegionId();
        return List.of();
    }

    /**
     * 根据服务id查询服务详情
     * @param serveId server主键
     * @return 服务详情
     */

    @Caching(
            cacheable = {
                    @Cacheable(value = RedisConstants.CacheName.SERVE_ITEM,
                            key = "#serveId",cacheManager = RedisConstants.CacheManager.THIRTY_MINUTES,
                            unless = "#result!=null"),//定义查询数据为空处理缓存穿透
                    @Cacheable(value = RedisConstants.CacheName.SERVE_ITEM,
                            key = "#serveId",cacheManager = RedisConstants.CacheManager.FOREVER,
                            unless = "#result==null")//缓存数据
            })
    @Override
    public  ServeAggregationSimpleResDTO  queryServeDetailByServeId(Long serveId) {

        return  serveMapper.queryServeDetailByServeId(serveId);
    }

    /**
     * 查询所有上架serveid
     * @return
     */
    @Override
    public List<Long> queryServeOnSale() {
        return serveMapper.queryServeIdOnSale();
    }
}
