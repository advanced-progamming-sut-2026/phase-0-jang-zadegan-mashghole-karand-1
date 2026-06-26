package view;

import controller.InputHandler;
import model.core.ReadOnlyGameState;
import model.service.AuthState;
import model.service.GameNavigationState;
import view.renderer.Renderer;

public class ViewManager {
    private final Renderer renderer;
    private final InputListener inputListener;

    public ViewManager(Renderer renderer, InputHandler inputHandler) {
        this.renderer = renderer;
        this.inputListener = new InputListener(inputHandler, renderer::renderCommandPrompt);
    }

    public void showMessage(String message) {
        renderer.renderMessage(message);
    }

    public void showError(String message) {
        renderer.renderError(message);
    }

    public void render(ReadOnlyGameState state, ScreenType currentScreen, MenuType currentMenu,
            AuthState authState, GameNavigationState gameNavigation) {
        String screenKey = currentScreen.name();
        if (currentScreen == ScreenType.LEVEL_SELECTOR) {
            screenKey += "-" + gameNavigation.phase.name();
        }
        renderer.prepareScreen(screenKey);
        switch (currentScreen) {
            case REGISTER:
                renderer.renderRegisterScreen(authState.questions);
                break;
            case LOGIN:
                renderer.renderLoginScreen(authState.isAwaitingSecurityAnswer, authState.isAwaitingNewPassword,
                        authState.passwordResetQuestion);
                break;
            case MAIN:
                renderer.renderMainScreen();
                break;
            case LEVEL_SELECTOR:
                renderer.renderLevelSelectionScreen(gameNavigation);
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
        inputListener.start();
    }

    public void initialize() {
        renderer.clearScreen();
    }

    public void stop() {
        inputListener.stop();
    }
}
