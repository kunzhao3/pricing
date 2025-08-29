package com.parses.server.bean;

import lombok.Data;

@Data
public class ElementDataBean {
    /**
     * 来源数据类型
     */
    private Integer dataType;
    /**
     * 来源数据编号
     */
    private String dataNo;
    /**
     * 来源数据镜像编号
     */
    private String dataMirrorNo;
    /**
     * 要素代码
     */
    private String elementCode;
    /**
     * 要素代码数据
     */
    private String elementData;
}
