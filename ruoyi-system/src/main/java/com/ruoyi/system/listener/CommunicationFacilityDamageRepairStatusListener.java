package com.ruoyi.system.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.ruoyi.system.domain.entity.CommunicationFacilityDamageRepairStatus;
import com.ruoyi.system.domain.entity.TransferSettlementInfo;
import com.ruoyi.system.mapper.CommunicationFacilityDamageRepairStatusMapper;
import com.ruoyi.system.webSocket.WebSocketServerExcel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommunicationFacilityDamageRepairStatusListener implements ReadListener<CommunicationFacilityDamageRepairStatus> {
    private final List<CommunicationFacilityDamageRepairStatus> list = new ArrayList<CommunicationFacilityDamageRepairStatus>();
    private CommunicationFacilityDamageRepairStatusMapper communicationFacilityDamageRepairStatusMapper;
    private int totalRows;
    private int currentRow = 0;
    private String userName;
    private boolean stopReading = false;
    public CommunicationFacilityDamageRepairStatusListener(CommunicationFacilityDamageRepairStatusMapper communicationFacilityDamageRepairStatusMapper, int totalRows, String userName) {
        this.communicationFacilityDamageRepairStatusMapper = communicationFacilityDamageRepairStatusMapper;
        this.totalRows = totalRows;
        this.userName = userName;
    }

    @Override
    public void invoke(CommunicationFacilityDamageRepairStatus data, AnalysisContext context) {
        System.out.println(data);
        // 检查当前行的第一个单元格
        if (data.getEarthquakeZoneName() == null||data.getEarthquakeZoneName().contains("填写单位")) {
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
    public List<CommunicationFacilityDamageRepairStatus> getList() {
        return list;
    }
}
