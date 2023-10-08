/**
 * 角色管理
 */
let pageCurr;
let form;

$(function() {
    layui.use(['laydate', 'laypage', 'layer', 'table', 'carousel', 'upload', 'element', 'slider', 'form'], function(){
        let table = layui.table;
        let upload = layui.upload;
        form = layui.form;
        tableIns=table.render({
            elem: '#dictList',
            url: '/dict/getDictList',
            method: 'get',
            cellMinWidth: 80,
            page: true,
            request: {
                pageName: 'pageNum', //页码的参数名称，默认：pageNum
                limitName: 'pageSize' //每页数据量的参数名，默认：pageSize
            },
            response: {
                statusName: 'code',
                statusCode: 200,
                countName: 'totals',
                dataName: 'list'
            },
            cols: [[
                {type: 'numbers'}
                , {field: 'id', title: 'ID', align: 'center',}
                , {field: 'parentTypeId', title: '字典类型', align: 'center',}
                , {field: 'code', title: '项目编码', align: 'center'}
                , {field: 'value', title: '项目值', align: 'center'}
                , {field: 'active', title: '是否激活', align: 'center'}
                , {field: 'description', title: '描述', align: 'center'}
                , {field: 'addition1', title: '附加值1', align: 'center'}
                , {field: 'remark', title: '备注', align: 'center'}
                // , {field: 'createTime', title: '创建时间', align: 'center'}
                // , {field: 'modifyTime', title: '更新时间', align: 'center'}
                , {fixed: 'right', title: '操作', align: 'center', toolbar: '#optBar'}
            ]],
            done: function (res, curr) {
                $("[data-field='active']").children().each(function () {
                    if ($(this).text() === '1') {
                        $(this).text("激活")
                    } else if ($(this).text() === '0') {
                        $(this).text("未激活")
                    }
                });
                pageCurr = curr;
            }
        });
        //指定允许上传的文件类型
        upload.render({
            //在这里上传Excel
            elem: '#uploadExcel'
            , data: { "type": "rslist" } //参数
            , url: document.domain + '/dict/importDict' //此处为所上传的请求路径
            , accept: 'file' //普通文件
            , exts: 'xls|excel|xlsx' //只允许上传压缩文件
            , choose: function (obj) {//选择文件后的回调函数。返回一个object参数，详见下文：https://www.layui.com/doc/modules/upload.html
                // showLoad();//启动动画
            }
            , done: function (res) {
                //closeLoad(showLoad());//成功后关闭动画
                if (res.code === "200") {
                    layer.msg(res.msg + ":导入数据" + res.count + "行");
                    setTimeout('window.location.reload()', 1000);
                } else {
                    layer.msg(res.msg);
                }
            }
        });
        //监听工具条
        table.on('tool(dictTable)', function(obj){
            let data = obj.data;
            if(obj.event === 'del'){
                //删除
                delDict(data,data.id);
            } else if(obj.event === 'edit'){
                //编辑
                editDict(data, '编辑');
            }
        });
        //监听提交
        form.on('submit(dictSubmit)', function(data){
            formSubmit(data);
            return false;
        });
    });
    //搜索框
    layui.use(['form','laydate'], function(){
        let form = layui.form ,laydate = layui.laydate;
        laydate.render({
            elem: '#createTimeStart'
            ,type: 'datetime'
        });
        laydate.render({
            elem: '#createTimeEnd'
            ,type: 'datetime'
        });
        laydate.render({
            elem: '#updateTimeStart'
            ,type: 'datetime'
        });
        laydate.render({
            elem: '#updateTimeEnd'
            ,type: 'datetime'
        });
        //监听搜索框
        form.on('submit(searchSubmit)', function(data){
            //重新加载table
            load(data);
            return false;
        });
    });
});

function load(obj){
    let data = {};
    let field = obj.field;
    if(null !== field.createTimeStart && field.createTimeStart !== ''){
        data.createTimeStart = field.createTimeStart;
    }
    if(null !== field.createTimeEnd && field.createTimeEnd !== ''){
        data.createTimeEnd = field.createTimeEnd;
    }
    if(null !== field.updateTimeStart && field.updateTimeStart !== ''){
        data.updateTimeStart = field.updateTimeStart;
    }
    if(null !== field.updateTimeEnd && field.updateTimeEnd !== ''){
        data.updateTimeEnd = field.updateTimeEnd;
    }
    if(null !== field.parentTypeId && field.parentTypeId !== ''){
        data.parentTypeId = field.parentTypeId;
    }
    if(null !== field.code && field.code !== ''){
        data.code = field.code;
    }
    if(null !== field.active && field.active !== ''){
        data.active = field.active;
    }
    if(null !== field.description && field.description !== ''){
        data.description = field.description;
    }
    if(null !== field.remark && field.remark !== ''){
        data.remark = field.remark;
    }
    tableIns.reload({
        where: data
        , page: {
            curr: pageCurr //从当前页码开始
        }
    });
}

function importDict(){
    let uploadUrl = document.domain + '/dict/importDict';
    layer.open({
        type: 2,
        title: 'Excel上传',
        shadeClose: true,
        shade: 0.8,
        area: ['500px', '50%'],
        content: uploadUrl,
        btn: ['确定', '取消'],
        yes: function (index) {
            var formSubmit = layer.getChildFrame('form', index);
            var submited = formSubmit.find('button')[0];
            submited.click();
            $('.layui-laypage-btn').click();
        }
    })
}

function editDict(data,title){
    if(data==null){
        layer.alert('数据为空无法编辑');
        return;
    }
    $("#id").val(data.id);
    $("#parentTypeIdNew").val(data.parentTypeId);
    $("#codeNew").val(data.code);
    $("#valueNew").val(data.value);
    $("#addition1New").val(data.addition1);
    $("#descriptionNew").val(data.description);
    $("#remarkNew").val(data.remark);
    let pageNum = $(".layui-laypage-skip").find("input").val();
    $("#pageNum").val(pageNum);
    layer.open({
        type:1,
        title: title,
        fixed:false,
        resize :false,
        shadeClose: true,
        area: ['550px'],
        content:$('#setDict'),
        end:function(){
            cleanDict();
        }
    });
}

function delDict(data, id){
    if(null!=id){
        layer.confirm('您确定要删除'+data.description+'吗？', {
            btn: ['确认','返回'] //按钮
        }, function(){
            $.post("/dict/delDict",{"id":id},function(data){
                if (data.code === 1) {
                    layer.alert(data.msg,function(){
                        layer.closeAll();
                        load(data);
                    });
                } else {
                    layer.alert(data.msg);
                }
            });
        }, function(){
            layer.closeAll();
        });
    }else{
        layer.alert('主键ID为空，无法删除');
    }
}

//提交表单
function formSubmit(obj){
    let formItems = $("#dictForm").serialize();
        $.ajax({
        type: "POST",
        data: formItems.replaceAll('New', ''),
        url: "/dict/editDict",
        success: function (data) {
            if (data.code === 1) {
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

function cleanDict(){
    $("#parentTypeIdNew").val("");
    $("#codeNew").val("");
    $("#valueNew").val("");
    $('#addition1New').val("");
    $("#descriptionNew").val("");
    $("#remarkNew").val("");
}

