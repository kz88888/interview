package Utils;

import TagFramework.TagLayer.TagLayer;

public class TransferUtil {

    //to use template for test with NaNInteger 88888888
    public static int IntTransferForFlag(int intValue){
        if(intValue == Integer.MAX_VALUE-1) return TagLayer.CircuitBreakInteger;
        if(intValue>Integer.MAX_VALUE-10){
            return TagLayer.NaNInteger;
        }else {
            return intValue;
        }
    }
}
