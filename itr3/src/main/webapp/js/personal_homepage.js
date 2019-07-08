/**
 * Created by LZ on 2017/6/1.
 */
function save_personal_info(){
    var username=document.getElementById("name").value;
    var password=document.getElementById("pwd").value;
    var d={"userName":username,"password":password};
    $.ajax({
        type:'post',
        url :'/user/updateInfo',
        contentType:'application/json;charset=utf-8',
        data:JSON.stringify(d),
        success:function (data) {
            if(data==true) {
                alert("更新成功");
            }
            else{
                alert("更新失败");
            }
        },
        error:function(){
            alert("请求失败");
        }
    });
}

function get_person_info(){
    var username=document.getElementById("name").value;
    $.ajax({
        type:'get',
        url :'/user/getPersonInfo',
        data:{userName:username},
        success:function (data) {
            document.getElementById("pwd").value=data;
        },
        error:function(){
            alert("密码获取失败");
        }
    });
}

function delete_focus_stocks(array){
    var username=document.getElementById("name").value;
    var d={"stockCodes":array,"userName":username};
    $.ajax({
        type:'post',
        url :'/oneStock/deleteFocusStock',
        contentType:'application/json;charset=utf-8',
        data:JSON.stringify(d),
        success:function (data) {
            if(data==true) {
                alert("删除成功");
                $('#example').DataTable().rows('.selected').remove().draw(false);
            }
            else{
                alert("删除失败");
            }
        },
        error:function(){
            alert("删除请求失败");
        }
    });
}

function delete_history_strategies(array){
    var username=document.getElementById("name").value;
    var d={"strategyCodes":array,"userName":username};
    $.ajax({
        type:'post',
        url :'/strategy/deleteHistoryStrategies',
        contentType:'application/json;charset=utf-8',
        data:JSON.stringify(d),
        success:function (data) {
            alert("删除成功");
            $('#strategy_history').DataTable().rows('.selected').remove().draw( false );
        },
        error:function(){
            alert("删除失败");
        }
    });
}

function init_focus_stocks_table(focus_stocks){
    $('#example').DataTable( {
        "bInfo":false,
        /*
        "aaData": [
            [ "000001", "一股票","主板",4.2,22],
            [ "000002", "二股票","中小板",3.6,20],
            [ "000003", "三股票","创业板",3.9,31],
            [ "000060", "四股票","主板",2.3,27],
            [ "000005", "五股票","中小板",4.1,2],
            [ "000011", "六股票","创业板",4.2,15],
            [ "000007", "七股票","创业板",3.9,31],
            [ "000028", "八股票","主板",2.7,21],
            [ "000009", "九股票","中小板",4.4,4],
            [ "000010", "十股票","创业板",4.8,18]
        ],
        "aoColumns": [
            { "sTitle": "股票代码" },
            { "sTitle": "股票名称" },
            { "sTitle": "所属板块" },
            { "sTitle": "评分" },
            { "sTitle": "关注度" }
        ],*/

        "sPaginationType": "full_numbers",
        dom: 'lrtip',
        select: true,
        data:focus_stocks,
        columns:[
            {data:'code'},
            {data:'name'},
            {data:'business'},
            {data:'focusHeat'}
        ],
        "columnDefs":[
            {
                "targets" :0,
                "createdCell":function (td, cellData, rowData, row, col) {
                    $(td).addClass("gotoSee");
                    $(td).click(function () {
                        var value = $('#example').DataTable().cell(row,0).data();
                        alert(value);

                        var url = "oneStockInfo.html"+"?code="+value;
                        window.location.href = url;
                    })
                }

            }

        ],

        //国际化
        "oLanguage": {
            "sProcessing": "疯狂加载数据中.",
            "sLengthMenu": "每页显示 _MENU_ 条记录",
            "sZeroRecords": "抱歉， 没有找到",
            "sInfo": "从 _START_ 到 _END_ /共 _TOTAL_ 条数据",
            "sInfoEmpty": "没有数据",
            "sInfoFiltered": "(从 _MAX_ 条数据中检索)",
            "sZeroRecords": "没有检索到数据",
            "sSearch": "模糊查询:  ",
            "oPaginate": {
                "sFirst": "首页",
                "sPrevious": "前一页",
                "sNext": "后一页",
                "sLast": "尾页"
            },
            "sZeroRecords": "没有检索到数据",
            "sProcessing": "<img src='http://www.guoxk.com/guoxk/html/DataTables/loading.gif' />"
        }
    });
}

function init_strategy_history_table(strategy_history) {
    $('#strategy_history').DataTable( {
        "bInfo":false,
        /*
        "aaData": [
            [ "000001", "主板","动量策略","策略基准累计收益率",'<a class="btn btn-default btn-xs" href="#">查看结果</a>'],
            [ "000002", "中小板","动量策略","超额收益率/策略胜率",'<a class="btn btn-default btn-xs" href="#">查看结果</a>'],
            [ "000003", "创业板","动量策略","收益率分布",'<a class="btn btn-default btn-xs" href="#">查看结果</a>'],
            [ "000004", "自选","动量策略","策略基准累计收益率",'<a class="btn btn-default btn-xs" href="#">查看结果</a>'],
            [ "000005", "主板","动量策略","超额收益率/策略胜率",'<a class="btn btn-default btn-xs" href="#">查看结果</a>'],
            [ "000006", "中小板","均值回归","收益率分布",'<a class="btn btn-default btn-xs" href="#">查看结果</a>'],
            [ "000007", "创业板","均值回归","策略基准累计收益率",'<a class="btn btn-default btn-xs" href="#">查看结果</a>'],
            [ "000008", "自选","均值回归","超额收益率/策略胜率",'<a class="btn btn-default btn-xs" href="#">查看结果</a>'],
            [ "000009", "主板","均值回归","收益率分布",'<a class="btn btn-default btn-xs" href="#">查看结果</a>'],
            [ "000010", "中小板","均值回归","策略基准累计收益率",'<a class="btn btn-default btn-xs" href="#">查看结果</a>']
        ],
        "aoColumns": [
            { "sTitle": "策略编号" },
            { "sTitle": "应用范围" },
            { "sTitle": "策略类型" },
            { "sTitle": "图表类型" },
            { "sTitle": "查看结果" }
        ],*/
        "sPaginationType": "full_numbers",
        dom: 'lrtip',
        select: true,

        data:strategy_history,
        columns:[
            {data:'code'},
            {data:'bankuai'},
            {data:'type'},
            {data:'startDate'},
            {data:'endDate'},
            {data:'holdTime'},
            {data:'formTime'}
        ],
        "columnDefs": [
            {
                "targets": [7], // 目标列位置，下标从0开始
                "data": "result", // 数据列名
                "render": function() { // 返回自定义内容
                    return '<a class="btn btn-default btn-xs" href="#">查看结果</a>';
                }
            }
        ],

        //国际化
        "oLanguage": {
            "sProcessing": "疯狂加载数据中.",
            "sLengthMenu": "每页显示 _MENU_ 条记录",
            "sZeroRecords": "抱歉， 没有找到",
            "sInfo": "从 _START_ 到 _END_ /共 _TOTAL_ 条数据",
            "sInfoEmpty": "没有数据",
            "sInfoFiltered": "(从 _MAX_ 条数据中检索)",
            "sZeroRecords": "没有检索到数据",
            "sSearch": "模糊查询:  ",
            "oPaginate": {
                "sFirst": "首页",
                "sPrevious": "前一页",
                "sNext": "后一页",
                "sLast": "尾页"
            },
            "sZeroRecords": "没有检索到数据",
            "sProcessing": "<img src='http://www.guoxk.com/guoxk/html/DataTables/loading.gif' />"
        }
    });
}
