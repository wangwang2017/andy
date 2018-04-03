package com.yuyuehao.andy.utils;

import com.google.gson.JsonParser;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Wang
 * on 2017-09-27
 */

public class Verify {

    public static boolean isJson(String json){
        boolean isjson = true;
        try {
            new JsonParser().parse(json).getAsJsonObject();//解析json
        }catch(Exception e){
            isjson = false;
        }
        return isjson;
    }


    public static boolean isAddress(String addr)
    {
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))
        {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";

        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(addr);

        boolean is = mat.matches();
        return is;
    }

    public static boolean isIP(String port){
        if (port.equals("")){
            return false;
        }

        String rexp = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d):([0-9]{0,5}$)";

        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(port);

        boolean is = mat.matches();

        return is;
    }

    public static boolean isAlphanumeric(String pwd){
        if (pwd.equals("")){
            return false;
        }
        String rexp ="^[0-9a-zA-Z_-]{1,}$";
        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(pwd);

        boolean is = mat.matches();

        return is;
    }

    public static boolean isAlpha(String name){
        if (name.equals("")){
            return false;
        }
        String rexp ="^[a-zA-Z_-]{1,}$";
        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(name);

        boolean is = mat.matches();

        return is;
    }

    public static boolean isAngle(String pwd){
        if (pwd.equals("")){
            return false;
        }
        if (pwd.equals("0") || pwd.equals("90") || pwd.equals("180") || pwd.equals("270")){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isName(String name){
        if (name.equals("")){
            return false;
        }
        String rexp ="[\\u4e00-\\u9fa5]{1,}";
        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(name);

        boolean is = mat.matches();

        return is;
    }

    public static boolean isEcode(String name){
        if (name.equals("")){
            return false;
        }
        String rexp ="^[\\w\\d_-]*$";
        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(name);

        boolean is = mat.find();

        return is;
    }

    public static boolean isDate(String name){
        if (name.equals("")){
            return false;
        }
        String rexp ="^((20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d)$";
        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(name);

        boolean is = mat.matches();

        return is;
    }

    public static boolean isOneDigit(String name) {
        if (name.equals("")){
            return false;
        }
        return name.matches("[0-9]{1}");
    }


    public static boolean isPort(String name) {
        if (name.equals("") && name.length() <4){
            return false;
        }
        String rexp ="^([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]{1}|6553[0-5])$";
        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(name);

        boolean is = mat.matches();

        return is;
    }

    public static boolean isDigit(String name) {
        if (name.equals("")){
            return false;
        }
        return name.matches("[0-9]{1,}");
    }

    public static boolean isEmail(String name){
        if (name.equals("")){
            return false;
        }
        String rexp ="\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(name);

        boolean is = mat.matches();

        return is;
    }

    public static boolean isImage(String string) {
        if (string.equals("")) {
            return false;
        }
        if (string.toLowerCase().endsWith(".webp") || string.toLowerCase().endsWith(".jpg") || string.toLowerCase().endsWith(".png") || string.toLowerCase().endsWith(".bnp")) {
            if (new File(string).exists()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    public static boolean isFileName(String fileName){
        if (fileName.equals("")){
            return false;

        }
        if (fileName.contains("\\") || fileName.contains("/")
                || fileName.contains("*")  || fileName.contains(":")
                || fileName.contains("?") || fileName.contains("<")
                || fileName.contains(">") || fileName.contains("|")){

            return false;
        }else{
            return true;
        }
    }

    public static boolean isDomain(String name){
        if (name.equals("")){
            return false;
        }
        String pattern = "[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(name);
        boolean is = m.matches();
        return is;
    }
}
