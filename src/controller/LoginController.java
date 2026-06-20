package controller;

import model.CommandResult.CommandResult;
import model.service.AuthService;
import model.service.GameService;

public class LoginController {
    private AuthService authService;
    //private LoginView loginView;
    public CommandResult login(String username, String password, boolean stayLoggedIn){ return null; }

    public CommandResult forgotPassword(String username, String email){ return null; }

    public CommandResult answer(String answer){  return null; }

}
