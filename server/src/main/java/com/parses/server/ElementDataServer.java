package com.parses.server;

import com.parses.server.bean.CapitalBean;
import com.parses.server.bean.ElementDataBean;

import java.util.List;

public interface ElementDataServer {
    int batchInsertCapitalElement(List<ElementDataBean> list);
    void addCapitalInfo(List<ElementDataBean> list, CapitalBean capitalBean);

}
