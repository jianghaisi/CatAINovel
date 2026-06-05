package cn.bugstack.infrastructure.gateway.dto;

import lombok.*;

/**
 * @description 鑾峰彇寰俊鐧诲綍浜岀淮鐮佽姹傚璞?
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeixinQrCodeRequestDTO {

    private int expire_seconds;
    private String action_name;
    private ActionInfo action_info;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ActionInfo {
        Scene scene;

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Scene {
            int scene_id;
            String scene_str;
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public enum ActionNameTypeVO {
        QR_SCENE("QR_SCENE", "涓存椂鐨勬暣鍨嬪弬鏁板€?),
        QR_STR_SCENE("QR_STR_SCENE", "涓存椂鐨勫瓧绗︿覆鍙傛暟鍊?),
        QR_LIMIT_SCENE("QR_LIMIT_SCENE", "姘镐箙鐨勬暣鍨嬪弬鏁板€?),
        QR_LIMIT_STR_SCENE("QR_LIMIT_STR_SCENE", "姘镐箙鐨勫瓧绗︿覆鍙傛暟鍊?);

        private String code;
        private String info;
    }

}
