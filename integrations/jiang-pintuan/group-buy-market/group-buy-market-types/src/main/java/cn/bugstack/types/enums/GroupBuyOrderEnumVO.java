package cn.bugstack.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @description 鎷煎洟璁㈠崟鏋氫妇
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum GroupBuyOrderEnumVO {

    PROGRESS(0, "鎷煎崟涓?),
    COMPLETE(1, "瀹屾垚"),
    FAIL(2, "澶辫触"),
    COMPLETE_FAIL(3, "瀹屾垚-鍚€€鍗?),
    ;

    private Integer code;
    private String info;

    public static GroupBuyOrderEnumVO valueOf(Integer code) {
        switch (code) {
            case 0:
                return PROGRESS;
            case 1:
                return COMPLETE;
            case 2:
                return FAIL;
            case 3:
                return COMPLETE_FAIL;
        }
        throw new RuntimeException("err code not exist!");
    }

}
