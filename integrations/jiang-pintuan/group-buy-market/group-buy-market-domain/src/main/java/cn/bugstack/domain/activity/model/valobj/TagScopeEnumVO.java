package cn.bugstack.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @description 娲诲姩浜虹兢鏍囩浣滅敤鍩熻寖鍥存灇涓?
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum TagScopeEnumVO {

    VISIBLE(true,false,"鏄惁鍙湅瑙佹嫾鍥?),
    ENABLE(true, false,"鏄惁鍙弬涓庢嫾鍥?),
    ;

    private Boolean allow;
    private Boolean refuse;
    private String desc;

}
