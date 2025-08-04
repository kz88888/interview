package com.qsystem.qsexcel;

import com.qsystem.clientlib.QPortfolioBase;
import com.qsystem.clientlib.JsonModel.RiskMeasurement;
import com.qsystem.qsexcel.excelutil.ExcelHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;
import java.time.LocalDateTime;

/**
 * Excel风险测量导出器
 * 对应C# QSystem.Excel.ExcelExporteRiskMeasurement
 */
public class ExcelExporteRiskMeasurement {

    private static ExcelHelper _excel = null;
    private static final String RiskMeasurementSheetName = "RiskMeasurement";
    private static Logger log = LogManager.getLogger("QBroker");

    /**
     * 导出投资组合风险测量
     * 对应C# ExportPortRiskMeasurements方法
     */
    public static void ExportPortRiskMeasurements(QPortfolioBase QPortfolioBaseInfo, String fileName) {
        _excel = ExcelExportContext.getExcelHelper(fileName);
        ExportPortRiskMeasurementList(QPortfolioBaseInfo.RiskMeasurementList, Arrays.asList("TimeStamp", "balance", "benchMark", "exposure", "grossExposure", "deltaDollar", "vega", "theta", "margin"), RiskMeasurementSheetName);
        // Complete() will be called by ExcelExportContext
    }

    /**
     * 导出投资组合风险测量列表
     * 对应C# ExportPortRiskMeasurementList方法
     */
    private static void ExportPortRiskMeasurementList(SortedMap<LocalDateTime, RiskMeasurement> list, Collection<String> titles, String spreadSheetName) {
        int rowIndex = 0;
        int colIndex = 0;

        _excel.CreateNewWorkSheet(spreadSheetName);
        int count = list.size() + 1;

        Object[][] valueArray = new Object[count][titles.size()];

        // region Title
        for (String title : titles) {
            valueArray[rowIndex][colIndex++] = title;
        }
        // endregion

        for (Map.Entry<LocalDateTime, RiskMeasurement> ele : list.entrySet()) {
            LocalDateTime dt = ele.getKey();
            RiskMeasurement riskMeasurement = ele.getValue();
            colIndex = 0;
            rowIndex++;
            for (String title : titles) {
                if (title.equals("TimeStamp")) {
                    try {
                        valueArray[rowIndex][colIndex++] = dt.toString();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                } else {
                    String text = riskMeasurement.getRMMember(title);
                    try {
                        valueArray[rowIndex][colIndex++] = text;
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
            }
        }
        _excel.SetRangValue(1, 1, count, titles.size(), valueArray);
        // Complete() will be called by ExcelExportContext
    }
} 