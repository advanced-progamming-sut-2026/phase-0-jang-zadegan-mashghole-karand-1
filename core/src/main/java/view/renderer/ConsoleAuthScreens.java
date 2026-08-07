package view.renderer;

import static view.renderer.ConsoleTheme.BOLD;
import static view.renderer.ConsoleTheme.CYAN;
import static view.renderer.ConsoleTheme.GREEN;
import static view.renderer.ConsoleTheme.RED;
import static view.renderer.ConsoleTheme.RESET;
import static view.renderer.ConsoleTheme.YELLOW;

import java.util.List;

import model.storage.user.SafetyQuestion;

final class ConsoleAuthScreens {

        private final ConsoleRenderEngine engine;

        ConsoleAuthScreens(ConsoleRenderEngine engine) {
                this.engine = engine;
        }

        String getRegisterScreen(List<SafetyQuestion> questions) {
                StringBuilder sb = new StringBuilder();
                String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Register" + RESET + "  🧟";

                sb.append(engine.getHeaderBox(title, GREEN));
                sb.append("\n");
                sb.append("  " + CYAN + "1." + RESET + " Register: " + GREEN
                                + "register -u <username> -p <password>"+ 
                                "<password_confirm> -n <nickname> -e <email> -g <gender>"
                                + RESET + "\n");
                sb.append("  " + CYAN + "2." + RESET + " Pick Security Question: " + GREEN
                                + "pick question -q <question_number> -a <answer> -c <answer_confirm>" + RESET + "\n");
                sb.append("  " + CYAN + "3." + RESET + " Login Menu: " + GREEN + "menu enter login" + RESET + "\n");
                sb.append("  " + CYAN + "4." + RESET + " Quit: " + GREEN + "quit" + RESET + "\n");
                sb.append("\n");
                sb.append("  " + BOLD + "Safety Questions:" + RESET + "\n");
                for (int i = 0; i < questions.size(); i++) {
                        sb.append("    ").append(CYAN).append(i + 1).append(".").append(RESET).append(" ")
                                        .append(questions.get(i).type.question).append("\n");
                }
                sb.append("\n");
                sb.append(engine.getMessages());

                return sb.toString();
        }

        String getLoginScreen(boolean isAwaitingSecurityAnswer, boolean isAwaitingNewPassword,
                        String passwordResetQuestion) {
                StringBuilder sb = new StringBuilder();
                String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Login" + RESET + "  🧟";

                sb.append(engine.getHeaderBox(title, GREEN));
                sb.append("\n");
                sb.append("  " + CYAN + "1." + RESET + " Login: " + GREEN
                                + "login -u <username> -p <password>" + RESET + "\n");
                sb.append("  " + CYAN + "2." + RESET + " Stay Logged In: " + GREEN
                                + "login -u <username> -p <password> -stay-logged-in" + RESET + "\n");
                sb.append("  " + CYAN + "3." + RESET + " Forget Password: " + GREEN
                                + "forget password -u <username> -e <email>" + RESET + "\n");
                sb.append("  " + CYAN + "4." + RESET + " Answer Security Question: " + GREEN
                                + "answer -a <answer>" + RESET + "\n");
                sb.append("  " + CYAN + "5." + RESET + " Reset Password: " + GREEN
                                + "reset password -p <password> <password_confirm>" + RESET + "\n");
                sb.append("  " + CYAN + "6." + RESET + " Register Menu: " + GREEN + "menu exit" + RESET + "\n");
                sb.append("  " + CYAN + "7." + RESET + " Quit: " + GREEN + "quit" + RESET + "\n");

                if (isAwaitingSecurityAnswer && passwordResetQuestion != null) {
                        sb.append("\n");
                        sb.append("  " + BOLD + "Security Question:" + RESET + " ")
                                        .append(passwordResetQuestion).append("\n");
                }
                if (isAwaitingNewPassword) {
                        sb.append("\n");
                        sb.append("  " + YELLOW + "Enter your new password using reset password." + RESET + "\n");
                }

                sb.append("\n");
                sb.append(engine.getMessages());

                return sb.toString();
        }

        String getMainScreen(boolean hasUnreadNews) {
                StringBuilder sb = new StringBuilder();
                String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2" + RESET + "  🧟";
                String unreadIndicator = hasUnreadNews ? " " + RED + "● unread" + RESET : "";

                sb.append(engine.getHeaderBox(title, GREEN));
                sb.append("\n");
                sb.append("  " + CYAN + "1." + RESET + " Start Game: " + GREEN + "menu enter game" + RESET + "\n");
                sb.append("  " + CYAN + "2." + RESET + " Settings: " + GREEN + "menu enter settings" + RESET + "\n");
                sb.append("  " + CYAN + "3." + RESET + " News: " + GREEN + "menu enter news" + RESET + unreadIndicator
                                + "\n");
                sb.append("  " + CYAN + "4." + RESET + " Profile: " + GREEN + "menu enter profile" + RESET + "\n");
                sb.append("  " + CYAN + "5." + RESET + " Logout: " + GREEN + "menu logout" + RESET + "\n");
                sb.append("  " + CYAN + "6." + RESET + " Quit: " + GREEN + "quit" + RESET + "\n");
                sb.append("\n");
                sb.append(engine.getMessages());

                return sb.toString();
        }
}
