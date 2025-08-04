package Config;

import Module.Broker.BrokerConfig;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class SystemConfig {
    private int port;
    private Date startDate;
    private Date endDate;
    private String dataPath;
    private String userOutputPath;
    private int loadDaysOnce;
    private boolean preLoad;
    private Set<String> preLoadSymbols;
    private SortedSet<Integer> preLoadYears;
    private String underlyingModelBlockString;
    private String debug;

    private HashMap<String, MarketInfo> marketInfoMap;
    private BrokerConfig brokerConfig;
    private CommissionConfig commissionConfig;
    private TagFrameworkConfig tagFrameworkConfig;

    private int blockSize;

    private static SystemConfig instance = null;

    private static Logger logger = Logger.getLogger(SystemConfig.class);

    private SystemConfig() {
        preLoadSymbols = new HashSet<>();
        preLoadYears = new TreeSet<>();
    }

    public static SystemConfig getInstance()
    {
        if (instance == null) {
            instance = new SystemConfig();
            instance.load();
        }
        return instance;
    }

    public void load() {
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read("system_config.xml");
            Element root = doc.getRootElement();
            Node nodeSimulationConfig = root.selectSingleNode("/SystemConfig/SimulationConfig");
            dataPath = nodeSimulationConfig.selectSingleNode("DataPath").getText();
            userOutputPath = nodeSimulationConfig.selectSingleNode("UserOutputPath").getText();
            debug = nodeSimulationConfig.selectSingleNode("debug").getText();

            Node nodeBackTestingPeroid = nodeSimulationConfig.selectSingleNode("BackTestingPeroid");
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            Element elementBackTestingPeroid = (Element)nodeBackTestingPeroid;
            startDate = formatter.parse(elementBackTestingPeroid.attributeValue("start"));
            endDate = formatter.parse(elementBackTestingPeroid.attributeValue("end"));

            Node nodeSoftwareConfig = root.selectSingleNode("/SystemConfig/SoftwareConfig");
            port = Integer.valueOf(nodeSoftwareConfig.selectSingleNode("Port").getText());
            loadDaysOnce = Integer.valueOf(nodeSoftwareConfig.selectSingleNode("LoadDaysOnce").getText());
            blockSize = Integer.valueOf(nodeSoftwareConfig.selectSingleNode("BlockSize").getText());
            Node nodeYears = nodeSoftwareConfig.selectSingleNode("PreLoads/Years");
            String[] proLoadYearsStrList = nodeYears.getText().split(",");
            for(String proLoadYearsStr : proLoadYearsStrList) {
                if(proLoadYearsStr.toUpperCase().equals("ALL")) {
                    preLoadYears.clear();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(startDate);
                    int startYear = cal.get(Calendar.YEAR);
                    cal.setTime(endDate);
                    int endYear = cal.get(Calendar.YEAR);
                    for(int year = startYear; year <= endYear; year++) {
                        preLoadYears.add(year);
                    }
                    break;
                }
                preLoadYears.add(Integer.valueOf(proLoadYearsStr.trim()));
            }
            Node nodeSymbols = nodeSoftwareConfig.selectSingleNode("PreLoads/Symbols");
            String[] proLoadSymbolsStrList = nodeSymbols.getText().split(",");
            for(String proLoadSymbolsStr : proLoadSymbolsStrList) {
                preLoadSymbols.add(proLoadSymbolsStr.toUpperCase());
            }

            Node nodeRiskConfig = root.selectSingleNode("/SystemConfig/RiskConfig");
            long initialCash = Long.valueOf(nodeRiskConfig.selectSingleNode("InitialCash").getText());
            int positionSizeLimit = Integer.valueOf(nodeRiskConfig.selectSingleNode("PositionSizeLimit").getText());
            int longExposureMax = Integer.valueOf(nodeRiskConfig.selectSingleNode("LongExposureMax").getText());
            int shortExposureMax = Integer.valueOf(nodeRiskConfig.selectSingleNode("ShortExposureMax").getText());
            double deltaDollarLimit=Integer.valueOf(nodeRiskConfig.selectSingleNode("DeltaDollarLimit").getText());
            double vegaLimit=Integer.valueOf(nodeRiskConfig.selectSingleNode("VegaLimit").getText());
            double thetaLimit=Integer.valueOf(nodeRiskConfig.selectSingleNode("ThetaLimit").getText());

            brokerConfig = new BrokerConfig(initialCash, positionSizeLimit, longExposureMax, shortExposureMax, deltaDollarLimit,vegaLimit,thetaLimit);

            marketInfoMap = new HashMap<>();
            Node nodeMarketsConfig = root.selectSingleNode("/SystemConfig/Markets");
            for(Node marketNode:nodeMarketsConfig.selectNodes("Market")){
                String marketName = ((Element)marketNode).attribute("value").getText();
//                Node defaultBenchMarkNode = marketNode.selectSingleNode("DefaultBenchMark");
//                String defaultBenchMark = "";
//                if(defaultBenchMarkNode != null) {
//                    defaultBenchMark = ((Element)defaultBenchMarkNode).getText();
//                }

                Node openTimeNode = marketNode.selectSingleNode("OpenTime");
                int openTime=0;
                if(openTimeNode != null) {
                    openTime = Integer.valueOf(((Element)openTimeNode).getText());
                }

                Node closeTimeNode = marketNode.selectSingleNode("CloseTime");
                int closeTime=0;
                if(closeTimeNode != null) {
                    closeTime = Integer.valueOf(((Element)closeTimeNode).getText());
                }

                Node loadStrikeRatioNode = marketNode.selectSingleNode("LoadStrikeRatio");
                int loadStrikeRatio=0;
                if(loadStrikeRatioNode != null) {
                    loadStrikeRatio = Integer.valueOf(((Element)loadStrikeRatioNode).getText());
                }
                Node underlyingRatioNode = marketNode.selectSingleNode("UnderlyingRatio");
                int underlyingRatio=0;
                if(underlyingRatioNode != null) {
                    underlyingRatio = Integer.valueOf(((Element)underlyingRatioNode).getText());
                }
                Node intRatioNode = marketNode.selectSingleNode("IntRatio");
                int intRatio=0;
                if(intRatioNode != null) {
                    intRatio = Integer.valueOf(((Element)intRatioNode).getText());
                }
                MarketInfo marketInfo = new MarketInfo(openTime,closeTime,loadStrikeRatio,underlyingRatio,intRatio);
                marketInfoMap.put(marketName,marketInfo);
            }

            dataPath = nodeSimulationConfig.selectSingleNode("DataPath").getText();
            userOutputPath = nodeSimulationConfig.selectSingleNode("UserOutputPath").getText();
            debug = nodeSimulationConfig.selectSingleNode("debug").getText();

            Node nodeCommissionConfig = doc.selectSingleNode("/SystemConfig/CommissionConfig");
            double stockPerShare = Double.valueOf(nodeCommissionConfig.selectSingleNode("StockPerShare").getText());
            double stockPerTrade = Double.valueOf(nodeCommissionConfig.selectSingleNode("StockPerTrade").getText());
            //20200114新增ETF（stock）交易费率
            double stockTransactionFeeRate = Double.valueOf(nodeCommissionConfig.selectSingleNode("stockTransactionFeeRate").getText());
            double optionPerContract = Double.valueOf(nodeCommissionConfig.selectSingleNode("OptionPerContract").getText());
            double optionPerTrade = Double.valueOf(nodeCommissionConfig.selectSingleNode("OptionPerTrade").getText());
            double optionExercisePerTrade = Double.valueOf(nodeCommissionConfig.selectSingleNode("OptionExercisePerTrade").getText());
            double optionExercisePerContract = Double.valueOf(nodeCommissionConfig.selectSingleNode("OptionExercisePerContract").getText());
            double optionAssignmentPerTrade = Double.valueOf(nodeCommissionConfig.selectSingleNode("OptionAssignmentPerTrade").getText());
            double optionAssignmentPerContract = Double.valueOf(nodeCommissionConfig.selectSingleNode("OptionAssignmentPerContract").getText());
            int exerciseMargin = Integer.valueOf(nodeCommissionConfig.selectSingleNode("ExerciseMargin").getText());
            int contractMultiplier = Integer.valueOf(nodeCommissionConfig.selectSingleNode("ContractMultiplier").getText());
            commissionConfig = new CommissionConfig(stockPerShare,
                    stockPerTrade,
                    stockTransactionFeeRate,
                    optionPerContract,
                    optionPerTrade,
                    optionExercisePerTrade,
                    optionExercisePerContract,
                    optionAssignmentPerTrade,
                    optionAssignmentPerContract,
                    contractMultiplier,
                    exerciseMargin);

            String TFDataPath = root.selectSingleNode("/SystemConfig/TagFrameworkConfig/DataPath").getText();
            boolean statData = root.selectSingleNode("/SystemConfig/TagFrameworkConfig/StatData").getText().equalsIgnoreCase("TRUE");
//            Node symbolNode = root.selectSingleNode("/SystemConfig/TagFrameworkConfig/symbols");
//            String[] symbolListStrings=symbolNode.getText().split(",");
//            List<String> symbolList=new ArrayList<String>();
//            if(symbolListStrings.length==0)
//            {
//                logger.error("Symbols node in TagFrameworkConfig is wrong, please check it!");
//            }
//            else
//            {
//                if(symbolListStrings.length ==1 && symbolListStrings[0].equals("*"))
//                {
//                    File folder = new File(TFDataPath);
//                    File[] subFolders = folder.listFiles();
//                    for (File subFolder : subFolders)
//                    {
//                        symbolList.add(subFolder.getName());
//                    }
//                }
//                else {
//                    for (String symbol : symbolListStrings) {
//                        symbolList.add(symbol);
//                    }
//                }
//            }

            List<Node> DataPackagesNodeList=root.selectSingleNode("/SystemConfig/TagFrameworkConfig/DataPackages").selectNodes("DataPackage");
            List<LoadDataPackageConfig> loadDataPackageConfigs =new ArrayList<LoadDataPackageConfig>();
            for (Node DataPackagesNode:DataPackagesNodeList)
            {
                String type = DataPackagesNode.selectSingleNode("type").getText();
                String symbol = DataPackagesNode.selectSingleNode("symbol").getText();
                boolean isDefault = DataPackagesNode.selectSingleNode("default").getText().toUpperCase().equals("TRUE")? true:false;
                String path = DataPackagesNode.selectSingleNode("path").getText();
                String tagName = DataPackagesNode.selectSingleNode("tagName").getText();
                String classStr= DataPackagesNode.selectSingleNode("class").getText();
                String methodStr= DataPackagesNode.selectSingleNode("method").getText();
                LoadDataPackageConfig loadDataPackageConfig = new LoadDataPackageConfig(type, symbol, path, tagName, classStr, methodStr, isDefault);
                loadDataPackageConfigs.add(loadDataPackageConfig);
            }

            tagFrameworkConfig=new TagFrameworkConfig(TFDataPath, statData, loadDataPackageConfigs);
            
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return port;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getDataPath() {
        return dataPath;
    }

    public String getUserOutputPath() {
        return userOutputPath;
    }

    public int getLoadDaysOnce() {
        return loadDaysOnce;
    }

    public String getDebug() {
        return debug;
    }

    public boolean isPreLoad() {
        return !preLoadYears.isEmpty();
    }

    public Set<String> getPreLoadSymbols() {
        return preLoadSymbols;
    }

    public Set<Integer> getPreLoadYears() {
        return preLoadYears;
    }

    public String getUnderlyingModelBlockString() {
        return underlyingModelBlockString;
    }

    public BrokerConfig getBrokerConfig() {
        return brokerConfig;
    }

    public CommissionConfig getCommissionConfig() {
        return commissionConfig;
    }

    public TagFrameworkConfig getTagFrameworkConfig() {
        return tagFrameworkConfig;
    }

    public int getBlockSize() { return blockSize; }

    public MarketInfo getMarketInfo(String market) {
        if(marketInfoMap.containsKey(market)) {
            return marketInfoMap.get(market);
        }else {
            logger.error("Can't find " + market +"in system_config.\n Please double check the market config in yours config files!");
            return null;
        }
    }
}
