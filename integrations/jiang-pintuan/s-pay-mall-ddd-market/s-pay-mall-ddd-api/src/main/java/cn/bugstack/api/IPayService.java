package cn.bugstack.api;

import cn.bugstack.api.dto.CreatePayRequestDTO;
import cn.bugstack.api.dto.NotifyRequestDTO;
import cn.bugstack.api.dto.QueryOrderListRequestDTO;
import cn.bugstack.api.dto.QueryOrderListResponseDTO;
import cn.bugstack.api.dto.RefundOrderRequestDTO;
import cn.bugstack.api.dto.RefundOrderResponseDTO;
import cn.bugstack.api.response.Response;

public interface IPayService {

    Response<String> createPayOrder(CreatePayRequestDTO createPayRequestDTO);

    /**
     * 鎷煎洟缁撶畻鍥炶皟
     *
     * @param requestDTO 璇锋眰瀵硅薄
     * @return 杩斿弬锛宻uccess 鎴愬姛
     */
    String groupBuyNotify(NotifyRequestDTO requestDTO);

    /**
     * 鏌ヨ鐢ㄦ埛璁㈠崟鍒楄〃
     *
     * @param requestDTO 璇锋眰瀵硅薄
     * @return 璁㈠崟鍒楄〃
     */
    Response<QueryOrderListResponseDTO> queryUserOrderList(QueryOrderListRequestDTO requestDTO);

    /**
     * 鐢ㄦ埛閫€鍗?
     *
     * @param requestDTO 璇锋眰瀵硅薄
     * @return 閫€鍗曠粨鏋?
     */
    Response<RefundOrderResponseDTO> refundOrder(RefundOrderRequestDTO requestDTO);

    Response<String> activePayNotify(String outTradeNo);
}
