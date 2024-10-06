package com.ruoyi.web.controller.exportAndInport;

import com.alibaba.excel.EasyExcel;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.mapper.SysOperLogMapper;
import com.ruoyi.system.service.impl.*;
import com.ruoyi.system.service.strategy.DataExportStrategy;
import com.ruoyi.system.service.strategy.DataExportStrategyContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 导入导出控制层
 *
 * @author 方
 */
@RequestMapping("/excel")
@RestController
@RequiredArgsConstructor
public class ExcelController {
    private static final Logger log = LoggerFactory.getLogger(ExcelController.class);
    private final DataExportStrategyContext dataExportStrategyContext;

    @Resource
    private SysOperLogMapper sysOperLogMapper;

    @Resource
    private AftershockInformationServiceImpl aftershockInformationServiceImpl;
    @Resource
    private CasualtyReportServiceImpl caseCacheServiceImpl;

    @Resource
    private TransferSettlementInfoServiceImpl transferSettlementInfoServiceImpl;

    @Resource
   private MeetingsServiceImpl meetingsServiceImpl;

    @Resource
    private TrafficControlSectionsServiceImpl trafficControlSectionsServiceImpl;

    @Resource
    private CommunicationFacilityDamageRepairStatusServiceImpl communicationFacilityDamageRepairStatusServiceImpl;



    @PostMapping("/getData")
    public AjaxResult getData(@RequestBody RequestBTO requestBTO) {
        return AjaxResult.success(dataExportStrategyContext.getStrategy(requestBTO.getFlag()).getPage(requestBTO));

    }

    @PostMapping("/exportExcel")
    @Log(title = "数据导出", businessType = BusinessType.EXPORT)
    public void exportExcel(HttpServletResponse response, @RequestBody RequestBTO RequestBTO) throws IOException {
        try {
            DataExportStrategy strategy = dataExportStrategyContext.getStrategy(RequestBTO.getFlag());
            Class<?> clazz = strategy.getExportExcelClass();
            List<?> dataList = strategy.exportExcelGetData(RequestBTO);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("地震数据信息统计表", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), clazz)
                    .includeColumnFiledNames(Arrays.asList(RequestBTO.getFields()))
                    .sheet("地震数据信息统计表")
                    .doWrite(dataList);
        } catch (Exception e) {
          e.printStackTrace();
        }
    }


    @PostMapping("/getExcelUploadByTime")
    public R getExcelUploadByTime(@RequestParam("time") String time, @RequestParam("requestParams") String requestParams, @RequestParam("username") String username) {
        List<SysOperLog> message = null;

        switch (time) {
            case "今日":
                message = sysOperLogMapper.getMessageByDay(requestParams,username);
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
        try {
            if (filename.equals("震情伤亡-震情灾情统计表")) {
                List<AftershockInformation> yaanAftershockStatistics = aftershockInformationServiceImpl.importExcelAftershockInformation(file, userName,eqId);
                return R.ok(yaanAftershockStatistics);
            } if (filename.equals("震情伤亡-人员伤亡统计表")) {
                List<CasualtyReport> yaanCasualties = caseCacheServiceImpl.importExcelCasualtyReport(file, userName,eqId);
                return R.ok(yaanCasualties);
            }
            if (filename.equals("震情伤亡-转移安置统计表")) {
                List<TransferSettlementInfo> YaanRelocationResettlementDisasterReliefGroup=transferSettlementInfoServiceImpl.importExcelTransferSettlementInfo(file, userName,eqId);
                return R.ok(YaanRelocationResettlementDisasterReliefGroup);
            }
            if (filename.equals("震情伤亡-文会情况统计表")) {
                List<Meetings> meetings = meetingsServiceImpl.importExcelMeetings(file, userName,eqId);
                return R.ok(meetings);
            }
            if (filename.equals("交通电力通信-交通管控情况统计表")) {
                List<TrafficControlSections> trafficControlSections = trafficControlSectionsServiceImpl.importExcelTrafficControlSections(file, userName,eqId);
                return R.ok(trafficControlSections);
            }
            if (filename.equals("交通电力通信-通信设施损毁及抢修情况统计表")) {
                List<CommunicationFacilityDamageRepairStatus>  communicationFacilityDamageRepairStatus = communicationFacilityDamageRepairStatusServiceImpl.importExcelCommunicationFacilityDamageRepairStatus(file, userName,eqId);
                return R.ok(communicationFacilityDamageRepairStatus);
            }
            else {
                return R.fail("上传文件名称错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("操作失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteData")
    public AjaxResult deleteData(@RequestBody Map<String,Object> requestBTO) {
        System.out.println(requestBTO);
        List<Map<String, Object>> idsList = (List<Map<String, Object>>) requestBTO.get("ids");
        return AjaxResult.success(dataExportStrategyContext.getStrategy((String) requestBTO.get("flag")).deleteData(idsList));
    }


}
