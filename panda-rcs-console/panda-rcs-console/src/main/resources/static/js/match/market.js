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
            url: '/match/marketList',
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
            cols: [[
                {type: 'numbers'}
                , {field: 'id', title: '盘口ID', align: 'left'}
                , {field: 'sport_id', title: '运动种类id', align: 'left'}
                , {field: 'standard_tournament_id', title: '所属联赛ID', align: 'left'}
                , {field: 'standard_match_info_id', title: '标准比赛ID', align: 'left'}
                , {field: 'market_category_id', title: '标准玩法id', align: 'left'}
                , {
                    field: 'market_type', title: '盘口类型', align: 'left', templet: function (d) {
                        if (d.market_type = '1') {
                            return "赛前盘"
                        }
                        if (d.market_type = '0') {
                            return "滚球盘"
                        }
                    }
                }
                , {field: 'odds_value', title: '盘口显示值', align: 'left'}
                , {field: 'odds_name', title: '盘口名称', align: 'left'}
                , {field: 'data_source_code', title: '数据来源', align: 'left'}
                , {field: 'status', title: '盘口状态', align: 'left'}
                , {field: 'scope_id', title: '盘口阶段Id', align: 'left'}
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

//恢复
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