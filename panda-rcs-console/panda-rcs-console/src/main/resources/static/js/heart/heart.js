/**
 * 用户管理
 */
var pageCurr;
var form;
$(function() {
    layui.use('table', function(){
        var table = layui.table;
        form = layui.form;

        tableIns=table.render({
            elem: '#uesrList',
            url:'/heart/show',
            method: 'post', //默认：get请求
            cellMinWidth: 200,
            page: true,
            request: {
                pageName: 'pageNum', //页码的参数名称，默认：pageNum
                limitName: 'pageSize' //每页数据量的参数名，默认：pageSize
            },
            response:{
                statusName: 'code', //数据状态的字段名称，默认：code
                statusCode: 200, //成功的状态码，默认：0
                countName: 'totals', //数据总数的字段名称，默认：count
                dataName: 'list' //数据列表的字段名称，默认：data
            },
            cols: [[
                {type: 'numbers'}
                , {field: 'serverName', title: '服务名', align: 'left'}
                , {field: 'startNode', title: '启动实例数', align: 'left'}
                , {field: 'instanceCount', title: '存活实例数', align: 'left'}
                , {field: 'healthStatus', title: '健康状态', align: 'left', templet: function (d) {
                	if(parseInt(d.healthStatus) == 1) return '正常';
                    return  '异常';
                }}
                , {field: 'sumDiff', title: '未消费数量', align: 'left'}
                , {field: 'tps', title: 'MQ TPS', align: 'left'}
                , {field: 'currentTime', title: '上次心跳时间', align: 'left', templet: function (d) {
                        return  transformTime2(d.currentTime);
                    }}

            ]],
            done: function(res, curr, count){
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
                $("[data-field='modifyTime']").children().each(function(){
                    if($(this).text()!=null&&$(this).text().indexOf("时间")==-1){
                        var a = parseFloat($(this).text());
                        var b = new Date(a);
                        $(this).text(transformTime(b));
                    }
                });
                $("[data-field='eventTime']").children().each(function(){
                    if($(this).text()!=null&&$(this).text().indexOf("时间")==-1){
                        var a = parseFloat($(this).text());
                        var b = new Date(a);
                        $(this).text(transformTime(b));
                    }
                });
                //得到数据总量
                //console.log(count);
                pageCurr=curr;
            }
        });

        //监听工具条
        table.on('tool(usersTable)', function(obj){
            var data = obj.data;
            if(obj.event === 'del'){
                //删除
                delUser(data,data.id,data.sysUserName);
            } else if(obj.event === 'edit'){
                //编辑
                openUser(data,"编辑");
            }else if(obj.event === 'recover'){
                //恢复
                recoverUser(data,data.id);
            }
        });

        //监听工具条
        table.on('row(usersTable)', function(obj){
            location.href="service?serverName="+obj.data.serverName;
        });

        //监听提交
        form.on('submit(userSubmit)', function(data){
            // TODO 校验
            formSubmit(data);
            return false;
        });
    });

    //搜索框
    layui.use(['form','laydate'], function(){
        var form = layui.form ,layer = layui.layer
            ,laydate = layui.laydate;
        //日期
        laydate.render({
            elem: '#startTime'
            ,type: 'datetime'
        });
        laydate.render({
            elem: '#endTime'
            ,type: 'datetime'
        });
        //TODO 数据校验
        //监听搜索框
        form.on('submit(searchSubmit)', function(data){
            //重新加载table
            load(data);
            return false;
        });
    });
});

//提交表单
function formSubmit(obj){
    $.ajax({
        type: "POST",
        data: $("#userForm").serialize(),
        url: "/user/setUser",
        success: function (data) {
            if (data.code == 1) {
                layer.alert(data.msg,function(){
                    layer.closeAll();
                    load(obj);
                });
            } else {
                layer.alert(data.msg);
            }
        },
        error: function () {
            layer.alert("操作请求错误，请您稍后再试",function(){
                layer.closeAll();
                //加载load方法
                load(obj);//自定义
            });
        }
    });
}

//开通用户
function addUser(){
    openUser(null,"开通用户");
}
function openUser(data,title){
    var roleId = null;
    if(data==null || data==""){
        $("#id").val("");
    }else{
        $("#id").val(data.id);
        $("#username").val(data.sysUserName);
        $("#mobile").val(data.userPhone);
        roleId = data.roleId;
    }
    var pageNum = $(".layui-laypage-skip").find("input").val();
    $("#pageNum").val(pageNum);
    $.ajax({
        url:'/role/getRoles',
        dataType:'json',
        async: true,
        success:function(data){
            $.each(data,function(index,item){
                if(!roleId){
                    var option = new Option(item.roleName,item.id);
                }else {
                    var option = new Option(item.roleName,item.id);
                    // // 如果是之前的parentId则设置选中
                    if(item.id == roleId) {
                        option.setAttribute("selected",'true');
                    }
                }
                $('#roleId').append(option);//往下拉菜单里添加元素
                form.render('select'); //这个很重要
            })
        }
    });

    layer.open({
        type:1,
        title: title,
        fixed:false,
        resize :false,
        shadeClose: true,
        area: ['550px'],
        content:$('#setUser'),
        end:function(){
            cleanUser();
        }
    });
}

function delUser(obj,id,name) {
    var currentUser=$("#currentUser").html();
    if(null!=id){
        if(currentUser==id){
            layer.alert("对不起，您不能执行删除自己的操作！");
        }else{
            layer.confirm('您确定要删除'+name+'用户吗？', {
                btn: ['确认','返回'] //按钮
            }, function(){
                $.post("/user/updateUserStatus",{"id":id,"status":0},function(data){
                    if (data.code == 1) {
                        layer.alert(data.msg,function(){
                            layer.closeAll();
                            load(obj);
                        });
                    } else {
                        layer.alert(data.msg);
                    }
                });
            }, function(){
                layer.closeAll();
            });
        }
    }
}
//恢复
function recoverUser(obj,id) {
    if(null!=id){
        layer.confirm('您确定要恢复吗？', {
            btn: ['确认','返回'] //按钮
        }, function(){
            $.post("/user/updateUserStatus",{"id":id,"status":1},function(data){
                if (data.code == 1) {
                    layer.alert(data.msg,function(){
                        layer.closeAll();
                        load(obj);
                    });
                } else {
                    layer.alert(data.msg);
                }
            });
        }, function(){
            layer.closeAll();
        });
    }
}

function load(obj){
    //重新加载table
    tableIns.reload({
        where: obj.field
        , page: {
            curr: pageCurr //从当前页码开始
        }
    });
}

function cleanUser(){
    $("#username").val("");
    $("#mobile").val("");
    $("#password").val("");
    $('#roleId').html("");
}

// function transformTime(timestamp = +new Date()) {
//     if (timestamp) {
//         var time = new Date(timestamp);
//         var y = time.getFullYear(); //getFullYear方法以四位数字返回年份
//         var M = time.getMonth() + 1; // getMonth方法从 Date 对象返回月份 (0 ~ 11)，返回结果需要手动加一
//         var d = time.getDate(); // getDate方法从 Date 对象返回一个月中的某一天 (1 ~ 31)
//         var h = time.getHours(); // getHours方法返回 Date 对象的小时 (0 ~ 23)
//         var m = time.getMinutes(); // getMinutes方法返回 Date 对象的分钟 (0 ~ 59)
//         var s = time.getSeconds(); // getSeconds方法返回 Date 对象的秒数 (0 ~ 59)
//         M = formatDate(M);
//         d = formatDate(d);
//         h = formatDate(h);
//         m = formatDate(m);
//         s = formatDate(s);
//         return y + '-' + M + '-' + d + ' ' + h + ':' + m + ':' + s;
//     } else {
//         return '';
//     }
// }
function transformTime2(timestamp) {
    if (timestamp) {
        var time = new Date(parseInt(timestamp));
        var y = time.getFullYear(); //getFullYear方法以四位数字返回年份
        var M = time.getMonth() + 1; // getMonth方法从 Date 对象返回月份 (0 ~ 11)，返回结果需要手动加一
        var d = time.getDate(); // getDate方法从 Date 对象返回一个月中的某一天 (1 ~ 31)
        var h = time.getHours(); // getHours方法返回 Date 对象的小时 (0 ~ 23)
        var m = time.getMinutes(); // getMinutes方法返回 Date 对象的分钟 (0 ~ 59)
        var s = time.getSeconds(); // getSeconds方法返回 Date 对象的秒数 (0 ~ 59)
        M = formatDate(M);
        d = formatDate(d);
        h = formatDate(h);
        m = formatDate(m);
        s = formatDate(s);
        return y + '-' + M + '-' + d + ' ' + h + ':' + m + ':' + s;
    } else {
        return '';
    }
}
function formatDate(data) {
    data = data + "";
    if (data.length < 2){
        data = "0" + data;
    }
    return data;
}

function init_heart_search(){
    $.ajax({
        type: "GET",
        async:false,
        url: "/heart/init",
        success: function (data) {
            if (data) {
                //重新加载table
                tableIns.reload({
                    page: {
                        curr: pageCurr //从当前页码开始
                    }
                });
            }
        },
        error: function (data) {
            var msg = "操作请求错误，请您稍后再试";
            if (data.status == 200) {
                msg = "没有数据。请更换查询条件";
            }
            layer.alert(msg, function () {
                layer.closeAll();
                clearFrom();
            });
        }
    });
}