package com.parses.util.xmlparser;

import com.google.common.collect.Lists;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@XmlRootElement(name = "parses")
@XmlAccessorType(XmlAccessType.FIELD)
public class Parses {
	@XmlElement(name = "parse")
	private List<Parse> parse =  Lists.newArrayList();
}
