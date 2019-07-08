/**
 * Created by LZ on 2017/6/3.
 */
/*
function valueReplace(v){
    v=v.toString().replace(new RegExp('(["\"])', 'g'),"\\\"");
    return v;
}*/

function getQueryStr(str){
    var LocString=String(window.document.location.href);
    var rs = new RegExp("(^|)"+str+"=([^/&]*)(/&|$)","gi").exec(LocString), tmp;

    if(tmp=rs){
        return tmp[2];
    }

    // parameter cannot be found
    return "";
}



function getQueryString(key){//得到英文或中文的url参数
    var reg = new RegExp("(^|&)"+key+"=([^&]*)(&|$)");
    var result = window.location.search.substr(1).match(reg);
    return result?decodeURIComponent(result[2]):null;
}

function userLogout() {
    var userName=document.getElementById('span_username').innerHTML;
    $.ajax({
        type:'post',
        url:'/user/logout',
        data:{userName:userName},
        success:function (data){
            if(data==true){
                localStorage.removeItem("userName");
                document.getElementById('span_username').innerHTML = "用户";
                $("#userDropdown").css('display','none');
                $("#noneuserDropdown").toggle();
                to_main();
            }
            else{
                alert("退出失败！");
            }
        },
        error:function () {
            alert("网络不畅通！");
        }
    });
}

function  showvalf(){//如果url中有id,则将用户菜单内容置为id参数,即用户名,如果无id参数,则置为"用户"
    var username;
    var is_username;
    username=localStorage.getItem("userName");

   // alert(username);

    if(username != null)
    {
        //alert("jinrule");
        document.getElementById('span_username').innerHTML = username;

        $("#noneuserDropdown").css('display','none');
        is_username=true;
        //$("#userDropdown").css('display','block');

    }

    else
    {

        document.getElementById('span_username').innerHTML = "用户";

        //$("#noneuserDropdown").css('display','block');
        $("#userDropdown").css('display','none');
        is_username=false;
    }

    if(getQueryStr('stra')){
        var data=localStorage.getItem("picture");
        localStorage.removeItem("picture");
        var res=JSON.parse(data);
        var stra=getQueryString('stra');
        var pict="策略基准累计收益率";
        var pool="";
        if(res.pool=="M"){
            pool="主板";
        }
        if(res.pool=="S"){
            pool="中小板";
        }
        if(res.pool=="G"){
            pool="创业板";
        }
        var hasST=res.hasST;
        var list=[];
        if(res.list.length>0) {
            list=res.list.split(',');
        }
        var begin=res.begin;
        var end=res.end;
        var holdTime=res.holdTime.toString();
        var formTime=res.formTime.toString();
        if(list.length>0){
            alert("自选股票:"+list);
            var nTrs = $('#example').dataTable().fnGetNodes();//fnGetNodes获取表格所有行，nTrs[i]表示第i行tr对象
            var contain=false;
            for(var i = 0; i < nTrs.length; i++){
                var code=$('#example').dataTable().fnGetData(nTrs[i]).code;
                var name=$('#example').dataTable().fnGetData(nTrs[i]).name;
                for(var j=0;j<list.length;j++){
                        if(code==list[j]){
                            contain=true;
                            break;
                        }
                }
                if(contain==true) {
                    $('#self_pool').DataTable().row.add([
                        code,
                        name
                    ]).draw(false);
                }
                contain==false;
            }
        }
        else{
            document.getElementById("chosen_plate").innerHTML=pool;
            if(hasST==1) {
                document.getElementById("chosen_st").innerHTML = "包含";
            }
            else{
                document.getElementById("chosen_st").innerHTML = "排除";
            }
        }
        document.getElementById("dp1").value=begin;
        document.getElementById("dp2").value=end;
        document.getElementById("chosen_strategy").innerHTML=stra;
        document.getElementById("chosen_picture").innerHTML=pict;
        $('#save_stra_button').attr('disabled',"true");
        $('#button_show_a_c').attr('disabled',"true");
        $('#button_show_b').attr('disabled',"true");
        $('#ave_button_show_a_c').attr('disabled',"true");
        $('#ave_button_show_b').attr('disabled',"true");

        show_result_a_and_c(res);

        if(document.getElementById("chosen_strategy").innerHTML=="动量策略") {
            $('#ave_a_and_c').css('display', 'none');
            $('#ave_b').css('display','none');
            $('#ave_picture_a').css('display','none');
            $('#ave_picture_c').css('display','none');
            $('#ave_picture_aa').css('display','none');
            if (document.getElementById("chosen_picture").innerHTML == "策略基准累计收益率" || document.getElementById("chosen_picture").innerHTML == "收益率分布") {
                $('#mom_a_and_c').css('display', 'block');
                $('#mom_b').css('display', 'none');
                document.getElementById("mom_hold").value=holdTime;
                document.getElementById("mom_form").value=formTime;
                if (document.getElementById("chosen_picture").innerHTML == "策略基准累计收益率") {
                    $('#mom_picture_a').css('display', 'block');
                    $('#mom_picture_c').css('display', 'none');
                    $('#mom_picture_aa').css('display', 'block');
                }
                else {
                    $('#mom_picture_c').css('display', 'block');
                    $('#mom_picture_a').css('display', 'none');
                    $('#mom_picture_aa').css('display', 'none');
                }
            }
        }
        if(document.getElementById("chosen_strategy").innerHTML=="均值回归"){
            $('#mom_a_and_c').css('display', 'none');
            $('#mom_b').css('display','none');
            $('#mom_picture_a').css('display','none');
            $('#mom_picture_c').css('display','none');
            $('#mom_picture_aa').css('display','none');
            if (document.getElementById("chosen_picture").innerHTML == "策略基准累计收益率" || document.getElementById("chosen_picture").innerHTML == "收益率分布") {
                $('#ave_a_and_c').css('display', 'block');
                $('#ave_b').css('display', 'none');
                document.getElementById("ave_hold").value=holdTime;
                if(formTime==5){
                    document.getElementById("chosen_ave_a_and_c").innerHTML="5日均线";
                }
                else if(formTime==10){
                    document.getElementById("chosen_ave_a_and_c").innerHTML="10日均线";
                }
                else if(formTime==20){
                    document.getElementById("chosen_ave_a_and_c").innerHTML="20日均线";
                }
                if (document.getElementById("chosen_picture").innerHTML == "策略基准累计收益率") {
                    $('#ave_picture_a').css('display', 'block');
                    $('#ave_picture_c').css('display', 'none');
                    $('#ave_picture_aa').css('display', 'block');
                }
                else {
                    $('#ave_picture_c').css('display', 'block');
                    $('#ave_picture_a').css('display', 'none');
                    $('#ave_picture_aa').css('display', 'none');
                }
            }
        }
    }

    return is_username;
}
function to_personal_homepage(){
    var  getval =document.getElementById("span_username").innerHTML;
    window.location.href="personal_homepage.html?id="+getval;
}

function to_strategy(){
    var  getval =document.getElementById("span_username").innerHTML;
    window.location.href="strategy.html?id="+getval;
}

function to_market_temper(){
    var  getval =document.getElementById("span_username").innerHTML;
    window.location.href="MarketTemper.html?id="+getval;
}

function to_main(){
    var  getval =document.getElementById("span_username").innerHTML;
    window.location.href="main.html?id="+getval;
}

function to_compare_stock(){
    var  getval =document.getElementById("span_username").innerHTML;
    window.location.href="compare_stock.html?id="+getval;
}

