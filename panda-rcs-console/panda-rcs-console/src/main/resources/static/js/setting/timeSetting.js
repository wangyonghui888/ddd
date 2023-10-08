/**
 * 权限管理
 */
$(function () {
    $.ajax({
        url: 'getAllTime',
        type: "Post",
        dataType: "json",
        success: function (data) {
            $("#time1").val(data.sportType);
            $("#time2").val(data.sportRegion);
            $("#time3").val(data.sportTournament);
            $("#time4").val(data.mathTeam);
            $("#time5").val(data.marketCategory);
            $("#time6").val(data.marketCategory);
            $("#time7").val(data.virtualMarketCategory);
            $("#time8").val(data.sportOutrightMatchData);
            $("#time9").val(data.standardSportPlayer);
        },
        error: function (data) {
            console.log(data);
        }
    });
});

function modifyTime(id) {
    layui.use('layer', function () {
        var adr;
        var time;
        var bussId;
        var bussName;
        var ids;
        if (id == 1) {
            adr = '/setting/updateSyncSportTypeDataTime'
            time = $("#time1").val();
        }
        if (id == 2) {
            adr = '/setting/updateSyncSportRegionDataTime'
            time = $("#time2").val();
        }
        if (id == 3) {
            adr = '/setting/updateSyncSportTournamentDataTime'
            time = $("#time3").val();
        }
        if (id == 4) {
            adr = '/setting/updateSyncMathTeamDataTime'
            time = $("#time4").val();
        }
        if (id == 5) {
            adr = '/setting/updateSyncSportMarketCategoryDataTime'
            time = $("#time5").val();
        }
        if (id == 6) {
            adr = '/setting/updateBussIdAndName'
            bussId = $("#bussId").val();
            bussName = $("#bussName").val();
        }
        if (id == 7) {
            adr = '/setting/updateSyncVirtualMarketCategoryDataTime'
            time = $("#time7").val();
        }
        if (id == 8) {
            adr = '/setting/updateSyncSportOutrightMatchDataTime'
            time = $("#time8").val();
        }
        if (id == 9) {
            adr = '/setting/updateStandardSportPlayerTime'
            time = $("#time9").val();
        }
        if (id == 111) {
            adr = '/setting/startMoveLanguageInternation'
        }
        if (id == 222) {
            adr = '/setting/updatePlaySetNameCodeLanguageInternation'
            ids = $("#lids").val();
        }
        var layer = layui.layer;
        layer.msg('你确定修改么？', {
            time: 2000,//2秒自动关闭
            btn: ['确定', '取消'],
            yes: function (index) {
                console.log(id);
                $.ajax({
                    url: adr,
                    data: {'beginTime': time, 'bussId': bussId, 'bussName': bussName, 'lids': ids},
                    type: "Post",
                    dataType: "json",
                    success: function (data) {
                        console.log(data);
                        if (id == 1) {
                            $("#time1").val(data.beginTime);
                        }
                        if (id == 2) {
                            $("#time2").val(data.beginTime);
                        }
                        if (id == 3) {
                            $("#time3").val(data.beginTime);
                        }
                        if (id == 4) {
                            $("#time4").val(data.beginTime);
                        }
                        if (id == 5) {
                            $("#time5").val(data.beginTime);
                        }
                        if (id == 7) {
                            $("#time7").val(data.beginTime);
                        }
                        if (id == 8) {
                            $("#time8").val(data.beginTime);
                        }
                        if (id == 9) {
                            $("#time9").val(data.beginTime);
                        }

                        // if (id == 6) {
                        //     $("#bussName").val(data.bussName);
                        //     $("#bussId").val(data.bussId);
                        // }
                        layer.msg("请求成功");
                    },
                    error: function (data) {
                        layer.msg("请求失败");
                        console.log(data);
                    }
                });
                layer.close(index);
            }
        });
    });
};