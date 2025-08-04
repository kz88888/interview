package Config;

import java.util.List;

public class LoadDataPackageConfig
{
    public String _type;
    public boolean _isDefault;
    public String _path;
    public String _tagName;
    public String _class;
    public String _method;
    public String _symbols;

    public LoadDataPackageConfig(String type, String symbol, String path, String tagName, String classStr, String methodStr,boolean isDefault)
    {
        this._type=type;
        this._symbols=symbol;
        this._isDefault=isDefault;
        this._path=path;
        this._tagName=tagName;
        this._class=classStr;
        this._method=methodStr;
    }
}
