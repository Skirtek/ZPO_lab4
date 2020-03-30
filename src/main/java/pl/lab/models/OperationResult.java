package pl.lab.models;

public class OperationResult {
    private Boolean isSuccess;
    private String message;
    private Object data;

    public OperationResult(Boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public OperationResult(Boolean isSuccess, Object data) {
        this.isSuccess = isSuccess;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
