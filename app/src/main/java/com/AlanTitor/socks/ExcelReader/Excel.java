    package com.AlanTitor.socks.ExcelReader;

    import com.AlanTitor.socks.DTO.Socks;
    import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
    import org.apache.poi.ss.usermodel.Row;
    import org.apache.poi.ss.usermodel.Sheet;
    import org.apache.poi.ss.usermodel.Workbook;
    import org.apache.poi.xssf.usermodel.XSSFWorkbook;

    import java.io.IOException;
    import java.io.InputStream;
    import java.util.ArrayList;
    import java.util.List;

    public class Excel {

        public static List<Socks> readSocksFromExcel(InputStream file) throws IOException, InvalidFormatException {
            List<Socks> listOfSocks = new ArrayList<>();
            try (Workbook workbook = new XSSFWorkbook(file)) {
                Sheet sheet = workbook.getSheetAt(0);

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;

                    String color = row.getCell(0).getStringCellValue();
                    double percentCotton = row.getCell(1).getNumericCellValue();
                    int amount = (int) row.getCell(2).getNumericCellValue();

                    // Проверка корректности данных
                    if (color.isEmpty()) {
                        throw new IllegalArgumentException("Invalid data in row " + row.getRowNum() + ": color is empty.");
                    }
                    if (percentCotton < 0 || percentCotton > 100) {
                        throw new IllegalArgumentException("Invalid data in row " + row.getRowNum() + ": percentCotton must be between 0 and 100.");
                    }
                    if (amount < 0) {
                        throw new IllegalArgumentException("Invalid data in row " + row.getRowNum() + ": amount must be non-negative.");
                    }


                    Socks socks = new Socks(null, color, percentCotton, amount);
                    listOfSocks.add(socks);
                }

            } catch (IOException e) {
                throw new IOException(e.getMessage());
            }
            return listOfSocks;
        }
    }
