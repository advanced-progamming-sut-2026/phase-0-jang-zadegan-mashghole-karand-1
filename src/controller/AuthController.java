package pvz.controller;

import pvz.model.service.AuthService;
import pvz.view.AuthView;

public class AuthController implements Controller {

    private AuthService authService;
    private AuthView authView;

    @Override
    public void handle(String command) {
    }
}
