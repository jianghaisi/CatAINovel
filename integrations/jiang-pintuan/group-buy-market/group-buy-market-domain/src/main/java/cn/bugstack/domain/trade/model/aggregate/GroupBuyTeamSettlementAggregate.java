package cn.bugstack.domain.trade.model.aggregate;

import cn.bugstack.domain.trade.model.entity.GroupBuyTeamEntity;
import cn.bugstack.domain.trade.model.entity.TradePaySuccessEntity;
import cn.bugstack.domain.trade.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 鎷煎洟缁勯槦缁撶畻鑱氬悎
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyTeamSettlementAggregate {

    /** 鐢ㄦ埛瀹炰綋瀵硅薄 */
    private UserEntity userEntity;
    /** 鎷煎洟缁勯槦瀹炰綋瀵硅薄 */
    private GroupBuyTeamEntity groupBuyTeamEntity;
    /** 浜ゆ槗鏀粯璁㈠崟瀹炰綋瀵硅薄 */
    private TradePaySuccessEntity tradePaySuccessEntity;

}
