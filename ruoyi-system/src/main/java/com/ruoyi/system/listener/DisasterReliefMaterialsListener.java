package com.ruoyi.system.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.ruoyi.system.domain.entity.DisasterReliefMaterials;
import com.ruoyi.system.mapper.DisasterReliefMaterialsMapper;
import com.ruoyi.system.webSocket.WebSocketServerExcel;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DisasterReliefMaterialsListener implements ReadListener<DisasterReliefMaterials> {
    private final List<DisasterReliefMaterials> list = new ArrayList<DisasterReliefMaterials>();
    private DisasterReliefMaterialsMapper disasterReliefMaterialsMapper;
    private int totalRows;
    private int currentRow = 0;
    private String userName;
    private boolean stopReading = false;

    public DisasterReliefMaterialsListener(DisasterReliefMaterialsMapper disasterReliefMaterialsMapper, int totalRows, String userName) {
        this.disasterReliefMaterialsMapper = disasterReliefMaterialsMapper;
        this.totalRows = totalRows;
        this.userName = userName;
    }

    @Override
    public void invoke(DisasterReliefMaterials data, AnalysisContext context) {
// 检查当前行的第一个单元格
        if (data.getEarthquakeAreaName() == null || data.getEarthquakeAreaName().contains("填写单位")) {
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
    public List<DisasterReliefMaterials> getList() {
        return list;
    }
}
