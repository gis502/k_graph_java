package com.ruoyi.system.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.ruoyi.system.domain.entity.AfterSeismicInformation;
import com.ruoyi.system.domain.entity.SocialOrder;
import com.ruoyi.system.mapper.AfterSeismicInformationMapper;
import com.ruoyi.system.mapper.SocialOrderMapper;
import com.ruoyi.system.webSocket.WebSocketServerExcel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SocialOrderListener implements ReadListener<SocialOrder> {
    private final List<SocialOrder> list = new ArrayList<SocialOrder>();
    private SocialOrderMapper socialOrderMapper;
    private int totalRows;
    private int currentRow = 0;
    private String userName;
    private boolean stopReading = false;
    public SocialOrderListener(SocialOrderMapper socialOrderMapper,
                                           int totalRows,
                                           String userName) {
        this.socialOrderMapper = socialOrderMapper;
        this.totalRows = totalRows;
        this.userName = userName;
    }


    @Override
    public void invoke(SocialOrder data, AnalysisContext analysisContext) {
        System.out.println(data);
        // 检查当前行的第一个单元格
        if (data.getEarthquakeAreaName() == null||data.getEarthquakeAreaName().contains("填写单位")) {
            stopReading = true;
        }
        if (stopReading) {
            return;
        }
        list.add(data);
        // 更新进度
        currentRow++;
        try {
            int progress = (int) ((double) currentRow / (double) totalRows * 100);
            WebSocketServerExcel.sendInfo(String.valueOf(progress), userName);
        } catch (IOException e) {
            System.out.println("返回进度失败，原因：" + e);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        try {
            WebSocketServerExcel.sendInfo("100", userName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public List<SocialOrder> getList() {
        return list;
    }
}


