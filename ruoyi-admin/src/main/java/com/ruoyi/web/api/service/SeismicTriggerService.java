package com.ruoyi.web.api.service;

import com.ruoyi.common.constant.MessageConstants;
import com.ruoyi.common.exception.ParamsIsEmptyException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.dto.EqEventTriggerDTO;
import com.ruoyi.system.domain.entity.EqList;
import com.ruoyi.system.service.impl.EqListServiceImpl;
import com.ruoyi.web.api.ThirdPartyCommonApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author: xiaodemos
 * @date: 2024-11-26 2:45
 * @description: 地震触发业务逻辑
 */


@Service
@Transactional
public class SeismicTriggerService {
    @Resource
    private ThirdPartyCommonApi thirdPartyCommonApi;
    @Resource
    private EqListServiceImpl eqListService;

    /**
     * @param params 手动触发的地震事件参数
     * @author: xiaodemos
     * @date: 2024/11/26 2:53
     * @description: 地震事件触发时，会自动触发灾损评估接口，返回灾损评估结果
     * @return: 返回人员伤亡、经济损失、建筑破坏的数据
     */
    public String seismicEventTrigger(EqEventTriggerDTO params) {
        // 1.把前端上传的数据保存到对方的数据库中
        String eqqueueId = thirdPartyCommonApi.getSeismicTriggerByPost(params);

        // 2.如果返回的结果是一个字符串的话 表明数据已经插入成功，如果不是，则事务回滚
        if (StringUtils.isEmpty(eqqueueId)) {
            throw new ParamsIsEmptyException(MessageConstants.RETURN_PARAMS_IS_EMPTY);
        }

        // 3.插入到对方数据库成功后把数据插入到我们自己的数据库中
//        eqListService.save(params);
//        eqListService.upload();
        // 4.插入到我们数据库后 立即进行灾损评估。



        // 5.灾损评估需要等待10 - 15 min，所以这里需要使用异步调用。
        // 6.如果15分钟后没有拿到灾损评估结果，则进行事务回滚。
        // 7.拿到后评估结果后，保存到我们的数据库中。图片或者地址需要下载下来保存到本地
        // 8.额外写一个接口需要反复的读取我们自己数据库中的评估数据。
        // 9.拿到数据后立刻返回
        return "";
    }

    //TODO 转换都放着

}
