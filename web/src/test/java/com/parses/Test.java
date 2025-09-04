package com.parses;

import com.parses.server.bean.ProductPricingBean;
import com.parses.server.mapping.FeeFormulaParamMapping;
import com.parses.server.mapping.FormulaParamMapping;
import com.parses.util.bean.ExlFeeParamBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Test {

    private static final Map<String, Map<String, Map<String, Integer>>> feeParamLevelRateIdListMap = new HashMap<>();

    public static void main(String[] args) throws Exception {
        InputStream inputStream = Files.newInputStream(Paths.get("/Users/zhaokun/Downloads/费项/C5059.xlsx"));
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(1);

        List<CellRangeAddress> mergedRegions = new ArrayList<>();
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            mergedRegions.add(sheet.getMergedRegion(i));
        }

        Row feeRow = sheet.getRow(0);
        Map<String, List<Integer>> feeParamIdListMap = getMergedCellMap(feeRow, 1, feeRow.getLastCellNum(), mergedRegions);

        Row paramRow = sheet.getRow(1);
        Map<String, Map<String, List<Integer>>> feeParamLevelIdListMap = new HashMap<>();
        feeParamIdListMap.forEach((feeName, paramIds) -> {
            Map<String, List<Integer>> paramLevelIdMap = getMergedCellMap(paramRow, paramIds.get(0), paramIds.get(paramIds.size() - 1) + 1, mergedRegions);
            feeParamLevelIdListMap.put(feeName, paramLevelIdMap);
        });

        Row levelRow = sheet.getRow(2);
        Set<String> allLevelSet = new HashSet<>();
        Map<String, Set<String>> feeLevelsMap = new HashMap<>();
        feeParamLevelIdListMap.forEach((feeName, paramLevelIdMap) -> {
            Set<String> feeLevelSet = new HashSet<>();
            Map<String, Map<String, Integer>> paramLevelRateIdListMap = new HashMap<>();
            paramLevelIdMap.forEach((param, levelIds) -> {
                Map<String, Integer> levelRateIdListMap = new HashMap<>();
                for (Integer levelId : levelIds) {
                    String[] levels = getMergedCellValue(sheet, mergedRegions, levelRow, levelId).trim().split("/");
                    for (String level : levels) {
                        levelRateIdListMap.put(level, levelId);
                        allLevelSet.add(level);
                        feeLevelSet.add(level);
                    }
                    feeLevelsMap.put(feeName, feeLevelSet);
                    paramLevelRateIdListMap.put(param, levelRateIdListMap);
                    feeParamLevelRateIdListMap.put(feeName, paramLevelRateIdListMap);
                }
            });
        });

        List<String> allLevelList = allLevelSet.stream().sorted().collect(Collectors.toList());
        List<ProductPricingBean> productPricingBeans = new ArrayList<>();
        for (int rowNum = 3; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null) continue;
            Integer totalStage = Integer.valueOf(getCellStringValue(row.getCell(0)));
            for (String level : allLevelList) {
                String[] consumerLabels = level.split("-");
                ProductPricingBean productPricingBean = new ProductPricingBean();
                productPricingBean.setTotalStage(totalStage);
                productPricingBean.setRankLevel(consumerLabels.length > 1 ? consumerLabels[0] : level);
                productPricingBean.setConsumerLabel(consumerLabels.length > 1 ? consumerLabels[1] : "");
                productPricingBean.setYearRate(getRateValue(sheet, mergedRegions, row, "资方", FormulaParamMapping.yearRate.getParamName(), level));
                productPricingBean.setAprMonthRate(getRateValue(sheet, mergedRegions, row, "月综合", FormulaParamMapping.complexMonthRate.getParamName(), level));
                productPricingBeans.add(productPricingBean);
            }
        }

        List<ExlFeeParamBean> lists = new ArrayList<>();
        feeParamLevelRateIdListMap.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("月综合"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .forEach((feeName, paramMap) -> {
                    for (int rowNum = 3; rowNum <= sheet.getLastRowNum(); rowNum++) {
                        Row row = sheet.getRow(rowNum);
                        if (isRowEmpty(row)) continue;
                        Integer totalStage = Integer.valueOf(getCellStringValue(row.getCell(0)));
                        for (String level : feeLevelsMap.get(feeName)) {
                            ExlFeeParamBean exlFeeParamBean = new ExlFeeParamBean();
                            String[] consumerLabels = level.split("-");
                            exlFeeParamBean.setTotalStage(totalStage);
                            exlFeeParamBean.setRankLevel(consumerLabels.length > 1 ? consumerLabels[0] : level);
                            exlFeeParamBean.setConsumerLabel(consumerLabels.length > 1 ? consumerLabels[1] : "");
                            if ("资方".equals(feeName)) {
                                exlFeeParamBean.setFeeCode(FeeFormulaParamMapping.YEAR_RATE.getFeeCode());
                                exlFeeParamBean.setFeeName(FeeFormulaParamMapping.YEAR_RATE.getFeeName());
                            } else {
                                FeeFormulaParamMapping feeParamsMapping = FeeFormulaParamMapping.getFeeParamsMappingByFeeName(feeName);
                                exlFeeParamBean.setFeeCode(feeParamsMapping.getFeeCode());
                                exlFeeParamBean.setFeeName(feeName);
                            }
                            Class<?> exlFeeParamBeanClass = exlFeeParamBean.getClass();
                            Field[] declaredFields = exlFeeParamBeanClass.getDeclaredFields();
                            paramMap.forEach((paramName, levelMap) -> {
                                FormulaParamMapping formulaParamMapping = FormulaParamMapping.getParamMappingByParamName(paramName);
                                for (Field declaredField : declaredFields) {
                                    declaredField.setAccessible(true);
                                    if (declaredField.getName().equals(formulaParamMapping.getParamCode())) {
                                        try {
                                            PropertyDescriptor pd = new PropertyDescriptor(formulaParamMapping.getParamCode(), exlFeeParamBeanClass);
                                            Method writeMethod = pd.getWriteMethod();
                                            Class<?> type = pd.getPropertyType();//获取类型
                                            Cell cell = row.getCell(levelMap.get(level), Row.CREATE_NULL_AS_BLANK);
                                            for (CellRangeAddress region : mergedRegions) {
                                                if (region.isInRange(row.getRowNum(), levelMap.get(level))) {
                                                    Row firstRow = sheet.getRow(region.getFirstRow());
                                                    cell = firstRow.getCell(region.getFirstColumn());
                                                }
                                            }
                                            switch (cell.getCellType()) {
                                                case Cell.CELL_TYPE_BLANK:
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                case Cell.CELL_TYPE_FORMULA:
                                                    cell.setCellType(Cell.CELL_TYPE_STRING);
                                                    invokeField(exlFeeParamBean, writeMethod, type, cell);
                                                    break;
                                                case Cell.CELL_TYPE_STRING:
                                                    invokeField(exlFeeParamBean, writeMethod, type, cell);
                                                    break;
                                            }
                                        } catch (IntrospectionException e) {
                                            throw new RuntimeException(e);
                                        }
                                        break;
                                    }
                                }
                            });
                            lists.add(exlFeeParamBean);
                        }
                    }
                });
        List<ExlFeeParamBean> exlFeeParamBeanList = lists.stream()
                .sorted(Comparator.comparing(ExlFeeParamBean::getTotalStage)
                        .thenComparing(ExlFeeParamBean::getRankLevel)
                        .thenComparing(ExlFeeParamBean::getConsumerLabel)
                        .thenComparing(ExlFeeParamBean::getFeeCode))
                .collect(Collectors.toList());
        inputStream.close();
    }

    private static <T> void invokeField(T o, Method writeMethod, Class<?> type, Cell cell) {
        try {
            if (type.equals(String.class)) {
                writeMethod.invoke(o, cell.getStringCellValue());
            } else if (type.equals(Integer.class)) {
                if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                    writeMethod.invoke(o, Integer.valueOf(cell.getStringCellValue()));
                }
            } else if (type.equals(BigDecimal.class)) {
                if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                    writeMethod.invoke(o, BigDecimal.valueOf(Double.parseDouble(cell.getStringCellValue())));
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isMergedCell(List<CellRangeAddress> mergedRegions, int row, int col) {
        for (CellRangeAddress region : mergedRegions) {
            if (region.isInRange(row, col) && (region.getFirstRow() != row || region.getFirstColumn() != col)) {
                return true;
            }
        }
        return false;
    }

    private static Map<String, List<Integer>> getMergedCellMap(Row row, int cellStartNum, int cellEndNum, List<CellRangeAddress> mergedRegions) {
        Map<String, List<Integer>> mergedCellMap = new HashMap<>();
        String mergedCellKey = "";
        for (int cellIndex = cellStartNum; cellIndex < cellEndNum; cellIndex++) {
            List<Integer> cellIndexList = new ArrayList<>();
            Cell cell = row.getCell(cellIndex, Row.CREATE_NULL_AS_BLANK);
            if (cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                cellIndexList.add(cellIndex);
                mergedCellKey = cell.getStringCellValue();
                mergedCellMap.put(mergedCellKey, cellIndexList);
                continue;
            }
            if (isMergedCell(mergedRegions, row.getRowNum(), cellIndex)) {
                List<Integer> paramIndex = mergedCellMap.get(mergedCellKey);
                paramIndex.add(cellIndex);
            }
        }
        return mergedCellMap;
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

    private static BigDecimal getRateValue(Sheet sheet, List<CellRangeAddress> mergedRegions, Row row, String feeName, String paramName, String level) {
        Integer colIndex = feeParamLevelRateIdListMap.get(feeName).get(paramName).get(level);
        return BigDecimal.valueOf(Double.parseDouble(getMergedCellValue(sheet, mergedRegions, row, colIndex)));
    }

    private static boolean isRowEmpty(Row row) {
        if (row == null) return true;

        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i, Row.CREATE_NULL_AS_BLANK);
            if (cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                return false;
            }
        }
        return true;
    }
}
