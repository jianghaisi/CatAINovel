package cn.bugstack.infrastructure.adapter.repository;

import cn.bugstack.domain.goods.adapter.repository.IGoodsRepository;
import cn.bugstack.infrastructure.dao.IOrderDao;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @description 缁撶畻浠撳偍鏈嶅姟
 */
@Repository
public class GoodsRepository implements IGoodsRepository {

    @Resource
    private IOrderDao orderDao;

    @Override
    public void changeOrderDealDone(String orderId) {
        orderDao.changeOrderDealDone(orderId);
    }

}
