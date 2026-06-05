package cn.bugstack.domain.trade.model.valobj;

import lombok.*;

/**
 * @description 浜ゆ槗璁㈠崟鐘舵€佹灇涓?
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum TradeOrderStatusEnumVO {

    CREATE(0, "鍒濆鍒涘缓"),
    COMPLETE(1, "娑堣垂瀹屾垚"),
    CLOSE(2, "鐢ㄦ埛閫€鍗?),
    ;

    private Integer code;
    private String info;

    public static TradeOrderStatusEnumVO valueOf(Integer code) {
        switch (code) {
            case 0:
                return CREATE;
            case 1:
                return COMPLETE;
            case 2:
                return CLOSE;
        }
        return CREATE;
    }

}
