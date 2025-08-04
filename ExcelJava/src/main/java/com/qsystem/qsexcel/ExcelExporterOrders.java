package com.qsystem.qsexcel;

import com.qsystem.clientlib.QPortfolioBase;
import com.qsystem.clientlib.TradeList;
import com.qsystem.clientlib.JsonModel.TradeConfirmation;
import com.qsystem.clientlib.JsonModel.OrderSymbolType;
import com.qsystem.clientlib.JsonModel.OrderCategory;
import com.qsystem.qsexcel.excelutil.ExcelHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;
import java.time.LocalDateTime;

/**
 * Excel订单导出器
 * 对应C# QSystem.Excel.ExcelExporterOrders (在ExcelExporterTrades.cs中)
 */
public class ExcelExporterOrders {
    private static ExcelHelper _excel = null;
    private static final String OrdersSheetName = "Orders";
    private static final String RollbackOrdersSheetName = "RollbackOrders";
    private static final String PortfolioHedgingSheetName = "PortfolioHedging";
    private static Logger log = LogManager.getLogger("QBroker");

    /**
     * 仅导出订单
     * 对应C# ExportOrdersOnly方法
     */
    public static void ExportOrdersOnly(TradeList orders, String sheetName, String fileName) {
        _excel = ExcelExportContext.getExcelHelper(fileName);
        ExportPortTradeList(orders, Arrays.asList("TimeStamp", "OrderTime", "Symbol", "OptionSymbol", "OrderStatus", "OrderType", "Price", "Quantity", "TransactionExpenses", "OrderID", "TradeGroupID", "ParentID", "OrderCategory", "Note"), sheetName);
        // Complete() will be called by ExcelExportContext
    }

    /**
     * 导出投资组合订单
     * 对应C# ExportPortOrders方法
     */
    public static void ExportPortOrders(QPortfolioBase QPortfolioBaseInfo, String fileName) {
        _excel = ExcelExportContext.getExcelHelper(fileName);
        //20200115新增ordertype
        ExportPortTradeList(QPortfolioBaseInfo.Orders, Arrays.asList("TimeStamp", "OrderTime", "Symbol", "OptionSymbol", "OrderStatus", "OrderType", "Price", "Quantity", "TransactionExpenses", "OrderID", "TradeGroupID", "ParentID", "OrderCategory", "Note"), OrdersSheetName);
        //TradeList rollbackOrders = QPortfolioBaseInfo.Orders;
        //ExportPortTradeList(QPortfolioBaseInfo.Orders, new[] { "TimeStamp", "OrderTime", "Symbol", "OptionSymbol", "OrderStatus","OrderType", "Price", "Quantity", "TransactionExpenses", "OrderID","TradeGroupID", "ParentID", "OrderCategory", "Ask", "Bid", "Underlying", "Note" }, RollbackOrdersSheetName);
        //ExportPortTradeList(QPortfolioBaseInfo.PortfolioHedgingOrders, new[] { "TimeStamp", "OrderTime", "Symbol", "OptionSymbol", "OrderStatus","OrderType", "Price", "Quantity", "TradeGroupID", "TransactionExpenses", "OrderCategory", "Note" }, PortfolioHedgingSheetName);
        // Complete() will be called by ExcelExportContext
    }

    /**
     * 导出投资组合交易列表
     * 对应C# ExportPortTradeList方法
     */
    private static void ExportPortTradeList(TradeList list, Collection<String> titles, String spreadSheetName) {
        int rowIndex = 0;
        int colIndex = 0;
        List<String> processedSystemTradeList = new ArrayList<>();
        boolean rollback = false;
        if (spreadSheetName.contains("Rollback")) {
            rollback = true;
        }
        _excel.CreateNewWorkSheet(spreadSheetName);
        int count = 0;
        for (LocalDateTime dt : list.keySet()) {
            for (List<TradeConfirmation> TradeList : list.get(dt).values()) {
                count += TradeList.size();
                for (TradeConfirmation Order : TradeList) {
                    if (Order.SymbolType == OrderSymbolType.Stock) {
                        count++;
                        count++;
                    }
                }
            }
        }
        count = count + 2;
        Object[][] valueArray = new Object[count][titles.size()];

        // region Title
        for (String title : titles) {
            valueArray[rowIndex][colIndex++] = title;
        }
        // endregion

        for (Map.Entry<LocalDateTime, SortedMap<String, List<TradeConfirmation>>> ele : list.entrySet()) {
            for (String symbol : ele.getValue().keySet()) {
                List<TradeConfirmation> TradeList = ele.getValue().get(symbol);

                for (TradeConfirmation Order : TradeList) {
                    if (rollback && Order.orderCategory != OrderCategory.Executable_Client)
                        continue;
                    colIndex = 0;
                    rowIndex++;
                    for (String title : titles) {
                        String text = Order.GetOrderMember(title);
                        if (title.equals("OptionSymbol")) {
                            if (Order.SymbolType != OrderSymbolType.Option) {
                                colIndex++;
                                continue;
                            }
                        } else if (title.equals("Price")) {
                            String price = String.format("%.4f", Order.Price);
                            if (price.contains(".")) {
                                String[] parts = price.split("\\.");
                                if (parts.length == 2) {
                                    if (parts[1].length() == 3) {
                                        price = price + "0K";
                                    }
                                }
                            }
                            valueArray[rowIndex][colIndex++] = price;
                            continue;
                        } else if (title.equals("TradeGroupID")) {
                            valueArray[rowIndex][colIndex++] = Order.TradeGroupIDStr;
                            continue;
                        } else if (title.equals("ParentID")) {
                            valueArray[rowIndex][colIndex++] = Order.ParentIDStr;
                            continue;
                        } else if (title.equals("Note")) {
                            valueArray[rowIndex][colIndex++] = Order.Note;
                            continue;
                        }
                        try {
                            valueArray[rowIndex][colIndex++] = text;
                        } catch (Exception e) {
                            log.error(e.getMessage());
                        }
                    }
                }
            }
        }
        _excel.SetRangValue(1, 1, count, titles.size(), valueArray);
    }

    /**
     * 导出投资组合交易记录
     * 对应C# ExportPortTrades方法
     */
    public static void ExportPortTrades(QPortfolioBase port, String fileName) {
        ExportPortTrades(port.Orders, fileName);
    }

    /**
     * 导出交易记录
     * 对应C# ExportPortTrades方法
     */
    public static void ExportPortTrades(TradeList trades, String fileName) {
        _excel = ExcelExportContext.getExcelHelper(fileName);
        ExportPortTradeList(trades, Arrays.asList("TimeStamp", "OrderTime", "Symbol", "OptionSymbol", "OrderStatus", "OrderType", "Price", "Quantity", "TransactionExpenses", "OrderID", "TradeGroupID", "ParentID", "OrderCategory", "Note"), "Trades");
        // Complete() will be called by ExcelExportContext
    }
} 