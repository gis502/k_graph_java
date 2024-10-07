package com.ruoyi.system.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.ruoyi.system.domain.entity.TrafficControlSections;
import com.ruoyi.system.domain.entity.TransferSettlementInfo;
import com.ruoyi.system.mapper.TrafficControlSectionsMapper;
import com.ruoyi.system.mapper.TransferSettlementInfoMapper;
import com.ruoyi.system.webSocket.WebSocketServerExcel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class trafficControlSectionsListener implements ReadListener<TrafficControlSections> {
    private final List<TrafficControlSections> list = new ArrayList<TrafficControlSections>();
    private TrafficControlSectionsMapper trafficControlSectionsMapper;
    private int totalRows;
    private int currentRow = 0;
    private String userName;
    private boolean stopReading = false;
    public trafficControlSectionsListener(TrafficControlSectionsMapper trafficControlSectionsMapper, int totalRows, String userName) {
        this.trafficControlSectionsMapper = trafficControlSectionsMapper;
        this.totalRows = totalRows;
        this.userName = userName;
    }

    @Override
    public void invoke(TrafficControlSections data, AnalysisContext context) {
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

    }
    public List<TrafficControlSections> getList() {
        return list;
    }
}
