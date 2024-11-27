package com.ruoyi.web.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.MessageConstants;
import com.ruoyi.common.exception.ParamsIsEmptyException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.dto.EqEventGetMapDTO;
import com.ruoyi.system.domain.dto.EqEventGetReportDTO;
import com.ruoyi.system.domain.dto.EqEventGetYxcDTO;
import com.ruoyi.system.domain.dto.EqEventTriggerDTO;
import com.ruoyi.system.domain.entity.AssessmentOutput;
import com.ruoyi.system.domain.entity.EqList;
import com.ruoyi.system.service.impl.EqListServiceImpl;
import com.ruoyi.web.api.ThirdPartyCommonApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author: xiaodemos
 * @date: 2024-11-26 2:45
 * @description: 地震触发业务逻辑
 */


@Service
@Transactional
public class SeismicTriggerService {
    @Resource
    private ThirdPartyCommonApi thirdPartyCommonApi;
    @Resource
    private EqListServiceImpl eqListService;

    /**
     * @param params 手动触发的地震事件参数
     * @author: xiaodemos
     * @date: 2024/11/26 2:53
     * @description: 地震事件触发时，会自动触发灾损评估接口，返回灾损评估结果
     * @return: 返回人员伤亡、经济损失、建筑破坏的数据
     */
    public String seismicEventTrigger(EqEventTriggerDTO params) {
        // 1.把前端上传的数据保存到对方的数据库中
        String eqqueueId = thirdPartyCommonApi.getSeismicTriggerByPost(params);

        // 2.如果返回的结果是一个字符串的话 表明数据已经插入成功，如果不是，则事务回滚
        if (StringUtils.isEmpty(eqqueueId)) {
            throw new ParamsIsEmptyException(MessageConstants.RETURN_PARAMS_IS_EMPTY);
        }

        // 3.插入到对方数据库成功后把数据插入到我们自己的数据库中
//        eqListService.save(params);
//        eqListService.upload();
        // 4.插入到我们数据库后 立即进行灾损评估。



        // 5.灾损评估需要等待10 - 15 min，所以这里需要使用异步调用。
        // 6.如果15分钟后没有拿到灾损评估结果，则进行事务回滚。
        // 7.拿到后评估结果后，保存到我们的数据库中。图片或者地址需要下载下来保存到本地
        // 8.额外写一个接口需要反复的读取我们自己数据库中的评估数据。
        // 9.拿到数据后立刻返回
        return "";
    }

    //TODO 转换都放着

    // 获取灾情报告并存到本地
    public List<AssessmentOutput> getReport(EqEventGetReportDTO params) {

        String seismicEventGetReportByGET = thirdPartyCommonApi.getSeismicEventGetReportByGET(params);

        // 将从第三方接口获取到的String数据转换成map,再拿出data,再将data里的数据转换成List,最终得到Report_result
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(seismicEventGetReportByGET, Map.class);

            if (responseMap != null && responseMap.get("code").equals(200)) {
                String dataJson = objectMapper.writeValueAsString(responseMap.get("data"));
                List<AssessmentOutput> Report_result = objectMapper.readValue(dataJson, new TypeReference<List<AssessmentOutput>>() {});

                // 调用文件下载方法
                downloadFiles(Report_result);  // 下载文件并存储到本地
                return Report_result;
            } else {
                throw new RuntimeException("API 返回错误信息: " + responseMap.get("msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("解析 seismicEventGetReportByGET 时出错: " + e.getMessage());
        }
    }

    /**
     * 处理文件下载逻辑
     *
     * @param assessmentOutputs 包含文件信息的 AssessmentOutput 列表
     */
    private void downloadFiles(List<AssessmentOutput> assessmentOutputs) {
        for (AssessmentOutput output : assessmentOutputs) {
            String sourceFile = output.getSourceFile();  // 获取文件路径
            String eqqueueId = output.getEqqueueId();   // 获取地震队列ID

            // 生成目标文件保存路径
            String saveDir = "D:\\雅安灾损文件\\" + eqqueueId + "\\灾情报告";
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();  // 如果目录不存在则创建
            }

            // 拼接完整的文件URL
            String fileUrl = "http://tq-test.xixily.com:10340" + sourceFile;

            // 获取文件名（从 sourceFile 中提取文件名）
            String fileName = new File(sourceFile).getName();

            // 目标文件路径
            File targetFile = new File(saveDir, fileName);

            // 调用方法下载文件
            downloadFileFromUrl(fileUrl, targetFile);
        }
    }

    /**
     * 从 URL 下载文件并保存到指定路径
     *
     * @param fileUrl 文件的 URL
     * @param targetFile 目标文件
     */
    private void downloadFileFromUrl(String fileUrl, File targetFile) {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            // 获取输入流并保存到文件
            try (InputStream inputStream = connection.getInputStream();
                 OutputStream outputStream = new FileOutputStream(targetFile)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                System.out.println("文件已下载并保存: " + targetFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("下载文件失败: " + fileUrl);
        }
    }





    /**
     * 获取专题图并存到本地
     * @param params
     * @return
     */

    public List<AssessmentOutput> getMap(EqEventGetMapDTO params) {

        String seismicEventGetMapByGET = thirdPartyCommonApi.getSeismicEventGetGetMapByGet(params);

        // 将从第三方接口获取到的String数据转换成map,再拿出data,再将data里的数据转换成List,最终得到Map_result
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(seismicEventGetMapByGET, Map.class);

            if (responseMap != null && responseMap.get("code").equals(200)) {
                String dataJson = objectMapper.writeValueAsString(responseMap.get("data"));
                List<AssessmentOutput> Map_result = objectMapper.readValue(dataJson, new TypeReference<List<AssessmentOutput>>() {});

                // 调用文件下载方法
                downloadMapFiles(Map_result);  // 下载文件并存储到本地
                return Map_result;
            } else {
                throw new RuntimeException("API 返回错误信息: " + responseMap.get("msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("解析 seismicEventGetMapByGET 时出错: " + e.getMessage());
        }
    }

    /**
     * 处理专题图文件下载逻辑
     *
     * @param assessmentOutputs 包含文件信息的 AssessmentOutput 列表
     */
    private void downloadMapFiles(List<AssessmentOutput> assessmentOutputs) {
        for (AssessmentOutput output : assessmentOutputs) {
            String sourceFile = output.getSourceFile();  // 获取文件路径
            String eqqueueId = output.getEqqueueId();   // 获取地震队列ID

            // 生成目标文件保存路径
            String saveDir = "D:\\雅安灾损文件\\" + eqqueueId + "\\专题图";
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();  // 如果目录不存在则创建
            }

            // 拼接完整的文件URL
            String fileUrl = "http://tq-test.xixily.com:10340" + sourceFile;

            // 获取文件名（从 sourceFile 中提取文件名）
            String fileName = new File(sourceFile).getName();

            // 目标文件路径
            File targetFile = new File(saveDir, fileName);

            // 调用方法下载文件
            downloadFileFromUrl(fileUrl, targetFile);
        }
    }






    /**
     * 获取地震影响场并存到本地
     */
    public List<AssessmentOutput> getYxc(EqEventGetYxcDTO params) {

        String seismicEventGetImpactFieldByGET = thirdPartyCommonApi.getSeismicEventGetYxcByGet(params);

        // 将从第三方接口获取到的String数据转换成map,再拿出data,再将data里的数据转换成List,最终得到Yxc_result
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(seismicEventGetImpactFieldByGET, Map.class);

            if (responseMap != null && responseMap.get("code").equals(200)) {
                String dataJson = objectMapper.writeValueAsString(responseMap.get("data"));
                List<AssessmentOutput> Yxc_result = objectMapper.readValue(dataJson, new TypeReference<List<AssessmentOutput>>() {});

                // 调用文件下载方法
                downloadImpactFieldFiles(Yxc_result);  // 下载文件并存储到本地
                return Yxc_result;
            } else {
                throw new RuntimeException("API 返回错误信息: " + responseMap.get("msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("解析 seismicEventGetImpactFieldByGET 时出错: " + e.getMessage());
        }
    }

    /**
     * 处理地震影响场文件下载逻辑
     *
     * @param assessmentOutputs 包含文件信息的 AssessmentOutput 列表
     */
    private void downloadImpactFieldFiles(List<AssessmentOutput> assessmentOutputs) {
        for (AssessmentOutput output : assessmentOutputs) {
            String sourceFile = output.getSourceFile();  // 获取文件路径
            String eqqueueId = output.getEqqueueId();   // 获取地震队列ID

            // 生成目标文件保存路径
            String saveDir = "D:\\雅安灾损文件\\" + eqqueueId + "\\地震影响场";
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();  // 如果目录不存在则创建
            }

            // 拼接完整的文件URL
            String fileUrl = "http://tq-test.xixily.com:10340" + sourceFile;

            // 获取文件名（从 sourceFile 中提取文件名）
            String fileName = new File(sourceFile).getName();

            // 目标文件路径
            File targetFile = new File(saveDir, fileName);

            // 检查目标文件是否已存在
            if (targetFile.exists()) {
                System.out.println("文件已存在，跳过下载: " + targetFile.getAbsolutePath());
                continue;
            }

            // 调用方法下载文件
            downloadFileFromUrl(fileUrl, targetFile);
        }
    }

}



