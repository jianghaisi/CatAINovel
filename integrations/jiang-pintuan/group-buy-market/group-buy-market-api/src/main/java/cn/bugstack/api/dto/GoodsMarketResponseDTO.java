package cn.bugstack.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @description 鍟嗗搧钀ラ攢搴旂瓟瀵硅薄
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsMarketResponseDTO {

    // 娲诲姩ID
    private Long activityId;
    // 鍟嗗搧淇℃伅
    private Goods goods;
    // 缁勯槦淇℃伅锛?涓釜浜虹殑缃《銆?涓殢鏈虹殑銆岃幏鍙?0涓紝闅忔満鍙?涓€嶏級
    private List<Team> teamList;
    // 缁勯槦缁熻
    private TeamStatistic teamStatistic;

    /**
     * 鍟嗗搧淇℃伅
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Goods {
        // 鍟嗗搧ID
        private String goodsId;
        // 鍘熷浠锋牸
        private BigDecimal originalPrice;
        // 鎶樻墸閲戦
        private BigDecimal deductionPrice;
        // 鏀粯浠锋牸
        private BigDecimal payPrice;
    }

    /**
     * 缁勯槦淇℃伅
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Team {
        // 鐢ㄦ埛ID
        private String userId;
        // 鎷煎崟缁勯槦ID
        private String teamId;
        // 娲诲姩ID
        private Long activityId;
        // 鐩爣鏁伴噺
        private Integer targetCount;
        // 瀹屾垚鏁伴噺
        private Integer completeCount;
        // 閿佸崟鏁伴噺
        private Integer lockCount;
        // 鎷煎洟寮€濮嬫椂闂?- 鍙備笌鎷煎洟鏃堕棿
        private Date validStartTime;
        // 鎷煎洟缁撴潫鏃堕棿 - 鎷煎洟鏈夋晥鏃堕暱
        private Date validEndTime;
        // 鍊掕鏃?瀛楃涓? validEndTime - validStartTime
        private String validTimeCountdown;
        /** 澶栭儴浜ゆ槗鍗曞彿-纭繚澶栭儴璋冪敤鍞竴骞傜瓑 */
        private String outTradeNo;

        public static String differenceDateTime2Str(Date validStartTime, Date validEndTime) {
            if (validStartTime == null || validEndTime == null) {
                return "鏃犳晥鐨勬椂闂?;
            }

            long diffInMilliseconds = validEndTime.getTime() - validStartTime.getTime();

            if (diffInMilliseconds < 0) {
                return "宸茬粨鏉?;
            }

            long seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMilliseconds) % 60;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilliseconds) % 60;
            long hours = TimeUnit.MILLISECONDS.toHours(diffInMilliseconds) % 24;
            long days = TimeUnit.MILLISECONDS.toDays(diffInMilliseconds);

            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

    }

    /**
     * 缁勯槦缁熻
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TeamStatistic {
        // 寮€鍥㈤槦浼嶆暟閲?
        private Integer allTeamCount;
        // 鎴愬洟闃熶紞鏁伴噺
        private Integer allTeamCompleteCount;
        // 鍙傚洟浜烘暟鎬婚噺 - 涓€涓晢鍝佺殑鎬诲弬鍥汉鏁?
        private Integer allTeamUserCount;
    }

}
