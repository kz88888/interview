package Config;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagFrameworkConfig
{
    public String _DataPath;
    public Set<String> _symbolList = new HashSet<>();
    public List<LoadDataPackageConfig> _Load_dataPackageConfigList;

    public boolean statData;

    public TagFrameworkConfig(String TFDataPath, Boolean statData,  List<LoadDataPackageConfig> loadDataPackageConfigs)
    {
        this._DataPath=TFDataPath;
        this._Load_dataPackageConfigList = loadDataPackageConfigs;
        this.statData = statData;

    }
}
