/**
 * Created by yipeng on 2017/5/23.
 */

function initStockScoreChart(Code) {

    var score;
    $.ajax({
        type:'get',
        url:'/oneStock/value',
        contentType:'application/json;charset=utf-8',
        data:{code:Code},
        success:function (data){


            score=data;
            score=parseFloat((score*100).toFixed(2));

            var gaugeOptions = {
                chart: {
                    type: 'solidgauge'
                },
                title: null,
                pane: {
                    center: ['50%', '95%'],
                    size: '140%',
                    startAngle: -90,
                    endAngle: 90,
                    background: {
                        backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || '#EEE',
                        innerRadius: '60%',
                        outerRadius: '100%',
                        shape: 'arc'
                    }
                },
                tooltip: {
                    enabled: false
                },
                // the value axis
                yAxis: {
                    min: 0,
                    max: 100,
                    stops: [
                        [0.1, '#55BF3B'], // green
                        [0.5, '#DDDF0D'], // yellow
                        [0.9, '#DF5353'] // red
                    ],
                    lineWidth: 0,
                    minorTickInterval: null,
                    tickPixelInterval: 4000,
                    title: {
                        y: -110
                    },
                    labels: {
                        y: 16
                    }
                },
                plotOptions: {
                    solidgauge: {
                        dataLabels: {
                            y: 5,
                            borderWidth: 0,
                            useHTML: true,
                            style:{
                                color:'#D5B781',
                                fontSize:'16px'
                            }

                        }

                    }
                }
            };

       var load=     $('#stockScoreChart').highcharts(Highcharts.merge(gaugeOptions, {

                loading: {
                    labelStyle: {
                        color: 'white'
                    },
                    style: {
                        backgroundColor: 'gray'
                    }
                },



                yAxis: {
                    title: {
                        text: '个股评分',
                        style:{
                            fontSize:'20px'
                        }
                    }
                },
                credits: {
                    enabled: false
                },
                series: [{
                    name: '得分',
                    data: [score]



                }]
            }));
        load.showLoading();

        },
        error:function (xhr) {
            alert("网络不畅通！");
            alert(xhr.status+xhr.statusText);
        }
    });


}

function initStockPolarChart(Code) {

    $.ajax({
        type:'get',
        url:'/oneStock/getStockRada',
        contentType:'application/json;charset=utf-8',
        data:{code:Code},
        success:function (data){

            var changerate=data.changerate;
            var pb=data.pb;
            var profit=data.profit;
            var undp=data.undp;
            var mrq=data.mrq;
            var totals=data.totals;
            var changerate_max=data.changerate_max;

            var pb_max=data.pb_max;
            var profit_max=data.profit_max;
            var undp_max=data.undp_max;
            var mrq_max=data.mrq_max;
            var totals_max=data.totals_max;
          //  var changerateRate=(changerate / changerate_max)*10000;
          //  alert(changerate_max);
            var numbers=[(changerate / changerate_max) ,( pb / pb_max ),( profit / profit_max) , (undp / undp_max ),( mrq / mrq_max ) , (totals /totals_max) ]

            var xaxis=new Array();
            var xx=['换手率', '市净率', '利润同比(%)', '未分利润',
                '市营率mrq', '总股本(亿）'];
            var max=[changerate_max,pb_max,profit_max,undp_max,mrq_max,totals_max];
            for(var i=0;i<6;i++){
                xaxis[i]={"t":xx[i],"f":max[i]};
            }



            $('#stockPolarChart').highcharts({
                chart: {
                    polar: true,
                    type: 'line',
                    spacingRight:0

                },


                title: {
                    text: '股票因子表现',
                    x: -10
                },
                pane: {
                    size: '90%'
                },
                xAxis: {
                    categories: xaxis,
                    tickmarkPlacement: 'on',
                    lineWidth: 0,
                    labels : {
                        formatter : function(){
                            return this.value.t;
                        }
                    }
                },
                yAxis: {
                    gridLineInterpolation: 'polygon',
                    lineWidth: 0,

                },
                tooltip: {
                    shared: true,
                    formatter : function(){
                        return '<span>'+(this.y*this.x.f).toFixed(2)+'</span>';
                    }
                },
                credits: {
                    enabled: false
                },
                legend: {
                    enabled:false
                    /*         align: 'right',
                     verticalAlign: 'top',
                     floating: true,
                     x:-70,
                     y: 70,
                     layout: 'vertical'*/
                },
                series: [{

                    data: numbers,
                    pointPlacement: 'on'
                }]
            });

        },
        error:function () {
            alert("网络不畅通！");
        }
    });



}

var dataChart;

function initPerStockKChart(Code,Name) {
    var myChart = echarts.init(document.getElementById('perStock_KChart'));
    var data0=[];


    var Kmap;

    var KmapNum;
    $.ajax({
        type:'get',
        url:'/oneStock/getKInfo',
        contentType:'application/json;charset=utf-8',
        data:{code:Code},
        success:function (data){
            var temp=data.MainK;


            KmapNum=data.MainKNum;
            data0=new Array(KmapNum);
            var dataAdd=[];//赋值给data0的数组
            var tempDate=[];
            var dataDate=[];//记录交易日时间
            var dataData=[];//记录交易日数据
            var time;
            dataDate=data.MainKDate;

            for(var i= 0 ;i< dataDate.length;i++)
            {

                time=dataDate[i];
                tempDate=dataDate[i].split('-');

                data0[i]=new Array();
                data0[i][0]   =tempDate[0]+"/"+tempDate[1]+"/"+tempDate[2];


                dataData=temp[time];

                for(var j = 0;j<4;j++)
                {

                    data0[i][j+1]   =parseFloat(dataData[j].toFixed(2));
                }

            }



            dataChart=splitData(data0);


            option = {
                title: {
                    text: Name,
                    left: 0
                },
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'line'
                    }
                },
                legend: {
                    data: ['日K', 'MA5', 'MA10', 'MA20', 'MA30']
                },
                grid: {
                    left: '10%',
                    right: '10%',
                    bottom: '15%'
                },
                xAxis: {
                    type: 'category',
                    data: dataChart.categoryData,
                    scale: true,
                    boundaryGap : false,
                    axisLine: {onZero: false},
                    splitLine: {show: false},
                    splitNumber: 20,
                    min: 'dataMin',
                    max: 'dataMax'
                },
                yAxis: {
                    scale: true,
                    splitArea: {
                        show: true
                    }
                },
                dataZoom: [
                    {
                        type: 'inside',
                        start: 50,
                        end: 100
                    },
                    {
                        show: true,
                        type: 'slider',
                        y: '90%',
                        start: 50,
                        end: 100
                    }
                ],
                series: [
                    {
                        name: '日K',
                        type: 'candlestick',
                        data: dataChart.values,
                        markPoint: {
                            label: {
                                normal: {
                                    formatter: function (param) {
                                        return param != null ? Math.round(param.value) : '';
                                    }
                                }
                            },
                            data: [
                                {
                                    name: 'XX标点',
                                    coord: ['2013/5/31', 2300],
                                    value: 2300,
                                    itemStyle: {
                                        normal: {color: 'rgb(41,60,85)'}
                                    }
                                },
                                {
                                    name: 'highest value',
                                    type: 'max',
                                    valueDim: 'highest'
                                },
                                {
                                    name: 'lowest value',
                                    type: 'min',
                                    valueDim: 'lowest'
                                },
                                {
                                    name: 'average value on close',
                                    type: 'average',
                                    valueDim: 'close'
                                }
                            ],
                            tooltip: {
                                formatter: function (param) {
                                    return param.name + '<br>' + (param.data.coord || '');
                                }
                            }
                        },
                        markLine: {
                            symbol: ['none', 'none'],
                            data: [
                                [
                                    {
                                        name: 'from lowest to highest',
                                        type: 'min',
                                        valueDim: 'lowest',
                                        symbol: 'circle',
                                        symbolSize: 10,
                                        label: {
                                            normal: {show: false},
                                            emphasis: {show: false}
                                        }
                                    },
                                    {
                                        type: 'max',
                                        valueDim: 'highest',
                                        symbol: 'circle',
                                        symbolSize: 10,
                                        label: {
                                            normal: {show: false},
                                            emphasis: {show: false}
                                        }
                                    }
                                ],
                                {
                                    name: 'min line on close',
                                    type: 'min',
                                    valueDim: 'close'
                                },
                                {
                                    name: 'max line on close',
                                    type: 'max',
                                    valueDim: 'close'
                                }
                            ]
                        }
                    },
                    {
                        name: 'MA5',
                        type: 'line',
                        data: calculateMA(5),
                        smooth: true,
                        lineStyle: {
                            normal: {opacity: 0.5}
                        }
                    },
                    {
                        name: 'MA10',
                        type: 'line',
                        data: calculateMA(10),
                        smooth: true,
                        lineStyle: {
                            normal: {opacity: 0.5}
                        }
                    },
                    {
                        name: 'MA20',
                        type: 'line',
                        data: calculateMA(20),
                        smooth: true,
                        lineStyle: {
                            normal: {opacity: 0.5}
                        }
                    },
                    {
                        name: 'MA30',
                        type: 'line',
                        data: calculateMA(30),
                        smooth: true,
                        lineStyle: {
                            normal: {opacity: 0.5}
                        }
                    },
                ]
            };


            // 使用刚指定的配置项和数据显示图表。
            myChart.setOption(option);

        },
        error:function (xhr) {
            alert("获取K线数据失败！");
            alert(xhr.status+"  "+xhr.statusText);
        }
    });



}

function calculateMA(dayCount) {
    var result = [];
    for (var i = 0, len = dataChart.values.length; i < len; i++) {
        if (i < dayCount) {
            result.push('-');
            continue;
        }
        var sum = 0;
        for (var j = 0; j < dayCount; j++) {
            sum += dataChart.values[i - j][1];
        }
        result.push(parseFloat((sum / dayCount).toFixed(2)));
    }
    return result;
}

function splitData(rawData) {
    var categoryData = [];

    var values = [];
    for (var i = 0; i < rawData.length; i++) {
        categoryData.push(rawData[i].splice(0, 1)[0]);
        values.push(rawData[i])
    }
    return {
        categoryData: categoryData,
        values: values
    };
}


var stockCode;//得到从别的界面点击进来的股票代码

var isColor;//决定心形标签的颜色  true代表红色  false代表白色

$(document).ready(function () {

    stockCode=getQueryString('code');
    document.getElementById('stockCode').innerHTML="<span class='glyphicon glyphicon-asterisk' style='color:#216343;'></span>"+stockCode;

  //  $('#waitData').modal();

        if(showvalf())
        {
            var username=localStorage.getItem("userName");
            var codeList=new Array();
            var flag=false;
            $.ajax({
                type:'get',
                url:'/oneStock/getPersonFocusForOne',
                contentType:'application/json;charset=utf-8',
                data:{userName:username},
                success:function (res){

                    codeList=res.codelist;

                    for(var i=0;i<codeList.length;i++)
                    {

                        if(codeList[i]==stockCode)
                        {
                            flag=true;
                            break;
                        }
                    }

                    if(flag)
                    {
                        $("#favoriteStock").css('color','#E4AFD3');
                        options={
                        title:""
                    };
                        $('#favoriteStock').tooltip(options);
                        isColor=true;

                    }
                    else
                    {
                        options={
                            title:"点击即可加入特别关注哦~"
                        };
                        $('#favoriteStock').tooltip(options);
                        isColor=false;
                    }

                },
                error:function () {
                    alert("网络不畅通！");
                }
            });

        }
        else
        {
            options={
                title:"点击即可加入特别关注哦~"
            };
            $('#favoriteStock').tooltip(options);

        }

        //var stockInfo=[];//记录一支股票的各种信息
        getOneStockInfo(stockCode);



        initStockScoreChart(stockCode);
        initStockPolarChart(stockCode);


});

var v1=false;
var v2=false;
function show_down(){
    if(document.getElementById('span_username').innerHTML == "用户") {
        if(v2==false) {
            $("#userDropdown").css('display', 'none');
            $("#noneuserDropdown").show();
            v2=true;
        }
        else{
            $("#noneuserDropdown").css('display', 'none');
            v2=false;
        }
    }
    else{
        if(v1==false) {
            $("#noneuserDropdown").css('display', 'none');
            $("#userDropdown").show();
            v1=true;
        }
        else{
            $("#userDropdown").css('display', 'none');
            $("#noneuserDropdown").css('display', 'none');
            v1=false;
        }
    }
}


$(document).on(
    {

        click:function () {
            if(showvalf())
            {
                var username=localStorage.getItem("userName");
                var stockCodes=new Array();
                stockCodes[0]=stockCode;
                var StockVO=
                    {
                        "stockCodes":stockCodes,
                        "userName":username
                    };
                //取消关注
                if(isColor)
                {

                    $.ajax({
                        type:'post',
                        url:'/oneStock/deleteFocusStock',
                        contentType:'application/json;charset=utf-8',
                        data:JSON.stringify(StockVO),
                        success:function (res){
                            if(res==true)
                            {

                                $('#removeSuccess').modal();
                                $("#favoriteStock").css('color','#FDF8F7');
                                options={
                                    title:"点击即可加入特别关注哦~"
                                };
                                $('#favoriteStock').tooltip(options);
                                isColor=false;

                            }

                        },
                        error:function (xhr) {
                            alert("删除失败！");
                            alert(xhr.status+" "+xhr.statusText);
                        }
                    });
                }
                //添加关注
                else
                {

                    $.ajax({
                        type:'post',
                        url:'/oneStock/addFocusStock',
                        contentType:'application/json;charset=utf-8',
                        data:JSON.stringify(StockVO),
                        success:function (res){
                            if(res==true)
                            {

                                $('#focusSuccess').modal();
                                $("#favoriteStock").css('color','#E4AFD3');
                                options={
                                    title:"点击即可取消关注"
                                };
                                $('#favoriteStock').tooltip(options);
                                isColor=true;

                            }

                        },
                        error:function (xhr) {
                            alert("添加失败！");
                            alert(xhr.status+" "+xhr.statusText);
                        }
                    });

                }

            }
            else
            {
                $('#failAdd').modal();
            }

        }
    },'#favoriteStock'

);

function getOneStockInfo(Code) {
    var stockInfo=[];
    $.ajax({
        type:'get',
        url:'/oneStock/getStockInfo',
        contentType:'application/json;charset=utf-8',
        data:{code:Code},
        success:function (data){

            stockInfo[0]=data.name;

            stockInfo[1]=data.area;

            stockInfo[2]=data.asset;

            stockInfo[3]=data.business;

            stockInfo[4]=data.date;

            initPerStockKChart(stockCode,stockInfo[0]);
            document.getElementById('stockName').innerHTML="<span class='glyphicon glyphicon-asterisk' style='color:#216343;'></span>"+stockInfo[0];
            document.getElementById('companyName').innerHTML=stockInfo[0];
            document.getElementById('companyAddress').innerHTML=stockInfo[1];
            document.getElementById('companyDate').innerHTML=stockInfo[4];
            document.getElementById('companyWork').innerHTML=stockInfo[3];
            document.getElementById('companyMoney').innerHTML=stockInfo[2];
           // return stockInfo;
        },
        error:function () {
            alert("网络不畅通！");
        }
    });


}