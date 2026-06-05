package cn.bugstack.domain.order.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description 钀ラ攢绫诲瀷
 */
@Getter
@AllArgsConstructor
public enum MarketTypeVO {


    NO_MARKET(0, "鏃犺惀閿€"),
    GROUP_BUY_MARKET(1, "鎷煎洟钀ラ攢"),
    ;

    private final Integer code;
    private final String desc;


    public static MarketTypeVO valueOf(Integer code) {
        switch (code) {
            case 0:
                return NO_MARKET;
            case 1:
                return GROUP_BUY_MARKET;
        }
        throw new RuntimeException("err code not exist!");
    }

}
