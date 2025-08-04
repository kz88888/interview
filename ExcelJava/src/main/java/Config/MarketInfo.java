package Config;

public class MarketInfo {

    public int loadStrikeRatio;
    public int underLyingRatio;

    public int intRatio;
    public int openTime;
    public int closeTime;

    public MarketInfo(int openTime, int closeTime, int loadStrikeRatio, int underLyingRatio, int intRatio) {
    this.openTime=openTime;
    this.closeTime=closeTime;
    this.loadStrikeRatio=loadStrikeRatio;
    this.underLyingRatio=underLyingRatio;
    this.intRatio=intRatio;
    }
}
