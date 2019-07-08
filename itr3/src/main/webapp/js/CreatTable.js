/**
 * Created by yipeng on 2017/6/7.
 */

function initStockTable(data) {


    for(var i=0;i<data.length;i++)
    {
        data[i].open=(data[i].open).toFixed(2);
        data[i].close=(data[i].close).toFixed(2);
        data[i].high=(data[i].high).toFixed(2);
        data[i].low=(data[i].low).toFixed(2);

        data[i].change=(data[i].change).toFixed(2)+"%";
    }


    var main_stockTable= $("#table_local").DataTable({

        //lengthMenu: [5, 10, 20, 30],//这里也可以设置分页，但是不能设置具体内容，只能是一维或二维数组的方式，所以推荐下面language里面的写法。
        //dom: '<l<"#topPlugin">frtip>',
        dom: 'lfrtip',
        paging: true,//分页
        ordering: true,//是否启用排序
        searching: true,//搜索
        select:true,
        processing: true,
        data:data,


        columns:[
            {data:'name'},
            {data:'code'},
            {data:'open'},
            {data:'close'},
            {data:'high'},
            {data:'low'},
            {data:'change'}


        ],

        "columnDefs":[{
            "targets" : 6,
            "createdCell":function (td, cellData, rowData, row, col) {

                if(cellData>"0") /*利用charCode进行比较*/
                {
                    $(td).css('color','red')
                }
                else
                {
                    $(td).css('color','green')
                }
            }

        },
            {
                "targets" :0,
                "createdCell":function (td, cellData, rowData, row, col) {
                    $(td).addClass("gotoSee");
                    $(td).click(function () {
                        var value = main_stockTable.cell(row,1).data();

                        var url = "oneStockInfo.html"+"?code="+value;
                        window.location.href = url;
                    })
                }

            },

        ],
        language: {
            lengthMenu: '<select class="form-control input-xsmall">' + '<option value="1">1</option>' + '<option value="10">10</option>' + '<option value="20">20</option>' + '<option value="30">30</option>' + '<option value="40">40</option>' + '<option value="50">50</option>' + '</select>条记录',//左上角的分页大小显示。
            search: '<span class="label label-success">搜索：</span>',//右上角的搜索文本，可以写html标签

            paginate: {//分页的样式内容。
                previous: "上一页",
                next: "下一页",
                first: "第一页",
                last: "最后"
            },

            zeroRecords: "没有内容",//table tbody内容为空时，tbody的内容。
            //下面三者构成了总体的左下角的内容。
            info: "总共_PAGES_ 页，显示第_START_ 到第 _END_ ，筛选之后得到 _TOTAL_ 条，初始_MAX_ 条 ",//左下角的信息显示，大写的词为关键字。
            infoEmpty: "0条记录",//筛选为空时左下角的显示。
            infoFiltered: ""//筛选之后的左下角筛选提示，
        },
        "oLanguage": {//下面是一些汉语翻译

            "sInfoEmtpy": "没有数据",
            "sProcessing": " 数据加载中...",

        },
        paging: true,
        pagingType: "full_numbers",//分页样式的类型



    });
    $("#table_local_filter input[type=search]").css({ width: "auto" });//右上角的默认搜索文本框，不写这个就超出去了。

    /*为表格中选中的股票进行跳转*/

    /*   $('#table_local tbody').on( 'click', 'tr', function () {
     /!*datatable和Datatable有不一样*!/
     main_stockTable.$('tr.selected').removeClass('selected');
     $(this).addClass('selected');
     var selected_row = main_stockTable.row('.selected').index();
     var value = main_stockTable.cell(selected_row,1).data();
     var url = "oneStockInfo.html"+"?code="+value;
     window.location.href = url;

     } );*/

    /* main_stockTable.buttons().container()
     .appendTo( $('.col-md-4:eq(0)', main_stockTable.table().container() ) );*/

}

$('#table_local tbody').on('click', 'tr', function() {
    $(this).toggleClass('selected');
});