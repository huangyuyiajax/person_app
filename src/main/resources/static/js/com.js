var shangxiaoValue = getShangxiaoValue();
var shangxiaoValueJson = getShangxiaoValueJson();
function callData(url, param, succ,err) {
    $.ajax({
        async: false,
        url: url,
        type: 'post',
        dataType: 'json',
        data: param,
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            if(err){
                err(XMLHttpRequest, textStatus, errorThrown);
            }
        },
        success: function (data) {
            if(succ) {
                succ(data);
            }
        }
    });
}
function callDataJson(url, param, succ,err) {
    $.ajax({
        async: false,
        url: url,
        type: 'post',
        dataType: 'json',
        contentType: "application/json",
        data: JSON.stringify(param),
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            if(err){
                err(XMLHttpRequest, textStatus, errorThrown);
            }
        },
        success: function (data) {
            if(succ) {
                succ(data);
            }
        }
    });
}
function getShangxiao(){
    var now = new Date();
    var year = now.getFullYear();
    var ss = year - 2008;//设定2008为初始年份
    var ssc = ss%12;
    var ssyear = new Array("鼠","牛","虎","兔","龙","蛇","马","羊","猴","鸡","狗","猪");
    var jinnian = ssyear[ssc-1];//今年是?年
    var sx = ',猪,狗,鸡,猴,羊,马,蛇,龙,兔,虎,牛,鼠';
    var zb = sx.substring(sx.indexOf(jinnian),sx.length);
    var yb = sx.substring(0,sx.indexOf(jinnian)-1);
    var nsx =  zb+yb;
    var sxarr = nsx.split(',');
    return sxarr;
}
function getShangxiaoValue(){
    var sxarr = getShangxiao();//['兔', '虎', '牛', '鼠', '猪', '狗', '鸡', '猴', '羊', '马', '蛇', '龙']
    var sxJson = {};
    sxarr.forEach(function (val,i) {
        for(var j=0;j<5;j++){
            var value = j*12+i+1;
            if(value<50){
                sxJson[val] = (sxJson[val]?sxJson[val]+',':"")+value;
            }
        }
    })
    return sxJson;
}
function getShangxiaoValueJson(){
    var sxarr = getShangxiao();//['兔', '虎', '牛', '鼠', '猪', '狗', '鸡', '猴', '羊', '马', '蛇', '龙']
    var sxJson = {};
    var j = 0;
    for(var i=1;i<50;i++){
        if(j%12==0){
            j = 0;
        }
        sxJson[i<10?'0'+i:i] = sxarr[j];
        j++;
    }
    return sxJson;
}

function logout(e) {
    e.stopPropagation();
    callData("/action/logout", {}, function(res) {
        if (res.code == 0) {
            layer.msg("退出成功")
            window.location = '/app/login';
        }else {
            layer.alert(res.message)
        }
    });
}
function showDeil(event) {
    var $detil = $(event).parent('span').parent('li').next('.info');
    if($detil.is(':hidden')){
        $detil.show();
        $(event).text('收起')
    }else {
        $detil.hide();
        $(event).text('展开')
    }
}