package com.ruoyi;


import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.mapper.EarthquakeListMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = RuoYiApplication.class)
public class test1 {
    @Autowired
    private EarthquakeListMapper earthquakeListMapper;
    @Test
    public void test(){
        List<EarthquakeList> earthquakeLists = earthquakeListMapper.selectList(null);
        for (EarthquakeList earthquakeList : earthquakeLists) {
            System.out.println(earthquakeList);
        }

    }
}
