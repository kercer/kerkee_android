/**
 * 原生系统组件调用
 * @class Widget
 * @extends Klass
 * @constructor
 * @module nativeUI
 * @example
 *     define('api/nativeUI/widget',function(widget){
 *          widget.toast();
 *     });
 * @since 1.0.0
 * @public
 */

define(['api/helper/util'], function (util) {

    var Widget = (function () {

        //构建一个Widget类,继承自基础类
        util.Klass().sub(Widget);

        //构造函数
        function Widget() {
            /**
             * 映射客户端类的名称 <strong>(必选)</strong>
             * @property nativeCls
             * @type string
             * @since 1.0.0
             * @default "widget"
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

            Widget.__super__.constructor.apply(this, arguments);
        }

        Widget.include({
            /**
             * h5调用系统原生toast
             * @method toast
             * @public
             * @param {String} info 文本信息
             * @return {Null} void
             * @example
             *      widget.toast(info);
             * @since 1.0.0
             */
            toast:function(info){
               this.sendData({
                   method: "toast",
                   param: {
                       "info": info
                   }
               });
            },

            /**
             * h5调用系统原生dialog，显示效果类似promt
             * @method showDialog
             * @public
             * @param  {String} title 标题文本
             * @param  {String} message 内容文本
             * @param  {String} okBtn 确定文本
             * @param  {String} cancelBtn 取消文本
             * @param {Function} fn 回调函数
             * @return {Object} type 1 - 确定 | 0 - 取消
             * {
             *    "type":"0"
             * }
             * <p>主要字段</p>
             * <table>
             * <tr><th>type</th><th>说明</th></tr>
             * <tr><td>1</td><td>确定</td></tr>
             * <tr><td>0</td><td>取消</td></tr>
             * </table>
             * @example
             *      widget.showDialog("提示","内容",function(data){},"确定","取消");
             * @since 1.0.0
             */
            showDialog:function(title,message,fn,okBtn,cancelBtn){
                this.sendData({
                    method: "showDialog",
                    param: {
                        "title" : title || "提示",
                        "message" :message || "内容",
                        "okBtn" : okBtn || "确定",
                        "cancelBtn" :cancelBtn || "取消"
                    },
                    callBack:fn
                });
            },


            /**
             * h5调用系统原生dailog，显示效果类似alert
             * @method alertDialog
             * @public
             * @param  {String} title 标题文本
             * @param  {String} message 内容文本
             * @param  {String} okBtn 确定文本
             * @param {Function} fn 回调函数
             * @return {Object} type 1 - 确定
             * {
             *    "type":"1"
             * }
             * <p>主要字段</p>
             * <table>
             * <tr><th>type</th><th>说明</th></tr>
             * <tr><td>1</td><td>确定</td></tr>
             * </table>
             * @example
             *      widget.alertDialog("提示","内容",function(data){},"确定");
             * @since 1.0.0
             */
            alertDialog:function(title,message,fn,okBtn){
                this.sendData({
                    method: "showDialog",
                    param: {
                        "title" : title || "提示",
                        "message" :message || "内容",
                        "okBtn" : okBtn || "确定"
                    },
                    callBack:fn
                });
            }
        });

        return Widget;

    })();


	return new Widget({
        name: "device widget",
        author: "huangjian",
        version: "1.0",
        jsbc: jsBridgeClient,
        nativeCls: "widget"
    });
});