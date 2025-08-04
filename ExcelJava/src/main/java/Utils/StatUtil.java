package Utils;

import TagFramework.Metadata.IntTagGroup;
import TagFramework.Model.Bar;
import TagFramework.Model.OptionCore;
import TagFramework.Model.OptionGreeks;
import TagFramework.TagLayer.*;
import org.apache.log4j.Logger;

import java.util.*;

public class StatUtil {
    private static Logger logger = Logger.getLogger(StatUtil.class);

    //state data
    public static void calculateIntStat(IntTagPackage intTagPackage, TreeMap<String, String> statInfo)
    {
        List<Double> intValues=new ArrayList<>();
        for(int i=0;i<intTagPackage._intArray.length;i++)
        {
            for(int j=0;j<intTagPackage._intArray[i].length;j++)
            {
                if(Math.abs(intTagPackage._intArray[i][j])!= TagLayer.NaNInteger&&Math.abs(intTagPackage._intArray[i][j])<= TagLayer.CircuitBreakInteger-10)
                    intValues.add(Utils.ConvertIntToDoubleByRatio( intTagPackage._intArray[i][j], IntTagGroup.dataRatio,7));
            }
        }
        ((IntTagGroup)intTagPackage._tagGroup).mean= Calculator.calculateAverage(intValues);
        ((IntTagGroup)intTagPackage._tagGroup).SD= Math.sqrt(Calculator.getVariance(intValues));
        Collections.sort(intValues);
        String key = intTagPackage._tagGroup.symbolname + ", ," + intTagPackage._tagGroup.TagNameB.split("_")[0];
        if(!intValues.isEmpty()) {
            double low = intValues.get(0);
            double high = intValues.get(intValues.size() - 1);
            String lowHigh = low + ","+ high;
            statInfo.put(key,lowHigh);
            //System.out.println(intTagPackage._tagGroup.symbolname + "_" + intTagPackage._tagGroup.TagNameB + " low:" + low + ", " + " high : " + high);
        }else{
            System.out.println( key+ " values are all NAN or other exceptions");
        }
    }

    public static void calculateCoreStat(OptionCoreTagPackage coreTagPackagePackage, TreeMap<String, String> statInfo) {
        TreeSet<Integer> openInterestValues = new TreeSet<>();
        TreeSet<Integer> volumeValues = new TreeSet<>();
        for (String block_name : coreTagPackagePackage._data.keySet()) {
            OptionCore[][][] dataArray = coreTagPackagePackage._data.get(block_name);
            for (int i = 0; i < dataArray.length; i++) {
                for (int j = 0; j < dataArray[i].length; j++) {
                    for (int k = 0; k < dataArray[i][j].length; k++) {
                        if (dataArray[i][j][k] != null) {
                            OptionCore optionCore = dataArray[i][j][k];
                            if (optionCore.open_interest != TagLayer.NaNInteger && optionCore.open_interest <= TagLayer.CircuitBreakInteger - 10) {
                                openInterestValues.add(optionCore.open_interest);
                            }
                            if (optionCore.volume != TagLayer.NaNInteger && optionCore.volume != TagLayer.CircuitBreakInteger) {
                                volumeValues.add(optionCore.volume);
                            }
                        }
                    }
                }
            }
        }
        if (!openInterestValues.isEmpty()) {
            int low = openInterestValues.first();
            int high = openInterestValues.last();
            String key = coreTagPackagePackage._tagGroup.symbolname + ",OC,openInterest";
            String lowHigh = low + ","+ high;
            statInfo.put(key,lowHigh);
            //System.out.println(coreTagPackagePackage._tagGroup.symbolname + "_" + coreTagPackagePackage._tagGroup.TagNameB + " openInterestValues low:" + low + ", " + " high : " + high);
        } else {
            System.out.println(coreTagPackagePackage._tagGroup.symbolname + "_" + coreTagPackagePackage._tagGroup.TagNameB + " openInterestValues are all NAN or other exceptions");
        }
        if (!volumeValues.isEmpty()) {
            int low = volumeValues.first();
            int high = volumeValues.last();
            String key = coreTagPackagePackage._tagGroup.symbolname +",OC,volume";
            String lowHigh = low + ","+ high;
            statInfo.put(key,lowHigh);
            //System.out.println(coreTagPackagePackage._tagGroup.symbolname + "_" + coreTagPackagePackage._tagGroup.TagNameB + " volumeValues low:" + low + ", " + " high : " + high);
        }
        else {
            System.out.println(coreTagPackagePackage._tagGroup.symbolname + "_" + coreTagPackagePackage._tagGroup.TagNameB + " volumeValues are all NAN or other exceptions");
        }
    }

    public static void calculateGreeksStat(OptionGreeksTagPackage greeksTagPackage, TreeMap<String, String> statInfo) {
        TreeSet<Double> deltaValues = new TreeSet<>();
        TreeSet<Double> gammaValues = new TreeSet<>();
        TreeSet<Double> ivValues = new TreeSet<>();
        TreeSet<Double> thetaValues = new TreeSet<>();
        TreeSet<Double> vegaValues = new TreeSet<>();
        for (String block_name : greeksTagPackage._greeks.keySet()) {
            OptionGreeks[][][] dataArray = greeksTagPackage._greeks.get(block_name);
            for (OptionGreeks[][] greeks : dataArray) {
                for (OptionGreeks[] greek : greeks) {
                    for (OptionGreeks value : greek) {
                        if (value != null) {
                            if (Math.abs(value.Delta) != TagLayer.NaNInteger && Math.abs(value.Delta) <= TagLayer.CircuitBreakInteger - 10) {
                                deltaValues.add(value.getDeltaDouble());
                            }
                            if (Math.abs(value.Gamma) != TagLayer.NaNInteger && Math.abs(value.Gamma) <= TagLayer.CircuitBreakInteger - 10) {
                                gammaValues.add(value.getGammaDouble());
                            }
                            if (Math.abs(value.Theta) != TagLayer.NaNInteger && Math.abs(value.Theta) <= TagLayer.CircuitBreakInteger - 10) {
                                thetaValues.add(value.getThetaDouble());
                            }
                            if (Math.abs(value.IV) != TagLayer.NaNInteger && Math.abs(value.IV) <= TagLayer.CircuitBreakInteger - 10) {
                                ivValues.add(value.getIVDouble());
                            }
                            if (Math.abs(value.Vega) != TagLayer.NaNInteger && Math.abs(value.Vega) <= TagLayer.CircuitBreakInteger - 10) {
                                vegaValues.add(value.getVegaDouble());
                            }
                        }
                    }
                }
            }
        }
        if (!deltaValues.isEmpty()) {
            double low = deltaValues.first();
            double high = deltaValues.last();
            String key = greeksTagPackage._tagGroup.symbolname +  ",OG,delta";
            String lowHigh = low +","+ high;
            statInfo.put(key,lowHigh);
            //System.out.println(greeksTagPackage._tagGroup.symbolname + "_" + greeksTagPackage._tagGroup.TagNameB + " deltaValues low:" + low + ", " + " high : " + high);
        } else {
            String key = greeksTagPackage._tagGroup.symbolname + "_" + greeksTagPackage._tagGroup.TagNameB + ",delta";
            System.out.println(key+ " Values are all NAN or other exceptions");
        }
        if (!gammaValues.isEmpty()) {
            double low = gammaValues.first();
            double high = gammaValues.last();
            String key = greeksTagPackage._tagGroup.symbolname + ",OG,gamma";
            String lowHigh = low +","+ high;
            statInfo.put(key,lowHigh);
            //System.out.println(greeksTagPackage._tagGroup.symbolname + "_" + greeksTagPackage._tagGroup.TagNameB + " gammaValues low:" + low + ", " + " high : " + high);
        } else {
            System.out.println(greeksTagPackage._tagGroup.symbolname + "_" + greeksTagPackage._tagGroup.TagNameB + " gammaValues are all NAN or other exceptions");
        }
        if (!thetaValues.isEmpty()) {
            double low = thetaValues.first();
            double high = thetaValues.last();
            String key = greeksTagPackage._tagGroup.symbolname + ",OG,theta";
            String lowHigh = low +","+ high;
            statInfo.put(key,lowHigh);
            //System.out.println(greeksTagPackage._tagGroup.symbolname + "_" + greeksTagPackage._tagGroup.TagNameB + " thetaValues low:" + low + ", " + " high : " + high);
        } else {
            System.out.println(greeksTagPackage._tagGroup.symbolname + "_" + greeksTagPackage._tagGroup.TagNameB + " thetaValues are all NAN or other exceptions");
        }
        if (!ivValues.isEmpty()) {
            double low = ivValues.first();
            double high = ivValues.last();
            String key = greeksTagPackage._tagGroup.symbolname +",OG,iv";
            String lowHigh = low +","+ high;
            statInfo.put(key,lowHigh);
            //System.out.println(greeksTagPackage._tagGroup.symbolname + "_" + greeksTagPackage._tagGroup.TagNameB + " ivValues low:" + low + ", " + " high : " + high);
        } else {
            System.out.println(greeksTagPackage._tagGroup.symbolname + "_" + greeksTagPackage._tagGroup.TagNameB + " ivValues are all NAN or other exceptions");
        }
        if (!vegaValues.isEmpty()) {
            double low = vegaValues.first();
            double high = vegaValues.last();
            String key = greeksTagPackage._tagGroup.symbolname  + ",OG,vega";
            String lowHigh = low +","+ high;
            statInfo.put(key,lowHigh);
            //System.out.println(greeksTagPackage._tagGroup.symbolname + "_" + greeksTagPackage._tagGroup.TagNameB + " vegaValues low:" + low + ", " + " high : " + high);
        }else {
            System.out.println(greeksTagPackage._tagGroup.symbolname + "_" + greeksTagPackage._tagGroup.TagNameB + " vegaValues are all NAN or other exceptions");
        }
    }

    public static void calculateBarStat(BarTagPackage barPackage, TreeMap<String, String> statInfo)
    {
        TreeSet<Double> openValues = new TreeSet<>();
        TreeSet<Double> closeValues = new TreeSet<>();
        TreeSet<Double> highValues = new TreeSet<>();
        TreeSet<Double> lowValues = new TreeSet<>();
        TreeSet<Integer> volumeValues = new TreeSet<>();
        for(Bar[] bars:barPackage._underlying) {
            for (Bar bar : bars) {
                if (bar != null) {
                    if (bar.open != TagLayer.NaNInteger && bar.open <= TagLayer.CircuitBreakInteger-10) {
                        openValues.add(bar.getOpenDouble());
                    }
                    if (bar.close != TagLayer.NaNInteger && bar.close <= TagLayer.CircuitBreakInteger-10) {
                        closeValues.add(bar.getCloseDouble());
                    }
                    if (bar.high != TagLayer.NaNInteger && bar.high <= TagLayer.CircuitBreakInteger-10) {
                        highValues.add(bar.getHighDouble());
                    }
                    if (bar.low != TagLayer.NaNInteger && bar.low <= TagLayer.CircuitBreakInteger-10) {
                        lowValues.add(bar.getLowDouble());
                    }
                    if (bar.volume != TagLayer.NaNInteger && bar.volume == TagLayer.CircuitBreakInteger) {
                        volumeValues.add(bar.volume);
                    }
                }
            }
        }
        if(!openValues.isEmpty()) {
            double low = openValues.first();
            double high = openValues.last();
            String key = barPackage._tagGroup.symbolname + ",Bar,open";
            String lowHigh = low +","+ high;
            statInfo.put(key,lowHigh);
            //System.out.println(barPackage._tagGroup.symbolname + "_" + barPackage._tagGroup.TagNameB + " openValues low:" + low + ", " + " high : " + high);
        }else{
            System.out.println(barPackage._tagGroup.symbolname + "_" + barPackage._tagGroup.TagNameB + " openValues are all NaN or exceptions" );
        }
        if(!closeValues.isEmpty()) {
            double low = closeValues.first();
            double high = closeValues.last();
            String key = barPackage._tagGroup.symbolname + ",Bar,open";
            String lowHigh = low +","+ high;
            statInfo.put(key,lowHigh);
            //System.out.println(barPackage._tagGroup.symbolname + "_" + barPackage._tagGroup.TagNameB + " closeValues low:" + low + ", " + " high : " + high);
        }else{
            System.out.println(barPackage._tagGroup.symbolname + "_" + barPackage._tagGroup.TagNameB + " closeValues are all NaN or exceptions");
        }
        if(!highValues.isEmpty()) {
            double low = highValues.first();
            double high = highValues.last();
            String key = barPackage._tagGroup.symbolname + ",Bar,high";
            String lowHigh = low +","+ high;
            statInfo.put(key,lowHigh);
            //System.out.println(barPackage._tagGroup.symbolname + "_" + barPackage._tagGroup.TagNameB + " highValues low:" + low + ", " + " high : " + high);
        }else {
            System.out.println(barPackage._tagGroup.symbolname + "_" + barPackage._tagGroup.TagNameB + " highValues are all NaN or exceptions");
        }
        if(!lowValues.isEmpty()) {
            double low = lowValues.first();
            double high = lowValues.last();
            String key = barPackage._tagGroup.symbolname + ",Bar,low";
            String lowHigh = low +","+ high;
            statInfo.put(key,lowHigh);
            //System.out.println(barPackage._tagGroup.symbolname + "_" + barPackage._tagGroup.TagNameB + " lowValues low:" + low + ", " + " high : " + high);
        }else {
            //System.out.println(barPackage._tagGroup.symbolname + "_" + barPackage._tagGroup.TagNameB + " lowValues are all NaN or exceptions");
        }
        if(!volumeValues.isEmpty()){
            int lowVolume = volumeValues.first();
            int highVolume = volumeValues.last();
            String key = barPackage._tagGroup.symbolname + ",Bar,volume";
            String lowHigh = lowVolume +","+ highVolume;
            statInfo.put(key,lowHigh);
            //System.out.println(barPackage._tagGroup.symbolname +"_" +  barPackage._tagGroup.TagNameB + " volumeValues low:" + lowVolume + ", " + " high : " + highVolume);
        }else {
            System.out.println(barPackage._tagGroup.symbolname + "_" + barPackage._tagGroup.TagNameB + " volumeValues are all NaN or exceptions");
        }
    }

    public static void StatPackages(TreeMap<String, String> statInfo) {
        //state data
        logger.info("====OptionCorePackages Keys:");
        for(String corePackageKey:TagLayer.getInstance()._OptionCorePackages.keySet()){
            logger.info(corePackageKey);
            logger.info("====OptionCorePackages Keys2:");
            for(String key2:TagLayer.getInstance()._OptionCorePackages.get(corePackageKey).keySet()){
                logger.info("   " + key2);
                OptionCoreTagPackage corePackage = TagLayer.getInstance()._OptionCorePackages.get(corePackageKey).get(key2);
                StatUtil.calculateCoreStat(corePackage,statInfo);
            }
        }
        logger.info("====BarPackages Keys:");
        for(String barPackageKey:TagLayer.getInstance()._barPackages.keySet()){
            logger.info(barPackageKey);
            logger.info("====BarPackages Keys2:");
            for(String key2:TagLayer.getInstance()._barPackages.get(barPackageKey).keySet()){
                logger.info("   " + key2);
                BarTagPackage barPackage = TagLayer.getInstance()._barPackages.get(barPackageKey).get(key2);
                StatUtil.calculateBarStat(barPackage,statInfo);
            }
        }
        logger.info("====OptionGreeksPackages Keys:");
        for(String greeksPackageKey:TagLayer.getInstance()._OptionGreeksPackages.keySet()){
            logger.info(greeksPackageKey);
            logger.info("====OptionGreeksPackages Keys2:");
            for(String key2:TagLayer.getInstance()._OptionGreeksPackages.get(greeksPackageKey).keySet()){
                logger.info("   " + key2);
                OptionGreeksTagPackage optionGreeksTagPackage = TagLayer.getInstance()._OptionGreeksPackages.get(greeksPackageKey).get(key2);
                StatUtil.calculateGreeksStat(optionGreeksTagPackage,statInfo);
            }
        }
        logger.info("====IntPackages Keys:");
        for(String intPackageKey:TagLayer.getInstance()._IntPackages.keySet()){
            logger.info(intPackageKey);
            logger.info("====IntPackages Keys2:");
            for(String key2:TagLayer.getInstance()._IntPackages.get(intPackageKey).keySet()){
                logger.info("   " + key2);
                IntTagPackage intTagPackage = TagLayer.getInstance()._IntPackages.get(intPackageKey).get(key2);
                StatUtil.calculateIntStat(intTagPackage,statInfo);
            }
        }
        StringBuilder sb = new StringBuilder();
        logger.info("===========statInfo:");
        for(String key:statInfo.keySet()){
            String row = key+","+statInfo.get(key);
            logger.info("---"+row+"---");
            sb.append(row);
            sb.append("\n");
        }
        sb.deleteCharAt(sb.lastIndexOf("\n"));
        Utils.WriteToFile("statInfo/dataStatInfo.csv",sb.toString());
    }
}
