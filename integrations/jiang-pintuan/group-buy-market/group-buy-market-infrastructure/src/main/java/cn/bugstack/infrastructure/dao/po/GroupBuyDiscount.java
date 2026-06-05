package cn.bugstack.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @description 鎶樻墸閰嶇疆
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyDiscount {

    /**
     * 鑷ID
     */
    private Long id;

    /**
     * 鎶樻墸ID
     */
    private String discountId;

    /**
     * 鎶樻墸鏍囬
     */
    private String discountName;

    /**
     * 鎶樻墸鎻忚堪
     */
    private String discountDesc;

    /**
     * 鎶樻墸绫诲瀷锛?:base銆?:tag锛?
     */
    private Integer discountType;

    /**
     * 钀ラ攢浼樻儬璁″垝锛圸J:鐩村噺銆丮J:婊″噺銆丯鍏冭喘锛?
     */
    private String marketPlan;

    /**
     * 钀ラ攢浼樻儬琛ㄨ揪寮?
     */
    private String marketExpr;

    /**
     * 浜虹兢鏍囩锛岀壒瀹氫紭鎯犻檺瀹?
     */
    private String tagId;

    /**
     * 鍒涘缓鏃堕棿
     */
    private Date createTime;

    /**
     * 鏇存柊鏃堕棿
     */
    private Date updateTime;

    public static String cacheRedisKey(String discountId) {
        return "group_buy_market_cn.bugstack.infrastructure.dao.po.GroupBuyDiscount_" + discountId;
    }

}
