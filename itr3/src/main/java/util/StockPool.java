package util;

/**
 * Created by lienming on 2017/6/10.
 */
public class StockPool {
    final static String MAIN_CODE_0 = "000" ;
    final static String MAIN_CODE_1 = "001" ;
    final static String GROW_CODE = "300" ;
    final static String SMALL_CODE = "002" ;


    /**根据股票代码前3位确定股票属于哪个板块：主板-000、001，中小板-002，创业板-300
     * @param code
     * @return
     */
    public static String getPool ( String code) {
        String pool = "" ;
        if( code.startsWith("002"))
            pool = "S" ;
        else if( code.startsWith("000") || code.startsWith("001"))
            pool = "M" ;
        else if( code.startsWith("300"))
            pool = "G" ;

        return pool ; //股票代码有误，返回null
    }



}
