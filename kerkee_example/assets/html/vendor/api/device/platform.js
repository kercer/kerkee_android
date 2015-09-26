/**
 * 客户端基础信息获取
 * @class Platform
 * @extends Klass
 * @constructor
 * @module device
 * @example
 *     define('api/client/Platform',function(platform){
 *          platform.getNetworkType();
 *     });
 * @since 1.0.0
 * @public
 */

define(['api/helper/util'],function(util){

    var Platform=(function(){

        //构建一个Platform类,继承自基础类
        util.Klass().sub(Platform);

        //构造函数
        function Platform() {
            /**
             * 映射客户端类的名称 <strong>(必选)</strong>
             * @property nativeCls
             * @type string
             * @since 1.0.0
             * @default "platform"
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

            Platform.__super__.constructor.apply(this, arguments);
        }

        Platform.include({
            /**
             * 判断当前的网络状态
             * @method getNetwork
             * @public
             * @param {Function} callBack 回调函数
             * @return {Object}
             * {
             *    "type":"wifi",
             *    "p1":"NjAxMjIzODg3Nzc3NDIyOTU4Mg=="
             * }
             * <p>主要字段</p>
             * <table>
             * <tr><th>字段名称</th><th>字段说明</th></tr>
             * <tr><td>type</td><td>网络状态</td></tr>
             * <tr><td>p1</td><td>设备p1值</td></tr>
             * </table>
             * @example
             *      clientInfo.getNetwork(function(status){
             *          console.log(status.type);
             * 	    });
             * @since 1.0.0
             */
            getNetworkType:function(callBack){
                this.sendData({
                    method: "getNetworkType",
                    param: {
                        "info": "getNetworkType"
                    },
                    callBack: callBack
                });
            },

            /**
             * 读取设备分辨率信息
             * @method getDevice
             * @public
             * @param {Function} callBack 回调函数
             * @return {Object}
             * {
             *    "model":"Nexus 4",
             *    "brand":"google",
             *    "device":"mako",
             *    "display":"768x1184",
             *    "product":"occam",
             *    "hardware":"mako",
             *    "density":"2",
             *    "densityDpi":"320",
             * }
             * <p>主要字段</p>
             * <table>
             * <tr><th>字段名称</th><th>字段说明</th></tr>
             * <tr><td>model</td><td>手机型号</td></tr>
             * <tr><td>brand</td><td>手机出品商</td></tr>
             * <tr><td>device</td><td>设备信息</td></tr>
             * <tr><td>hardware</td><td>设备信息</td></tr>
             * <tr><td>density</td><td>屏幕精度倍数</td></tr>
             * <tr><td>densityDpi</td><td>屏幕精度</td></tr>
             * </table>
             * @example
             *      clientInfo.getDevice(function(data){
             *          console.log(data);
             * 	    }
             * @since 1.0.0
             */
            getDevice:function(callBack){
                this.sendData({
                    method: "getDevice",
                    param: {
                        "info": "getDevice"
                    },
                    callBack: callBack
                });
            }
        });


		return Platform;
	})();


	return new Platform({
        name: "device infomation",
        author: "huangjian",
        version: "1.0",
        jsbc: jsBridgeClient,
        nativeCls: "platform"
    });
});