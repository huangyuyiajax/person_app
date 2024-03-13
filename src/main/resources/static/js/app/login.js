$(function () {
    var accounts = localStorage.getItem('accounts');//[{"account":"10000","password":"1"}]
    $('#account').val(localStorage.getItem('account'));
    $('#password').val(localStorage.getItem('password'));
    $("input[name='area'][value=" + localStorage.getItem('area') + "]").attr("checked", true);
    $('#cookies').empty();
    if (accounts) {
        accounts = JSON.parse(accounts);
        $('#accounts').append('<option></option>');
        for (var i = 0; i < accounts.length; i++) {
            $('#accounts').append('<option value="' + accounts[i].account + '" data="'+accounts[i].password+'">' + accounts[i].account + '</option>');
        }
        $('#accounts').val("");
    }
})
document.onkeydown = function (e) { // 回车提交表单
    var theEvent = window.event || e;
    var code = theEvent.keyCode || theEvent.which || theEvent.charCode;
    if (code == 13) {
        login();
    }
}
function login() {
    var account = $('#account').val();
    if(account!='10000'){

        if (account && /^1(3\d|47|(5[0-3|5-9])|(7[0|7|8])|(8[0-3|5-9]))-?\d{4}-?\d{4}$/.test(account)) {
            if (account.length > 11 || account.length < 11) {
                layer.msg("请输入11位的手机号码");
                return;
            }
        } else {
            layer.msg("请输入正确的手机号码");
            return;
        }
    }
    var password = $('#password').val();
    if(!password){
        layer.msg("请输入密码");
        return;
    }
    var area = $("input[name='area']:checked").val();
    if(!area){
        layer.msg("请选择地区");
        return;
    }
    callData("/action/loginAction", {account:account,password:password,area:area}, function(res) {
        if (res.code == 0) {
            localStorage.setItem('account',account);
            localStorage.setItem('password',password);
            localStorage.setItem('area',area);
            var accounts = localStorage.getItem('accounts');//[{"account":"10000","password":"1"}]
            if(accounts&&accounts.indexOf('{')>0){
                accounts = JSON.parse(accounts);
                var flag = true;
                accounts.forEach(function (obj) {
                    if(obj.account == account){
                        flag = false;
                        obj.password = password;
                        localStorage.setItem('accounts',JSON.stringify(accounts));
                    }
                });
                if(flag){
                    accounts.push({"account":account,"password":password});
                    localStorage.setItem('accounts',JSON.stringify(accounts));
                }
            }else {
                localStorage.setItem('accounts',JSON.stringify([{"account":account,"password":password}]));
            }
            if(account=='10000'){
                window.location = '/app/admin';
            }else {
                window.location = '/app/index';
            }
        }else {
            layer.alert(res.message)
        }
    });
}
function changheAccounts(event) {
    $('#account').val($(event).find("option:selected").val());
    $('#password').val($(event).find("option:selected").attr("data"));
}