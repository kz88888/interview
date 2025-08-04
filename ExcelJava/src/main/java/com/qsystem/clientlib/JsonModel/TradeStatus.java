package com.qsystem.clientlib.JsonModel;

/**
 * 交易状态枚举
 * 对应C# QSystem.ClientLib.JsonModel.TradeStatus
 */
public enum TradeStatus {
    TRADE_OK,
    TRADE_FAILED_MARKET_NOT_OPEN,
    TRADE_FAILED_INVALID_ORDER,
    TRADE_FAILED,
    TRADE_FAILED_APPROVE_TRADE,
    TRADE_FAILED_INVALID_ASK_BID,
    TRADE_FAILED_GET_PRICE,
    TRADE_FAILED_INVALID_PRICE,
    TRADE_CANCELLED,
    TRADE_DUPLICATED,
    TRADE_WAITEXECTION,
    NULL
} 