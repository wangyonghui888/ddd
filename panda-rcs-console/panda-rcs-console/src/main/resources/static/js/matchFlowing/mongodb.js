/**
 * 权限管理
 */

function sRedisKey(id) {
    layui.use('layer', function () {
        let url;
        let matchId;
        let matchStatus;
        if (id === 1) {
            matchId = $("#time1").val();
            url = '/matchFlowing/getMatchStatus';
        }else if (id === 2) {
            matchId = $("#time3").val();
            matchStatus = $("#time4").val();
            url = '/matchFlowing/setMatchStatus';
        }
        $.ajax({
            url: url,
            data: {'matchId': matchId,'matchStatus': matchStatus},
            type: "Post",
            dataType: "text",
            success: function (data) {
                $("#time10").val(JSON.stringify(data));
                layer.msg("请求成功");
            },
            error: function (data) {
                layer.msg("请求失败");
                console.log(data);
            }
        });
    });
}