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
    private PublicOpinionServiceImpl publicOpinionServiceImpl;

    @Resource
    private SocialOrderServiceImpl socialOrderServiceImpl;

    @Resource
    private TransferSettlementInfoServiceImpl transferSettlementInfoServiceImpl;

    @Resource
    private MeetingsServiceImpl meetingsServiceImpl;
    @Resource
    private AfterSeismicInformationServiceImpl afterSeismicInformationServiceImpl;

    @Resource
    private TrafficControlSectionsServiceImpl trafficControlSectionsServiceImpl;

    @Resource
    private CommunicationFacilityDamageRepairStatusServiceImpl communicationFacilityDamageRepairStatusServiceImpl;

    @Resource
    private PowerSupplyInformationServiceImpl powerSupplyInformationServiceImpl;

    @Resource
    private RoadDamageServiceImpl roadDamageServiceImpl;

    @Resource
    private RiskConstructionGeohazardsServiceImpl riskConstructionGeohazardsServiceImpl;

    @Resource
    private BarrierLakeSituationServiceImpl barrierLakeSituationService;

    @Resource
    private SecondaryDisasterInfoServiceImpl secondaryDisasterInfoService;

    @Resource
    private DisasterAreaWeatherForecastServiceImpl disasterAreaWeatherForecastService;

    @Resource
    private HousingSituationServiceImpl housingSituationService;

    @Resource
    private RescueForcesServiceImpl rescueForcesServiceImpl;

    @Resource
    private DisasterReliefMaterialsServiceImpl disasterReliefMaterialsServiceImpl;

    @Resource
    private LargeSpecialRescueEquipmentServiceImpl largeSpecialRescueEquipmentServiceImpl;

    @Resource
    private SupplySituationServiceImpl supplySituationService;

    @Resource
    private CharityOrganizationDonationsServiceImpl charityOrganizationDonationsService;

    @Resource
    private GovernmentDepartmentDonationsServiceImpl governmentDepartmentDonationsService;

    @Resource
    private RedCrossDonationsServiceImpl redCrossDonationsService;

    @Resource
    private SupplyWaterServiceImpl supplyWaterService;

    @Resource
    private MaterialDonationServiceImpl materialDonationService;

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
                message = sysOperLogMapper.getMessageByDay(requestParams, username);
                break;
            case "近七天":
                message = sysOperLogMapper.getMessageByWeek(requestParams, username);
                break;
            case "近一个月":
                message = sysOperLogMapper.getMessageByMonth(requestParams, username);
                break;
            case "近三个月":
                message = sysOperLogMapper.getMessageByThreeMonth(requestParams, username);
                break;
            case "近一年":
                message = sysOperLogMapper.getMessageByYear(requestParams, username);
                break;
        }
        return R.ok(message);
    }


    @PostMapping("/importExcel/{userName}&{filename}&{eqId}")
    @Log(title = "导入数据", businessType = BusinessType.IMPORT)
    public R getAfterShockStatistics(@RequestParam("file") MultipartFile file, @PathVariable(value = "userName") String userName, @PathVariable(value = "filename") String filename, @PathVariable(value = "eqId") String eqId) throws IOException {
        try {
            if (filename.equals("震情伤亡-震情灾情统计表")) {
                List<AftershockInformation> yaanAftershockStatistics = aftershockInformationServiceImpl.importExcelAftershockInformation(file, userName, eqId);
                return R.ok(yaanAftershockStatistics);
            }
            if (filename.equals("震情伤亡-人员伤亡统计表")) {
                List<CasualtyReport> yaanCasualties = caseCacheServiceImpl.importExcelCasualtyReport(file, userName, eqId);
                return R.ok(yaanCasualties);
            }
            if (filename.equals("震情伤亡-转移安置统计表")) {
                List<TransferSettlementInfo> YaanRelocationResettlementDisasterReliefGroup = transferSettlementInfoServiceImpl.importExcelTransferSettlementInfo(file, userName, eqId);
                return R.ok(YaanRelocationResettlementDisasterReliefGroup);
            }
            if (filename.equals("震情伤亡-文会情况统计表")) {
                List<Meetings> meetings = meetingsServiceImpl.importExcelMeetings(file, userName, eqId);
                return R.ok(meetings);
            }
            if (filename.equals("震情伤亡-震情受灾统计表")) {
                List<AfterSeismicInformation> afterSeismicInformations = afterSeismicInformationServiceImpl.importExcelAfterSeismicInformation(file, userName, eqId);
                return R.ok(afterSeismicInformations);
            }
            if (filename.equals("交通电力通信-交通管控情况统计表")) {
                List<TrafficControlSections> trafficControlSections = trafficControlSectionsServiceImpl.importExcelTrafficControlSections(file, userName, eqId);
                return R.ok(trafficControlSections);
            }
            if (filename.equals("交通电力通信-通信设施损毁及抢修情况统计表")) {
                List<CommunicationFacilityDamageRepairStatus> communicationFacilityDamageRepairStatus = communicationFacilityDamageRepairStatusServiceImpl.importExcelCommunicationFacilityDamageRepairStatus(file, userName, eqId);
                return R.ok(communicationFacilityDamageRepairStatus);
            }
            if (filename.equals("交通电力通信-电力设施损毁及抢修情况统计表")) {
                List<PowerSupplyInformation> powerSupplyInformation = powerSupplyInformationServiceImpl.importExcelPowerSupplyInformation(file, userName, eqId);
                return R.ok(powerSupplyInformation);
            }
            if (filename.equals("交通电力通信-道路交通损毁及抢修情况统计表")) {
                List<RoadDamage> RoadDamage = roadDamageServiceImpl.importExcelRoadDamage(file, userName, eqId);
                return R.ok(RoadDamage);
            }
            if (filename.equals("建筑物、工程受损-房屋情况统计表")) {
                List<HousingSituation> housingSituations = housingSituationService.importExcelHousingSituation(file, userName, eqId);
                return R.ok(housingSituations);
            }
            if (filename.equals("建筑物、工程受损-集中供水工程受损统计表")) {
                List<SupplySituation> supplySituations = supplySituationService.importExcelSupplySituation(file, userName, eqId);
                return R.ok(supplySituations);
            }
            if (filename.equals("建筑物、工程受损-保障安置点供水统计表")) {
                List<SupplyWater> supplyWaters = supplyWaterService.importExcelSupplyWater(file, userName, eqId);
                return R.ok(supplyWaters);
            }
            if (filename.equals("次生灾害-地质灾害统计表")) {
                List<RiskConstructionGeohazards> riskConstructionGeohazards = riskConstructionGeohazardsServiceImpl.importExcelRiskConstructionGeohazards(file, userName, eqId);
                return R.ok(riskConstructionGeohazards);
            }
            if (filename.equals("次生灾害-堰塞湖（雍塞体）统计表")) {
                List<BarrierLakeSituation> barrierLakeSituations = barrierLakeSituationService.importExcelBarrierLakeSituation(file, userName, eqId);
                return R.ok(barrierLakeSituations);
            }
            if (filename.equals("次生灾害-山洪危险区统计表")) {
                List<SecondaryDisasterInfo> secondaryDisasterInfos = secondaryDisasterInfoService.importExcelSecondaryDisasterInfo(file, userName, eqId);
                return R.ok(secondaryDisasterInfos);
            }
            if (filename.equals("次生灾害-气象情况统计表")) {
                List<DisasterAreaWeatherForecast> disasterAreaWeatherForecasts = disasterAreaWeatherForecastService.importExcelDisasterAreaWeatherForecast(file, userName, eqId);
                return R.ok(disasterAreaWeatherForecasts);
            }
            if (filename.equals("资金及物资捐赠-物资捐赠情况统计表")) {
                List<MaterialDonation> materialDonations = materialDonationService.importExcelMaterialDonation(file, userName, eqId);
                return R.ok(materialDonations);
            }
            if (filename.equals("资金及物资捐赠-资金援助情况-政府部门接收捐赠资金统计表")) {
                List<GovernmentDepartmentDonations> governmentDepartmentDonations = governmentDepartmentDonationsService.importExcelGovernmentDepartmentDonations(file, userName, eqId);
                return R.ok(governmentDepartmentDonations);
            }
            if (filename.equals("资金及物资捐赠-资金援助情况-慈善机构接收捐赠资金统计表")) {
                List<CharityOrganizationDonations> charityOrganizationDonations = charityOrganizationDonationsService.importExcelCharityOrganizationDonations(file, userName, eqId);
                return R.ok(charityOrganizationDonations);
            }
            if (filename.equals("资金及物资捐赠-资金援助情况-红十字会系统接收捐赠资金统计表")) {
                List<RedCrossDonations> redCrossDonations = redCrossDonationsService.importExcelRedCrossDonations(file, userName, eqId);
                return R.ok(redCrossDonations);
            }
            if (filename.equals("力量物资资金-救援力量情况统计表")) {
                List<RescueForces> rescueForces = rescueForcesServiceImpl.importExcelRescueForces(file, userName, eqId);
                return R.ok(rescueForces);
            }
            if (filename.equals("力量物资资金-救灾物资情况（累计）统计表")) {
                List<DisasterReliefMaterials> disasterReliefMaterials = disasterReliefMaterialsServiceImpl.importExcelDisasterReliefMaterials(file, userName, eqId);
                return R.ok(disasterReliefMaterials);
            }
            if (filename.equals("力量物资资金-大型、特种救援装备统计表")) {
                List<LargeSpecialRescueEquipment> largeSpecialRescueEquipment = largeSpecialRescueEquipmentServiceImpl.importExcelLargeSpecialRescueEquipment(file, userName, eqId);
                return R.ok(largeSpecialRescueEquipment);
            }
            if (filename.equals("宣传舆情治安-社会秩序统计表")) {
                List<SocialOrder> socialOrder = socialOrderServiceImpl.importExcelSocialOrder(file, userName, eqId);
                return R.ok(socialOrder);
            }
            if (filename.equals("宣传舆情治安-宣传舆论统计表")) {
                List<PublicOpinion> publicOpinions = publicOpinionServiceImpl.importExcelPublicOpinion(file, userName, eqId);
                return R.ok(publicOpinions);
            } else {
                return R.fail("上传文件名称错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("操作失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteData")
    public AjaxResult deleteData(@RequestBody Map<String, Object> requestBTO) {
        System.out.println(requestBTO);
        List<Map<String, Object>> idsList = (List<Map<String, Object>>) requestBTO.get("ids");
        return AjaxResult.success(dataExportStrategyContext.getStrategy((String) requestBTO.get("flag")).deleteData(idsList));
    }

}

