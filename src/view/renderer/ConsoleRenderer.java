package view.renderer;

import java.util.List;

import controller.GreenhouseController;
import controller.ShopController;
import model.core.ReadOnlyGameState;
import model.data.plant.PlantType;
import model.service.*;
import model.storage.user.SafetyQuestion;

public class ConsoleRenderer implements Renderer {

    private final ConsoleRenderEngine engine = new ConsoleRenderEngine();
    private final ConsoleGridRenderer gridRenderer = new ConsoleGridRenderer(engine);
    private final ConsoleAuthScreens authScreens = new ConsoleAuthScreens(engine);
    private final ConsoleLevelScreens levelScreens = new ConsoleLevelScreens(engine);
    private final ConsoleGameScreens gameScreens = new ConsoleGameScreens(engine, gridRenderer);
    private final ConsoleGreenhouseScreens greenhouseScreens = new ConsoleGreenhouseScreens(engine);
    private final ConsoleOverlayScreens overlayScreens = new ConsoleOverlayScreens(engine);

    @Override
    public void prepareScreen(String screenKey) {
        engine.prepareScreen(screenKey);
    }

    @Override
    public void renderRegisterScreen(List<SafetyQuestion> questions) {
        engine.render(authScreens.getRegisterScreen(questions));
    }

    @Override
    public void renderLoginScreen(boolean isAwaitingSecurityAnswer, boolean isAwaitingNewPassword,
            String passwordResetQuestion) {
        engine.render(authScreens.getLoginScreen(isAwaitingSecurityAnswer, isAwaitingNewPassword,
                passwordResetQuestion));
    }

    @Override
    public void renderMainScreen(boolean hasUnreadNews) {
        engine.render(authScreens.getMainScreen(hasUnreadNews));
    }

    @Override
    public void renderGameScreen(ReadOnlyGameState state) {
        renderGameScreen(state, HudViewState.empty());
    }

    @Override
    public void renderGameScreen(ReadOnlyGameState state, HudViewState hud) {
        engine.render(gameScreens.getGameScreen(state, hud));
    }

    @Override
    public void renderLevelSelectionScreen(GameNavigationState gameNavigation) {
        engine.render(levelScreens.getLevelSelectionScreen(gameNavigation));
    }

    @Override
    public void renderGreenHouseScreen(GreenhouseController greenhouseController) {
        engine.render(greenhouseScreens.getGreenHouseScreen(greenhouseController));
    }

    @Override
    public void renderCollectionScreen(CollectionViewState collection) {
        engine.render(overlayScreens.getCollectionScreen(collection));
    }

    @Override
    public void renderSettingOverlay(SettingsViewState settings) {
        engine.render(overlayScreens.getSettingsOverlay(settings));
    }

    @Override
    public void renderNewsOverlay(NewsViewState news) {
        engine.render(overlayScreens.getNewsOverlay(news));
    }

    @Override
    public void renderLeaderboardOverlay(LeaderboardViewState leaderboardViewState) {
        engine.render(overlayScreens.getLeaderboardOverlay(leaderboardViewState));
    }

    @Override
    public void renderProfileOverlay(ProfileViewState profile) {
        engine.render(overlayScreens.getProfileOverlay(profile));
    }

    @Override
    public void renderShopScreen(int coins, int gems, PlantType dailyPlant, int dailyPrice, boolean dailyPurchased,
            ShopController.ShopDisplayMode mode) {
        engine.render(overlayScreens.getShopScreen(coins, gems, dailyPlant, dailyPrice, dailyPurchased, mode));
    }

    @Override
    public void renderPauseOverlay() {
    }

    @Override
    public void renderQuestsOverlay(QuestViewState quests) {
        engine.render(overlayScreens.getQuestsOverlay(quests));
    }

    @Override
    public void renderPlantSelectorOverlay() {
    }

    @Override
    public void renderGameOverOverlay(boolean won, int score, int wavesSurvived) {
        // use getGameScreen
    }

    @Override
    public void renderLevelCompleteOverlay(int stars, int score) {
        // use getGameScreen
    }

    @Override
    public void renderHUD(ReadOnlyGameState state) {
    }

    @Override
    public void renderGrid(ReadOnlyGameState state) {
    }

    @Override
    public void renderPlantSelectorOverlay(List<PlantType> availablePlants, int selectedIndex, int sunAmount) {
        // if (availablePlants == null || availablePlants.isEmpty())
        // return;

        // System.out.print(GREEN + "🌱 " + RESET);
        // for (int i = 0; i < availablePlants.size(); i++) {
        // PlantType type = availablePlants.get(i);
        // String prefix = (i == selectedIndex) ? "▶ " : " ";
        // boolean canAfford = sunAmount >= type.baseStats.cost;
        // String color = canAfford ? GREEN : RED;
        // System.out.printf("%s%s%s (%d)%s",
        // prefix,
        // color,
        // type.name,
        // type.baseStats.cost,
        // RESET);
        // if (i < availablePlants.size() - 1) {
        // System.out.print(" | ");
        // }
        // }
        // System.out.println();
    }

    @Override
    public void renderMessage(String message) {
        engine.renderMessage(message);
    }

    @Override
    public void renderError(String error) {
        engine.renderError(error);
    }

    @Override
    public boolean scrollMessages(int olderDelta) {
        return engine.scrollMessages(olderDelta);
    }

    public String getMessages() {
        return engine.getMessages();
    }

    @Override
    public void renderMessages() {
    }

    @Override
    public void renderCommandPrompt(String input) {
        engine.renderCommandPrompt(input);
    }

    @Override
    public void clearScreen() {
        engine.clearScreen();
    }

    @Override
    public void initialize() {
        engine.initialize();
    }

    @Override
    public void shutdown() {
    }

    @Override
    public boolean isRunning() {
        return true;
    }

    @Override
    public void stop() {
    }

    @Override
    public void renderSunDrops(ReadOnlyGameState state) {
    }

    @Override
    public void renderZombieDetails(ReadOnlyGameState state) {
    }

    @Override
    public void renderPlantDetails(ReadOnlyGameState state) {
    }

    @Override
    public void renderProjectiles(ReadOnlyGameState state) {
    }
}
