package com.jzo2o.foundations.controller.operation;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import com.jzo2o.foundations.service.IServeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
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

    /**
     * 区域服务分页查询
     * @param servePageQueryReqDTO
     * @return PageResult<ServeResDTO> 区域服务分页查询结果
     */
    @GetMapping("/page")
    @ApiOperation("区域服务分页查询")
    //key-value form表单传参= 不需要@RequestBody注解，springmvc会自动映射
    // @RequestParam 注解用于将请求中的单个参数（如 URL 查询参数或表单字段）绑定到方法参数上
    //方法参数是简单类型（如 String、int、Long 等），需要接收单个参数时；
    //参数名与请求参数名不一致时，可以通过 @RequestParam("paramName") 指定；
    //需要设置参数为必填或提供默认值时（如 @RequestParam(required = true, defaultValue = "1")）
    public PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO){
        PageResult<ServeResDTO> page = serveService.page(servePageQueryReqDTO);
        return page;
    }

    /**
     * 添加区域服务
     * @param serveUpsertReqDTOList
     */
    @PostMapping("/batch")
    @ApiOperation("添加区域服务")
    public void add(@RequestBody List<ServeUpsertReqDTO> serveUpsertReqDTOList){
        serveService.batchAdd(serveUpsertReqDTOList);

    }

    /**
     * 修改区域服务价格
     */ 
    @PutMapping("/{id}")
    @ApiOperation("修改区域服务价格")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "区域服务ID", required = true, dataTypeClass = Long.class),
        @ApiImplicitParam(name = "price", value = "区域服务价格", required = true, dataTypeClass = BigDecimal.class)
    })
    public void update(@PathVariable("id") Long id, @RequestParam("price") BigDecimal price){
        serveService.updatePrice(id, price);
    }

    /**
     * 区域服务上架
     * @param id 服务ID
     */
    @PutMapping("/onSale/{id}")
    @ApiOperation("区域服务上架")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "服务id", required = true, dataTypeClass = Long.class),
    })
    public void onSale(@PathVariable("id") Long id) {
        serveService.onsale(id);
    }


    /**
     * 删除区域服务开发
     * 通常项目会有一个统一的 全局响应 封装类（如 Result、Response），
     * 其中 msg 字段默认值为“ok”，当发生异常时，全局异常处理器会捕获异常并修改
     * msg 字段为具体的错误信息。这样前端收到的响应 msg 就能反映操作结果或错误原因。
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除区域服务")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "服务id", required = true, dataTypeClass = Long.class),
    })
    public void delete(@PathVariable("id") Long id) {
        serveService.delete(id);
    }

    @PutMapping("/offSale/{id}")
    @ApiOperation("区域服务下架")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "服务id", required = true, dataTypeClass = Long.class),
    })
    public void offSale(@PathVariable("id")Long id) {
        serveService.offSale(id);
    }


    /**
     * 设置热门服务
     * @param id 服务ID
     */
    @PutMapping("/onHot/{id}")
    @ApiOperation("设置热门服务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "服务id", required = true, dataTypeClass = Long.class),
    })
    public void onHot(@PathVariable("id") Long id) {
        serveService.onHot(id);
    }


    /**
     * 取消热门服务
     * @param id 服务ID
     */
    @PutMapping("/offHot/{id}")
    @ApiOperation("取消热门服务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "服务id", required = true, dataTypeClass = Long.class),
    })
    public void offHot(@PathVariable("id") Long id) {
        serveService.offHot(id);
    }




}
