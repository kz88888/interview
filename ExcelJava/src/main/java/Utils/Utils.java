package Utils;

import TagFramework.TagLayer.TagLayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    public static double convertStringToDouble(String source) {
        try {
            return Double.valueOf(source);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public static int convertStringToInteger(String source) {
        try {
            return Integer.valueOf(source);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static long convertStringToLong(String source) {
        try {
            return Long.valueOf(source);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static boolean convertStringToBoolean(String source) {
        try {
            return Boolean.valueOf(source);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int convertStringToNDate(String source) {
        try {
            if(source.length() > 8) {
                source = source.substring(0, 8);
            }

            if(source.length() < 8) {
                return 0;
            }
            return Integer.valueOf(source);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static Date convertStringToDate(String source) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            if(source.length() > 8) {
                source = source.substring(0, 8);
            }

            if(source.length() < 8) {
                return null;
            }
            return formatter.parse(source);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String[] splitString(String source, String regex) {
        ArrayList<String> rtnList = new ArrayList<>();
        int currentIndex = 0;
        int lastIndex = 0;
        while(lastIndex < source.length()) {
            if (source.charAt(lastIndex) == '{') {
                int leftBracket = 0;
                currentIndex = -1;
                for(int i = lastIndex; i < source.length(); i++) {
                    if (source.charAt(i) == '{') {
                        leftBracket++;
                    }
                    if (source.charAt(i) == '}') {
                        leftBracket--;
                    }
                    if(leftBracket == 0) {
                        currentIndex = i;
                        break;
                    }
                }

                if (currentIndex == -1) {
                    break;
                }
                rtnList.add(source.substring(lastIndex, currentIndex+1));
                lastIndex = currentIndex+2;
                continue;
            }

            currentIndex = source.indexOf(regex, lastIndex);
            if(currentIndex == -1) {
                break;
            }
            rtnList.add(source.substring(lastIndex, currentIndex));
            lastIndex = currentIndex+1;
        }
        if(lastIndex <= source.length()-1) {
            rtnList.add(source.substring(lastIndex));
        }
        if(source.substring(source.length()-1).equals(",")){
            rtnList.add("");
        }

        String[] result = new String[rtnList.size()];
        return rtnList.toArray(result);
    }

    static public void main(String[] args) {
        String test = "AAL,20160129210000000,30.0,False,{sfa},{sfds:{sdfd},sae:{132}},*,20160129210000000,0.01,0.0,0.02,11,38.99,8";
        String[] qwwe = splitString(test, ",");
        String sdf= "";
    }

    public static Object convert(String typeName, String source) {
        if(typeName.compareTo("double") == 0) {
            return Utils.convertStringToDouble(source);
        } else if(typeName.compareTo("varchar") == 0 || typeName.compareTo("map") == 0) {
            return source;
        } else if(typeName.compareTo("bigint") == 0) {
            return Utils.convertStringToLong(source);
        } else if(typeName.compareTo("int") == 0) {
            return Utils.convertStringToInteger(source);
        } else if(typeName.compareTo("boolean") == 0) {
            return Utils.convertStringToBoolean(source);
        }
        return null;
    }

    public static Class<?> getClass(String typeName) {
        if(typeName.compareTo("double") == 0) {
            return Double.class;
        } else if(typeName.compareTo("varchar") == 0 || typeName.compareTo("map") == 0) {
            return String.class;
        } else if(typeName.compareTo("bigint") == 0) {
            return Long.class;
        } else if(typeName.compareTo("int") == 0) {
            return Integer.class;
        } else if(typeName.compareTo("boolean") == 0) {
            return Boolean.class;
        }
        return null;
    }

    public static String Sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public static void WriteToFile(String filePath, String context) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            if(!file.exists()){
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file, true);
            fw.write(context);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double formatDouble(Double d, int scale) {
        if(d == null || d.isNaN()) {
            return Double.NaN;
        }
        BigDecimal bg = new BigDecimal(d).setScale(scale, RoundingMode.UP);
        return bg.doubleValue();
    }

    public static double calculateCNPrice(double last, double ask, double bid) {
        if(last > 0) {
            if(bid > 0 && ask > 0) {
                if(bid <= last && last <= ask) {
                    return last;
                } else {
                    return (ask + bid) / 2.0;
                }
            } else if(bid > 0) {
                return bid >= last? bid : last;
            } else if(ask > 0) {
                return ask <= last? ask : last;
            }
            return last;
        } else {
            if(bid > 0 && ask > 0) {
                return (ask + bid) / 2.0;
            } else if(bid > 0) {
                return bid;
            } else if(ask > 0) {
                return ask;
            }
        }
        return 0;
    }


    public static double ConvertIntToDoubleByRatio2(int intSource,int ratio,int newScale)
    {
        if(intSource!= TagLayer.NaNInteger)
        {
            double doubleValue=(new BigDecimal(intSource).divide(new BigDecimal(ratio))).setScale(newScale, RoundingMode.HALF_UP).doubleValue();
            return doubleValue;
        }
        return  TagLayer.NaNDouble;
    }
    public static double ConvertIntToDoubleByRatio(int intSource,int ratio,int newScale)
    {
        if(intSource!= TagLayer.NaNInteger)
        {
            double doubleValue= ((double)(intSource*1.0))/((double)ratio*1.0);
            return doubleValue;
        }
        return  TagLayer.NaNDouble;
    }

    public static int convertDouleStringToIntByRatio(String doubleStr, int scale, int dataRatio)
    {
        int intValue=(new BigDecimal(doubleStr).setScale(scale, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(String.valueOf(dataRatio)))).intValue();
        return intValue;
    }



}
