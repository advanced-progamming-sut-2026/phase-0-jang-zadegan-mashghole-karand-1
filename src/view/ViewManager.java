package view;

import controller.InputHandler;
import model.core.ReadOnlyGameState;
import view.renderer.Renderer;

public class ViewManager {
    private final Renderer renderer;
    private final InputHandler inputHandler;
    private final InputListener inputListener;

    private ScreenType currentScreen = ScreenType.MAIN;
    private MenuType currentMenu = MenuType.NONE;

    public ViewManager(Renderer renderer, InputHandler inputHandler) {
        this.renderer = renderer;
        this.inputHandler = inputHandler;
        this.inputListener = new InputListener(inputHandler, renderer::renderCommandPrompt);
    }

    public void showScreen(ScreenType screen) {
        this.currentScreen = screen;
        this.currentMenu = MenuType.NONE;
    }

    public void showMenuOverlay(MenuType menu) {
        this.currentMenu = menu;
    }

    public void hideMenuOverlay() {
        this.currentMenu = MenuType.NONE;
    }

    public void showMessage(String message) {
        renderer.renderMessage(message);
    }

    public void render(ReadOnlyGameState state) {
        switch (currentScreen) {
            case MAIN:
                renderer.renderMainScreen();
                break;
            case LEVEL_SELECTOR:
                renderer.renderLevelSelectionScreen();
                break;
            case GAME:
                renderer.renderGameScreen(state);
                break;
            case COLLECTION:
                renderer.renderCollectionScreen();
                break;
            case GREEN_HOUSE:
                renderer.renderGreenHouseScreen();
                break;
            case SHOP:
                renderer.renderShopScreen();
                break;
            case HELP:
                renderer.renderHelpScreen();
                break;
        }

        if (currentMenu != MenuType.NONE) {
            switch (currentMenu) {
                case PAUSE:
                    renderer.renderPauseOverlay();
                    break;
                case REGISTER:
                    renderer.renderRegisterOverlay();
                    break;
                case LOGIN:
                    renderer.renderLoginOverlay();
                    break;
                case SETTING:
                    renderer.renderSettingOverlay();
                    break;
                case PROFILE:
                    renderer.renderProfileOverlay();
                    break;
                case NEWS:
                    renderer.renderNewsOverlay();
                    break;
                case QUESTS:
                    renderer.renderQuestsOverlay();
                    break;
                case PLANT_SELECTOR:
                    renderer.renderPlantSelectorOverlay();
                    break;
            }
        }
    }

    public void start() {
        renderer.clearScreen();
        inputListener.start();
    }

    public void stop() {
        inputListener.stop();
    }
}
