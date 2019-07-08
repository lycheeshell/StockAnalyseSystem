 /**
 * Created by yipeng on 2017/6/5.
 */

var plateStockList;

var arrayCode;

var arrayName;

$(document).ready(function () {
    var plateName;
    plateName=getQueryString('plate');
    var findTime=getQueryString('findTime');

    showvalf();
    if(plateName)
    {
        $.ajax({
            type:'post',
            url:'/marketTemper/findSDE_fromConceptBench',


            data:{conceptName:plateName,
                  datestr:findTime
            },
            success:function (res){

                plateStockList=res;
                initStockTable(plateStockList);
            },
            error:function (xhr) {
                alert("请求失败！");
                alert(xhr.status+"  "+xhr.statusText);
            }
        });
    }

    document.getElementById('plateName').innerHTML=plateName;

    document.getElementById('panelTitle').innerHTML=plateName+"板块股票列表";



});


/*$('#table_local tbody').on('click', 'tr', function() {
    $(this).toggleClass('selected');
});*/

$(document).on(
    {

        click:function () {
            var nTrs = $('#table_local').dataTable().fnGetNodes();//fnGetNodes获取表格所有行，nTrs[i]表示第i行tr对象
            var count=0;
            arrayCode=new Array();
            arrayName=new Array();
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
            sessionStorage.setItem("addPStockFromPlateLen",count);
            sessionStorage.setItem("addPStockNameFromPlate",strarrayName);
            sessionStorage.setItem("addPStockCodeFromPlate",strarrayCode);
        }
    },'#addPersonStock'
);



