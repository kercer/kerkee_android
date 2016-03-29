'use strict';

//requirejs通用模块设置
requirejs.config({

    baseUrl:"../../modules",

    paths: {
        api:"../vendor/api",
        kerkee: "../kerkee",
        zepto:"../vendor/lib/zepto.min",
        template: '../vendor/lib/template',
        domReady: '../vendor/plugin/domReady'
    },

    waitSeconds: 10,

    shim: {
        "kerkee": {
            exports: "kerkee"
        },

        'zepto':{
            exports:'$'
        },

        'template': {
            exports: "template"
        }
    }
});

//入口方法初始化
require(["test/testDo"],function(testDo){
    testDo.init();
});
