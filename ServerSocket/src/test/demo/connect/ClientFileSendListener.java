package test.demo.connect;

public interface ClientFileSendListener {
    public void onError(String errorMessage);
    public void onProcess(String message);
    public void changeProcess(int process);
    public void sendSuccess();
}
