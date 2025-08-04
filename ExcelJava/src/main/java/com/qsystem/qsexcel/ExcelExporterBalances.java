package com.qsystem.qsexcel;

import com.qsystem.clientlib.QPortfolioBase;
import com.qsystem.qsexcel.excelutil.ExcelHelper;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.qsystem.clientlib.BalanceDailyList;
import com.qsystem.clientlib.BalanceList;
import com.qsystem.clientlib.LnOnBalanceList;
import java.time.LocalDate;
import java.util.*;

/**
 * Excel余额导出器
 */
public class ExcelExporterBalances {
    private static ExcelHelper _excel = null;

    /**
     * 仅导出余额
     */
    public static void ExportBalancesOnly(Map<LocalDateTime, Double> balanceDailyList, String name, String fileName) {
        _excel = ExcelExportContext.getExcelHelper(fileName);
        int rowIndex = 0;
        int colIndex = 0;
        _excel.CreateNewWorkSheet(name);
        
        if (balanceDailyList.size() < 1) {
            return;
        }

        Object[][] valueArray = new Object[balanceDailyList.size() + 1][2];
        
        // 标题
        valueArray[rowIndex][colIndex++] = "TimeStamp";
        valueArray[rowIndex][colIndex++] = "Account Balance";

        for (Map.Entry<LocalDateTime, Double> balance : balanceDailyList.entrySet()) {
            colIndex = 0;
            rowIndex++;
            LocalDateTime time1 = balance.getKey();
            valueArray[rowIndex][colIndex++] = time1.toLocalDate().toString();
            valueArray[rowIndex][colIndex++] = balance.getValue().toString();
        }

        _excel.SetRangValue(1, 1, balanceDailyList.size() + 1, 2, valueArray);
        // Complete() will be called by ExcelExportContext
    }

    /**
     * 导出投资组合余额
     */
    public static void ExportPortBalances(QPortfolioBase QPortfolioBaseInfo, String fileName) {
        _excel = ExcelExportContext.getExcelHelper(fileName);
        int rowIndex = 0;
        int colIndex = 0;
        _excel.CreateNewWorkSheet("Balances");
        
        Map<LocalDateTime, Double> balances = QPortfolioBaseInfo.BalancesDaily;
        SortedMap<LocalDateTime, Double> benchmarkValues = QPortfolioBaseInfo.Benchmark.benchmarkValues;
        Map<LocalDateTime, Double> lns = QPortfolioBaseInfo.LnOnBalances;

        if (balances.size() < 1 || benchmarkValues.size() < 1 || lns.size() < 1) {
            return;
        }

        Object[][] valueArray = new Object[balances.size() + 1][7];

        // 标题
        valueArray[rowIndex][colIndex++] = "TimeStamp";           
        valueArray[rowIndex][colIndex++] = "Account Balance";
        valueArray[rowIndex][colIndex++] = "Benchmark Value";
        valueArray[rowIndex][colIndex++] = "Sharp Ratio";
        valueArray[rowIndex][colIndex++] = "Benchmark Sharp Ratio";
        valueArray[rowIndex][colIndex++] = "Ln on Balance";
        valueArray[rowIndex][colIndex] = "nTime";

        double startBalance = balances.values().iterator().next();
        double startBenchmark = benchmarkValues.values().iterator().next();
        double factor = startBalance / startBenchmark;
        double last_benchmark = startBenchmark * factor;
        
        for (Map.Entry<LocalDateTime, Double> balance : balances.entrySet()) {
            colIndex = 0;
            rowIndex++;
            LocalDateTime time1 = balance.getKey();
            LocalDate dateOnly = time1.toLocalDate();
            String nTimeStr = time1.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            valueArray[rowIndex][colIndex++] = dateOnly.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));                
            valueArray[rowIndex][colIndex++] = String.valueOf(balance.getValue());
            
            // Find benchmark value with matching date
            Optional<Map.Entry<LocalDateTime, Double>> matchingBenchmark = benchmarkValues.entrySet().stream()
                    .filter(entry -> entry.getKey().toLocalDate().equals(dateOnly))
                    .findFirst();
            
            if (matchingBenchmark.isPresent()) {
                valueArray[rowIndex][colIndex++] = String.valueOf(matchingBenchmark.get().getValue() * factor);
                last_benchmark = matchingBenchmark.get().getValue() * factor;
            } else {
                valueArray[rowIndex][colIndex++] = String.valueOf(last_benchmark);
            }

            // 这里需要根据实际的QPortfolioBase结构来调整
            // 暂时设置为0
            valueArray[rowIndex][colIndex++] = "0";
            valueArray[rowIndex][colIndex++] = "0";

            if (lns.containsKey(time1)) {
                valueArray[rowIndex][colIndex++] = String.valueOf(lns.get(time1));
            } else {
                valueArray[rowIndex][colIndex++] = "NaN";
            }
            valueArray[rowIndex][colIndex++] = nTimeStr;
        }
        
        _excel.SetRangValue(1, 1, balances.size() + 1, 7, valueArray);
        // Complete() will be called by ExcelExportContext
    }

    // 年度分组余额导出及图表
    public static void ExportPortBalancesByYear(QPortfolioBase QPortfolioBaseInfo, String fileName) {
        _excel = ExcelExportContext.getExcelHelper(fileName);
        int rowIndex = 0;
        int colIndex = 0;
        _excel.CreateNewWorkSheet(SimulatorSystemStrings.AnnualPerfomance);
        Map<LocalDateTime, Double> balances = QPortfolioBaseInfo.BalancesDaily;
        Map<LocalDateTime, Double> balancesHiFrequency = QPortfolioBaseInfo.BalancesHiFrequency;
        SortedMap<LocalDateTime, Double> benchmarkValues = QPortfolioBaseInfo.Benchmark.benchmarkValues;
        Map<LocalDateTime, Double> lns = QPortfolioBaseInfo.LnOnBalances;
        
        if (balances.size() < 1 || benchmarkValues.size() < 1 || lns.size() < 1) {
            return;
        }
        
        // 收集所有数据，按年份分组
        Map<Integer, List<Map.Entry<LocalDateTime, Double>>> balancesByYear = new LinkedHashMap<>();
        for (Map.Entry<LocalDateTime, Double> balance : balances.entrySet()) {
            int year = balance.getKey().getYear();
            balancesByYear.computeIfAbsent(year, k -> new ArrayList<>()).add(balance);
        }
        
        int startColIndex = 900;
        int endColIndex = startColIndex + 2;
        int startLineLeft = 30;
        int startLineTop = 30;
        int countOfChart = 0;
        
        double initialBalance = balancesHiFrequency.values().iterator().next();
        double initialBenchmark = benchmarkValues.values().iterator().next();
        
        // 处理每个年份的数据
        for (Map.Entry<Integer, List<Map.Entry<LocalDateTime, Double>>> yearEntry : balancesByYear.entrySet()) {
            int year = yearEntry.getKey();
            List<Map.Entry<LocalDateTime, Double>> yearBalances = yearEntry.getValue();
            
            // 创建适当大小的数组（年份数据行数 + 标题行）
            int arraySize = Math.min(yearBalances.size() + 1, 400); // 限制最大400行
            Object[][] valueArray = new Object[arraySize][3];
            rowIndex = 0;
            colIndex = 0;
            
            // 标题行
            valueArray[rowIndex][colIndex++] = "TimeStamp";
            valueArray[rowIndex][colIndex++] = "Account Balance";
            valueArray[rowIndex][colIndex++] = "Benchmark Value";
            
            // 获取该年第一个balance作为起始值
            double startBalance = yearBalances.get(0).getValue();
            LocalDate firstDate = yearBalances.get(0).getKey().toLocalDate();
            
            // 查找对应的benchmark起始值
            LocalDateTime matchingBenchmarkKey = null;
            for (LocalDateTime benchmarkKey : benchmarkValues.keySet()) {
                if (benchmarkKey.toLocalDate().equals(firstDate)) {
                    matchingBenchmarkKey = benchmarkKey;
                    break;
                }
            }
            
            if (matchingBenchmarkKey == null) {
                // 如果没找到，查找下一个可用日期
                LocalDate dt = firstDate;
                while (matchingBenchmarkKey == null && dt.isBefore(benchmarkValues.lastKey().toLocalDate().plusDays(1))) {
                    dt = dt.plusDays(1);
                    for (LocalDateTime benchmarkKey : benchmarkValues.keySet()) {
                        if (benchmarkKey.toLocalDate().equals(dt)) {
                            matchingBenchmarkKey = benchmarkKey;
                            break;
                        }
                    }
                }
            }
            
            if (matchingBenchmarkKey == null) {
                continue; // 跳过这一年
            }
            
            double startBenchmark = benchmarkValues.get(matchingBenchmarkKey);
            double factor = QPortfolioBaseInfo._config.InitialCash / startBenchmark;
            double balanceFactor = QPortfolioBaseInfo._config.InitialCash / startBalance;
            double last_benchmark = startBenchmark * factor;
            
            // 填充该年的数据
            for (Map.Entry<LocalDateTime, Double> balance : yearBalances) {
                rowIndex++;
                colIndex = 0;
                
                LocalDate date = balance.getKey().toLocalDate();
                valueArray[rowIndex][colIndex++] = date.toString();
                valueArray[rowIndex][colIndex++] = String.valueOf(balanceFactor * balance.getValue());
                
                // 查找benchmark值
                LocalDateTime benchmarkKey = null;
                for (LocalDateTime bKey : benchmarkValues.keySet()) {
                    if (bKey.toLocalDate().equals(date)) {
                        benchmarkKey = bKey;
                        break;
                    }
                }
                
                if (benchmarkKey != null) {
                    valueArray[rowIndex][colIndex++] = String.valueOf(benchmarkValues.get(benchmarkKey) * factor);
                    last_benchmark = benchmarkValues.get(benchmarkKey) * factor;
                } else {
                    valueArray[rowIndex][colIndex++] = String.valueOf(last_benchmark);
                }
            }
            
            // 写入数据并创建图表 - 只写入实际的数据行数
            int actualRows = rowIndex + 1; // rowIndex是最后一行的索引，加1得到总行数
            _excel.SetRangValue(1, startColIndex, actualRows, endColIndex, valueArray);
            
            // 计算该年的收益率
            double endBalance = yearBalances.get(yearBalances.size() - 1).getValue();
            double strategyReturn = endBalance / startBalance;
            
            BuildPortAnnualPerfomanceLineChart(_excel, strategyReturn, QPortfolioBaseInfo._config.InitialCash, 
                startColIndex, endColIndex, startLineLeft, startLineTop, String.valueOf(year), fileName);
            
            // 更新位置
            countOfChart++;
            startColIndex += 3;
            endColIndex = startColIndex + 2;
            if (countOfChart % 2 == 0) {
                startLineLeft = 30;
                startLineTop += 350;
            } else {
                startLineLeft += 500;
            }
        }
        
        // Complete() will be called by ExcelExportContext
    }

    // 年度图表
    public static void BuildPortAnnualPerfomanceLineChart(ExcelHelper excel, double strategyReturn, double initCapital, int startColIndex, int endColIndex, int left, int top, String titleName, String fileName) {
        String dataSheetName = SimulatorSystemStrings.AnnualPerfomance;
        String chartSheetName = SimulatorSystemStrings.AnnualPerfomance;
        boolean isLogScale = false;
        if (strategyReturn > 10) {
            isLogScale = true;
        }
        excel.CreateLineChart(isLogScale, initCapital, dataSheetName, chartSheetName, startColIndex, endColIndex, left, top, 2, titleName, "Date", "Balance", "Benchmark Value", 400, 250);
    }

    // 期间收益导出
    public static void ExportPortPeriodicalReturn(QPortfolioBase QPortfolioBaseInfo, String fileName) {
        _excel = ExcelExportContext.getExcelHelper(fileName);
        int rowIndex = 0;
        int colIndex = 0;
        _excel.CreateNewWorkSheet("Periodical Return");
        Map<LocalDateTime, Double> balances = QPortfolioBaseInfo.BalancesDaily;
        if (balances.size() < 1) {
            return;
        }
        Object[][] valueArray = new Object[balances.size() + 1][4];
        valueArray[rowIndex][colIndex++] = "Date";
        valueArray[rowIndex][colIndex++] = "Return";
        List<LocalDateTime> keys = new ArrayList<>(balances.keySet());
        keys.sort(Comparator.naturalOrder());
        LocalDateTime startDate = keys.get(0);
        boolean first = true;
        LocalDateTime curDate = startDate.plusMonths(1).withDayOfMonth(1).minusDays(1);
        int count = 0;
        while (curDate.isBefore(keys.get(keys.size() - 1)) || curDate.isEqual(keys.get(keys.size() - 1))) {
            rowIndex++;
            LocalDateTime lastDate = curDate.plusMonths(1).withDayOfMonth(1).minusDays(1);
            if (first) {
                lastDate = startDate;
            }
            double balanceReturn = CalculateBalanceReturn(balances, curDate, lastDate);
            valueArray[rowIndex][colIndex++] = curDate.toLocalDate().toString();
            valueArray[rowIndex][colIndex++] = balanceReturn + "";
            curDate = curDate.plusMonths(1).withDayOfMonth(1).minusDays(1);
            count++;
        }
        _excel.SetRangValue(1, 1, count + 1, 2, valueArray);
        // Complete() will be called by ExcelExportContext
    }

    // 辅助方法：查找余额
    public static double GetBalanceValue(Map<LocalDateTime, Double> balanceList, LocalDateTime date) {
        double balance = Double.NaN;
        if (balanceList.containsKey(date)) {
            return balanceList.get(date);
        } else {
            List<LocalDateTime> keys = new ArrayList<>(balanceList.keySet());
            keys.sort(Comparator.naturalOrder());
            if (keys.get(0).isBefore(date)) {
                LocalDateTime cur = date;
                while (!cur.isBefore(keys.get(0))) {
                    if (balanceList.containsKey(cur)) {
                        balance = balanceList.get(cur);
                        break;
                    }
                    cur = cur.minusDays(1);
                }
            }
        }
        return balance;
    }

    // 辅助方法：计算期间收益率
    public static double CalculateBalanceReturn(Map<LocalDateTime, Double> balanceList, LocalDateTime endTime, LocalDateTime startTime) {
        double balanceReturn = Double.NaN;
        double startReturn = GetBalanceValue(balanceList, startTime);
        double endReturn = GetBalanceValue(balanceList, endTime);
        if (!Double.isNaN(startReturn) && !Double.isNaN(endReturn)) {
            balanceReturn = (endReturn - startReturn) / startReturn;
        }
        return balanceReturn;
    }
} 