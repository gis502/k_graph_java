package com.ruoyi.system.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.ruoyi.system.domain.entity.BarrierLakeSituation;
import com.ruoyi.system.domain.entity.HousingSituation;
import com.ruoyi.system.mapper.BarrierLakeSituationMapper;
import com.ruoyi.system.mapper.HousingSituationMapper;
import com.ruoyi.system.webSocket.WebSocketServerExcel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HousingSituationListener implements ReadListener<HousingSituation> {

    private final List<HousingSituation> list = new ArrayList<HousingSituation>();
    private HousingSituationMapper housingSituationMapper;
    private int totalRows;
    private int currentRow = 0;
    private String userName;
    private boolean stopReading = false;
    public HousingSituationListener(HousingSituationMapper housingSituationMapper,
                                        int totalRows,
                                        String userName) {
        this.housingSituationMapper = housingSituationMapper;
        this.totalRows = totalRows;
        this.userName = userName;
    }


    @Override
    public void invoke(HousingSituation data, AnalysisContext analysisContext) {
        System.out.println(data);
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
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        try {
            WebSocketServerExcel.sendInfo("100", userName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<HousingSituation> getList() {
        return list;
    }


}
