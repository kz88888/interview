package com.qsystem.qsexcel;

import com.qsystem.clientlib.QPortfolioBase;
import com.qsystem.clientlib.CompleteOrder;
import com.qsystem.clientlib.JsonModel.TradeGroup;
import com.qsystem.qsexcel.excelutil.ExcelHelper;
import java.util.*;

/**
 * Excel完整订单导出器
 * 对应C#的ExcelExporterCompleteOrders类
 */
public class ExcelExporterCompleteOrders {
    
    private static ExcelHelper _excel = null;
    private static final String CompleteOrderSBYSYMBOL = "CompletedOrdersBySymbol";
    private static final String CompleteOrderSGROUPBYSYMBOL = "CompletedOrdersGroupBySymbol";

    /**
     * 导出投资组合完整订单
     * 对应C#的ExportPortCompleteOrders方法
     */
    public static void ExportPortCompleteOrders(QPortfolioBase QPortfolioBase, String fileName) {
        _excel = ExcelExportContext.getExcelHelper(fileName);
        //ExportPortCompleteTradeList(QPortfolioBase.CompletedOrdersBySymbolDictionary, new[] { "TimeStamp", "Price", "Quantity", "ORDERCATEGORY" }, CompleteOrderSBYSYMBOL, true, true);
        //ExportPortCompletedOrdersGroupBySymbol(QPortfolioBase, QPortfolioBase.CompletedOrdersBySymbolDictionary, CompleteOrderSGROUPBYSYMBOL);
        ExportPortCompletedOrdersGroupBySymbol(QPortfolioBase.TradeGroupList, CompleteOrderSGROUPBYSYMBOL);//20200131修改
        // Complete() will be called by ExcelExportContext
    }

    /*
    private static void ExportPortCompleteTradeList(SortedMap<String, List<CompleteOrder>> list, Collection<String> titles, String spreadSheetName, boolean flag, boolean symbolSummary) {
        // 对应C#的ExportPortCompleteTradeList方法
        // 由于C#版本被注释掉了，这里也注释掉
    }
    */

    /**
     * 按符号分组导出已完成订单
     * 对应C#的ExportPortCompletedOrdersGroupBySymbol方法（第一个重载）
     */
    private static void ExportPortCompletedOrdersGroupBySymbol(QPortfolioBase QPortfolioBase, SortedMap<String, List<CompleteOrder>> list, String spreadSheetName) {
        int rowIndex = 0;
        int colIndex = 0;
        _excel.CreateNewWorkSheet(spreadSheetName);
        SortedMap<String, List<QPortfolioBase.SectorIndustry>> sectorIndustryDictionary = QPortfolioBase.sectorIndustryDictionary;
        int maxSectorCount = QPortfolioBase.maxSectorCount;
        int colCount = 2 + maxSectorCount * 2;
        int rowCount = list.size() + 1;
        Object[][] valueArray = new Object[rowCount][colCount];
        
        valueArray[rowIndex][colIndex++] = "Symbol";
        valueArray[rowIndex][colIndex++] = "Sum P&L";
        for (int i = 0; i < maxSectorCount; i++) {
            valueArray[rowIndex][colIndex++] = "Sector";
            valueArray[rowIndex][colIndex++] = "Industry";
        }
        rowIndex++;
        
        for (String symbolKey : list.keySet()) {
            double sumPNL = 0;
            boolean valid = false;
            List<CompleteOrder> completeTradeList = list.get(symbolKey);
            if (completeTradeList.size() < 1) {
                continue;
            }
            for (CompleteOrder CompleteOrder : completeTradeList) {
                if (CompleteOrder.CompleteTradeList.size() < 1) {
                    continue;
                }
                valid = true;
                sumPNL += CompleteOrder.OrderProfit;
            }
            if (!valid) {
                continue;
            }
            colIndex = 0;
            valueArray[rowIndex][colIndex++] = completeTradeList.get(0).CompleteTradeList.get(0).getSymbol();
            valueArray[rowIndex][colIndex++] = String.valueOf(sumPNL);
            //QPortfolioBase._qBroker.GetSectorIndustry(completeTradeList[0].CompleteTradeList[0].Symbol);
            if (sectorIndustryDictionary.containsKey(completeTradeList.get(0).CompleteTradeList.get(0).getSymbol())) {
                List<QPortfolioBase.SectorIndustry> sectorIndustryList = sectorIndustryDictionary.get(completeTradeList.get(0).CompleteTradeList.get(0).getSymbol());
                for (QPortfolioBase.SectorIndustry sectorIndustry : sectorIndustryList) {
                    valueArray[rowIndex][colIndex++] = sectorIndustry.sector;
                    valueArray[rowIndex][colIndex++] = sectorIndustry.industry;
                }
            }
            rowIndex++;
        }
        _excel.SetRangValue(1, 1, rowCount, colCount, valueArray);
    }

    /**
     * 按符号分组导出已完成订单（20200131新增）
     * 对应C#的ExportPortCompletedOrdersGroupBySymbol方法（第二个重载）
     */
    private static void ExportPortCompletedOrdersGroupBySymbol(SortedMap<String, TradeGroup> tradeGroupList, String spreadSheetName) {
        int rowIndex = 0;
        int colIndex = 0;
        _excel.CreateNewWorkSheet(spreadSheetName);

        Set<String> underlyingSymbolList = new HashSet<>();
        for (TradeGroup tradeGroup : tradeGroupList.values()) {
            underlyingSymbolList.add(tradeGroup.underlyingSymbol);
        }

        int colCount = 2;
        int rowCount = underlyingSymbolList.size() + 1;
        Object[][] valueArray = new Object[rowCount][colCount];
        valueArray[rowIndex][colIndex++] = "Symbol";
        valueArray[rowIndex][colIndex++] = "Sum P&L";
       
        rowIndex++;
        for (String underlyingSymbol : underlyingSymbolList) {
            double sumPNL = 0;
            boolean valid = false;
            for (TradeGroup tradeGroup : tradeGroupList.values()) {
                if (!tradeGroup.underlyingSymbol.equals(underlyingSymbol)) {
                    continue;
                }
                valid = true;
                sumPNL += tradeGroup.PNL;
            }
            if (!valid) {
                continue;
            }
            colIndex = 0;
            valueArray[rowIndex][colIndex++] = underlyingSymbol;
            valueArray[rowIndex][colIndex++] = String.valueOf(sumPNL);              
            rowIndex++;
        }
        _excel.SetRangValue(1, 1, rowCount, colCount, valueArray);
    }
} 