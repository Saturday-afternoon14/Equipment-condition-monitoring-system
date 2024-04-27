package org.example;
import kotlin.Result;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RandomXlsReader {
    private static final String FILE_PATH = "E:\\Download\\test1.xls";
    private static final int ROWS_PER_READ = 2048;
    private static final double RANDOM_RANGE = 0.00005;
    private static final Random RANDOM = new Random();
    private List<Double>Result=new ArrayList<Double>();

    /*public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(RandomXmlReader::readRandomColumnAndAddNoise, 0, 5, TimeUnit.SECONDS);
    }*/

    public void readRandomColumnAndAddNoise() {
        try (FileInputStream fis = new FileInputStream(FILE_PATH)) {
            HSSFWorkbook workbook = new HSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            // 随机选择一列
            int randomColumn = (int) (Math.random() * 10);

            // 读取连续的2048行数据
            for (int rowNum = 0; rowNum < ROWS_PER_READ; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row != null) {
                    Cell cell = row.getCell(randomColumn);
                    if (cell != null) {
                        // 假设数据是double类型，如果不是，需要相应转换
                        double value = cell.getNumericCellValue();
                        double noise = RANDOM.nextDouble() * 2 * RANDOM_RANGE - RANDOM_RANGE; // 可正可负的随机数
                        double newValue = value+noise ;
                        Result.add(newValue);
                        //System.out.println("Row " + (rowNum + 1) + ", Column " + (randomColumn + 1) + ": " + newValue);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Double>getResult(){
        return Result;
    }

}
