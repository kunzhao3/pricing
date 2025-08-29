package com.parses.util.xmlparser;

import lombok.Data;

import javax.xml.bind.annotation.*;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Parse {
    @XmlAttribute(name = "id")
    private String id;
    @XmlAttribute(name = "name")
    private String name;
    @XmlAttribute(name = "filename")
    private String filename;//可以扩展动态名字
    @XmlAttribute(name = "filetype")
    private String filetype;//可扩展不同的文件类型
    @XmlAttribute(name = "classname")
    private String classname;
    @XmlElement(name = "columns")
    private Columns columns;
}
