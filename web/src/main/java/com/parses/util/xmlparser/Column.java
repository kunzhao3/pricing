package com.parses.util.xmlparser;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Column {
    /**
     * excel 列索引
     */
    @XmlAttribute(name = "cidx")
    private int cidx;
    /**
     * excel 列名称
     */
    @XmlAttribute(name = "cname")
    private String cname;
    /**
     * java 属性名
     */
    @XmlAttribute(name = "pname")
    private String pname;
    /**
     * java 数据类型 如：String,Integer,Long,Date
     */
    @XmlAttribute(name = "type")
    private String type;
    /**
     * 数据格式规则 如: yyyy-MM-dd
     */
    @XmlAttribute(name = "cformat")
    private String cformat;
    /**
     * 数据处理类
     */
    @XmlAttribute(name = "handler")
    private String handler;
}
