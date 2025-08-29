package com.parses;

import com.parses.util.ExlRead;
import com.parses.util.bean.ExlFeeParamBean;
import com.parses.util.constant.SheetIndex;

import java.util.List;

public class ExlReadTest {
    public static void main(String[] args) {
        ExlRead exlRead = new ExlRead("/Users/zhaokun/Downloads/费项/C5048.xlsx");
        List<ExlFeeParamBean> lists = exlRead.readExcel("ExlFeeParamBean", SheetIndex.PRODUCT_FEE, false);
        for (ExlFeeParamBean list : lists) {
            System.out.printf("%-3s", list.getTotalStage());
            System.out.printf("%-3s", list.getRankLevel());
            System.out.printf("%-8s", list.getConsumerLabel());
            System.out.printf("%-20s", list.getFeeName());
            System.out.printf("%-35s", list.getFeeCode());
            if (list.getYearRate() != null) {
                System.out.printf("%-15s", list.getYearRate());
            } else {
                System.out.printf("%-15s", "");
            }
            if (list.getBaseRate() != null) {
                System.out.printf("%-15s", list.getBaseRate());
            } else {
                System.out.printf("%-15s", "");
            }
            if (list.getPreSettleDayRate() != null) {
                System.out.printf("%-15s", list.getPreSettleDayRate());
            } else {
                System.out.printf("%-15s", "");
            }
            if (list.getYearDays() != null) {
                System.out.printf("%-15s", list.getYearDays());
            } else {
                System.out.printf("%-15s", "");
            }
            if (list.getBaseAmountType() != null) {
                System.out.printf("%-15s", list.getBaseAmountType());
            } else {
                System.out.printf("%-15s", "");
            }
            if (list.getComplexMonthRate() != null) {
                System.out.printf("%-15s", list.getComplexMonthRate());
            } else {
                System.out.printf("%-15s", "");
            }
            if (list.getCapitalAndUndertakeCapitalGuaranteeYearIRR() != null) {
                System.out.printf("%-15s", list.getCapitalAndUndertakeCapitalGuaranteeYearIRR());
            } else {
                System.out.printf("%-15s", "");
            }
            System.out.println();
        }
    }
}
