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
    private TagFrameworkConfig tagFrameworkConfig;
    private static SystemConfig instance = null;

    private SystemConfig() {
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


            Node nodeSoftwareConfig = root.selectSingleNode("/SystemConfig/SoftwareConfig");
            port = Integer.valueOf(nodeSoftwareConfig.selectSingleNode("Port").getText());

            String TFDataPath = root.selectSingleNode("/SystemConfig/TagFrameworkConfig/DataPath").getText();
            boolean statData = root.selectSingleNode("/SystemConfig/TagFrameworkConfig/StatData").getText().equalsIgnoreCase("TRUE");


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
        }
    }

    public int getPort() {
        return port;
    }


    public TagFrameworkConfig getTagFrameworkConfig() {
        return tagFrameworkConfig;
    }
}
