/**
 * Created by huangjian on 15-3-25.
 */
define(["zepto", "template"], function ($, template) {

    var Utils = {

        /**
         * 转换10000变成文字"万"
         * @param {Int} value
         * @return {String}
         */
        parseNum:function(value){
            var _val="";
            if(value>10000){
                _val=(parseInt(value)/10000).toFixed(1)+"万";
            }else{
                _val=value;
            }
            return _val;
        },

        /*
         * 调试页面方法
         */
        reloadPage:function(){
            var html='<div id="reloadPage_debug" class="debug-page">R</div>';
            $("body").append(html);
        },

        parseTemplate: function (id, artmpl, data) {
            var objE = document.createElement("div");
            var timesup = new Date().getTime();
            objE.id = "artmplBox" + timesup;
            var template_id = "artmpl_" + id;
            objE.innerHTML = '<script type="text/html" id="' + template_id + '">' + artmpl + '</script>';
            objE.style.display = "none";
            document.body.appendChild(objE);
            var template_cont = template(template_id, data);
            document.body.removeChild(objE);
            return template_cont;
        },

        /**
         * artempalte解析模板的方法封装
         * @param {String} template_id 模板id
         * @param {Object} data 传入的json对象
         * @return {String} html 返回解析完成后的html
         */
        compileTempl: function (template_id, data) {
            var template_cont = template(template_id, data);
            return template_cont;
        },

        /**
         * 判断是否为一个空对象{}
         * @param {Object} js对象
         * @return {Boolean} true | false
         */
        isEmptyObject: function( obj ) {
            var name;
            for ( name in obj ) {
                return false;
            }
            return true;
        },


        /**
         * 实现一个js对象的基类,具有与native通信,原型继承,deviceready等方法
         */
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
        },

        //日期格式初始化
        getLocalTime:function(unixtime,pattern){
            Date.prototype.format = function(pattern){
                var pad = function (source, length) {
                    var pre = "",
                        negative = (source < 0),
                        string = String(Math.abs(source));

                    if (string.length < length) {
                        pre = (new Array(length - string.length + 1)).join('0');
                    }

                    return (negative ?  "-" : "") + pre + string;
                };

                if ('string' != typeof pattern) {
                    return this.toString();
                }

                var replacer = function(patternPart, result) {
                    pattern = pattern.replace(patternPart, result);
                }

                var year    = this.getFullYear(),
                    month   = this.getMonth() + 1,
                    date2   = this.getDate(),
                    hours   = this.getHours(),
                    minutes = this.getMinutes(),
                    seconds = this.getSeconds();

                replacer(/yyyy/g, pad(year, 4));
                replacer(/yy/g, pad(parseInt(year.toString().slice(2), 10), 2));
                replacer(/MM/g, pad(month, 2));
                replacer(/M/g, month);
                replacer(/dd/g, pad(date2, 2));
                replacer(/d/g, date2);

                replacer(/HH/g, pad(hours, 2));
                replacer(/H/g, hours);
                replacer(/hh/g, pad(hours % 12, 2));
                replacer(/h/g, hours % 12);
                replacer(/mm/g, pad(minutes, 2));
                replacer(/m/g, minutes);
                replacer(/ss/g, pad(seconds, 2));
                replacer(/s/g, seconds);

                return pattern;
            };
            var timestr = new Date(parseInt(unixtime));
            var datetime = timestr.format(pattern);
            return datetime;
        },

        //判断当前系统是ios或andorid
        OS:function(){
            var os = navigator.userAgent.match(/iphone|ipad|ipod/i) ? "ios" : "android";
            $("html").addClass(os);
            return os;
        },

        /*读取查询字符串*/
        getQueryString:function(name){
            var params = location.search.substring(1).toLowerCase();
            var paramList = [];
            var param = null;
            var parami;
            if (params.length > 0) {
                if (params.indexOf("&") >= 0) {
                    paramList = params.split("&");
                } else {
                    paramList[0] = params;
                }
                for (var i = 0, listLength = paramList.length; i < listLength; i++) {
                    parami = paramList[i].indexOf(name + "=");
                    if (parami >= 0) {
                        param = paramList[i].substr(parami + (name + "=").length);
                    }
                }
            }
            return param;
        },

        //新闻列表内容去重
        uniqueData: function () {

            var allDataCache = [];
            var filterNum = 0;

            return function (dataList, key, repeat) {

                var tempData = [];
                var _repeat;

                if (repeat == null) {
                    _repeat = 5;
                }else{
                    _repeat=repeat;
                }

                if (filterNum == _repeat) {
                    allDataCache = [];
                    filterNum = 1;
                } else {
                    filterNum++;
                }

                for (var i = 0, len = dataList.length; i < len; i++) {
                    var item = dataList[i][key];
                    if (allDataCache.indexOf(item) < 0) {
                        allDataCache.push(item);
                        tempData.push(dataList[i]);
                    }
                }

                return {
                    allData: allDataCache,
                    data: tempData,
                    filterNum: filterNum
                };
            };
        },

        //查询字符串转成js对象
        query2obj:function(url){
            var obj={};
            var reg=new RegExp("^"+"http");
            var queryString;

            if(reg.test(url)){
                queryString=url.substring(url.indexOf("?")+1,url.length);
            }else{
                queryString=url;
            }

            if(queryString){
                var _arr=queryString.split("&"),
                    len=_arr.length;

                for (var i = 0; i < len; i++) {
                    var item=_arr[i].split('=');
                    var key=item[0];
                    var value=item[1];
                    obj[key]=value;
                }
                return obj;
            }
        },

        /*随机数*/
        getRandomNum: function (min, max) {
            var range = max - min;
            var rand = Math.random();
            return (min + Math.round(rand * range));
        },


        /**
         * 空闲控制 返回函数连续调用时，空闲时间必须大于或等于 idle，action 才会执行
         * @param idle   {number}    空闲时间，单位毫秒
         * @param action {function}  请求关联函数，实际应用需要调用的函数
         * @return {function}    返回客户调用函数
         */
        debounce : function(func, wait, immediate){
            // immediate默认为false
            var timeout, args, context, timestamp, result;

            var later = function() {
                // 当wait指定的时间间隔期间多次调用_.debounce返回的函数，则会不断更新timestamp的值，导致last < wait && last >= 0一直为true，从而不断启动新的计时器延时执行func
                var last = new Date().getTime() - timestamp;

                if (last < wait && last >= 0) {
                    timeout = setTimeout(later, wait - last);
                } else {
                    timeout = null;
                    if (!immediate) {
                        result = func.apply(context, args);
                        if (!timeout) context = args = null;
                    }
                }
            };

            return function() {
                context = this;
                args = arguments;
                timestamp = new Date().getTime();
                // 第一次调用该方法时，且immediate为true，则调用func函数
                var callNow = immediate && !timeout;
                // 在wait指定的时间间隔内首次调用该方法，则启动计时器定时调用func函数
                if (!timeout) timeout = setTimeout(later, wait);
                if (callNow) {
                    result = func.apply(context, args);
                    context = args = null;
                }

                return result;
            };
        },


        //ajax请求方法封装
        sendData:function(type,url,data,successs,before,error,complate,headers,dataType){
            $.ajax({
                type:type,
                url:url,
                timeout:200000,
                data:data,
                headers:{
                    "SCOOKIE":headers
                },
                dataType:dataType||"json",
                beforeSend:before||function(){

                },
                success:successs,
                error:error||function(){

                },
                complete:complate||function(){

                }
            });
        },

        /*scrollView*/
        scrollViewer: {
            getTopHeight: function () {
                return {
                    pTop: window.pageYOffset || document.documentElement.scrollTop,
                    pHeight: document.documentElement.clientHeight || window.innerHeight
                };
            },
            /**
             * 当页面滚动到可视区域时处理某事件
             * @param ele
             * @param options {iScreens:2,pTop:100,pHeight:200，handle:function(){}}
             */
            inViewPort: function (ele, options) {
                var me = this,
                    iScreens = 1, //1=当前屏;2=下一屏
                    pTop = options.pTop, //scrollTop
                    pHeight = options.pHeight, //clientHeight
                    pBottom,
                    $document=$(document),
                    $window=$(window);

                if (options.iScreens) {
                    iScreens = options.iScreens;
                }

                if (!pTop) {
                    pTop = me.getTopHeight().pTop;
                }

                if (!pHeight) {
                    pHeight = me.getTopHeight().pHeight;
                }

                pBottom = pTop + pHeight * iScreens;

                var fn = function () {
                    if (typeof(options.handle) == "function") {
                        options.handle();
                    }
                };

                if (ele) {
                    if (ele.getBoundingClientRect) {
                        var eleTop = ele.getBoundingClientRect().top + pTop,
                            eleBottom = eleTop + ele.clientHeight;
                        //可视区域范围(eleTop > pTop && eleTop < pBottom) && (eleBottom > pTop && eleBottom < pBottom)
                        //浏览过的视图范围 eleTop>=0 && pBottom-eleBottom>=0
                        if ((eleTop > pTop && eleTop < pBottom) && (eleBottom > pTop && eleBottom < pBottom)) {
                            fn();
                        }
                    } else {
                        var scrollPos =$window.scrollTop();
                        var totalHeight = parseFloat($window.height()) + parseFloat(scrollPos);
                        if ((($document.height() - 20) <= totalHeight)) {
                            fn();
                        }
                    }
                }
            },

            create:function(id,callBack){
                var ele=document.querySelector(id);
                var pPos = Utils.scrollViewer.getTopHeight();
                Utils.scrollViewer.inViewPort(ele, {
                    iScreens: 1,
                    pTop: pPos.pTop,
                    pHeight: pPos.pHeight + 150,
                    handle: function () {
                        callBack();
                    }
                });
            }
        },

        // 设置夜间模式 1夜间  0日间
        chgMode:function chgMode(num) {
            var $ele = $("#myMode");
            if ($ele.size()>0) {
                if ($ele.hasClass("Mode1")) {
                    if (!num) {
                        $ele.removeClass("Mode1");
                    }
                    return;
                } else {
                   if(num==1){
                       $ele.addClass("Mode1");
                   }else{
                       $ele.removeClass("Mode1")
                   }
                }
            }
        },

        //onload 时检测是否是夜间模式  解决安卓打包情况下夜间与日间模式的切换
        checkIfNight:function () {
            if (Utils.getQueryString("mode") == "1") {
                Utils.chgMode(1);
            } else {
                Utils.chgMode(0);
            }
        },

        // 设置无图模式 1无图  0有图
        chgImgMode:function chgMode(num) {
            var $ele = $("#myMode");
            if ($ele) {
                if ($($ele).hasClass("noImgMode")) {
                    if (!num) {
                        $ele.removeClass("noImgMode");
                    }
                    return;
                } else {
                    if(num==1){
                        $ele.addClass("noImgMode");
                    }else{
                        $ele.removeClass("noImgMode")
                    }
                }
            }
        },

        //检测是否为无图模式
        checkImgMode:function () {
            var imgMode="";
            if (Utils.getQueryString("imgs") == "1") {
                Utils.chgImgMode(1);
            } else {
                Utils.chgImgMode(0);
            }
        }

    };
    return Utils;
});
