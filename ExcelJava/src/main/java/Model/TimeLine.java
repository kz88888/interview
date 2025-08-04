package Model;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TimeLine {
    private ArrayList<Integer> timestampList;
    private HashMap<Integer, Integer> timestampIndexMap;
    private SortedMap<Integer, TimeLine> childrenMap; //子TimeLine记录一天中的分钟或者Tick时间

    public TimeLine() {
        timestampList = new ArrayList<>();
        timestampIndexMap = new HashMap<>();

        childrenMap = new TreeMap<>();
    }

    public TimeLine( TreeMap<Integer, List<Integer>> timeLineNew)
    {
        timestampList = new ArrayList<>(timeLineNew.keySet());
        timestampIndexMap = new HashMap<>();
        childrenMap = new TreeMap<>();
    }

    public void addTimestamp(int timestamp)
    {
        timestampList.add(timestamp);
        timestampIndexMap.put(timestamp, timestampList.size()-1);
    }

    public void sort() {
        Collections.sort(timestampList);
        timestampIndexMap.clear();
        for(int i = 0; i < timestampList.size(); i++) {
            timestampIndexMap.put(timestampList.get(i), i);
        }

        for(TimeLine child : childrenMap.values()) {
            child.sort();
        }
    }

    public void clear() {
        timestampList.clear();
        timestampIndexMap.clear();
        childrenMap.clear();
    }

    public Integer get(int index) {
        return timestampList.get(index);
    }

    public int size() {
        return timestampList.size();
    }

    public Integer getIndex(int nTimestamp) {
        return timestampIndexMap.get(nTimestamp);
    }

    public TimeLine subTimeLine(int start, int end) {
        TimeLine timeLine = new TimeLine();
        for(int i = 0; i < size(); i++) {
            int timestamp = get(i);
            if(start <= timestamp && timestamp <= end) {
                timeLine.addTimestamp(timestamp);
                timeLine.newChild(timestamp);

                TimeLine timeLineChild = getChild(timestamp);
                if(timeLineChild != null) {
                    for(int j = 0; j < timeLineChild.size(); j++)
                        timeLine.getChild(timestamp).addTimestamp(timeLineChild.get(j));
                }
            }
        }
        return timeLine;
    }

    public boolean contains(int nTimestamp) {
        return timestampIndexMap.containsKey(nTimestamp);
    }

    public void newChild(int nTimestamp) {
        childrenMap.put(nTimestamp, new TimeLine());
    }

    public TimeLine getChild(int nTimestamp) {
        return childrenMap.get(nTimestamp);
    }

    public ArrayList<Integer> getTimestampList()
    {
        return  timestampList;
    }
}
