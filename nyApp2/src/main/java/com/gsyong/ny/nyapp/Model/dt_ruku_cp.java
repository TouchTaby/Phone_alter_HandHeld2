package com.gsyong.ny.nyapp.Model;

/**
 * Created by administrator on 2018-01-17.
 */

public class dt_ruku_cp {
    public String  _uid; //GUID 32位唯一字符串 不带符号，字母小写  如：077a2585e54044b1bbfd112be6371f24
    public String _listuid;//关联入库单的GUID
    public String _daima;//产品的流向码 非限制农药 保存流向码前11位，限制农药保存完整流向码
    public String _cpuid;//已无效
    public int _ylint1=0;//入库数量 限制农药只能为1
    public int _ylint2=0;//预留
    public int _ylint3=0;//预留
    public String _ylstr1 = "";//预留
    public String _ylstr2 = "";//预留
    public String _ylstr3 = "";//预留
}
