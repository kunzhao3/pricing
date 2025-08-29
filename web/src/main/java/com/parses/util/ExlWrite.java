package com.parses.util;

import com.parses.server.bean.PricingFeeBean;
import com.parses.server.util.UUIDUtils;
import com.parses.util.xmlparser.Column;
import com.parses.util.xmlparser.Parse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;


public class ExlWrite {
    public static final String fileName = "/excelWrite.xml";
    public static final short START_ROW = 0;

    //样式表
    private static Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        DataFormat fmt = wb.createDataFormat();
        CellStyle style1 = wb.createCellStyle();
        Font headerFont1 = wb.createFont();
        // 粗体
        headerFont1.setBoldweight(Font.BOLDWEIGHT_BOLD);
        // 设置单元格背景色
        style1.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        style1.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style1.setFont(headerFont1);
        styles.put("header", style1);

        CellStyle style2 = wb.createCellStyle();
        style2.setAlignment(CellStyle.ALIGN_CENTER);
        styles.put("body", style2);

        CellStyle style3 = wb.createCellStyle();
        style3.setAlignment(CellStyle.ALIGN_RIGHT);
        style3.setDataFormat(fmt.getFormat("¥#,##0.00"));
        styles.put("currency", style3);

        CellStyle style4 = wb.createCellStyle();
        style4.setAlignment(CellStyle.ALIGN_CENTER);
        style4.setDataFormat(fmt.getFormat("yyyy年m月d日"));
        styles.put("date", style4);

        CellStyle style5 = wb.createCellStyle();
        style5.setAlignment(CellStyle.ALIGN_RIGHT);
        style5.setDataFormat(fmt.getFormat("0.00%"));
        styles.put("percent", style5);

        CellStyle style6 = wb.createCellStyle();
        style6.setAlignment(CellStyle.ALIGN_RIGHT);
        style6.setDataFormat(fmt.getFormat("0.00"));
        styles.put("snum", style6);//两位小数
        return styles;
    }

    public static <T, E> Workbook exportExcel(List<T> list, Parse nxp, E E, Parse nxpTotal) {
        if (null == nxp) {
            throw new NullPointerException("excel解析器不存在");
        }
        // create a new workbook
        Workbook wb = new XSSFWorkbook();
        try {
            Class<?> clazz = Class.forName(nxp.getClassname());

            // create a new sheet
            Sheet s = wb.createSheet();

            Map<String, CellStyle> styles = createStyles(wb);

            //生成表头
            Row hr = s.createRow(START_ROW);

            List<Column> cols = nxp.getColumns().getColumn();

            if (null == cols) {
                throw new NullPointerException("excel没有配置column");
            }

            int cno = cols.size();

            if (cno == 0) {
                throw new NullPointerException("excel没有配置column");
            }

            //设置表头样式
            CellStyle hcs = styles.get("header");
            //设置表头
            for (Column col : cols) {
                Cell cell = hr.createCell(col.getCidx());
                cell.setCellStyle(hcs);
                cell.setCellValue(col.getCname());
            }

            if (null != list) {
                int rNo = list.size();
                //生成数据体
                if (rNo > 0) {
                    for (int i = 0; i < rNo; i++) {
                        Row hbr = s.createRow(i + 1);
                        T td = list.get(i);
                        for (Column c : cols) {
                            createCell(clazz, td, styles, c, hbr);

                        }
                    }

                    if (nxpTotal != null) {
                        Class<?> clazzTotal = Class.forName(nxpTotal.getClassname());
                        List<Column> colsTotal = nxpTotal.getColumns().getColumn();
                        if (null == colsTotal) {
                            throw new NullPointerException("excel没有配置汇总column");
                        }
                        int cnoTotal = colsTotal.size();

                        if (cnoTotal == 0) {
                            throw new NullPointerException("excel没有配置汇总column");
                        }
                        Row hbr = s.createRow(rNo + 1);
                        for (Column c : colsTotal) {
                            createCell(clazzTotal, E, styles, c, hbr);

                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return wb;
    }

    public static <T> void createCell(Class<?> clazz, T td, Map<String, CellStyle> styles, Column c, Row hbr) {
        try {
            String pn = c.getPname();
            PropertyDescriptor pd = new PropertyDescriptor(pn, clazz);
            Method getMethod = pd.getReadMethod();//获得get方法
            Object o = getMethod.invoke(td);//执行get方法返回一个Object
            if (null == o) {
                return;
            }
            String pType = c.getType();
            Class<?> pc = pd.getPropertyType();//获取类型
            if (StringUtils.isBlank(pType)) {//如果没有设置type，就直接返回对象的中的类型
                pType = pc.getSimpleName();
            }

            if ("obj".equals(pType)) {
                if (StringUtils.isNotBlank(c.getHandler())) {
                    String[] strs = c.getHandler().split("#");
                    if (strs[0].startsWith("insastead")) {
                        PropertyDescriptor cpd = new PropertyDescriptor(strs[1], pc);
                        Method getcMethod = cpd.getReadMethod();//获得get方法
                        o = getcMethod.invoke(o);
                        pType = "String";
                    } else if (strs[0].startsWith("int")) {
                        PropertyDescriptor cpd = new PropertyDescriptor(strs[1], pc);
                        Method getcMethod = cpd.getReadMethod();//获得get方法
                        o = getcMethod.invoke(o);
                        pType = "Integer";
                    }

                }
            }
            if ("String".equals(pType)) {
                Cell cell = hbr.createCell(c.getCidx());
                cell.setCellValue((String) o);
                cell.setCellType(Cell.CELL_TYPE_STRING);
            } else if ("Integer".equals(pType) || "int".equals(pType)) {
                Cell cell = hbr.createCell(c.getCidx());
                cell.setCellValue((Integer) o);
                cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            } else if ("Float".equals(pType) || "float".equals(pType)) {
                Cell cell = hbr.createCell(c.getCidx());
                cell.setCellValue((Float) o);
                cell.setCellStyle(styles.get("snum"));
            } else if ("Double".equals(pType) || "bouble".equals(pType)) {
                Cell cell = hbr.createCell(c.getCidx());
                cell.setCellValue((Double) o);
                cell.setCellStyle(styles.get("snum"));
            } else if ("Byte".equals(pType) || "byte".equals(pType)) {
                Cell cell = hbr.createCell(c.getCidx());
                cell.setCellValue((Byte) o);
                cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            } else if ("Long".equals(pType) || "long".equals(pType)) {
                Cell cell = hbr.createCell(c.getCidx());
                cell.setCellValue((Long) o);
                cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            } else if ("Boolean".equals(pType) || "boolean".equals(pType)) {
                Cell cell = hbr.createCell(c.getCidx());
                cell.setCellValue((Long) o);
                cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
            } else if ("Date".equals(pType)) {
                Cell cell = hbr.createCell(c.getCidx());
                cell.setCellValue((Date) o);
                cell.setCellStyle(styles.get("date"));
            } else if ("currency".equals(pType)) {
                Cell cell = hbr.createCell(c.getCidx());
                cell.setCellValue(((BigDecimal) o).doubleValue());
                cell.setCellStyle(styles.get("snum"));
            } else if ("BigDecimal".equals(pType)) {
                Cell cell = hbr.createCell(c.getCidx());
                cell.setCellValue(((BigDecimal) o).doubleValue());
                cell.setCellStyle(styles.get("snum"));
            } else if ("percent".equals(pType)) {
                Cell cell = hbr.createCell(c.getCidx());
                cell.setCellValue((Date) o);
                cell.setCellStyle(styles.get("percent"));
            }
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Workbook exportExcel(List<T> list){
        if (null == list || list.isEmpty()) {
            throw new NullPointerException("数据为空");
        }
        T t = list.get(0);
        Class<?> clazz = t.getClass();
        String csm = clazz.getSimpleName();
        //获取解析器
        Parse nxp = ParsesUtils.nanalysisByDocument(fileName).get(csm);
        return exportExcel(list, nxp, null, null);
    }

    /**
     * 导出excel汇总
     *
     */
    public static <T, E> Workbook exportExcelTotal(List<T> list, E E) {
        if (list.isEmpty()) {
            throw new NullPointerException("数据为空");
        }
        T t = list.get(0);
        Class<?> clazz = t.getClass();
        String csm = clazz.getSimpleName();
        //获取解析器
        Parse nxp = ParsesUtils.nanalysisByDocument(fileName).get(csm);

        Parse nxpTotal = ParsesUtils.nanalysisByDocument(fileName).get(E.getClass().getSimpleName());
        return exportExcel(list, nxp, E, nxpTotal);
    }

    /**
     * @param <T>      传入类
     * @param list     结果集
     * @param parserId 解析器ID
     */
    public static <T> Workbook exportExcel(List<T> list, String parserId) {
        if (StringUtils.isBlank(parserId)) {
            return exportExcel(list);
        } else {
            Parse nxp = ParsesUtils.nanalysisByDocument(fileName).get(parserId);
            return exportExcel(list, nxp, null, null);
        }
    }

    public static void main(String[] args) throws IOException, IllegalArgumentException {
        List<PricingFeeBean> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            PricingFeeBean sc = new PricingFeeBean();
            sc.setPricingNo(UUIDUtils.genSimpleUUID());
            list.add(sc);
        }

        Workbook wb = exportExcel(list);
        FileOutputStream fileOut = new FileOutputStream("/Users/zhaokun/Downloads/费项/00001.xlsx");
        wb.write(fileOut);
        fileOut.close();

    }

}
