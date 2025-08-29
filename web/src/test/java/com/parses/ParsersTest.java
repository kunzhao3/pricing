package com.parses;

import com.parses.util.xmlparser.Parse;
import com.parses.util.xmlparser.Parses;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ParsersTest {
    public static void main(String[] args) throws IOException, JAXBException {
        ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        Resource[] commonResources = patternResolver.getResources("classpath*:**/excelRead.xml");
        List<Parses> list = new ArrayList<>();
        for (Resource commonResource : commonResources) {
            InputStream is = commonResource.getInputStream();
            JAXBContext jaxbContext = JAXBContext.newInstance(Parses.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Parses parses = (Parses) unmarshaller.unmarshal(is);
            list.add(parses);
        }
        for (Parses parses : list) {
            for (Parse parse : parses.getParse()) {
                System.out.printf("%-25s",parse.getId());
                System.out.printf("%-15s",parse.getName());
                System.out.printf("%-15s",parse.getClassname());
                System.out.println();
                parse.getColumns().getColumn().forEach(column -> {
                    System.out.printf("%-25s",column.getCidx());
                    System.out.printf("%-15s",column.getCname());
                    System.out.printf("%-15s",column.getPname());
                    System.out.println();
                });
                System.out.println("------------------");
            }
        }
    }
}
