package Module.Loader;


import Config.LoadDataPackageConfig;
import Config.TagFrameworkConfig;
import TagFramework.TagLayer.*;
import Utils.StatUtil;
import Utils.Utils;
import org.apache.log4j.Logger;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class TFLoader
{
    private static TreeMap<String, String> statInfo = new TreeMap<>();
    private static Logger logger = Logger.getLogger(TFLoader.class);
    public static void LoadData(TagFrameworkConfig tagFrameworkConfig)
    {
        String rootPath=tagFrameworkConfig._DataPath;
        for(LoadDataPackageConfig loadDataPackageConfig :tagFrameworkConfig._Load_dataPackageConfigList)
        {
            if(!loadDataPackageConfig._type.toUpperCase().equals("LOAD")) continue;

            if(loadDataPackageConfig._symbols.equals("*"))
            {
                StringBuilder sb = new StringBuilder();
                //从目录读取symbols
                tagFrameworkConfig._symbolList.addAll(getSymbolsFromPath(rootPath));
                for(String symbol:tagFrameworkConfig._symbolList){
                    String dataFilesPath=rootPath+File.separator+ symbol+File.separator+ loadDataPackageConfig._path;
                    File filePath = new File(dataFilesPath);
                    if (!filePath.exists()) {
                        continue;
                    }
                    if (filePath.exists()) {
                        try {
                            sb.append(symbol).append(",");
                            LoadDataWithMetaData(dataFilesPath, loadDataPackageConfig);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                sb.deleteCharAt(sb.lastIndexOf(","));
                loadDataPackageConfig._symbols =sb.toString();
            }
            else
            {
                //单个symbol的package
                tagFrameworkConfig._symbolList.add(loadDataPackageConfig._symbols);
                String dataFilesPath=rootPath+ File.separator+loadDataPackageConfig._symbols+File.separator+ loadDataPackageConfig._path;
                File filePath = new File(dataFilesPath);
                if (!filePath.exists()) {
                    continue;
                }
                try {
                    LoadDataWithMetaData(dataFilesPath, loadDataPackageConfig);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if(!tagFrameworkConfig.statData) {
            return;
        }else{
            StatUtil.StatPackages(statInfo);
        }

    }

    private static ArrayList<String> getSymbolsFromPath(String rootPath) {
        ArrayList<String> symbolList = new ArrayList<>();
        File folder = new File(rootPath);
        File[] subFolders = folder.listFiles();
        for (File subFolder : subFolders)
        {
            if(!subFolder.isDirectory()) continue;
            if(!subFolder.getName().startsWith("."))
            {
                symbolList.add(subFolder.getName());
            }
        }
        return symbolList;
    }

    private static void LoadDataWithMetaData(String dataFilesPath, LoadDataPackageConfig loadDataPackageConfig) throws Exception
    {
        Method method = null;
        String parseClassStr = loadDataPackageConfig._class;
        String parseMethodStr = loadDataPackageConfig._method;
        method = Class.forName(parseClassStr).getMethod(parseMethodStr, String.class);
        TagPackage tagPackage=(TagPackage) method.invoke(null,dataFilesPath);
        TagLayer.getInstance().AddPackage(tagPackage);
        tagPackage.Initialize();
    }

    private static void LoadDataWithMetaDataWithDateRange(String dataFilesPath, LoadDataPackageConfig loadDataPackageConfig,int startDate,int endDate) throws Exception
    {
        Method method = null;
        String parseClassStr = loadDataPackageConfig._class;
        String parseMethodStr = loadDataPackageConfig._method;
        method = Class.forName(parseClassStr).getMethod(parseMethodStr, String.class, Integer.class, Integer.class);
        TagPackage tagPackage=(TagPackage) method.invoke(null,dataFilesPath,startDate,endDate);
        TagLayer.getInstance().AddPackage(tagPackage);
        tagPackage.Initialize();
    }

}
