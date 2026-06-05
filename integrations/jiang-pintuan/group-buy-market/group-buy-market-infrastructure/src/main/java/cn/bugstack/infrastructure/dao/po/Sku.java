package cn.bugstack.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description 鍟嗗搧淇℃伅
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sku {

    /** 鑷 */
    private Long id;
    /** 鏉ユ簮 */
    private String source;
    /** 娓犻亾 */
    private String channel;
    /** 鍟嗗搧ID */
    private String goodsId;
    /** 鍟嗗搧鍚嶇О */
    private String goodsName;
    /** 鍘熷浠锋牸 */
    private BigDecimal originalPrice;
    /** 鍒涘缓鏃堕棿 */
    private Date createTime;
    /** 鏇存柊鏃堕棿 */
    private Date updateTime;

}
