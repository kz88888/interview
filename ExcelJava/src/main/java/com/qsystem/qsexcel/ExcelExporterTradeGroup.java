package com.qsystem.qsexcel;

import com.qsystem.clientlib.QPortfolioBase;
import com.qsystem.clientlib.JsonModel.TradeGroup;
import com.qsystem.qsexcel.excelutil.ExcelHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;

/**
 * Excel交易组导出器
 * 对应C# QSystem.Excel.ExcelExporterTradeGroup
 */
public class ExcelExporterTradeGroup {
    //20200121新增类
    private static ExcelHelper _excel = null;
    private static final String TradeGroupsSheetName = "TradeGroups";
    private static Logger log = LogManager.getLogger("QBroker");

    /**
     * 导出投资组合交易组
     * 对应C# ExportPortTradeGroups方法
     */
    public static void ExportPortTradeGroups(QPortfolioBase QPortfolioBaseInfo, String fileName) {
        _excel = ExcelExportContext.getExcelHelper(fileName);
        ExportPortTradeGroupList(QPortfolioBaseInfo.TradeGroupList, Arrays.asList("OpenTimeStamp", "CloseTimeStamp", "TradeGroupID", "MD5ID", "ParentID", "OrderIDList", "underlyingSymbol", "SymbolsList", "IsLive", "PNL", "ReNameMap", "Note", "ChangeNote"), TradeGroupsSheetName);
        // Complete() will be called by ExcelExportContext
    }

    /**
     * 导出投资组合交易组列表
     * 对应C# ExportPortTradeGroupList方法
     */
    private static void ExportPortTradeGroupList(SortedMap<String, TradeGroup> list, Collection<String> titles, String spreadSheetName) {
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

        for (Map.Entry<String, TradeGroup> ele : list.entrySet()) {
            TradeGroup tradeGroup = ele.getValue();
            colIndex = 0;
            rowIndex++;
            for (String title : titles) {
                String text = tradeGroup.getTGMember(title);
                try {
                    valueArray[rowIndex][colIndex++] = text;
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        _excel.SetRangValue(1, 1, count, titles.size(), valueArray);
        // Complete() will be called by ExcelExportContext
    }
} 