
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

            var registerVO={"userName":userName,"password":password};
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
                $('#illegalLen').modal();
            }
            else if(!(/^[0-9]+$/.test(password)))
            {
                $('#illegalPass').modal();
            }
            else //请求注册方法
            {

                $.ajax({
                    type:'post',
                    url:'/user/register',
                    contentType:'application/json;charset=utf-8',
                    data:JSON.stringify(registerVO),
                    success:function (data){

                        if(data==true){
                            $('#Success').modal();
                            $('#Success').on('hide.bs.modal',
                                function() {
                                    window.location.href = "Login.html";
                                })
                        }
                        else{
                            $('#Reapeat').modal();
                        }
                    },
                    error:function (xhr) {

                        alert(xhr.status+" "+xhr.statusText);
                    }
                });


            }

        }
    },'#registBtn'
);

