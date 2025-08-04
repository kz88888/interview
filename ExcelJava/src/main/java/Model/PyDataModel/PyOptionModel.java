package Model.PyDataModel;

import TagFramework.Metadata.OptionDataDescriptor;
import TagFramework.Model.OptionCore;
import TagFramework.Model.OptionGreeks;
import TagFramework.TagLayer.TagLayer;


import java.io.Serializable;

public class PyOptionModel implements Serializable {
    private static final long serialVersionUID = -9800000001L;
    private transient String optionTag;

    public OptionCore _optionCore;
    public OptionDataDescriptor _OptionDataDescriptor;

    public OptionGreeks _optionGreeks;


    public PyOptionModel(OptionCore optionCore, OptionDataDescriptor optionDataDescriptor, OptionGreeks optionGreeks, String symbol)
    {
        this._optionCore= optionCore;
        this._OptionDataDescriptor = optionDataDescriptor;
        this._optionGreeks = optionGreeks;
        this.optionTag = symbol+"_"+ optionDataDescriptor.getOptionShortStr();
    }



    public static String makeTag(String symbol, String shortName)
    {
        return String.format("%s_%s", symbol, shortName);
    }

    public String getOptionTag()
    {
        return optionTag;
    }
  
    
    public int getExpiration()
    {
        int expiration= _OptionDataDescriptor.getExpirationInt();
        return expiration;
    }

    public boolean isCall()
    {
        return _OptionDataDescriptor.is_call();
    }

    public double getStrike()
    {
        double strike= _OptionDataDescriptor.getStrikeDouble();
        return strike;
    }

    public double getAsk()
    {
        double ask= (_optionCore.ask== TagLayer.NaNInteger || _optionCore.ask== TagLayer.CircuitBreakInteger) ? 0.0: _optionCore.getAskDouble();
        return ask;
    }
   
    public double getBid()
    {
        double bid= (_optionCore.bid== TagLayer.NaNInteger || _optionCore.bid== TagLayer.CircuitBreakInteger ) ? 0.0: _optionCore.getBidDouble();
        return bid;
    }

    

    public double getLast()
    {
        double last=(_optionCore.last== TagLayer.NaNInteger || _optionCore.last == TagLayer.CircuitBreakInteger) ? 0.0: _optionCore.getLastDouble();
        return last;
    }

  
    public double getBlackscholes_mid_delta()
    {
        if(_optionGreeks == null) return 0.0;
        double delta= (_optionGreeks.Delta== TagLayer.NaNInteger || _optionGreeks.Delta == TagLayer.CircuitBreakInteger )? 0.0: _optionGreeks.getDeltaDouble();
        return delta;
    }


    public double getBlackscholes_mid_gamma()
    {

        double gamma= (_optionGreeks.Gamma== TagLayer.NaNInteger)? 0.0: _optionGreeks.getGammaDouble();
        return gamma;
    }



    public double getBlackscholes_mid_iv()
    {
        double iv= (_optionGreeks.IV== TagLayer.NaNInteger)? 0.0: _optionGreeks.getIVDouble();
        return iv;
    }



    public double getBlackscholes_mid_theta()
    {
        double theta= (_optionGreeks.Theta== TagLayer.NaNInteger)? 0.0: _optionGreeks.getThetaDouble();

        return theta;
    }


    public double getBlackscholes_mid_vega()
    {
        double vega= (_optionGreeks.Vega== TagLayer.NaNInteger)? 0.0: _optionGreeks.getVegaDouble();
        return vega;
    }


    public double getDelta()
    {
        double delta= (_optionGreeks.Delta== TagLayer.NaNInteger || _optionGreeks.Delta == TagLayer.CircuitBreakInteger)? 0.0: _optionGreeks.getDeltaDouble();
        return delta;
    }
}

