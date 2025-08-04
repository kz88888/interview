package com.qsystem.qsexcel;

/**
 * 模拟器模式枚举
 */
public enum SimulatorMode {
    ExecuteStrategySaveToTag,
    LoadSignalsFromTagNoMerge,
    LoadSignalsFromTagMerge,
    EmailSignals,
    IncrementalModeMerge,
    EmailTrades,
    BackTestMerge,
    BackTestNoMerge,
    ForwardTest,
    None
} 