package Model.PyDataModel;

import TagFramework.Model.Bar;
import TagFramework.TagLayer.TagLayer;
import TagFramework.TagLayer.TagOperation;
import TagOperationBackTest.TagOperationBackTest;
import TagOperationBackTest.TagOperators;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class PyDataModel implements Serializable
{
    private static final long serialVersionUID = -9000000001L;
    public  String underlyingSymbol;
    protected TreeMap<String, PyOptionModel> m_mapOption;

    public long nDateTime;
    public Bar _bar;

    public PyDataModel(long nTimestamp, String underlyingSymbol)
    {
        this.nDateTime=nTimestamp;
        this.underlyingSymbol=underlyingSymbol;
        this.m_mapOption = new TreeMap<>();
    }



    public void addOption(PyOptionModel pyOptionModel)
    {
        m_mapOption.put(pyOptionModel.getOptionTag(), pyOptionModel);
    }

    public PyOptionModel getOption(String symbol, int expiration, boolean is_call, double strike)
    {
        return m_mapOption.get(makeTag(symbol, expiration, is_call, strike));
    }

    public PyOptionModel getOption(String tag)
    {
        if (m_mapOption==null)
            return null;
        if(m_mapOption.containsKey(tag))
            return m_mapOption.get(tag);
        return null;
    }

    public Set<Map.Entry<String, PyOptionModel>> getOptions()
    {
        if (m_mapOption==null)
            return null;
        return m_mapOption.entrySet();
    }


    public Object get(String parameter)
    {
        String uppParam = parameter.toUpperCase();
        boolean isGettingUnderlying=uppParam.equals("CLOSE") || uppParam.equals("OPEN") || uppParam.equals("HIGH")|| uppParam.equals("LOW");

        if(isGettingUnderlying)
        {
            return getUnderlying(uppParam);
        }
        else
        {
            return getIntValue(uppParam);
        }

    }

    private Object getUnderlying(String uppParam)
    {
        if(_bar == null)
        {
           return 0.0;
        }
        else
        {
            switch (uppParam)
            {
                case "CLOSE":
                    double close= _bar.close== TagLayer.NaNInteger? 0.0 : _bar.getCloseDouble();
                    return close;
                case "OPEN":
                    double open= _bar.open== TagLayer.NaNInteger? 0.0 : _bar.getOpenDouble();
                    return open;
                case "HIGH":
                    double high= _bar.high== TagLayer.NaNInteger? 0.0 : _bar.getHighDouble();
                    return high;
                case "LOW":
                    double low= _bar.low== TagLayer.NaNInteger? 0.0 : _bar.getLowDouble();
                    return low;
            }
        }
        return 0.0;
    }

//    private Object getMaxpain()
//    {
//        double maxpain = TagOperators.getInstance(underlyingSymbol).GetDataPoint(nDateTime,"Maxpain");
//        return maxpain;
//    }
//
//    private double getVix()
//    {
//        double vix = TagOperators.getInstance(underlyingSymbol).GetDataPoint(nDateTime, "IVix");
//        return vix;
//    }
//
//    private double getVix2()
//    {
//        double vix2 = TagOperators.getInstance(underlyingSymbol).GetDataPoint(nDateTime, "VIX");
//        return vix2;
//    }

    private Object getIntValue(String intType)
    {
        if(nDateTime==0)
        {
            return 0.0;
        }
        double intValue = TagOperators.getInstance(underlyingSymbol).GetDataPoint(nDateTime,intType);
        return intValue;
    }

    public static String makeTag(String symbol, int expiration, boolean is_call, double strike) {
        return String.format("%s_%d_%s_%d", symbol, expiration, is_call?'C':'P', (int)(strike*1000));
    }

}
