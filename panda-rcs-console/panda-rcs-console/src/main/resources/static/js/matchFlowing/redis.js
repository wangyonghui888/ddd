/**
 * 权限管理
 */

function sRedisKey(id) {
    layui.use('layer', function () {
        var adr;
        var name;
        var hashKey;
        if (id == 1) {
            adr = '/matchFlowing/getSingelRedisKeyValue'
            name = $("#time1").val();
        }else if (id == 2) {
            adr = '/matchFlowing/getRedisKeyValue'
            name = $("#time2").val();
        }else if (id == 3) {
            adr = '/matchFlowing/getRedisHashKeyValue'
            name = $("#time3").val();
            hashKey = $("#time4").val();

        }
        //var layer = layui.layer;
        console.log(id);
        $.ajax({
            url: adr,
            data: {'name': name,'hashKey': hashKey,'type': id},
            type: "Post",
            dataType: "text",
            success: function (data) {
                console.log(data);
                $("#time10").val(JSON.stringify(data));
                //layer.msg("请求成功");
            },
            error: function (data) {
                layer.msg("请求失败");
                console.log(data);
            }
        });
        //layer.close(index);
    });
}


function sRedisKeyTrade(id) {
    layui.use('layer', function () {
        var adr;
        var name;
        var hashKey;
        if (id == 1) {
            adr = '/matchFlowing/getSingelRedisKeyValue-trade'
            name = $("#time1").val();
        }else if (id == 2) {
            adr = '/matchFlowing/getRedisKeyValue-trade'
            name = $("#time2").val();
        }else if (id == 3) {
            adr = '/matchFlowing/getRedisHashKeyValue-trade'
            name = $("#time3").val();
            hashKey = $("#time4").val();

        }
        //var layer = layui.layer;
        console.log(id);
        $.ajax({
            url: adr,
            data: {'name': name,'hashKey': hashKey,'type': id},
            type: "Post",
            dataType: "text",
            success: function (data) {
                console.log(data);
                $("#time10").val(JSON.stringify(data));
                //layer.msg("请求成功");
            },
            error: function (data) {
                layer.msg("请求失败");
                console.log(data);
            }
        });
        //layer.close(index);
    });
}