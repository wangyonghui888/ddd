// Vue实例
let app = new Vue({
    el: '#app',
    data: {
        defaultActive: '内存监控',
        myData:[],
        myData2:[],
        myData3:[],
        myData4:[],
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
        /**
         * 初始化
         */
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
                url: api.memory.get,
                data:{"one":"2","serverName":$this.serverName},
                async: false,
                success: function (data) {
                    var myData=data.data.reverse();
                    var data1 = [];
                    var data2 = [];
                    var data3 = [];
                    var data4 = [];
                    for(var i of myData) {
                        var xx=(new Date(i.createTime.replace(new RegExp("-","gm"),'/'))).getTime();
                        var yy=parseInt(i.committed/1024/1024);
                        var zz=parseInt(i.used/1024/1024);
                        var aa=parseInt(i.nonCommitted/1024/1024);
                        var bb=parseInt(i.nonUsed/1024/1024);
                        data1.push({
                            x: xx,
                            y: yy
                        });
                        data2.push({
                            x: xx,
                            y: zz
                        });
                        data3.push({
                            x: xx,
                            y: aa
                        });
                        data4.push({
                            x: xx,
                            y: bb
                        });
                    }
                    $this.myData=data1;
                    $this.myData2=data2;
                    $this.myData3=data3;
                    $this.myData4=data4;
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
            this.$http.get(api.memory.get).then(response => {
                if (response.body.code == 200) {
                    Highcharts.chart('heap-commit', {
                        chart: {
                            events: {
                                load: function () {
                                    var series = this.series[0];
                                    setInterval(function () {
                                        $this.$http.post(api.memory.get,{"one":1,"serverName":$this.serverName}).then(response => {
                                            if (response.body.code == 200) {
                                                var data = response.body.data;
                                                if(data==null||data==undefined)return;
                                                var x = (new Date(data.createTime.replace(new RegExp("-","gm"),'/'))).getTime();
                                                y = parseInt(data.committed/1024/1024);
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
                            text: "堆区已申请内存大小（Heap）",
                        },
                        subtitle: {
                            text: document.ontouchstart === undefined ?
                                '鼠标拖动可以进行缩放' : '手势操作进行缩放'
                        },
                        legend: {
                            enabled: false
                        },
                        series: [{
                            name: "堆区已申请内存大小（Heap）",
                            data: function () {
                                var myData=$this.myData;
                                return myData;
                            }()
                        }]
                    });
                    Highcharts.chart('heap-used', {
                        chart: {
                            events: {
                                load: function () {
                                    var series = this.series[0];
                                    setInterval(function () {
                                        $this.$http.post(api.memory.get,{"one":1,"serverName":$this.serverName}).then(response => {
                                            if (response.body.code == 200) {
                                                var data = response.body.data;
                                                if(data==null||data==undefined)return;
                                                var x = (new Date(data.createTime.replace(new RegExp("-","gm"),'/'))).getTime();
                                                y = parseInt(data.used/1024/1024);
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
                            text: "堆区已使用内存大小（Heap）",
                        },
                        subtitle: {
                            text: document.ontouchstart === undefined ?
                                '鼠标拖动可以进行缩放' : '手势操作进行缩放'
                        },
                        legend: {
                            enabled: false
                        },
                        series: [{
                            name: "堆区已使用内存大小（Heap）",
                            data: function () {
                                var myData=$this.myData2;
                                return myData;
                            }()
                        }]
                    });
                    Highcharts.chart('nonheap-commit', {
                        chart: {
                            events: {
                                load: function () {
                                    var series = this.series[0];
                                    setInterval(function () {
                                        $this.$http.post(api.memory.get,{"one":1,"serverName":$this.serverName}).then(response => {
                                            if (response.body.code == 200) {
                                                var data = response.body.data;
                                                if(data==null||data==undefined)return;
                                                var x = (new Date(data.createTime.replace(new RegExp("-","gm"),'/'))).getTime();
                                                y = parseInt(data.nonCommitted/1024/1024);
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
                            text: "非堆已申请内存大小（NonHeap）",
                        },
                        subtitle: {
                            text: document.ontouchstart === undefined ?
                                '鼠标拖动可以进行缩放' : '手势操作进行缩放'
                        },
                        legend: {
                            enabled: false
                        },
                        series: [{
                            name: "非堆区已申请内存大小（NonHeap）",
                            data: function () {
                                var myData=$this.myData3;
                                return myData;
                            }()
                        }]
                    });
                    Highcharts.chart('nonheap-used', {
                        chart: {
                            events: {
                                load: function () {
                                    var series = this.series[0];
                                    setInterval(function () {
                                        $this.$http.post(api.memory.get,{"one":1,"serverName":$this.serverName}).then(response => {
                                            if (response.body.code == 200) {
                                                var data = response.body.data;
                                                if(data==null||data==undefined)return;
                                                var x = (new Date(data.createTime.replace(new RegExp("-","gm"),'/'))).getTime();
                                                y = parseInt(data.nonUsed/1024/1024);
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
                            text: "非堆区已使用内存大小（NonHeap）",
                        },
                        subtitle: {
                            text: document.ontouchstart === undefined ?
                                '鼠标拖动可以进行缩放' : '手势操作进行缩放'
                        },
                        legend: {
                            enabled: false
                        },
                        series: [{
                            name: "非堆区已使用内存大小（NonHeap）",
                            data: function () {
                                var myData=$this.myData4;
                                return myData;
                            }()
                        }]
                    });
                } else {
                    this._notify(response.body.data, 'error');
                }
            })
            this.$http.get(api.gc.getPools).then(response => {
                if (response.body.code == 200) {
                    let data = [];
                    response.body.data.forEach(bean => {
                        let obj = {
                            name: bean.name,
                            data: function () {
                                var data = [], time = new Date().getTime(), i;
                                for (i = -19; i <= 0; i++) {
                                    data.push({
                                        x: time + i * 1e3,
                                        y: 0
                                    });
                                }
                                return data;
                            }()
                        }
                        data.push(obj)
                    })
                    Highcharts.chart('pools-commit', {
                        chart: {
                            events: {
                                load: function () {
                                    var series = this.series[0];
                                    var series1 = this.series[1];
                                    var series2 = this.series[2];
                                    var series3 = this.series[3];
                                    var series4 = this.series[4];
                                    var series5 = this.series[5];
                                    setInterval(function () {
                                        $this.$http.get(api.gc.getPools).then(response => {
                                            if (response.body.code == 200) {
                                                var data = response.body.data[0];
                                                var x = (new Date()).getTime(), y = (data.committed / 1024 / 1024);
                                                series.addPoint([x, y], true, true);

                                                var data1 = response.body.data[1];
                                                var x1 = (new Date()).getTime(), y1 = (data1.committed / 1024 / 1024);
                                                series1.addPoint([x1, y1], true, true);

                                                var data2 = response.body.data[2];
                                                var x2 = (new Date()).getTime(), y2 = (data2.committed / 1024 / 1024);
                                                series2.addPoint([x2, y2], true, true);

                                                var data3 = response.body.data[3];
                                                var x3 = (new Date()).getTime(), y3 = (data3.committed / 1024 / 1024);
                                                series3.addPoint([x3, y3], true, true);

                                                var data4 = response.body.data[4];
                                                var x4 = (new Date()).getTime(), y4 = (data4.committed / 1024 / 1024);
                                                series4.addPoint([x4, y4], true, true);

                                                var data5 = response.body.data[5];
                                                var x5 = (new Date()).getTime(), y5 = (data5.committed / 1024 / 1024);
                                                series5.addPoint([x5, y5], true, true);
                                            } else {
                                                this._notify(response.body.data, 'error')
                                            }
                                        })
                                    }, 6e3);
                                }
                            }
                        },
                        title: {
                            text: "各内存区监控（已申请内存）",
                        },
                        series: data,
                    });
                    Highcharts.chart('pools-used', {
                        chart: {
                            events: {
                                load: function () {
                                    var series = this.series[0];
                                    var series1 = this.series[1];
                                    var series2 = this.series[2];
                                    var series3 = this.series[3];
                                    var series4 = this.series[4];
                                    var series5 = this.series[5];
                                    setInterval(function () {
                                        $this.$http.get(api.gc.getPools).then(response => {
                                            if (response.body.code == 200) {
                                                var data = response.body.data[0];
                                                var x = (new Date()).getTime(), y = (data.used / 1024 / 1024);
                                                series.addPoint([x, y], true, true);

                                                var data1 = response.body.data[1];
                                                var x1 = (new Date()).getTime(), y1 = (data1.used / 1024 / 1024);
                                                series1.addPoint([x1, y1], true, true);

                                                var data2 = response.body.data[2];
                                                var x2 = (new Date()).getTime(), y2 = (data2.used / 1024 / 1024);
                                                series2.addPoint([x2, y2], true, true);

                                                var data3 = response.body.data[3];
                                                var x3 = (new Date()).getTime(), y3 = (data3.used / 1024 / 1024);
                                                series3.addPoint([x3, y3], true, true);

                                                var data4 = response.body.data[4];
                                                var x4 = (new Date()).getTime(), y4 = (data4.used / 1024 / 1024);
                                                series4.addPoint([x4, y4], true, true);

                                                var data5 = response.body.data[5];
                                                var x5 = (new Date()).getTime(), y5 = (data5.used / 1024 / 1024);
                                                series5.addPoint([x5, y5], true, true);
                                            } else {
                                                this._notify(response.body.data, 'error')
                                            }
                                        })
                                    }, 6e3);
                                }
                            }
                        },
                        title: {
                            text: "各内存区监控（已使用内存）",
                        },
                        series: data,
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
    global: { useUTC: false },
    yAxis: {
        title: {
            text: "单位/Mb"
        },
        plotLines: [{
            value: 0,
            width: 1,
            color: "#808080"
        }]
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
            return "<b>" + this.series.name + "</b><br/>" + Highcharts.dateFormat("%Y-%m-%d %H:%M:%S", this.x) + "<br/>" + Highcharts.numberFormat(this.y, 2);
        }
    },
});