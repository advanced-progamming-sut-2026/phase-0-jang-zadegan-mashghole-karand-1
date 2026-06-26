package model.service;

import java.util.List;

import model.storage.user.SafetyQuestion;

public class AuthState {
    public List<SafetyQuestion> questions;
    public boolean isAwaitingSecurityAnswer;
    public boolean isAwaitingNewPassword;
    public String passwordResetQuestion;

}
