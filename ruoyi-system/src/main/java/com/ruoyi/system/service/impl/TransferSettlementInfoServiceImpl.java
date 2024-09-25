package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.bto.RequestBTO;
import com.ruoyi.system.domain.entity.AftershockInformation;
import com.ruoyi.system.service.strategy.DataExportStrategy;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.mapper.TransferSettlementInfoMapper;
import com.ruoyi.system.domain.entity.TransferSettlementInfo;
import com.ruoyi.system.service.TransferSettlementInfoService;
@Service
public class TransferSettlementInfoServiceImpl
        extends ServiceImpl<TransferSettlementInfoMapper, TransferSettlementInfo>
        implements TransferSettlementInfoService, DataExportStrategy {
    @Override
    public IPage<TransferSettlementInfo> getPage(RequestBTO requestBTO) {
        Page<TransferSettlementInfo>
                transferSettlementInfo =
                new Page<>(requestBTO.getCurrentPage(), requestBTO.getPageSize());
        String requestParam = requestBTO.getRequestParams();
        LambdaQueryWrapper<TransferSettlementInfo> lambdaQueryWrapper = new LambdaQueryWrapper<TransferSettlementInfo>()
                .like(TransferSettlementInfo::getEarthquakeName, requestParam)
                .or()
                .like(TransferSettlementInfo::getTransferId, requestParam)
                .or()
                .like(TransferSettlementInfo::getEarthquakeId, requestParam)
                .or()
                .like(TransferSettlementInfo::getEarthquakeAreaName, requestParam)
                .or()
                .apply("CAST(emergency_shelters AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(temporary_shelters AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(newly_transferred AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(cumulative_transferred AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(centralized_settlement AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(distributed_settlement AS TEXT) = {0}", requestParam)
                .or()
                .apply("CAST(reporting_deadline AS TEXT) LIKE {0}", "%" + requestParam + "%")
                .or()
                .apply("CAST(system_inserttime AS TEXT) LIKE {0}", "%" + requestParam + "%")
                .or()
                .apply("CAST(earthquake_time AS TEXT) LIKE {0}", "%" + requestParam + "%");
        return this.page(transferSettlementInfo, lambdaQueryWrapper);
    }

    @Override
    public List<TransferSettlementInfo> exportExcelGetData(RequestBTO requestBTO) {
        String [] ids = requestBTO.getIds();
        List<TransferSettlementInfo> list;
        if (ids == null || ids.length == 0) {
            list = this.list().stream()
                    .sorted(Comparator.comparing(TransferSettlementInfo::getSystemInserttime, Comparator.nullsLast(Comparator.naturalOrder()))
                            .reversed()).collect(Collectors.toList());
        } else {
            list = this.listByIds(Arrays.asList(ids));
        }
        return list;
    }

}
