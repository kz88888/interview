package TagOperationBackTest;

import Model.PyDataModel.PyDataModel;
import Model.PyDataModel.PyOptionModel;

import Model.TimeLine;
import Module.Broker.DataDirection;
import TagFramework.Metadata.*;
import TagFramework.Model.Bar;
import TagFramework.Model.OptionCore;
import TagFramework.Model.OptionGreeks;
import TagFramework.TagLayer.*;
import Utils.TimeLineUtil;
import Utils.Utils;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class TagOperationBackTest
{
    private String _OGPackageTagName;
    public String _BarPackageTagName;
    public String _symbol;

    public  String _typeTokenBase;
    public String _subSymbol;
    public TimeLine _timeLine;
    public String _OCPackageTagName;


    public TagOperationBackTest(String symbol, String typeTokenBase, String tagNameOC, String tagNameBar, String tagNameOG)
    {
       this._symbol =symbol;
       if(tagNameOG!=null)
           this._subSymbol = tagNameOG.split("_")[0].split("\\.")[1]; //form *.Mid2_PVT.M.OG  get 'Mid2'
       else
           this._subSymbol = "";
       this._typeTokenBase = typeTokenBase;
       this._BarPackageTagName = symbol+"_"+tagNameBar;
       this._OCPackageTagName = symbol+"_"+tagNameOC;
       this._OGPackageTagName = symbol+"_"+tagNameOG;
       MakeBackTestTimeLine();
    }

    private void MakeBackTestTimeLine()
    {
        TreeMap<Integer, List<Integer>> timeLineIS=GetAllTimeLinesInterSection();
        for(int nDate:timeLineIS.keySet())
        {
            if(_timeLine==null)
                _timeLine=new TimeLine();
            if (!_timeLine.contains(nDate))
            {
                _timeLine.addTimestamp(nDate);
                _timeLine.newChild(nDate);
            }
            for(int nTime:timeLineIS.get(nDate))
            {
                _timeLine.getChild(nDate).addTimestamp(nTime);
            }
        }
    }

    private TreeMap<Integer, List<Integer>> GetAllTimeLinesInterSection()
    {
        TreeMap<Integer, List<Integer>> rtnTimeLine=new TreeMap<Integer, List<Integer>>();
        if(TagLayer.getInstance()._OptionCorePackages.get(_symbol).size()==1)
        {
            TreeMap<String, OptionCoreTagPackage> optionCorePackageMap = TagLayer.getInstance()._OptionCorePackages.get(_symbol);
            rtnTimeLine=optionCorePackageMap.get(optionCorePackageMap.firstKey())._tagGroup.timeLine;
        }
        else
        {
            logger.error("There are no or more than one OptionCore Packages of "+ _symbol);
            return rtnTimeLine;
        }

        for(BarTagPackage barPackage:TagLayer.getInstance()._barPackages.get(_symbol).values())
        {
            rtnTimeLine= TimeLineUtil.InterSectionTimeLine(rtnTimeLine,barPackage._tagGroup.timeLine);
        }

        if(!TagLayer.getInstance()._OptionGreeksPackages.containsKey(_symbol))
            return rtnTimeLine;
        if(TagLayer.getInstance()._OptionGreeksPackages.get(_symbol).size()==0)
            return rtnTimeLine;
        for(OptionGreeksTagPackage optionGreeksTagPackage:TagLayer.getInstance()._OptionGreeksPackages.get(_symbol).values())
        {
            rtnTimeLine= TimeLineUtil.InterSectionTimeLine(rtnTimeLine,optionGreeksTagPackage._tagGroup.timeLine);
        }

        if(TagLayer.getInstance()._IntPackages==null)
            return rtnTimeLine;
        if(TagLayer.getInstance()._IntPackages.size()==0||!TagLayer.getInstance()._IntPackages.containsKey(_symbol))
            return rtnTimeLine;
        for(IntTagPackage intTagPackage:TagLayer.getInstance()._IntPackages.get(_symbol).values())
        {
            rtnTimeLine= TimeLineUtil.InterSectionTimeLine(rtnTimeLine,intTagPackage._tagGroup.timeLine);
        }
        return rtnTimeLine;
    }

    private  static Logger logger = Logger.getLogger(TagOperationBackTest.class);

    public  PyDataModel getPyDataModelPerCycle(long nTimestamp) throws Exception
    {
        PyDataModel pyDataModel=new PyDataModel(nTimestamp, _symbol);
        int nDate= (int) (nTimestamp/1000000L);
        OptionCoreTagGroup optionCoretagGroup = getOptionCoreTagGroup();
        OptionCore[] opitonCorePerCycle = optionCoretagGroup.getOptionCoreByDateTime(nTimestamp);
        if(opitonCorePerCycle==null)
            return null;
        for(int optIndex=0;optIndex<opitonCorePerCycle.length;optIndex++)
        {
            OptionCore optionCore=opitonCorePerCycle[optIndex];

            OptionDataDescriptor optionCoreDescriptor = (OptionDataDescriptor) optionCoretagGroup.GetTagDescriptorByOptIndex(nDate, optIndex);
            if(optionCoreDescriptor ==null)
            {
                logger.error("tagDescriptorOC is null @"+nDate+",optIndex:"+optIndex);
                return null;
            }
            //String tagNameBOG=TagLayer.getInstance().getOptionGreeksTagGroup().TagNameB;
            String tagNameAOG= optionCoreDescriptor.TagNameA;
            OptionGreeks optionGreeks=GetOGDataPoint(nTimestamp, tagNameAOG);
            if(optionGreeks==null)//todo:not start option should be null
            {
                logger.error("optionGreeks is null"+ tagNameAOG+"@"+nTimestamp);
                //return null;
            }
            PyOptionModel pyOptionModel = new PyOptionModel(optionCore, (OptionDataDescriptor) optionCoreDescriptor,optionGreeks, _symbol);
            pyDataModel.addOption(pyOptionModel);
        }

        Bar barMin= GetBarDataPoint(nTimestamp);
        pyDataModel._bar=barMin;
        return pyDataModel;
    }

    public  Bar GetBarDataPoint(long nTimestamp)
    {
        int nDate=(int) (nTimestamp / 1000000L);
        int nTime=(int) (nTimestamp % 1000000L);
        try {
            Bar barMin = GetUnderlyingBarPerCycle(nDate, nTime);
            return barMin;
        }catch (Exception e) {
            return null;
        }
    }


    public  OptionGreeks GetOGDataPoint(long datetime,String tagNameBase)
    {
        OptionGreeksTagPackage optionGreeksTagPackage = (OptionGreeksTagPackage) TagLayer.getInstance().GetPackage(_OGPackageTagName);
        OptionGreeksTagGroup tagGroup = (OptionGreeksTagGroup) optionGreeksTagPackage._tagGroup;
        int nDate=(int) (datetime / 1000000L);
        int nTime=(int) (datetime % 1000000L);
        String blockName= tagGroup._blockNameMap.get(nDate);
        DerivedDescriptor  tagDescriptor=(DerivedDescriptor) GroupDescriptorLink.GetTagDescriptorByTagNameA(tagGroup.optionTagMetaTree, tagNameBase);
        if(tagDescriptor!=null)
        {
            if (tagDescriptor.IndexPaths != null && tagDescriptor.IndexPaths.containsKey(blockName))
            {
                int optionIndex = tagDescriptor.IndexPaths.get(blockName);
                return tagGroup._OptionGreeksMap.get(datetime)[optionIndex];
            }
        }

        logger.error(tagNameBase+"."+_subSymbol + " of " + nDate + "_" + nTime + "can't find in OG");
        return null;
    }


    public  double GetDataPoint(long nDateTime, String intType)
    {
        String tagName=_symbol+"_"+intType.toUpperCase()+"_"+ _typeTokenBase;
        IntTagPackage intTagPackage = (IntTagPackage) TagLayer.getInstance().GetPackage(tagName);
        if(intTagPackage==null){
            logger.error("Getting IntTagPackage failed with tagName: " + tagName);
        }
        int[][] data = intTagPackage._intArray;
        TreeMap<Integer, List<Integer>> timeLine = intTagPackage._tagGroup.timeLine;

        int dataRatio=IntTagGroup.dataRatio;

        if(!data.equals(new int [0][])&&timeLine!=null&&dataRatio!=0)
        {
            int nDate = (int) (nDateTime / 1000000L);
            int nTime = (int) (nDateTime % 1000000L);
            int dateIndex = TimeLineUtil.getDateIndexFromTimeLine(nDate, timeLine);
            int timeIndex = TimeLineUtil.getTimeIndex(nDate, nTime, timeLine);
            if(dateIndex==-1||timeIndex==-1)//can't find in timeLine
                return 0.0;
            int dataInt = data[dateIndex][timeIndex];
            double dataDouble = Utils.ConvertIntToDoubleByRatio(dataInt,dataRatio,7);
            return dataDouble;
        }
        return 0.0;
    }

    public  PyOptionModel getPyOptionModelByTimeName(long datetime, String name)
    {
        String shortName=name.split("_")[1];
        int nDate=(int) (datetime / 1000000L);
        OptionCoreTagGroup optionCoretagGroup = getOptionCoreTagGroup();
        String blockName=optionCoretagGroup._blockNameMap.get(nDate);
        OptionDataDescriptor tagDescriptorOC=(OptionDataDescriptor)optionCoretagGroup.GetTagDescriptorByShortNameNew(nDate,shortName);
        OptionCore optionCore=null;
        if(tagDescriptorOC==null)
        {
            return null;
        }
        else
        {
            if (tagDescriptorOC.IndexPaths != null && tagDescriptorOC.IndexPaths.containsKey(blockName))
            {
                int optionIndex = tagDescriptorOC.IndexPaths.get(blockName);
                optionCore=optionCoretagGroup.getOptionCoreByDateTime(datetime)[optionIndex];
            }
        }
        if(optionCore==null)
        {
            return null;
        }
        String tagNameAOG= tagDescriptorOC.getTagNameA();
        OptionGreeks optionGreeks=GetOGDataPoint(datetime, tagNameAOG);
        if(optionGreeks==null)
        {
            logger.error("optionGreeks is null"+ tagNameAOG+"@"+datetime);
            return null;
        }
        String symbol=name.split("_")[0];
        PyOptionModel pyOptionModel = new PyOptionModel(optionCore, tagDescriptorOC,optionGreeks,symbol);
        return pyOptionModel;
    }


    public  TreeMap<String, OptionCore[][][]>GetTagData()
    {
        OptionCoreTagPackage optionCoreTagPackage=(OptionCoreTagPackage) TagLayer.getInstance().GetPackage(_OCPackageTagName);
        if(optionCoreTagPackage!=null)
            return optionCoreTagPackage._data;
        else
            logger.error("Cann't get the data ："+ _OCPackageTagName);
        return null;
    }

    public  double GetUnderlyingClose(int nDate, int nTime)
    {
        Bar barMin= GetUnderlyingBarPerCycle(nDate,nTime);
        double underlyingClose=barMin.getCloseDouble();
        return underlyingClose;
    }

    public  int GetUnderlyingCloseInt(int nDate, int nTime)
    {
        Bar barMin= GetUnderlyingBarPerCycle(nDate,nTime);
        return barMin.close;
    }

    public  OptionCoreTagGroup getOptionCoreTagGroup()
    {
        OptionCoreTagPackage optionCoreTagPackage=(OptionCoreTagPackage) TagLayer.getInstance().GetPackage(_OCPackageTagName);
        if(optionCoreTagPackage!=null)
            return (OptionCoreTagGroup) optionCoreTagPackage._tagGroup;
        logger.error("Cann't get the OptionCoreTagGroup ："+ _OCPackageTagName);
        return null;
    }

    public  OptionGreeksTagGroup getOptionGreeksTagGroup()
    {
        OptionGreeksTagPackage optionGreeksTagPackage=(OptionGreeksTagPackage) TagLayer.getInstance().GetPackage(_OGPackageTagName);
        if(optionGreeksTagPackage!=null)
            return (OptionGreeksTagGroup) optionGreeksTagPackage._tagGroup;
        logger.error("Cann't get the OptionGreeksTagGroup ："+ _OGPackageTagName);
        return null;
    }

    public OptionCore GetOptionDataPointByTagDescriptor(long datetime, OptionDataDescriptor tagDescriptor)
    {
        OptionCoreTagGroup tagGroup = getOptionCoreTagGroup();
        int nDate=(int) (datetime / 1000000L);
        String blockName=tagGroup._blockNameMap.get(nDate);
        if(tagDescriptor!=null)
        {
            if (tagDescriptor.IndexPaths != null && tagDescriptor.IndexPaths.containsKey(blockName))
            {
                int optionIndex = tagDescriptor.IndexPaths.get(blockName);
                return tagGroup.getOptionCoreByDateTime(datetime)[optionIndex];
            }
        }
        return null;
    }

    public  HashMap<OptionCore,OptionDataDescriptor> getOneMinOCMap(int nDate, int nTime)
    {
        HashMap<OptionCore,OptionDataDescriptor> oneMinOCMap=new HashMap<>();
        OptionCoreTagGroup tagGroup = getOptionCoreTagGroup();
        long nDateTime=nDate*1000000L+nTime;
        HashMap<String, int[]> dateTimeIndexMap = tagGroup.getOptionCoreDateTimeIndex(nDateTime);
        if(dateTimeIndexMap==null)//can't find indexMap in tagGroup
        {
            logger.error(nDate + "_" + nTime + " can't find in OC tagGroup");
            return null;
        }
        String blockName=(String) dateTimeIndexMap.keySet().toArray()[0];
        int dateIndexInBlock= dateTimeIndexMap.get(blockName)[0];
        int timeIndex= dateTimeIndexMap.get(blockName)[1];
        TreeMap<String,OptionCore[][][]> data = TagOperation.GetTagData(_OCPackageTagName);
        OptionCore[] opitonCoreMin = data.get(blockName)[dateIndexInBlock][timeIndex];
        for(int optIndex=0;optIndex<opitonCoreMin.length;optIndex++)
        {
            OptionDataDescriptor tagDescriptor = (OptionDataDescriptor) tagGroup.GetTagDescriptorByOptIndex(nDate, optIndex);
            if(tagDescriptor!=null&&opitonCoreMin[optIndex]!=null)
                oneMinOCMap.put(opitonCoreMin[optIndex],tagDescriptor);
        }
        return oneMinOCMap;
    }

    public  HashMap<OptionCore,OptionDataDescriptor> GetOneMinOCMapNew(int nDate, int nTime)
    {
        HashMap<OptionCore,OptionDataDescriptor> oneMinOCMap=new HashMap<>();
        OptionCoreTagGroup tagGroup = getOptionCoreTagGroup();
        long nDateTime=nDate*1000000L+nTime;
        OptionCore[] opitonCoreMin = tagGroup.getOptionCoreByDateTime(nDateTime);
        for(int optIndex=0;optIndex<opitonCoreMin.length;optIndex++)
        {
            OptionDataDescriptor tagDescriptor =  (OptionDataDescriptor) tagGroup.GetTagDescriptorByOptIndex(nDate, optIndex);
            if(tagDescriptor!=null&&opitonCoreMin[optIndex]!=null)
                oneMinOCMap.put(opitonCoreMin[optIndex],tagDescriptor);
        }
        return oneMinOCMap;
    }



    public  BarTagGroup getUnderlyingTagGroup()
    {
        BarTagPackage barTagPackage=(BarTagPackage) TagLayer.getInstance().GetPackage(_BarPackageTagName);
        if(barTagPackage!=null)
        {
            return (BarTagGroup) barTagPackage._tagGroup;
        }
        logger.error("Can't get the UnderlyingTagGroup ："+ _BarPackageTagName);
        return null;
    }

    public  Bar[][] get_underlying()
    {
        BarTagPackage barTagPackage=(BarTagPackage) TagLayer.getInstance().GetPackage(_BarPackageTagName);
        if(barTagPackage!=null)
        {
            return barTagPackage._underlying;
        }
        logger.error("Cann't get the Underlying Bars："+"_"+ _BarPackageTagName);
        return null;
    }



    public  Bar GetUnderlyingBarPerCycle(int nDate, int nTime)
    {
        TreeMap<Integer, List<Integer>> timeLine = getUnderlyingTagGroup().getTimeLine();
        int dateIndex= TagFramework.Utils.TimeLineUtil.getDateIndexFromTimeLine(nDate,timeLine);
        int timeIndex= TagFramework.Utils.TimeLineUtil.getTimeIndex(nDate,nTime,timeLine);
        Bar[][] underlying = get_underlying();
        Bar bar=underlying[dateIndex][timeIndex];
        return bar;
    }


    public OptionGreeks GetOGDataPointWithDataDirection(long timeStamp, DataDirection direction, String tagNameBase)
    {
        OptionGreeks og=null;
        TreeMap<Integer, List<Integer>> timeLineOG = getOptionGreeksTagGroup().timeLine;
        int nDate=(int) (timeStamp/1000000L);
        if(direction== DataDirection.Exact)
        {
            og=GetOGDataPoint(timeStamp,tagNameBase);
            return og;
        }
        long dataAvailableTime=timeStamp;
        if(direction== DataDirection.ExactOrBefore3)
        {
            int backMins=3;
            while(dataAvailableTime>nDate*1000000L+timeLineOG.get(nDate).get(0)&&backMins>0&&GetOGDataPoint(dataAvailableTime,tagNameBase)!=null
                    &&(GetOGDataPoint(dataAvailableTime,tagNameBase).Delta==TagLayer.CircuitBreakInteger||
                        GetOGDataPoint(dataAvailableTime,tagNameBase).Delta==TagLayer.NaNInteger))
            {
                int nTimeAvailable=(int) (dataAvailableTime%1000000L);
                int dataAvailableTimeIndex=TimeLineUtil.getTimeIndex(nDate,nTimeAvailable,timeLineOG);
                dataAvailableTime = nDate*1000000L+timeLineOG.get(nDate).get(dataAvailableTimeIndex-1);
                backMins--;
            }
        }
        if(direction== DataDirection.ExactOrBefore5)
        {
            int backMins=5;
            while(dataAvailableTime>nDate*1000000L+timeLineOG.get(nDate).get(0)&&backMins>0&&GetOGDataPoint(dataAvailableTime,tagNameBase)!=null
                    &&(GetOGDataPoint(dataAvailableTime,tagNameBase).Delta==TagLayer.CircuitBreakInteger||
                    GetOGDataPoint(dataAvailableTime,tagNameBase).Delta==TagLayer.NaNInteger))
            {
                int nTimeAvailable=(int) (dataAvailableTime%1000000L);
                int dataAvailableTimeIndex=TimeLineUtil.getTimeIndex(nDate,nTimeAvailable,timeLineOG);
                dataAvailableTime = nDate*1000000L+timeLineOG.get(nDate).get(dataAvailableTimeIndex-1);
                backMins--;
            }
        }
        if(direction== DataDirection.ExactOrSameDayEarlier)
        {
            while(dataAvailableTime>nDate*1000000L+timeLineOG.get(nDate).get(0)&&GetOGDataPoint(dataAvailableTime,tagNameBase)!=null
                    &&(GetOGDataPoint(dataAvailableTime,tagNameBase).Delta==TagLayer.CircuitBreakInteger||
                    GetOGDataPoint(dataAvailableTime,tagNameBase).Delta==TagLayer.NaNInteger))
            {
                int nTimeAvailable=(int) (dataAvailableTime%1000000L);
                int dataAvailableTimeIndex=TimeLineUtil.getTimeIndex(nDate,nTimeAvailable,timeLineOG);
                dataAvailableTime = nDate*1000000L+timeLineOG.get(nDate).get(dataAvailableTimeIndex-1);
            }
        }

        if(GetOGDataPoint(dataAvailableTime,tagNameBase)!=null
                && GetOGDataPoint(dataAvailableTime,tagNameBase).Delta!=TagLayer.CircuitBreakInteger
                && GetOGDataPoint(dataAvailableTime,tagNameBase).Delta!=TagLayer.NaNInteger)
        {
            logger.debug(timeStamp+" Data CircuitBreak/is NaN, Find Last OptionGreeks@"+dataAvailableTime);
            og=GetOGDataPoint(dataAvailableTime,tagNameBase);
        }
        else
        {
            logger.debug(timeStamp+" Data CircuitBreak/is NaN, And can't find OptionGreeks after"+dataAvailableTime);
        }
        return og;
    }



    public OptionGreeks GetOGDataPointDirectionByTheta(long timeStamp, DataDirection direction, String tagNameBase)
    {

        OptionGreeks og=null;
        TreeMap<Integer, List<Integer>> timeLineOG = getOptionGreeksTagGroup().timeLine;
        int nDate=(int) (timeStamp/1000000L);
        if(direction== DataDirection.Exact)
        {
            og=GetOGDataPoint(timeStamp,tagNameBase);
            return og;
        }
        long dataAvailableTime=timeStamp;
        if(direction== DataDirection.ExactOrBefore3)
        {
            int backMins=3;
            while(dataAvailableTime>nDate*1000000L+timeLineOG.get(nDate).get(0)&&backMins>0&&GetOGDataPoint(dataAvailableTime,tagNameBase)!=null
                    &&(GetOGDataPoint(dataAvailableTime,tagNameBase).Theta==TagLayer.CircuitBreakInteger||
                    GetOGDataPoint(dataAvailableTime,tagNameBase).Theta==TagLayer.NaNInteger))
            {
                int nTimeAvailable=(int) (dataAvailableTime%1000000L);
                int dataAvailableTimeIndex=TimeLineUtil.getTimeIndex(nDate,nTimeAvailable,timeLineOG);
                dataAvailableTime = nDate*1000000L+timeLineOG.get(nDate).get(dataAvailableTimeIndex-1);
                backMins--;
            }
        }
        if(direction== DataDirection.ExactOrBefore5)
        {
            int backMins=5;
            while(dataAvailableTime>nDate*1000000L+timeLineOG.get(nDate).get(0)&&backMins>0&&GetOGDataPoint(dataAvailableTime,tagNameBase)!=null
                    &&(GetOGDataPoint(dataAvailableTime,tagNameBase).Theta==TagLayer.CircuitBreakInteger||
                    GetOGDataPoint(dataAvailableTime,tagNameBase).Theta==TagLayer.NaNInteger))
            {
                int nTimeAvailable=(int) (dataAvailableTime%1000000L);
                int dataAvailableTimeIndex=TimeLineUtil.getTimeIndex(nDate,nTimeAvailable,timeLineOG);
                dataAvailableTime = nDate*1000000L+timeLineOG.get(nDate).get(dataAvailableTimeIndex-1);
                backMins--;
            }
        }
        if(direction== DataDirection.ExactOrSameDayEarlier)
        {
            while(dataAvailableTime>nDate*1000000L+timeLineOG.get(nDate).get(0)&&GetOGDataPoint(dataAvailableTime,tagNameBase)!=null
                    &&(GetOGDataPoint(dataAvailableTime,tagNameBase).Theta==TagLayer.CircuitBreakInteger||
                    GetOGDataPoint(dataAvailableTime,tagNameBase).Theta==TagLayer.NaNInteger))
            {
                int nTimeAvailable=(int) (dataAvailableTime%1000000L);
                int dataAvailableTimeIndex=TimeLineUtil.getTimeIndex(nDate,nTimeAvailable,timeLineOG);
                dataAvailableTime = nDate*1000000L+timeLineOG.get(nDate).get(dataAvailableTimeIndex-1);
            }
        }

        if(GetOGDataPoint(dataAvailableTime,tagNameBase)!=null
                && GetOGDataPoint(dataAvailableTime,tagNameBase).Theta!=TagLayer.CircuitBreakInteger
                && GetOGDataPoint(dataAvailableTime,tagNameBase).Theta!=TagLayer.NaNInteger)
        {
            logger.debug(timeStamp+" Data CircuitBreak/is NaN, Find Last OptionGreeks@"+dataAvailableTime);
            og=GetOGDataPoint(dataAvailableTime,tagNameBase);
        }
        else
        {
            logger.debug(timeStamp+" Data CircuitBreak/is NaN, And can't find OptionGreeks after"+dataAvailableTime);
        }
        return og;
    }

    public OptionGreeks GetOGDataPointDirectionByVega(long timeStamp, DataDirection direction, String tagNameBase)
    {

        OptionGreeks og=null;
        TreeMap<Integer, List<Integer>> timeLineOG = getOptionGreeksTagGroup().timeLine;
        int nDate=(int) (timeStamp/1000000L);
        if(direction== DataDirection.Exact)
        {
            og=GetOGDataPoint(timeStamp,tagNameBase);
            return og;
        }
        long dataAvailableTime=timeStamp;
        if(direction== DataDirection.ExactOrBefore3)
        {
            int backMins=3;
            while(dataAvailableTime>nDate*1000000L+timeLineOG.get(nDate).get(0)&&backMins>0&&GetOGDataPoint(dataAvailableTime,tagNameBase)!=null
                    &&(GetOGDataPoint(dataAvailableTime,tagNameBase).Vega==TagLayer.CircuitBreakInteger||
                    GetOGDataPoint(dataAvailableTime,tagNameBase).Vega==TagLayer.NaNInteger))
            {
                int nTimeAvailable=(int) (dataAvailableTime%1000000L);
                int dataAvailableTimeIndex=TimeLineUtil.getTimeIndex(nDate,nTimeAvailable,timeLineOG);
                dataAvailableTime = nDate*1000000L+timeLineOG.get(nDate).get(dataAvailableTimeIndex-1);
                backMins--;
            }
        }
        if(direction== DataDirection.ExactOrBefore5)
        {
            int backMins=5;
            while(dataAvailableTime>nDate*1000000L+timeLineOG.get(nDate).get(0)&&backMins>0&&GetOGDataPoint(dataAvailableTime,tagNameBase)!=null
                    &&(GetOGDataPoint(dataAvailableTime,tagNameBase).Vega==TagLayer.CircuitBreakInteger||
                    GetOGDataPoint(dataAvailableTime,tagNameBase).Vega==TagLayer.NaNInteger))
            {
                int nTimeAvailable=(int) (dataAvailableTime%1000000L);
                int dataAvailableTimeIndex=TimeLineUtil.getTimeIndex(nDate,nTimeAvailable,timeLineOG);
                dataAvailableTime = nDate*1000000L+timeLineOG.get(nDate).get(dataAvailableTimeIndex-1);
                backMins--;
            }
        }
        if(direction== DataDirection.ExactOrSameDayEarlier)
        {
            while(dataAvailableTime>nDate*1000000L+timeLineOG.get(nDate).get(0)&&GetOGDataPoint(dataAvailableTime,tagNameBase)!=null
                    &&(GetOGDataPoint(dataAvailableTime,tagNameBase).Vega==TagLayer.CircuitBreakInteger||
                    GetOGDataPoint(dataAvailableTime,tagNameBase).Vega==TagLayer.NaNInteger))
            {
                int nTimeAvailable=(int) (dataAvailableTime%1000000L);
                int dataAvailableTimeIndex=TimeLineUtil.getTimeIndex(nDate,nTimeAvailable,timeLineOG);
                dataAvailableTime = nDate*1000000L+timeLineOG.get(nDate).get(dataAvailableTimeIndex-1);
            }
        }

        if(GetOGDataPoint(dataAvailableTime,tagNameBase)!=null
                && GetOGDataPoint(dataAvailableTime,tagNameBase).Vega!=TagLayer.CircuitBreakInteger
                && GetOGDataPoint(dataAvailableTime,tagNameBase).Vega!=TagLayer.NaNInteger)
        {
            logger.debug(timeStamp+" Data CircuitBreak/is NaN, Find Last OptionGreeks@"+dataAvailableTime);
            og=GetOGDataPoint(dataAvailableTime,tagNameBase);
        }
        else
        {
            logger.debug(timeStamp+" Data CircuitBreak/is NaN, And can't find OptionGreeks after"+dataAvailableTime);
        }
        return og;
    }


    public OptionCore GetOCDataPointWithDataDirection(long timeStamp, DataDirection direction, OptionDataDescriptor tagDescriptorOC)
    {
        OptionCore oc =null;
        TreeMap<Integer, List<Integer>> timeLineOC = getOptionCoreTagGroup().timeLine;
        int nDate=(int) (timeStamp/1000000L);
        if(direction== DataDirection.Exact)
        {
            oc =GetOptionDataPointByTagDescriptor(timeStamp,tagDescriptorOC);
            return oc;
        }
        long dataAvailableTime=timeStamp;
        if(direction== DataDirection.ExactOrBefore3)
        {
            int backMins=3;
            while(dataAvailableTime>nDate*1000000L+timeLineOC.get(nDate).get(0)&&backMins>0&&GetOptionDataPointByTagDescriptor(dataAvailableTime,tagDescriptorOC)!=null
                    &&(GetOptionDataPointByTagDescriptor(dataAvailableTime,tagDescriptorOC).ask==TagLayer.CircuitBreakInteger||
                    GetOptionDataPointByTagDescriptor(dataAvailableTime,tagDescriptorOC).ask==TagLayer.NaNInteger))
            {
                int nTimeAvailable=(int) (dataAvailableTime%1000000L);
                int dataAvailableTimeIndex=TimeLineUtil.getTimeIndex(nDate,nTimeAvailable,timeLineOC);
                dataAvailableTime = nDate*1000000L+timeLineOC.get(nDate).get(dataAvailableTimeIndex-1);
                backMins--;
            }
        }
        if(direction== DataDirection.ExactOrBefore5)
        {
            int backMins=5;
            while(dataAvailableTime>nDate*1000000L+timeLineOC.get(nDate).get(0)&&backMins>0&&GetOptionDataPointByTagDescriptor(dataAvailableTime,tagDescriptorOC)!=null
                    &&(GetOptionDataPointByTagDescriptor(dataAvailableTime,tagDescriptorOC).ask==TagLayer.CircuitBreakInteger||
                    GetOptionDataPointByTagDescriptor(dataAvailableTime,tagDescriptorOC).ask==TagLayer.NaNInteger))
            {
                int nTimeAvailable=(int) (dataAvailableTime%1000000L);
                int dataAvailableTimeIndex=TimeLineUtil.getTimeIndex(nDate,nTimeAvailable,timeLineOC);
                dataAvailableTime = nDate*1000000L+timeLineOC.get(nDate).get(dataAvailableTimeIndex-1);
                backMins--;
            }
        }
        if(direction== DataDirection.ExactOrSameDayEarlier)
        {
            while(dataAvailableTime>nDate*1000000L+timeLineOC.get(nDate).get(0)&&GetOptionDataPointByTagDescriptor(dataAvailableTime,tagDescriptorOC)!=null
                    &&(GetOptionDataPointByTagDescriptor(dataAvailableTime,tagDescriptorOC).ask==TagLayer.CircuitBreakInteger||
                    GetOptionDataPointByTagDescriptor(dataAvailableTime,tagDescriptorOC).ask==TagLayer.NaNInteger))
            {
                int nTimeAvailable=(int) (dataAvailableTime%1000000L);
                int dataAvailableTimeIndex=TimeLineUtil.getTimeIndex(nDate,nTimeAvailable,timeLineOC);
                dataAvailableTime = nDate*1000000L+timeLineOC.get(nDate).get(dataAvailableTimeIndex-1);
            }
        }

        if(GetOptionDataPointByTagDescriptor(dataAvailableTime,tagDescriptorOC)!=null
                &&GetOptionDataPointByTagDescriptor(dataAvailableTime,tagDescriptorOC).ask!=TagLayer.CircuitBreakInteger
                &&GetOptionDataPointByTagDescriptor(dataAvailableTime,tagDescriptorOC).ask!=TagLayer.NaNInteger)
        {
            logger.debug(timeStamp+" Data CircuitBreak/is NaN, Find Last OptionCore@"+dataAvailableTime);
            oc =GetOptionDataPointByTagDescriptor(dataAvailableTime,tagDescriptorOC);
        }
        else
        {
            logger.debug(timeStamp+" Data CircuitBreak/is NaN, And can't find OptionCore after"+dataAvailableTime);
        }
        return oc;
    }


    public SortedMap<Long, Bar> GetUnderlyingTag()
    {

        return TagQueryOperation.GetUnderlyingTag(_BarPackageTagName);
    }


    public Bar GetBarDataPointWithDirection(long timeStamp, DataDirection direction)
    {
        Bar bar =null;
        TreeMap<Integer, List<Integer>> timeLineBar = getUnderlyingTagGroup().timeLine;
        int nDate=(int) (timeStamp/1000000L);
        if(direction== DataDirection.Exact)
        {
            bar =GetBarDataPoint(timeStamp);
            return bar;
        }
        long dataAvailableTime=timeStamp;
        if(direction== DataDirection.ExactOrBefore3)
        {
            int backMins=3;
            while(dataAvailableTime>nDate*1000000L+ timeLineBar.get(nDate).get(0)&&backMins>0&&GetBarDataPoint(dataAvailableTime)!=null
                    &&(GetBarDataPoint(dataAvailableTime).close==TagLayer.CircuitBreakInteger||
                    GetBarDataPoint(dataAvailableTime).close==TagLayer.NaNInteger))
            {
                int nTimeAvailable=(int) (dataAvailableTime%1000000L);
                int dataAvailableTimeIndex=TimeLineUtil.getTimeIndex(nDate,nTimeAvailable, timeLineBar);
                dataAvailableTime = nDate*1000000L+ timeLineBar.get(nDate).get(dataAvailableTimeIndex-1);
                backMins--;
            }
        }
        if(direction== DataDirection.ExactOrBefore5)
        {
            int backMins=5;
            while(dataAvailableTime>nDate*1000000L+ timeLineBar.get(nDate).get(0)&&backMins>0&&GetBarDataPoint(dataAvailableTime)!=null
                    &&(GetBarDataPoint(dataAvailableTime).close==TagLayer.CircuitBreakInteger||
                    GetBarDataPoint(dataAvailableTime).close==TagLayer.NaNInteger))
            {
                int nTimeAvailable=(int) (dataAvailableTime%1000000L);
                int dataAvailableTimeIndex=TimeLineUtil.getTimeIndex(nDate,nTimeAvailable, timeLineBar);
                dataAvailableTime = nDate*1000000L+ timeLineBar.get(nDate).get(dataAvailableTimeIndex-1);
                backMins--;
            }
        }
        if(direction== DataDirection.ExactOrSameDayEarlier)
        {
            while(dataAvailableTime>nDate*1000000L+ timeLineBar.get(nDate).get(0)&&GetBarDataPoint(dataAvailableTime)!=null
                    &&(GetBarDataPoint(dataAvailableTime).close==TagLayer.CircuitBreakInteger||
                    GetBarDataPoint(dataAvailableTime).close==TagLayer.NaNInteger))
            {
                int nTimeAvailable=(int) (dataAvailableTime%1000000L);
                int dataAvailableTimeIndex=TimeLineUtil.getTimeIndex(nDate,nTimeAvailable, timeLineBar);
                dataAvailableTime = nDate*1000000L+ timeLineBar.get(nDate).get(dataAvailableTimeIndex-1);
            }
        }

        if(GetBarDataPoint(dataAvailableTime)!=null  &&(GetBarDataPoint(dataAvailableTime).close!=TagLayer.CircuitBreakInteger||
                GetBarDataPoint(dataAvailableTime).close!=TagLayer.NaNInteger))
        {
            logger.debug(timeStamp+" Data CircuitBreak/is NaN, Find Last Bar@"+dataAvailableTime);
            bar =GetBarDataPoint(dataAvailableTime);
        }
        else
        {
            logger.debug(timeStamp+" Data CircuitBreak/is NaN, And can't find OptionCore after"+dataAvailableTime);
        }
        return bar;
    }

    public PyDataModel getPyDataModelPerday(Integer nDate) {
        //todo:for US data for per day
        return null;
    }
}
