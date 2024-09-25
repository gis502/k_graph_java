package com.ruoyi.system.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.ruoyi.system.domain.entity.AftershockInformation;
import com.ruoyi.system.mapper.AftershockInformationMapper;
import com.ruoyi.system.webSocket.WebSocketServerExcel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AftershockInformationListener  implements ReadListener<AftershockInformation> {
    private final List<AftershockInformation> list = new ArrayList<AftershockInformation>();
    private AftershockInformationMapper aftershockInformationMapper;
    private int totalRows;
    private int currentRow = 0;
    private String userName;
    private boolean stopReading = false;

    public AftershockInformationListener(AftershockInformationMapper afterShockStatisticsMapper, int totalRows, String userName) {
        this.aftershockInformationMapper = afterShockStatisticsMapper;
        this.totalRows = totalRows;
        this.userName = userName;
    }
    @Override
    public void onException(Exception e, AnalysisContext analysisContext) throws Exception {
        throw e;
    }

    @Override
    public void invoke(AftershockInformation data, AnalysisContext context) {
// 检查当前行的第一个单元格
        if (data.getAffectedArea().contains("填写单位")) {
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
    @Override
    public boolean hasNext(AnalysisContext analysisContext) {
        return true;
    }

    public List<AftershockInformation> getList() {
        return list;
    }
}
