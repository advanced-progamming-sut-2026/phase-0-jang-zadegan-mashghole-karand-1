package controller;

import model.CommandResult.CommandResult;
import model.service.AuthService;
import model.user.SafetyQuestion;
import view.RegisterMenuView;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

public class AuthController {

    private AuthService authService = new AuthService();
    List<SafetyQuestion> questions = new ArrayList<>();

    public void register(String username, String password, String passwordConfirm, String nickName, String email,
            String gender) {
    }

    public CommandResult register(String username, String password, String passwordConfirm, String nickName,
            String email, String gender) {
        return null;
    }

    public CommandResult pickQuestion(int QuestionNum, String answer, String answerConfirm) {
        return null;
    }
}
