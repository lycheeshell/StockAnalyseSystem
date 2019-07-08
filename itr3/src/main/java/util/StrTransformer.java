package util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lienming on 2017/6/7.
 * 实现String数组、List<String> 与字符串"a,b,c,..."之间的相互转换
 */
public class StrTransformer {
    final static String spliter = "," ;

    public static String[] StrToArray ( String str ) {
        return str.split(spliter) ;
    }

    public static ArrayList<String> StrToArrayList( String str ){
        String[] arr = StrToArray(str) ;
        ArrayList<String> list = new ArrayList<>();
        for(String param : arr) {
            list.add(param) ;
        }
        return list ;
    }

    public static String ArrayToStr ( String[] arr ) {
        int i = 0 ;
        String str = "";
        for( ; i < arr.length - 1 ; i++ ) {
            str = str + arr[i] + "," ;
        }
        str = str + arr[i] ;
        return str ;
    }

    public static String ArrayListToStr (List<String> list ) {
        int i = 0 ;
        String str = "";
        for( ; i < list.size() ; i++ ) {
            str = str + list.get(i) + "," ;
        }
        str = str + list.get(i) ;
        return str ;
    }


}
