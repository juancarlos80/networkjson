package jc.personal.networkjson;

public class Result {
    public String mResultValue;
    public Exception mException;

    public Result( String resultValue ){
        mResultValue = resultValue;
    }
    public Result( Exception exception ){
        mException = exception;
    }
}
