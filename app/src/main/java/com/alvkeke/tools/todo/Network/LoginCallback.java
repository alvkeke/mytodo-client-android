package com.alvkeke.tools.todo.Network;

public interface LoginCallback {
    void loginSuccess(int key);
    void loginFailed(int failedType);
}
