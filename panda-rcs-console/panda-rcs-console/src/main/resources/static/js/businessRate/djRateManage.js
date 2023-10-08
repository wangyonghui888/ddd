/**
 * 折扣利率
 */
var pageCurr;
var form;
$(function () {
    layui.use('table', function () {
        var table = layui.table;
        form = layui.form;
        tableIns = table.render({
            elem: '#myTable',
            url: '/businessRate/listPageDj',
            method: 'post', //默认：get请求
            cellMinWidth: 80,
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
                , {field: 'businessId', type: 'checkbox'}
                , {field: 'businessId', title: '商户id', align: 'center'}
                , {field: 'businessCode', title: '商户code', align: 'center'}
                , {field: 'otsRate', title: 'ots折扣利率', align: 'center'}
                , {field: 'updateTime', title: '修改时间', align: 'center'}
                , {title: '操作', align: 'center', toolbar: '#optBar'}
            ]],
            done: function (res, curr, count) {
                pageCurr = curr;
            },
            toolbar: '#rateToolbar',
            defaultToolbar: []
        });

        //监听批量编辑弹窗
        table.on('toolbar(rateTable)', function (obj) {
            if (obj.event === 'batchEdit') {
                var checkData = table.checkStatus(obj.config.id)
                openBatchSet(checkData.data);
            }else if (obj.event === 'batchAdd') {
                openBatchAddSet();
            } else if (obj.event === 'rateAllSet') {
                openAllSet();
            } else if (obj.event === 'rateVirtualSet') {
                var checkData = table.checkStatus(obj.config.id)
                openVirtualSet(checkData.data);
            }
        });

        //监听编辑弹窗
        table.on('tool(rateTable)', function (obj) {
            var data = obj.data;
            if (obj.event === 'edit') {
                //编辑
                openEdit(data, "编辑");
            }
        });

        //监听编辑提交
        form.on('submit(rateSubmit)', function (data) {
            formSubmit(data);
            return false;
        });

        //监听批量提交
        form.on('submit(batchRateSubmit)', function (data) {
            batchSubmit(data);
            return false;
        });

        //监听批量提交
        form.on('submit(batchRateAddSubmit)', function (data) {
            batchAddSubmit(data);
            return false;
        });

        //监听虚拟提交
        form.on('submit(virtualSubmit)', function (data) {
            virtualSubmit(data);
            return false;
        });

        //监听通用提交
        form.on('submit(rateAllSubmit)', function (data) {
            setAllSubmit(data);
            return false;
        });


    });

    layui.use(['form', 'laydate'], function () {
        var form = layui.form;
        //TODO 数据校验
        //监听搜索框
        form.on('submit(searchSubmit)', function (data) {
            //重新加载table
            reload(data);
            return false;
        });

    });


});

function rateRedisSet() {
    layer.msg('你确定同步数据库数据到redis么？', {
        time: 3000,//2秒自动关闭
        btn: ['确定', '取消'],
        yes: function (index) {
            $.ajax({
                url: '/businessRate/initRedisBusinessRateDj',
                type: "Post",
                success: function (data) {
                    console.log(data);
                    layer.alert(data.message);
                },
                error: function (data) {
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
function formSubmit(obj) {
    if ($("#otsRate").val() > 0 && $("#otsRate").val() <= 1) {
        $.ajax({
            type: "POST",
            data: $("#editRate").serialize(),
            url: "/businessRate/saveDj",
            success: function (data) {
                if (data.code == 1) {
                    layer.alert(data.message, function () {
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
                layer.alert("操作请求错误，请您稍后再试", function () {
                    layer.closeAll();
                    //加载load方法
                    load(obj);//自定义
                });
            }
        });
    } else {
        layer.alert("折扣利率必须大于0，小于等于1");
    }
}

/**
 * 批量修改提交
 * @param obj
 */
function batchSubmit(obj) {
    if ($("#otsRateBatch").valueOf() > 0 || $("#otsRateBatch").val() <= 1) {
        $.ajax({
            type: "POST",
            data: $("#batchRateForm").serialize(),
            url: "/businessRate/batchUpdateDj",
            success: function (data) {
                if (data.code == 1) {
                    layer.alert(data.message, function () {
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
    } else {
        layer.alert("折扣利率必须大于0，小于等于1");
    }
}

/**
 * 批量修改提交
 * @param obj
 */
function batchAddSubmit(obj) {
    if ($("#otsRateAdd").valueOf() > 0 || $("#otsRateAdd").val() <= 1) {
        var param={
            businessId:obj.businessId,
            businessCode:obj.businessCode,
            otsRate:obj.otsRate,
        }
        $.ajax({
            type: "POST",
            data: $("#batchRateAddForm").serialize(),
            url: "/businessRate/batchAddDj",
            success: function (data) {
                if (data.code == 1) {
                    layer.alert(data.message, function () {
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
    } else {
        layer.alert("折扣利率必须大于0，小于等于1");
    }
}

function virtualSubmit(obj) {
    $.ajax({
        type: "POST",
        data: $("#virtualRateForm").serialize(),
        url: "/businessRate/batchVirtualUpdateDj",
        success: function (data) {
            if (data.code == 1) {
                layer.alert(data.message, function () {
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
function setAllSubmit(obj) {
    // if ($("#otsRateAll").val() > 0 && $("#otsRateAll").val() <= 1) {
        $.ajax({
            type: "POST",
            data: $("#rateAllForm").serialize(),
            url: "/businessRate/saveAllRateDj",
            success: function (data) {
                if (data.code == 1) {
                    layer.alert(data.message, function () {
                        layer.closeAll();
                    });
                } else {
                    layer.alert(data.message);
                }
            }
        });
    // } else {
    //     layer.alert("折扣利率必须大于0，小于等于1");
    // }
}

function openEdit(data, title) {
    $("#businessId").val(data.businessId);
    $("#businessCode").val(data.businessCode);
    $("#otsRate").val(data.otsRate);
    var pageNum = $(".layui-laypage-skip").find("input").val();
    $("#pageNum").val(pageNum);
    layer.open({
        type: 1,
        title: title,
        fixed: false,
        resize: false,
        shadeClose: true,
        area: ['550px'],
        content: $('#setRate'),
        end: function () {
            cleanMode();
        }
    });
}

function reload(obj) {
    //重新加载table
    tableIns.reload({
        where: obj.field
        , page: {
            curr: 1 //从当前页码开始
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

function cleanMode() {
    $("#businessId").val("");
    $("#businessCode").val("");
}

function batchCleanMode() {
    $("#otsRateAdd").val("");
    $("#busCodeAdd").val("");
    $("#busIdAdd").val("");
}

function virtualCleanMode() {
    $("#inVirtualRateBatch").val("");
}

function allCleanMode() {
    $("#otsRateAll").val("");
}

function closeMode() {
    layer.closeAll();
}

/**
 * 批量设置弹窗
 * @param obj
 */
function openBatchSet(obj) {
    var ids = [];
    for (var i = 0; i < obj.length; i++) {
        ids.push(obj[i].businessId)
    }
    $("#busIds").val(ids.join());
    layer.open({
        type: 1,
        title: "批量折扣利率设置",
        fixed: false,
        resize: false,
        shadeClose: true,
        area: ['600px'],
        content: $('#batchRateDiv'),
        end: function () {
            batchCleanMode();
        }
    });
}

/**
 * 批量设置弹窗
 * @param obj
 */
function openBatchAddSet(obj) {
    // var ids = [];
    // for (var i = 0; i < obj.length; i++) {
    //     ids.push(obj[i].businessId)
    // }
    // $("#busIds").val(ids.join());
    layer.open({
        type: 1,
        title: "新增商户折扣设置",
        fixed: false,
        resize: false,
        shadeClose: true,
        area: ['600px'],
        content: $('#batchRateAddDiv'),
        end: function () {
            batchCleanMode();
        }
    });
}

/**
 * 虚拟折扣设置
 * */
function openVirtualSet(obj) {
    var ids = [];
    for (var i = 0; i < obj.length; i++) {
        ids.push(obj[i].businessId)
    }
    $("#busIds_1").val(ids.join());
    layer.open({
        type: 1,
        title: "例外折扣利率批量设置",
        fixed: false,
        resize: false,
        shadeClose: true,
        area: ['600px'],
        content: $('#virtualRateDiv'),
        end: function () {
            virtualCleanMode();
        }
    });
}

/**
 * 通用设置弹窗
 * @param obj
 */
function openAllSet() {
    $.ajax({
        type: "GET",
        url: "/businessRate/getAllRateDj",
        success: function (data) {
            if (data.code == 1) {
                $("#otsRateAll").val(data.obj.otsRateAll)
            } else {
                layer.alert(data.message);
            }
        }
    });
    layer.open({
        type: 1,
        title: "默认折扣利率设置",
        fixed: false,
        resize: false,
        shadeClose: true,
        area: ['500px'],
        content: $('#rateAllDiv'),
        end: function () {
            batchCleanMode();
        }
    });

}