package com.ruoyi.system.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.ruoyi.system.domain.entity.AfterSeismicInformation;
import com.ruoyi.system.domain.entity.AftershockInformation;
import com.ruoyi.system.mapper.AfterSeismicInformationMapper;
import com.ruoyi.system.mapper.AftershockInformationMapper;
import com.ruoyi.system.webSocket.WebSocketServerExcel;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AfterSeismicInformationListener implements ReadListener<AfterSeismicInformation> {
    private final List<AfterSeismicInformation> list = new ArrayList<AfterSeismicInformation>();
    private AfterSeismicInformationMapper afterSeismicInformationMapper;
    private int totalRows;
    private int currentRow = 0;
    private String userName;
    private boolean stopReading = false;

    public AfterSeismicInformationListener(AfterSeismicInformationMapper afterSeismicInformationMapper, int totalRows, String userName) {
        this.afterSeismicInformationMapper = afterSeismicInformationMapper;
        this.totalRows = totalRows;
        this.userName = userName;
    }

    @Override
    public void invoke(AfterSeismicInformation data, AnalysisContext context) {
// 检查当前行的第一个单元格
        if (data.getAffectedAreaName() == null || data.getAffectedAreaName().contains("填写单位")) {
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

    @SneakyThrows
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        WebSocketServerExcel.sendInfo("100", userName);
    }
    public List<AfterSeismicInformation> getList() {
        return list;
    }
}
