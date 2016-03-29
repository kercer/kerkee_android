/**
 * SHFramework基础类工具
 * @class Utils
 * @extends Object
 * @constructor
 * @module api
 * @example
 *     define('api/helper/util',function(util){
 *          util.Klass().sub(ClientInfo);
 *     });
 * @since 1.0.0
 * @public
 * @kerkee
 */
define(["kerkee"],function(kerkee){
    var Utils = {

        Klass:function(){

            var extend = function(child, parent) { for (var key in parent) { if (hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; },
                bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; },
                hasProp = {}.hasOwnProperty,
                extendObj=function (target, source) {for (var p in source) {if (source.hasOwnProperty(p)) {target[p] = source[p];}}return target;};

            var BaseCls=(function(){

                /** 初始化参数
                 * 初始类对象字符串
                 * @property attributes (必选)
                 * @type Object
                 * @since 1.0.0
                 * @public
                 */
                function BaseCls(attributes) {
                    this.attributes=attributes;
                }

                /**
                 * 给基础类添加实例方法
                 * @method include
                 * @static
                 * @param {Object} obj 实例方法键值对
                 * @return {Null}
                 * @example
                 *      BaseCls.include({
                 *          addLog:function(type,log){}
                 * 	    })
                 * @since 1.0.0
                 */
                BaseCls.include=function(obj){
                    var included=obj.included;
                    for(var i in obj){
                        BaseCls.prototype[i]=obj[i];
                    }
                    if(included){
                        included(BaseCls);
                    }
                };

                /**
                 * 创建一个子类
                 * @method sub
                 * @static
                 * @param {Constructor} child 构建函数
                 * @return {Constructor}
                 * @example
                 *      BaseCls.sub(ClientInfo)
                 * @since 1.0.0
                 */
                BaseCls.sub=function(child){
                    return extend(child,this);
                };

                /**
                 * 与jsbridge进行通信
                 * @method _exec
                 * @private
                 * @param {Object} data js对象串
                 * @return {Null | Cllaback}
                 * @example
                 *      this._exec(data)
                 * @since 1.0.0
                 */
                BaseCls.prototype._exec=function(data){
                    if(!data.callBack){
                        this.attributes.jsbc.invoke(data.nativeCls,data.method,data.param);
                    }else{
                        this.attributes.jsbc.invoke(data.nativeCls,data.method,data.param,data.callBack);
                    }
                };

                /**
                 * 对_exec方法进行封装
                 * @method sendData
                 * @public
                 * @param {Object} param js对象串
                 * <p>主要参数</p>
                 * <table>
                 * <tr><th>参数名称</th><th>参数说明</th></tr>
                 * <tr><td>nativeCls</td><td>映射native类名</td></tr>
                 * <tr><td>method</td><td>映射映射native方法</td></tr>
                 * <tr><td>param</td><td>H5传递到native的json参数</td></tr>
                 * <tr><td>callback</td><td>native通知h5的回调</td></tr>
                 * </table>
                 * @return {Null | Callback}
                 * @example
                 *      this.sendData(data)
                 * @since 1.0.0
                 */
                BaseCls.prototype.sendData=function(param){
                    var data=extendObj({
                        nativeCls:this.attributes.nativeCls,
                        method:"",
                        param:{}
                    },param);
                    this._exec(data);
                };

                /**
                 * 设备初始化完成
                 * @method start
                 * @public
                 * @param {Function} callBack 回调函数
                 * @return {Null}
                 * @example
                 *      this.start(function(){})
                 * @since 1.0.0
                 */
                BaseCls.prototype.start=function(callBack){
                    this.attributes.jsbc.onDeviceReady(callBack);
                };

                return BaseCls;

            })();

            return BaseCls;
        }
    };
    return Utils;
});