package com.ruoyi;


import com.ruoyi.system.domain.entity.EarthquakeList;
import com.ruoyi.system.domain.entity.SafetyProtection;
import com.ruoyi.system.mapper.EarthquakeListMapper;
import com.ruoyi.system.service.SafetyProtectionService;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;


import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class test1 {
    @Autowired
    private EarthquakeListMapper earthquakeListMapper;

    @Test
    public void test() {
        List<EarthquakeList> earthquakeLists = earthquakeListMapper.selectList(null);
        for (EarthquakeList earthquakeList : earthquakeLists) {
            System.out.println(earthquakeList);
        }
    }

    @Autowired
    private SafetyProtectionService safetyProtectionService;

    @Test
    public void test2() {
        SafetyProtection safetyProtection = new SafetyProtection();
        safetyProtection.setApplicationType("TypeA");
        safetyProtection.setSource("SourceA");
        safetyProtection.setAgreement("AgreementA");
        safetyProtection.setPort(8080);
        safetyProtection.setNotes("Test notes");
        boolean result = safetyProtectionService.save(safetyProtection);
        System.out.println(result);

    }
}
