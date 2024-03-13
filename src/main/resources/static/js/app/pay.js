$(function() {
    window.addEventListener('popstate', function(e) {
        //监听到返回事件
        window.location = "/app/pay?amount="+$('#amount').val();
    }, false);

    $("#toPay").click(function () {
        $('#toPay').attr('disabled', "disabled");
        var amount = $('#amount').val();
        var openid = $('#openid').val();
        callData("/pay/unifiedorder", {amount: amount, openid: openid}, function (res) {
            if (res.code == 0) {
                var prePayResult = res.data;
                if (typeof WeixinJSBridge == "undefined") {
                    if (document.addEventListener) {
                        document.addEventListener('WeixinJSBridgeReady', onBridgeReady(prePayResult), false);
                    } else if (document.attachEvent) {
                        document.attachEvent('WeixinJSBridgeReady', onBridgeReady(prePayResult));
                        document.attachEvent('onWeixinJSBridgeReady', onBridgeReady(prePayResult));
                    }
                } else {
                    onBridgeReady(prePayResult);
                }
            } else {
                layer.alert(res.message)
            }
        });
        $('#toPay').removeAttr("disabled");
    });

    function onBridgeReady(prePayResult) {
        WeixinJSBridge.invoke(
            'getBrandWCPayRequest', {
                "appId": prePayResult.appId,     //公众号名称，由商户传入
                "timeStamp": prePayResult.timeStamp,         //时间戳，自1970年以来的秒数
                "nonceStr": prePayResult.nonceStr, //随机串
                "package": prePayResult.package,
                "signType": prePayResult.signType,         //微信签名方式：
                "paySign": prePayResult.paySign //微信签名
            },
            function (res) {
                if (res.err_msg == "get_brand_wcpay_request:ok") {// 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。
                    var state = {
                        title: "title",
                        url: "#"
                    };
                    window.history.pushState(state, "title", "#");
                }
            }
        );
    }

    $("#cancelPay").click(function () {
        $.modal({
            title: '确定离开支付中心？',
            text: '你离开支付中心后，订单将被取消',
            buttons: [{
                text: '取消',
                onClick: function () {
                }
            }, {
                text: '确定',
                onClick: function () {
                    window.location = '/app/index';
                }
            }],
            extraClass: "my-skin"
        })
    });
})