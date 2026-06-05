package cn.bugstack.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @description 浜虹兢鏍囩
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrowdTags {

    /** 鑷ID */
    private Long id;
    /** 浜虹兢ID */
    private String tagId;
    /** 浜虹兢鍚嶇О */
    private String tagName;
    /** 浜虹兢鎻忚堪 */
    private String tagDesc;
    /** 浜虹兢鏍囩缁熻閲?*/
    private Integer statistics;
    /** 鍒涘缓鏃堕棿 */
    private Date createTime;
    /** 鏇存柊鏃堕棿 */
    private Date updateTime;

}
