package cn.bugstack.domain.trade.model.aggregate;

import cn.bugstack.domain.trade.model.entity.PayActivityEntity;
import cn.bugstack.domain.trade.model.entity.PayDiscountEntity;
import cn.bugstack.domain.trade.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @description 鎷煎洟璁㈠崟鑱氬悎瀵硅薄锛涜仛鍚堝彲浠ョ悊瑙ｇ敤鍚勪釜鍥涜偄銆佽韩浣撱€佸ご绛夌粍瑁呭嚭鏉ヤ竴涓汉
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyOrderAggregate {

    /** 鐢ㄦ埛瀹炰綋瀵硅薄 */
    private UserEntity userEntity;
    /** 鏀粯娲诲姩瀹炰綋瀵硅薄 */
    private PayActivityEntity payActivityEntity;
    /** 鏀粯浼樻儬瀹炰綋瀵硅薄 */
    private PayDiscountEntity payDiscountEntity;
    /** 宸插弬涓庢嫾鍥㈤噺 */
    private Integer userTakeOrderCount;

}
