package com.ruoyi.web.controller.exportAndInport;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.MessageConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.config.MapperConfig;
import com.ruoyi.system.config.PlotConfig;
import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.domain.bto.PlotBTO;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.domain.handler.ExcelConverter;
import com.ruoyi.system.mapper.PlotIconmanagementMapper;
import com.ruoyi.system.mapper.SituationPlotMapper;
import com.ruoyi.system.mapper.SysOperLogMapper;
import com.ruoyi.system.service.SituationPlotService;
import com.ruoyi.system.service.impl.*;
import com.ruoyi.system.service.strategy.DataExportStrategy;
import com.ruoyi.system.service.strategy.DataExportStrategyContext;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static com.ruoyi.system.domain.handler.ExcelConverter.convertContactPhones;

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
    private BarrierLakeSituationServiceImpl barrierLakeSituationServiceImpl;

    @Resource
    private SecondaryDisasterInfoServiceImpl secondaryDisasterInfoServiceImpl;

    @Resource
    private DisasterAreaWeatherForecastServiceImpl disasterAreaWeatherForecastServiceImpl;

    @Resource
    private HousingSituationServiceImpl housingSituationServiceImpl;

    @Resource
    private SituationPlotServiceImpl situationPlotServiceImpl;
    @Resource
    private RescueForcesServiceImpl rescueForcesServiceImpl;

    @Resource
    private PlotIconmanagementMapper plotIconmanagementMapper;

    @Resource
    private SituationPlotService situationPlotService;

    @Resource
    private SituationPlotMapper situationPlotMapper;

    @Resource
    private DisasterReliefMaterialsServiceImpl disasterReliefMaterialsServiceImpl;

    @Resource
    private LargeSpecialRescueEquipmentServiceImpl largeSpecialRescueEquipmentServiceImpl;

    @Resource
    private SupplySituationServiceImpl supplySituationServiceImpl;

    @Resource
    private CharityOrganizationDonationsServiceImpl charityOrganizationDonationsServiceImpl;

    @Resource
    private GovernmentDepartmentDonationsServiceImpl governmentDepartmentDonationsServiceImpl;

    @Resource
    private RedCrossDonationsServiceImpl redCrossDonationsServiceImpl;

    @Resource
    private SupplyWaterServiceImpl supplyWaterServiceImpl;

    @Resource
    private MaterialDonationServiceImpl materialDonationServiceImpl;

    @Resource
    private WorkGroupLogServiceImpl workGroupLogServiceImpl;

    /**
     * 搜索
     *
     * @param requestBTO 查询参数
     * @return AjaxResult
     */
    @PostMapping("/searchData")
    public AjaxResult searchData(@RequestBody RequestBTO requestBTO) {

        log.info("传入的Eqid为：{}", requestBTO.getQueryEqId());

        switch (requestBTO.getFlag()) {
            case "AfterSeismicInformation":
                return AjaxResult.success(afterSeismicInformationServiceImpl.searchData(requestBTO));
            case "AftershockInformation":
                return AjaxResult.success(aftershockInformationServiceImpl.searchData(requestBTO));
            case "CasualtyReport":
                return AjaxResult.success(caseCacheServiceImpl.searchData(requestBTO));
            case "TransferSettlementInfo":
                return AjaxResult.success(transferSettlementInfoServiceImpl.searchData(requestBTO));
            case "Meetings":
                return AjaxResult.success(meetingsServiceImpl.searchData(requestBTO));
            case "RoadDamage":
                return AjaxResult.success(roadDamageServiceImpl.searchData(requestBTO));
            case "TrafficControlSections":
                return AjaxResult.success(trafficControlSectionsServiceImpl.searchData(requestBTO));
            case "CommunicationFacilityDamageRepairStatus":
                return AjaxResult.success(communicationFacilityDamageRepairStatusServiceImpl.searchData(requestBTO));
            case "PowerSupplyInformation":
                return AjaxResult.success(powerSupplyInformationServiceImpl.searchData(requestBTO));
            case "HousingSituation":
                return AjaxResult.success(housingSituationServiceImpl.searchData(requestBTO));
            case "SupplySituation":
                return AjaxResult.success(supplySituationServiceImpl.searchData(requestBTO));
            case "SupplyWater":
                return AjaxResult.success(supplyWaterServiceImpl.searchData(requestBTO));
            case "RiskConstructionGeohazards":
                return AjaxResult.success(riskConstructionGeohazardsServiceImpl.searchData(requestBTO));
            case "BarrierLakeSituation":
                return AjaxResult.success(barrierLakeSituationServiceImpl.searchData(requestBTO));
            case "SecondaryDisasterInfo":
                return AjaxResult.success(secondaryDisasterInfoServiceImpl.searchData(requestBTO));
            case "DisasterAreaWeatherForecast":
                return AjaxResult.success(disasterAreaWeatherForecastServiceImpl.searchData(requestBTO));
            case "RescueForces":
                return AjaxResult.success(rescueForcesServiceImpl.searchData(requestBTO));
            case "LargeSpecialRescueEquipment":
                return AjaxResult.success(largeSpecialRescueEquipmentServiceImpl.searchData(requestBTO));
            case "DisasterReliefMaterials":
                return AjaxResult.success(disasterReliefMaterialsServiceImpl.searchData(requestBTO));
            case "MaterialDonation":
                return AjaxResult.success(materialDonationServiceImpl.searchData(requestBTO));
            case "GovernmentDepartmentDonations":
                return AjaxResult.success(governmentDepartmentDonationsServiceImpl.searchData(requestBTO));
            case "CharityOrganizationDonations":
                return AjaxResult.success(charityOrganizationDonationsServiceImpl.searchData(requestBTO));
            case "RedCrossDonations":
                return AjaxResult.success(redCrossDonationsServiceImpl.searchData(requestBTO));
            case "PublicOpinion":
                return AjaxResult.success(publicOpinionServiceImpl.searchData(requestBTO));
            case "SocialOrder":
                return AjaxResult.success(socialOrderServiceImpl.searchData(requestBTO));
            case "WorkGroupLog":
                return AjaxResult.success(workGroupLogServiceImpl.searchData(requestBTO));
            default:
                return AjaxResult.error(MessageConstants.SEARCH_Failed);
        }
    }


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

    @PostMapping("/downloadPlotExcel")
    @Log(title = "下载标绘数据Excel模板", businessType = BusinessType.EXPORT)
    public void downloadPlotExcel(HttpServletResponse response, @RequestBody PlotBTO plotBTO) throws IOException {
        System.out.println("导入的模板与导出的数据：" + plotBTO);
        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = "标绘数据模板.xlsx";
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8");

            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");

            // 使用 ByteArrayOutputStream 导出数据
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ExcelWriter excelWriter = EasyExcel.write(outputStream).build();

            // 判断plotBTO里的excelContent是否为空
            if (plotBTO.getExcelContent() != null && !plotBTO.getExcelContent().isEmpty()) {
                System.out.println("excelContent 不为 null 或空列表");

                // 遍历每个 sheet
                for (PlotBTO.Sheet sheet : plotBTO.getSheets()) {
                    // 准备每个 sheet 的数据
                    List<List<Object>> excelDataList = new ArrayList<>();
                    List<Object> headerRow = new ArrayList<>();

                    // 添加表头
                    for (PlotBTO.Field field : sheet.getFields()) {
                        headerRow.add(field.getName());
                    }
                    excelDataList.add(headerRow);

                    // 在这里根据excelContent中的数据进行填充

                    for (Map<String, List<Map<String, Object>>> contentMap : plotBTO.getExcelContent()) {
                        // 这里假设 contentMap 的 key 就是 sheet 的 name
                        if (contentMap.containsKey(sheet.getName())) {
                            List<Map<String, Object>> contentList = contentMap.get(sheet.getName());

                            // 遍历该 sheet 对应的内容列表
                            for (Map<String, Object> content : contentList) {
                                List<Object> dataRow = new ArrayList<>();
                                // 填充每一行的数据
                                for (PlotBTO.Field field : sheet.getFields()) {
                                    Object value = content.get(field.getName());
                                    dataRow.add(value != null ? value : ""); // 如果值为 null，则填充空字符串
                                }
                                excelDataList.add(dataRow);
                            }
                        }
                    }

                    // 将数据写入当前 sheet
                    WriteSheet writeSheet = EasyExcel.writerSheet(sheet.getName()).build();
                    excelWriter.write(excelDataList, writeSheet);
                }
                excelWriter.finish();
            } else {
                // 遍历每个 sheet
                for (PlotBTO.Sheet sheet : plotBTO.getSheets()) {
                    // 准备每个 sheet 的数据
                    List<List<Object>> excelDataList = new ArrayList<>();
                    List<Object> headerRow = new ArrayList<>();

                    // 添加表头
                    for (PlotBTO.Field field : sheet.getFields()) {
                        headerRow.add(field.getName());
                    }
                    excelDataList.add(headerRow);

                    // 添加100行空行
                    for (int i = 0; i < 100; i++) {
                        List<Object> emptyRow = Collections.nCopies(headerRow.size(), ""); // 创建与表头列数相同的空行
                        excelDataList.add(emptyRow);
                    }

                    // 将数据写入当前 sheet
                    WriteSheet writeSheet = EasyExcel.writerSheet(sheet.getName()).build();
                    excelWriter.write(excelDataList, writeSheet);
                }

                // 完成 EasyExcel 写入
                excelWriter.finish();
            }

            // 使用 Apache POI 添加下拉框
            try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(outputStream.toByteArray()))) {
                for (PlotBTO.Sheet sheet : plotBTO.getSheets()) {
                    Sheet excelSheet = workbook.getSheet(sheet.getName());

                    // 重新声明 headerRow
                    List<Object> headerRow = new ArrayList<>();
                    for (PlotBTO.Field field : sheet.getFields()) {
                        headerRow.add(field.getName());
                    }

                    for (PlotBTO.Field field : sheet.getFields()) {
                        if ("select".equals(field.getType()) && field.getContent() != null) {
                            // 提取选择框内容
                            String[] selectOptions = field.getContent().stream()
                                    .map(PlotBTO.Content::getLabel)
                                    .toArray(String[]::new);

                            int columnIndex = headerRow.indexOf(field.getName());
                            if (columnIndex != -1) {
                                // 确保行数有效
                                int lastRowNum = excelSheet.getLastRowNum();
                                if (lastRowNum >= 2) { // 行数至少要有数据
                                    // 设置下拉框的范围
                                    CellRangeAddressList addressList = new CellRangeAddressList(1, lastRowNum, columnIndex, columnIndex);
                                    DataValidationHelper validationHelper = excelSheet.getDataValidationHelper();
                                    DataValidationConstraint validationConstraint = validationHelper.createExplicitListConstraint(selectOptions);
                                    DataValidation validation = validationHelper.createValidation(validationConstraint, addressList);
                                    excelSheet.addValidationData(validation);
                                } else {
                                    System.err.println("没有足够的行数来添加下拉列表。");
                                }
                            } else {
                                System.err.println("未找到字段名称: " + field.getName());
                            }
                        }
                    }
                }

                // 将更改后的内容写入响应流
                workbook.write(response.getOutputStream());
            }

            // 刷新和关闭输出流
            response.getOutputStream().flush();
            response.getOutputStream().close();

        } catch (Exception e) {
            e.printStackTrace();
            // 返回错误信息
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "导出失败: " + e.getMessage());
        }
    }

    @PostMapping("/getExcelUploadByTime")
    public R getExcelUploadByTime(@RequestParam("time") String time, @RequestParam("requestParams") String
            requestParams, @RequestParam("username") String username) {
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
    public R getAfterShockStatistics(@RequestParam("file") MultipartFile
                                             file, @PathVariable(value = "userName") String userName, @PathVariable(value = "filename") String
                                             filename, @PathVariable(value = "eqId") String eqId) throws IOException {
        System.out.println(file);
        System.out.println(userName);
        System.out.println(eqId);
        System.out.println(filename);
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
                List<HousingSituation> housingSituations = housingSituationServiceImpl.importExcelHousingSituation(file, userName, eqId);
                return R.ok(housingSituations);
            }
            if (filename.equals("建筑物、工程受损-集中供水工程受损统计表")) {
                List<SupplySituation> supplySituations = supplySituationServiceImpl.importExcelSupplySituation(file, userName, eqId);
                return R.ok(supplySituations);
            }
            if (filename.equals("建筑物、工程受损-保障安置点供水统计表")) {
                List<SupplyWater> supplyWaters = supplyWaterServiceImpl.importExcelSupplyWater(file, userName, eqId);
                return R.ok(supplyWaters);
            }
            if (filename.equals("次生灾害-地质灾害统计表")) {
                List<RiskConstructionGeohazards> riskConstructionGeohazards = riskConstructionGeohazardsServiceImpl.importExcelRiskConstructionGeohazards(file, userName, eqId);
                return R.ok(riskConstructionGeohazards);
            }
            if (filename.equals("次生灾害-堰塞湖（雍塞体）统计表")) {
                List<BarrierLakeSituation> barrierLakeSituations = barrierLakeSituationServiceImpl.importExcelBarrierLakeSituation(file, userName, eqId);
                return R.ok(barrierLakeSituations);
            }
            if (filename.equals("次生灾害-山洪危险区统计表")) {
                List<SecondaryDisasterInfo> secondaryDisasterInfos = secondaryDisasterInfoServiceImpl.importExcelSecondaryDisasterInfo(file, userName, eqId);
                return R.ok(secondaryDisasterInfos);
            }
            if (filename.equals("次生灾害-气象情况统计表")) {
                List<DisasterAreaWeatherForecast> disasterAreaWeatherForecasts = disasterAreaWeatherForecastServiceImpl.importExcelDisasterAreaWeatherForecast(file, userName, eqId);
                return R.ok(disasterAreaWeatherForecasts);
            }
            if (filename.equals("资金及物资捐赠-物资捐赠情况统计表")) {
                List<MaterialDonation> materialDonations = materialDonationServiceImpl.importExcelMaterialDonation(file, userName, eqId);
                return R.ok(materialDonations);
            }
            if (filename.equals("资金及物资捐赠-资金援助情况-政府部门接收捐赠资金统计表")) {
                List<GovernmentDepartmentDonations> governmentDepartmentDonations = governmentDepartmentDonationsServiceImpl.importExcelGovernmentDepartmentDonations(file, userName, eqId);
                return R.ok(governmentDepartmentDonations);
            }
            if (filename.equals("资金及物资捐赠-资金援助情况-慈善机构接收捐赠资金统计表")) {
                List<CharityOrganizationDonations> charityOrganizationDonations = charityOrganizationDonationsServiceImpl.importExcelCharityOrganizationDonations(file, userName, eqId);
                return R.ok(charityOrganizationDonations);
            }
            if (filename.equals("资金及物资捐赠-资金援助情况-红十字会系统接收捐赠资金统计表")) {
                List<RedCrossDonations> redCrossDonations = redCrossDonationsServiceImpl.importExcelRedCrossDonations(file, userName, eqId);
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
            }
            if (filename.equals("工作组动态-工作组每日工作动态统计表")) {
                List<WorkGroupLog> workGroupLogs = workGroupLogServiceImpl.importExcelWorkGroupLog(file, userName, eqId);
                return R.ok(workGroupLogs);
            } else {
                return R.fail("上传文件名称错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("操作失败: " + e.getMessage());
        }
    }

    @PostMapping("/importPlotExcel/{filename}&{eqId}")
    public R importPlotExcel(@RequestParam("file") MultipartFile file,
                             @PathVariable(value = "filename") String filename,
                             @PathVariable(value = "eqId") String eqId) throws IOException {
        System.out.println("导入了标绘数据");
        System.out.println("filename: " + filename);
        System.out.println("eqId: " + eqId);

        List<SituationPlot> plotDataList = new ArrayList<>();
        List<String> plotProperty = new ArrayList<>();
        List<String> sheetNames = new ArrayList<>();
        Map<String, String> sheetIcon = new HashMap<>();
        Map<String, List<String>> plotIds = new HashMap<>(); // 使用 Map 按 drawType 分类存储 plotId

        // 获取映射配置
        Map<String, String> fieldMapping = new PlotConfig().getFieldMapping();
        Map<String, String> sheetMapping = new MapperConfig().plotTypeToMapperType();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) continue;

                String sheetName = sheet.getSheetName();
                String mappedSheetName = sheetMapping.get(sheetName);
                if (mappedSheetName == null) {
                    System.out.println("No mapping found for sheet: " + sheetName);
                    continue;
                }
                sheetNames.add(sheetName);

                System.out.println("Sheet: " + mappedSheetName);
                Row headerRow = sheet.getRow(0);
                Map<Integer, String> columnFieldMap = new HashMap<>();

                // 查找“结束时间”的列索引，用于划分标绘与属性数据
                int endTimeIndex = -1;
                for (int col = 0; col < headerRow.getPhysicalNumberOfCells(); col++) {
                    Cell cell = headerRow.getCell(col);
                    if (cell != null && "结束时间".equals(cell.getStringCellValue().trim())) {
                        endTimeIndex = col;
                        break;
                    }
                }

                if (endTimeIndex == -1) {
                    throw new RuntimeException("标题行中未找到 '结束时间' 列");
                }

                // 处理标绘数据
                int maxColumns = Math.min(endTimeIndex + 1, headerRow.getPhysicalNumberOfCells());
                for (int col = 0; col < maxColumns; col++) {
                    Cell cell = headerRow.getCell(col);
                    String fieldName = fieldMapping.get(cell.getStringCellValue());
                    if (fieldName != null) {
                        columnFieldMap.put(col, fieldName);
                    }
                }
                for (int rowIndex = 1; rowIndex <= sheet.getPhysicalNumberOfRows(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row == null) continue;

                    SituationPlot plotData = new SituationPlot();
                    plotData.setPlotId(UUID.randomUUID().toString()); // 每个实例生成唯一的 plotId

                    boolean rowIsEmpty = true;
                    Map<String, String> additionalFields = new HashMap<>();

                    // 处理前七列
                    for (Map.Entry<Integer, String> entry : columnFieldMap.entrySet()) {
                        Cell cell = row.getCell(entry.getKey());
                        String fieldName = entry.getValue();
                        String cellValue = "";

                        if (cell != null) {
                            try {
                                switch (cell.getCellType()) {
                                    case STRING:
                                        cellValue = cell.getStringCellValue().trim();
                                        break;
                                    case NUMERIC:
                                        if (DateUtil.isCellDateFormatted(cell)) {
                                            cellValue = ExcelConverter.convertExcelDate(cell.getNumericCellValue()).toString();
                                        } else {
                                            cellValue = String.valueOf(cell.getNumericCellValue());
                                        }
                                        break;
                                    case BOOLEAN:
                                        cellValue = String.valueOf(cell.getBooleanCellValue());
                                        break;
                                    case FORMULA:
                                        try {
                                            cellValue = String.valueOf(cell.getNumericCellValue());
                                        } catch (IllegalStateException e) {
                                            cellValue = cell.getStringCellValue();
                                        }
                                        break;
                                    default:
                                        cellValue = "";
                                }

                                if (!cellValue.isEmpty()) {
                                    rowIsEmpty = false;
                                    try {
                                        Field field = SituationPlot.class.getDeclaredField(fieldName);
                                        field.setAccessible(true);
                                        Class<?> fieldType = field.getType();

                                        if (fieldType.equals(Double.class)) {
                                            field.set(plotData, Double.valueOf(cellValue));
                                        } else if (fieldType.equals(BigDecimal.class)) {
                                            field.set(plotData, new BigDecimal(cellValue));
                                        } else if (fieldType.equals(LocalDateTime.class)) {
                                            try {
                                                field.set(plotData, LocalDateTime.parse(cellValue, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                                            } catch (DateTimeParseException e) {
                                                System.err.println("Invalid date format for field: " + fieldName + ". Skipping.");
                                            }
                                        } else if (fieldType.equals(Integer.class)) {
                                            field.set(plotData, Integer.valueOf(cellValue));
                                        } else {
                                            field.set(plotData, cellValue);
                                        }
                                    } catch (NoSuchFieldException e) {
                                        System.err.println("Field not found: " + fieldName);
                                    } catch (NumberFormatException e) {
                                        System.err.println("Invalid number format for field: " + fieldName + " - " + cellValue);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                System.err.println("Error processing cell: " + e.getMessage());
                            }
                        }
                    }

                    for (int col = endTimeIndex + 1; col < row.getPhysicalNumberOfCells(); col++) {
                        Cell cell = row.getCell(col);
                        String headerName = fieldMapping.get(headerRow.getCell(col).getStringCellValue());

                        // 因为前端的映射“破坏形式”对应"damageForm"与"damageType"，而『公路破坏点』中字段为后者，故如此处理
                        if ("roadDamagePoints".equals(mappedSheetName) && "damageForm".equals(headerName)) {
                            headerName = "damageType";
                        }

                        if (cell != null) {
                            String cellValue = cell.toString().trim();
                            if (!cellValue.isEmpty()) {
                                if (headerName != null) {
                                    additionalFields.put(headerName, cellValue);
                                }
                            }
                        }
                    }

                    if (!rowIsEmpty) {
                        plotData.setPlotType(sheetName);
                        plotDataList.add(plotData);

                        StringBuilder propertyString = new StringBuilder(mappedSheetName + "(");
                        propertyString.append("plotId=").append(plotData.getPlotId()).append(", ");
                        additionalFields.forEach((key, value) ->
                                propertyString.append(key).append("=").append(value).append(", "));
                        if (propertyString.length() > 2) {
                            propertyString.setLength(propertyString.length() - 2);
                        }
                        propertyString.append(")");

                        plotProperty.add(propertyString.toString());

                        // 分类存储 plotId
                        String drawType = plotData.getDrawtype();
                        plotIds.computeIfAbsent(drawType, k -> new ArrayList<>()).add((String) plotData.getPlotId());
                    }
                }
            }
        }

        List<PlotIconmanagement> icons = plotIconmanagementMapper.findIconsBySheetNames(sheetNames);
        for (PlotIconmanagement icon : icons) {
            sheetIcon.put(icon.getName(), icon.getImg());
        }

        for (SituationPlot plotData : plotDataList) {
            plotData.setEarthquakeId(eqId);
            plotData.setIcon(sheetIcon.get(plotData.getPlotType()));
        }

        List<String> updatedPlotProperty = convertContactPhones(plotProperty);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("plotIds", plotIds); // 返回分类后的 plotIds
        responseMap.put("plotDataList", plotDataList); // 完整的 plot 数据
        responseMap.put("updatedPlotProperty", updatedPlotProperty); // 更新后的属性数据

        // 保存标绘数据及其属性
        situationPlotService.savePlotDataAndProperties(plotDataList, updatedPlotProperty);

        return R.ok(responseMap);
    }


    @DeleteMapping("/deleteData")
    public AjaxResult deleteData(@RequestBody Map<String, Object> requestBTO) {
        System.out.println(requestBTO);
        List<Map<String, Object>> idsList = (List<Map<String, Object>>) requestBTO.get("ids");
        return AjaxResult.success(dataExportStrategyContext.getStrategy((String) requestBTO.get("flag")).deleteData(idsList));
    }
}

