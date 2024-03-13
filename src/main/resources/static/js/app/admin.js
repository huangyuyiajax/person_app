$(function() {
    laydate.render({
        elem: '#date', type: 'date',value: ''
    });
    var mySwiper = new Swiper('.swiper-container',{
        initialSlide: 0, // 坐标索引值初始是第几个
        on:{
            init: function(){
                console.info("init")
                this.emit('slideChangeTransitionEnd');//在初始化时触发一次slideChangeTransitionEnd事件
            },
            slideChangeTransitionEnd: function(){
                $('.navbar').find("a").removeClass("acctive");
                var index = this.activeIndex;
                if(index==4||index==5){
                    index = 3;
                }
                if(index==7){
                    index = 6;
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
    $('#calculation_person').on('click',function () {
        if($(this).hasClass('swiper-no-swiping')){
            $(this).removeClass('swiper-no-swiping')
            $(this).find('a.title-left').show();
        }else {
            $(this).addClass('swiper-no-swiping');
            $(this).find('a.title-left').hide();
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
            loadRecordDetilList();
            break;
        case 1:
            loadLottery();
            break;
        case 2:
            loadBillDetil();
            break;
        case 3:
            calculation();
            break;
        case 4:
            calculationAll();
            break;
        case 5:
            loadRecordDetilRepList();
            break;
        case 6:
            loadPersonDetil();
            break;
        case 7:
            calculationPerson();
            break;
        default:
    }
}
var nextLotteryName = '';
function loadLottery() {
    callDataJson("/action/findLottery", {}, function(res) {
        if (res.code == 0) {
            var lottery = res.data.lottery;
            $('#scheduled').text(lottery?'已排期':'未排期');
            $('#name').val(lottery?lottery.name:'');
            $('#date').val(lottery?lottery.date:'');
            $('#code').val(lottery?lottery.code:'');
            var setup = res.data.setup;
            $('#odds').val(setup?setup.odds:'');
            $('#minutes').val(setup?setup.minutes:'');
            nextLotteryName = res.data.nextLotteryName;
        }else {
            layer.alert(res.message)
        }
    });
}
function paiqi() {
    var param = {name:$('#name').val(),date:$('#date').val()};
    if(!param.name){
        return layer.msg("开奖期数不能为空");
    }
    if(!param.date){
        return layer.msg("开奖日期不能为空");
    }
    callDataJson("/admin/saveLottery", param, function(res) {
        if (res.code == 0) {
            layer.msg("操作成功");
            $('#scheduled').text('已排期');
        }else {
            layer.alert(res.message)
        }
    });
}
function kaijiang() {
    var param = {code:$('#code').val(),date:$('#date').val()};
    if(!param.code){
        return layer.msg("开奖特码不能为空");
    }
    if(param.code<=0||param.code>49){
        return layer.msg("开奖特码应该为01到49");
    }
    layer.confirm('开特码为'+param.code+'，确定？',{btn:['确定','取消']}, function(index_lay) {
        layer.close(index_lay);
        callData("/admin/kaijiang", param, function (res) {
            if (res.code == 0) {
                layer.msg("操作成功");
                document.querySelector('.swiper-container').swiper.slidePrev();
                if(dropload_recordDetil){
                    dropload_recordDetil.opts.loadUpFn(dropload_recordDetil);//刷新纪录
                }
            } else {
                layer.alert(res.message)
            }
        });
    })
}
var dropload_recordDetil;
function loadRecordDetilList() {
    var $layer = $('#recordDetil');
    if(!dropload_recordDetil){
        var pageNumber = 0,pageSize = 20,down = false;
        dropload_recordDetil = $layer.dropload({
            loadUpFn : function(me){
                pageNumber = 1;down = false;
                $layer.find('.content .data-list').empty();
                getDataList();
                me.noData(false);//应该会有更多数据
                me.resetload();//重置
                me.unlock();//解锁底部
            },
            loadDownFn : function(me){
                pageNumber = pageNumber +1;
                getDataList();
                if(down){
                    me.lock('down');//锁住底部
                    me.noData();//没有更多数据
                }
                me.resetload();//重置
            }
        });
    }
    function getDataList() {
        callData("/admin/loadRecordDetilList", {pageNumber:pageNumber,pageSize:pageSize}, function(res) {
            $('head title').text(res.area=='1'?'澳门':(res.area=='2'?'台湾':'香港'));
            if (res.code == 0) {
                var list = res.data;
                list.forEach(function (obj) {
                    obj.shengxiao = shangxiaoValueJson[obj.code];
                });
                var listHtml = template("recordDetilList", {list:list});
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
var dropload_recordDetil_report;
function loadRecordDetilRepList() {
    var $layer = $('#recordDetil_report');
    if(!dropload_recordDetil_report) {
        dropload_recordDetil_report = $layer.dropload({
            loadUpFn: function (me) {
                getDataList();
                me.resetload();//重置
            }
        });
        getDataList();
    }
    function getDataList() {
        callData("/admin/loadRecordDetilRepList", {}, function(res) {
            if (res.code == 0) {
                var rep = res.data;
                var $layer = $('#recordDetil_report');
                var listHtml = template("recordDetilReportList", rep);
                $layer.find('.content').html(listHtml);
            }else {
                layer.alert(res.message)
            }
        });
    }
}
var dropload_recordAll;
function loadBillDetil() {
    var $layer = $('#recordAll');
    if(!dropload_recordAll) {
        var pageNumber = 0,pageSize = 20,down = false;
        dropload_recordAll = $layer.dropload({
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
        callData("/admin/findRecordAllList", {pageNumber:pageNumber,pageSize:pageSize}, function(res) {
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
                var listHtml = template("recordAllList", {list:list});
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
var dropload_personDetil;
function loadPersonDetil() {
    var $layer = $('#personDetil');
    if(!dropload_personDetil) {
        var pageNumber = 0, pageSize = 20, down = false;
        dropload_personDetil = $layer.dropload({
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
        callData("/admin/loadPersonDetil", {pageNumber:pageNumber,pageSize:pageSize}, function(res) {
            if (res.code == 0) {
                var list = res.data;
                var listHtml = template("userAllList", {list:list});
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
function recharge(e,id) {
    e.stopPropagation();
    layer.prompt({
        formType: 0,
        value: '',
        title: '请输入金额',
        maxlength: 11,
        btn: ['充值', '提现', '取消'],
        btn2:function(index_lay, elem){
            var value = $('#layui-layer'+index_lay + " .layui-layer-input").val();
            if(!value){
                return layer.msg("请输入提现金额");
            }
            if(!/^[-+]?\d*$/.test(value)){
                return layer.msg("请输入数字");
            }
            layer.close(index_lay);
            callData("/admin/recharge", {id:id,amount:-value}, function(res) {
                if (res.code == 0) {
                    layer.msg("操作成功");
                    loadPersonDetil();
                }else {
                    layer.alert(res.message)
                }
            });
        }
    }, function(value, index_lay, elem) {
        if(!value){
            return layer.msg("请输入充值金额");
        }
        if(!/^[-+]?\d*$/.test(value)){
            return layer.msg("请输入数字");
        }
        layer.close(index_lay);
        callData("/admin/recharge", {id:id,amount:value}, function(res) {
            if (res.code == 0) {
                layer.msg("操作成功");
                if(dropload_personDetil){
                    dropload_personDetil.opts.loadUpFn(dropload_personDetil);//刷新纪录
                }
            }else {
                layer.alert(res.message)
            }
        });
    })
}
var dropload_calculation;
function calculation() {
    var $layer = $('#calculation');
    if(!dropload_calculation) {
        dropload_calculation = $layer.dropload({
            loadUpFn: function (me) {
                getDataList();
                me.resetload();//重置
            }
        });
        getDataList();
    }
    function getDataList() {
        callData("/admin/findCalculationList", {}, function(res) {
            if (res.code == 0) {
                var resJson = res.data;
                resJson.list.forEach(function (obj) {
                    obj.shengxiao = shangxiaoValueJson[obj.code];
                });
                var listHtml = template("calculationList", resJson);
                $layer.find('.content').html(listHtml);
            }else {
                layer.alert(res.message)
            }
        });
    }
}
var dropload_calculation_all;
function calculationAll() {
    var $layer = $('#calculation_all');
    if(!dropload_calculation_all) {
        dropload_calculation_all = $layer.dropload({
            loadUpFn: function (me) {
                getDataList();
                me.resetload();//重置
            }
        });
        getDataList();
    }
    function getDataList() {
        callData("/admin/findAllCalculationList", {}, function(res) {
            if (res.code == 0) {
                var resJson = res.data;
                resJson.list.forEach(function (obj) {
                    obj.shengxiao = shangxiaoValueJson[obj.code];
                });
                var listHtml = template("calculationList", resJson);
                $layer.find('.content').html(listHtml);
            }else {
                layer.alert(res.message)
            }
        });
    }
}
var dropload_calculation_person;
function calculationPerson() {
    var $layer = $('#calculation_person');
    if(!dropload_calculation_person) {
        dropload_calculation_person = $layer.dropload({
            loadUpFn: function (me) {
                getDataList();
                me.resetload();//重置
            }
        });
        getDataList();
    }
    function getDataList() {
        callData("/admin/findAllPersonCalculationList", {}, function(res) {
            if (res.code == 0) {
                var resJson = res.data;
                var listHtml = template("calculation_person_list", resJson);
                $layer.find('.content .data-list').html(listHtml);
            }else {
                layer.alert(res.message)
            }
        });
    }
}
function savesetup() {
    var param = {odds:$('#odds').val(),minutes:$('#minutes').val()};
    if(!param.odds||!param.minutes){
        return;
    }
    callDataJson("/admin/saveSetup", param, function(res) {
        if (res.code == 0) {
            layer.msg("操作成功");
        }else {
            layer.alert(res.message)
        }
    });
}
function clearNoNum(obj){
    obj.value = obj.value.replace(/[^\d.]/g,"");  //清除“数字”和“.”以外的字符
    obj.value = obj.value.replace(/\.{2,}/g,"."); //只保留第一个. 清除多余的
    obj.value = obj.value.replace(".","$#$").replace(/\./g,"").replace("$#$",".");
    obj.value = obj.value.replace(/^(\-)*(\d+)\.(\d\d).*$/,'$1$2.$3');//只能输入两个小数
    if(obj.value.indexOf(".")< 0 && obj.value !=""){//以上已经过滤，此处控制的是如果没有小数点，首位不能为类似于 01、02的金额
        obj.value= parseFloat(obj.value);
    }
}
function showPic(e) {
    $(e).children('.pic').show();
}
function setLotteryName(e) {
    var value = $(e).val();
    if(!value){
        $(e).val(nextLotteryName);
    }
}