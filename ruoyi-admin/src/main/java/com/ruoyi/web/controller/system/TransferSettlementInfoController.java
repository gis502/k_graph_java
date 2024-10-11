package com.ruoyi.web.controller.system;

import com.ruoyi.system.service.TransferSettlementInfoService;
import com.ruoyi.system.domain.entity.TransferSettlementInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transfer")
public class TransferSettlementInfoController {

    @Autowired
    private TransferSettlementInfoService transferSettlementInfoService;

    /**
     * 获取最新的转移安置信息
     */
    @GetMapping("/getTotal")
    public ResponseEntity<List<TransferSettlementInfo>> getTotal(@RequestParam("eqid") String eqid) {
        List<TransferSettlementInfo> transferSettlementInfoList = transferSettlementInfoService.getTotal(eqid);
        return ResponseEntity.ok(transferSettlementInfoList);
    }

    @GetMapping("/getTransferInfo")
    public ResponseEntity<List<TransferSettlementInfo>> getTransferInfo(@RequestParam("eqid") String eqid) {
        List<TransferSettlementInfo> transferInfo = transferSettlementInfoService.getTransferInfo(eqid);
        return ResponseEntity.ok(transferInfo);
    }
}
