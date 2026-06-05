package cn.bugstack.domain.tag.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @description 鎵规浠诲姟瀵硅薄
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrowdTagsJobEntity {

    /** 鏍囩绫诲瀷锛堝弬涓庨噺銆佹秷璐归噾棰濓級 */
    private Integer tagType;
    /** 鏍囩瑙勫垯锛堥檺瀹氱被鍨?N娆★級 */
    private String tagRule;
    /** 缁熻鏁版嵁锛屽紑濮嬫椂闂?*/
    private Date statStartTime;
    /** 缁熻鏁版嵁锛岀粨鏉熸椂闂?*/
    private Date statEndTime;

}
