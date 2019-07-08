package controller.Helper;

import entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lienming on 2017/6/4.
 * 记录已登录的用户
 */
public class UserSession {

    private static List<String> list = new ArrayList<>();

    public static boolean online(UserEntity user){
        String userName = user.getName() ;
        for(String unit : list )
        {
            if(userName.equals(unit)) {  //已登录
                System.out.println("^^already");
                return false;
            }
        }
        System.out.println("^^if cont " + list.contains(userName));
        return list.add(userName) ;
    }

    public static boolean offline(String userName){
        boolean op = false ;
        for(String unit : list) {
            if(userName.equals(unit) )
            {
                op = true; break ;
            }
        }
        if(op) {
            for(String s : list ) {
               System.out.println("& "+ s );
            }
            System.out.println("try out " + list.remove(userName));
            for(String s : list ) {
                System.out.println("& "+ s );
            }
        }
        return true ;
    }

//    public static void main(String[] args) {
//    //test
//        UserEntity ue = new UserEntity() ;
//        ue.setName("mumu");
//
//        System.out.println(UserSession.online(ue));
//
//        System.out.println(UserSession.online(ue));
//
//        System.out.println(UserSession.offline("mumu"));
//
//        System.out.println(UserSession.online(ue));
//
//        System.out.println(UserSession.offline("mumu"));
//
//        System.out.println(UserSession.online(ue));
//    }
}
