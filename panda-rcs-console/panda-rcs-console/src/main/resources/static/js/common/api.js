//设置全局表单提交格式
Vue.http.options.emulateJSON = true;

//前端API访问接口
let api = {
    gc: {
        get: '/jvm/gc/get',
    },
    memory: {
        get: '/jvm/memory/get',
    },
    systemInfo: {
        get: '/jvm/systemInfo/get',
    },
    serviceInfo: {
        get: '/jvm/serviceInfo/get',
    },
    serverName: {
        get: '/jvm/getServerName',
    },
    thread: {
        get: '/jvm/thread/get',
    }

}