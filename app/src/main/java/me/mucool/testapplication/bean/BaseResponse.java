package me.mucool.testapplication.bean;

public class BaseResponse<T> {

    private Boolean success;
    private String errorCode;
    private String msg;
    T body;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    String a = "FROM aftersale_view WHERE 1=1  #refund_id@refundId@=# #id@id@=# #type@playType@=# #check_status@checkStatus@=# #is_getgoods@isGetgoods@=# #tid@tid@=# #sid@sid@=# #created@created_start@>=# #created@created_end@<=# #storeid@storeid@in# #type@platformid@in# #storeid@storeid_id@=#";
    String b = "FROM aftersale_view WHERE 1=1  #refund_id@refundId@=# #id@id@=# #type@playType@=# #check_status@checkStatus@=# #is_getgoods@isGetgoods@=# #tid@tid@=# #sid@sid@=# #created@created_start@>=# #created@created_end@<=# #storeid@storeid@in# #type@platformid@in#";

}
