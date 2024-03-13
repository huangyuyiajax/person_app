$(function() {
    var mySwiper = new Swiper('.swiper-container',{
        initialSlide: $('#index').val()?$('#index').val():0, // 坐标索引值初始是第几个
        on:{
            init: function(){
                console.info("init")
                this.emit('slideChangeTransitionEnd');//在初始化时触发一次slideChangeTransitionEnd事件
            },
            slideChangeTransitionEnd: function(){
                $('.navbar').find("a").removeClass("acctive");
                var index = this.activeIndex;
                if(index==3){
                    index = 2;
                }
                var $a = $('.navbar').find("a[data-value="+index+"]");
                $a.addClass("acctive");
                navbarAcctive(this.activeIndex);
            }
        }
    });
    $('.navbar').find("a").on('click', function () {
        $('.navbar').find("a").removeClass("acctive");
        $(this).addClass("acctive");
        var index = $(this).data("value");
        mySwiper.slideTo(index, 1000, true);
        navbarAcctive(index);
    });
    $('#order .edit-bottom .check-many').on('click',function () {
        if($(this).hasClass("success")){
            $(this).removeClass("success");
        }else {
            $(this).addClass("success");
        }
    });
    $('#order .edit-bottom .check-confirm').on('click',function () {
        var code = "";
        $('#order').find(".edit-bottom .check-many.success").each(function(){
            $(this).val($.trim($(this).val()));
            code += $(this).text()+"，";
        });
        if(!code){
            return layer.msg("请至少选择一项");
        }
        layer.prompt({
            formType: 0,
            value: '',
            title: "包"+code+'输入金额',
            maxlength: 11
        }, function(value, index_lay, elem) {
            if(!value){
                return layer.msg("请输入金额");
            }
            if(!/^[-+]?\d*$/.test(value)){
                return layer.msg("请输入数字");
            }
            layer.close(index_lay);
            setCodeString(code.replace(/，/g,":"+value+";"));
        })
    });
    $('#order .edit-bottom .check-one').on('click',function () {
        var code = $(this).text();
        var number = $(this).data('value');
        layer.prompt({
            formType: 0,
            value: '',
            title: (number?'随机选':"包")+code+'，输入金额',
            maxlength: 11
        }, function(value, index_lay, elem) {
            if(!value){
                return layer.msg("请输入金额");
            }
            if(!/^[-+]?\d*$/.test(value)){
                return layer.msg("请输入数字");
            }
            layer.close(index_lay);
            if(number){
                code = "";
                for(var i=0;i<number;){
                    var sjval = Math.floor(Math.random()*49)+1;
                    if(code.indexOf(sjval)<0){
                        code+= sjval+",";
                        i++
                    }
                }
            }
            setCodeString(code+":"+value);
        })
    })
    $('#order .edit-bottom .check-clear').on('click',function () {
        $('#order .edit-bottom .check-many.success').removeClass("success");
    });
    monitorPrice();
    $(document).click(function (e) {
        var $target = $(e.target);
        if (!$target.is('#order .quick-fill-options *')&&!$target.is('#order input[name=price]')) {
            $('#order .quick-fill-options').hide();
        }
    });
    $('a.title-left').on('click',function () {
        mySwiper.slidePrev();
    });
    $('a.title-right').on('click',function () {
        mySwiper.slideNext();
    });
});
function navbarAcctive(index) {
    switch(index){
        case 0:
            loadOrder();
            break;
        case 1:
            loadRecord();
            break;
        case 2:
            loadBillReport();
            break;
        case 3:
            loadBill();
            break;
        case 4:
            loadPerson();
            break;
        default:
    }
}
function clearAll() {
    $('#order').find('tbody tr').each(function (i,$tr) {
        if(i==0){
            $($tr).find('.shengxiao').text("");
            $($tr).find('input[name=price]').val("");
            $($tr).find('input[name=code]').val("");
        }else {
            $($tr).remove();
        }
    })
    setTotalAmount();
}
function save() {
    var $layer = $('#person');
    var param = {};
    $layer.find("[name]").each(function(){
        $(this).val($.trim($(this).val()));
        param[$(this).attr("name")] = $(this).val();
    });
    if(!param.password){
        return layer.msg("密码不能为空");
    }
    var picture = $('#pictureFile').attr("data-value");
    if(picture){
        param.picture = picture;
    }
    param.sex = $("input[name='sex']:checked").val();
    layer.confirm('确定修改个人信息',{btn:['确定','取消']}, function(index_lay) {
        layer.close(index_lay);
        callData("/action/saveUser", param, function (res) {
            if (res.code == 0) {
                layer.msg("保存成功");
                loadPerson();
            } else {
                layer.alert(res.message)
            }
        });
    })
}
function imgChange() {
    $('#pictureFile').click();
    $('#pictureFile').change(function(){
        var end = $(this).val().slice(-4);
        if(end=='.bmp'||end=='.png'||end=='.jpg'||end=='.gif'){
            var file = this.files[0];
            var fileSize = file.size / (1024 * 1024);
            if (fileSize > 5) {
                layer.msg("选择的图片不要超过5M");
                return;
            }
            var maxSize = 0.2 * 1024 * 1024; // 0.5M
            var minSize = 0.1 * 1024 * 1024; // 200K
            var photoCompress = new PhotoCompress(minSize, maxSize);
            photoCompress.compress(file, function(fileNew) {
                var reader = new FileReader()
                reader.readAsDataURL(fileNew);
                reader.onload = function(ev) {
                    $('#pictureFile').attr('data-value',ev.target.result);
                    $("#picture").attr("src",reader.result);
                };
            });
        }else {
            layer.msg("请选择图片");
        }
    })
}
var dropload_person;
function loadPerson() {
    var $layer = $('#person');
    if(!dropload_person) {
        dropload_person = $layer.dropload({
            loadUpFn: function (me) {
                getDataList();
                me.resetload();//重置
            }
        });
        getDataList();
    }
    function getDataList() {
        callData("/action/loadPerson", {}, function(res) {
            if (res.code == 0) {
                var data = res.data;
                $layer.find("[name][type='text']").each(function(){
                    $(this).val(data[$(this).attr("name")]);
                });
                if(data.picture){
                    $('#picture').attr("src",data.picture)
                }else {
                    $('#picture').attr("src","/img/jqb.jpg")
                }
                if(data.sex) {
                    $("input[name='sex'][value=" + data.sex + "]").attr("checked", true);
                }
                $('#cash').text(data.cash);
                $('#account').text(data.account);
            }else {
                layer.alert(res.message)
            }
        });
    }
}
function loadOrder() {
    callData("/action/findLottery", {}, function(res) {
        var area = res.area=='1'?'澳门':(res.area=='2'?'台湾':'香港');
        $('head title').text(area);
        if (res.code == 0) {
            var lottery = res.data.lottery;
            if(lottery){
                var datelist = ['周日','周一','周二','周三','周四','周五','周六'];
                $('#lotteryName').text('第'+lottery.name+'期'+(lottery.date?' '+lottery.date+' '+datelist[new Date(lottery.date).getDay()]+' 开':''));
                $('#lotteryName').attr('data',lottery.id);
            }else {
                $('#lotteryName').text('未排期');
                $('#lotteryName').attr('data','');
            }
            var setup = res.data.setup;
            $('#odds').text(setup?setup.odds:'46');
            $('#minutes').text(setup?setup.minutes:'15');
        }
    })
}
var dropload_record;
function loadRecord() {
    var $layer = $('#record');
    if(!dropload_record) {
        var pageNumber = 0,pageSize = 20,down = false;
        dropload_record = $layer.dropload({
            loadUpFn: function (me) {
                pageNumber = 1;down = false;
                $layer.find('.content .data-list').empty();
                getDataList();
                me.noData(false);//应该会有更多数据
                me.resetload();//重置
                me.unlock();//解锁底部
            },
            loadDownFn: function (me) {
                pageNumber = pageNumber + 1;
                getDataList();
                if (down) {
                    me.lock('down');//锁住底部
                    me.noData();//没有更多数据
                }
                me.resetload();//重置
            }
        });
    }
    function getDataList() {
        callData("/action/findOrder", {pageNumber:pageNumber,pageSize:pageSize}, function(res) {
            if (res.code == 0) {
                var list = res.data;
                list.forEach(function (obj) {
                    obj.shengxiao = shangxiaoValueJson[obj.code];
                    var dlist = [];
                    var detilList = obj.list;
                    for(var i=0;i<detilList.length;i=i+3){
                        var detil = {};
                        detil['shengxiao1'] = shangxiaoValueJson[detilList[i].code];
                        detil['code1'] = detilList[i].code;
                        detil['price1'] = detilList[i].price;
                        detil['shengxiao2'] = i+1<detilList.length?shangxiaoValueJson[detilList[i+1].code]:'';
                        detil['code2'] = i+1<detilList.length?detilList[i+1].code:'';
                        detil['price2'] = i+1<detilList.length?detilList[i+1].price:'';
                        detil['shengxiao3'] = i+2<detilList.length?shangxiaoValueJson[detilList[i+2].code]:'';
                        detil['code3'] = i+2<detilList.length?detilList[i+2].code:'';
                        detil['price3'] = i+2<detilList.length?detilList[i+2].price:'';
                        dlist.push(detil)
                    }
                    obj.list = dlist;

                    var dlist2 = [];
                    var detilList2 = obj.list2;
                    for(var i=0;i<detilList2.length;i=i+3){
                        var detil = {};
                        detil['shengxiao1'] = shangxiaoValueJson[detilList2[i].code];
                        detil['code1'] = detilList2[i].code;
                        detil['price1'] = detilList2[i].price;
                        detil['shengxiao2'] = i+1<detilList2.length?shangxiaoValueJson[detilList2[i+1].code]:'';
                        detil['code2'] = i+1<detilList2.length?detilList2[i+1].code:'';
                        detil['price2'] = i+1<detilList2.length?detilList2[i+1].price:'';
                        detil['shengxiao3'] = i+2<detilList2.length?shangxiaoValueJson[detilList2[i+2].code]:'';
                        detil['code3'] = i+2<detilList2.length?detilList2[i+2].code:'';
                        detil['price3'] = i+2<detilList2.length?detilList2[i+2].price:'';
                        dlist2.push(detil)
                    }
                    obj.list2 = dlist2;
                });
                var listHtml = template("detil", {list:list});
                $layer.find('.content .data-list').append(listHtml);
                if(!list||list.length<pageSize){
                    down = true;
                }
                $layer.find('.content .oder-by').on('click',function () {
                    var $info = $(this).parent().parent();
                    if($info.find('.code').is(':hidden')){
                        $info.find('.code').show();
                        $info.find('.shengxiao').hide();
                    }else {
                        $info.find('.code').hide();
                        $info.find('.shengxiao').show();
                    }
                })
            }else {
                layer.alert(res.message);
                down = true;
            }
        });
    }
}
var dropload_bill;
function loadBill() {
    var $layer = $('#bill');
    if(!dropload_bill) {
        var pageNumber = 0,pageSize = 20,down = false;
        dropload_bill = $layer.dropload({
            loadUpFn: function (me) {
                pageNumber = 1;down = false;
                $layer.find('.content .data-list').empty();
                getDataList();
                me.noData(false);//应该会有更多数据
                me.resetload();//重置
                me.unlock();//解锁底部
            },
            loadDownFn: function (me) {
                pageNumber = pageNumber + 1;
                getDataList();
                if (down) {
                    me.lock('down');//锁住底部
                    me.noData();//没有更多数据
                }
                me.resetload();//重置
            }
        });
    }
    function getDataList() {
        callData("/action/selectBillList", {pageNumber:pageNumber,pageSize:pageSize}, function(res) {
            if (res.code == 0) {
                var list = res.data;
                var listHtml = template("billList", {list:list});
                $layer.find('.content .data-list').append(listHtml);
                if(!list||list.length<pageSize){
                    down = true;
                }
            }else {
                layer.alert(res.message);
                down = true;
            }
        });
    }
}
var dropload_bill_report;
function loadBillReport() {
    var $layer = $('#bill_report');
    if(!dropload_bill_report) {
        dropload_bill_report = $layer.dropload({
            loadUpFn: function (me) {
                getDataList();
                me.resetload();//重置
            }
        });
        getDataList();
    }
    function getDataList() {
        callData("/action/selectBillListReport", {}, function(res) {
            if (res.code == 0) {
                var rep = res.data;
                var listHtml = template("billReportList", rep);
                $layer.find('.content').html(listHtml);
                $layer.find('.content').next().show();//显示充值，提现
            }else {
                layer.alert(res.message)
            }
        });
    }
}
function addItem(event) {
    var $tr = $(event).parent('th').parent('tr');
    $('#order').find('tbody').append('<tr>'+$tr.html()+'</tr>');
    $('#order').find('tbody tr').last().find('.shengxiao').text("");
    setTotalAmount();
    monitorPrice();
}
function deleteItem(event) {
    if($('#order').find('tbody tr').length>1){
        $(event).parent('th').parent('tr').remove();
        setTotalAmount();
    }else {
        $('#order').find('tbody tr .shengxiao').text("");
        $('#order').find('tbody tr input[name=price]').val("");
        $('#order').find('tbody tr input[name=code]').val("");
        setTotalAmount();
    }
}
function setTotalAmount() {
    var totalAmount = 0;
    var totalPour = 0;
    $('#order').find('tbody tr input[name=price]').each(function (i,$input) {
        var price = $($input).val();
        if(price){
            totalAmount += parseFloat(price);
            totalPour++;
        }
    });
    $('#order').find('tbody tr th.index').each(function (i,$index) {
        $($index).text(i+1);
    });
    $('#totalPour').text(totalPour);
    $('#totalAmount').text(totalAmount);
}
function showPrice(event) {
    var $options = $(event).parent().next();
    if($options.is(':hidden')){
        $('#order .quick-fill-options').hide();
        $options.show();
    }else {
        $options.hide();
    }
}
function monitorPrice() {
    $('#order .quick-fill-options div').on('click', function (){
        var price = $(this).data("value");
        $(this).parent().prev().children('input').val(price);
        $(this).parent().hide();
        setTotalAmount();
    });
}
function changheCode(event) {
    var value = $(event).val();
    var $xiaosheng = $(event).parent().parent().prev();
    $xiaosheng.text(shangxiaoValueJson[value]);
    if(parseInt(value)>=50||parseInt(value)<=0){
        $xiaosheng.text('');
        return $(event).val('');
    }
    if(value){
        if(value.length==1){
            value = '0'+value;
            $(event).val(value);
            $xiaosheng.text(shangxiaoValueJson[value]);
        }
        $('#order').find('tbody tr input[name=code]').each(function (i,$input) {
            var code = $($input).val();
            if(value==code&&$input!=event){
                layer.msg("已存在"+value+"特码");
                $(event).val('');
                $xiaosheng.text('');
            }
        });
    }
}
function pay() {
    var lotteryId = $('#lotteryName').attr('data');
    if(!lotteryId){
        return layer.msg("无法下单，未排期");
    }
    var params = [];
    var num = $('#order').find('tbody tr').length;
    $('#order').find('tbody tr').each(function (i,$tr) {
        var code = $($tr).find('input[name=code]').val();
        var price = $($tr).find('input[name=price]').val();
        if(code&&price){
            if(price>0){
                params.push({code:code,price:price})
            }else if(params.length>0||(params.length===0&&i<num-1)){
                $($tr).remove();
            }
        }else {
            if(params.length>0||(params.length===0&&i<num-1)){
                $($tr).remove();
            }
        }
    });
    if(params.length<=0){
        return layer.msg("请填特码和金额");
    }
    setTotalAmount();
    layer.confirm('确定下单',{btn:['确定','取消']}, function(index_lay) {
        layer.close(index_lay);
        callDataJson("/action/saveOrder", {lotteryId:lotteryId,list:params}, function (res) {
            if (res.code == 0) {
                layer.msg("下单成功");
                document.querySelector('.swiper-container').swiper.slideNext();
                if(dropload_record){
                    dropload_record.opts.loadUpFn(dropload_record);//刷新纪录
                }
            } else {
                layer.alert(res.message)
            }
        });
    })
}
function delOrder(event,orderId) {
    layer.confirm('确定退单',{btn:['确定','取消']}, function(index_lay) {
        layer.close(index_lay);
        callData("/action/delOrder", {orderId: orderId}, function (res) {
            if (res.code == 0) {
                layer.msg("退单成功");
                var $detil = $(event).parent('span').parent('div.info');
                $detil.prev('li').remove();
                $detil.remove();
            } else {
                layer.alert(res.message)
            }
        });
    })
}
function codeString(event) {
    var codeString = $(event).val();
    setCodeString(codeString);
}
function setCodeString(codeString) {
    if(codeString){
        codeString = codeString.replace(/单/g,'01,03,05,07,09,11,13,15,17,19,21,23,25,27,29,31,33,35,37,39,41,43,45,47,49:').replace(/双/g,'02,04,06,08,10,12,14,16,18,20,22,24,26,28,30,32,34,36,38,40,42,44,46,48:');
        codeString = codeString.replace(/红波/g,'01,02,07,08,12,13,18,19,23,24,29,30,34,35,40,45,46:').replace(/蓝波/g,'03,04,09,10,14,15,20,25,26,31,36,37,41,42,47,48:').replace(/绿波/g,'05,06,11,16,17,21,22,27,28,32,33,38,39,43,44,49:');
        codeString = codeString.replace(/金/g,'01,02,09,10,23,24,31,32,39,40:').replace(/木/g,'05,06,13,14,21,22,35,36,43,44:').replace(/水/g,'11,12,19,20,27,28,41,42,49:').replace(/火/g,'07,08,15,16,29,30,37,38,45,46:').replace(/土/g,'03,04,17,18,25,26,33,34,47,48:');
        codeString = codeString.replace(/0头/g,'01,02,03,04,05,06,07,08,09:').replace(/1头/g,'10,11,12,13,14,15,16,17,18,19:').replace(/2头/g,'20,21,22,23,24,25,26,27,28,29:').replace(/3头/g,'30,31,32,33,34,35,36,37,38,39:').replace(/4头/g,'40,41,42,43,44,45,46,47,48,49:');
        codeString = codeString.replace(/0尾/g,'10,20,30,40:').replace(/1尾/g,'01,11,21,31,41:').replace(/2尾/g,'02,12,22,32,42:').replace(/3尾/g,'03,13,23,33,43:').replace(/4尾/g,'04,14,24,34,44:').replace(/5尾/g,'05,15,25,35,45:').replace(/6尾/g,'06,16,26,36,46:').replace(/7尾/g,'07,17,27,37,47:').replace(/8尾/g,'08,18,28,38,48:').replace(/9尾/g,'09,19,29,39,49:');
        $.each(shangxiaoValue,function(key,value){
            codeString = codeString.replace(key+', ',value+",");
            codeString = codeString.replace(key+'、',value+",");
            codeString = codeString.replace(key+'，',value+",");
            codeString = codeString.replace(key,value+':');
        })
        codeString = codeString.replace(/，/g,',').replace(/-/g,',').replace(/\n/g,';').replace(/、/g,',').replace(/；/g,';').replace(/；/g,';').replace(/。/g,';').replace(/\s+/g,':').replace(/元/g,';').replace(/各:/g,':').replace(/:各/g,':').replace(/各/g,':').replace(/::/g,':').replace(/,,/g,',');
        var arr = codeString.split(';');//12:34;23,33:5
        arr.forEach(function (obj) {//23,33:5
            var price = obj.substring(obj.indexOf(':')+1,obj.length);
            if(/^[0-9]*[1-9][0-9]*$/.test(price)){
                var arr2 = obj.substring(0,obj.indexOf(':')).split(',');
                arr2.forEach(function (code) {//23,33:5
                    if(/^[0-9]*[1-9][0-9]*$/.test(code)&&code>0&&code<50){
                        if(code.length==1){
                            code = '0'+code;
                        }
                        var flag = true;
                        $('#order').find('tbody tr').each(function (i,$tr) {
                            var code2 = $($tr).find('input[name=code]').val();
                            if(code2==code){
                                $($tr).find('input[name=price]').val(price);
                                flag = false;
                                return false;
                            }
                            if(!code2){
                                $($tr).find('.shengxiao').text(shangxiaoValueJson[code]);
                                $($tr).find('input[name=code]').val(code);
                                $($tr).find('input[name=price]').val(price);
                                flag = false;
                                return false;
                            }
                        });
                        if(flag) {
                            var $addItem = $('#order').find('tbody tr th i.addItem').last();//.eq(0);
                            $addItem.click();
                            var $tr = $addItem.parent('th').parent('tr').next('tr');
                            $tr.find('.shengxiao').text(shangxiaoValueJson[code]);
                            $tr.find('input[name=code]').val(code);
                            $tr.find('input[name=price]').val(price);
                        }
                    }
                })
            }
        })
        setTotalAmount();
    }
}
function rechargeCash() {
    layer.prompt({
        formType: 0,
        value: '',
        title: '请输入金额',
        maxlength: 11,
        btn: ['充值', '取消']
    }, function(value, index_lay, elem) {
        if(!value){
            return layer.msg("请输入充值金额");
        }
        if(!/^[-+]?\d*$/.test(value)){
            return layer.msg("请输入数字");
        }
        if(value<=0){
            return layer.msg("金额不能小于等于0");
        }
        layer.close(index_lay);
        location.href = "/app/pay?amount="+value;
    })
}
function withdrawalCash() {
    layer.prompt({
        formType: 0,
        value: '',
        title: '请输入金额',
        maxlength: 11,
        btn: ['提现', '取消']
    }, function(value, index_lay, elem) {
        if(!value){
            return layer.msg("请输入提现金额");
        }
        if(!/^[-+]?\d*$/.test(value)){
            return layer.msg("请输入数字");
        }
        if(value<=0){
            return layer.msg("金额不能小于等于0");
        }
        layer.close(index_lay);
        location.href = "/app/transfers?amount="+value;
    })
}
