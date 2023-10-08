/**
 * 折扣利率
 */
var pageCurr;
var form;
$(function() {
    layui.use('table', function(){
        var table = layui.table;
        form = layui.form;
        tableIns=table.render({
            elem: '#myTable',
            url:'/businessRate/listPage',
            method: 'post', //默认：get请求
            cellMinWidth: 80,
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
                {type:'numbers',width:'5%'}
                ,{field:'businessId',type:'checkbox',width:'5%'}
                ,{field:'businessId', title:'商户id',align:'center',width:'13%'}
                ,{field:'businessCode', title:'商户code',align:'center',width:'7%'}
                ,{field:'mtsRate', title:'mts折扣利率',align:'center',width:'7%'}
                ,{field:'ctsRate', title:'cts折扣利率',align:'center',width:'7%'}
                ,{field:'gtsRate', title:'gts折扣利率',align:'center',width:'7%'}
                ,{field:'otsRate', title:'ots折扣利率',align:'center',width:'7%'}
                ,{field:'rtsRate', title:'rts折扣利率',align:'center',width:'7%'}
                ,{field:'virtualRate', title: '虚拟折扣利率',align:'center',width:'7%'}
                ,{field:'updateTime', title: '修改时间',align:'center',width:'13%'}
                ,{title:'操作',align:'center', toolbar:'#optBar'}
            ]],
            done: function (res, curr, count) {
                pageCurr = curr;
            },
            toolbar:'#rateToolbar',
            defaultToolbar: []
        });

        //监听批量编辑弹窗
        table.on('toolbar(rateTable)', function(obj){
            if (obj.event === 'batchEdit'){
                var checkData = table.checkStatus(obj.config.id)
                openBatchSet(checkData.data);
            }else if (obj.event === 'rateAllSet') {
                openAllSet();
            }else if(obj.event === 'rateVirtualSet'){
                var checkData = table.checkStatus(obj.config.id)
                openVirtualSet(checkData.data);
            }
        });

        //监听编辑弹窗
        table.on('tool(rateTable)', function(obj){
            var data = obj.data;
            if(obj.event === 'edit'){
                //编辑
                openEdit(data,"编辑");
            }
        });

        //监听编辑提交
        form.on('submit(rateSubmit)', function(data){
            formSubmit(data);
            return false;
        });

        //监听批量提交
        form.on('submit(batchRateSubmit)', function(data){
            batchSubmit(data);
            return false;
        });

        //监听虚拟提交
        form.on('submit(virtualSubmit)', function(data){
            virtualSubmit(data);
            return false;
        });

        //监听通用提交
        form.on('submit(rateAllSubmit)', function(data){
            setAllSubmit(data);
            return false;
        });




    });

    layui.use(['form','laydate'], function(){
        var form = layui.form;
        //TODO 数据校验
        //监听搜索框
        form.on('submit(searchSubmit)', function(data){
            //重新加载table
            reload(data);
            return false;
        });

    });


});

function rateRedisSet(){
    layer.msg('你确定同步数据库数据到redis么？', {
        time: 3000 ,//2秒自动关闭
        btn: ['确定', '取消'],
        yes: function(index){
            $.ajax({
                url:'/businessRate/initRedisBusinessRate',
                type:"Post",
                success:function(data){
                    console.log(data);
                    layer.alert(data.message);
                },
                error:function(data){
                    layer.msg("请求失败");
                    layer.alert(data.message);
                }
            });
            layer.close(index);
        }
    });
}
/**
 * 编辑提交
 * @param data
 */
function formSubmit(obj){
    $.ajax({
        type: "POST",
        data: $("#editRate").serialize(),
        url: "/businessRate/save",
        success: function (data) {
            if (data.code == 1) {
                layer.alert(data.message,function(){
                    layer.closeAll();
                    obj.field.businessId = $("#busId").val();
                    obj.field.businessCode = $("#busCode").val();
                    delete obj.field.pageNum;
                    load(obj);
                });
            } else {
                layer.alert(data.message);
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

/**
 * 批量修改提交
 * @param obj
 */
function batchSubmit(obj){
    if ((obj.field.virtualRateAll == null || obj.field.virtualRateAll == "") &&
        (obj.field.rtsRateAll == null || obj.field.rtsRateAll == "") &&
        (obj.field.otsRateAll == null || obj.field.otsRateAll == "") &&
        (obj.field.gtsRateAll == null || obj.field.gtsRateAll == "") &&
        (obj.field.ctsRateAll == null || obj.field.ctsRateAll == "") &&
        (obj.field.mtsRateAll == null || obj.field.mtsRateAll == "")) {
        layer.alert("至少填入一项！");
        return
    }
    $.ajax({
        type: "POST",
        data: $("#batchRateForm").serialize(),
        url: "/businessRate/batchUpdate",
        success: function (data) {
            if (data.code == 1) {
                layer.alert(data.message,function(){
                    layer.closeAll();
                    obj.field.businessId = $("#busId").val();
                    obj.field.businessCode = $("#busCode").val();
                    delete obj.field.pageNum;
                    load(obj);
                });
            } else {
                layer.alert(data.message);
            }
        }
    });
}
function virtualSubmit(obj){
    console.log(1,obj.field.virtualRateAll)
    if ((obj.field.virtualRateAll == null || obj.field.virtualRateAll == "") &&
        (obj.field.rtsRateAll == null || obj.field.rtsRateAll == "") &&
        (obj.field.otsRateAll == null || obj.field.otsRateAll == "") &&
        (obj.field.gtsRateAll == null || obj.field.gtsRateAll == "") &&
        (obj.field.ctsRateAll == null || obj.field.ctsRateAll == "") &&
        (obj.field.mtsRateAll == null || obj.field.mtsRateAll == "")) {
        layer.alert("至少填入一项！");
        return
    }
    $.ajax({
        type: "POST",
        data: $("#virtualRateForm").serialize(),
        url: "/businessRate/batchVirtualUpdate",
        success: function (data) {
            if (data.code == 1) {
                layer.alert(data.message,function(){
                    layer.closeAll();
                    obj.field.businessId = $("#busId").val();
                    obj.field.businessCode = $("#busCode").val();
                    delete obj.field.pageNum;
                    load(obj);
                });
            } else {
                layer.alert(data.message);
            }
        }
    });
}

/**
 * 通用修改提交
 * @param obj
 */
function setAllSubmit(obj){
    if ((obj.field.mtsRate == null || obj.field.mtsRate == "") &&
        (obj.field.ctsRate == null || obj.field.ctsRate == "") &&
        (obj.field.gtsRate == null || obj.field.gtsRate == "") &&
        (obj.field.otsRate == null || obj.field.otsRate == "") &&
        (obj.field.rtsRate == null || obj.field.rtsRate == "") &&
        (obj.field.virtualRateAll == null || obj.field.virtualRateAll == "")) {
        layer.alert("至少填入一项！");
        return
    }
    $.ajax({
        type: "POST",
        data: $("#rateAllForm").serialize(),
        url: "/businessRate/saveAllRate",
        success: function (data) {
            if (data.code == 1) {
                layer.alert(data.message,function(){
                    layer.closeAll();
                });
            } else {
                layer.alert(data.message);
            }
        }
    });
}

function openEdit(data,title){
    $("#businessId").val(data.businessId);
    $("#businessCode").val(data.businessCode);
    $("#mtsRate").val(data.mtsRate);
    $("#ctsRate").val(data.ctsRate);
    $("#gtsRate").val(data.gtsRate);
    $("#otsRate").val(data.otsRate);
    $("#rtsRate").val(data.rtsRate);
    $("#virtualRate").val(data.virtualRate);
    var pageNum = $(".layui-laypage-skip").find("input").val();
    $("#pageNum").val(pageNum);
    layer.open({
        type:1,
        title: title,
        fixed:false,
        resize :false,
        shadeClose: true,
        area: ['550px'],
        content:$('#setRate'),
        end:function(){
            cleanMode();
        }
    });
}

function reload(obj){
    //重新加载table
    tableIns.reload({
        where: obj.field
        , page: {
            curr: 1 //从当前页码开始
        }
    });
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

function cleanMode(){
    $("#businessId").val("");
    $("#businessCode").val("");
}

function batchCleanMode(){
    $("#mtsRateBatch").val("");
    $("#ctsRateBatch").val("");
    $("#gtsRateBatch").val("");
    $("#otsRateBatch").val("");
    $("#rtsRateBatch").val("");
    $("#virtualRateBatch").val("");
}
function virtualCleanMode(){
    $("#inVirtualRateBatch").val("");
}
function allCleanMode(){
    $("#mtsRateAll").val("");
    $("#ctsRateAll").val("");
    $("#gtsRateAll").val("");
    $("#otsRateAll").val("");
    $("#rtsRateAll").val("");
    $("#virtualRateAll").val("");
}

function closeMode() {
    layer.closeAll();
}

/**
 * 批量设置弹窗
 * @param obj
 */
function openBatchSet(obj){
    var ids = [];
    for (var i = 0 ;i<obj.length ;i++){
        ids.push(obj[i].businessId)
    }
    $("#busIds").val(ids.join());
    layer.open({
        type:1,
        title: "批量折扣利率设置",
        fixed:false,
        resize :false,
        shadeClose: true,
        area: ['600px'],
        content:$('#batchRateDiv'),
        end:function(){
            batchCleanMode();
        }
    });
}
/**
 * 虚拟折扣设置
 * */
function openVirtualSet(obj){
    var ids = [];
    for (var i = 0 ;i<obj.length ;i++){
        ids.push(obj[i].businessId)
    }
    $("#busIds_1").val(ids.join());
    layer.open({
        type:1,
        title: "例外折扣利率设置",
        fixed:false,
        resize :false,
        shadeClose: true,
        area: ['600px'],
        content:$('#virtualRateDiv'),
        end:function(){
            virtualCleanMode();
        }
    });
}

/**
 * 通用设置弹窗
 * @param obj
 */
function openAllSet(){
    $.ajax({
        type: "GET",
        url: "/businessRate/getAllRate",
        success: function (data) {
            if (data.code == 1) {
                $("#mtsRateAll").val(data.obj.mtsRateAll)
                $("#ctsRateAll").val(data.obj.ctsRateAll)
                $("#gtsRateAll").val(data.obj.gtsRateAll)
                $("#otsRateAll").val(data.obj.otsRateAll)
                $("#rtsRateAll").val(data.obj.rtsRateAll)
                $("#virtualRateAll").val(data.obj.virtualRateAll)
            } else {
                layer.alert(data.message);
            }
        }
    });
    layer.open({
        type:1,
        title: "默认折扣利率设置",
        fixed:false,
        resize :false,
        shadeClose: true,
        area: ['500px'],
        content:$('#rateAllDiv'),
        end:function(){
            batchCleanMode();
        }
    });

}