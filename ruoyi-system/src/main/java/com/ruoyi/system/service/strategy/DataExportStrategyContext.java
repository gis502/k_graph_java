package com.ruoyi.system.service.strategy;

import com.ruoyi.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 导入导出策略上下文
 *
 * @author 方
 */
@Component
public class DataExportStrategyContext {
    private final Map<String, DataExportStrategy> strategyMap;

    @Autowired
    public DataExportStrategyContext(List<DataExportStrategy> strategy) {
        strategyMap = new HashMap<>();
        strategy.forEach(strategyItem -> strategyMap.put(strategyItem.mark(), strategyItem));
    }

    /**
     * 根据flag获取对应的导出策略
     * @param flag 策略标识
     * @return 导出策略
     */
    public DataExportStrategy getStrategy(String flag) {
        DataExportStrategy strategy = strategyMap.get(flag);
        if (strategy == null) {
            throw new ServiceException("系统执行异常请联系管理员");
        }
        return strategy;
    }
}
