package Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class TimeLineUtil {
//static methods

    // TimeLinePattern = "925, 928, 930-1129, 1300-1547, 1500";
    public static List<Integer> getTimeBlockFromPattern(String timeLinePattern)
    {
        List<Integer> timeBlock = new ArrayList<>();
        String[] timeBlocksStr = timeLinePattern.split(",");
        for (String timeBlockStr : timeBlocksStr) {
            if (!timeBlockStr.contains("-")) {
                timeBlock.add(Integer.valueOf(timeBlockStr) * 100);
            } else {
                String startTimeStr = timeBlockStr.substring(0, timeBlockStr.indexOf('-'));
                String endTimeStr = timeBlockStr.substring(timeBlockStr.indexOf('-') + 1);
                int startTime = Integer.valueOf(startTimeStr);
                int endTime = Integer.valueOf(endTimeStr);
                int hours = endTime / 100 - startTime / 100;
                int startMin = startTime % 100;
                int endMin = 60;
                for (int hour = startTime / 100; hour < endTime / 100 + 1; hour++) {
                    if (hour == endTime / 100) {
                        endMin = endTime % 100 + 1;
                    }
                    for (int min = startMin; min < endMin; min++) {
                        timeBlock.add(hour * 10000 + min * 100);
                    }
                    startMin = 0;
                }
            }

        }
        return timeBlock;
    }

    public static int getDateIndexFromTimeLine(int nDate, TreeMap<Integer, List<Integer>> timeLine)
    {
        List<Integer> keyList = new ArrayList<Integer>(timeLine.keySet());
        return keyList.indexOf(nDate);
    }

    public static int getTimeIndex(int nDate, int nTime, TreeMap<Integer, List<Integer>> timeLine)
    {
        return timeLine.get(nDate).indexOf(nTime);
    }


    public static int findPreviousDay(int nDate, TreeMap<Integer, List<Integer>> timeLine)
    {
        int previousDay = 0;
        for (int timeLineKey : timeLine.keySet()) {
            if (timeLineKey > previousDay && timeLineKey != nDate)
                previousDay = timeLineKey;
        }
        return previousDay;
    }

    public static int getTimeCount(List<Integer> nDateKeys, TreeMap<Integer, List<Integer>> timeLine) {
        int count = timeLine.get(nDateKeys.get(0)).size();
        for (int nDate : nDateKeys) {
            if (timeLine.get(nDate).size() > count)
                count = timeLine.get(nDate).size();
        }
        return count;
    }


    public static int getTimeCountForOneDay(int nDate, TreeMap<Integer, List<Integer>> timeLine) {
        return timeLine.get(nDate).size();
    }

    public static int getTimeLineDateSize(TreeMap<Integer, List<Integer>> timeLine) {
        return timeLine.keySet().size();
    }

    public static int getTimeCountFromTimeLine(TreeMap<Integer, List<Integer>> timeLine)
    {
        List<Integer> keyList = new ArrayList<Integer>(timeLine.keySet());
        int count = timeLine.get(keyList.get(0)).size();
        for(int i=0;i<keyList.size();i++)
        {
            int nDate=keyList.get(i);
            if(timeLine.get(nDate).size()>count)
                count=timeLine.get(nDate).size();
        }
        return count;
    }

    public static int getFirstDate(TreeMap<Integer, List<Integer>> timeLine) {
        return timeLine.firstKey();
    }

    public static int getLastDate(TreeMap<Integer, List<Integer>> timeLine) {
        return timeLine.lastKey();
    }

    public static int getDateFromIndex(int dateIndex, TreeMap<Integer, List<Integer>> timeLine) {
        List<Integer> dateList = new ArrayList<Integer>(timeLine.keySet());
        int nDate = dateList.get(dateIndex);
        return nDate;
    }

    public static int getTimeFromIndex(int dateIndex, int timeIndex, TreeMap<Integer, List<Integer>> timeLine)
    {
        List<Integer> dateList = new ArrayList<Integer>(timeLine.keySet());
        int nDate = dateList.get(dateIndex);
        List<Integer> timeList = new ArrayList<Integer>(timeLine.get(nDate));
        int nTime = timeList.get(timeIndex);
        return nTime;
    }

    public static TreeMap<Integer, List<Integer>> InterSectionTimeLine(TreeMap<Integer, List<Integer>> timeLine1, TreeMap<Integer, List<Integer>> timeLine2) {
        TreeMap<Integer, List<Integer>> timeLineNew = new TreeMap<Integer, List<Integer>>();
        for (int nDate1 : timeLine1.keySet()) {
            if (timeLine2.containsKey(nDate1)) {
                List<Integer> timeListNew = new ArrayList<>();
                for (int nTime1 : timeLine1.get(nDate1)) {
                    if (timeLine2.get(nDate1).contains(nTime1)) {
                        timeListNew.add(nTime1);
                    }
                }
                timeLineNew.put(nDate1, timeListNew);
            }
        }
        return timeLineNew;
    }
}
