package com.parses.util;

import com.parses.server.bean.ProductPricingBean;
import com.parses.server.mapping.FeeFormulaParamMapping;
import com.parses.server.mapping.FormulaParamMapping;
import com.parses.util.bean.ExlFeeParamBean;
import com.parses.util.xmlparser.Column;
import com.parses.util.xmlparser.Parse;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
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

public class ExlRead {
    public static final String fileName = "/excelRead.xml";
    Workbook wb;

    public ExlRead(String path) {
        try {
            InputStream inp = Files.newInputStream(Paths.get(path));
            wb = WorkbookFactory.create(inp);
        } catch (IOException | InvalidFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> getDataByColumn(int sheetIndex, boolean readHead, Parse parse) {
        List<T> dataList = new ArrayList<>();
        try {
            Class<?> clazz = Class.forName(parse.getClassname());
            List<Column> columns = parse.getColumns().getColumn();
            if (null == columns || columns.isEmpty()) {
                throw new NullPointerException(fileName + "文件没有配置column");
            }
            Sheet sheet = wb.getSheetAt(sheetIndex);
            for (Row row : sheet) {
                if (!readHead) {
                    if (row.getRowNum() == 0) continue;
                }
                if (isRowEmpty(row)) {
                    continue;
                }
                T o = (T) clazz.newInstance();
                for (Column column : columns) {
                    Cell cell = row.getCell(column.getCidx(), Row.CREATE_NULL_AS_BLANK);
                    String pn = column.getPname();
                    PropertyDescriptor pd = new PropertyDescriptor(pn, clazz);
                    Method writeMethod = pd.getWriteMethod();
                    Class<?> type = pd.getPropertyType();//获取类型
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_BLANK:
                            break;
                        case Cell.CELL_TYPE_BOOLEAN:
                            writeMethod.invoke(o, cell.getBooleanCellValue());
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                writeMethod.invoke(o, String.valueOf(cell.getDateCellValue()));
                            } else {
                                cell.setCellType(Cell.CELL_TYPE_STRING);
                                invokeField(o, writeMethod, type, cell);
                            }
                            break;
                        case Cell.CELL_TYPE_STRING:
                            invokeField(o, writeMethod, type, cell);
                            break;
                        case Cell.CELL_TYPE_FORMULA:
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            writeMethod.invoke(o, cell.getStringCellValue());
                            break;
                        case Cell.CELL_TYPE_ERROR:
                        default:
                            writeMethod.invoke(o, "");
                            break;
                    }
                }
                dataList.add(o);
            }
        } catch (ClassNotFoundException | IntrospectionException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return dataList;
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

    private <T> void invokeField(T o, Method writeMethod, Class<?> type, Cell cell) {
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

    public <T> List<T> readExcel(String parserId, int sheetIndex, boolean readHead) {
        Parse parse = ParsesUtils.nanalysisByDocument(fileName).get(parserId);
        return this.getDataByColumn(sheetIndex, readHead, parse);
    }

    private Map<String, Map<String, Map<String, Integer>>> readExcelHeadMap(int sheetIndex, int cellStartNum) {
        Sheet sheet = wb.getSheetAt(sheetIndex);
        List<CellRangeAddress> mergedRegions = this.getMergedRegions(sheet);

        Row feeRow = sheet.getRow(0);
        Map<String, List<Integer>> feeParamIdListMap = this.getMergedCellIdListMap(feeRow, cellStartNum, feeRow.getLastCellNum(), mergedRegions);

        Row paramRow = sheet.getRow(1);
        Map<String, Map<String, List<Integer>>> feeParamLevelIdListMap = new HashMap<>();
        feeParamIdListMap.forEach((feeName, paramIds) -> {
            Map<String, List<Integer>> paramLevelIdMap = this.getMergedCellIdListMap(paramRow, paramIds.get(0), paramIds.get(paramIds.size() - 1) + 1, mergedRegions);
            feeParamLevelIdListMap.put(feeName, paramLevelIdMap);
        });

        Row levelRow = sheet.getRow(2);
        Map<String, Map<String, Map<String, Integer>>> feeParamLevelRateIdListMap = new HashMap<>();
        feeParamLevelIdListMap.forEach((feeName, paramLevelIdMap) -> {
            Map<String, Map<String, Integer>> paramLevelRateIdListMap = new HashMap<>();
            paramLevelIdMap.forEach((param, levelIds) -> {
                Map<String, Integer> levelRateIdListMap = new HashMap<>();
                for (Integer levelId : levelIds) {
                    String[] levels = this.getMergedCellValue(sheet, levelRow, levelId).trim().split("/");
                    for (String level : levels) {
                        levelRateIdListMap.put(level, levelId);
                    }
                    paramLevelRateIdListMap.put(param, levelRateIdListMap);
                    feeParamLevelRateIdListMap.put(feeName, paramLevelRateIdListMap);
                }
            });
        });
        return feeParamLevelRateIdListMap;
    }

    public List<ProductPricingBean> getProductPricingBeans(int sheetIndex, int rowStartNum) {
        Sheet sheet = wb.getSheetAt(sheetIndex);
        Map<String, Map<String, Map<String, Integer>>> feeParamLevelRateIdListMap = this.readExcelHeadMap(sheetIndex, 1);
        Set<String> allLevelSet = feeParamLevelRateIdListMap.values().stream()
                .flatMap(outerMap -> outerMap.values().stream())
                .flatMap(innerMap -> innerMap.keySet().stream())
                .collect(Collectors.toSet());
        List<String> allLevelList = allLevelSet.stream().sorted().collect(Collectors.toList());
        List<ProductPricingBean> productPricingBeans = new ArrayList<>();
        for (int rowNum = rowStartNum; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (isRowEmpty(row)) continue;
            Integer totalStage = Integer.valueOf(getCellStringValue(row.getCell(0)));
            for (String level : allLevelList) {
                String[] consumerLabels = level.split("-");
                ProductPricingBean productPricingBean = new ProductPricingBean();
                productPricingBean.setTotalStage(totalStage);
                productPricingBean.setRankLevel(consumerLabels.length > 1 ? consumerLabels[0] : level);
                productPricingBean.setConsumerLabel(consumerLabels.length > 1 ? consumerLabels[1] : "");
                Integer yearRateColumn = feeParamLevelRateIdListMap.get("资方").get(FormulaParamMapping.yearRate.getParamName()).get(level);
                productPricingBean.setYearRate(BigDecimal.valueOf(Double.parseDouble(getMergedCellValue(sheet, row, yearRateColumn))));
                Integer aprMonthRateColumn = feeParamLevelRateIdListMap.get("月综合").get(FormulaParamMapping.complexMonthRate.getParamName()).get(level);
                productPricingBean.setAprMonthRate(BigDecimal.valueOf(Double.parseDouble(getMergedCellValue(sheet, row, aprMonthRateColumn))));
                productPricingBeans.add(productPricingBean);
            }
        }
        return productPricingBeans;
    }

    public List<ExlFeeParamBean> getExlFeeParamBeans(int sheetIndex, int rowStartNum) {
        Sheet sheet = wb.getSheetAt(sheetIndex);
        Map<String, Map<String, Map<String, Integer>>> feeParamLevelRateIdListMap = this.readExcelHeadMap(sheetIndex, 1);
        List<CellRangeAddress> mergedRegions = this.getMergedRegions(sheet);
        Map<String, Set<String>> feeLevelsMap = feeParamLevelRateIdListMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().values().stream()
                                .flatMap(innerMap -> innerMap.keySet().stream())
                                .collect(Collectors.toSet())
                ));
        List<ExlFeeParamBean> exlFeeParamBeans = new ArrayList<>();
        feeParamLevelRateIdListMap.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("月综合"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .forEach((feeName, paramMap) -> {
                    for (int rowNum = rowStartNum; rowNum <= sheet.getLastRowNum(); rowNum++) {
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
                            exlFeeParamBeans.add(exlFeeParamBean);
                        }
                    }
                });
        return exlFeeParamBeans;
    }

    private List<CellRangeAddress> getMergedRegions(Sheet sheet) {
        List<CellRangeAddress> mergedRegions = new ArrayList<>();
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            mergedRegions.add(sheet.getMergedRegion(i));
        }
        return mergedRegions;
    }

    private Map<String, List<Integer>> getMergedCellIdListMap(Row row, int cellStartNum, int cellEndNum, List<CellRangeAddress> mergedRegions) {
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

    private boolean isMergedCell(List<CellRangeAddress> mergedRegions, int row, int col) {
        for (CellRangeAddress region : mergedRegions) {
            if (region.isInRange(row, col) && (region.getFirstRow() != row || region.getFirstColumn() != col)) {
                return true;
            }
        }
        return false;
    }

    private String getMergedCellValue(Sheet sheet, Row row, int column) {
        List<CellRangeAddress> mergedRegions = this.getMergedRegions(sheet);
        for (CellRangeAddress region : mergedRegions) {
            if (region.isInRange(row.getRowNum(), column)) {
                Row firstRow = sheet.getRow(region.getFirstRow());
                Cell firstCell = firstRow.getCell(region.getFirstColumn());
                return getCellStringValue(firstCell);
            }
        }
        return getCellStringValue(row.getCell(column));
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                if (cell.getNumericCellValue() == 0) {
                    return "0.00";
                }
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(10); // 最大小数位数
                return nf.format(cell.getNumericCellValue()).replaceAll("0*$", "").replaceAll("\\.$", "");
            default:
                return "";
        }
    }
}
