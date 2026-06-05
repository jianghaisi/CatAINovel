package cn.bugstack.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @description 浜虹兢鏍囩浠诲姟
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrowdTagsJob {

    /** 鑷ID */
    private Long id;
    /** 鏍囩ID */
    private String tagId;
    /** 鎵规ID */
    private String batchId;
    /** 鏍囩绫诲瀷锛堝弬涓庨噺銆佹秷璐归噾棰濓級 */
    private Integer tagType;
    /** 鏍囩瑙勫垯锛堥檺瀹氱被鍨?N娆★級 */
    private String tagRule;
    /** 缁熻鏁版嵁锛屽紑濮嬫椂闂?*/
    private Date statStartTime;
    /** 缁熻鏁版嵁锛岀粨鏉熸椂闂?*/
    private Date statEndTime;
    /** 鐘舵€侊紱0鍒濆銆?璁″垝锛堣繘鍏ユ墽琛岄樁娈碉級銆?閲嶇疆銆?瀹屾垚 */
    private Integer status;
    /** 鍒涘缓鏃堕棿 */
    private Date createTime;
    /** 鏇存柊鏃堕棿 */
    private Date updateTime;

}
