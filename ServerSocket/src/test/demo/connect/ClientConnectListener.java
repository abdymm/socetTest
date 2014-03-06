package test.demo.connect;

public interface ClientConnectListener {
    public void onConnectSuccess(boolean success);
    public void onConnectError(String errorMessage);
}
