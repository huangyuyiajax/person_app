$(function() {
    $("#toTransfers").click(function () {
        $('#toTransfers').attr('disabled', "disabled");
        var amount = $('#amount').val();
        var openid = $('#openid').val();
        callData("/pay/transfers", {amount: amount, openid: openid}, function (res) {
            if (res.code == 0) {
                layer.msg("提现成功")
            } else {
                layer.alert(res.message)
            }
        });
        $('#toTransfers').removeAttr("disabled");
    });


    $("#cancelTransfers").click(function () {
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