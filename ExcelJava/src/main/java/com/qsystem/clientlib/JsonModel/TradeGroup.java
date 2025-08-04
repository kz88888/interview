package com.qsystem.clientlib.JsonModel;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeGroup {
    public long openTimeStamp;
    public long closeTimeStamp;
    public String transactionID;
    @JsonProperty("parentID")
    public String parentID;
    public List<String> orderIDList; // transaction中涉及到的orders的orderID
    public Set<String> symbolsList; // transaction中涉及到的symbol
    public double PNL; // 整个tradeGroup的PNL
    public TradeGroupStatus status; // 判断是否整个tradeGroup已全部关仓
    @JsonProperty("renameMap")
    public Map<String, String> reNameMap;
    public String Note;
    public String ChangeNote;
    public String underlyingSymbol;
    public Map<String, Long> _CloseTimeStampMap;

    public TradeGroup() {
        orderIDList = new ArrayList<>();
        symbolsList = new HashSet<>();
        reNameMap = new HashMap<>();
    }

    // Getters and Setters
    public long getOpenTimeStamp() { return openTimeStamp; }
    public void setOpenTimeStamp(long openTimeStamp) { this.openTimeStamp = openTimeStamp; }
    public long getCloseTimeStamp() { return closeTimeStamp; }
    public void setCloseTimeStamp(long closeTimeStamp) { this.closeTimeStamp = closeTimeStamp; }
    public String getTransactionID() { return transactionID; }
    public void setTransactionID(String transactionID) { this.transactionID = transactionID; }
    public String getParentID() { return parentID; }
    public void setParentID(String parentID) { this.parentID = parentID; }
    public List<String> getOrderIDList() { return orderIDList; }
    public void setOrderIDList(List<String> orderIDList) { this.orderIDList = orderIDList; }
    public Set<String> getSymbolsList() { return symbolsList; }
    public void setSymbolsList(Set<String> symbolsList) { this.symbolsList = symbolsList; }
    public double getPNL() { return PNL; }
    public void setPNL(double PNL) { this.PNL = PNL; }
    public TradeGroupStatus getStatus() { return status; }
    public void setStatus(TradeGroupStatus status) { this.status = status; }
    public Map<String, String> getReNameMap() { return reNameMap; }
    public void setReNameMap(Map<String, String> reNameMap) { this.reNameMap = reNameMap; }
    public String getNote() { return Note; }
    public void setNote(String note) { Note = note; }
    public String getChangeNote() { return ChangeNote; }
    public void setChangeNote(String changeNote) { ChangeNote = changeNote; }
    public String getUnderlyingSymbol() { return underlyingSymbol; }
    public void setUnderlyingSymbol(String underlyingSymbol) { this.underlyingSymbol = underlyingSymbol; }
    public Map<String, Long> get_CloseTimeStampMap() { return _CloseTimeStampMap; }
    public void set_CloseTimeStampMap(Map<String, Long> map) { this._CloseTimeStampMap = map; }

    public String getTGMember(String title) {
        String get_title = title.toUpperCase();
        if (get_title.equals("OPENTIMESTAMP")) {
            return String.format("%015d", openTimeStamp);
        }
        if (get_title.equals("CLOSETIMESTAMP")) {
            return String.format("%017d", closeTimeStamp);
        }
        if (get_title.equals("TRADEGROUPID")) {
            return transactionID;
        }
        if (get_title.equals("PARENTID")) {
            if (parentID == null || parentID.isEmpty())
                return "";
            else
                return parentID;
        }
        else if (get_title.equals("ORDERIDLIST")) {
            return String.join(",", orderIDList);
        }
        else if (get_title.equals("SYMBOLSLIST")) {
            return String.join(",", symbolsList);
        }
        else if (get_title.equals("PNL")) {
            return String.valueOf(PNL);
        }
        else if (get_title.equals("ISLIVE")) {
            return status.toString();
        }
        else if (get_title.equals("RENAMEMAP")) {
            return reNameMap.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        }
        else if (get_title.equals("NOTE")) {
            return Note;
        }
        else if (get_title.equals("CHANGENOTE")) {
            return ChangeNote;
        }
        if (get_title.equals("UNDERLYINGSYMBOL")) {
            return underlyingSymbol;
        }
        else {
            return "";
        }
    }

    @Override
    public String toString() {
        return transactionID + ":" + Note + ";\n " + ChangeNote + "\n";
    }
} 