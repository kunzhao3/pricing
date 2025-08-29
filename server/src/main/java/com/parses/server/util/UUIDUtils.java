package com.parses.server.util;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class UUIDUtils {

    public static String genSimpleUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static void main(String[] args) {
        List<Item> items = Arrays.asList(
                new Item("A", new BigDecimal("10.50")),
                new Item("A", new BigDecimal("20.50")),
                new Item("B", new BigDecimal("30.10"))
        );

        Map<String, BigDecimal> sumByGroup = items.stream()
                .collect(Collectors.groupingBy(
                        Item::getGroup,
                        Collectors.reducing(BigDecimal.ZERO, Item::getValue, BigDecimal::add)
                ));

        sumByGroup.forEach((key, value) -> System.out.println(key + ": " + value));

        for (int i = 0; i < 10; i++) {
            System.out.println(genSimpleUUID());
        }

        String  input ="insert into t_pec_element_data\n" +
                "    (f_data_type, f_data_no,f_data_mirror_no, f_element_code, f_element_data)\n" +
                "    values\n" +
                "\n" +
                "    (?, ?, ?, ?, ?)\n" +
                "     ,\n" +
                "    (?, ?, ?, ?, ?)\n" +
                "     ,\n" +
                "    (?, ?, ?, ?, ?)\n" +
                ";";
        String output = input.replaceAll("values\\n", "values").replaceAll("\\)\\s*,\\s*\\n\\s*\\(", "),\n    (");
        System.out.println(output);
    }

    static class Item {
        private String group;
        private BigDecimal value;

        public Item(String group, BigDecimal value) {
            this.group = group;
            this.value = value;
        }

        public String getGroup() {
            return group;
        }

        public BigDecimal getValue() {
            return value;
        }
    }
}


















