/**
 * Created by yipeng on 2017/5/17.
 */


/*通过逻辑层的getPlateMarketTemper()方法得到数据*/
/*数组第一个值默认为x轴，第二个值默认为y轴*/
var dataUp;//记录上涨股票数比下跌股票数多的板块

var dataDown;//记录相反的板块

/*通过逻辑层getMarketTemper()方法得到数据*/
var data;

var changeData;

var name=[];//记录板块名称
var isUp=[];//记录是涨是跌
var upNum=[];//记录上涨股票个数
var downNum=[];//记录下跌股票个数
var plateName=[];

var today;
var myDate;
var restime;
var transtime;

function initPopChart(chartDataUp,chartDataDown,findTime) {





    var popChart=echarts.init(document.getElementById('MarketPopChart'));

/*    var schema = [
        {name: '涨停股票数', index: 0, text: '涨停股票数'},
        {name: '涨幅', index: 1, text: '涨幅≥5%数'},
        {name: '跌停', index: 2, text: '跌停股票数'},
        {name: '跌幅', index: 3, text: '跌幅≥5%数'}

    ];*/
    var schema = [
        {name: '上涨股票数', index: 0, text: '上涨股票数'},
        {name: '下跌股票数', index: 1, text: '下跌股票数'}

    ];

    var itemStyle = {
        normal: {
            opacity: 0.8,
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowOffsetY: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
    };

    option = {
        backgroundColor: '#A7E2B7',
        color: [
            '#dd4444','#4781C2'
        ],

        grid: {
            x: '10%',
            x2: 150,
            y: '18%',
            y2: '10%'
        },
        tooltip: {
            padding: 10,
            backgroundColor: '#222',
            borderColor: '#777',
            borderWidth: 1,
            formatter: function (params) {
                var value = params.value;
                var valueIndex=params.seriesIndex;
                return '<div style="border-bottom: 1px solid rgba(255,255,255,.3); font-size: 18px;padding-bottom: 7px;margin-bottom: 7px">'
                    + value[2] + ' ' + '</div>'
                    + schema[0].text + '：' + value[0] + '<br>'
                    + schema[1].text + '：' + value[1] + '<br>';
/*

                if(valueIndex==0)
                {
                    return '<div style="border-bottom: 1px solid rgba(255,255,255,.3); font-size: 18px;padding-bottom: 7px;margin-bottom: 7px">'
                        + value[2] + ' ' + '</div>'
                        + schema[0].text + '：' + value[0] + '<br>'
                        + schema[1].text + '：' + value[1] + '<br>';

                }
                else
                {
                    return '<div style="border-bottom: 1px solid rgba(255,255,255,.3); font-size: 18px;padding-bottom: 7px;margin-bottom: 7px">'
                        + value[2] + ' ' + '</div>'
                        + schema[2].text + '：' + value[0] + '<br>'
                        + schema[3].text + '：' + value[1] + '<br>';
                }
*/

            }
        },
        xAxis: {
            type: 'value',

            show:false,
            nameGap: 16,
            nameTextStyle: {
                color: '#fff',
                fontSize: 14
            },
            max: 31,
            splitLine: {
                show: false
            },
            axisLine: {
                lineStyle: {
                    color: '#eee'
                }
            }
        },
        yAxis: {
            type: 'value',

            show:false,
            nameLocation: 'end',
            nameGap: 20,
            nameTextStyle: {
                color: '#fff',
                fontSize: 16
            },
            axisLine: {
                lineStyle: {
                    color: '#eee'
                }
            },
            splitLine: {
                show: false
            }
        },
        visualMap: [
            {
                seriesIndex:0,//映射第一个数据源（涨停） 改为涨
                left: 'right',
                top: '10%',
                dimension: 0,
                min: 0,
                max: 40,
                itemWidth: 30,
                itemHeight: 120,
                calculable: true,
                precision: 0.1,
                text: ['上涨股票个数'],
                textGap: 30,
                textStyle: {
                    color: '#fff'
                },
                inRange: {
                    symbolSize: [10, 70]
                },
                outOfRange: {
                    symbolSize: [10, 70],
                    color: ['rgba(255,255,255,.2)']
                },
                controller: {
                    inRange: {
                        color: ['#c23531']
                    },
                    outOfRange: {
                        color: ['#444']
                    }
                }
            },
            {
                seriesIndex:1,//映射第二个数据源（跌停） 改为跌
                left: 'left',
                top: '10%',
                dimension: 1,//映射第二个数据源的第二个维度
                min: 0,
                max: 40,
                itemWidth: 30,
                itemHeight: 120,
                calculable: true,
                precision: 0.1,
                text: ['下跌股票个数'],
                textGap: 30,
                textStyle: {
                    color: '#fff'
                },
                inRange: {
                    symbolSize: [10, 70]
                },
                outOfRange: {
                    symbolSize: [10, 70],
                    color: ['rgba(255,255,255,.2)']
                },
                controller: {
                    inRange: {
                        color: ['#4781C2']
                    },
                    outOfRange: {
                        color: ['#444']
                    }
                }
            }
        /*    {
                seriesIndex:0,//映射第一个数据源
                left: 'right',
                bottom: '5%',
                dimension: 1,//映射数据的第二个维度
                min: 0,
                max: 40,
                itemHeight: 120,
                calculable: true,
                precision: 0.1,
                text: ['涨幅≥5%股票数'],
                textGap: 30,
                textStyle: {
                    color: '#fff'
                },
                inRange: {
                    colorLightness: [1, 0.5]
                },
                outOfRange: {
                    color: ['rgba(255,255,255,.2)']
                },
                controller: {
                    inRange: {
                        color: ['#c23531']
                    },
                    outOfRange: {
                        color: ['#444']
                    }
                }
            },
            {
                seriesIndex:1,//映射第二个数据源（跌幅）
                left: 'left',
                bottom: '5%',
                dimension: 1,//映射数据的第二个维度
                min: 0,
                max: 40,
                itemHeight: 120,
                calculable: true,
                precision: 0.1,
                text: ['下跌股票数'],
                textGap: 30,
                textStyle: {
                    color: '#fff'
                },
                inRange: {
                    colorLightness: [1, 0.5]
                },
                outOfRange: {
                    color: ['rgba(255,255,255,.2)']
                },
                controller: {
                    inRange: {
                        color: ['#4781C2']
                    },
                    outOfRange: {
                        color: ['#444']
                    }
                }
            }*/
        ],
        series: [
            {
                name: '上涨板块',
                type: 'scatter',
                itemStyle: itemStyle,
                data: chartDataUp
            },
            {
                name:'下跌板块',
                type: 'scatter',
                itemStyle: itemStyle,
                data: chartDataDown
            }

        ]
    };
    popChart.setOption(option);

    popChart.on('click',function (params) {
        var selectValue=params.value;


        window.location.href="plateStock.html?plate="+selectValue[2]+"&findTime="+findTime;
    })
}


function initBarChart(chartData) {

    var barChart=echarts.init(document.getElementById('MarketBarChart'));
    var dataAxis = ['涨停股', '涨幅≥5', '跌停股','跌幅≥5','总交易量(十亿元)'];



    var color1=new echarts.graphic.LinearGradient(
        0, 0, 0, 1,
        [
            {offset: 0, color: '#9FDFA7'},
            {offset: 0.5, color: '#6CC44F'},
            {offset: 1, color: '#4FC34B'}
        ]
    );

    var color2=new echarts.graphic.LinearGradient(
        0, 0, 0, 1,
        [
            {offset: 0, color: '#C95E87'},
            {offset: 0.5, color: '#B83D6A'},
            {offset: 1, color: '#B83D60'}
        ]
    );

    var color3=new echarts.graphic.LinearGradient(
        0, 0, 0, 1,
        [
            {offset: 0, color: '#CACC66'},
            {offset: 0.5, color: '#C6C85B'},
            {offset: 1, color: '#BEC144'}
        ]
    );

    option = {

        backgroundColor:'#94DBDB',

        tooltip:{
            trigger:'axis',
            /*有时间研究一下如何把数字变色*/
            formatter:'{b0}:{c0}'
        },

        title: {
            text: '市场温度',
            subtext: 'Data from Sina',
            textStyle:{
                color:"#fff"
            },
            subtextStyle:{
                color:"#fff"
            },
        },
        xAxis: {
            data: dataAxis,
            axisLabel: {

                textStyle: {
                    color: '#fff'
                }
            },
            axisTick: {
                show: false
            },
            axisLine: {
                show: false
            },
            z: 10
        },
        yAxis: {
            axisLine: {
                show: false
            },
            axisTick: {
                show: false
            },
            axisLabel: {
                textStyle: {
                    color: '#fff'
                }
            }
        },
        dataZoom: [
            {
                type: 'inside'
            }
        ],
        series: [

            {
                type: 'bar',
                itemStyle: {
                    normal: {
                        color: function (params) {
                            var colorList = [
                               color2,color2,color1,color1,color3

                            ];
                            return colorList[params.dataIndex]
                        }
                        

                    }

                },
                data: chartData
            }
        ]
    };

    barChart.setOption(option);
}

function show_result() {
    data=new Array();
    name=new Array();
    isUp=new Array();
    upNum=new Array();
    downNum=new Array();
    changeData=new Array();
    plateName=new Array();

    var findDate=$("#findDate").val();

    myDate=new Date();
    today=myDate.toLocaleDateString();
    transtime=today.split("/");
    restime=transtime[0]+"-0"+transtime[1]+"-"+transtime[2];

    var timeVO={
        "date":findDate
    };

    if(findDate=="")
    {
        $('#noneSelect').modal();
    }

    else if(findDate>restime)
    {
        $('#illegalTime').modal();

    }

    else
    {
        /*调用逻辑层findMarketTemper(String beginDate, String endDate);

         findPlateMarketTemper(String beginDate, String endDate);方法*/

        $.ajax({
            type:'get',
            url:'/marketTemper/findMarketTemper',
            dataType : "json",
            data:{datest:findDate},
            success:function (res){
                changeData[0]=res.limitUp;//涨停股
                changeData[1]=res.limitDown;//跌停股
                changeData[2]=res.notBad;//涨幅
                changeData[3]=res.notGood;//跌幅
                changeData[4]=(res.totalVolume)/1000000000;//成交量


                if(changeData[4]>0)
                {
                    initBarChart(changeData);
                    dataUp=new Array();
                    dataDown=new Array();
                    var countUp=0;
                    var countDown=0;
                    $.ajax({
                        type:'get',
                        url:'/marketTemper/findPlateMarketTemper',
                        contentType:'application/json;charset=utf-8',
                        dataType : "json",
                        data:{datest:findDate},
                        success:function (res){
                            name=res.name;
                            plateName=name.split(",");
                            isUp=res.isUp;

                            upNum=res.upNum;

                            downNum=res.downNum;

                            for(var i =0 ;i< plateName.length ;i++)
                            {
                                if(isUp[i])
                                {
                                    dataUp[countUp]=new Array();
                                    dataUp[countUp]=[upNum[i],downNum[i],plateName[i]];
                                    countUp++;
                                }
                                else
                                {
                                    dataDown[countDown]=new Array();
                                    dataDown[countDown]=[upNum[i],downNum[i],plateName[i]];
                                    countDown++;
                                }
                            }


                            initPopChart(dataUp,dataDown,findDate);
                        },
                        error:function (xhr) {
                            alert("请求失败！");
                            alert(xhr.status+"  "+xhr.statusText);
                        }
                    });

                }
                else
                {
                    $('#nonePurchase').modal();

                }

            },
            error:function (xhr) {
                alert("请求失败！");
                alert(xhr.status+"  "+xhr.statusText);
            }
        });


    }

}

$(document).ready(function () {
      showvalf();
      myDate=new Date();
      today=myDate.toLocaleDateString();
      transtime=today.split("/");
      restime=transtime[0]+"-0"+transtime[1]+"-"+(transtime[2]-1);
        initMarket(restime);
});

function initMarket(today) {
    data=new Array();
    name=new Array();
    isUp=new Array();
    upNum=new Array();
    downNum=new Array();
    plateName=new Array();


    $.ajax({
        type:'get',
        url:'/marketTemper/findMarketTemper',
        data:{datest:today},
        dataType : "json",
        success:function (res){
            data[0]=res.limitUp;//涨停股
            data[1]=res.limitDown;//跌停股
            data[2]=res.notBad;//涨幅
            data[3]=res.notGood;//跌幅
            data[4]=(res.totalVolume)/1000000000;//成交量
            //交易日
            if(data[4]>0)
            {
                var countUp=0;
                var countDown=0;
                dataUp=new Array();
                dataDown=new Array();
                initBarChart(data);
                $.ajax({
                    type:'get',
                    url:'/marketTemper/getPlateMarketTemper',
                    dataType : "json",
                    success:function (res){
                        name=res.name;
                        plateName=name.split(",");


                        isUp=res.isUp;

                        upNum=res.upNum;

                        downNum=res.downNum;

                        for(var i =0 ;i< plateName.length ;i++)
                        {
                            if(isUp[i])
                            {
                                dataUp[countUp]=new Array();
                                dataUp[countUp]=[upNum[i],downNum[i],plateName[i]];
                                countUp++;
                            }
                            else
                            {
                                dataDown[countDown]=new Array();
                                dataDown[countDown]=[upNum[i],downNum[i],plateName[i]];
                                countDown++;
                            }
                        }


                        initPopChart(dataUp,dataDown,today);
                    },
                    error:function (xhr) {
                        alert("市场温度计请求失败！");
                        alert(xhr.status+"  "+xhr.statusText);
                    }
                });

            }

            else
            {
                $('#nonePurchase').modal();

            }

        },
        error:function (xhr) {
            alert("市场温度计请求失败！");
            alert(xhr.status+"  "+xhr.statusText);
        }
    });


}