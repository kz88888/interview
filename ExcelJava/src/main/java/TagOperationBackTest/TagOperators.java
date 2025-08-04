package TagOperationBackTest;

import Config.LoadDataPackageConfig;
import Config.SystemConfig;
import TagFramework.TagLayer.TagLayer;
import org.apache.log4j.Logger;

import java.util.*;

public class TagOperators
{
    public static Map<String, TagOperationBackTest> _tagOperationBackTests;
    private  static Logger logger = Logger.getLogger(TagOperators.class);

    public static void createInstance(String symbol, String typeTokenBase, String tagNameOC, String tagNameBar, String tagNameOG)
    {
        if(_tagOperationBackTests==null)
            _tagOperationBackTests = new HashMap<>();
        _tagOperationBackTests.put(symbol,new TagOperationBackTest(symbol, typeTokenBase, tagNameOC, tagNameBar, tagNameOG));
    }

    public static TagOperationBackTest getInstance(String symbol)
    {
        if(_tagOperationBackTests.containsKey(symbol)) {
            return _tagOperationBackTests.get(symbol);
        }else{
            logger.error("Can not find the TagOperationBackTest of "+ symbol);
            return null;
        }
    }

    public static boolean isNaN(double doubleNum)
    {
        if(Math.abs(doubleNum- TagLayer.NaNDouble)<1e-6)
            return true;
        else
            return false;
    }

    public static void CreateTagOperators(SystemConfig systemConfig) {
        String tagNameOC = null;
        String tagNameBAR = null;
        String tagNameOG = null;

        String sourceOC = null;
        String frequencyOC = null;

        for (String symbol : systemConfig.getTagFrameworkConfig()._symbolList) {
            for (LoadDataPackageConfig loadDataPackageConfig : systemConfig.getTagFrameworkConfig()._Load_dataPackageConfigList) {
                List<String> packageSymbols = new ArrayList<>();
                if (loadDataPackageConfig._symbols.contains(",")) {
                    String[] symbolsArray = loadDataPackageConfig._symbols.split(",");
                    packageSymbols.addAll(Arrays.asList(symbolsArray));
                }else{
                    packageSymbols.add(loadDataPackageConfig._symbols);
                }
                if (loadDataPackageConfig._isDefault && packageSymbols.contains(symbol)) {
                    if (loadDataPackageConfig._tagName.toUpperCase().contains("OC")) {
                        tagNameOC = loadDataPackageConfig._tagName;
                        String[] tagNameBOCStrs = tagNameOC.substring(tagNameOC.lastIndexOf("_") + 1).split("\\.");
                        sourceOC = tagNameBOCStrs[0];
                        frequencyOC = tagNameBOCStrs[1];
                    }

                    if (loadDataPackageConfig._tagName.toUpperCase().contains("BAR")) {
                        tagNameBAR = loadDataPackageConfig._tagName;
                    }

                    if (loadDataPackageConfig._tagName.toUpperCase().contains("OG")) {
                        tagNameOG = loadDataPackageConfig._tagName;
                    }

                }
            }
            String typeTokenBase = sourceOC + "." + frequencyOC;
            if (typeTokenBase == null || tagNameOC == null || tagNameBAR == null || tagNameOG == null) {
                logger.error("taken error!");
            }
            TagOperators.createInstance(symbol, typeTokenBase, tagNameOC, tagNameBAR, tagNameOG);
            logger.info("TagOperators create: " + symbol + "_" + typeTokenBase + "_" + tagNameOC + "_" + tagNameBAR + "_" + tagNameOG);
        }
    }
}
