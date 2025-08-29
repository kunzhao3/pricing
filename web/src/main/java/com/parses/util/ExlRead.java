package com.parses.util;

import com.parses.util.xmlparser.Column;
import com.parses.util.xmlparser.Parse;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ExlRead {
    public static final String fileName = "/excelRead.xml";

    private final String path;

    public ExlRead(String path) {
        this.path = path;
    }

    public <T> List<T> getDataByColumn(int sheetIndex, boolean readHead, Parse parse) {
        List<T> dataList = new ArrayList<>();
        try {
            Class<?> clazz = Class.forName(parse.getClassname());
            InputStream inp = Files.newInputStream(Paths.get(path));
            Workbook wb = WorkbookFactory.create(inp);
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
            inp.close();
        } catch (ClassNotFoundException | IOException | InvalidFormatException | IntrospectionException |
                 InstantiationException | IllegalAccessException | InvocationTargetException e) {
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

}
