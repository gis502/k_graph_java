package com.ruoyi.system.domain.handler;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelConverter extends AbstractMergeStrategy {

    private int mergeRowIndex;    // 要合并的行号
    private int mergeStartColumnIndex; // 开始合并的列
    private int mergeEndColumnIndex;   // 结束合并的列

    public ExcelConverter(int mergeRowIndex, int mergeStartColumnIndex, int mergeEndColumnIndex) {
        this.mergeRowIndex = mergeRowIndex;
        this.mergeStartColumnIndex = mergeStartColumnIndex;
        this.mergeEndColumnIndex = mergeEndColumnIndex;
    }

    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();

        if (rowIndex == mergeRowIndex && columnIndex == mergeStartColumnIndex) {
            // 合并从 mergeStartColumnIndex 到 mergeEndColumnIndex 的单元格
            sheet.addMergedRegion(new CellRangeAddress(mergeRowIndex, mergeRowIndex, mergeStartColumnIndex, mergeEndColumnIndex));

            // 设置合并单元格的样式
            Workbook workbook = sheet.getWorkbook();
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER); // 水平居中
            cellStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER); // 垂直居中

            // 将样式应用到合并单元格
            Cell mergedCell = sheet.getRow(mergeRowIndex).getCell(mergeStartColumnIndex);
            mergedCell.setCellStyle(cellStyle);
        }
    }

    // Excel日期转换函数
    public static LocalDateTime convertExcelDate(double excelDateValue) {
        // Excel 的日期基准（1900-01-01）
        LocalDateTime baseDate = LocalDateTime.of(1900, 1, 1, 0, 0, 0);

        // 如果 Excel 日期值大于 60，调整闰年错误
        if (excelDateValue > 60) {
            excelDateValue -= 2; // 减去一天
        }

        // Excel 日期值是从1900年1月1日开始的天数，因此我们需要加上天数
        long daysSinceBase = (long) excelDateValue; // 整数部分为天数
        double fractionalDay = excelDateValue - daysSinceBase; // 小数部分为时间

        // 计算时间部分（86400 秒 = 24 小时）
        long seconds = (long) (fractionalDay * 86400);

        // 根据基准日期加上天数和秒数
        return baseDate.plusDays(daysSinceBase).plusSeconds(seconds); // 返回转换后的 LocalDateTime
    }

    // 将科学计数法转换为字符串的函数
    public static List<String> convertContactPhones(List<String> properties) {
        List<String> updatedProperties = new ArrayList<>();
        Pattern pattern = Pattern.compile("contactPhone=(\\d+\\.\\d+E\\d+)");

        for (String property : properties) {
            Matcher matcher = pattern.matcher(property);
            StringBuffer updatedProperty = new StringBuffer();

            while (matcher.find()) {
                // 将科学计数法转换为标准字符串格式
                String scientificNotation = matcher.group(1);
                String standardPhone = new BigDecimal(scientificNotation).toBigInteger().toString();
                // 替换 contactPhone 的值
                matcher.appendReplacement(updatedProperty, "contactPhone=" + standardPhone);
            }
            matcher.appendTail(updatedProperty); // 追加未匹配的部分
            updatedProperties.add(updatedProperty.toString());
        }

        return updatedProperties;
    }

}
