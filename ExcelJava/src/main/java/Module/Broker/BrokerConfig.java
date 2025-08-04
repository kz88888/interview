package Module.Broker;

public class BrokerConfig
{
    private double initialCash;
    private int positionSizeLimit;
    private int longExposureMax;
    private int shortExposureMax;
    private double deltaDollarLimit;
    private double vegaLimit;
    private double thetaLimit;

    public BrokerConfig(long initialCash, int positionSizeLimit, int longExposureMax, int shortExposureMax, double deltaDollarLimit,double vegaLimit,double thetaLimit)
    {
        this.initialCash = initialCash;
        this.positionSizeLimit = positionSizeLimit;
        this.longExposureMax = longExposureMax;
        this.shortExposureMax = shortExposureMax;
        this.deltaDollarLimit=deltaDollarLimit;
        this.vegaLimit=vegaLimit;
        this.thetaLimit=thetaLimit;
    }

    public double getInitialCash() {
        return initialCash;
    }

    public int getPositionSizeLimit() {
        return positionSizeLimit;
    }

    public int getLongExposureMax() {
        return longExposureMax;
    }

    public int getShortExposureMax() {
        return shortExposureMax;
    }


    public double getDeltaDollarLimit() {
        return deltaDollarLimit;
    }

    public double getVegaLimit() {
        return vegaLimit;
    }

    public double getThetaLimit() {
        return thetaLimit;
    }
}
