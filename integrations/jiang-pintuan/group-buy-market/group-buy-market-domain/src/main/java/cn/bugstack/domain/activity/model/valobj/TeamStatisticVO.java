package cn.bugstack.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @description 闃熶紞缁熻鍊煎璞?
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamStatisticVO {

    // 寮€鍥㈤槦浼嶆暟閲?
    private Integer allTeamCount;
    // 鎴愬洟闃熶紞鏁伴噺
    private Integer allTeamCompleteCount;
    // 鍙傚洟浜烘暟鎬婚噺 - 涓€涓晢鍝佺殑鎬诲弬鍥汉鏁?
    private Integer allTeamUserCount;

}
