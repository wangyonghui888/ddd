/**
 * 用户管理
 */
var pageCurr;
var form;
var headerMap;
var allcols = [[
    {type: 'numbers'}
    , {field: 'userId', title: '用户ID', align: 'center', width: '5%'}
    , {field: 'createTime', title: '操作时间', align: 'center', width: '10%'}
    , {field: 'ip', title: '操作人IP', align: 'center', width: '8%'}
    , {field: 'url', title: '接口地址', align: 'center', width: '20%'}
    , {field: 'name', title: '接口描述', align: 'center'}
    , {field: 'requestVal', title: '请求内容', align: 'center'}
    , {field: 'title', title: '请求参数含义', align: 'center'}
    , {field: 'returnVal', title: '返回内容', align: 'center'}
    , {field: 'uuid', title: 'UUID', align: 'center'}
    , {field: 'code', title: '接口', align: 'center'}
    , {field: 'exeTime', title: '响应时间', align: 'center', width: '6%'}

]]
$(function () {
    layui.use('table', function () {
        var table = layui.table;
        form = layui.form;
        tableIns = table.render({
            elem: '#uesrList',
            url: '/logRecords/list',
            method: 'post', //默认：get请求
            cellMinWidth: 240,
            // width: 'auto', //宽度自动，100%父级宽度，超出宽度出现滚动
            // height: 380, //固定容器高度，内容超出高度出现滚动
            page: true,
            request: {
                pageName: 'pageNum', //页码的参数名称，默认：pageNum
                limitName: 'pageSize' //每页数据量的参数名，默认：pageSize
            },
            response: {
                statusName: 'code', //数据状态的字段名称，默认：code
                statusCode: 200, //成功的状态码，默认：0
                countName: 'totals', //数据总数的字段名称，默认：count
                dataName: 'list' //数据列表的字段名称，默认：data
            },
            cols: allcols,
            done: function (res, curr, count) {

                if (headerMap) {
                    var index = 0;
                    $.each(res.list, function (index, value) {
                        var requestVal = JSON.parse(value.requestVal);
                        $.each(headerMap, function (headKey, headValue) {
                            $.each(requestVal, function (key, value) {
                                if (key == headKey) {
                                    $("tr[data-index="+index+"] td[data-field=" + headKey + "]").find("div").text(value);
                                }
                            });
                        });
                        index++;
                    });
                }
                pageCurr = curr;
            }
        });

    });

    $("#url").on("input", function (e) {
        //获取input输入的值
        var value = e.delegateTarget.value;
        if (value.length > 0) {
            $("#requestDiv").show();
        } else {
            $("#requestDiv").hide();
        }
    });

    //搜索框
    layui.use(['form', 'laydate'], function () {
        var form = layui.form, layer = layui.layer
            , laydate = layui.laydate;
        //日期
        laydate.render({
            elem: '#startTime'
            , type: 'datetime'
            ,value: new Date(new Date().getTime()-1000*60*60*12)
            ,min:getRecentDay(-7)
        });
        laydate.render({
            elem: '#endTime'
            , type: 'datetime'
            ,min:getRecentDay(-7)
        });
        // 设置最小可选的日期
        function getRecentDay(day){
            var today = new Date();
            var targetday_milliseconds=today.getTime() + 1000*60*60*24*day;
            today.setTime(targetday_milliseconds);
            var tYear = today.getFullYear();
            var tMonth = today.getMonth();
            var tDate = today.getDate();
            tMonth = doHandleMonth(tMonth + 1);
            tDate = doHandleMonth(tDate);
            return tYear+"-"+tMonth+"-"+tDate;
        }
        function doHandleMonth(month){
            var m = month;
            if(month.toString().length == 1){
                m = "0" + month;
            }
            return m;
        }
        //TODO 数据校验
        //监听搜索框
        form.on('submit(searchSubmit)', function (data) {
            //重新加载table
            abs(data);
            return false;
        });
    });
});

//获取动态表头
function abs(obj) {
    $.ajax({
        type: "post",
        url: "/logRecords/getHeader",
        data: {
            code: $("#url").val(), //搜索条件
        },
        success: function (data) {
            if (data != null && Object.keys(data).length > 0) {
                $.each(data, function (k, n) {
                    var obj = {field: k, title: n, align: 'center'};
                    var index = -1;
                    for (var i = 0, len = allcols[0].length; i < len; i++) {
                        if (allcols[0][i].field == k) {
                            index = index + 1;
                        }
                    }
                    if (index < 0) {
                        allcols[0].push(obj);
                    }
                });
                headerMap = data;
            }
            load(obj);
        },
        error: function () {
        }
    });
}

function load(obj) {
    //重新加载table
    tableIns.reload({
        where: obj.field
        , page: {
            curr: 1 //从当前页码开始
        }
    });
}
