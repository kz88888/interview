package Config;

public class CommissionConfig {
    private double stockPerShare;
    private double stockPerTrade;
    //20200114新增费率方式
    private double stockTransactionFeeRate;
    private double optionPerContract;
    private double optionPerTrade;
    private double optionExercisePerTrade;
    private double optionExercisePerContract;
    private double optionAssignmentPerTrade;
    private double optionAssignmentPerContract;
    private int contractMultiplier;
    private int exerciseMargin;

    public CommissionConfig(double stockPerShare,
                            double stockPerTrade,
                            double stockTransactionFeeRate,
                            double optionPerContract,
                            double optionPerTrade,
                            double optionExercisePerTrade,
                            double optionExercisePerContract,
                            double optionAssignmentPerTrade,
                            double optionAssignmentPerContract,
                            int contractMultiplier,int exerciseMargin) {
        this.stockPerShare = stockPerShare;
        this.stockPerTrade = stockPerTrade;
        this.stockTransactionFeeRate = stockTransactionFeeRate;
        this.optionPerContract = optionPerContract;
        this.optionPerTrade = optionPerTrade;
        this.optionExercisePerTrade = optionExercisePerTrade;
        this.optionExercisePerContract = optionExercisePerContract;
        this.optionAssignmentPerTrade = optionAssignmentPerTrade;
        this.optionAssignmentPerContract = optionAssignmentPerContract;
        this.contractMultiplier = contractMultiplier;
        this.exerciseMargin =exerciseMargin;
    }

    public double getStockPerShare() {
        return stockPerShare;
    }

    public double getStockPerTrade() {
        return stockPerTrade;
    }

    public double getStockTransactionFeeRate()
    {
        return stockTransactionFeeRate;
    }

    public double getOptionPerContract() {
        return optionPerContract;
    }

    public double getOptionPerTrade() {
        return optionPerTrade;
    }

    public double getOptionExercisePerTrade() {
        return optionExercisePerTrade;
    }

    public double getOptionExercisePerContract() {
        return optionExercisePerContract;
    }

    public double getOptionAssignmentPerTrade() {
        return optionAssignmentPerTrade;
    }

    public double getOptionAssignmentPerContract() {
        return optionAssignmentPerContract;
    }

    public int getContractMultiplier() {
        return contractMultiplier;
    }

    public int getExerciseMargin(){return exerciseMargin;}
}
