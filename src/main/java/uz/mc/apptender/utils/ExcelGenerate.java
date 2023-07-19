package uz.mc.apptender.utils;

import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Component;
import uz.mc.apptender.modules.Object;
import uz.mc.apptender.modules.Smeta;
import uz.mc.apptender.payload.projections.TenderProjection;
import uz.mc.apptender.repositories.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExcelGenerate {

    private final StroyRepository stroyRepository;
    private final ObjectRepository objectRepository;
    private final SmetaRepository smetaRepository;
    private final SmetaItogRepository smetaItogRepository;
    private final TenderCustomerRepository tenderCustomerRepository;


    public void generateExcel(long lotId, HttpServletResponse httpServletResponse) {
        List<Object> objectList = findAllObject(lotId);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Xisobot");

        CellStyle style = getCellStyle(workbook);
        CellStyle styleBasic = getStyleBasic(workbook);

        //Header qismdagi static qismlarni yozish
        int dataIndex = setHeaderByDefault(sheet, style);

        for (Object object : objectList) {
            CellRangeAddress cellAddressesObject = new CellRangeAddress(dataIndex, dataIndex, 0, 7);
            sheet.addMergedRegion(cellAddressesObject);

            HSSFRow rowObject = sheet.createRow(dataIndex++);
            rowObject.createCell(0).setCellValue(object.getObName());
            for (Cell cell : rowObject)
                cell.setCellStyle(style);

            sheet.createRow(dataIndex++);

            for (Smeta smeta : object.getSmArray()) {
                CellRangeAddress cellAddressesSmeta = new CellRangeAddress(dataIndex, dataIndex, 0, 7);
                sheet.addMergedRegion(cellAddressesSmeta);

                HSSFRow rowSmeta = sheet.createRow(dataIndex++);
                rowSmeta.createCell(0).setCellValue(smeta.getSmName());
                for (Cell cell : rowSmeta)
                    cell.setCellStyle(style);

                sheet.createRow(dataIndex++);

                int num = 1;
                for (TenderProjection tenderCustomer : tenderCustomerRepository.findAllBySmeta_IdWithQuery(smeta.getId())) {
                    HSSFRow rowTender = sheet.createRow(dataIndex++);

                    rowTender.createCell(0).setCellValue(num++);
                    rowTender.createCell(1).setCellValue("  ");
                    rowTender.createCell(2).setCellValue(tenderCustomer.getKod_snk());
                    rowTender.createCell(3).setCellValue(tenderCustomer.getName());
                    rowTender.createCell(4).setCellValue(tenderCustomer.getEd_ism());
                    rowTender.createCell(5).setCellValue(tenderCustomer.getNorma());
                    rowTender.createCell(6).setCellValue(tenderCustomer.getPrice().doubleValue());
                    rowTender.createCell(7).setCellValue(tenderCustomer.getSumma().doubleValue());

                    for (Cell cell : rowTender)
                        cell.setCellStyle(styleBasic);

                }
                sheet.createRow(dataIndex++);

            }
        }

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
        HSSFRow row = sheet.createRow(0);

        row.createCell(0).setCellValue("N%");
        row.createCell(1).setCellValue("РЕСУРС");
        row.createCell(2).setCellValue("ОБОСНОВАНИЕ");
        row.createCell(3).setCellValue("НАИМЕНОВАНИЕ РЕСУРСА");
        row.createCell(4).setCellValue("ЕД.ИЗМ");
        row.createCell(5).setCellValue("КОЛ-ВО");
        row.createCell(6).setCellValue("ЦЕНА");
        row.createCell(7).setCellValue("СУММА");

        for (Cell cell : row)
            cell.setCellStyle(style);

        HSSFRow rowIndexOne = sheet.createRow(1);
        rowIndexOne.createCell(0).setCellValue(1);
        rowIndexOne.createCell(1).setCellValue(2);
        rowIndexOne.createCell(2).setCellValue(3);
        rowIndexOne.createCell(3).setCellValue(4);
        rowIndexOne.createCell(4).setCellValue(5);
        rowIndexOne.createCell(5).setCellValue(6);
        rowIndexOne.createCell(6).setCellValue(7);
        rowIndexOne.createCell(7).setCellValue(8);

        for (Cell cell : rowIndexOne)
            cell.setCellStyle(style);

        return 2;
    }

    private List<Object> findAllObject(long lotId) {
        return objectRepository.findAllByStroy_LotId(lotId);
    }


    private static CellStyle getStyleBasic(HSSFWorkbook workbook) {
        CellStyle styleBasic = workbook.createCellStyle();
        styleBasic.setAlignment(HorizontalAlignment.CENTER);
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
