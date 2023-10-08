// Vue实例
let app = new Vue({
    el: '#app',
    data: {
        defaultActive: 'GC监控',
        myData:[],
        serverNames:[],
        serverName:'',
    },
    created() {
        //下拉监听
        this.init2();
        //初始化服务名
        this.init_serverName();
        //初始化数据
//        this.init6();
        //初始化表
//        this.init9();
        //其它初始化
        this.init10();
    },
    mounted() {
//        this.$refs.loader.style.display = 'none';
    },
    methods: {
        _notify(message, type) {
            this.$message({
                message: message,
                type: type
            })
        },
        init2() {
            let $this = this;
            layui.use('form', function () {
                var form = layui.form;
                form.on('select(serverType)', function(data){
                    var value = data.value;
                    $this.serverName=value;
                    $this.init6(); //初始化
                    $this.init9(); //初始化
                });
                form.render();
            });
        },
        init10() {
            let $this = this;
            $("#currentServer").html($this.serverName);
            document.onkeydown = function(e) {
                let key = window.event.keyCode;
                if (key == 13) {
                	var value = $("#currentServer").val();
                    $this.serverName=value;
                    $this.init6(); //初始化
                    $this.init9(); //初始化
                }
            };
        },
        init6() {
            let $this = this;
            $.ajax({
                type: "POST",
                url: api.systemInfo.get,
                data:{"one":"2","serverName":$this.serverName},
                async: false,
                success: function (data) {
                    var myData=data.data.reverse();
                    var data1 = [];
                    for(var i of myData) {
                        var xx=(new Date(i.createTime.replace(new RegExp("-","gm"),'/'))).getTime();
                        var yy=parseInt(i.usCpu);
                        data1.push({
                            x: xx,
                            y: yy
                        });

                    }
                    $this.myData=data1;
                },
                error: function () {
                    layer.alert("chartData操作请求错误，请您稍后再试");
                }
            });
        },
        init_serverName(){
        	let $this = this;
        	layui.use(['form'], function(){  //如果只加载一个模块，可以不填数组。如：layui.use('form')
        		form = layui.form;
        		
                $.ajax({
                    type: "POST",
                    url: api.serverName.get,
                    async: false,
                    success: function (data) {
                    	var data1 = data.data;
                    	$.each(data1,function(index,item){               		
                    		var values = item.serverName +"/"+ item.ip +"/"+ item.pid;
                            if(!$this.serverName){
                            	$this.serverName = values;
                                var option = new Option(values,values);
                            }else {
                                var option = new Option(values,values);
                                // // 如果是之前的parentId则设置选中
                                if(values == $this.serverName) {
                                    option.setAttribute("selected",'true');
                                }
                            }
                            $('#serverType').append(option);//往下拉菜单里添加元素
                            form.render('select'); //这个很重要
                        })
                        $this.init6();
                        $this.init9();
                    },
                    error: function (data) {
                        layer.alert("serverName操作请求错误，请您稍后再试");
                    }
                });
        	});
        },
        init9() {
            let $this = this;
            this.$http.post(api.systemInfo.get,{"one":1,"serverName":$this.serverName}).then(response => {
                if (response.body.code == 200) {
                    Highcharts.chart('gc-count', {
                        chart: {
                            events: {
                                load: function () {
                                    var series = this.series[0];
                                    setInterval(function () {
                                        $this.$http.get(api.systemInfo.get,{"one":1,"serverName":$this.serverName}).then(response => {
                                            if (response.body.code == 200) {
                                                var data = response.body.data;
                                                if(data==null||data==undefined)return;
                                                var x = (new Date(data.createTime.replace(new RegExp("-","gm"),'/'))).getTime();
                                                    y = parseInt(data.usCpu);
                                                series.addPoint([x, y], true, true);
                                            } else {
                                                this._notify(response.body.data, 'error')
                                            }
                                        })
                                    }, 20000);
                                }
                            },
                            zoomType: 'x'
                        },
                        title: {
                            text: "us_cpu",
                        },
                        subtitle: {
                            text: document.ontouchstart === undefined ?
                                '鼠标拖动可以进行缩放' : '手势操作进行缩放'
                        },
                        yAxis: {
                            title: {
                                text: "单位"
                            },
                        },
                        series: [{
                            name: "us_cpu",
                            data: (function () {
                                var myData=$this.myData;
                                return myData;
                            }())
                        }]
                    });
                } else {
                    this._notify(response.body.data, 'error');
                }
            })
        },
    },
});
Highcharts.setOptions({
    chart: {
        type: "spline",
        animation: Highcharts.svg,
        marginRight: 10,
    },
    title: {
        style: {
            "font-size": "1.2rem"
        }
    },
    xAxis: {
        type: 'datetime',
        tickPixelInterval: 150
    },
    yAxis: {
        plotLines: [{
            value: 0,
            width: 1,
            color: "#808080"
        }]
    },
    global: { useUTC: false },
    legend: {
        enabled: false
    },
    plotOptions: {
        line: {
            dataLabels: {
                // 开启数据标签
                enabled: true
            },
            // 关闭鼠标跟踪，对应的提示框、点击事件会失效
            enableMouseTracking: false
        }
    },
    tooltip: {
        formatter: function () {
            return "<b>" + this.series.name + "</b><br/>" + Highcharts.dateFormat("%Y-%m-%d %H:%M:%S", this.x) + "<br/>" + Highcharts.numberFormat(this.y, 2) ;
        }
    },
});
