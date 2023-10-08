/**
 * 订单记录查询
 */
var pageCurr;
var form;
$(function () {
    layui.use('table', function () {
        var table = layui.table;
        form = layui.form;

        tableIns = table.render({
            elem: '#orderList',
            url: '/orderRecords/orderVirtualList',
            method: 'post', //默认：get请求
            cellMinWidth: 200,
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
            cols: [[
                {type: 'numbers'}
                , {field: 'orderNo', title: '订单号', align: 'left'}
                , {field: 'tenantId', title: '商户id', align: 'left'}
                , {field: 'uid', title: '用户id', align: 'left'}
                , {
                    field: 'orderStatus', title: '订单状态', align: 'left', templet: function (d) {
                    	//订单状态(0 待处理  1：成功  2：拒绝\r\n)
                        if (d.orderStatus == '0') {
                            return '待处理'
                        }
                        if (d.orderStatus == '1') {
                            return '成功'
                        }
                        if (d.orderStatus == '2') {
                            return '拒单'
                        }
                    }
                }
                , {field: 'createTime', title: '下注时间', align: 'left'}
                , {field: 'reason', title: '拒单原因', align: 'left'}
                , {field: 'seriesType', title: '过关方式', align: 'left'}
                , {field: 'orderAmountTotal', title: '金额', align: 'left'}
                , {field: 'ip', title: 'ip', align: 'left'}
                , {field: 'betNo', title: '详情单号', align: 'left'}
                , {field: 'matchId', title: '赛事id', align: 'left'}
                , {
                    field: 'isAcct', title: '是否接拒单', align: 'left', templet: function (d) {
                        if (d.isAcct == '1') {
                            return '是'
                        }
                        if (d.isAcct == '0') {
                            return '否'
                        }
                    }
                }
                , {field: 'remark', title: '备注', align: 'left'}

            ]],
            done: function (res, curr, count) {
                //如果是异步请求数据方式，res即为你接口返回的信息。
                //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
                //console.log(res);
                //得到当前页码
                /*console.log(curr);
                $("[data-field='userStatus']").children().each(function(){
                    if($(this).text()=='1'){
                        $(this).text("有效")
                    }else if($(this).text()=='0'){
                        $(this).text("失效")
                    }
                });*/

                $("[data-field='betTime']").children().each(function () {
                    if ($(this).text() != null && $(this).text().indexOf("时间") == -1) {
                        var a = parseFloat($(this).text());
                        var b = new Date(a);
                        $(this).text(transformTime(b));
                    }
                });
                //得到数据总量
                //console.log(count);
                pageCurr = curr;
            }
        });

        //监听工具条
        table.on('tool(userTable)', function (obj) {
            var data = obj.data;
            if (obj.event === 'del') {
                //删除
                delUser(data, data.id, data.sysUserName);
            } else if (obj.event === 'edit') {
                //编辑
                openUser(data, "编辑");
            } else if (obj.event === 'recover') {
                //恢复
                recoverUser(data, data.id);
            }
        });

        //监听提交
        form.on('submit(userSubmit)', function (data) {
            // TODO 校验
            formSubmit(data);
            return false;
        });
    });

    //搜索框
    layui.use(['form', 'laydate'], function () {
        var form = layui.form, layer = layui.layer
            , laydate = layui.laydate;
        //日期
        laydate.render({
            elem: '#startTime'
            , type: 'datetime'
        });
        laydate.render({
            elem: '#endTime'
            , type: 'datetime'
        });
        //TODO 数据校验
        //监听搜索框
        form.on('submit(searchSubmit)', function (data) {
            //重新加载table
            load(data);
            return false;
        });
        var startTime = transformTime(new Date().getTime()-1000*60*60*12, 'yyyy-MM-dd HH:mm:ss')
        $("#startTime").val(startTime);
    });
});

//提交表单
function formSubmit(obj) {
    $.ajax({
        type: "POST",
        data: $("#userForm").serialize(),
        url: "/user/setUser",
        success: function (data) {
            if (data.code == 1) {
                layer.alert(data.msg, function () {
                    layer.closeAll();
                    load(obj);
                });
            } else {
                layer.alert(data.msg);
            }
        },
        error: function () {
            layer.alert("操作请求错误，请您稍后再试", function () {
                layer.closeAll();
                //加载load方法
                load(obj);//自定义
            });
        }
    });
}


function load(obj) {
    //重新加载table
    tableIns.reload({
        where: obj.field
        , page: {
            curr: pageCurr //从当前页码开始
        }
    });
}

function transformTime(timestamp = +new Date()) {
    if (timestamp) {
        var time = new Date(timestamp);
        var y = time.getFullYear(); //getFullYear方法以四位数字返回年份
        var M = time.getMonth() + 1; // getMonth方法从 Date 对象返回月份 (0 ~ 11)，返回结果需要手动加一
        var d = time.getDate(); // getDate方法从 Date 对象返回一个月中的某一天 (1 ~ 31)
        var h = time.getHours(); // getHours方法返回 Date 对象的小时 (0 ~ 23)
        var m = time.getMinutes(); // getMinutes方法返回 Date 对象的分钟 (0 ~ 59)
        var s = time.getSeconds(); // getSeconds方法返回 Date 对象的秒数 (0 ~ 59)
        return y + '-' + M + '-' + d + ' ' + h + ':' + m + ':' + s;
    } else {
        return '';
    }
}