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
            url:'/matchFlowing/getEventList',
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
                {type:'numbers'}
                ,{field:'linkId', title:'链路Id',align:'left'}
                ,{field:'oid', title:'id',align:'left'}
                ,{field:'standardMatchId', title: '标准赛事的id',align:'left'}
                ,{field:'eventTime', title: '事件发生时间',align:'left'}
                ,{field:'insertTime', title: 'insertTime',align:'left'}
                ,{field:'modifyTime', title: '修改时间',align:'left'}
                ,{field:'eventCode', title: '事件编码',align:'left'}
                ,{field:'sportId', title:'体育种类id',align:'left'}
                ,{field:'canceled', title: '是否被取消',align:'left'}
                ,{field:'dataSourceCode', title: 'dataSourceCode',align:'left'}
                ,{field:'extraInfo', title: '扩展信息',align:'left'}
                ,{field:'homeAway', title: '主客场',align:'left'}
                ,{field:'secondNum', title: '当前第几局',align:'left'}
                ,{field:'firstT1', title: '盘主队比分',align:'left'}
                ,{field:'firstT2', title: '盘客队比分',align:'left'}
                ,{field:'secondT1', title: '局主队比分',align:'left'}
                ,{field:'secondT2', title: '局客队比分',align:'left'}
                ,{field:'firstNum', title: '当前盘数',align:'left'}
                ,{field:'matchPeriodId', title: '比赛阶段id',align:'left'}
                ,{field:'player1Id', title: '球员1的id',align:'left'}
                ,{field:'player1Name', title: '球员1的名称',align:'left'}
                ,{field:'player2Id', title: '球员2的id',align:'left'}
                ,{field:'player2Name', title: '球员2的名称',align:'left'}
                ,{field:'secondsFromStart', title: '距离比赛开始多少秒',align:'left'}
                ,{field:'standardTeamId', title: '标准球队 ID',align:'left'}
                ,{field:'t1', title: '主队数量',align:'left'}
                ,{field:'t2', title: '客队数量',align:'left'}
                ,{field:'thirdEventId', title: '第三方数据源提供的该事件id',align:'left'}
                ,{field:'thirdMatchId', title: '第三方数据源提供的该事件id.',align:'left'}
                ,{field:'thirdMatchSourceId', title: '比赛在数据源中的ID',align:'left'}
                ,{field:'thirdTeamId', title: '第三方球队id',align:'left'}
                ,{field:'remark', title: '备注',align:'left'}
                ,{field:'periodRemainingSeconds', title: '当前节阶段剩余时间',align:'left'}
                ,{field:'addition1', title: '扩展字段1',align:'left'}
                ,{field:'addition2', title: '扩展字段2',align:'left'}
                ,{field:'addition3', title: '扩展字段3',align:'left'}
                ,{field:'addition4', title: '扩展字段4',align:'left'}
                ,{field:'addition5', title: '扩展字段5',align:'left'}
                ,{field:'addition6', title: '扩展字段6',align:'left'}
                ,{field:'addition7', title: '扩展字段7',align:'left'}
                ,{field:'addition8', title: '扩展字段8',align:'left'}
                ,{field:'addition9', title: '扩展字段9',align:'left'}
                ,{field:'addition10', title: '扩展字段10',align:'left'}
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
        table.on('tool(userTable)', function(obj){
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