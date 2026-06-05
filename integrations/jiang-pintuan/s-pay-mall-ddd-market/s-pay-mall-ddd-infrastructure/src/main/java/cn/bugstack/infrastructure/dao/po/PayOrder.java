package cn.bugstack.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayOrder {

    // 鑷ID
    private Long id;
    // 鐢ㄦ埛ID
    private String userId;
    // 鍟嗗搧ID
    private String productId;
    // 鍟嗗搧鍚嶇О
    private String productName;
    // 璁㈠崟ID
    private String orderId;
    // 涓嬪崟鏃堕棿
    private Date orderTime;
    // 璁㈠崟閲戦
    private BigDecimal totalAmount;
    // 璁㈠崟鐘舵€侊紱create-鍒涘缓瀹屾垚銆乸ay_wait-绛夊緟鏀粯銆乸ay_success-鏀粯鎴愬姛銆乨eal_done-浜ゆ槗瀹屾垚銆乧lose-璁㈠崟鍏冲崟
    private String status;
    // 鏀粯淇℃伅
    private String payUrl;
    // 鏀粯鏃堕棿
    private Date payTime;
    // 钀ラ攢绫诲瀷锛?鏃犺惀閿€銆?鎷煎洟钀ラ攢
    private Integer marketType;
    // 钀ラ攢閲戦锛涗紭鎯犻噾棰?
    private BigDecimal marketDeductionAmount;
    // 鏀粯閲戦
    private BigDecimal payAmount;
    // 鍒涘缓鏃堕棿
    private Date createTime;
    // 鏇存柊鏃堕棿
    private Date updateTime;

}
