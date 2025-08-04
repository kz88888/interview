package com.qsystem.clientlib;

/**
 * 持仓状态枚举
 * 对应C# QSystem.ClientLib.PositionState
 */
public enum PositionState {
    Opened,
    Closed,
    ParticallyClosed,
    Exercised, // Exercised 就是说你手里本来有+数量的 options然后因为符合我定义的条件，你行权了。
    Assigned,  // Assigned 就是说你手里本来有-数量的options然后因为符合我定义的条件，你被行权了。
    Expired    // Expired就是无论你有+ 还是- 数量的 都是过期作废没有行权
} 