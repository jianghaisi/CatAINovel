package cn.bugstack.api;

import cn.bugstack.api.dto.GoodsMarketRequestDTO;
import cn.bugstack.api.dto.GoodsMarketResponseDTO;
import cn.bugstack.api.response.Response;

/**
 * @description 钀ラ攢棣栭〉鏈嶅姟鎺ュ彛
 */
public interface IMarketIndexService {

    /**
     * 鏌ヨ鎷煎洟钀ラ攢閰嶇疆
     *
     * @param goodsMarketRequestDTO 钀ラ攢鍟嗗搧淇℃伅
     * @return 钀ラ攢閰嶇疆淇℃伅
     */
    Response<GoodsMarketResponseDTO> queryGroupBuyMarketConfig(GoodsMarketRequestDTO goodsMarketRequestDTO);

}
