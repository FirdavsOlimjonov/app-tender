package uz.mc.apptender.utils;

import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Component;
import uz.mc.apptender.modules.SvodResurs;
import uz.mc.apptender.repositories.SvodResourceRepository;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExcelGenerate {

    private final SvodResourceRepository svodResourceRepository;


    public void generateExcel(long lotId, HttpServletResponse httpServletResponse) {
        List<SvodResurs> svodResursList = svodResourceRepository.findAllByStroy_LotId(lotId);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Xisobot");

        CellStyle style = getCellStyle(workbook);
        CellStyle styleBasic = getStyleBasic(workbook);

        //Header qismdagi static qismlarni yozish
        int dataIndex = setHeaderByDefault(sheet, style);


        for (SvodResurs resurs : svodResursList) {
//            CellRangeAddress cellAddressesObject = new CellRangeAddress(dataIndex, dataIndex, 0, 7);
//            sheet.addMergedRegion(cellAddressesObject);

//            HSSFRow rowObject = sheet.createRow(dataIndex++);
//            rowObject.createCell(0).setCellValue(resurs.getObName());
//            for (Cell cell : rowObject)
//                cell.setCellStyle(style);
//
//            sheet.createRow(dataIndex++);
//
//            CellRangeAddress cellAddressesSmeta = new CellRangeAddress(dataIndex, dataIndex, 0, 7);
//            sheet.addMergedRegion(cellAddressesSmeta);

//            HSSFRow rowSmeta = sheet.createRow(dataIndex++);
//            rowSmeta.createCell(0).setCellValue(smeta.getSmName());
//            for (Cell cell : rowSmeta)
//                cell.setCellStyle(style);

            HSSFRow row = sheet.createRow(dataIndex++);

            row.createCell(0).setCellValue(resurs.getNum());
            row.createCell(1).setCellValue(resurs.getName());
            row.createCell(2).setCellValue(resurs.getKodiName());
            row.createCell(3).setCellValue(resurs.getKol());
            row.createCell(4).setCellValue(resurs.getPrice().doubleValue());
            row.createCell(5).setCellValue(resurs.getSumma().doubleValue());

            for (Cell cell : row)
                cell.setCellStyle(styleBasic);

        }

        sheet.autoSizeColumn(0);
//        sheet.autoSizeColumn(1);
        sheet.setDefaultColumnWidth(130);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);

        try {
            ServletOutputStream outputStream = httpServletResponse.getOutputStream();
            FileOutputStream fileOut = new FileOutputStream("test.xls");
            workbook.write(fileOut);
            workbook.write(outputStream);
            workbook.close();
            fileOut.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static int setHeaderByDefault(HSSFSheet sheet, CellStyle style) {
        CellRangeAddress first = new CellRangeAddress(0, 1, 0, 0);
        CellRangeAddress second = new CellRangeAddress(0, 1, 1, 1);
        CellRangeAddress third = new CellRangeAddress(0, 1, 2, 2);
        CellRangeAddress fourth= new CellRangeAddress(0, 1, 3, 3);
        CellRangeAddress five= new CellRangeAddress(0, 0, 4, 5);

        sheet.addMergedRegion(first);
        sheet.addMergedRegion(second);
        sheet.addMergedRegion(third);
        sheet.addMergedRegion(fourth);
        sheet.addMergedRegion(five);

        HSSFRow row = sheet.createRow(0);

        row.createCell(0).setCellValue("N%");
        row.createCell(1).setCellValue("НАИМЕНОВАНИЕ РЕСУРСА");
        row.createCell(2).setCellValue("ЕД.ИЗМ");
        row.createCell(3).setCellValue("КОЛ-ВО");
        row.createCell(4).setCellValue("СТОИМОСТЬ В ТЕКУЩИХ ЦЕНАХ");

        for (Cell cell : row)
            cell.setCellStyle(style);

        HSSFRow row1 = sheet.createRow(1);
        for (Cell cell : row1)
            cell.setCellStyle(style);

        HSSFRow sheetRow = sheet.createRow(1);
        sheetRow.createCell(4).setCellValue("ЕДИНИЦЫ");
        sheetRow.createCell(5).setCellValue("НА ВЕСЬ ОБЪЕМ");

        for (Cell cell : sheetRow)
            cell.setCellStyle(style);

        HSSFRow rowIndexOne = sheet.createRow(2);
        rowIndexOne.createCell(0).setCellValue(1);
        rowIndexOne.createCell(1).setCellValue(2);
        rowIndexOne.createCell(2).setCellValue(3);
        rowIndexOne.createCell(3).setCellValue(4);
        rowIndexOne.createCell(4).setCellValue(5);
        rowIndexOne.createCell(5).setCellValue(6);

        for (Cell cell : rowIndexOne)
            cell.setCellStyle(style);

        return 4;
    }

    private static CellStyle getStyleBasic(HSSFWorkbook workbook) {
        CellStyle styleBasic = workbook.createCellStyle();
        styleBasic.setAlignment(HorizontalAlignment.LEFT);
        styleBasic.setVerticalAlignment(VerticalAlignment.CENTER);

        // set border styleBasic of the cell
        styleBasic.setBorderTop(BorderStyle.THIN);
        styleBasic.setBorderRight(BorderStyle.THIN);
        styleBasic.setBorderBottom(BorderStyle.THIN);
        styleBasic.setBorderLeft(BorderStyle.THIN);
        return styleBasic;
    }

    private static CellStyle getCellStyle(HSSFWorkbook workbook) {
        CellStyle style = getStyleBasic(workbook);

        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

}
