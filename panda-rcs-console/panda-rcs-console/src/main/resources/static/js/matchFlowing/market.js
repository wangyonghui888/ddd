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
            url: '/matchFlowing/getMarketList',
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
                , {field: 'linkId', title: '链路id', align: 'left'}
                , {field: 'placeNumId', title: '位置id', align: 'left', event: 'queryOdds'}
                , {field: 'oid', title: 'id', align: 'left'}
                , {field: 'standardMatchInfoId', title: '标准比赛ID', align: 'left'}
                , {field: 'insertTime', title: 'insertTime', align: 'left'}
                , {field: 'modifyTime', title: '修改时间', align: 'left'}
                , {field: 'marketCategoryId', title: '标准玩法id', align: 'left'}
                , {field: 'marketType', title: '盘口类型', align: 'left'}
                , {field: 'addition1', title: '附加字段1', align: 'left'}
                , {field: 'addition2', title: '附加字段2', align: 'left'}
                , {field: 'addition3', title: '附加字段3', align: 'left'}
                , {field: 'addition4', title: '附加字段4', align: 'left'}
                , {field: 'addition5', title: '附加字段5', align: 'left'}
                , {field: 'dataSourceCode', title: 'dataSourceCode:', align: 'left'}
                , {field: 'status', title: '盘口状态', align: 'left'}
                , {field: 'thirdMarketSourceStatus', title: '数据源盘口状态', align: 'left'}
                , {field: 'placeNumStatus', title: '位置状态', align: 'left'}
                , {field: 'paStatus', title: 'pa状态', align: 'left'}
                , {field: 'paStatusReason', title: 'pa状态原因', align: 'left'}
                , {field: 'managerConfirmPrize', title: '人工确认开奖', align: 'left'}
                , {field: 'thirdMarketSourceId', title: 'thirdMarketSourceId', align: 'left'}
                , {field: 'i18nNames', title: '语言', align: 'left'}
                , {field: 'remark', title: '备注', align: 'left'}
                , {field: 'extraInfo', title: '扩展参数', align: 'left'}
                , {field: 'myRemark', title: 'myRemark', align: 'left'}
                , {field: 'oddsMetric', title: '赔率差', align: 'left'}
                , {field: 'versionId', title: '版本号', align: 'left'}
                , {field: 'placeNum', title: '盘口排序', align: 'left'}
                , {field: 'marketHeadGap', title: '盘口差', align: 'left'}
                , {field: 'marketSource', title: 'marketSource', align: 'left'}
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
                $("[data-field='modifyTime']").children().each(function () {
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
        var startTime = transformTime(new Date().getTime() - 1000 * 60 * 60 * 12, 'yyyy-MM-dd HH:mm:ss')
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

function openUser(data, title) {
    var roleId = null;
    if (data == null || data == "") {
        $("#id").val("");
    } else {
        $("#id").val(data.id);
        $("#username").val(data.sysUserName);
        $("#mobile").val(data.userPhone);
        roleId = data.roleId;
    }
    var pageNum = $(".layui-laypage-skip").find("input").val();
    $("#pageNum").val(pageNum);
    $.ajax({
        url: '/role/getRoles',
        dataType: 'json',
        async: true,
        success: function (data) {
            $.each(data, function (index, item) {
                if (!roleId) {
                    var option = new Option(item.roleName, item.id);
                } else {
                    var option = new Option(item.roleName, item.id);
                    // // 如果是之前的parentId则设置选中
                    if (item.id == roleId) {
                        option.setAttribute("selected", 'true');
                    }
                }
                $('#roleId').append(option);//往下拉菜单里添加元素
                form.render('select'); //这个很重要
            })
        }
    });

    layer.open({
        type: 1,
        title: title,
        fixed: false,
        resize: false,
        shadeClose: true,
        area: ['550px'],
        content: $('#setUser'),
        end: function () {
            cleanUser();
        }
    });
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
