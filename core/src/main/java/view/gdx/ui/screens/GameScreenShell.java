package view.gdx.ui.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import controller.ControllerManager;
import controller.InputHandler;
import view.MenuType;
import view.gdx.AssetContext;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiSkin;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.PamPreviewActor;
import view.gdx.ui.widgets.UiWidgets;

public final class GameScreenShell implements UiScreen {
    private static final String ZOMBOSS_PAM = "768/FULL/NPC/ZOMBOSS/ZOMBOSS.PAM";
    private static final String ZOMBOSS_CLIP = "zomboss_talk";
    private static final String CRAZY_DAVE_PAM = "768/INITIAL/CRAZYDAVE/CRAZYDAVE/CRAZYDAVE.PAM";
    private static final String CRAZY_DAVE_CLIP = "anim_mediumtalk";
    private static final int DEBUG_SUN_GRANT = 50;

    private final Stage stage = new Stage(new ScreenViewport());
    private final ImageButton pause;
    private final Table debugBar;
    private final Table cheatRow;
    private final TextField cheatField;
    private final Table dialogueRoot;
    private final Label speakerLabel;
    private final Label dialogueLabel;
    private final Stack characterStack;
    private final Texture panelTexture;

    private PamPreviewActor pennyPreview;
    private PamPreviewActor davePreview;
    private ControllerManager controller;
    private AssetContext assets;
    private InputHandler inputHandler;

    public GameScreenShell() {
        pause = new ImageButton(new ImageButton.ImageButtonStyle());
        UiWidgets.onChange(pause, () -> {
            if (controller != null) {
                UiWidgets.apply(controller, controller.enterMenu("pause"));
            }
        });

        TextButton addSun = UiWidgets.secondary("+Sun");
        TextButton addFood = UiWidgets.secondary("+Food");
        TextButton cheatsToggle = UiWidgets.plain("Cheats");
        TextButton runCheat = UiWidgets.primary("Run");
        cheatField = UiWidgets.field("cheat add -n 50 suns", false);
        cheatRow = new Table();
        cheatRow.add(cheatField).width(280f).height(36f).padRight(6f);
        cheatRow.add(runCheat).width(72f).height(36f);
        cheatRow.setVisible(false);

        UiWidgets.onChange(addSun, () -> {
            if (controller != null) {
                UiWidgets.apply(controller, controller.getGameMechanismController().addSun(DEBUG_SUN_GRANT));
            }
        });
        UiWidgets.onChange(addFood, () -> {
            if (controller != null) {
                UiWidgets.apply(controller, controller.getGameMechanismController().addPlantFood());
            }
        });
        UiWidgets.onChange(cheatsToggle, this::toggleCheatRow);
        UiWidgets.onChange(runCheat, this::submitCheat);
        cheatField.setTextFieldListener((field, c) -> {
            if (c == '\n' || c == '\r') {
                submitCheat();
            }
        });

        Table debugButtons = new Table();
        debugButtons.add(addSun).width(80f).height(36f).padRight(4f);
        debugButtons.add(addFood).width(80f).height(36f).padRight(4f);
        debugButtons.add(cheatsToggle).width(88f).height(36f);

        debugBar = new Table();
        debugBar.setVisible(false);
        debugBar.add(debugButtons).right().row();
        debugBar.add(cheatRow).right().padTop(6f);

        panelTexture = solid(new Color(0.75f, 0.55f, 0.30f, 1f), 1, 1);
        speakerLabel = new Label("", UiSkin.get(), "big");
        dialogueLabel = new Label("", UiSkin.get(), "default");
        dialogueLabel.setWrap(true);
        characterStack = new Stack();
        characterStack.setSize(220f, 220f);

        TextButton continueButton = UiWidgets.primary("Continue");
        UiWidgets.onChange(continueButton, () -> {
            if (controller != null) {
                controller.advanceDialogue();
            }
        });

        Table panel = new Table();
        panel.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        panel.pad(16f);
        panel.add(speakerLabel).left().padBottom(8f).row();
        panel.add(dialogueLabel).width(420f).left().padBottom(12f).row();
        panel.add(continueButton).right().width(140f).height(40f);

        dialogueRoot = new Table();
        dialogueRoot.setFillParent(true);
        dialogueRoot.setVisible(false);
        dialogueRoot.setTouchable(Touchable.enabled);
        dialogueRoot.center();
        dialogueRoot.add(characterStack).size(220f).padRight(16f);
        dialogueRoot.add(panel).width(460f);

        Table root = new Table();
        root.setFillParent(true);
        root.setTouchable(Touchable.childrenOnly);
        root.top().right().padTop(GlobalTopBar.reservedScreenHeight()).padRight(10f);
        root.add(pause).width(120f).height(44f).row();
        root.add(debugBar).right().padTop(8f);

        stage.addActor(root);
        stage.addActor(dialogueRoot);
    }

    @Override
    public void show(UiViewContext context) {
        controller = context.controller;
        assets = context.assets;
        if (controller != null) {
            inputHandler = new InputHandler(controller);
        }
        boolean dialogueActive = controller != null && controller.isDialogueActive();
        boolean menuClear = context.menu == null || context.menu == MenuType.NONE;
        boolean hudVisible = menuClear && !dialogueActive;
        boolean debug = context.settings != null && context.settings.debugMode;
        pause.setVisible(hudVisible);
        debugBar.setVisible(hudVisible && debug);
        if (!debug) {
            cheatRow.setVisible(false);
            stage.setKeyboardFocus(null);
        }
        dialogueRoot.setVisible(dialogueActive);
        if (dialogueActive) {
            ensureCharacterActors();
            refreshDialogue();
        }
    }

    private void toggleCheatRow() {
        cheatRow.setVisible(!cheatRow.isVisible());
        if (cheatRow.isVisible()) {
            stage.setKeyboardFocus(cheatField);
        } else {
            stage.setKeyboardFocus(null);
        }
    }

    private void submitCheat() {
        if (controller == null || inputHandler == null) {
            return;
        }
        String command = UiWidgets.text(cheatField);
        if (command.isEmpty()) {
            return;
        }
        inputHandler.handleInput(command);
        cheatField.setText("");
        stage.setKeyboardFocus(null);
    }

    @Override
    public void act(float deltaSeconds) {
        if (pause.getStyle().imageUp == null && assets != null) {
            var region = assets.region("IMAGE_UI_HUD_INGAME_PAUSE_BUTTON");
            if (region != null) {
                ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(pause.getStyle());
                style.imageUp = new TextureRegionDrawable(region);
                pause.setStyle(style);
            }
        }
        stage.act(deltaSeconds);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        panelTexture.dispose();
    }

    @Override
    public Stage stage() {
        return stage;
    }

    private void ensureCharacterActors() {
        if (pennyPreview != null || assets == null) {
            return;
        }
        pennyPreview = new PamPreviewActor(assets, ZOMBOSS_PAM, ZOMBOSS_CLIP, 0.55f);
        davePreview = new PamPreviewActor(assets, CRAZY_DAVE_PAM, CRAZY_DAVE_CLIP, 0.55f);
        pennyPreview.setSize(220f, 220f);
        davePreview.setSize(220f, 220f);
        characterStack.clearChildren();
        characterStack.add(pennyPreview);
        characterStack.add(davePreview);
    }

    private void refreshDialogue() {
        if (controller == null) {
            return;
        }
        speakerLabel.setText(controller.currentDialogueSpeaker());
        dialogueLabel.setText(controller.currentDialogueText());
        boolean penny = "Penny".equals(controller.currentDialogueSpeaker());
        if (pennyPreview != null && davePreview != null) {
            pennyPreview.setVisible(penny);
            davePreview.setVisible(!penny);
        }
    }

    private static Texture solid(Color color, int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
