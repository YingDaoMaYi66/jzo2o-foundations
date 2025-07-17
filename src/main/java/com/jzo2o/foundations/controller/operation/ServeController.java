package com.jzo2o.foundations.controller.operation;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import com.jzo2o.foundations.service.IServeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 区域服务管理相关接口
 */
@RestController("operationServeController")
@RequestMapping("/operation/serve")
@Api(tags = "运营端-区域服务管理相关接口")
public class ServeController {
    @Resource
    private IServeService serveService;

    @GetMapping("/page")
    @ApiOperation("区域服务分页查询")
    public PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO){
        PageResult<ServeResDTO> page = serveService.page(servePageQueryReqDTO);
        return page;

    }
}
