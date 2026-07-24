package view.renderer;

import java.util.List;

import controller.GreenhouseController;
import controller.ShopController;
import model.core.ReadOnlyGameState;
import model.data.plant.PlantType;
import model.service.*;
import model.storage.user.SafetyQuestion;

public interface Renderer {

    void prepareScreen(String screenKey);

    void renderRegisterScreen(List<SafetyQuestion> questions);

    void renderLoginScreen(boolean isAwaitingSecurityAnswer, boolean isAwaitingNewPassword,
                           String passwordResetQuestion);

    void renderMainScreen(boolean hasUnreadNews);

    void renderLevelSelectionScreen(GameNavigationState gameNavigation);

    void renderGameScreen(ReadOnlyGameState state);

    void renderGameScreen(ReadOnlyGameState state, HudViewState hud);

    void renderCollectionScreen(CollectionViewState collection);

    void renderGreenHouseScreen(GreenhouseController greenhouseController);

    void renderShopScreen(int coins, int gems, PlantType dailyPlant, int dailyPrice, boolean dailyPurchased, ShopController.ShopDisplayMode mode);

    void renderPauseOverlay();

    void renderSettingOverlay(SettingsViewState settings);

    void renderProfileOverlay(ProfileViewState profile);

    void renderNewsOverlay(NewsViewState news);

    void renderLeaderboardOverlay(LeaderboardViewState leaderboardViewState);

    void renderQuestsOverlay(QuestViewState questViewState);

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