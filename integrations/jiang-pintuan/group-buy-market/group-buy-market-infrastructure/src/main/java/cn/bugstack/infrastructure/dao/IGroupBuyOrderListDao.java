package cn.bugstack.infrastructure.dao;

import cn.bugstack.infrastructure.dao.po.GroupBuyOrderList;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @description 鐢ㄦ埛鎷煎崟鏄庣粏
 */
@Mapper
public interface IGroupBuyOrderListDao {

    void insert(GroupBuyOrderList groupBuyOrderListReq);

    GroupBuyOrderList queryGroupBuyOrderRecordByOutTradeNo(GroupBuyOrderList groupBuyOrderListReq);

    Integer queryOrderCountByActivityId(GroupBuyOrderList groupBuyOrderListReq);

    int updateOrderStatus2COMPLETE(GroupBuyOrderList groupBuyOrderListReq);

    List<String> queryGroupBuyCompleteOrderOutTradeNoListByTeamId(String teamId);

    List<GroupBuyOrderList> queryInProgressUserGroupBuyOrderDetailListByUserId(GroupBuyOrderList groupBuyOrderListReq);

    List<GroupBuyOrderList> queryInProgressUserGroupBuyOrderDetailListByRandom(GroupBuyOrderList groupBuyOrderListReq);

    List<GroupBuyOrderList> queryInProgressUserGroupBuyOrderDetailListByActivityId(Long activityId);

    int unpaid2Refund(GroupBuyOrderList groupBuyOrderListReq);

    int paid2Refund(GroupBuyOrderList groupBuyOrderListReq);

    int paidTeam2Refund(GroupBuyOrderList groupBuyOrderListReq);

    /**
     * 鏌ヨ瓒呮椂鏈敮浠樿鍗曞垪琛?
     * 鏉′欢锛氬綋鍓嶆椂闂翠笉鍦ㄦ椿鍔ㄦ椂闂磋寖鍥村唴銆佺姸鎬佷负0锛堝垵濮嬮攣瀹氾級銆乷ut_trade_time涓虹┖
     * @return 瓒呮椂鏈敮浠樿鍗曞垪琛紝闄愬埗10鏉?
     */
    List<GroupBuyOrderList> queryTimeoutUnpaidOrderList();

}
