// Vue实例
var form;
let app = new Vue({
    el: '#app',
    data: {
        defaultActive: '线程监控',
        daemonData:[],
        countData:[],
        serverNames:[],
        serverName:"",
        lastTime1:"",
        lastTime2:"",
    },
    created() {
    	this.init_serverName();
    	this.init_enter_listener();
    	this.init_serverName_listener();
//    	this.init_allData();
        this.init(); //初始化
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
        init_serverName_listener() {
            let $this = this;
            layui.use('form', function () {
                form = layui.form;
                form.on('select(serverType)', function(data){
                    var value = data.value;
                    $this.serverName=value;
                    $this.init_allData();
                    $this.init(); //初始化
                });
                form.render();
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
                        
                        $this.init_allData();
                    },
                    error: function (data) {
                        layer.alert("serverName操作请求错误，请您稍后再试");
                    }
                });
        	});
        },
        init_enter_listener() {
            let $this = this;
            $("#currentServer").val($this.serverName);
            document.onkeydown = function(e) {
                let key = window.event.keyCode;
                if (key == 13) {
                	var value = $("#currentServer").val();
                    $this.serverName=value;
                    $this.init_allData();
                    $this.init(); //初始化
                }
            };
        },
        init_allData() {
            let $this = this;
            $.ajax({
                type: "POST",
                url: api.thread.get,
                data:{"one":"2","serverName":$this.serverName},
                async: false,
                success: function (data) {
                    var myData = data.data.reverse();
                    var data1 = [];
                    var data2 = [];
                    for(var i of myData) {
                        var xx=(new Date(i.create_time.replace(new RegExp("-","gm"),'/'))).getTime();
                        var yy=parseInt(i.daemon_count);
                        var zz=parseInt(i.count);
                        data1.push({
                            x: xx,
                            y: yy
                        });
                        data2.push({
                            x: xx,
                            y: zz
                        });
                        lastTime1 = xx;
                        lastTime2 = xx;
                    }
                    $this.daemonData=data1;
                    $this.countData=data2;
                },
                error: function () {
                    layer.alert("chartData操作请求错误，请您稍后再试");
                }
            });
        },
        /**
         * 初始化
         */
        init() {
            let $this = this;
            this.$http.get(api.thread.get).then(response => {
                if (response.body.code == 200) {
                    this.memory = response.body.data;
                    Highcharts.chart('thread-daemon', {
                        chart: {
                            events: {
                                load: function () {
                                    var series = this.series[0];
                                    setInterval(function () {
                                    	$this.$http.post(api.thread.get,{"one":"1","serverName":$this.serverName}).then(response => {
                                            if (response.body.code == 200) {
                                                var data = response.body.data;
                                                if(!data || !data.create_time) return;
                                                var xx=(new Date(data.create_time.replace(new RegExp("-","gm"),'/'))).getTime();
                                                var x = xx, y = data.daemon_count;
                                                if(x > lastTime1){
                                                	series.addPoint([x, y], true, true);
                                                	lastTime1 = x;
                                                }
                                            } else {
                                                this._notify(response.body.data, 'error')
                                            }
                                        })
                                    }, 6e3);
                                }
                            }
                        },
                        title: {
                            text: "JVM 守护线程数量",
                        },
                        chart: {
                            zoomType: 'x'
                        },
                        subtitle: {
                            text: document.ontouchstart === undefined ?
                                '鼠标拖动可以进行缩放' : '手势操作进行缩放'
                        },
                        series: [{
                            name: "JVM 守护线程数量",
                            data: function () {
                            	var daemonData = $this.daemonData;
                            	return daemonData;
                            }()
                        }]
                    });
                    Highcharts.chart('thread-count', {
                        chart: {
                            events: {
                                load: function () {
                                    var series = this.series[0];
                                    setInterval(function () {
                                    	$this.$http.post(api.thread.get,{"one":"1","serverName":$this.serverName}).then(response => {
                                            if (response.body.code == 200) {
                                                var data = response.body.data;
                                                if(!data || !data.create_time) return;
                                                var xx=(new Date(data.create_time.replace(new RegExp("-","gm"),'/'))).getTime();
                                                var x = xx, y = data.count;
                                                if(x > lastTime2){
                                                	series.addPoint([x, y], true, true);
                                                	lastTime2 = x;
                                                }
                                            } else {
                                                this._notify(response.body.data, 'error')
                                            }
                                        })
                                    }, 6e3);
                                }
                            }
                        },
                        title: {
                            text: "JVM 线程总数量",
                        },
                        chart: {
                            zoomType: 'x'
                        },
                        subtitle: {
                            text: document.ontouchstart === undefined ?
                                '鼠标拖动可以进行缩放' : '手势操作进行缩放'
                        },
                        series: [{
                            name: "JVM 线程总数量",
                            data: function () {
                            	var countData = $this.countData;
                            	return countData;
                            }()
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
        title: {
            text: "单位/个"
        },
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
            return "<b>" + this.series.name + "</b><br/>" 
            	+ Highcharts.dateFormat("%Y-%m-%d %H:%M:%S", this.x) 
            	+ "<br/>" + Highcharts.numberFormat(this.y, 2) + "个";
        }
    },
});

