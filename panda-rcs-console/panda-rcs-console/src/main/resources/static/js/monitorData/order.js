/**
 * 用户管理
 */
var pageCurr;
var form;
$(function () {
    layui.use('table', function () {
        var table = layui.table;
        form = layui.form;

        tableIns = table.render({
            elem: '#uesrList',
            url: '/monitorData/monitorData/order',
            method: 'post', //默认：get请求
            where: {"dataType": 0},
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
                , {field: 'monitorCode', title: '类型', align: 'left'}
                , {field: 'allCount', title: '总请求数', align: 'left'}
                , {field: 'value100', title: '100ms以内', align: 'left',templet: function (d) {
                        return toPercent(d.value100/d.allCount);
                    }}
                , {field: 'value200', title: '200ms以内', align: 'left',templet: function (d) {
                        return toPercent(d.value200/d.allCount);
                    }}
                , {field: 'value500', title: '500ms以内', align: 'left',templet: function (d) {
                        return toPercent(d.value500/d.allCount);
                    }}
                , {field: 'value1000', title: '1000ms以内', align: 'left',templet: function (d) {
                        return toPercent(d.value1000/d.allCount);
                    }}
                , {field: 'value2000', title: '2000ms以内', align: 'left',templet: function (d) {
                        return toPercent(d.value2000/d.allCount);
                    }}
            ]],
            done: function (res, curr, count) {
                $("[data-field='monitorCode']").children().each(function(){
                    if($(this).text()=='RPC_CATEGORY_SET'){
                        $(this).text("玩法集获取")
                    }else if($(this).text()=='RPC_CATEGORY_SET_PLAY'){
                        $(this).text("根据玩法集获取玩法")
                    }else if($(this).text()=='RPC_QUERY_MAXBET'){
                        $(this).text("投注最大最小值")
                    }else if($(this).text()=='RPC_SAVE_ORDER'){
                        $(this).text("投注校验")
                    }else if($(this).text()=='MQ_ORDER_PREDICT_CALC'){
                        $(this).text("货量")
                    }else if($(this).text()=='MQ_ORDER_INFO_WS'){
                        $(this).text("注单推送")
                    }
                });
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
            } else if (obj.event === 'queryOdds') {
                queryOdds(data);
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
        var startTime = transformTime(new Date().getTime() - 1000 * 60 * 60 * 1, 'yyyy-MM-dd HH:mm:ss')
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

//开通用户
function addUser() {
    openUser(null, "开通用户");
}

function c100(obj) {
    return obj.allCount/obj.value100+'%';
}

function c200(obj) {
    return obj.allCount/obj.value200+'%';
}

function c500(obj) {
    return obj.allCount/obj.value500+'%';
}

function c1000(obj) {
    return obj.allCount/obj.value1000+'%';
}

function c2000(obj) {
    return obj.allCount/obj.value2000+'%';
}


function delUser(obj, id, name) {
    var currentUser = $("#currentUser").html();
    if (null != id) {
        if (currentUser == id) {
            layer.alert("对不起，您不能执行删除自己的操作！");
        } else {
            layer.confirm('您确定要删除' + name + '用户吗？', {
                btn: ['确认', '返回'] //按钮
            }, function () {
                $.post("/user/updateUserStatus", {"id": id, "status": 0}, function (data) {
                    if (data.code == 1) {
                        layer.alert(data.msg, function () {
                            layer.closeAll();
                            load(obj);
                        });
                    } else {
                        layer.alert(data.msg);
                    }
                });
            }, function () {
                layer.closeAll();
            });
        }
    }
}

//查询对应参数
function queryOdds(obj) {
    $.post("/matchFlowing/getMarketOddsByParam", {"linkId": obj.linkId, "marketId": obj.oid}, function (data) {
        //layer.alert(JSON.stringify(data));
        var content = JSON.stringify(data);
        content = content.replaceAll('Value', '<a style="color:#FF0000";>Value</a>').replaceAll('Id', '<a style="color:#FF0000";>Id</a>').replaceAll(',', '<a style="color:#FF0000";>,</a>');
        content = '<div style="padding: 20px; line-height: 20px; background-color: #FFFFFF; color: #111; font-weight:200">' + content + '</div>'
        layer.open({
            type: 1
            , area: ['1800px', '280px']
            , shadeClose: true
            , id: 'LAY_layuipro' //设定一个id，防止重复弹出
            , btnAlign: 'c'
            , moveType: 1 //拖拽模式，0或者1
            , content: content

        });
    });
}

//查询赔率
function recoverUser(obj, id) {
    if (null != id) {
        layer.confirm('您确定要恢复吗？', {
            btn: ['确认', '返回'] //按钮
        }, function () {
            $.post("/user/updateUserStatus", {"id": id, "status": 1}, function (data) {
                if (data.code == 1) {
                    layer.alert(data.msg, function () {
                        layer.closeAll();
                        load(obj);
                    });
                } else {
                    layer.alert(data.msg);
                }
            });
        }, function () {
            layer.closeAll();
        });
    }
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

function cleanUser() {
    $("#username").val("");
    $("#mobile").val("");
    $("#password").val("");
    $('#roleId').html("");
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

function toPercent(point){
    var str=Number(point*100).toFixed(1);
    str+="%";
    return str;
}