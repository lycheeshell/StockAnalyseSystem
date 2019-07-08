package controller;

import entity.UserEntity;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.UserService;
import controller.Helper.UserSession;

import java.rmi.RemoteException;

/**
 * Created by lienming on 2017/6/3.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService ;

    /** 注册 */
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    @ResponseBody
    public boolean register(@RequestBody JSONObject jsonObject) throws RemoteException {
        String userName = jsonObject.getString("userName");
        String password = jsonObject.getString("password");
       return userService.register( userName, password);
    }

    /** 用户登录
     *  假设在web已经检查过输入合法
     * */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public String login(@RequestBody JSONObject jsonObject) throws RemoteException {

        //System.out.print("test");
        String userName = jsonObject.getString("userName");
        String password = jsonObject.getString("password");
        //System.out.print("login:"+userName);
        //System.out.print(password);
        if(! userService.checkIfNameExist(userName) ) {
            //System.out.println("^^11");
            return "NOT_EXIST";
        }
        UserEntity user = new UserEntity();
        user.setName(userName);
        user.setPassword(password);

        boolean isValid = userService.checkIfValid(user) ;

        if(isValid) {
            boolean msg = UserSession.online(user);
            if( true == msg ) {
                //System.out.println("^^22");
                return "SUCCESS";
            }
            //System.out.println("^^33");
            return "FAIL" ;
        }
        //System.out.println("^^44");
        return "FAIL" ;
    }


    /** 用户登出 */
    @RequestMapping(value = "/logout",method = RequestMethod.POST)
    @ResponseBody
    public boolean logout(String userName) {
        //System.out.println("logout:" + userName);
        boolean result=UserSession.offline(userName) ;

        return  result ;

    }

    /** 用户信息更新，实际只有更新密码 */
    @RequestMapping(value = "/updateInfo",method = RequestMethod.POST)
    @ResponseBody
    public boolean updateInfo(@RequestBody JSONObject jsonObject) throws RemoteException {
        String userName = jsonObject.getString("userName");
        String password = jsonObject.getString("password");
        return userService.updateInfo(password,userName);
    }

    /** 查看个人信息，实际只有密码 */
    @RequestMapping(value = "/getPersonInfo",method = RequestMethod.GET)
    @ResponseBody
    public String getPersonInfo(String userName) throws RemoteException {
        return userService.getPersonInfo(userName);
    }

}