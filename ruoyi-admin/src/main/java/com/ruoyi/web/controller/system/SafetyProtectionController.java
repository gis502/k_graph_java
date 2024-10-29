package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.SafetyProtection;
import com.ruoyi.system.service.SafetyProtectionService;
import com.ruoyi.web.controller.common.RequestParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@RestController
@RequestMapping("/safety_protection")
public class SafetyProtectionController {

    private static final Logger log = LoggerFactory.getLogger(SafetyProtectionController.class);
    @Autowired
    private SafetyProtectionService safetyProtectionService;

    /**
     * 增
     */
    @PostMapping("/insert")
    public AjaxResult insert(@RequestBody SafetyProtection safetyProtection) {
        try {
            safetyProtection.setIfDelete("false");

            if (checkPort(safetyProtection)) {
                return AjaxResult.error("已经存在，请勿重新添加");
            }

            changePort(safetyProtection);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return AjaxResult.success(safetyProtectionService.save(safetyProtection));
    }

    public static void changePort(SafetyProtection safetyProtection) throws IOException, InterruptedException {

        String source = safetyProtection.getSource();
        String agreement = safetyProtection.getAgreement();
        String port = Integer.toString(safetyProtection.getPort());
        String tactics = safetyProtection.getTactics();

        ProcessBuilder processBuilder = new ProcessBuilder();
        if (!safetyProtection.getIfDelete().equals("true")) {
            processBuilder.command("sudo", "ufw", tactics,
                    "proto", agreement,
                    "from", source,
                    "to", "any",
                    "port", port);
        } else {
            processBuilder.command("sudo", "ufw", "delete", "allow",
                    "proto", agreement,
                    "from", source,
                    "to", "any",
                    "port", port);
        }

        System.out.println(processBuilder.command());
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        process.waitFor();
    }

    /**
     * 分页查
     */
    @RequestMapping(value = "getSafetyProtection", method = {RequestMethod.POST, RequestMethod.GET})
    public Page<SafetyProtection> page(@RequestBody RequestParams requestParams) {
        Page<SafetyProtection> page = new Page<>(requestParams.getPageNum(), requestParams.getPageSize());
        LambdaQueryWrapper<SafetyProtection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(SafetyProtection::getApplicationType, requestParams.getQueryValue())
                .or().like(SafetyProtection::getNotes, requestParams.getQueryValue());
        return safetyProtectionService.page(page, queryWrapper);
    }


    /**
     * 查全部
     */

    @PostMapping("/list")
    public AjaxResult list() {
        return AjaxResult.success(safetyProtectionService.list());
    }


    /**
     * 搜索查
     */

    @PostMapping("/searchSafetyProtection")
    public List<SafetyProtection> searchSafetyProtection(@RequestParam("inputData") String inputData) {
        LambdaQueryWrapper<SafetyProtection> wrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        wrapper.like(SafetyProtection::getApplicationType, inputData)
                .or()
                .like(SafetyProtection::getSource, inputData)
                .or()
                .like(SafetyProtection::getAgreement, inputData)
                .or()
                .like(SafetyProtection::getNotes, inputData)
                .or()
                .like(SafetyProtection::getTactics, inputData)
                .or()
                .apply("CAST(port AS TEXT) = {0}", inputData);


        return safetyProtectionService.list(wrapper);
    }

    /**
     * 刪
     */
    @DeleteMapping("/removeById")
    public boolean removeById(@RequestParam(value = "id") String id) throws IOException, InterruptedException {
        SafetyProtection safetyProtection = safetyProtectionService.getById(id);
        safetyProtection.setIfDelete("true");
        changePort(safetyProtection);

        return safetyProtectionService.removeById(id);
    }

    /**
     * 改
     */
    @PutMapping("/update")
    public AjaxResult update(@RequestBody SafetyProtection safetyProtection)  throws IOException, InterruptedException {

        if (checkPort(safetyProtection)) {
            return AjaxResult.error("已经存在，请勿重新添加");
        }

        this.removeById(safetyProtection.getUuid());
        this.insert(safetyProtection);

        return AjaxResult.success(safetyProtectionService.updateById(safetyProtection));
    }

    public boolean checkPort(SafetyProtection safetyProtection) {
        LambdaQueryWrapper<SafetyProtection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SafetyProtection::getSource, safetyProtection.getSource())
                .eq(SafetyProtection::getPort, safetyProtection.getPort())
                .eq(SafetyProtection::getAgreement, safetyProtection.getAgreement());

        return safetyProtectionService.count(queryWrapper) > 0; // Returns true if a matching entry exists
    }


}


