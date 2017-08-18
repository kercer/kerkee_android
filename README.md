---
name: 简介
---

# kerkee [ ![Download](https://api.bintray.com/packages/zihong/maven/kerkee/images/download.svg) ](https://bintray.com/zihong/maven/kerkee/_latestVersion)

### kerkee框架的诞生背景
Hybrid App兼具了Native App的所有优势，也兼具了Web App使用HTML5跨平台开发低成本的优势。以及具有使用Native扩展Web接口以弥补web无法调用平台性API等优势。Hybrid App也是未来客户端技术的发展趋势。HTML5的出现，市面上已有很多应用（如淘宝、百度搜索、高德地图）使用了Hybrid App的开发模式，但遇到了一些瓶颈（如性能不如预期，无法操作浏览器内部数据流程，无法自定义请求的需求），用户体验没有预期的好。
基于此，一种新一开发模式诞生了！kerkee框架是市面上唯一的多主体共存的灵活混合型开发模型。

### kerkee框架是什么
kerkee是一个多主体共存型Hybrid框架，具有跨平台、用户体验好、性能高、扩展性好、灵活性强、易维护、规范化、集成云服务、具有Debug环境、彻底解决跨域问题。

### kerkee官网
-  **官网：** [http://www.kerkee.com](http://www.kerkee.com)

-  **github:** [https://github.com/kercer](https://github.com/kercer)

-  **QQ交流群：** 110710084


### 使用kerkee案例
kerkee的Hybrid架构思想已使用在两款亿级用户量及多款千万级用户量的APP上

- UC游戏大厅
- 九游游戏中心
- 天翼导航
- 搜狐新闻客户端
- 搜狐News SDK（已应用到搜狐视频客户端中）
- 斗米客户端（商户端、用户端、斗米工作助手）
- 汽车之家


### 基于kerkee框架的开发模式

从开发者角度来说，它支持三种的团队开发模式：

>1. **针对Web开发者**
<br/> 这种模式其中的一个场景是：只会Web开发，却不会Native开发的开发者提供了一系列的平台型接口。这种方式具有开发周期短，跨平台等优点。
>1. **针对Native开发者** 
<br/> 这种开发模式的其中一个场景是：Native开发者想要截获Web页面的数据或者对数据进行自己的处理，或者Web页面中的行为进行修改。在这个时候，Kerkee框架将会为他们带来便利。
>1. **针对Web开发者和Native团队共同合作的开发团队** 
<br/> 对于这种模式的团队，kerkee框架具体更开放更透明的协作，并且严格地隔离各自职责。各得Web团队和Native团队把主要精力定位到各自的模块上，有利于各自的模块优化到极致。

### kerkee框架特性和能解决的问题
>1. **跨平台**
<br/> kerkee是Hybrid App框架，业务HTML5开发，HTML5具备了跨平台的特性，因此Kerkee也具备了跨平台的特性。
>1. **用户体验好**
<br/> 所有的web接口都可在Native自定义实现，即Web UI或数据操作上若满足不了用户体验，皆可通原生的代码进行实现，以达到较佳的用户体验。
>1. **性能高**
<br/> 在性能方面，kerkee框架做了大量的优化，底层网络层、IO操作等皆采用C/C++实现，并且重写了WebView，对Web中资源（如图片资源等）的控制，以事件驱动模型实现资源请求，并且实现一套针对Web的缓存策略，完全抛离webview那一套数据流程的操作。使开发者具有更透明的数据操作，以达到更佳的性能，并且突破了原有开发模式下的性能瓶颈。
>1. **扩展性好**
<br/> kerkee框架采用插件式模块化设计，每个模块即为一个webapp，在需要时可自由扩展。
>1. **灵活性强**
<br/> kerkee内部实现runtime，自动把js接口转化了native接口，Web层与Native层严格隔离达到无耦合状态，开发者对整个流程和接口都是开放透明，无特殊约束。
>1. **易维护**
<br/> kerkee框架使客户端严格模块化，使用了接口式的交互模型，具有动态更新特性，易于维护，便以运营。
>1. **规范化**
<br/> kerkee框架符合W3C标准，重新实现了XMLHttpRequest、WebSocket、LocalStorage、Application Cache等HTML5特性。Web前端开发者只需按W3C标准编写代码即可，即一次编写，到处运行的原则，无任何第三方库依赖。
>1. **Debug工具**
<br/> kerkee内部集成Debug环境，web端log将会打印到控制台或文件中
>1. **彻底解决跨域**
<br/> kerkee框架采用一套特殊机制，解决了跨域问题，也就是说开发者可以操作互联网上任何一个Web页面的数据。
>1. **使客户端架构更清晰**
<br/> kerkee框架会使得客户端的架构更为清晰。整体结构，自上而下分层如下图所示：
<br/> [![client](http://src.linzihong.com/clientframe.jpg)](http://src.linzihong.com/clientframe.jpg)

>1. **使用简便**
<br/> 对于开发者来说，使用简便。
<br/> Web开发者：无需添加其他代码，只需要按W3C规范实现代码即可。若要调用Native接口，只需要调用框架中对应的fuction即可。
<br/> Native开发者：只需要把对应的类注册到Kerkee中即可，代码量不超过5行便可使用Kerkee框架