package com.ruoyi.system.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.ruoyi.system.domain.entity.CasualtyReport;
import com.ruoyi.system.mapper.CasualtyReportMapper;
import com.ruoyi.system.webSocket.WebSocketServerExcel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CasualtyReportListener implements ReadListener<CasualtyReport> {

    private final List<CasualtyReport> list = new ArrayList<CasualtyReport>();
    private CasualtyReportMapper casualtyReportMapper;
    private int totalRows;
    private int currentRow = 0;
    private String userName;
    private boolean stopReading = false;
    public CasualtyReportListener(CasualtyReportMapper casualtyReportMapper, int totalRows, String userName) {
        this.casualtyReportMapper = casualtyReportMapper;
        this.totalRows = totalRows;
        this.userName = userName;
    }

    @Override
    public void invoke(CasualtyReport data, AnalysisContext context) {
// 检查当前行的第一个单元格
        if (data.getAffectedAreaName() == null||data.getAffectedAreaName().contains("填写单位")) {
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
    public void doAfterAllAnalysed(AnalysisContext context) {

    }

    public List<CasualtyReport> getList() {
        return list;
    }
}



