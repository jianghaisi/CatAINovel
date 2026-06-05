package cn.bugstack.infrastructure.dao.po;

import cn.bugstack.infrastructure.dao.po.base.Page;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description 鐢ㄦ埛鎷煎崟鏄庣粏
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyOrderList extends Page {

    /** 鑷ID */
    private Long id;
    /** 鐢ㄦ埛ID */
    private String userId;
    /** 鎷煎崟缁勯槦ID */
    private String teamId;
    /** 璁㈠崟ID */
    private String orderId;
    /** 娲诲姩ID */
    private Long activityId;
    /** 娲诲姩寮€濮嬫椂闂?*/
    private Date startTime;
    /** 娲诲姩缁撴潫鏃堕棿 */
    private Date endTime;
    /** 鍟嗗搧ID */
    private String goodsId;
    /** 娓犻亾 */
    private String source;
    /** 鏉ユ簮 */
    private String channel;
    /** 鍘熷浠锋牸 */
    private BigDecimal originalPrice;
    /** 鎶樻墸閲戦 */
    private BigDecimal deductionPrice;
    /** 鏀粯閲戦 */
    private BigDecimal payPrice;
    /** 鐘舵€侊紱0鍒濆閿佸畾銆?娑堣垂瀹屾垚 */
    private Integer status;
    /** 澶栭儴浜ゆ槗鍗曞彿-纭繚澶栭儴璋冪敤鍞竴骞傜瓑 */
    private String outTradeNo;
    /** 澶栭儴浜ゆ槗鏃堕棿 */
    private Date outTradeTime;
    /** 鍞竴涓氬姟ID */
    private String bizId;
    /** 鍒涘缓鏃堕棿 */
    private Date createTime;
    /** 鏇存柊鏃堕棿 */
    private Date updateTime;

}
