//package Model.PyDataModel;
//
//import Model.UnderlyingModel.*;
//import TagFramework.Model.Bar;
//import TagFramework.TagLayer.TagLayer;
//import TagOperationBackTest.TagOperators;
//import org.apache.commons.beanutils.BeanUtils;
//import org.apache.commons.beanutils.BeanUtilsBean;
//
//import java.io.Serializable;
//import java.lang.reflect.InvocationTargetException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
//public class PyDataModelUS extends PyDataModel implements Serializable
//{ //Feb 19th 2023 to make Broker using new data format
//    private static final long serialVersionUID = -9000000002L;
//
//    private YhModel yh;
//    private CalcModel calc;
//    private NasdaqModel nasdaq;
//    private CpsModel cps;
//    private CompoModel compo;
//
//
//    public PyDataModelUS(long nTimestamp, String underlyingSymbol)
//    {
//        super(nTimestamp, underlyingSymbol);
//    }
//
//    @Override
//    public Object get(String parameter)
//    {
//        String uppParam = parameter.toUpperCase();
//        if(calc == null) {
//            if(uppParam.equals("VIX") || uppParam.equals("AVIX") || uppParam.equals("MAXPAIN")) {
//                return 0.0;
//            }
//        } else {
//            switch (uppParam) {
//                case "VIX":
//                    return calc.getVix();
//                case "AVIX":
//                    return calc.getVix_div_best_c();
//                case "MAXPAIN":
//                    return calc.getMaxpain();
//            }
//        }
//
//        if(compo == null) {
//            //System.out.println(String.format("Compo Null:%d,%s", date, symbol));
//            if(uppParam.equals("CLOSE") || uppParam.equals("OPEN") || uppParam.equals("HIGH") || uppParam.equals("LOW")
//                    || uppParam.equals("ADJ_CLOSE")|| uppParam.equals("ADJ_OPEN") || uppParam.equals("ADJ_HIGH") || uppParam.equals("ADJ_LOW")) {
//                return 0.0;
//            } else if(uppParam.equals("RDQ") || uppParam.equals("PDATEQ")) {
//                return 0;
//            }
//            return null;
//        }
//        if(uppParam.equals("CLOSE")) {
//            return compo.getPrccdCompDaily();
//        } else if(uppParam.equals("ADJ_CLOSE")) {
//            return (compo.getAjexdiCompDaily() > 0 ? compo.getPrccdCompDaily() / compo.getAjexdiCompDaily() : 0.0);
//        } else if(uppParam.equals("OPEN")) {
//            return compo.getPrcodCompDaily();
//        } else if(uppParam.equals("ADJ_OPEN")) {
//            return (compo.getAjexdiCompDaily() > 0 ? compo.getPrcodCompDaily() / compo.getAjexdiCompDaily() : 0.0);
//        } else if(uppParam.equals("HIGH")) {
//            return compo.getPrchdCompDaily();
//        } else if(uppParam.equals("ADJ_HIGH")) {
//            return (compo.getAjexdiCompDaily() > 0 ? compo.getPrchdCompDaily() / compo.getAjexdiCompDaily() : 0.0);
//        } else if(uppParam.equals("LOW")) {
//            return compo.getPrcldCompDaily();
//        } else if(uppParam.equals("ADJ_LOW")) {
//            return (compo.getAjexdiCompDaily() > 0 ? compo.getPrcldCompDaily() / compo.getAjexdiCompDaily() : 0.0);
//        } else if(uppParam.equals("RDQ")) {
//            return compo.getRdqCompFundQuarterly();
//        } else if(uppParam.equals("PDATEQ")) {
//            return compo.getPdateqCompFundQuarterly();
//        } else if(uppParam.equals("AJEXDI")) {
//            return compo.getAjexdiCompDaily();
//        }
//        return 0;
//    }
//
//    public void setYh(String parameterName, Object parameterValue) throws InvocationTargetException, IllegalAccessException {
//        if(yh == null) {
//            yh = new YhModel();
//        }
//        BeanUtils.setProperty(yh, parameterName,parameterValue);
//    }
//
//    public Object getYh(String parameterName) {
//        try {
//            if(yh == null) {
//                return null;
//            }
//            return BeanUtilsBean.getInstance().getPropertyUtils().getSimpleProperty(yh, parameterName.toLowerCase());
//        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public void setCalc(String parameterName, Object parameterValue) throws InvocationTargetException, IllegalAccessException {
//        if(calc == null) {
//            calc = new CalcModel();
//        }
//        BeanUtils.setProperty(calc, parameterName,parameterValue);
//    }
//
//    public Object getCalc(String parameterName) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
//        try {
//            if(calc == null) {
//                return null;
//            }
//            //return BeanUtils.getSimpleProperty(calc, parameterName);
//            return BeanUtilsBean.getInstance().getPropertyUtils().getSimpleProperty(calc, parameterName.toLowerCase());
//        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public void setNasdaq(String parameterName, Object parameterValue) throws InvocationTargetException, IllegalAccessException {
//        if(nasdaq == null) {
//            nasdaq = new NasdaqModel();
//        }
//        BeanUtils.setProperty(nasdaq, parameterName,parameterValue);
//    }
//
//    public Object getNasdaq(String parameterName) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
//        try {
//            if(nasdaq == null) {
//                return null;
//            }
//            //return BeanUtils.getSimpleProperty(nasdaq, parameterName);
//            return BeanUtilsBean.getInstance().getPropertyUtils().getSimpleProperty(nasdaq, parameterName.toLowerCase());
//        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public void setCps(String parameterName, Object parameterValue) throws InvocationTargetException, IllegalAccessException {
//        if(cps == null) {
//            cps = new CpsModel();
//        }
//        BeanUtils.setProperty(cps, parameterName,parameterValue);
//    }
//
//    public Object getCps(String parameterName) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
//        try {
//            if(cps == null) {
//                return null;
//            }
//            //return BeanUtils.getSimpleProperty(cps, parameterName);
//            return BeanUtilsBean.getInstance().getPropertyUtils().getSimpleProperty(cps, parameterName.toLowerCase());
//        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public void setCompo(String parameterName, Object parameterValue) throws InvocationTargetException, IllegalAccessException {
//        if(compo == null) {
//            compo = new CompoModel();
//        }
//        BeanUtils.setProperty(compo, parameterName,parameterValue);
//    }
//
//    public Object getCompo(String parameterName) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
//        try {
//            if(compo == null) {
//                return null;
//            }
//            return BeanUtilsBean.getInstance().getPropertyUtils().getSimpleProperty(compo, parameterName);
//        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public YhModel getYh() {
//        return yh;
//    }
//
//    public CalcModel getCalc() {
//        return calc;
//    }
//
//    public NasdaqModel getNasdaq() {
//        return nasdaq;
//    }
//
//    public CpsModel getCps() {
//        return cps;
//    }
//
//    public CompoModel getCompo() {
//        return compo;
//    }
//
//    public void setCalc(CalcModel calc) {
//        this.calc= calc;
//    }
//
//    public void setCompo(CompoModel compo) {
//        this.compo = compo;
//    }
//
//    public void setCps(CpsModel cps) {
//        this.cps = cps;
//    }
//
//    public void setNasdaq(NasdaqModel nasdaq) {
//        this.nasdaq = nasdaq;
//    }
//
//    public void setYh(YhModel yh) {
//        this.yh = yh;
//    }
//}
