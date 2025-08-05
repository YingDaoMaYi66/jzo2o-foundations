package com.jzo2o.foundations.handler;

import com.jzo2o.api.foundations.dto.response.RegionSimpleResDTO;
import com.jzo2o.foundations.constants.RedisConstants;
import com.jzo2o.foundations.service.HomeService;
import com.jzo2o.foundations.service.IRegionService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SpringCacheSyncHandler {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private IRegionService regionService;
    @Autowired
    private HomeService homeService;

    //已开通服务列表缓存定时预热程序
    @XxlJob("activeRegionCacheSync")//指定任务名称
    public void activeRegionCacheSync() {
        log.info(">>>>>>>>>>>>>>开始进行缓存同步，更新已启用区域");
        //删除原来的缓存
        //key
        String key = RedisConstants.CacheName.JZ_CACHE+"::ACTIVE_REGIONS";
        //删除缓存
        redisTemplate.delete(key);
        //添加新缓存 查询到所有的开通的区域
        List<RegionSimpleResDTO> regionSimpleResDTOS = regionService.queryActiveRegionList();

        //遍历区域，对每个区域首页服务列表进行删除缓存再添加缓存
        regionSimpleResDTOS.forEach(item->{
            //key
            String key1 = RedisConstants.CacheName.SERVE_ICON + "::" + item.getId();
            //删除缓存
            redisTemplate.delete(key1);
            //调用首页服务列表的查询方法去添加缓存
            homeService.queryServeIconCategoryByRegionId(item.getId());

        });
        log.info(">>>>>>>>>>>>>>更新已启用区域完成");
    }

    //每天凌晨缓存服务类型列表
    @XxlJob("serveTypeCacheSync")//指定任务名称
    public void serveTypeCacheSync() {
        log.info(">>>>>>>>>>>>>>开始进行缓存同步，更新服务类型列表");
        //删除原来的缓存
        //key
        String pattern = RedisConstants.CacheName.SERVE_TYPE+"::*";
        //删除缓存
        Set<String> keys = redisTemplate.keys(pattern);
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }

        //添加新缓存 查询到所有的开通的区域id
        List<RegionSimpleResDTO> regionSimpleResDTOS = regionService.queryActiveRegionList();

        //将区域id转换为List<Long>
        List<Long> regionIds = regionSimpleResDTOS.stream()
                .map(RegionSimpleResDTO::getId)
                .collect(Collectors.toList());
        //遍历区域
        regionIds.forEach(region->{
            //调用首页服务类型列表的查询方法去添加缓存
            homeService.queryAllServeAtRegion(region);
        });

        log.info(">>>>>>>>>>>>>>更新服务类型列表完成");
    }

    /**
     * 每天凌晨缓存热门服务列表
     */


    @XxlJob("hotServeCacheSync")//指定任务名称
    public void hotServeCacheSync() {
        log.info(">>>>>>>>>>>>>>开始进行缓存同步，更新热门服务列表");
        //删除原来的缓存
        //key
        String pattern = RedisConstants.CacheName.HOT_SERVE+"::*";
        //删除缓存
        Set<String> keys = redisTemplate.keys(pattern);
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }

        //添加新缓存 查询到所有的开通热门的区域id
        List<Long> regionIds =  homeService.queryHotServeRegionId();

        //遍历区域
        regionIds.forEach(region->{
            //调用首页热门服务列表的查询方法去添加缓存
            homeService.findHotServeAggregationByRegionId(region);
        });

        log.info(">>>>>>>>>>>>>>更新全部区域热门服务列表完成");
    }

    /**
     * 每天凌晨缓存服务详情
     */
    @XxlJob("detailServeCacheSync")//指定任务名称
    public void detailServeCacheSync() {
        log.info(">>>>>>>>>>>>>>开始进行缓存同步，缓存热门服务列表");
        //删除原来的缓存
        //key
        String pattern = RedisConstants.CacheName.SERVE_ITEM+"::*";
        //删除缓存
        Set<String> keys = redisTemplate.keys(pattern);
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }

        //添加新缓存 查询到所有serve上架id
        List<Long> serveIds =  homeService.queryServeOnSale();

        //遍历区域
        serveIds.forEach(serveId ->{
            //调用首页热门服务列表的查询方法去添加缓存
            homeService.queryServeDetailByServeId(serveId);
        });
        log.info(">>>>>>>>>>>>>>更新全部区域热门服务列表完成");
    }
}
