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
            url: '/match/getList',
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
                , {field: 'id', title: '赛事ID', align: 'left'}
                , {field: 'sport_id', title: '体育种类ID', align: 'left'}
                , {field: 'standard_tournament_id', title: '标准联赛ID', align: 'left'}
                , {field: 'third_match_id', title: '第三方比赛ID', align: 'left'}
                , {field: 'seconds_match_start', title: '开赛后的时间', align: 'left'}
                , {
                    field: 'pre_match_business', title: '赛事是否开放赛前盘', align: 'left', templet: function (d) {
                        if (d.pre_match_business = '1') {
                            return "开放"
                        }
                        if (d.pre_match_business = '0') {
                            return "不开放"
                        }
                    }
                }
                , {
                    field: 'live_odd_business', title: '赛事是否开放滚球', align: 'left', templet: function (d) {
                        if (d.live_odd_business = '1') {
                            return "开放"
                        }
                        if (d.live_odd_business = '0') {
                            return "不开放"
                        }
                    }
                }
                , {
                    field: 'operate_match_status', title: '比赛开盘标识', align: 'left', templet: function (d) {
                        if (d.operate_match_status == '0') {
                            return "未开盘"
                        }
                        if (d.operate_match_status == '1') {
                            return "开盘"
                        }
                        if (d.operate_match_status == '2') {
                            return "关盘"
                        }
                        if (d.operate_match_status == '3') {
                            return "封盘"
                        }
                    }
                }
                , {field: 'begin_time', title: '比赛开始时间', align: 'left'}
                , {
                    field: 'active', title: '比赛是否被激活', align: 'left', templet: function (d) {
                        if (d.active == '1') {
                            return "激活"
                        }
                        if (d.active == '0') {
                            return "未激活"
                        }
                    }
                }
                , {
                    field: 'pre_match_bet_status', title: '赛前盘下注状态', align: 'left', templet: function (d) {
                        if (d.pre_match_bet_status == '1') {
                            return "可下注"
                        }
                        if (d.pre_match_bet_status == '0') {
                            return "不可下注"
                        }
                    }
                }
                , {
                    field: 'live_odds_bet_status', title: '滚球下注状态. 滚球中使用:', align: 'left', templet: function (d) {
                        if (d.live_odds_bet_status == '1') {
                            return "可下注"
                        }
                        if (d.live_odds_bet_status == '0') {
                            return "不可下注"
                        }
                    }
                }
                , {field: 'match_status', title: '赛事状态: ', align: 'left'}
                , {
                    field: 'neutral_ground', title: '是否为中立场', align: 'left', templet: function (d) {
                        if (d.neutral_ground == '1') {
                            return "中立场"
                        }
                        if (d.neutral_ground == '0') {
                            return "非中立场"
                        }
                    }
                }
                , {field: 'match_position_name', title: '比赛场地名称 ', align: 'left'}
                , {field: 'data_source_code', title: '数据来源编码 ', align: 'left'}
                , {field: 'home_away_info', title: '赛事双方的对阵信息 ', align: 'left'}
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