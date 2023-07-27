package uz.mc.apptender.utils;

import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uz.mc.apptender.modules.SvodResurs;
import uz.mc.apptender.repositories.SvodResourceRepository;
import uz.mc.apptender.repositories.TenderCustomerRepository;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExcelGenerate {

    private final SvodResourceRepository svodResourceRepository;
    private final TenderCustomerRepository tenderCustomerRepository;

    final static DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    public ResponseEntity<Resource> generateExcel(long lotId) {

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Xisobot");

        CellStyle style = getCellStyle(workbook);
        CellStyle styleBasic = getStyleBasic(workbook);


        //Header qismdagi static qismlarni yozish
        int dataIndex = setHeaderByDefault(sheet, style, styleBasic);

        for (int tip = 1; tip <= 5; tip++) {
            //tip va stroy boyicha svod_resorslarni olib kelish
            List<SvodResurs> svodResursList = svodResourceRepository.findAllByStroy_LotId(lotId, tip);

            /*
            1-Затраты труда
            2-Эксплуатация машин
            3-Строительные материалы
            4-Оборудование
            5-Перевозка
             */
            String tipName = tip == 1 ? "Затраты труда" : tip == 2 ? "Эксплуатация машин" :
                    tip == 3 ? "Строительные материалы" : tip == 4 ? "Оборудование" : "Перевозка";

            if (!svodResursList.isEmpty())
                dataIndex = setTipValue(tipName, dataIndex, sheet, style);

            for (SvodResurs resurs : svodResursList) {
                HSSFRow row = sheet.createRow(dataIndex++);

                row.createCell(0).setCellValue(resurs.getNum());
                row.createCell(1).setCellValue(resurs.getKodr());
                row.createCell(2).setCellValue(resurs.getName());
                row.createCell(3).setCellValue(resurs.getKodiName());

                //hamma kolichistvolarni yigib keladi
                double sumAllKol = calculateKol(resurs, lotId);
                double price = resurs.getPrice().doubleValue();
                double sum = price * sumAllKol;
                row.createCell(4).setCellValue(sumAllKol);
                row.createCell(5).setCellValue(price);
                row.createCell(6).setCellValue(sum);

                for (Cell cell : row)
                    cell.setCellStyle(styleBasic);

            }
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        ;
        sheet.setDefaultColumnWidth(130);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            HttpHeaders headers = new HttpHeaders();

            LocalDateTime ldt = LocalDateTime.now();
            String formattedString = ldt.format(CUSTOM_FORMATTER).concat(".xls");

            workbook.write(byteArrayOutputStream);
            workbook.close();

            byte[] bytes = byteArrayOutputStream.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(bytes);

            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData(formattedString, formattedString);

            FileOutputStream fileOut = new FileOutputStream(formattedString);
            workbook.write(fileOut);
            fileOut.close();

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(bytes.length)
                    .body(resource);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }

    }

    private double calculateKol(SvodResurs resurs, long lotId) {
        if (!resurs.getKodr().isBlank())
            return tenderCustomerRepository.sumRashod(lotId, resurs.getKodr());

        return resurs.getKol();
    }

    private static int setTipValue(String tipName, int dataRow, HSSFSheet sheet, CellStyle style) {
        CellRangeAddress first = new CellRangeAddress(dataRow, dataRow, 0, 6);
        sheet.addMergedRegion(first);

        HSSFRow row = sheet.createRow(dataRow++);
        row.createCell(0);
        row.createCell(1);
        row.createCell(2);
        row.createCell(3);
        row.createCell(4);
        row.createCell(5);
        row.createCell(6);
        for (Cell cell : row)
            cell.setCellStyle(style);

        CellRangeAddress second = new CellRangeAddress(dataRow, dataRow, 0, 6);
        sheet.addMergedRegion(second);

        HSSFRow row2 = sheet.createRow(dataRow++);
        row2.createCell(0).setCellValue(tipName);

        row2.createCell(1);
        row2.createCell(2);
        row2.createCell(3);
        row2.createCell(4);
        row2.createCell(5);
        row2.createCell(6);
        for (Cell cell : row2)
            cell.setCellStyle(style);

        return dataRow;
    }

    private static int setHeaderByDefault(HSSFSheet sheet, CellStyle style, CellStyle styleBasic) {
        CellRangeAddress first = new CellRangeAddress(0, 1, 0, 0);
        CellRangeAddress second = new CellRangeAddress(0, 1, 1, 1);
        CellRangeAddress third = new CellRangeAddress(0, 1, 2, 2);
        CellRangeAddress fourth = new CellRangeAddress(0, 1, 3, 3);
        CellRangeAddress five = new CellRangeAddress(0, 1, 4, 4);
        CellRangeAddress six = new CellRangeAddress(0, 0, 5, 6);

        sheet.addMergedRegion(first);
        sheet.addMergedRegion(second);
        sheet.addMergedRegion(third);
        sheet.addMergedRegion(fourth);
        sheet.addMergedRegion(five);
        sheet.addMergedRegion(six);

        HSSFRow row = sheet.createRow(0);

        row.createCell(0).setCellValue("N%");
        row.createCell(1).setCellValue("РЕСУРС");
        row.createCell(2).setCellValue("НАИМЕНОВАНИЕ РЕСУРСА");
        row.createCell(3).setCellValue("ЕД.ИЗМ");
        row.createCell(4).setCellValue("КОЛ-ВО");
        row.createCell(5).setCellValue("СТОИМОСТЬ В ТЕКУЩИХ ЦЕНАХ");

        for (Cell cell : row)
            cell.setCellStyle(style);

        HSSFRow row1 = sheet.createRow(1);

        for (Cell cell : row1)
            cell.setCellStyle(style);

        row1.createCell(5).setCellValue("ЕДИНИЦЫ");
        row1.createCell(6).setCellValue("НА ВЕСЬ ОБЪЕМ");

        for (Cell cell : row1)
            cell.setCellStyle(style);

        HSSFRow rowIndexOne = sheet.createRow(2);
        rowIndexOne.createCell(0).setCellValue(1);
        rowIndexOne.createCell(1).setCellValue(2);
        rowIndexOne.createCell(2).setCellValue(3);
        rowIndexOne.createCell(3).setCellValue(4);
        rowIndexOne.createCell(4).setCellValue(5);
        rowIndexOne.createCell(5).setCellValue(6);
        rowIndexOne.createCell(6).setCellValue(7);

        for (Cell cell : rowIndexOne)
            cell.setCellStyle(style);

        return 3;
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
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

}
