package com.yuyuehao.andy.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 日志工具
 * 
 * @author Lqc
 */
public class LogUtils {
  private static String DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+"yyh"+ File.separator+"log"+ File.separator+"data";

  // private static String FILE_PATH = "";
  public final static String LEVEL_VERBOSE ="verbose";

  public final static String LEVEL_DEBUG   ="debug";

  public final static String LEVEL_INFO    ="info";

  public final static String LEVEL_WARN    ="warn";

  public final static String LEVEL_ERROR   ="error";

  public static String getException(Throwable e) {
    StringWriter sw=new StringWriter();
    PrintWriter pw=new PrintWriter(sw);
    e.printStackTrace(pw);
    return sw.toString();
  }

  public static String getCurrentTime() {
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return simpleDateFormat.format(new Date());
  }

  /**
   * 默认以info等级且不换行写入日志
   * 
   * @param tag
   * @param throwable
   */
  public static void write(String tag, Throwable throwable) {
    write(tag,LEVEL_INFO,throwable,false);
  }

  /**
   * 默认以info等级且不换行写入日志
   * 
   * @param tag
   * @param throwable
   */
  public static void write(String tag, String throwable) {
    write(tag,LEVEL_INFO,throwable,false);
  }

  /**
   * 默认不换行写入日志
   * 
   * @param tag
   * @param level
   * @param throwable
   */
  public static void write(String tag, String level, String throwable) {
    write(tag,level,throwable,false);
  }

  /**
   * 默认不换行写入日志
   * 
   * @param tag
   * @param level
   * @param throwable
   */
  public static void write(String tag, String level, Throwable throwable) {
    write(tag,level,throwable,false);
  }

  /**
   * 默认以info等级写入日志
   * 
   * @param tag
   * @param throwable
   * @param seq
   */
  public static void write(String tag, Throwable throwable, boolean seq) {
    write(tag,LEVEL_INFO,throwable,seq);
  }

  /**
   * 默认以info等级写入日志
   * 
   * @param tag
   * @param throwable
   * @param seq
   */
  public static void write(String tag, String throwable, boolean seq) {
    write(tag,LEVEL_INFO,throwable,seq);
  }

  /**
   * 将异常信息写入log文件中
   * 
   * @param tag 类TAG
   * @param level log等级分别为verbose,debug，info，warn，error
   * @param throwable 异常Throwable对象
   * @param seq 是否换行
   */
  public static void write(String tag, String level, Throwable throwable, boolean seq) {
    write(  tag,   level, getException(throwable) ,   seq);
  }
  
  /**
   * 将异常信息写入log文件中
   * 
   * @param tag 类TAG
   * @param level log等级分别为verbose,debug，info，warn，error
   * @param seq 是否换行
   */
  public static void write(String tag, String level, Object o, boolean seq) {
    write(  tag,   level, o==null?"":o. toString(),   seq);
  }

  /**
   * 将异常信息写入log文件中
   *
   * @param level log等级分别为verbose,debug，info，warn，error
   * @param throwable 异常Throwable对象的字符串
   * @param seq 是否换行
   */
  @SuppressWarnings("rawtypes")
  public static void write(Class cls, String level, String throwable, boolean seq) {
    write(cls.getClass().getName(),level,throwable,seq);
  }

  /**
   * 将异常信息写入log文件中
   * 
   * @param tag 类TAG
   * @param level log等级分别为verbose,debug，info，warn，error
   * @param seq 是否换行
   */
  public static void write(String tag, String level, String str, boolean seq) {
    String s=str;
    if(LEVEL_ERROR.equalsIgnoreCase(level))
      Log.e(tag,s);
    if(LEVEL_WARN.equalsIgnoreCase(level))
      Log.w(tag,s);
    else//f(LEVEL_INFO.equalsIgnoreCase(level))
      Log.i(tag,s);
    
    
    if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) { return; }
    File file=new File(DIR);
    if(!file.exists()) {
      file.mkdirs();
    }
    initLog();
    FileWriter fw=null;
    try {
      fw=new FileWriter(DIR + "/canise-log" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".txt",true);
      if(seq) {
        fw.write("\n\r-------------------------------\r\n");
      }
      fw.write("[" + tag + "]" + getCurrentTime() + "(" + level + "):" + str + "\r\n");
      fw.flush();
      fw.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    finally {
      fw=null;
      file=null;
    }
  }

  private static void initLog() {
    File file=new File(DIR + "/canise-log" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".txt");
    Calendar last= Calendar.getInstance();
    last.setTimeInMillis(file.lastModified());
    Calendar now= Calendar.getInstance();
    // System.out.println(last.get(Calendar.YEAR) + "-"
    // + last.get(Calendar.MONTH) + "-" + last.get(Calendar.DATE));
    // System.out.println(now.get(Calendar.YEAR) + "-"
    // + now.get(Calendar.MONTH) + "-" + now.get(Calendar.DATE));
    if(last.get(Calendar.YEAR) < now.get(Calendar.YEAR) && last.get(Calendar.MONTH) < now.get(Calendar.MONTH)
      && last.get(Calendar.DATE) < now.get(Calendar.DATE)) {
      file.setLastModified(System.currentTimeMillis());
      System.out.println("log.txt reload");
      FileWriter fw=null;
      try {
        fw=new FileWriter(DIR + "/canise-log" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".txt",false);
        fw.write("");
        fw.flush();
        fw.close();
        file.setLastModified(System.currentTimeMillis());
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      finally {
        fw=null;
        file=null;
      }
    }
  }

  /**
   * 清理日志
   */
  public static void clearlog(int preday){
    try{
      File _path=new File(DIR);
      if(_path.exists()&&_path.isDirectory()){
        Calendar c= Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, preday*-1);
        File[] _files=_path.listFiles();
        for(File _f:_files){
          if(_f.lastModified()<c.getTimeInMillis()){
            System.out.println("删除"+preday+"天之前的app监控日志！");
            _f.delete();
          }
        }
      }
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
