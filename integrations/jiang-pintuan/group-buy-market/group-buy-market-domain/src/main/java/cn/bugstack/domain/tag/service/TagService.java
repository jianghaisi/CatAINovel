package cn.bugstack.domain.tag.service;

import cn.bugstack.domain.tag.adapter.repository.ITagRepository;
import cn.bugstack.domain.tag.model.entity.CrowdTagsJobEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 浜虹兢鏍囩鏈嶅姟
 */
@Slf4j
@Service
public class TagService implements ITagService {

    @Resource
    private ITagRepository repository;

    @Override
    public void execTagBatchJob(String tagId, String batchId) {
        log.info("浜虹兢鏍囩鎵规浠诲姟 tagId:{} batchId:{}", tagId, batchId);

        // 1. 鏌ヨ鎵规浠诲姟
        CrowdTagsJobEntity crowdTagsJobEntity = repository.queryCrowdTagsJobEntity(tagId, batchId);

        // 2. 閲囬泦鐢ㄦ埛鏁版嵁 - 杩欓儴鍒嗛渶瑕侀噰闆嗙敤鎴风殑娑堣垂绫绘暟鎹紝鍚庣画鏈夌敤鎴峰彂璧锋嫾鍗曞悗鍐嶅鐞嗐€?

        // 3. 鏁版嵁鍐欏叆璁板綍
        List<String> userIdList = new ArrayList<String>() {{
            add("jiang");
            add("liergou");
            add("xfg01");
            add("xfg02");
            add("xfg03");
            add("xfg04");
            add("xfg05");
            add("xfg06");
            add("xfg07");
            add("xfg08");
            add("xfg09");
        }};

        // 4. 涓€鑸汉缇ゆ爣绛剧殑澶勭悊鍦ㄥ叕鍙镐腑锛屼細鏈変笓闂ㄧ殑鏁版嵁鏁颁粨鍥㈤槦閫氳繃鑴氭湰鏂瑰紡鍐欏叆鍒版暟鎹簱锛屽氨涓嶇敤杩欐牱涓€涓釜鎴栬€呮壒娆℃潵鍐欍€?
        for (String userId : userIdList) {
            repository.addCrowdTagsUserId(tagId, userId);
        }

        // 5. 鏇存柊浜虹兢鏍囩缁熻閲?
        repository.updateCrowdTagsStatistics(tagId, userIdList.size());
    }

}
