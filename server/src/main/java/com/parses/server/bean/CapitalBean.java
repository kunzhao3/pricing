package com.parses.server.bean;


import lombok.Data;

@Data
public class CapitalBean{
    /**
     * 资金方编号
     */
    private String capitalNo;

    /**
     * 当前镜像编号
     */
    private String currentMirrorNo;

    /**
     * 关联模板编号
     */
    private String templateNo;

    /**
     * 关联模板镜像编号
     */
    private String templateMirrorNo;

    /**
     * 资金方代码
     */
    private String capitalCode;

    /**
     * 资金方名称
     */
    private String capitalName;

    /**
     * 资金方开户的用户编号
     */
    private String capitalMemberNo;
    /**
     * 资方状态 0-不可用 1-可用
     */
    private Integer capitalStatus;
    /**
     * 操作员
     */
    private String operaterName;
}
