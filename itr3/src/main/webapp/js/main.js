/**
 * Created by yipeng on 2017/5/16.
 */

var stock_Data;


var recommend_Stock;
/*=[
 ['深发展A','sh00001',0.52],
 ['绿地科技','sh00023',0.63] ,
 ['澳新资产','sh00033',0.78],
 ['盈奥科技','sh00322',0.26],
 ['东 莞','sh00078',0.56]
 ];*/



function initRecommendList() {
    var stockName=new Array();

    var stockRate=new Array();


    $.ajax({
        type:'post',
        url:'/main/getRecommendStock',

        success:function (res){

            recommend_Stock=new Array();
            stockName= res.name;

            stockRate= res.up;

            for(var i= 0 ; i<stockRate.length-1 ; i++)
            {
                for(var j = 0; j<stockRate.length-i-1 ; j++)
                {
                    if(stockRate[j]<stockRate[j+1])
                    {
                        var swapRate=stockRate[j];
                        var swapName=stockName[j];
                        stockRate[j]=stockRate[j+1];
                        stockRate[j+1]=swapRate;
                        stockName[j]=stockName[j+1];
                        stockName[j+1]=swapName;
                    }
                }
            }
            for (var i = 0; i < 5; i++)
            {
                recommend_Stock[i]=new Array();

                recommend_Stock[i][0]=stockName[i].split(":")[0];
                recommend_Stock[i][1]=stockName[i].split(":")[1];

                recommend_Stock[i][2]=stockRate[i];

                recommend_Stock[i][2]=(recommend_Stock[i][2]).toFixed(2);
                document.getElementById("recommendName"+(i+1)).innerHTML=recommend_Stock[i][0]+"("+
                    recommend_Stock[i][1]+")";
                document.getElementById("recommendRate"+(i+1)).innerHTML=recommend_Stock[i][2]+"%";
            }
        },
        error:function () {
            alert("获取推荐股票失败！");
        }
    });


}

$(document).ready( function () {
    showvalf();
    $.ajax({
        type:'post',
        url:'/main/getMainStockToday',
        success:function (data){

            stock_Data=data;

            initStockTable(stock_Data);
        },
        error:function () {
            alert("获取今日股票失败！");
        }
    });
    $("#TodayBestStock").hide();
    $("#TodayBestStock").fadeIn(2000);


    initRecommendList();

});


$(document).on(
    {
        /*为今日推荐股票进行跳转*/
        click:function () {
            alert("点的是"+document.getElementById("recommendName1").innerHTML);
            var code=document.getElementById("recommendName1").innerHTML.split("(")[1].split(")")[0];

            var url = "oneStockInfo.html"+"?code="+code;
            window.location.href = url;
        }
    },'#recommendName1'

);

$(document).on(
    {
        /*为今日推荐股票进行跳转*/
        click:function () {

            var code=document.getElementById("recommendName2").innerHTML.split("(")[1].split(")")[0];

            var url = "oneStockInfo.html"+"?code="+code;
            window.location.href = url;
        }
    },'#recommendName2'

);

$(document).on(
    {
        /*为今日推荐股票进行跳转*/
        click:function () {

            var code=document.getElementById("recommendName3").innerHTML.split("(")[1].split(")")[0];

            var url = "oneStockInfo.html"+"?code="+code;
            window.location.href = url;
        }
    },'#recommendName3'

);

$(document).on(
    {
        /*为今日推荐股票进行跳转*/
        click:function () {

            var code=document.getElementById("recommendName4").innerHTML.split("(")[1].split(")")[0];

            var url = "oneStockInfo.html"+"?code="+code;
            window.location.href = url;
        }
    },'#recommendName4'

);

$(document).on(
    {
        /*为今日推荐股票进行跳转*/
        click:function () {

            var code=document.getElementById("recommendName5").innerHTML.split("(")[1].split(")")[0];

            var url = "oneStockInfo.html"+"?code="+code;
            window.location.href = url;
        }
    },'#recommendName5'

);

$(document).on(
    {

        click:function () {
            var nTrs = $('#table_local').dataTable().fnGetNodes();//fnGetNodes获取表格所有行，nTrs[i]表示第i行tr对象
            var count=0;
            var   arrayCode=new Array();
            var   arrayName=new Array();
            var strarrayCode;
            var strarrayName;//只能存储string类型
            for(var i = 0; i < nTrs.length; i++){

                if($(nTrs[i]).hasClass('selected')){

                    var valueCode=$('#table_local').dataTable().fnGetData(nTrs[i]).code;
                    var valueName=$('#table_local').dataTable().fnGetData(nTrs[i]).name;

                    arrayCode[count]=valueCode;

                    arrayName[count]=valueName;
                    count++;

                }
            }
            strarrayCode=JSON.stringify(arrayCode);
            strarrayName=JSON.stringify(arrayName);
            alert(count);
            alert(strarrayCode);
            sessionStorage.setItem("addPStockFromMainLen",count);
            sessionStorage.setItem("addPStockNameFromMain",strarrayName);
            sessionStorage.setItem("addPStockCodeFromMain",strarrayCode);
        }
    },'#addPersonStock'
);

