/**
 * Created by LZ on 2017/6/3.
 */


var myDate;
var today;
var transtime;
var restime;


function get_result(){
    var stock1=document.getElementById("s1").value;
    var stock2=document.getElementById("s2").value;
    var start=document.getElementById("dp1").value;
    var end=document.getElementById("dp2").value;
    var flag1=false;
    var flag2=false;
    if(document.getElementById("stock1").innerHTML=='股票名称'){
        flag1=true;
    }
    if(document.getElementById("stock2").innerHTML=='股票名称'){
        flag2=true;
    }
    if(!stock1||!stock2||!start||!end){
        $('#inputEmpty').modal();
        return;
    }

    if(end>restime)
    {
        $('#illegalTime').modal();
        return;
    }
    if(start>end){

       $('#illegalTime').modal();
        return;
    }
    var d={"beginDate":start,"endDate":end,"stock1":stock1,"stock2":stock2,"stock1IsName":flag1,"stock2IsName":flag2};
    $.ajax({
        type:'post',
        url :'/oneStock/compareTwoStock',
        dataType : "json",
        contentType:'application/json;charset=utf-8',
        data:JSON.stringify(d),
        success:function (res) {
            if(res==null){
                $('#failData').modal();
                return;
            }
            if(res.p3s1max.toString()=="shit"){
                $('#plateDif').modal();
                return;
            }
            $('#successData').modal();
            var t1s1=res.p1s1.split(',');
            var t1s2=res.p1s2.split(',');
            var t2s1=res.p2s1.split(',');
            var t2s2=res.p2s2.split(',');
            var p1s1=new Array();
            var p1s2=new Array();
            var p2s1=new Array();
            var p2s2=new Array();
            var p3s1=new Array();
            var p3s2=new Array();
            var x=new Array();
            var xx=['换手率', '市净率', '利润同比(%)', '未分利润',
                '市营率mrq', '总股本(亿）'];
            for(var i=0;i<6;i++){
                p3s1[i]=res.p3s1[i]/res.p3s1max[i];
                p3s2[i]=res.p3s2[i]/res.p3s2max[i];
            }
            for(var i=0;i<6;i++){
                x[i]={"t":xx[i],"f":res.p3s1max[i]};
            }
            for(var i=0;i<t1s1.length/2;i++){
                p1s1[i]=new Array();
                var d=t1s1[2*i].split('-');
                p1s1[i][0]=Date.UTC(d[0]*1,d[1]*1-1,d[2]*1);
                p1s1[i][1]=t1s1[2*i+1]*1;
            }
            for(var i=0;i<t1s2.length/2;i++){
                p1s2[i]=new Array();
                var d=t1s2[2*i].split('-');
                p1s2[i][0]=Date.UTC(d[0]*1,d[1]*1-1,d[2]*1);
                p1s2[i][1]=t1s2[2*i+1]*1;
            }
            for(var i=0;i<t2s1.length/2;i++){
                p2s1[i]=new Array();
                var d=t2s1[2*i].split('-');
                p2s1[i][0]=Date.UTC(d[0]*1,d[1]*1-1,d[2]*1);
                p2s1[i][1]=t2s1[2*i+1]*1;
            }
            for(var i=0;i<t2s2.length/2;i++){
                p2s2[i]=new Array();
                var d=t2s2[2*i].split('-');
                p2s2[i][0]=Date.UTC(d[0]*1,d[1]*1-1,d[2]*1);
                p2s2[i][1]=t2s2[2*i+1]*1;
            }
            var cov1=res.s1cov*100000;
            var cov2=res.s2cov*100000;
            $('#example').DataTable( {
                "bInfo":false,
                "bFilter": false,
                "scrollY":        "200px",
                "scrollCollapse": true,
                "paging":         false,
                "aaData": [
                    [ "最低值",res.s1low, res.s2low],
                    [ "最高值",res.s1high, res.s2high],
                    [ "涨/跌幅",res.s1change, res.s2change],
                    [ "对数收益方差(e-5)",cov1.toFixed(2), cov2.toFixed(2)]
                ],
                "aoColumns": [
                    { "sTitle": "比较项" },
                    { "sTitle": "股票一" },
                    { "sTitle": "股票二" },
                ]
            } );
            $(function () {
                $('#p1').highcharts({
                    chart: {
                        zoomType: 'x'
                    },
                    title: {
                        text: '收盘价走势图'
                    },
                    subtitle: {
                        text: document.ontouchstart === undefined ?
                            '鼠标拖动可以进行缩放' : '手势操作进行缩放'
                    },
                    xAxis: {
                        type: 'datetime',
                        dateTimeLabelFormats: {
                            millisecond: '%H:%M:%S.%L',
                            second: '%H:%M:%S',
                            minute: '%H:%M',
                            hour: '%H:%M',
                            day: '%m-%d',
                            week: '%m-%d',
                            month: '%Y-%m',
                            year: '%Y'
                        }
                    },
                    tooltip: {
                        dateTimeLabelFormats: {
                            millisecond: '%H:%M:%S.%L',
                            second: '%H:%M:%S',
                            minute: '%H:%M',
                            hour: '%H:%M',
                            day: '%Y-%m-%d',
                            week: '%m-%d',
                            month: '%Y-%m',
                            year: '%Y'
                        }
                    },
                    yAxis: {
                        title: {
                            text: '收盘价'
                        }
                    },
                    legend: {
                        enabled: false
                    },
                    plotOptions: {
                        area: {
                            fillColor: {
                                linearGradient: {
                                    x1: 0,
                                    y1: 0,
                                    x2: 0,
                                    y2: 1
                                },
                                stops: [
                                    [0, Highcharts.getOptions().colors[0]],
                                    [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                                ]
                            },
                            marker: {
                                radius: 2
                            },
                            lineWidth: 1,
                            states: {
                                hover: {
                                    lineWidth: 1
                                }
                            },
                            threshold: null
                        }
                    },
                    series: [{
                        name: '股票一',
                        data: p1s1
                    }, {
                        name: '股票二',
                        data: p1s2
                    }]
                });
            });
            $(function () {
                $('#p2').highcharts({
                    chart: {
                        zoomType: 'x'
                    },
                    title: {
                        text: '对数收益率走势图'
                    },
                    subtitle: {
                        text: document.ontouchstart === undefined ?
                            '鼠标拖动可以进行缩放' : '手势操作进行缩放'
                    },
                    xAxis: {
                        type: 'datetime',
                        dateTimeLabelFormats: {
                            millisecond: '%H:%M:%S.%L',
                            second: '%H:%M:%S',
                            minute: '%H:%M',
                            hour: '%H:%M',
                            day: '%m-%d',
                            week: '%m-%d',
                            month: '%Y-%m',
                            year: '%Y'
                        }
                    },
                    tooltip: {
                        dateTimeLabelFormats: {
                            millisecond: '%H:%M:%S.%L',
                            second: '%H:%M:%S',
                            minute: '%H:%M',
                            hour: '%H:%M',
                            day: '%Y-%m-%d',
                            week: '%m-%d',
                            month: '%Y-%m',
                            year: '%Y'
                        }
                    },
                    yAxis: {
                        title: {
                            text: '对数收益率'
                        }
                    },
                    legend: {
                        enabled: false
                    },
                    plotOptions: {
                        area: {
                            fillColor: {
                                linearGradient: {
                                    x1: 0,
                                    y1: 0,
                                    x2: 0,
                                    y2: 1
                                },
                                stops: [
                                    [0, Highcharts.getOptions().colors[0]],
                                    [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                                ]
                            },
                            marker: {
                                radius: 2
                            },
                            lineWidth: 1,
                            states: {
                                hover: {
                                    lineWidth: 1
                                }
                            },
                            threshold: null
                        }
                    },
                    series: [{
                        name: '股票一',
                        data: p2s1
                    }, {
                        name: '股票二',
                        data: p2s2
                    }]
                });
            });
            $(function () {
                $('#p3').highcharts({
                    chart: {
                        polar: true,
                        type: 'line'
                    },
                    title: {
                        text: '综合指标',
                        x:-50

                    },
                    pane: {
                        size: '80%'
                    },
                    xAxis: {
                        categories: x,
                        tickmarkPlacement: 'on',
                        lineWidth: 0,
                        labels : {
                            formatter : function(){
                                return this.value.t;
                            }
                        }
                    },
                    credits: {
                        enabled: false
                    },
                    yAxis: {
                        gridLineInterpolation: 'polygon',
                        lineWidth: 0,
                        max:1
                    },
                    tooltip: {
                        shared: true,
                        formatter : function(){
                            return '<span>'+(this.y*this.x.f).toFixed(2)+'</span>';
                        }
                    },
                    legend: {
                        align: 'right',
                        verticalAlign: 'top',
                        y: 70,
                        layout: 'vertical'
                    },
                    series: [{
                        name: '股票一',
                        data: p3s1,
                        pointPlacement: 'on'
                    }, {
                        name: '股票二',
                        data: p3s2,
                        pointPlacement: 'on'
                    }]
                });
            });
        },
        error:function(){
            alert("请求失败，获取不到双方数据！");
        }
    });
}


$(document).ready( function () {
 //   showvalf();
    myDate=new Date();
    today=myDate.toLocaleDateString();
    transtime=today.split("/");
    restime=transtime[0]+"-0"+transtime[1]+"-"+(transtime[2]-1);
});
