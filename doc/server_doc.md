# 1:通知方式:
    HTTP POST方式
# 2:数据格式
    json, http头部application/json
# 3:推送数据格式：
    {
        "order": "流水号，60字符",
        "appId": "应用ID，数字",
        "userId": "平台帐号ID，数字",
        "gameRoleId": "游戏内角色ID， 30字符",
        "serverId": "被通知的服务器ID，30字符",
        "channelType": "充值渠道，数字",
        "productId": "充值商品ID， 32字符",
        "productAmount": "购买商品数量，数字",
        "money": "充值金额，20字符",
        "currency": "货币单位，8字符",
        "status": "充值成功与否，数字",
        "sandbox": "是否沙盒，数字",
        "cparam": "客户端上报的附加数据，250字符，需要base64解码",
        "virtualCoin": "充值获得虚拟币数量，数字",
        "giveVirtualCoin": "充值赠送虚拟币数量，数字",
        "sign": "签名"
    }
# 4:签名规则：
    md5(order + "|" + appId + "|" + userId + "|" + gameRoleId + "|" + serverId + "|" + channelType + "|" + productId + "|" + "1" + "|" + money + "|" + currency + "|" + "0" + "|" + sandbox + "|" + cparam + "|" + virtualCoin + "|" + giveVirtualCoin + "|" + appSecret)
# 5:响应:
    处理成功返回本次流水号，失败返回fail
