package com.ruoyi.web.controller.exportAndInport;

import com.alibaba.excel.EasyExcel;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.AftershockInformation;
import com.ruoyi.system.mapper.SysOperLogMapper;
import com.ruoyi.system.service.impl.AftershockInformationServiceImpl;
import com.ruoyi.system.service.strategy.DataExportStrategy;
import com.ruoyi.system.service.strategy.DataExportStrategyContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

/**
 * 导入导出控制层
 *
 * @author 方
 */
@RequestMapping("/excel")
@RestController
@RequiredArgsConstructor
public class ExcelController {
    private final DataExportStrategyContext dataExportStrategyContext;

    @Resource
    private SysOperLogMapper sysOperLogMapper;

    @Resource
    private AftershockInformationServiceImpl aftershockInformationServiceImpl;
    @PostMapping("/getData")
    public AjaxResult getData(@RequestBody RequestBTO requestBTO) {
        return AjaxResult.success(dataExportStrategyContext.getStrategy(requestBTO.getFlag()).getPage(requestBTO));

    }

    @PostMapping("/exportExcel")
    public void exportExcel(HttpServletResponse response, @RequestBody RequestBTO RequestBTO) throws IOException {
        DataExportStrategy strategy = dataExportStrategyContext.getStrategy(RequestBTO.getFlag());
        Class<?> clazz = strategy.getExportExcelClass();
        List<?> dataList = strategy.exportExcelGetData(RequestBTO);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("地震数据信息统计表", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
//
        EasyExcel.write(response.getOutputStream(), clazz)
                .includeColumnFiledNames(Arrays.asList(RequestBTO.getFields()))
                .sheet("地震数据信息统计表")
                .doWrite(dataList);
    }


    @PostMapping("/getExcelUploadByTime")
    public R getExcelUploadByTime(@RequestParam("time") String time, @RequestParam("requestParams") String requestParams, @RequestParam("username") String username) {
        List<SysOperLog> message = null;

        switch (time) {
            case "今日":
                message = sysOperLogMapper.getMessageByDay(requestParams,username);
                System.out.println(message);
                break;
            case "近七天":
                message = sysOperLogMapper.getMessageByWeek(requestParams,username);
                break;
            case "近一个月":
                message = sysOperLogMapper.getMessageByMonth(requestParams,username);
                break;
            case "近三个月":
                message = sysOperLogMapper.getMessageByThreeMonth(requestParams,username);
                break;
            case "近一年":
                message = sysOperLogMapper.getMessageByYear(requestParams,username);
                break;
        }
        return R.ok(message);
    }

    @PostMapping("/importExcel/{userName}&{filename}&{eqId}")
    @Log(title = "导入数据", businessType = BusinessType.IMPORT)
    public R getAfterShockStatistics(@RequestParam("file") MultipartFile file, @PathVariable(value = "userName") String userName, @PathVariable(value = "filename") String filename, @PathVariable(value = "eqId") String eqId) throws IOException {
        System.out.println(eqId);
        try {
            if (filename.equals("震情伤亡-震情灾情统计表")) {
                List<AftershockInformation> yaanAftershockStatistics = aftershockInformationServiceImpl.importExcel(file, userName,eqId);
                return R.ok(yaanAftershockStatistics);
            }
            else {
                return R.fail("上传文件名称错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("操作失败: " + e.getMessage());
        }
    }


}
