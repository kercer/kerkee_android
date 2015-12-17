/**
 * Created by zihong on 2015/9/14.
 */
define([
    'zepto',
    'template',
    'clientApi/clientUI',
    'clientApi/clientInfo',
    'api/helper/util',
    'api/nativeUI/widget',
    'clientApi/testApi',
    'clientApi/objExampleApi',
    'domReady!'
],

    function ($,template,clientUI,clientInfo,util,widget,testApi,objExampleApi) {
        function TestDo(){
        }

        TestDo.prototype.render=function(){
            $('body').on('click','.toast',function(){
                widget.toast('this is a test');
            });

            $('body').on('click','.alert',function(){
                widget.alertDialog('this is a test','this is a test message',function(){ alert("callback normal"); },'OK');
            });

            // alertClientUI
            $('body').on('click','.alertClientUI',function(){
                clientUI.clientToast('this is a test','warn');
            });

            $('body').on('click','.testInfo',function(){
                alert(1);
                testApi.getTestInfo(function(data){
                    console.log("callback:"+data);
                },"test getTestInfo fun");
            });

            //objExampleNotStaticFunction
            $('body').on('click','.objExampleNotStaticFunction',function(){
                objExampleApi.objExampleNotStaticFunction(function(data){
                    console.log("callback:"+data);
                },"objExampleApi.objExampleNotStaticFunction that click from js");
            });

            //objExampleStaticFunction
            $('body').on('click','.objExampleStaticFunction',function(){
                objExampleApi.objExampleStaticFunction(function(data){
                    console.log("callback:"+data);
                },"objExampleApi.objExampleStaticFunction that click from js");
            });

        window.testReturnString = function (json){

//        var test = {};
//        test.a = "t1";
//        test.b = "t2";
//        test.c = "t3";
//        test.d = "t4";
//        alert(JSON.stringify(test));
        alert(typeof(json));
        alert(json);
        alert(JSON.stringify(json));

//        return test;
        return "Im testReturnString";
        };
    };

    TestDo.prototype.init=function(){
        var t=this;
        testApi.start(function () {
            t.render();
        });
    };

    return new TestDo();
});
