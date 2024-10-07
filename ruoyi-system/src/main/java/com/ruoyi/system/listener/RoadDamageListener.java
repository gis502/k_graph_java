package com.ruoyi.system.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.ruoyi.system.domain.entity.PowerSupplyInformation;
import com.ruoyi.system.domain.entity.RoadDamage;
import com.ruoyi.system.mapper.PowerSupplyInformationMapper;
import com.ruoyi.system.mapper.RoadDamageMapper;
import com.ruoyi.system.webSocket.WebSocketServerExcel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoadDamageListener implements ReadListener<RoadDamage> {
    private final List<RoadDamage> list = new ArrayList<RoadDamage>();
    private RoadDamageMapper roadDamageMapper;
    private int totalRows;
    private int currentRow = 0;
    private String userName;
    private boolean stopReading = false;
    public RoadDamageListener(RoadDamageMapper roadDamageMapper, int totalRows, String userName) {
        this.roadDamageMapper = roadDamageMapper;
        this.totalRows = totalRows;
        this.userName = userName;
    }

    @Override
    public void invoke(RoadDamage data, AnalysisContext context) {
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
    public void doAfterAllAnalysed(AnalysisContext context) {
        try {
            WebSocketServerExcel.sendInfo("100", userName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public List<RoadDamage> getList() {
        return list;
    }
}
