package com.jzo2o.foundations.mapper;

import com.jzo2o.foundations.model.domain.Serve;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class RegionMapperTest {
    @Autowired
    private RegionMapper regionMapper;
    @Test
    void selectByRegionId() {
        List<Serve> serves = regionMapper.selectByRegionId(1692472339767234562L);
        for (Serve serve : serves) {
            System.out.println(serve);
        }

    }
}