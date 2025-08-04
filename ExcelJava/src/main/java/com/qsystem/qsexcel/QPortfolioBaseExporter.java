package com.qsystem.qsexcel;

import com.qsystem.clientlib.QPortfolioBase;
import com.qsystem.qsexcel.excelutil.ExcelHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.function.Consumer;
import com.qsystem.qsexcel.ExcelExporterTradeGroupReturn;
import com.qsystem.qsexcel.ExcelExporterTradeGroup;
import com.qsystem.qsexcel.ExcelExporterPositionHistory;
import com.qsystem.qsexcel.ExcelExporteRiskMeasurement;

/**
 * 投资组合导出器
 * 对应C# QSystem.Excel.QPortfolioBaseExporter
 */
public class QPortfolioBaseExporter {
    private static final Logger Log = LogManager.getLogger("QBroker");

    // region vars and properties
    public static ExcelHelper _excel = null;
    public static boolean _exportQPortfolioBaseEnable;
    public static final String SIGNALS = "Signals";
    private static final String DEBUGINFO = "DebugInfo";
    private static final String OrderS = "Orders";
    private static final String CompleteOrderSBYSYMBOL = "CompletedOrdersBySymbol";
    private static final String CompleteOrderSGROUPBYSYMBOL = "CompletedOrdersGroupBySymbol";
    private static final String BALANCES = "Balances";
    private static final String CURRENTPOSITIONS = "CurrentPositions";
    private static final String OVERTIMEPOSITIONS = "OverTimePositions";
    private static final String SUMMARY = "Summary";
    private static final String BENCHMARK = "Benchmark";

    protected static String XmlFileName = "result.xml";
    // endregion

    // region Export Entrance
    // public static void Export(QPortfolioBase QPortfolioBase, String fileName) {
    //     // 结构保留，暂未实现
    // }
    // endregion

    // region Excel Export
    /**
     * 主导出入口，对应C# ExportToExcel
     */
    public static void ExportToExcel(String fileName, QPortfolioBase port, Consumer<String> func) {
        Log.debug("Start Export Excel, filename " + fileName);
        
        // 清理可能存在的旧缓存
        ExcelExportContext.clearAll();

        // func.accept("正在生成LongCompleteOrderDistributions...");
        // ExcelExporterCompleteOrderDistributions.ExportPortCompleteOrderDistributions(port, fileName, SimulatorSystemStrings.LongCompleteTradeReturnDistributions, port.Summary.LongOrderReturnDistribution);
        // Log.debug("Export Long Order Distributions succeed!");

        // func.accept("正在生成ShortCompleteOrderDistributions...");
        // ExcelExporterCompleteOrderDistributions.ExportPortCompleteOrderDistributions(port, fileName, SimulatorSystemStrings.ShortCompleteTradeReturnDistributions, port.Summary.ShortOrderReturnDistribution);
        // Log.debug("Export Short Order Distributions succeed!");

        // func.accept("正在生成CompleteTradeReturnDistributions...");
        // ExcelExporterCompleteOrderDistributions.ExportPortCompleteOrderDistributions(port, fileName, SimulatorSystemStrings.CompleteTradeReturnDistributions, port.Summary.OrderReturnDistribution);
        // Log.debug("Export Order Distributions succeed!");

        func.accept("正在生成Orders...");
        ExcelExporterOrders.ExportPortOrders(port, fileName);
        Log.debug("Export QPortfolioBase Orders succeed!");

        func.accept("正在生成Summary...");
        ExcelExporterSummary.ExportPortSummary(port, fileName);
        Log.debug("Export QPortfolioBase Summary succeed!");
//comment out 3 lines for debugging
        func.accept("正在生成BalanceLineChart...");
        ExcelExporterBalanceLineChart.BuildBalanceLineChart(port.Summary.StrategyReturn, port.InitialCash, fileName);
        Log.debug("Export Balance Line Chart succeed!");

        // func.accept("正在生成OrderDistributionsLineChart...");
        // ExcelExporterOrderDistributionsLineChart.BuildOrderDistributionsLineChart(fileName, port.Summary.OrderReturnDistribution.WinLossRangeAverage, port.Summary.OrderReturnDistribution.SpreadCount + 1);
        // Log.debug("Export CompleteOrder Distributions Line Chart succeed!");

        // if (port.Summary.LongOrderReturnDistribution != null && port.Summary.LongOrderReturnDistribution.CompleteTradeList != null && port.Summary.LongOrderReturnDistribution.CompleteTradeList.size() > 0) {
        //     func.accept("正在生成LongOrderDistributionsLineChart...");
        //     ExcelExporterOrderDistributionsLineChart.BuildLongOrderDistributionsLineChart(fileName, port.Summary.LongOrderReturnDistribution.WinLossRangeAverage, port.Summary.LongOrderReturnDistribution.SpreadCount + 1);
        //     Log.debug("Export Long CompleteOrder Distributions Line Chart succeed!");
        // }

        // if (port.Summary.ShortOrderReturnDistribution != null && port.Summary.ShortOrderReturnDistribution.CompleteTradeList != null && port.Summary.ShortOrderReturnDistribution.CompleteTradeList.size() > 0) {
        //     func.accept("正在生成ShortOrderDistributionsLineChart...");
        //     ExcelExporterOrderDistributionsLineChart.BuildShortOrderDistributionsLineChart(fileName, port.Summary.ShortOrderReturnDistribution.WinLossRangeAverage, port.Summary.ShortOrderReturnDistribution.SpreadCount + 1);
        //     Log.debug("Export Short CompleteOrder Distributions Line Chart succeed!");
        // }

        // 最后完成并保存文件
        ExcelExportContext.completeExport(fileName,true);
        
        Log.debug("Export Excel Complete, filename " + fileName + "!");
    }
    // endregion

    /**
     * 调试信息导出，对应C# ExportDebugInfoToExcel
     */
    public static void ExportDebugInfoToExcel(String fileName, QPortfolioBase port, Consumer<String> func) {
        Log.debug("Start Export Debug Excel, filename " + fileName);
        
        // 清理可能存在的旧缓存
        ExcelExportContext.clearAll();

        func.accept("正在生成BalancesByYear...");
        ExcelExporterBalances.ExportPortBalancesByYear(port, fileName);
        Log.debug("Export QPortfolioBase Annual Performance Charts succeed!");

        func.accept("正在生成Benchmark...");
        ExcelExporterBenchmarkList.ExportPortBenchmarkList(port, fileName);
        Log.debug("Export QPortfolioBase Benchmark succeed!");

        func.accept("正在生成DailyReturn...");
        ExcelExporterDailyReturn.ExportPortDailyReturn(port, fileName);
        Log.debug("Export QPortfolioBase Daily Return succeed!");

        func.accept("正在生成DailyReturnLineChart...");
        ExcelExporterReturnLineChart.BuildDailyReturnLineChart(fileName, port.Summary.QPortfolioBaseReturnDistribution.WinLossRangeAverage, port.Summary.QPortfolioBaseReturnDistribution.SpreadCount + 1);
        Log.debug("Export Daily Return Line Chart succeed!");

        func.accept("正在生成TradeGroupReturn..");
        ExcelExporterTradeGroupReturn.ExportTradeGroupReturn(port, fileName);
        Log.debug("Export QPortfolioBase Daily Return succeed!");

        func.accept("正在生成TradeGroupReturnLineChart...");
        ExcelExporterReturnLineChart.BuildTradeGroupReturnLineChart(fileName, port.Summary.TradeGroupReturnDistribution.WinLossRangeAverage, port.Summary.TradeGroupReturnDistribution.SpreadCount + 1);
        Log.debug("Export TradeGroup Return Line Chart succeed!");

        func.accept("正在生成TradeGroups...");
        ExcelExporterTradeGroup.ExportPortTradeGroups(port, fileName);
        Log.debug("Export QPortfolioBase Orders succeed!");

        func.accept("正在生成CompleteOrders...");
        ExcelExporterCompleteOrders.ExportPortCompleteOrders(port, fileName);
        Log.debug("Export QPortfolioBase Complete Orders succeed!");

        func.accept("正在生成Balances...");
        ExcelExporterBalances.ExportPortBalances(port, fileName);
        Log.debug("Export QPortfolioBase Balances succeed!");

        func.accept("正在生成Position History...");
        ExcelExporterPositionHistory.ExportPortPositionHistory(port.PositionHistory, port.BalancesHiFrequency, port.BalancesDaily, fileName, "PositionHistory");
        Log.debug("Export QPortfolioBase OverTime Positions succeed!");

        // ExcelExporterOrders.ExportPortOrders(port, fileName);
        // Log.debug("Export QPortfolioBase Orders succeed!");

        func.accept("正在生成RiskMeasurements...");
        ExcelExporteRiskMeasurement.ExportPortRiskMeasurements(port, fileName);
        Log.debug("Export QPortfolioBase OverTime RiskMeasurements succeed!");

        // 最后完成并保存文件
        ExcelExportContext.completeExport(fileName, false);
        
        Log.debug("Export Debug Excel Complete, filename " + fileName + "!");
    }
} 