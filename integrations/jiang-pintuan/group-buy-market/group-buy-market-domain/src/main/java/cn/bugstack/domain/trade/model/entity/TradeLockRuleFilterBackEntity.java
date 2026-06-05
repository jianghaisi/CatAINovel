package cn.bugstack.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @description 鎷煎洟浜ゆ槗锛岃繃婊ゅ弽棣堝疄浣?
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeLockRuleFilterBackEntity {

    // 鐢ㄦ埛鍙備笌娲诲姩鐨勮鍗曢噺
    private Integer userTakeOrderCount;

    // 鎭㈠缁勯槦搴撳瓨缂撳瓨key
    private String recoveryTeamStockKey;

}
