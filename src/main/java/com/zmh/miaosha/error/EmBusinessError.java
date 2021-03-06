package com.zmh.miaosha.error;

public enum EmBusinessError implements CommonError{

    // 10001 通用错误码
    PARAMETER_VALIDATION_ERROR(10001,"参数不合法"),
    UNKNOW_ERROR(10002,"未知错误"),

    // 20000开头为用户信息相关错误信息
    USER_NOT_EXIST(20001,"用户不存在"),
    USER_LOGIN_FAIL(20002,"用户手机号或密码不正确"),
    USER_NOT_LOGIN(20003,"用户没有登录"),
    // 30000开头为交易信息错误
    STOCK_NOT_ENOUGH(30001,"库存不足"),
    MQ_SEND_FAIL(30002,"库存异步消息失败");


         private int errCode;
         private String errMsg;

        EmBusinessError(int errCode, String errMsg) {
            this.errCode=errCode;
            this.errMsg=errMsg;
        }

        @Override
        public int getErrCode() {
            return errCode;
        }

        @Override
        public String getErrMsg() {
            return errMsg;
        }

        @Override
        public CommonError setErrMsg(String errMsg) {
            this.errMsg=errMsg;
            return this;
        }


}
