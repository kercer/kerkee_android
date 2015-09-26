/**
 * 调用客户端相关UI
 * @class ClientUI
 * @extends Klass
 * @constructor
 * @module client
 * @example
 *     define('api/client/clientUI',function(clientUI){
 *          clientUI.showLoadingView();
 *     });
 * @since 1.0.0
 * @public
 */
define(['api/helper/util'],function(util){

    var ClientUI = (function() {

        //构建一个ClientUI类,继承自基础类
        util.Klass().sub(ClientUI);

        //构造函数
        function ClientUI() {
            /**
             * 映射客户端类的名称 <strong>(必选)</strong>
             * @property nativeCls
             * @type string
             * @since 1.0.0
             * @default "clientUI"
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
            ClientUI.__super__.constructor.apply(this, arguments);
        }

        ClientUI.include({
            /**
             * 调用新闻客户端的toast
             * @method clientToast
             * @public
             * @param {String} msg 消息文本 (必选)
             * @param {String} type toast类型 (可选)
             * @return {Null}
             * @example
             *        ClientUI.clientToast("hello","notice")
             * @since 1.0.0
             */
            clientToast:function(msg,type){
                this.sendData({
                    method:"clientToast",
                    param:{
                        "msg":msg,
                        "type":type||"notice"   //notice | warn
                    }
                });
            },

            /**
             * h5调用native的download
             * @method clientDownloadMainVer
             * @public
             * @param {Int} type 下载类型: 1 - 收藏 | 2 - 评论
             * @return {Null}
             * @example
             *        ClientUI.clientDownloadMainVer(1)
             * @since 1.0.0
             */
            clientDownloadMainVer:function(type){
                this.sendData({
                    method:"clientDownloadMainVer",
                    param:{
                        "info":"clientDownloadMainVer",
                        "type":type  //1:收藏| 2:评论
                    }
                });
            },

            /**
             * h5调用native的城市切换
             * @method switchLocation
             * @public
             * @param {Null} none
             * @return {Null} void
             * @example
             *        ClientUI.switchLocation()
             * @since 1.0.0
             */
            switchLocation:function(){
                this.sendData({
                    method:"switchLocation",
                    param:{
                        "info":"switchLocation"
                    }
                });
            },

            /**
             * 返回新闻列表首页
             * @method backHeadChannel
             * @public
             * @param {Null} none
             * @return {Null} void
             * @example
             *        ClientUI.backHeadChannel()
             * @since 1.0.0
             */
            backHeadChannel:function(){
                this.sendData({
                    method:"backHeadChannel",
                    param:{
                        "info":"backHeadChannel"
                    }
                });
            },

            /**
             * H5调用页面初始化loading
             * @method showLoadingView
             * @public
             * @param {String} channelid 频道id
             * @param {Boolean} status loading可见: true - 显示 | false - 隐藏
             * @return {Null} void
             * @example
             *        ClientUI.showLoadingView("25",true)
             * @since 1.0.0
             */
            showLoadingView:function(channelid,status){
                this.sendData({
                    method:"showLoadingView",
                    param:{
                        "visible":status,    // true | false
                        "channelid":channelid
                    }
                });
            },

            /**
             * H5调用页面无网络提示
             * @method showLoadingFailedView
             * @public
             * @param {String} channelid 频道id
             * @param {Boolean} status loading可见: true - 显示 | false - 隐藏
             * @return {Null} void
             * @example
             *        ClientUI.showLoadingFailedView("25",true)
             * @since 1.0.0
             */
            showLoadingFailedView:function(channelid,status){
                this.sendData({
                    method:"showLoadingFailedView",
                    param:{
                        "visible":status,    // true | false
                        "channelid":channelid
                    }
                });
            },

            /**
             * h5调用移动网络流量提示
             * @method showVideoPlayNoWifiConfirmDialog
             * @public
             * @param {Function} callBack 回调函数
             * @return {Object}
             * {type: "1"}
             * @example
             *        ClientUI.showVideoPlayNoWifiConfirmDialog(function(data){
             *           console.log(data);
             *        })
             * <p>主要字段</p>
             * <table>
             * <tr><th>type</th><th>说明</th></tr>
             * <tr><td>1</td><td>继续播放</td></tr>
             * <tr><td>0</td><td>停止播放</td></tr>
             * </table>
             * @since 1.0.0
             */
            showVideoPlayNoWifiConfirmDialog:function(callBack){
                this.sendData({
                    method:"showVideoPlayNoWifiConfirmDialog",
                    param:{
                        "info":"showVideoPlayNoWifiConfirmDialog"
                    },
                    callBack:callBack
                });
            }
        });

        return ClientUI;

    })();

    return new ClientUI({
        name:"client ui",
        author:"huangjian",
        version:"1.0",
        jsbc:jsBridgeClient,
        nativeCls:"clientUI"
    });

});
