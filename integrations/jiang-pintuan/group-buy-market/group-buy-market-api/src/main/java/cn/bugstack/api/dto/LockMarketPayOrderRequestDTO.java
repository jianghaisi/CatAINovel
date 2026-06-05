package cn.bugstack.api.dto;

import lombok.*;

/**
 * @description 钀ラ攢鏀粯閿佸崟璇锋眰瀵硅薄
 */
@Data
public class LockMarketPayOrderRequestDTO {

    // 鐢ㄦ埛ID
    private String userId;
    // 鎷煎崟缁勯槦ID - 鍙负绌猴紝涓虹┖鍒欏垱寤烘柊缁勯槦ID
    private String teamId;
    // 娲诲姩ID
    private Long activityId;
    // 鍟嗗搧ID
    private String goodsId;
    // 娓犻亾
    private String source;
    // 鏉ユ簮
    private String channel;
    // 澶栭儴浜ゆ槗鍗曞彿
    private String outTradeNo;
    // 鍥炶皟閰嶇疆
    private NotifyConfigVO notifyConfigVO;

    // 鍏煎閰嶇疆
    public void setNotifyUrl(String url) {
        NotifyConfigVO notifyConfigVO = new NotifyConfigVO();
        notifyConfigVO.setNotifyType("HTTP");
        notifyConfigVO.setNotifyUrl(url);
        this.notifyConfigVO = notifyConfigVO;
    }

    // 鍏煎閰嶇疆 - MQ涓嶉渶瑕佹寚瀹氾紝绯荤粺浼氬彂缁熶竴MQ娑堟伅
    public void setNotifyMQ() {
        NotifyConfigVO notifyConfigVO = new NotifyConfigVO();
        notifyConfigVO.setNotifyType("MQ");
        this.notifyConfigVO = notifyConfigVO;
    }

    // 鍥炶皟閰嶇疆
    @Data
    public static class NotifyConfigVO {
        /**
         * 鍥炶皟鏂瑰紡锛汳Q銆丠TTP
         */
        private String notifyType;
        /**
         * 鍥炶皟娑堟伅
         */
        private String notifyMQ;
        /**
         * 鍥炶皟鍦板潃
         */
        private String notifyUrl;
    }

}
