package me.mucool.testapplication.network.exception;

import java.io.IOException;

public class CustomException extends IOException {

    private int code;

    public int getCode() {
        return code;
    }

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, int code) {
        super(message);
        this.code = code;
    }

    public static boolean isCustomException(Throwable e) {
        if (e instanceof CustomException) {
            return true;
        }
        return false;
    }

}
