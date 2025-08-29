package com.parses.server.csc.impl;

import com.parses.dao.datasource.RoutingDataSource;
import com.parses.dao.datasource.constant.DataSourceAddressEnum;
import com.parses.dao.mapper.csc.MerchantMemberMapper;
import com.parses.dao.model.csc.MerchantMemberEntity;
import com.parses.server.csc.CscMerchantMemberServer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class CscMerchantMemberServerImpl implements CscMerchantMemberServer {
    @Resource
    private MerchantMemberMapper merchantMemberMapper;

    /*
    methodA 调用 methodB https://yuanjava.com/spring-transactions-propogation/
    methodB使用如下方式
    REQUIRED(0), 如果 methodA 回滚，methodB 也会回滚；methodB 回滚，methodA也回滚
    SUPPORTS(1), 如果 methodA 有事务，methodB 也就有事务
    MANDATORY(2),  如果methodA 有事务，methodB可以正常参与事务；如果methodA没事务，methodB 被独立调用，会抛出异常
    REQUIRES_NEW(3), 开启新事务；如果 methodB 回滚，不会影响 methodA 的事务
    NOT_SUPPORTED(4), methodA 有没有事务，methodB 都没有事务
    NEVER(5), 如果methodA 有事务，调用methodB会抛出异常。如果methodA没有事务，methodB 被独立调用则正常执行。
    NESTED(6); 进行局部回滚；如果 methodB 出现异常且事务被回滚，只会回滚到 methodB 的开始，而 methodA 的事务仍然可以继续或回滚
    */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    @RoutingDataSource(DataSourceAddressEnum.datasource2)
    public MerchantMemberEntity selectByMerchantCodeAndMerchantType(String merchantCode, String merchantType) {
        return  merchantMemberMapper.selectByMerchantCodeAndMemberType(merchantCode, merchantType);
    }
}
