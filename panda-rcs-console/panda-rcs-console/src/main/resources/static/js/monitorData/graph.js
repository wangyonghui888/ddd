/**
 * 用户管理
 */

var myChart;
var option;
$(function () {
    var dom = document.getElementById("container");
    myChart = echarts.init(dom);
    var app = {};

    option = {
        title: {
            text: '访问数量'
        },
        xAxis: {
            type: 'category',
            data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        toolbox: {
            feature: {
                saveAsImage: {}
            }
        },
        tooltip: {
            trigger: 'axis'
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        legend: {
            data: ['玩法获取', '根据玩法集获取玩法', '投注最大最小值', '投注校验', '货量', '注单推送']
        },
        yAxis: {
            type: 'value'
        },
        series: [{
            name: '玩法获取',
            data: [0],
            type: 'line',
            itemStyle: {normal: {label: {show: true}}}
        }, {
            name: '根据玩法集获取玩法',
            data: [0],
            type: 'line',
            itemStyle: {normal: {label: {show: true}}}
        }, {
            name: '投注最大最小值',
            data: [0],
            type: 'line',
            itemStyle: {normal: {label: {show: true}}}
        }, {
            name: '投注校验',
            data: [0],
            type: 'line',
            itemStyle: {normal: {label: {show: true}}}
        }, {
            name: '货量',
            data: [0],
            type: 'line',
            itemStyle: {normal: {label: {show: true}}}
        }, {
            name: '注单推送',
            data: [0],
            type: 'line',
            itemStyle: {normal: {label: {show: true}}}
        }
        ]
    };

    if (option && typeof option === 'object') {
        $.ajax({
            type: "POST",
            data: $("#userSearch").serialize(),
            url: "/monitorData/monitorData/graph",
            success: function (data) {
                if (null != data.RPC_CATEGORY_SET) {
                    option.series[0].data = data.RPC_CATEGORY_SET.counts;
                }
                if (null != data.RPC_CATEGORY_SET_PLAY) {
                    option.series[1].data = data.RPC_CATEGORY_SET_PLAY.counts;
                }
                if (null != data.RPC_QUERY_MAXBET) {
                    option.series[2].data = data.RPC_QUERY_MAXBET.counts;
                }
                if (null != data.RPC_SAVE_ORDER) {
                    option.series[3].data = data.RPC_SAVE_ORDER.counts;
                }
                if (null != data.MQ_ORDER_PREDICT_CALC) {
                    option.series[4].data = data.MQ_ORDER_PREDICT_CALC.counts;
                }
                if (null != data.MQ_ORDER_INFO_WS) {
                    option.series[5].data = data.MQ_ORDER_INFO_WS.counts;
                }
                option.xAxis.data = data.RPC_CATEGORY_SET.hours;
                myChart.setOption(option);
            }
            ,
            error: function (e) {
                alert("查询失败")
            }
        });

    }
    ;

    layui.use(['form', 'laydate'], function () {
        laydate = layui.laydate;
        laydate.render({
            elem: '#startTime'
            , type: 'datetime'
        });
        laydate.render({
            elem: '#endTime'
            , type: 'datetime'
        });
        var startTime = transformTime(new Date().getTime() - 1000 * 60 * 60 * 1, 'yyyy-MM-dd HH:mm:ss')
        $("#startTime").val(startTime);
    });
});

//提交表单
function searchSubmit(obj) {
    $.ajax({
        type: "POST",
        data: $("#userSearch").serialize(),
        url: "/monitorData/monitorData/graph",
        success: function (data) {
            if (null != data.RPC_CATEGORY_SET) {
                option.series[0].data = data.RPC_CATEGORY_SET.counts;
            }
            if (null != data.RPC_CATEGORY_SET_PLAY) {
                option.series[1].data = data.RPC_CATEGORY_SET_PLAY.counts;
            }
            if (null != data.RPC_QUERY_MAXBET) {
                option.series[2].data = data.RPC_QUERY_MAXBET.counts;
            }
            if (null != data.RPC_SAVE_ORDER) {
                option.series[3].data = data.RPC_SAVE_ORDER.counts;
            }
            if (null != data.MQ_ORDER_PREDICT_CALC) {
                option.series[4].data = data.MQ_ORDER_PREDICT_CALC.counts;
            }
            if (null != data.MQ_ORDER_INFO_WS) {
                option.series[5].data = data.MQ_ORDER_INFO_WS.counts;
            }
            option.xAxis.data = data.RPC_CATEGORY_SET.hours;
            myChart.setOption(option);
        },
        error: function () {
            alert("查询失败")
        }
    });
}

function toPercent(point) {
    var str = Number(point * 100).toFixed(1);
    str += "%";
    return str;
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