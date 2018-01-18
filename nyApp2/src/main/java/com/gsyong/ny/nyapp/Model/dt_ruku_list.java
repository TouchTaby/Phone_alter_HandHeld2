package com.gsyong.ny.nyapp.Model;

/**
 * Created by administrator on 2018-01-16.
 */

public class dt_ruku_list {
    public String _uid; //GUID 32位唯一字符串 不带符号，字母小写  如：077a2585e54044b1bbfd112be6371f24
    public String _addtime;//创建时间
    public String _bianhao;//入库单编号 可以使用 yyyyMMddHHmmssffff 格式化日期来得到
    public int _qiyeid;   //关联的企业ID
    public int _shuliang; //入库单包含的 流向码个数
    public String _qystaff; //员工ID 只针对PC机  CE端：直接赋值 winCe  android端：直接赋值android
    public int _ylint1 = 0; //预留
    public int _ylint2 = 0;//预留
    public int _ylint3 = 0;//预留
    public int _ylint4 = 0;//预留
    public String _ylstr1 = "";//预留
    public String _ylstr2 = "";//预留
    public String _ylstr3 = "";//预留
    public String _ylstr4 = "";//预留
}
