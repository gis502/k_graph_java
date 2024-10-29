package com.ruoyi.system.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.ruoyi.system.domain.entity.BarrierLakeSituation;
import com.ruoyi.system.domain.entity.RiskConstructionGeohazards;
import com.ruoyi.system.mapper.BarrierLakeSituationMapper;
import com.ruoyi.system.mapper.RiskConstructionGeohazardsMapper;
import com.ruoyi.system.webSocket.WebSocketServerExcel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BarrierLakeSituationListener implements ReadListener<BarrierLakeSituation> {

    private final List<BarrierLakeSituation> list = new ArrayList<BarrierLakeSituation>();
    private BarrierLakeSituationMapper barrierLakeSituationMapper;
    private int totalRows;
    private int currentRow = 0;
    private String userName;
    private boolean stopReading = false;
    public BarrierLakeSituationListener(BarrierLakeSituationMapper barrierLakeSituationMapper,
                                              int totalRows,
                                              String userName) {
        this.barrierLakeSituationMapper = barrierLakeSituationMapper;
        this.totalRows = totalRows;
        this.userName = userName;
    }


    @Override
    public void invoke(BarrierLakeSituation data, AnalysisContext analysisContext) {
        System.out.println(data);
        // 检查当前行的第一个单元格
        if (data.getAffectedArea() == null||data.getAffectedArea().contains("填写单位")) {
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

    public List<BarrierLakeSituation> getList() {
        return list;
    }
}
