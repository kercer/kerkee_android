/**
 * Demo相关接口
 * @class objExampleApi
 * @extends Klass
 * @constructor
 * @module modules
 * @example
 *     define('helper/objExampleApi',',function(platform){
 *          testApi.getTestInfo();
 *     });
 * @since 1.0.0
 * @public
 */

define(["api/helper/util"], function (util) {

    var ObjExampleApi = (function () {

        //构建一个TestApi类,继承自基础类
        util.Klass().sub(ObjExampleApi);

        //构造函数
        function ObjExampleApi() {
            /**
             * 映射客户端类的名称 <strong>(必选)</strong>
             * @property nativeCls
             * @type string
             * @since 1.0.0
             * @default "jsBridgeClient"
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

            ObjExampleApi.__super__.constructor.apply(this, arguments);
        }

        ObjExampleApi.include({

            /**
             * 测试非静态的Native函数
             * @method objExampleNotStaticFunction
             * @public
             * @param {Function} callBack 回调函数
             * @param {String} testInfo 测试信息
             * @return {Object}
             * @example
             *      objExampleApi.objExampleNotStaticFunction(function(data){
             *          console.log(data);
             * 	    },"test");
             * @since 1.0.0
             */
            objExampleNotStaticFunction: function (callBack, testInfo) {
                this.sendData({
                    method: "objExampleNotStaticFunction",
                    param: {
                        "testInfo": testInfo
                    },
                    callBack: callBack
                });
            },

            /**
             * 测试静态的Native函数
             * @method objExampleStaticFunction
             * @public
             * @param {Function} callBack 回调函数
             * @param {String} testInfo 测试信息
             * @return {Object}
             * @example
             *      objExampleApi.objExampleStaticFunction(function(data){
             *          console.log(data);
             * 	    },"test");
             * @since 1.0.0
             */
            objExampleStaticFunction: function (callBack, testInfo) {
                this.sendData({
                    method: "objExampleStaticFunction",
                    param: {
                        "testInfo": testInfo
                    },
                    callBack: callBack
                });
            }

        });
        return ObjExampleApi;
    })();

    return new ObjExampleApi({
        name: "kerkee objExampleApi",
        author: "zihong",
        version: "1.0",
        jsbc: jsBridgeClient,
        nativeCls: "objExampleApi"
    });

});