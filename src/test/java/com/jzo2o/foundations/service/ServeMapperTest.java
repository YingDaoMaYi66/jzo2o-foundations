package com.jzo2o.foundations.service;

import cn.hutool.core.lang.Assert;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@Slf4j
public class ServeMapperTest {
    @Resource
    private ServeMapper serveMapper;

    @Test
    public void test_queryServeListByRegionId() {
        List<ServeResDTO> serveResDTOS = serveMapper.queryServeListByRegionId(1686303222843662337L);
        //断言是一种用于在代码运行时检查某个条件为真的机制，如果条件不成立，程序会抛出异常并重试
        Assert.notEmpty(serveResDTOS, "查询数据结果不能为空");
    }


}

