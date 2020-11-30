package com.mampod.track.sdk.constants;

/**
 * 统计业务类型
 *
 * @package com.mampod.track.sdk.constants
 * @author: Jack-Lu
 * @date:
 */
public class StatisBusiness {

    /**
     * 业务场景类型 p
     */
    public enum Scene {
        sys, //客户端系统留存类
        dur, //页面停留时长类
        ad,  //广告
        app, //客户端全局
        vm,  //视频首页
        vl,  //视频列表
        vi,  //视频详情页
        am,  //音频首页
        ai,  //音频列表
        share,//分享
        cm,   //缓存首页
        me,   //个人中心
        other, //其他
        delay, //延时
        vip,   //vip
        ab,    //ab测试
        credit,  //  存钱罐
        click, // 点击
        temp, //临时打点统计新老用户
        point, //通用计数、漏斗
        ui //日志收集sdk
    }

    /**
     * 事件 k
     */
    public enum Event {
        init, //客户端启动
        duration, //客户端单次启动后的活跃时长
        vi,   //
        video,
        audio,
        v, //曝光
        c, // 点击
        f, //失败
        banner,
        sign, //签到
        add,//获得点点币
        cost, //消费点点币
        role2, //角色
        role1, //动态权限-已持有
        role4, //动态权限
        v1, //通用计数、漏斗
        cmr1,//相机通用计数
        cmr2, //打电话通用计数
        cfg2,// app特征
        l,  //list
        b, //按钮
        p  //页面

    }

    /**
     * 业务触发事件类型 m
     */
    public enum Action {
        c, //点击
        v, //曝光
        d, //页面停留
        o,  //自定义行为|| 界面退出
        f,   // 失败
        i  //界面进入
    }

    /**
     * 上报来源
     */
    public enum Resource {

        BUFF_END(1), //缓冲结束
        EX_AV(2),// 切换音视频
        EXIT(3);//退出播放页
        int resouce;

        public int getResouce() {
            return this.resouce;
        }

        Resource(int resouce) {
            this.resouce = resouce;
        }
    }

    /**
     * 广告相关事件
     */
    public enum AdAction {
        AD_SUCCESS(1),//广告展现
        AD_FILURE(2),//广告失败
        AD_CLICK(3);//广告点击
        int action;

        public int getAction() {
            return action;
        }

        AdAction(int action) {
            this.action = action;
        }
    }

    /**
     * 用户标识
     */
    public enum UserTag {
        s, //开始
        e, //成功
        p1,//电话权限
        p2,//存储权限
        p3, //定位权限
        cl, //关闭申请权限弹框
        s1 //权限首个弹框
    }

    /**
     * 点点币来源
     */
    public enum Source {
        coin_1
    }

    /**
     * 广告类型
     */
    public enum AdType {
        dd, // 自定义
        inmobi, // inmobi
        jh, // 鑫谷聚合
        // banner
        bd, // 百度
        gdt, // 广点通
        csj, // 穿山甲
        yd, // 一点
        jk, // 聚看
        // 原生自渲染
        bdn,
        gdtn,
        csjn,
        smn, // 思盟
        wyn, // 万裕
        yq, // 云蜻（亿典）
        // 原生模板
        bdt,
        gdtt,
        csjt,
        jkt,
        // 原生自渲染  信息流-元素
        bde
    }

    /**
     * 广告位置
     */
    public enum AdPosition {
        sp1, //开屏
        sp2,//退出广告
        sp3,//信息流广告
        sp4,//计算题广告
        sp5,//分类冠名（仅展示）
        vb1, // 视频播放页广告位1
        vb2, // 视频播放页广告位2
        vb3, // 视频播放页广告位3
        vb4, // 视频播放页广告位4
        vp1, // 视频播放页前贴广告位
        vp2,//视频后贴
        vp3,//浮层广告
        vp4//视频冠名（仅展示）
    }

    /**
     * 视频源统计一级枚举
     */
    public enum Level1 {
        vm,  //首页-精选
        va,  //首页-分类
        vc,  //缓存
        vs,   //搜索
        vh,   //伙伴
        xm,  //宝宝学精选
        xa,   //宝宝学分类
        vb,   //宝宝看专辑观看记录
        xb,    //宝宝学专辑观看记录
        vr,  //宝宝看播放界面推荐
        xr   //宝宝学播放界面推荐
    }

    /**
     * 视频切换动作枚举
     */
    public enum AVSwitch {
        d,  //默认播放
        m,  //手动触发
        a   //自动触发
    }

    /**
     * vip打点位置
     */
    public enum VipPosition {
        vipc1, // 视频播放 关闭广告
        vipc2, // 视频播放 切换清晰度
        vipc3, // 存钱鑵兑换 不是VIP开通  h5
        vipc4, // 存钱鑵商品详情页 购买商品广告 h5
        vipc5, // 支付页 广告位
        vipc6, // 我的页面  查看
        vipc7, // 设置页面  查看
        vipc8, // 我的页面  立即开通
        vipc9, // 设置     立即开通
        vipc10, // 我的VIP 立即开通
        vipc11, // 帐户相关 立即开通
        vipc12, // appstore 开通会员
        vipc13, // 我的vip  会员福利 普通会员  h5
        vipc14, // 我的vip  会员福利 年卡会员  h5
        vipc15, // 我的vip  无未领取资产下方广告 h5
        vipc16, // 存钱鑵商城  不是VIP开通  h5
        vipc17, // 我的页面   vip宣传进入支付
        vipc18, // 设置页面   vip宣传进入支付
        vipc19, // 我的vip   vip宣传进入支付
        vipc20, // vip音频购买 进入支付
        vipc21, // 我的vip宣传卡 h5大礼包
        vipc22, // 设置vip宣传卡 h5大礼包
        vipc23,  // web vip宣传卡 h5大礼包
        vipc24,  // 加入VIP会员vip宣传卡 h5大礼包
        vipc25  //付费视频vip购买入口
    }

    /**
     * vip 购买结果
     */
    public enum VipResult {
        d,//点击购买
        t //支付成功
    }

    /**
     * 权限申请动作类型
     */
    public enum PermissionOp {
        START(1),//启动
        OP(2), //操作
        DISCARD(3), // 禁止不再询问
        POP(4),//弹框
        AFTER(5), // 以后再说
        DENIED(6); //单纯拒绝
        int code;

        public int getCode() {
            return code;
        }

        PermissionOp(int code) {
            this.code = code;
        }

    }

    /**
     * 相机等动态权限操作打点
     */
    public enum CameraAction {
        initReject(10),//点击按钮后 检查本地 无权限
        initAllow(41),//点击按钮后 检查本地 有权限
        operReject(30),//拒绝授权
        operAllow(31);//同意授权
        int code;

        public int getCode() {
            return code;
        }

        CameraAction(int code) {
            this.code = code;
        }
    }
}

