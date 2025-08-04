package Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Calculator {

    static public double calculateSharpeRatio(ArrayList<Double> balances, double strategyReturn, int firstNDate, int nDate) {
        ArrayList<Double> lnReturnCloseList = new ArrayList<>();
        if(balances.size() == 0) return 0;
        for (int i = 1; i < balances.size(); i++)
        {
            double lnReturnClose = calculateLN(balances.get(i), balances.get(i-1));
            lnReturnCloseList.add(lnReturnClose);
        }

        double volatility = calculateVolatility(lnReturnCloseList);
        double annualReturn = Math.pow(strategyReturn + 1, 1 / getYearInterval(firstNDate, nDate)) - 1;
        double annualExcessReturn = 0;
        if (annualReturn != 0)
            annualExcessReturn = annualReturn - 0.00529;
        double annualVol = volatility * Math.pow(252, 0.5);
        if (annualVol <= 0.0)
            return 0;
        return annualExcessReturn / annualVol;
        //System.out.println(String.format("%d: %f %f %f %f", nDate, strategyReturn, annualReturn, volatility, sharpeRatio));
        //return sharpeRatio;
    }

    static public double getYearInterval(int firstNDate, int nDate) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate ldFirst = LocalDate.parse(String.valueOf(firstNDate), df);
        LocalDate ldNow = LocalDate.parse(String.valueOf(nDate), df);
        return (double) ldFirst.until(ldNow, ChronoUnit.DAYS) / 365.0;
    }

    static public double calculateLN(double currentAdjClose, double formerAdjClose) {
        return Math.log(currentAdjClose / formerAdjClose);
    }

    static public List<Double> calculateLnOnBalances(List<Double> balances) {
        List<Double> balanceLnReturnList = new ArrayList<>();

        double formerBalance = 0;
        boolean isFirstValue = true;
        for(int i = 0; i < balances.size(); i++) {
            Double balance = balances.get(i);
            if (isFirstValue) {
                isFirstValue = false;
                balanceLnReturnList.add(Double.NaN);
            } else {
                balanceLnReturnList.add(calculateLN(balance, formerBalance));
                formerBalance = balance;
            }
        }
        return balanceLnReturnList;
    }

    static public SortedMap<Integer, Double> calculateLnOnBalances(SortedMap<Integer, Double> balances) {
        SortedMap<Integer, Double> balanceLnReturnMap = new TreeMap<>();

        double formerBalance = 0;
        boolean isFirstValue = true;
        for(Map.Entry<Integer, Double> entry : balances.entrySet()) {
            if (isFirstValue) {
                isFirstValue = false;
                balanceLnReturnMap.put(entry.getKey(), Double.NaN);
            } else {
                balanceLnReturnMap.put(entry.getKey(), calculateLN(entry.getValue(), formerBalance));
                formerBalance = entry.getValue();
            }
        }
        return balanceLnReturnMap;
    }

    static public double calculateVolatility(Collection<Double> lnValues) {
        double powSum = 0;
        double sum = 0;
        double sumPow = 0;

        int n = lnValues.size();

        for(double lnValue : lnValues) {
            powSum += Math.pow(lnValue, 2);
            sum += lnValue;
        }
        sumPow = Math.pow(sum,2);
        double tempValue = n * powSum - sumPow;
        tempValue = tempValue / (n * (n - 1));

        return Math.sqrt(tempValue);
    }

    public static double calculateBeta(List<Double> returnList, List<Double> balances)
    {
        List<Double> lnReturnList = calculateLnOnBalances(returnList);
        List<Double> LnOnBalances = calculateLnOnBalances(balances);

        double varValue = calculateVAR(lnReturnList);
        double covarValue = calculateCOVAR(lnReturnList, LnOnBalances);

        double beta = covarValue / varValue;
        return beta;
    }

    public static double calculateAverage(List<Double> valueList)
    {
        double sum = 0;
        for (double value : valueList)
        {
            if (!Double.isNaN(value) && !Double.isInfinite(value)) {
                sum += value;
            }
        }

        return sum / valueList.size();
    }

    public static double calculateVAR(List<Double> valueList)
    {
        double powSum = 0;
        double sum = 0;
        int count = 0;


        for (double value : valueList)
        {
            //double value = (valueList[date] as SimpleDouble).Data;

            if (!Double.isNaN(value) && !Double.isInfinite(value))
            {
                powSum += Math.pow(value, 2);
                sum += value;
                count++;
            }
        }

        double varValue = (count * powSum - Math.pow(sum, 2)) / (count * (count - 1));

        return varValue;
    }

    public static double calculateCOVAR(List<Double> xValueList, List<Double> yValueList)
    {
        double xAverage = calculateAverage(xValueList);
        double yAverage = calculateAverage(yValueList);

        double sum = 0;
        double count = 0;


        StringBuilder xValueBuilder = new StringBuilder();
        StringBuilder yValueBuilder = new StringBuilder();

        for (int i = 0; i < xValueList.size(); i++)
        {
            xValueBuilder.append(xValueList.get(i).toString());
            yValueBuilder.append(yValueList.get(i).toString());

            if (!Double.isNaN(xValueList.get(i))
                    && !Double.isNaN(yValueList.get(i))
                    && !Double.isInfinite(xValueList.get(i))
                    && !Double.isInfinite(yValueList.get(i)))
            {
                count++;

                sum += (xValueList.get(i) - xAverage) * (yValueList.get(i) - yAverage);
            }
        }

        double covar = sum / count;

        //File.AppendAllText("C:\\x.txt", xValueBuilder.ToString(), Encoding.Default);
        //File.AppendAllText("C:\\y.txt", yValueBuilder.ToString(), Encoding.Default);

        return covar;
    }


    public static double getVariance(List<Double> num)
    {

        double avg = 0.0;

        double count = num.size();

        for (int i = 0; i < num.size(); i++) {

            avg = avg + num.get(i);

        }

        avg = avg / count;

        return getVariance(num, avg);
    }

    public static double getVariance(List<Double> num, double avg) {

        double count = num.size();

        double ff = 0.0;

        for (int i = 0; i < num.size(); i ++) {

            ff = ff + (num.get(i) - avg) * (num.get(i) - avg);

        }

        ff = ff / count;

        return ff;

    }
}
