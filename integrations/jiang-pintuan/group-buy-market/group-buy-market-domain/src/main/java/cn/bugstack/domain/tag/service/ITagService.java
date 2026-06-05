package cn.bugstack.domain.tag.service;

/**
 * @description 浜虹兢鏍囩鏈嶅姟鎺ュ彛
 */
public interface ITagService {

    /**
     * 鎵ц浜虹兢鏍囩鎵规浠诲姟
     *
     * @param tagId   浜虹兢ID
     * @param batchId 鎵规ID
     */
    void execTagBatchJob(String tagId, String batchId);

}
