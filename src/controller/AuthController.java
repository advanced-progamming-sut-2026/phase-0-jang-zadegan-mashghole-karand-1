package controller;

import model.service.AuthService;

import java.util.ArrayList;
import java.util.List;

public class AuthController {

    private AuthService authService = new AuthService();
    private AuthView authView;
    //List<Question> questions = new ArrayList<>();


    public void register(String username, String password,String passwordConfirm, String nickName, String email, String gender) {}

    public void pickQuestion(int QuestionNum , String answer , String answerConfirm){}
}
