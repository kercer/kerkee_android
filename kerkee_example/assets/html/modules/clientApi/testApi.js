/**
 * Demo相关接口
 * @class TestApi
 * @extends Klass
 * @constructor
 * @module modules
 * @example
 *     define('helper/testApi',',function(platform){
 *          testApi.getTestInfo();
 *     });
 * @since 1.0.0
 * @public
 */

define(["api/helper/util"], function ( util) {

    var TestApi = (function () {

        //构建一个TestApi类,继承自基础类
        util.Klass().sub(TestApi);

        //构造函数
        function TestApi() {
            /**
             * 映射客户端类的名称 <strong>(必选)</strong>
             * @property nativeCls
             * @type string
             * @since 1.0.0
             * @default "channelModule"
             * @public
             */

            /**
             * jsBridgeClient通信对象 <strong>(必选)</strong>
             * @property jsbc
             * @type string
             * @since 1.0.0
             * @default jsBridgeClient
             * @public
             */

            /**
             * 模块信息描述 <strong>(可选)</strong>
             * @property name
             * @type string
             * @since 1.0.0
             * @public
             */

            /**
             * 模块版本 <strong>(可选)</strong>
             * @property verison
             * @type int
             * @since 1.0.0
             * @public
             */

            /**
             * 模块作者 <strong>(可选)</strong>
             * @property author
             * @type string
             * @since 1.0.0
             * @public
             */

            TestApi.__super__.constructor.apply(this, arguments);
        }

        TestApi.include({

            /**
             * 获取测试信息
             * @method getTestInfo
             * @public
             * @param {Function} callBack 回调函数
             * @param {String} testInfo 测试信息
             * @return {Object}
             * @example
             *      testApi.getTestInfo(function(data){
             *          console.log(data);
             * 	    },"test");
             * @since 1.0.0
             */
            getTestInfo: function (callBack, testInfo) {
                this.sendData({
                    method: "testInfo",
                    param: {
                        "testInfo": "I'm testInfo"
                    },
                    callBack: callBack
                });
            }


        });
        return TestApi;
    })();

    return new TestApi({
        name: "kerkee testApi",
        author: "zihong",
        version: "1.0",
        jsbc: jsBridgeClient,
        nativeCls: "testModule"
    });

});