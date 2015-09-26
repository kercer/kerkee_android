/**
 * 调用新闻客户端相关UI
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
