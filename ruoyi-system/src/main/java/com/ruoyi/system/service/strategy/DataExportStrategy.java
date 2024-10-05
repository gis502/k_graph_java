package com.ruoyi.system.service.strategy;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.bto.RequestBTO;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author 方
 * 导入导出策略接口
 */
public interface DataExportStrategy {
    /**
     * 获取分页数据
     *
     * @param requestBTO
     * @return
     */
    IPage getPage(RequestBTO requestBTO);


    /**
     * 获取导出数据
     *
     * @param requestBTO
     * @return
     */
    List<?> exportExcelGetData(RequestBTO requestBTO);

    /**
     * 返回策略的标识符
     *
     * @return 策略标识符
     */
    default String mark() {
        String className = this.getClass().getSimpleName();
        if (className.endsWith("ServiceImpl")) {
            className = className.substring(0, className.length() - 11);
        }
        return className;
    }

    /**
     * 返回导出数据的实体类
     *
     * @return 导出数据的实体类
     */

    default Class<?> getExportExcelClass() {
        // 反射获取当前实现类的泛型父类
        Type superclass = this.getClass().getGenericSuperclass();
        // 检查父类是否为泛型类型
        if (superclass instanceof ParameterizedType) {
            // 获取泛型参数
            Type[] actualTypeArguments = ((ParameterizedType) superclass).getActualTypeArguments();
            if (actualTypeArguments.length > 1) {
                // MyBatis-Plus的ServiceImpl通常会将实体类作为第二个泛型参数
                return (Class<?>) actualTypeArguments[1];
            }
        }
        throw new ServiceException("无法获取实体类的类型");
    }

    String deleteData(List<Map<String, Object>> idsList);
}
