package com.parses;

import lombok.Data;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.*;

public class ExcelMergeCellParser {

    private static final Map<String, Map<String, Integer>> levelColumnMap = new HashMap<>();

    public static void main(String[] args) throws Exception {
        Workbook workbook = WorkbookFactory.create(Files.newInputStream(Paths.get("/Users/zhaokun/Downloads/1.xlsx")));
        Sheet sheet = workbook.getSheetAt(0);

        List<CellRangeAddress> mergedRegions = new ArrayList<>();
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            mergedRegions.add(sheet.getMergedRegion(i));
        }

        Map<String, List<Integer>> feeLevelIndexMap = new HashMap<>();
        Row feeRow = sheet.getRow(0);
        String fee = "";
        for (int cellIndex = 1; cellIndex < feeRow.getLastCellNum(); cellIndex++) {
            List<Integer> levelIndexList = new ArrayList<>();
            Cell cell = feeRow.getCell(cellIndex, Row.CREATE_NULL_AS_BLANK);
            if (cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                levelIndexList.add(cellIndex);
                fee = cell.getStringCellValue();
                feeLevelIndexMap.put(fee, levelIndexList);
                continue;
            }
            if (isMergedCell(mergedRegions, 0, cellIndex)) {
                List<Integer> levelIndex = feeLevelIndexMap.get(fee);
                levelIndex.add(cellIndex);
            }
        }
        Row levelRow = sheet.getRow(1);
        Set<String> levelSet = new HashSet<>();
        feeLevelIndexMap.forEach((feeKey, levelIndexValues) -> {
            Map<String, Integer> levelMap = new HashMap<>();
            for (Integer levelIndex : levelIndexValues) {
                Cell cell = levelRow.getCell(levelIndex);
                String[] levels = cell.getStringCellValue().trim().split("/");
                for (String level : levels) {
                    levelMap.put(level, levelIndex);
                    levelSet.add(level);
                }
            }
            levelColumnMap.put(feeKey, levelMap);
        });

        List<RateData> results = new ArrayList<>();
        for (int rowNum = 2; rowNum < sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null) continue;

            for (String level : levelSet) {
                RateData data = new RateData();
                data.setTotalStage(Integer.valueOf(getCellStringValue(row.getCell(0))));
                String[] consumerLabels = level.split("-");
                data.setRankLevel(consumerLabels.length > 1 ? consumerLabels[0] : level);
                data.setConsumerLabel(consumerLabels.length > 1 ? consumerLabels[1] : "");
                // 处理合并单元格值
                data.setYearRate(getRateValue(sheet, mergedRegions, row, "年利率", level));
                data.setComplexMonthRate(getRateValue(sheet, mergedRegions, row, "月综合费率", level));
                data.setServiceFee(getRateValue(sheet, mergedRegions, row, "服务费", level));
                data.setAssetManageFee(getRateValue(sheet, mergedRegions, row, "资产管理费", level));
                data.setOverdueLiquidatedDamages(getRateValue(sheet, mergedRegions, row, "逾期违约金", level));
                data.setPenaltyInterest(getRateValue(sheet, mergedRegions, row, "罚息", level));
                data.setGuaranteeFee(getRateValue(sheet, mergedRegions, row, "担保费", level));
                results.add(data);
            }
        }
        results.forEach(System.out::println);
    }

    private static String getRateValue(Sheet sheet, List<CellRangeAddress> mergedRegions, Row row, String rateType, String level) {
        Integer colIndex = levelColumnMap.get(rateType).get(level);
        if (colIndex == null) return "";
        if (isMergedCell(mergedRegions, row.getRowNum(), colIndex)) {
            return getMergedCellValue(sheet, mergedRegions, row, colIndex);
        }
        Cell cell = row.getCell(colIndex);
        return getCellStringValue(cell);
    }

    private static boolean isMergedCell(List<CellRangeAddress> mergedRegions, int row, int col) {
        for (CellRangeAddress region : mergedRegions) {
            if (region.isInRange(row, col) &&
                    (region.getFirstRow() != row || region.getFirstColumn() != col)) {
                return true;
            }
        }
        return false;
    }

    private static String getMergedCellValue(Sheet sheet, List<CellRangeAddress> mergedRegions, Row row, int column) {
        for (CellRangeAddress region : mergedRegions) {
            if (region.isInRange(row.getRowNum(), column)) {
                Row firstRow = sheet.getRow(region.getFirstRow());
                Cell firstCell = firstRow.getCell(region.getFirstColumn());
                return getCellStringValue(firstCell);
            }
        }
        return getCellStringValue(row.getCell(column));
    }

    private static String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                return formatExcelNumber(cell.getNumericCellValue());
            default:
                return "";
        }
    }

    private static String formatExcelNumber(double value) {
        if (value == 0) {
            return "0.00";
        }
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(10); // 最大小数位数
        return nf.format(value).replaceAll("0*$", "").replaceAll("\\.$", "");
    }

    @Data
    public static class RateData {
        private Integer totalStage; // 期数
        private String rankLevel; // 等级(A/B/C/D)
        private String consumerLabel;// 标签(A0001)
        private String yearRate; // 年利率
        private String complexMonthRate; // 月综合费率
        private String serviceFee; // 服务费
        private String assetManageFee; // 资产管理费
        private String overdueLiquidatedDamages; // 逾期违约金
        private String penaltyInterest; // 罚息-日费率
        private String guaranteeFee;// 担保费
    }
}
