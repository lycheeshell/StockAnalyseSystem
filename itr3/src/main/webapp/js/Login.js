/**
 * Created by yipeng on 2017/6/4.
 */

$(document).ready(function () {
    showvalf();

});

$(document).on(
    {
        /*为今日推荐股票进行跳转*/
        click:function () {
            var userName=document.getElementById('userName').value;
            var password=document.getElementById('userPassword').value;
            var loginVO={
                "userName":userName,
                "password":password
                };
            if(userName.length==0)
            {
                $('#emptyUser').modal();
            }
            else  if(password.length==0)
            {
                $('#emptyPass').modal();
            }
            else if(userName.indexOf("%")>=0||userName.indexOf("@")>=0||userName.indexOf('$')>=0||
                userName.indexOf('#')>=0||userName.indexOf("&")>=0||userName.indexOf("*")>=0||userName.indexOf('￥')>=0)
            {
                $('#illegalUser').modal();
            }
            else if(password.length!=6)
            {
                $('#illegalPass').modal();
            }
            else if(!(/^[0-9]+$/.test(password)))
            {
                $('#illegalPass').modal();
            }
            else //调用登录方法确认是否成功
            {

                $.ajax({
                    type:'post',
                    url:'/user/login',
                    contentType:'application/json;charset=utf-8',
                    data:JSON.stringify(loginVO),
                    success:function (data){
                        //如果登录成功
                         if(data=="SUCCESS"){
                             localStorage.setItem("userName",userName);
                            $('#Success').modal();
                            $('#Success').on('hide.bs.modal',
                                function() {
                                    window.location.href = "personal_homepage.html?userName="+userName;
                                }
                            );
                         }
                         else{
                             if(data=="NOT_EXIST"){
                                 alert("用户不存在！");
                                 $('#noneUser').modal();
                             }
                             if(data=="FAIL"){
                                 alert("登录失败！");
                                 $('#illegalPass').modal();
                             }

                         }
                    },
                    error:function (xhr) {
                        alert(xhr.status+" "+xhr.statusText);
                    }
                });
            }
        }
    },'#LogBtn'
);