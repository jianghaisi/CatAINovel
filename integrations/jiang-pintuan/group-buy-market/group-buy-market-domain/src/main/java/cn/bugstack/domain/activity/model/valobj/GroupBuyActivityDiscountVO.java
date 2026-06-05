package cn.bugstack.domain.activity.model.valobj;

import cn.bugstack.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Objects;

/**
 * @description 鎷煎洟娲诲姩钀ラ攢閰嶇疆鍊煎璞?
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyActivityDiscountVO {

    /**
     * 娲诲姩ID
     */
    private Long activityId;
    /**
     * 娲诲姩鍚嶇О
     */
    private String activityName;
    /**
     * 鏉ユ簮
     */
    private String source;
    /**
     * 娓犻亾
     */
    private String channel;
    /**
     * 鍟嗗搧ID
     */
    private String goodsId;
    /**
     * 鎶樻墸閰嶇疆
     */
    private GroupBuyDiscount groupBuyDiscount;
    /**
     * 鎷煎洟鏂瑰紡锛?鑷姩鎴愬洟銆?杈炬垚鐩爣鎷煎洟锛?
     */
    private Integer groupType;
    /**
     * 鎷煎洟娆℃暟闄愬埗
     */
    private Integer takeLimitCount;
    /**
     * 鎷煎洟鐩爣
     */
    private Integer target;
    /**
     * 鎷煎洟鏃堕暱锛堝垎閽燂級
     */
    private Integer validTime;
    /**
     * 娲诲姩鐘舵€侊紙0鍒涘缓銆?鐢熸晥銆?杩囨湡銆?搴熷純锛?
     */
    private Integer status;
    /**
     * 娲诲姩寮€濮嬫椂闂?
     */
    private Date startTime;
    /**
     * 娲诲姩缁撴潫鏃堕棿
     */
    private Date endTime;
    /**
     * 浜虹兢鏍囩瑙勫垯鏍囪瘑
     */
    private String tagId;
    /**
     * 浜虹兢鏍囩瑙勫垯鑼冨洿
     */
    private String tagScope;

    /**
     * 鍙闄愬埗
     * 鍙瀛樺湪杩欐牱涓€涓€硷紝閭ｄ箞棣栨鑾峰緱鐨勯粯璁ゅ€煎氨鏄?false
     */
    public boolean isVisible() {
        if (StringUtils.isBlank(this.tagScope)) return TagScopeEnumVO.VISIBLE.getAllow();
        String[] split = this.tagScope.split(Constants.SPLIT);
        if (split.length > 0 && Objects.equals(split[0], "1") && StringUtils.isNotBlank(split[0])) {
            return TagScopeEnumVO.VISIBLE.getRefuse();
        }
        return TagScopeEnumVO.VISIBLE.getAllow();
    }

    /**
     * 鍙備笌闄愬埗
     * 鍙瀛樺湪杩欐牱涓€涓€硷紝閭ｄ箞棣栨鑾峰緱鐨勯粯璁ゅ€煎氨鏄?false
     */
    public boolean isEnable() {
        if (StringUtils.isBlank(this.tagScope)) return TagScopeEnumVO.VISIBLE.getAllow();
        String[] split = this.tagScope.split(Constants.SPLIT);
        if (split.length == 2 && Objects.equals(split[1], "2") && StringUtils.isNotBlank(split[1])) {
            return TagScopeEnumVO.ENABLE.getRefuse();
        }
        if (split.length == 1 && Objects.equals(split[0], "2")) {
            return TagScopeEnumVO.ENABLE.getRefuse();
        }
        return TagScopeEnumVO.ENABLE.getAllow();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GroupBuyDiscount {
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
        private DiscountTypeEnum discountType;

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
    }

}
