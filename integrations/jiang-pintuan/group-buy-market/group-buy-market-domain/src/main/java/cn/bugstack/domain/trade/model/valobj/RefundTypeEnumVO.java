package cn.bugstack.domain.trade.model.valobj;

import cn.bugstack.domain.trade.model.entity.TradeRefundOrderEntity;
import cn.bugstack.types.enums.GroupBuyOrderEnumVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

/**
 * 閫€鍗曠被鍨嬫灇涓?
 *
 * 2025/7/11 18:59
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum RefundTypeEnumVO {

    UNPAID_UNLOCK("unpaid_unlock", "unpaid2RefundStrategy", "鏈敮浠橈紝鏈垚鍥?) {
        @Override
        public boolean matches(GroupBuyOrderEnumVO groupBuyOrderEnumVO, TradeOrderStatusEnumVO tradeOrderStatusEnumVO) {
            return GroupBuyOrderEnumVO.PROGRESS.equals(groupBuyOrderEnumVO) && TradeOrderStatusEnumVO.CREATE.equals(tradeOrderStatusEnumVO);
        }
    },
    
    PAID_UNFORMED("paid_unformed", "paid2RefundStrategy", "宸叉敮浠橈紝鏈垚鍥?) {
        @Override
        public boolean matches(GroupBuyOrderEnumVO groupBuyOrderEnumVO, TradeOrderStatusEnumVO tradeOrderStatusEnumVO) {
            return GroupBuyOrderEnumVO.PROGRESS.equals(groupBuyOrderEnumVO) && TradeOrderStatusEnumVO.COMPLETE.equals(tradeOrderStatusEnumVO);
        }
    },
    
    PAID_FORMED("paid_formed", "paidTeam2RefundStrategy", "宸叉敮浠橈紝宸叉垚鍥?) {
        @Override
        public boolean matches(GroupBuyOrderEnumVO groupBuyOrderEnumVO, TradeOrderStatusEnumVO tradeOrderStatusEnumVO) {
            // 瀹屾垚銆佸畬鎴愬惈閫€鍗曪紝閮藉仛姝ゅ鐞?
            return (GroupBuyOrderEnumVO.COMPLETE.equals(groupBuyOrderEnumVO) || GroupBuyOrderEnumVO.COMPLETE_FAIL.equals(groupBuyOrderEnumVO))
                    && TradeOrderStatusEnumVO.COMPLETE.equals(tradeOrderStatusEnumVO);
        }
    },
    ;

    private String code;
    private String strategy;
    private String info;

    /**
     * 鎶借薄鏂规硶锛岀敱姣忎釜鏋氫妇鍊煎疄鐜拌嚜宸辩殑鍖归厤閫昏緫
     */
    public abstract boolean matches(GroupBuyOrderEnumVO groupBuyOrderEnumVO, TradeOrderStatusEnumVO tradeOrderStatusEnumVO);

    /**
     * 鏍规嵁鐘舵€佺粍鍚堣幏鍙栧搴旂殑閫€娆剧瓥鐣ユ灇涓?
     */
    public static RefundTypeEnumVO getRefundStrategy(GroupBuyOrderEnumVO groupBuyOrderEnumVO, TradeOrderStatusEnumVO tradeOrderStatusEnumVO) {
        return Arrays.stream(values())
                .filter(refundType -> refundType.matches(groupBuyOrderEnumVO, tradeOrderStatusEnumVO))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("涓嶆敮鎸佺殑閫€娆剧姸鎬佺粍鍚? groupBuyOrderStatus=" + groupBuyOrderEnumVO + ", tradeOrderStatus=" + tradeOrderStatusEnumVO));
    }

    public static RefundTypeEnumVO getRefundTypeEnumVOByCode(String code) {
        switch (code) {
            case "unpaid_unlock":
                return UNPAID_UNLOCK;
            case "paid_unformed":
                return PAID_UNFORMED;
            case "paid_formed":
                return PAID_FORMED;
        }
        throw new RuntimeException("閫€鍗曠被鍨嬫灇涓惧€间笉瀛樺湪: " + code);
    }

}
