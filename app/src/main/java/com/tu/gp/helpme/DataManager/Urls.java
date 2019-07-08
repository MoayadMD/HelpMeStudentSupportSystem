package com.tu.gp.helpme.DataManager;

public class Urls
{
    final static String ROOT_URL = "http://www.itrips.hostingerapp.com/";
    static String loginFilePath = "Login.php";
    static String getDataFilePath = "HelpMe/getDataFromDB.php";
    static String insertDataFilePath = "HelpMe/InsertDataToDB.php";
    static String connectionFilePath = "HelpMe/ConnectToDB.php";

    public static String getLoginURL(String username,String password)
    {
        return ROOT_URL+loginFilePath+"?username="+username+"&password="+password;
    }
    public static String getGetDataURL(String query)
    {
        return ROOT_URL+getDataFilePath+"?query="+query;

    }
    public static String getConnectionURL()
    {
        return ROOT_URL+connectionFilePath;
    }
    public static String getInsertDataUrl(String query)
    {
        return ROOT_URL+insertDataFilePath+"?query="+query;
    }
    public static String getWhatsappURL(String number)
    {
        return "https://api.whatsapp.com/send?phone="+number.replace("+","");
    }

}
