package com.parses.util;

import com.parses.util.xmlparser.Column;
import com.parses.util.xmlparser.Columns;
import com.parses.util.xmlparser.Parse;
import com.parses.util.xmlparser.Parses;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * DOM4J‌：
 * 专为高性能XML处理设计，采用优化的内存模型和灵活的API，解析速度在主流Java XML库中表现最佳，尤其适合大文件或高频操作场景
 * JAXB‌：
 * 基于数据绑定机制，需额外处理Java对象映射，解析性能略低于DOM4J，但适合规则化XML与Java对象的双向转换
 */
public class ParsesUtils {
    private static final Logger logger = LoggerFactory.getLogger(ParsesUtils.class);

    public static Document getDocByPath(String xmlFilePath) {
        SAXReader reader = new SAXReader();
        Document document;
        try {
            InputStream in = ParsesUtils.class.getResourceAsStream(xmlFilePath);
            document = reader.read(in);
        } catch (DocumentException e) {
            logger.error("读取classpath下xmlFileName文件发生异常，请检查CLASSPATH和文件名是否存在！");
            throw new RuntimeException(e.getMessage());
        }
        return document;
    }

    public static Map<String, Parse> nanalysisByDocument(String xmlFileName) {
        Document document = getDocByPath(xmlFileName);
        Element root = document.getRootElement();
        Map<String, Parse> map = new HashMap<>();
        for (Iterator iter = root.elementIterator("parse"); iter.hasNext(); ) {
            Parse nxp = new Parse();
            Element e1 = (Element) iter.next();
            Attribute id = e1.attribute("id");
            if (map.get(id.getValue()) != null) {
                logger.error("读取classpath下{}文件ID重复", xmlFileName);
                throw new RuntimeException("读取classpath下" + xmlFileName + "文件ID重复");
            }
            nxp.setId(id.getValue());

            Attribute name = e1.attribute("name");
            if (null != name) {
                nxp.setName(name.getValue());
            }
            Attribute filename = e1.attribute("filename");
            if (null != filename) {
                nxp.setFilename(filename.getValue());
            }
            Attribute filetype = e1.attribute("filetype");
            if (null != filetype) {
                nxp.setFiletype(filetype.getValue());
            }
            Attribute classname = e1.attribute("classname");
            if (null != classname) {
                nxp.setClassname(classname.getValue());
            }

            Element e2 = e1.element("columns");
            Columns columns = new Columns();
            nxp.setColumns(columns);
            List<Column> cols = new ArrayList<>();
            for (Iterator iter2 = e2.elementIterator(); iter2.hasNext(); ) {
                Element e3 = (Element) iter2.next();
                Column col = new Column();

                Attribute cidx = e3.attribute("cidx");
                if (null != cidx) {
                    col.setCidx(Integer.parseInt(cidx.getValue()));
                }
                Attribute cname = e3.attribute("cname");
                if (null != cname) {
                    col.setCname(cname.getValue());
                }
                Attribute pname = e3.attribute("pname");
                if (null != pname) {
                    col.setPname(pname.getValue());
                }
                Attribute type = e3.attribute("type");
                if (null != type) {
                    col.setType(type.getValue());
                }
                Attribute cformat = e3.attribute("cformat");
                if (null != cformat) {
                    col.setCformat(cformat.getValue());
                }
                Attribute handler = e3.attribute("handler");
                if (null != handler) {
                    col.setHandler(handler.getValue());
                }
                cols.add(col);
                columns.setColumn(cols);
            }
            map.put(id.getValue(), nxp);
        }
        return map;
    }

    public static Map<String, Parse> nanalysisByJAXBContext(String xmlFileName) {
        Map<String, Parse> map = new HashMap<>();
        ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] commonResources = patternResolver.getResources(xmlFileName);
            for (Resource commonResource : commonResources) {
                InputStream is = commonResource.getInputStream();
                JAXBContext jaxbContext = JAXBContext.newInstance(Parses.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                Parses parses = (Parses) unmarshaller.unmarshal(is);
                for (Parse parse : parses.getParse()) {
                    map.put(parse.getId(), parse);
                }
            }
        } catch (IOException | JAXBException e) {
            logger.error("getParse方法异常");
            throw new RuntimeException(e.getMessage());
        }
        return map;
    }

    public static void main(String[] args) {
        Parse nxp = nanalysisByDocument("/excelRead.xml").get("ProductPricingBean");
        System.out.println(nxp.getColumns().getColumn().get(1).getCidx());
        System.out.println(nxp.getColumns().getColumn().get(1).getCname());

        Parse parse = nanalysisByJAXBContext("/excelRead.xml").get("ProductPricingBean");
        System.out.println(parse.getColumns().getColumn().get(1).getCidx());
        System.out.println(parse.getColumns().getColumn().get(1).getCname());
    }
}
