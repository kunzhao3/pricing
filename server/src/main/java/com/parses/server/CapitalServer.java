package com.parses.server;

import com.parses.server.bean.CapitalBean;

public interface CapitalServer {

    int insertCapital(CapitalBean capitalBean);

    CapitalBean createCapital(String capitalCode);
}
