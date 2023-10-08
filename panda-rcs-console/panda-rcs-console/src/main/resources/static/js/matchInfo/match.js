/**
 * 订单记录查询
 */
// var pageCurr;
// var form;
// $(function () {
//     layui.use('table', function () {
//         var table = layui.table;
//         form = layui.form;
//
//         tableIns = table.render({
//             elem: '#orderList',
//             url: '/orderRecords/list',
//             method: 'post', //默认：get请求
//             cellMinWidth: 200,
//             page: true,
//             request: {
//                 pageName: 'pageNum', //页码的参数名称，默认：pageNum
//                 limitName: 'pageSize' //每页数据量的参数名，默认：pageSize
//             },
//             response: {
//                 statusName: 'code', //数据状态的字段名称，默认：code
//                 statusCode: 200, //成功的状态码，默认：0
//                 countName: 'totals', //数据总数的字段名称，默认：count
//                 dataName: 'list' //数据列表的字段名称，默认：data
//             },
//             cols: [[
//                 {type: 'numbers'}
//                 , {field: 'uid', title: '用户id', align: 'left'}
//                 , {field: 'orderNo', title: '订单号', align: 'left'}
//                 , {field: 'betNo', title: '注单号', align: 'left'}
//                 , {
//                     field: 'orderStatus', title: '订单状态', align: 'left', templet: function (d) {
//                         if (d.orderStatus == '0') {
//                             return '待处理'
//                         }
//                         if (d.orderStatus == '1') {
//                             return '已处理'
//                         }
//                         if (d.orderStatus == '2') {
//                             return '取消交易'
//                         }
//                     }
//                 }
//                 , {field: 'betTime', title: '下注时间', align: 'left'}
//                 , {
//                     field: 'validateResult', title: '注单状态', align: 'left', templet: function (d) {
//                         if (d.validateResult == '0') {
//                             return '待处理'
//                         }
//                         if (d.validateResult == '1') {
//                             return '已处理'
//                         }
//                         if (d.validateResult == '2') {
//                             return '失败'
//                         }
//                         if (d.validateResult == '3') {
//                             return '已取消'
//                         }
//                     }
//                 }
//                 , {field: 'betAmount', title: '注单金额', align: 'left'}
//                 , {field: 'oddsValue', title: '注单赔率', align: 'left'}
//                 , {
//                     field: 'isSettlement', title: '是否结算', align: 'left', templet: function (d) {
//                         if (d.isSettlement == '1') {
//                             return '已结算'
//                         }
//                         if (d.isSettlement == '2') {
//                             return '未结算'
//                         }
//                         if (d.isSettlement == '3') {
//                             return '拒单'
//                         }
//                     }
//                 }
//                 , {
//                     field: 'riskChannel', title: '验证渠道', align: 'left', templet: function (d) {
//                         if (d.riskChannel == '1') {
//                             return 'panda'
//                         }
//                         if (d.riskChannel == '2') {
//                             return 'mts'
//                         }
//                     }
//                 }
//                 , {field: 'productCount', title: '注单项数量', align: 'left'}
//                 , {
//                     field: 'seriesType', title: '串关类型', align: 'left', templet: function (d) {
//                         if (d.seriesType == '1') {
//                             return '单关'
//                         } else {
//                             return '串关'
//                         }
//                     }
//                 }
//                 , {field: 'productAmountTotal', title: '注单总价', align: 'left'}
//                 , {field: 'orderAmountTotal', title: '实际付款金额', align: 'left'}
//                 , {field: 'sportName', title: '种类名称', align: 'left'}
//                 , {
//                     field: 'matchType', title: '类型', align: 'left', templet: function (d) {
//                         if (d.matchType == '1') {
//                             return '早盘'
//                         }
//                         if (d.matchType == '2') {
//                             return '滚球盘'
//                         }
//                         if (d.matchType == '3') {
//                             return '冠军盘'
//                         }
//                     }
//                 }
//                 , {field: 'marketId', title: '盘口id', align: 'left'}
//                 , {field: 'marketValue', title: '盘口值', align: 'left'}
//                 , {
//                     field: 'marketType', title: '盘口类型', align: 'left', templet: function (d) {
//                         if (d.marketType == 'EU') {
//                             return '欧盘'
//                         }
//                         if (d.marketType == 'HK') {
//                             return '香港盘'
//                         }
//                         if (d.marketType == 'US') {
//                             return '美式盘'
//                         }
//                         if (d.marketType == 'ID') {
//                             return '印尼盘'
//                         }
//                         if (d.marketType == 'MY') {
//                             return '马来盘'
//                         }
//                         if (d.marketType == 'GB') {
//                             return '英式盘'
//                         }
//                     }
//                 }
//                 , {field: 'playName', title: '玩法名称', align: 'left'}
//                 , {field: 'matchId', title: '赛事id', align: 'left'}
//                 , {field: 'matchName', title: '赛事名称', align: 'left'}
//                 , {field: 'matchInfo', title: '对阵信息', align: 'left'}
//                 , {field: 'tenantId', title: '商户id', align: 'left'}
//                 , {field: 'currencyCode', title: '币种编码', align: 'left'}
//                 , {
//                     field: 'deviceType', title: '终端', align: 'left', templet: function (d) {
//                         if (d.deviceType == '1') {
//                             return 'H5'
//                         }
//                         if (d.deviceType == '2') {
//                             return 'PC'
//                         }
//                         if (d.deviceType == '3') {
//                             return 'Android'
//                         }
//                         if (d.deviceType == '4') {
//                             return 'IOS'
//                         }
//                     }
//                 }
//
//                 , {field: 'ip', title: 'ip地址', align: 'left'}
//                 , {field: 'ipArea', title: 'ip区域', align: 'left'}
//                 , {field: 'remark', title: '备注', align: 'left'}
//                 , {field: 'reason', title: '拒单原因', align: 'left'}
//             ]],
//             done: function (res, curr, count) {
//                 //如果是异步请求数据方式，res即为你接口返回的信息。
//                 //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
//                 //console.log(res);
//                 //得到当前页码
//                 /*console.log(curr);
//                 $("[data-field='userStatus']").children().each(function(){
//                     if($(this).text()=='1'){
//                         $(this).text("有效")
//                     }else if($(this).text()=='0'){
//                         $(this).text("失效")
//                     }
//                 });*/
//
//                 $("[data-field='betTime']").children().each(function () {
//                     if ($(this).text() != null && $(this).text().indexOf("时间") == -1) {
//                         var a = parseFloat($(this).text());
//                         var b = new Date(a);
//                         $(this).text(transformTime(b));
//                     }
//                 });
//                 //得到数据总量
//                 //console.log(count);
//                 pageCurr = curr;
//             }
//         });
//
//         //监听工具条
//         table.on('tool(userTable)', function (obj) {
//             var data = obj.data;
//             if (obj.event === 'del') {
//                 //删除
//                 delUser(data, data.id, data.sysUserName);
//             } else if (obj.event === 'edit') {
//                 //编辑
//                 openUser(data, "编辑");
//             } else if (obj.event === 'recover') {
//                 //恢复
//                 recoverUser(data, data.id);
//             }
//         });
//
//         //监听提交
//         form.on('submit(userSubmit)', function (data) {
//             // TODO 校验
//             formSubmit(data);
//             return false;
//         });
//     });
//
//     //搜索框
//     layui.use(['form', 'laydate'], function () {
//         var form = layui.form, layer = layui.layer
//             , laydate = layui.laydate;
//         //日期
//         laydate.render({
//             elem: '#startTime'
//             , type: 'datetime'
//         });
//         laydate.render({
//             elem: '#endTime'
//             , type: 'datetime'
//         });
//         //TODO 数据校验
//         //监听搜索框
//         form.on('submit(searchSubmit)', function (data) {
//             //重新加载table
//             load(data);
//             return false;
//         });
//     });
// });

// //提交表单
// function formSubmit(obj) {
//     $.ajax({
//         type: "POST",
//         data: $("#userForm").serialize(),
//         url: "/user/setUser",
//         success: function (data) {
//             if (data.code == 1) {
//                 layer.alert(data.msg, function () {
//                     layer.closeAll();
//                     load(obj);
//                 });
//             } else {
//                 layer.alert(data.msg);
//             }
//         },
//         error: function () {
//             layer.alert("操作请求错误，请您稍后再试", function () {
//                 layer.closeAll();
//                 //加载load方法
//                 load(obj);//自定义
//             });
//         }
//     });
// }

function match_info_search(){
    clearFrom();
    $.ajax({
        type: "POST",
        async:false,
        data: $("#userSearch").serialize(),
        url: "/matchInfo/show",
        success: function (data) {
            // debugger;
            if (data) {
                insertDataToTable(data);
                return
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
//清空表单
function clearFrom(){
    $("span").text("");
}
//表单设值
function insertDataToTable(data) {
    $("#match_id").text(data.matchId);
    $("#sport_id").text(data.sportId);
    $("#standard_tournament_id").text(data.standardTournamentId);
    $("#third_match_id").text(data.thirdMatchId);
    $("#seconds_match_start").text(data.secondsMatchStart);
    $("#pre_match_business").text(data.preMatchBusiness);
    $("#live_odd_business").text(data.liveOddBusiness);
    $("#operate_match_status").text(data.operateMatchStatus);
    $("#begin_time").text(transformTime(data.beginTime));
    $("#match_status").text(data.matchStatus);
    $("#match_manage_id").text(data.matchManageId);
    $("#data_source_code").text(data.dataSourceCode);
    $("#third_match_source_id").text(data.thirdMatchSourceId);
    $("#home_away_info").text(data.homeAwayInfo);
    $("#match_period_id").text(data.matchPeriodId);
    $("#update_time").text(data.updateTime);
    $("#pre_risk_manager_code").text(data.preRiskManagerCode);
    $("#pre_match_data_provider_code").text(data.liveMatchDataProviderCode);
    $("#live_risk_manager_code").text(data.liveRiskManagerCode);
    $("#live_match_data_provider_code").text(data.liveMatchDataProviderCode);
    $("#sell_live_odd_business").text(data.sellLiveOddBusiness);
    $("#pre_match_time").text(transformTime(data.preMatchTime));
    $("#live_odd_time").text(transformTime(data.liveOddTime));
    $("#business_event").text(data.businessEvent);
    $("#pre_match_sell_status").text(data.preMatchSellStatus);
    $("#live_match_sell_status").text(data.liveMatchSellStatus);
    $("#video_source").text(data.videoSource);
    $("#tournament_name_cn").text(data.tournamentNameCn);
    $("#tournament_name_en").text(data.tournamentNameEn);
    $("#sell_update_time").text(data.sellUpdateTime);
    $("#market_id").text(data.marketId);
    $("#standard_match_info_id").text(data.standardMatchInfoId);
    $("#market_category_id").text(data.playId);
    $("#market_type").text(data.marketType);
    $("#operation_type").text(data.operationType);
    $("#order_type").text(data.orderType);
    $("#odds_metric").text(data.oddsMetric);
    $("#addition_1").text(data.addition1);
    $("#addition_2").text(data.addition2);
    $("#addition_3").text(data.addition3);
    $("#addition_4").text(data.addition4);
    $("#market_data_source_code").text(data.marketDataSourceCode);
}

function show_tips(data) {
    var tip_msg = '';
    if (data == 'th_sport_id'){
        tip_msg = '体育种类id. 运动种类id 对应sport.id';
    }else if(data == 'th_pre_match_business'){
        tip_msg = '赛事是否开放赛前盘. 取值为 1  或  0.  1=开放; 0=不开放';
    }else if(data == 'th_live_odd_business'){
        tip_msg = '赛事是否开放滚球. 取值为 1  或  0.  1=开放; 0=不开放';
    }else if(data == 'th_operate_match_status'){
        tip_msg = '比赛开盘标识. 0: 未开盘; 1: 开盘; 2: 关盘; 3: 封盘; 开盘后用户可下注';
    }else if(data == 'th_match_status'){
        tip_msg = '赛事状态  0:未开赛, 1:滚球, 2:暂停，3:结束 ，4:关闭，5:取消，6:放弃，7:延迟，8:未知，9:延期，10:中断';
    }else if(data == 'th_sell_live_odd_business'){
        tip_msg = '是否支持滚球 1 支持  0 不支持';
    }else if(data == 'th_pre_match_sell_status'){
        tip_msg = '赛前开售状态 未售Unsold，逾期未售Overdue_Unsold，申请延期 Apply_Delay，开售 Sold，申请停售 Apply_Stop_Sold，停售 Stop_Sold，意外停售 Expected_End_Sold';
    }else if(data == 'th_live_match_sell_status'){
        tip_msg = '滚球开售状态 未售Unsold，逾期未售Overdue_Unsold，申请延期 Apply_Delay，开售 Sold，申请停售 Apply_Stop_Sold，停售 Stop_Sold，意外停售 Expected_End_Sold';
    }else if(data == 'th_market_type'){
        tip_msg = '盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 2: 滚球盘.';
    }else if(data == 'th_operation_type'){
        tip_msg = '空=未设置; 1=手动; 0=自动';
    }else if(data == 'th_match_period_id'){
        tip_msg = '比赛阶段id. 取自基础表 : match_status.id';
    }
    return tip_msg;
}

// function getQueryParm(){
//     var data = {};
//     data.matchId = $("#matchId").val();
//     data.playId = $("#playId").val();
//     data.marketValue = $("#marketValue").val();
//     data.marketId = $("#marketId").val();
//     data.matchManageId = $("#matchManageId").val();
//     return data;
// }

// function load(obj) {
//     //重新加载table
//     tableIns.reload({
//         where: obj.field
//         , page: {
//             curr: pageCurr //从当前页码开始
//         }
//     });
// }

function transformTime(timestamp) {
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