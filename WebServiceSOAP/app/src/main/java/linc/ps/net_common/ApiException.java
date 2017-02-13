package linc.ps.net_common;

/**
 * Created by Frank on 2016/12/9.
 * 服务端返回的错误信息都在这里做转义处理!!!
 */
public class ApiException extends Throwable {

    private String msgCode;// 错误代码
    private String msg;// 错误原因

    @Override
    public String getMessage() {
        return "错误代码:" + msgCode + " 原因:" + msg;
    }

    public ApiException(String msgCode, String msg) {
        this.msgCode = msgCode;
        this.msg = msg;
    }
}
