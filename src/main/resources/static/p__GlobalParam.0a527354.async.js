(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([[3],{"+5+v":function(e,t,a){e.exports={form:"antd-pro-pages-global-param-index-form"}},GfQJ:function(e,t,a){"use strict";var r=a("g09b");Object.defineProperty(t,"__esModule",{value:!0}),t.default=void 0,a("g9YV");var n=r(a("wCAj"));a("5NDa");var l=r(a("5rEg"));a("+L6B");var u=r(a("2/Rp"));a("+BJd");var d=r(a("mr32")),o=r(a("gWZ8"));a("y8nQ");var f=r(a("Vl3Y")),i=r(a("qIgq")),c=y(a("q1tI")),s=r(a("+5+v")),p=a("Hg0r");function m(e){if("function"!==typeof WeakMap)return null;var t=new WeakMap,a=new WeakMap;return(m=function(e){return e?a:t})(e)}function y(e,t){if(!t&&e&&e.__esModule)return e;if(null===e||"object"!==typeof e&&"function"!==typeof e)return{default:e};var a=m(t);if(a&&a.has(e))return a.get(e);var r={},n=Object.defineProperty&&Object.getOwnPropertyDescriptor;for(var l in e)if("default"!==l&&Object.prototype.hasOwnProperty.call(e,l)){var u=n?Object.getOwnPropertyDescriptor(e,l):null;u&&(u.get||u.set)?Object.defineProperty(r,l,u):r[l]=e[l]}return r.default=e,a&&a.set(e,r),r}var v=function(){var e=f.default.useForm(),t=(0,i.default)(e,1),a=t[0],r=(0,c.useState)(new Map),p=(0,i.default)(r,2),m=p[0],y=p[1],v=function(){var e=a.getFieldsValue(),t=new Map(m);t.set(a.getFieldValue("key"),e),localStorage.setItem("easy-doc-global-params",JSON.stringify((0,o.default)(t))),y(t)},g=function(e){var t=new Map(m);t.delete(e.key),localStorage.setItem("easy-doc-global-params",JSON.stringify((0,o.default)(t))),y(t)};(0,c.useEffect)(function(){var e=localStorage.getItem("easy-doc-global-params");e&&y(new Map(JSON.parse(e)))},[]);var k=[{title:"Key",dataIndex:"key",key:"key"},{title:"Value",dataIndex:"value",key:"value"},{title:"\u63cf\u8ff0",dataIndex:"description",key:"description",render:function(e){return c.default.createElement(d.default,{color:"green",key:"description"},e)}},{title:"\u64cd\u4f5c",dataIndex:"action",key:"action",render:function(e,t){return c.default.createElement(u.default,{type:"danger",shape:"round",onClick:function(){return g(t)}},"\u5220\u9664")}}];return c.default.createElement(c.default.Fragment,null,c.default.createElement(f.default,{layout:"inline",form:a,onFinish:v,className:s.default.form},c.default.createElement(f.default.Item,{name:"key",rules:[{required:!0}]},c.default.createElement(l.default,{addonBefore:"Key"})),c.default.createElement(f.default.Item,{name:"value",rules:[{required:!0}]},c.default.createElement(l.default,{addonBefore:"Value"})),c.default.createElement(f.default.Item,{name:"description"},c.default.createElement(l.default,{className:s.default.input,addonBefore:"\u63cf\u8ff0"})),c.default.createElement(f.default.Item,null,c.default.createElement(u.default,{type:"primary",shape:"round",htmlType:"submit"},"\u6dfb\u52a0"))),c.default.createElement(n.default,{key:"key",dataSource:Array.from(m.values()),columns:k,pagination:!1}))},g=(0,p.connect)(function(e){var t=e.service;return{serviceData:t.serviceData}})(v);t.default=g}}]);