package view.renderer;

import java.util.List;

import model.core.ReadOnlyGameState;
import model.data.plant.PlantType;
import model.service.GameNavigationState;
import model.storage.user.SafetyQuestion;

public interface Renderer {

    void prepareScreen(String screenKey);

    void renderRegisterScreen(List<SafetyQuestion> questions);

    void renderLoginScreen(boolean isAwaitingSecurityAnswer, boolean isAwaitingNewPassword,
            String passwordResetQuestion);

    void renderMainScreen();

    void renderLevelSelectionScreen(GameNavigationState gameNavigation);

    void renderGameScreen(ReadOnlyGameState state);

    void renderCollectionScreen();

    void renderGreenHouseScreen();

    void renderShopScreen();

    void renderPauseOverlay();

    void renderSettingOverlay();

    void renderProfileOverlay();

    void renderNewsOverlay();

    void renderQuestsOverlay();

    void renderPlantSelectorOverlay();

    void renderGameOverOverlay(boolean won, int score, int wavesSurvived);

    void renderLevelCompleteOverlay(int stars, int score);

    void renderHUD(ReadOnlyGameState state);

    void renderGrid(ReadOnlyGameState state);

    void renderPlantSelectorOverlay(List<PlantType> availablePlants, int selectedIndex, int sunAmount);

    void renderZombieDetails(ReadOnlyGameState state);

    void renderPlantDetails(ReadOnlyGameState state);

    void renderSunDrops(ReadOnlyGameState state);

    void renderProjectiles(ReadOnlyGameState state);

    void renderMessage(String message);

    void renderError(String error);

    void renderMessages();

    void renderCommandPrompt(String input);

    void clearScreen();

    void initialize();

    void shutdown();

    boolean isRunning();

    void stop();
}