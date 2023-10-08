/**
 * 权限管理
 */


function setReids(id){
    layui.use('layer',function(){
        var adr;
        var key ;
        var hashKey ;
        var value ;
        adr = '/setting/setRedis';
        if(id == 1){
            key=$("#time1").val();
            hashKey=$("#time2").val();
            value=$("#time3").val();
        }
        else if(id == 3){
            key=$("#time6").val();
            hashKey=$("#time7").val();
            value=$("#time8").val();
        }
        else if(id == 2){
            key=$("#time4").val();
            value=$("#time5").val();
        }
        else if(id == 4){
            key=$("#time9").val();
        }
        else if(id == 5){
            key=$("#delKeys").val();
        }
        var layer = layui.layer;
        layer.msg('你确定修改么？', {
            time: 2000 ,//2秒自动关闭
            btn: ['确定', '取消'],
            yes: function(index){
                $.ajax({
                    url:adr,
                    data:{'key':key,'hashKey':hashKey,'value':value,"type":id},
                    type:"Post",
                    dataType:"text",
                    success:function(data){
                        layer.msg(data);
                    },
                    error:function(data){
                        layer.msg("请求失败");
                        console.log(data);
                    }
                });
                layer.close(index);
            }
        });
    });
}



function setReidsTrade(id){
    layui.use('layer',function(){
        var adr;
        var key ;
        var hashKey ;
        var value ;
        adr = '/setting/setRedis-trade';
        if(id == 1){
            key=$("#time1").val();
            hashKey=$("#time2").val();
            value=$("#time3").val();
        }
        else if(id == 3){
            key=$("#time6").val();
            hashKey=$("#time7").val();
            value=$("#time8").val();
        }
        else if(id == 2){
            key=$("#time4").val();
            value=$("#time5").val();
        }
        else if(id == 4){
            key=$("#time9").val();
            value=$("#time10").val();
        }
        var layer = layui.layer;
        layer.msg('你确定修改么？', {
            time: 2000 ,//2秒自动关闭
            btn: ['确定', '取消'],
            yes: function(index){
                console.log(id);
                $.ajax({
                    url:adr,
                    data:{'key':key,'hashKey':hashKey,'value':value,"type":id},
                    type:"Post",
                    dataType:"text",
                    success:function(data){
                        console.log(data);
                        layer.msg(data);
                    },
                    error:function(data){
                        layer.msg("请求失败");
                        console.log(data);
                    }
                });
                layer.close(index);
            }
        });
    });
}