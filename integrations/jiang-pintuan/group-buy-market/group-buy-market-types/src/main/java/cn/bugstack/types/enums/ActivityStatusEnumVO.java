package cn.bugstack.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @description 鎷煎洟娲诲姩鐘舵€佹灇涓?
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ActivityStatusEnumVO {

    CREATE(0, "鍒涘缓"),
    EFFECTIVE(1, "鐢熸晥"),
    OVERDUE(2, "杩囨湡"),
    ABANDONED(3, "搴熷純"),
    ;

    private Integer code;
    private String info;

    public static ActivityStatusEnumVO valueOf(Integer code) {
        switch (code) {
            case 0:
                return CREATE;
            case 1:
                return EFFECTIVE;
            case 2:
                return OVERDUE;
            case 3:
                return ABANDONED;
        }
        throw new RuntimeException("err code not exist!");
    }

}
