package com.parses.dao.datasource;

import com.parses.dao.datasource.constant.DataSourceAddressEnum;

public class DataSourceContextHolder {
    private static final ThreadLocal<DataSourceAddressEnum> CONTEXT_HOLDER = ThreadLocal.withInitial(() -> DataSourceAddressEnum.datasource1);

    public static void setCurrentDataSource(DataSourceAddressEnum dataSourceAddressEnum) {
        CONTEXT_HOLDER.set(dataSourceAddressEnum);
    }

    public static DataSourceAddressEnum getCurrentDataSource() {
        return CONTEXT_HOLDER.get();
    }

    public static void removeDataSource() {
        CONTEXT_HOLDER.remove();
    }
}
