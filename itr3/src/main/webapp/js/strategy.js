/**
 * Created by LZ on 2017/6/3.
 */
var array=[];//this var is used to save stocksCode chosen by users
var countArray=0;//用来记录array中已经有了多少股票
var arrayName=[];//this var is used to save stocksName chosen by users
var strategyVO;
var myDate;
var today;
var transtime;
var restime;

/*初始化用户选择的自选股票池*/
function initPersonStockPool() {

    array=JSON.parse(sessionStorage.getItem("initStockCode"));

    arrayName=JSON.parse(sessionStorage.getItem("initStockName"));
    if(!array)
    {
        array=new Array();
        arrayName=new Array();

    }
    else
    {
        countArray=array.length;
        //alert("countArray 变成了"+countArray);
    }



    var PSLFromPlateLen=sessionStorage.getItem("addPStockFromPlateLen");//从板块股票列表得来的股票长度

    var PSLFromMainLen= sessionStorage.getItem("addPStockFromMainLen");//从大盘股票列表得来的股票长度

    var strpersonArrayCodeFromPlate;
    var strpersonArrayNameFromPlate;
    var strpersonArrayCodeFromMain;
    var strpersonArrayNameFromMain;

    if(PSLFromPlateLen != null)
    {
        personArrayCodeFromPlate=new Array( PSLFromPlateLen);
        personArrayNameFromPlate=new Array(PSLFromPlateLen);
        strpersonArrayCodeFromPlate=sessionStorage.getItem("addPStockCodeFromPlate");
        strpersonArrayNameFromPlate=sessionStorage.getItem("addPStockNameFromPlate");

        personArrayCodeFromPlate=JSON.parse(strpersonArrayCodeFromPlate);
       // alert(personArrayCodeFromPlate);
        personArrayNameFromPlate=JSON.parse( strpersonArrayNameFromPlate);


        /*检查从板块来的添加股票*/

        for(var i=0;i<PSLFromPlateLen;i++)
        {
            if(array.indexOf(personArrayCodeFromPlate[i])!=(-1))
            {
                //包含
            }
            else
            {

                array[countArray]=personArrayCodeFromPlate[i];
                arrayName[countArray]=personArrayNameFromPlate[i];
                countArray++;
            }
        }
    }

    if(PSLFromMainLen != null)
    {
        //alert("进入主页");
       // alert("MainLen is"+PSLFromMainLen);
        personArrayNameFromMain=new Array(PSLFromMainLen);
        personArrayCodeFromMain=new Array(PSLFromMainLen);
        strpersonArrayCodeFromMain=sessionStorage.getItem("addPStockCodeFromMain");
        strpersonArrayNameFromMain=sessionStorage.getItem("addPStockNameFromMain");
        personArrayNameFromMain=JSON.parse( strpersonArrayNameFromMain);
        personArrayCodeFromMain=JSON.parse( strpersonArrayCodeFromMain);
        //alert(personArrayCodeFromMain);
        /*检查从主页来的添加股票*/
        for(var i=0;i<PSLFromMainLen;i++)
        {
            if(array.indexOf(personArrayCodeFromMain[i])!=(-1))
            {
                //包含
            }
            else
            {
               // alert("array 不包含"+personArrayCodeFromMain[i]);
                array[countArray]=personArrayCodeFromMain[i];
               // alert("array 添加 "+personArrayCodeFromMain[i]);
                arrayName[countArray]=personArrayNameFromMain[i];
                countArray++;
            }


        }

    }


    sessionStorage.setItem("initStockCode",JSON.stringify(array));
    sessionStorage.setItem("initStockName",JSON.stringify(arrayName));
    //alert(countArray);
    //alert(array);

}
function save_strategy() {
    if(document.getElementById("chosen_picture").innerHTML=="超额收益率/策略胜率"){
        $("#failSave1").modal();
    }
    else if(!strategyVO){
        $("#failSave2").modal();
    }
    else{
        var userName=document.getElementById('span_username').innerHTML;
        var type;
        if(document.getElementById("chosen_strategy").innerHTML=="动量策略"){
            type="MUM";
        }
        else if(document.getElementById("chosen_strategy").innerHTML=="均值回归"){
            type="AVE";
        }
        else{
            $("#failSave2").modal();
           // alert("当前无可保存的策略");
            return;
        }
        var money=1000000;
        var begin=document.getElementById("dp1").value;
        var end=document.getElementById("dp2").value;
        var pool;
        if(document.getElementById("chosen_plate").innerHTML=="主板"){
            pool=1;
        }
        else if(document.getElementById("chosen_plate").innerHTML=="中小板"){
            pool=2;
        }
        else if(document.getElementById("chosen_plate").innerHTML=="创业板"){
            pool=3;
        }
        if(array.length>0){
            pool=4;
        }
        var hasST=0;
        if(document.getElementById("chosen_st").innerHTML=="包含"){
            hasST=1;
        }
        else if(document.getElementById("chosen_st").innerHTML=="排除"){
            hasST=0;
        }
        var hold;
        var form;
        if(type=="MUM"){
            hold=document.getElementById("mom_hold").value;
            form=document.getElementById("mom_form").value;
        }
        else{
            hold=document.getElementById("ave_hold").value;
            if(document.getElementById("chosen_ave_a_and_c").innerHTML=="5日均线"){
                form="5";
            }
            else if(document.getElementById("chosen_ave_a_and_c").innerHTML=="10日均线"){
                form="10";
            }
            else if(document.getElementById("chosen_ave_a_and_c").innerHTML=="20日均线"){
                form="20";
            }
        }
        if(begin>end){
          //  alert("时间错误");
            $("#illegalTime").modal();
            return;
        }
        if(begin>restime){
            $("#illegalTime").modal();
            // alert("选择的时间段内缺失数据");
            return;
        }
        if(!begin||!end||!hold||!form){
            $("#inputEmpty").modal();
           // alert("输入不完整 "+begin+" "+end+" "+hold+" "+form);
            return;
        }
        if(pool==4&&array.length==0){
            $("#unSelect").modal();
           // alert("请选择股票池");
            return;
        }
        var userChosenStocks="";
        for(var i=0;i<countArray;i++){
            if(userChosenStocks==""){
                userChosenStocks+=array[i];
            }
            else{
                userChosenStocks=userChosenStocks+","+array[i];
            }
        }
        var code=0;
      //  alert(userName);
        $.ajax({
            type:'post',
            url:'/strategy/getHistoryNum',
            data:{userName:userName},
            success:function (data){
                data++;
                code+=data;
                code=code.toString();
                var StrategyEntity={
                    "userName":userName,
                    "type":type,
                    "startDate":begin,
                    "endDate":end,
                    "bankuai":pool,
                    "hasST":hasST,
                    "userChosenStocks":userChosenStocks,
                    "money":money,
                    "code":code,
                    "holdTime":hold,
                    "formTime":form
                };
                $.ajax({
                    type:'post',
                    url:'/strategy/saveStrategy',
                    contentType:'application/json;charset=utf-8',
                    data:JSON.stringify(StrategyEntity),
                    success:function (data){
                        if(data==true){
                           // alert("成功保存到策略历史");
                            $("#successSave").modal();
                            strategyVO=null;
                        }
                        else{
                            alert("保存策略失败");

                        }
                    },
                    error:function () {
                        alert("保存策略失败");
                    }
                });
            },
            error:function () {
                alert("保存策略失败");
            }
        });
    }
}

function decide_pool(){
    var nTrs = $('#example').dataTable().fnGetNodes();//fnGetNodes获取表格所有行，nTrs[i]表示第i行tr对象
    var contain=false;
    for(var i = 0; i < nTrs.length; i++){
        if($(nTrs[i]).hasClass('selected')){
            //alert("有股票被选中");
            var code=$('#example').dataTable().fnGetData(nTrs[i]).code;
            var name=$('#example').dataTable().fnGetData(nTrs[i]).name;
            //alert("选中的股票是"+code);
            for(var j=0;j<countArray;j++){
                if(code==array[j]){
                    contain=true;
                    break;
                }
            }
            if(contain==false) {
                //alert("s");
                array[countArray] = code;
                arrayName[countArray] = name;
                countArray++;
                $('#self_pool').DataTable().row.add([
                    code,
                    name
                ]).draw(false);
            }
            contain==false;
        }
    }
}

function cancel_selected() {
    $('#example').DataTable().rows().deselect();
    $('#self_pool').DataTable().rows().remove().draw( false );
    array=[];//clean the array
    arrayName=[];
    countArray=0;
}

function init_stock_pool(pool){
    $('#example').DataTable( {
        dom: 'lrtip',
        select: true,
        //multiselect:true,
        "bFilter": false,
        "scrollY":        "200px",
        "scrollCollapse": true,
        "paging":         false,
        data:pool,
        columns:[
            {data:'code'},
            {data:'name'}
        ]
    });

    $('#example tbody').on('click', 'tr', function() {
        $(this).toggleClass('selected');
    });
    //$('#self_pool').dataTable().fnDestroy();
    $('#self_pool').DataTable( {
        dom: 'rtip',
        "bFilter": false,
        "scrollY":        "200px",
        "scrollCollapse": true,
        "paging":         false,
        "aoColumns": [
            { "sTitle": "股票代码" },
            { "sTitle": "股票名称" }
        ]
    });
    //alert(countArray);
    for(var i=0;i<countArray;i++) {
        $('#self_pool').DataTable().row.add([
            array[i],
            arrayName[i]
        ]).draw(false);
    }
}

function get_mom_b(){

    var type;
    if(array.length==0){
        type=true;
    }
    else{
        if(array.length<100){
            $("#numLess").modal();
            //alert("当前自选股票数为"+array.length+"，自选股票小于100只");
            return;
        }
        else {
            type = false;
        }
    }
    var money=1000000;
    var begin=document.getElementById("dp1").value;
    var end=document.getElementById("dp2").value;
    var is_hold="s";
    if(document.getElementById("chosen_period").innerHTML=="持有期"){
        is_hold=false;
    }
    if(document.getElementById("chosen_period").innerHTML=="形成期"){
        is_hold=true;
    }
    var time=document.getElementById("time").value;
    if(begin>end){
       // alert("时间错误");
        $("#illegalTime").modal();
        return;
    }


    if(begin>restime){
        $("#illegalTime").modal();
       // alert("选择的时间段内缺失数据");
        return;
    }
    if(!begin||!end||is_hold=="s"||!time){
        //alert("输入不完整");
        $("#inputEmpty").modal();
        return;
    }
    var pool="M";
    if(document.getElementById("chosen_plate").innerHTML=="主板"){
        pool="M";
    }
    else if(document.getElementById("chosen_plate").innerHTML=="中小板"){
        pool="S";
    }
    else if(document.getElementById("chosen_plate").innerHTML=="创业板"){
        pool="G";
    }
    var hasST=true;
    if(document.getElementById("chosen_st").innerHTML=="包含"){
        hasST=true;
    }
    else if(document.getElementById("chosen_st").innerHTML=="排除"){
        hasST=false;
    }
    if((!pool)&&array.length==0){
       // alert("请选择股票池");
        $("#unSelect").modal();
        return;
    }
    var selectVO= {
        "type": type,
        "money": money,
        "begin": begin,
        "end": end,
        "isHoldingPeriod": is_hold,
        "time": time,
        "vo": {"pool": pool, "hasST": hasST, "list": array}
    }
    $.ajax({
        type:'post',
        url:'/chart/mum_b',
        dataType: "json",
        contentType:'application/json;charset=utf-8',
        data:JSON.stringify(selectVO),
        success:function (res){
            var calcircle;
            if(res.isHoldingPeriod){
                calcircle="持有期";
            }
            else{
                calcircle="形成期";
            }
            $(function () {
                $('#b_table').dataTable().fnDestroy();
                $('#b_table').DataTable({
                    "bInfo": false,
                    "bFilter": false,
                    "scrollY": "700px",
                    "scrollCollapse": true,
                    "paging": false,
                    data:res.BItemList,
                    columns:[
                        {data:'circle'},
                        {data:'excessEarnings'},
                        {data:'annualWinRate'}
                    ]
                });
            });
            $(function () {
                $('#mom_picture_b_part2a').highcharts({
                    chart: {
                        type: 'area'
                    },
                    title: {
                        text: '超额收益分布图'
                    },
                    xAxis: {
                        title: {
                            text: calcircle
                        },
                        categories: res.circleList
                    },
                    credits: {
                        enabled: false
                    },
                    yAxis: {
                        title: {
                            text: '超额收益'
                        },
                        labels: {
                            formatter: function () {
                                return this.value + '%';
                            }
                        }
                    },
                    plotOptions: {
                        area: {
                            marker: {
                                enabled: false,
                                symbol: 'circle',
                                radius: 1,
                                states: {
                                    hover: {
                                        enabled: true
                                    }
                                }
                            }
                        }
                    },
                    series: [{
                        name: '收益',
                        data: res.p1
                    }]
                });
            });
            $(function () {
                $('#mom_picture_b_part2b').highcharts({
                    chart: {
                        type: 'area'
                    },
                    title: {
                        text: '策略胜率分布图'
                    },
                    xAxis: {
                        title: {
                            text: calcircle
                        },
                        categories: res.circleList
                    },
                    credits: {
                        enabled: false
                    },
                    yAxis: {
                        title: {
                            text: '策略胜率'
                        },
                        labels: {
                            formatter: function () {
                                return this.value + '%';
                            }
                        }
                    },
                    plotOptions: {
                        area: {
                            marker: {
                                enabled: false,
                                symbol: 'circle',
                                radius: 1,
                                states: {
                                    hover: {
                                        enabled: true
                                    }
                                }
                            }
                        }
                    },
                    series: [{
                        name: '胜率',
                        data: res.p2
                    }]
                });
            });
        },
        error:function () {
            alert("请求失败");
        }
    });
}

function get_ave_b(){

    var type;
    if(array.length==0){
        type=true;
    }
    else{
        if(array.length<100){
           // alert("当前自选股票数为"+array.length+"，自选股票小于100只");
            $("#numLess").modal();
            return;
        }
        else {
            type = false;
        }
    }
    var money=1000000;
    var begin=document.getElementById("dp1").value;
    var end=document.getElementById("dp2").value;
    var time;
    if(document.getElementById("chosen_ave_b").innerHTML=="5日均线"){
        time=5;
    }
    else if(document.getElementById("chosen_ave_b").innerHTML=="10日均线"){
        time=10;
    }
    else if(document.getElementById("chosen_ave_b").innerHTML=="20日均线"){
        time=20;
    }
    if(begin>end){
        $("#illegalTime").modal();
        return;
    }
    if(begin>restime){
        $("#illegalTime").modal();
        // alert("选择的时间段内缺失数据");
        return;
    }
    if(!begin||!end||!time){
       // alert("输入不完整");
        $("#inputEmpty").modal();
        return;
    }
    var pool="M";
    if(document.getElementById("chosen_plate").innerHTML=="主板"){
        pool="M";
    }
    else if(document.getElementById("chosen_plate").innerHTML=="中小板"){
        pool="S";
    }
    else if(document.getElementById("chosen_plate").innerHTML=="创业板"){
        pool="G";
    }
    var hasST=true;
    if(document.getElementById("chosen_st").innerHTML=="包含"){
        hasST=true;
    }
    else if(document.getElementById("chosen_st").innerHTML=="排除"){
        hasST=false;
    }
    if((!pool)&&array.length==0){
       // alert("请选择股票池");
        $("#unSelect").modal();
        return;
    }
    var selectVO={
        "type":type,
        "money":money,
        "begin":begin,
        "end":end,
        "time":time,
        "vo":{"pool":pool,"hasST":hasST,"list":array}
    };
    $.ajax({
        type:'post',
        url:'/chart/ave_b',
        dataType: "json",
        contentType:'application/json;charset=utf-8',
        data: JSON.stringify(selectVO),
        success:function (res){
            var calcircle;
            if(res.isHoldingPeriod){
                calcircle="持有期";
            }
            else{
                calcircle="形成期";
            }
            $(function () {
                $('#ave_b_table').dataTable().fnDestroy();
                $('#ave_b_table').DataTable({
                    "bInfo": false,
                    "bFilter": false,
                    "scrollY": "700px",
                    "scrollCollapse": true,
                    "paging": false,
                    data:res.BItemList,
                    columns:[
                        {data:'circle'},
                        {data:'excessEarnings'},
                        {data:'annualWinRate'}
                    ]
                });
            });
            $(function () {
                $('#ave_picture_b_part2a').highcharts({
                    chart: {
                        type: 'area'
                    },
                    title: {
                        text: '超额收益分布图'
                    },
                    xAxis: {
                        title: {
                            text: calcircle
                        },
                        categories: res.circleList
                    },
                    credits: {
                        enabled: false
                    },
                    yAxis: {
                        title: {
                            text: '超额收益'
                        },
                        labels: {
                            formatter: function () {
                                return this.value + '%';
                            }
                        }
                    },
                    plotOptions: {
                        area: {
                            marker: {
                                enabled: false,
                                symbol: 'circle',
                                radius: 1,
                                states: {
                                    hover: {
                                        enabled: true
                                    }
                                }
                            }
                        }
                    },
                    series: [{
                        name: '收益',
                        data: res.p1
                    }]
                });
            });
            $(function () {
                $('#ave_picture_b_part2b').highcharts({
                    chart: {
                        type: 'area'
                    },
                    title: {
                        text: '策略胜率分布图'
                    },
                    xAxis: {
                        title: {
                            text: calcircle
                        },
                        categories: res.circleList
                    },
                    credits: {
                        enabled: false
                    },
                    yAxis: {
                        title: {
                            text: '策略胜率'
                        },
                        labels: {
                            formatter: function () {
                                return this.value + '%';
                            }
                        }
                    },
                    plotOptions: {
                        area: {
                            marker: {
                                enabled: false,
                                symbol: 'circle',
                                radius: 1,
                                states: {
                                    hover: {
                                        enabled: true
                                    }
                                }
                            }
                        }
                    },
                    series: [{
                        name: '胜率',
                        data: res.p2
                    }]
                });
            });
        },
        error:function () {
            alert("请求失败");
        }
    });
}

function get_mom_ac(data){

    if(data!=0){
        draw_mom_ac(data);
    }
    else{
        var type;
        if(array.length==0){
            type=true;
        }
        else{
            if(array.length<100){
              //  alert("当前自选股票数为"+array.length+"，自选股票小于100只");
                $("#numLess").modal();
                return;
            }
            else {
                type = false;
            }
        }
        var money=1000000;
        var begin=document.getElementById("dp1").value;
        var end=document.getElementById("dp2").value;
        var hold=document.getElementById("mom_hold").value;
        var form=document.getElementById("mom_form").value;
        if(begin>end){
         //   alert("时间错误");
            $("#illegalTime").modal();
            return;
        }
        if(begin>restime){
            $("#illegalTime").modal();
            // alert("选择的时间段内缺失数据");
            return;
        }
        if(!begin||!end||!hold||!form){
            $("#inputEmpty").modal();
            //alert("输入不完整");
            return;
        }
        var pool="M";
        if(document.getElementById("chosen_plate").innerHTML=="主板"){
            pool="M";
        }
        else if(document.getElementById("chosen_plate").innerHTML=="中小板"){
            pool="S";
        }
        else if(document.getElementById("chosen_plate").innerHTML=="创业板"){
            pool="G";
        }
        var hasST=true;
        if(document.getElementById("chosen_st").innerHTML=="包含"){
            hasST=true;
        }
        else if(document.getElementById("chosen_st").innerHTML=="排除"){
            hasST=false;
        }
        if((!pool)&&array.length==0){
           // alert("请选择股票池");
            $("#unSelect").modal();
            return;
        }
        var selectVO={
            "type":type,
            "money":money,
            "begin":begin,
            "end":end,
            "holdTime":hold,
            "formTime":form,
            "vo":{"pool":pool,"hasST":hasST,"list":array}
        };
        $.ajax({
            type:'post',
            url:'/chart/mum_ac',
            dataType: "json",
            contentType:'application/json;charset=utf-8',
            data: JSON.stringify(selectVO),
            success:function (res){
                strategyVO=res;
                draw_mom_ac(res);
            },
            error:function () {
                alert("请求失败");
            }
        });
    }
}

function draw_mom_ac(res){

    var t1=res.benchmarkYieldList.split(',');
    var t2=res.dailyYieldList.split(',');
    var s1=new Array();
    for(var i=0;i<t1.length/2;i++){
        s1[i]=new Array();
        var d=t1[2*i].split('-');
        s1[i][0]=Date.UTC(d[0]*1,d[1]*1-1,d[2]*1);
        s1[i][1]=t1[2*i+1]*1;
    }
    var s2=new Array();
    for(var i=0;i<t2.length/2;i++){
        s2[i]=new Array();
        var d=t2[2*i].split('-');
        s2[i][0]=Date.UTC(d[0]*1,d[1]*1-1,d[2]*1);
        s2[i][1]=t2[2*i+1]*1;
    }
    $('#mom_param').dataTable().fnDestroy();
    $('#mom_param').DataTable( {
        "bInfo":false,
        "bFilter": false,
        "paging":         false,
        "aaData": [
            [ res.annualYield, res.benchmarkAnnualYield,res.alpha,res.beta,res.sharp,res.maxDrawdown]
        ]
    } );
    $('#mom_picture_aa').highcharts({
        global: {
            useUTC: false //关闭UTC
        },
        chart: {
            type: 'spline'
        },
        title: {
            text: '累计收益率比较图'
        },
        xAxis: {
            type: 'datetime',
            title: {
                text: null
            }
        },
        credits: {
            enabled: false
        },
        yAxis: {
            title: {
                text: '累计 收益 (%)'
            }
        },
        tooltip: {
            headerFormat: '<b>{series.name}</b><br>',
            pointFormat: '{point.x:%e. %b}: {point.y:.2f} %'
        },
        plotOptions: {
            spline: {
                marker: {
                    enabled: true
                }
            },
            series: {
                turboThreshold: 0
            }
        },
        series: [{
            name: '基准',
            data: s1
        }, {
            name: '策略',
            data: s2
        }]
    });
    $(function () {
        var data1 = res.profitList;
        var data2 = res.lossList;
        /**
         * Get histogram data out of xy data
         * @param   {Array} data  Array of tuples [x, y]
         * @param   {Number} step Resolution for the histogram
         * @returns {Array}       Histogram data
         */
        function histogram1(data, step) {
            var histo = {},
                x,
                i,
                arr = [];
            // Group down
            for (i = 0; i < data.length; i++) {
                x = Math.floor(Math.abs(data[i]) / step) * step;
                if (!histo[x]) {
                    histo[x] = 0;
                }
                if(data[i]>=0){
                    histo[x]++;
                }
            }
            // Make the histo group into an array
            for (x in histo) {
                if (histo.hasOwnProperty((x))) {
                    arr.push([parseFloat(x), histo[x]]);
                }
            }
            // Finally, sort the array
            arr.sort(function (a, b) {
                return a[0] - b[0];
            });
            return arr;
        }
        function histogram2(data, step) {
            var histo = {},
                x,
                i,
                arr = [];
            // Group down
            for (i = 0; i < data.length; i++) {
                x = Math.floor(Math.abs(data[i]) / step) * step;
                if (!histo[x]) {
                    histo[x] = 0;
                }
                if(data[i]<0){
                    histo[x]--;
                }
            }
            // Make the histo group into an array
            for (x in histo) {
                if (histo.hasOwnProperty((x))) {
                    arr.push([parseFloat(x), histo[x]]);
                }
            }
            // Finally, sort the array
            arr.sort(function (a, b) {
                return a[0] - b[0];
            });
            return arr;
        }
        $('#mom_picture_c').highcharts({
            chart: {
                type: 'column'
            },
            title: {
                text: '相对指数分布收益图'
            },
            xAxis: {
                gridLineWidth: 1
            },
            credits: {
                enabled: false
            },
            yAxis: [{
                title: {
                    text: '正负收益次数'
                }
            }],
            series: [{
                name: '正收益次数',
                type: 'column',
                data: histogram1(data1, 1),
                pointPadding: 0,
                groupPadding: 0,
                pointPlacement: 'between'
            }, {
                name: '负收益次数',
                type: 'column',
                data: histogram2(data2, 1),
                pointPadding: 0,
                groupPadding: 0,
                pointPlacement: 'between'
            }]
        });
    });
}

function draw_ave_ac(res){
    var t1=res.benchmarkYieldList.split(',');
    var t2=res.dailyYieldList.split(',');
    var s1=new Array();
    for(var i=0;i<t1.length/2;i++){
        s1[i]=new Array();
        var d=t1[2*i].split('-');
        s1[i][0]=Date.UTC(d[0]*1,d[1]*1-1,d[2]*1);
        s1[i][1]=t1[2*i+1]*1;
    }
    var s2=new Array();
    for(var i=0;i<t2.length/2;i++){
        s2[i]=new Array();
        var d=t2[2*i].split('-');
        s2[i][0]=Date.UTC(d[0]*1,d[1]*1-1,d[2]*1);
        s2[i][1]=t2[2*i+1]*1;
    }
    $('#ave_param').dataTable().fnDestroy();
    $('#ave_param').DataTable( {
        "bInfo":false,
        "bFilter": false,
        "paging":         false,
        "aaData": [
            [ res.annualYield, res.benchmarkAnnualYield,res.alpha,res.beta,res.sharp,res.maxDrawdown]
        ]
    } );
    $('#ave_picture_aa').highcharts({
        global: {
            useUTC: false //关闭UTC
        },
        chart: {
            type: 'spline'
        },
        title: {
            text: '累计收益率比较图'
        },
        xAxis: {
            type: 'datetime',
            title: {
                text: null
            }
        },
        credits: {
            enabled: false
        },
        yAxis: {
            title: {
                text: '累计 收益 (%)'
            }
        },
        tooltip: {
            headerFormat: '<b>{series.name}</b><br>',
            pointFormat: '{point.x:%e. %b}: {point.y:.2f} %'
        },
        plotOptions: {
            spline: {
                marker: {
                    enabled: true
                }
            },
            series: {
                turboThreshold: 0
            }
        },
        series: [{
            name: '基准',
            data: s1
        }, {
            name: '策略',
            data: s2
        }]
    });
    $(function () {
        var data1 = res.profitList;
        var data2 = res.lossList;
        /**
         * Get histogram data out of xy data
         * @param   {Array} data  Array of tuples [x, y]
         * @param   {Number} step Resolution for the histogram
         * @returns {Array}       Histogram data
         */
        function histogram1(data, step) {
            var histo = {},
                x,
                i,
                arr = [];
            // Group down
            for (i = 0; i < data.length; i++) {
                x = Math.floor(Math.abs(data[i]) / step) * step;
                if (!histo[x]) {
                    histo[x] = 0;
                }
                if(data[i]>=0){
                    histo[x]++;
                }
            }
            // Make the histo group into an array
            for (x in histo) {
                if (histo.hasOwnProperty((x))) {
                    arr.push([parseFloat(x), histo[x]]);
                }
            }
            // Finally, sort the array
            arr.sort(function (a, b) {
                return a[0] - b[0];
            });
            return arr;
        }
        function histogram2(data, step) {
            var histo = {},
                x,
                i,
                arr = [];
            // Group down
            for (i = 0; i < data.length; i++) {
                x = Math.floor(Math.abs(data[i]) / step) * step;
                if (!histo[x]) {
                    histo[x] = 0;
                }
                if(data[i]<0){
                    histo[x]--;
                }
            }
            // Make the histo group into an array
            for (x in histo) {
                if (histo.hasOwnProperty((x))) {
                    arr.push([parseFloat(x), histo[x]]);
                }
            }
            // Finally, sort the array
            arr.sort(function (a, b) {
                return a[0] - b[0];
            });
            return arr;
        }
        $('#ave_picture_c').highcharts({
            chart: {
                type: 'column'
            },
            title: {
                text: '相对指数分布收益图'
            },
            xAxis: {
                gridLineWidth: 1
            },
            credits: {
                enabled: false
            },
            yAxis: [{
                title: {
                    text: '正负收益次数'
                }
            }],
            series: [{
                name: '正收益次数',
                type: 'column',
                data: histogram1(data1, 1),
                pointPadding: 0,
                groupPadding: 0,
                pointPlacement: 'between'
            }, {
                name: '负收益次数',
                type: 'column',
                data: histogram2(data2, 1),
                pointPadding: 0,
                groupPadding: 0,
                pointPlacement: 'between'
            }]
        });
    });
}

function get_ave_ac(data){
    if(data!=0){
        draw_ave_ac(data);
    }
    else{
        var type;
        if(array.length==0){
            type=true;
        }
        else{
            if(array.length<100){
                $("#numLess").modal();
              //  alert("当前自选股票数为"+array.length+"，自选股票小于100只");
                return;
            }
            else {
                type = false;
            }
        }
        var money=1000000;
        var begin=document.getElementById("dp1").value;
        var end=document.getElementById("dp2").value;
        var hold=document.getElementById("ave_hold").value;
        var form;
        if(document.getElementById("chosen_ave_a_and_c").innerHTML=="5日均线"){
            form="5";
        }
        else if(document.getElementById("chosen_ave_a_and_c").innerHTML=="10日均线"){
            form="10";
        }
        else if(document.getElementById("chosen_ave_a_and_c").innerHTML=="20日均线"){
            form="20";
        }
        if(begin>end){
            $("#illegalTime").modal();
           // alert("时间错误");
            return;
        }
        if(begin>restime){
            $("#illegalTime").modal();
            // alert("选择的时间段内缺失数据");
            return;
        }
        if(!begin||!end||!hold||!form){
            $("#inputEmpty").modal();
          //  alert("输入不完整");
            return;
        }
        var pool="M";
        if(document.getElementById("chosen_plate").innerHTML=="主板"){
            pool="M";
        }
        else if(document.getElementById("chosen_plate").innerHTML=="中小板"){
            pool="S";
        }
        else if(document.getElementById("chosen_plate").innerHTML=="创业板"){
            pool="G";
        }
        var hasST=true;
        if(document.getElementById("chosen_st").innerHTML=="包含"){
            hasST=true;
        }
        else if(document.getElementById("chosen_st").innerHTML=="排除"){
            hasST=false;
        }
        if((!pool)&&array.length==0){
            //alert("请选择股票池");
            $("#unSelect").modal();
            return;
        }
        var selectVO={
            "type":type,
            "money":money,
            "begin":begin,
            "end":end,
            "holdTime":hold,
            "formTime":form,
            "vo":{"pool":pool,"hasST":hasST,"list":array}
        };
        $.ajax({
            type:'post',
            url:'/chart/ave_ac',
            dataType: "json",
            contentType:'application/json;charset=utf-8',
            data: JSON.stringify(selectVO),
            success:function (res){
                strategyVO=res;
                draw_ave_ac(res);
            },
            error:function () {
                alert("请求失败");
            }
        });
    }
}


$(document).ready( function () {

     myDate=new Date();
     today=myDate.toLocaleDateString();
     transtime=today.split("/");
     restime=transtime[0]+"-0"+transtime[1]+"-"+(transtime[2]-1);
});
