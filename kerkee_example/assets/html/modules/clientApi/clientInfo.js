/**
 * 获取客户端相关信息
 * @class ClientInfo
 * @extends Klass
 * @constructor
 * @module client
 * @example
 *     define('api/client/clientInfo',function(clientInfo){
 *          clientInfo.addLog();
 *     });
 * @since 1.0.0
 * @public
 */

define(['api/helper/util'], function (util) {

    var ClientInfo = (function () {

        //构建一个ClientInfo类,继承自基础类
        util.Klass().sub(ClientInfo);

        //构造函数
        function ClientInfo() {
            /**
             * 映射客户端类的名称 <strong>(必选)</strong>
             * @property nativeCls
             * @type string
             * @since 1.0.0
             * @default "clientInfo"
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

            ClientInfo.__super__.constructor.apply(this, arguments);
        }

        ClientInfo.include({

            /**
             * 获取当前客户端的服务接口域名信息
             * @method getHost
             * @public
             * @param {Function} callBack 回调函数
             * @return {Object}
             * {info: "http://api.k.sohu.com/"}
             * @example
             *        clientInfo.getHost(function(host){
             *          console.log(host.info);
             * 	    }
             * @since 1.0.0
             */
            getHost: function (callBack) {
                this.sendData({
                    method: "getHost",
                    param: {
                        "info": "getHost"
                    },
                    callBack: callBack
                });
            },


            /**
             * 获取当前客户端的基础信息，如:p1,diu,gid等
             * @method getRequestParam
             * @public
             * @param {Function} callBack 回调函数
             * @return {Object}
             * {
             *   p1: "hNjAxMjIzODg3Nzc3NDIyOTU4Mg==",
             *   pid:"",
             *   token: "",
             *   gid: "02ffff110611118bce83f5a21bd814a1a7c22c2fd44308",
             *   apiVersion:"29",
             *   sid:"10",
             *   productId:"5",
             *   sCookie:"....."
             * }
             * <p>主要字段</p>
             * <table>
             * <tr><th>字段名称</th><th>字段说明</th></tr>
             * <tr><td>p1</td><td>设备p1值</td></tr>
             * <tr><td>pid</td><td>设备pid值</td></tr>
             * <tr><td>token</td><td>设备token值</td></tr>
             * <tr><td>gid</td><td>设备gid值</td></tr>
             * <tr><td>apiVersion</td><td>请求服务接口的api版本号</td></tr>
             * <tr><td>sid</td><td>设备sid值</td></tr>
             * <tr><td>productId</td><td>设备productId值</td></tr>
             * <tr><td>sCookie</td><td>设备sCookie,写入http请求的header中</td></tr>
             * </table>
             * @example
             *        clientInfo.getRequestParam(function(param){
             *          console.log(param.p1);
             * 	    }
             * @since 1.0.0
             */
            getRequestParam: function (callBack) {
                this.sendData({
                    method: "getRequestParam",
                    param: {
                        "info": "getRequestParam"
                    },
                    callBack: callBack
                });
            },

            /**
             * 获取当前客户端的文章内容请求字段
             * @method getArticleNeedParam
             * @public
             * @param {Function} callBack 回调函数
             * @return {Object}
             * {
             *    param: "newsId=58374047&subId=7614&from=newslite&channelId=1&position=2&page=1&cmt=2335",
             *    secondProtocal: "news://newsId=58374047&subId=7614&from=newslite&channelId=1&position=2&page=1&cmt=2335"
             * }
             * <p>主要字段</p>
             * <table>
             * <tr><th>字段名称</th><th>字段说明</th></tr>
             * <tr><td>param</td><td>文章请求参数关键字段</td></tr>
             * <tr><td>secondProtocal</td><td>新闻二代协议</td></tr>
             * </table>
             * @example
             *      clientInfo.getHost(function(host){
             *          console.log(host.info);
             * 	    }
             * @since 1.0.0
             */
            getArticleNeedParam: function (callBack) {
                this.sendData({
                    method: "getArticleNeedParam",
                    param: {
                        "info": "getArticleNeedParam"
                    },
                    callBack: callBack
                });
            },

            /**
             * h5调用native的log收集器
             * @method addLog
             * @public
             * @param {String} type 日志类型
             * @param {String} log  日志内容
             * @return {Null} void
             * @example
             *      var logObj={
             *          Type:"newssdk",
             *          statType:"read",
             *          protocol: t.secondProtocal
             * 	    };
             * 	    clientInfo.addLog("0","Type="+logObj.Type+"&="+logObj.statType+"&="+logObj.protocol);
             * @since 1.0.0
             */
            addLog: function (type, log) {
                this.sendData({
                    method: "addLog",
                    param: {
                        "type": type,
                        "log": log
                    }
                });
            },

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
             * 	    }
             * @since 1.0.0
             */
            getNetwork: function (callBack) {
                this.sendData({
                    method: "getNetwork",
                    param: {
                        "info": "getNetwork"
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
            getDevice: function (callBack) {
                this.sendData({
                    method: "getDevice",
                    param: {
                        "info": "getDevice"
                    },
                    callBack: callBack
                });
            },


            /**
             * 设置正文评论数
             * @method setArticleCmtCount
             * @public
             * @param {String} count 评论数量
             * @return {Null} void
             * @example
             * 	   clientInfo.setArticleCmtCount("20");
             * @since 1.0.0
             */
            setArticleCmtCount: function (count) {
                this.sendData({
                    method: "setArticleCmtCount",
                    param: {
                        "count": count
                    }
                });
            }
        });

        return ClientInfo;

    })();

    return new ClientInfo({
        name: "client infomation",
        author: "huangjian",
        version: "1.0",
        jsbc: jsBridgeClient,
        nativeCls: "clientInfo"
    });
});