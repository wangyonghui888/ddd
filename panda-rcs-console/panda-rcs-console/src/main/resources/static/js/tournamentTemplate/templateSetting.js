function modifyTemplate(id) {
    layui.use('layer',
    function() {
        var layer = layui.layer;
        var adr;
        //初始化对应玩法的特殊限额数据
        if (id == 1) {
            adr = '/tournamentTemplate/updateSpecialPumping';
            layer.msg('你确定修改么？', {
                time: 2000,
                //2秒自动关闭
                btn: ['确定', '取消'],
                yes: function(index) {
                    $.ajax({
                        url: adr,
                        data: {
                            'playId': $("#ipt1").val(),
                            'preStr': $("#text1").val(),
                            'liveStr': $("#text2").val()
                        },
                        type: "Post",
                        dataType: "json",
                        success: function(data) {
                            layer.msg(data.message);
                        },
                        error: function(data) {
                            layer.msg(data.message);
                        }
                    });
                    layer.close(index);
                }
            });
        }
        //新增玩法
        if (id == 2) {
            adr = '/tournamentTemplate/addTournamentTemplatePlay';
            layer.msg('是否与技术人员确认过该json串是否包含所有字段的值？', {
                time: 2000,
                //2秒自动关闭
                btn: ['确定', '取消'],
                yes: function(index) {
                    $.ajax({
                        url: adr,
                        data: {
                            'param': $("#text3").val()
                        },
                        type: "Post",
                        dataType: "json",
                        success: function(data) {
                            layer.msg(data.message);
                        },
                        error: function(data) {
                            layer.msg(data.message);
                        }
                    });
                    layer.close(index);
                }
            });
        }

        //初始化联赛模板
        if (id == 3) {
            adr = '/tournamentTemplate/initTournamentTemplate';
            layer.msg('是否确认初始化当前赛种的联赛模板？', {
                time: 2000,
                //2秒自动关闭
                btn: ['确定', '取消'],
                yes: function(index) {
                    $.ajax({
                        url: adr,
                        data: {
                            'sportId': $("#ipt2").val()
                        },
                        type: "Post",
                        dataType: "json",
                        success: function(data) {
                            layer.msg(data.message);
                        },
                        error: function(data) {
                            layer.msg(data.message);
                        }
                    });
                    layer.close(index);
                }
            });
        }
    });
};