package view.gdx.ui.screens.menus;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import controller.CommandResult.CommandResult;
import controller.ControllerManager;
import model.data.plant.PlantType;
import model.shop.ShopItem;
import model.shop.ShopItems;
import pvz.skin.PvzSkin;
import view.gdx.AssetContext;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.UiWidgets;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public final class ShopOverlayScreen implements UiScreen, ShopOverlayScreenSupport.ShopActions {

    private ShopItem pendingBuyItem;
    private int pendingQty;

    private String selectedPlant;

    private Table plantSidebar;

    private final Stage stage;
    private ControllerManager controller;
    private AssetContext assest;

    private final Table root;
    private final Table panel;
    private final Table itemsPanel;
    private Table confirmPanel;

    private Table noticePanel;
    private float noticeTimer;

    private Label dailyNameLabel;
    private Label dailyPriceLabel;
    private TextButton dailyBuyButton;

    private Label dailyTimerLabel;
    private float dailyTimerTick;
    private LocalDate lastSeenDate = LocalDate.now();

    private final Map<ShopItem, com.badlogic.gdx.scenes.scene2d.ui.Image> itemImages = new HashMap<>();

    public ShopOverlayScreen() {
        stage = new Stage(new ScreenViewport());
        root = new Table();
        root.setFillParent(true);
        itemsPanel = new Table();
        itemsPanel.top();
        panel = buildMainPanel();
        root.add(panel).width(700).height(800).center().top();
        stage.addActor(root);
    }

    private Table buildMainPanel() {
        Table mainPanel = new Table();
        mainPanel.setBackground(
                PvzSkin.get().getDrawable("image_ui_quests_panel_edge_to_edge_ten")
        );

        Label dailyTitle = UiWidgets.body("Daily Deal");
        dailyTitle.setAlignment(Align.center);
        mainPanel.add(dailyTitle).colspan(4).padTop(12).padBottom(4).row();

        dailyTimerLabel = UiWidgets.body("Resets in --:--:--");
        dailyTimerLabel.setAlignment(Align.center);
        mainPanel.add(dailyTimerLabel).colspan(4).padBottom(8).row();

        createDailyDealRow(mainPanel);
        mainPanel.add().colspan(4).height(16).row();

        ShopOverlayScreenSupport.addItemsPanelHeader(itemsPanel);
        for (ShopItems entry : ShopItems.values()) {
            ShopOverlayScreenSupport.createShopItem(itemsPanel, itemImages, entry.getShopItem(), this);
        }

        var shopScroll = ShopOverlayScreenSupport.scroll(itemsPanel);
        shopScroll.setScrollingDisabled(true, false);
        shopScroll.setForceScroll(true, false);
        mainPanel.add(shopScroll).colspan(4).width(680).height(520).pad(8).grow().row();

        TextButton close = UiWidgets.plain("Close");
        UiWidgets.onChange(close, () -> UiWidgets.apply(controller, controller.exitMenu()));
        mainPanel.add(close).colspan(4).center().padTop(12).padBottom(15).row();
        return mainPanel;
    }

    private void createDailyDealRow(Table targetPanel) {
        dailyNameLabel = UiWidgets.body("-");
        dailyPriceLabel = UiWidgets.body("-");
        Label dailyInfoLabel = UiWidgets.body("10 seed packets · once per day");
        dailyBuyButton = UiWidgets.plain("BUY");

        dailyNameLabel.setAlignment(Align.center);
        dailyPriceLabel.setAlignment(Align.center);
        dailyInfoLabel.setAlignment(Align.center);
        dailyInfoLabel.setWrap(true);

        Table itemTable = new Table();
        itemTable.add(dailyNameLabel).width(110).center();
        targetPanel.add(itemTable).width(180).center();
        targetPanel.add(dailyPriceLabel).width(100).center();
        targetPanel.add(dailyInfoLabel).width(220).center();
        targetPanel.add(UiWidgets.body("1")).width(100).center().row();
        targetPanel.add().colspan(4).height(10).row();

        dailyBuyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showDailyConfirm();
            }
        });
        targetPanel.add(dailyBuyButton).colspan(4).width(100).height(35).center().padBottom(10).row();
    }

    @Override
    public void showPlantSelection(ShopItem shopItem, int quantity) {
        pendingBuyItem = shopItem;
        pendingQty = quantity;

        plantSidebar = ShopOverlayScreenSupport.buildPlantSidebar(controller,
                plantName -> {
                    selectedPlant = plantName;
                    showConfirm(pendingBuyItem, pendingQty);
                },
                this::closePlantSelection);
        root.addActor(plantSidebar);
        ShopOverlayScreenSupport.centerOverlay(root, plantSidebar, 380, 380);
    }

    @Override
    public void showConfirm(ShopItem shopItem, int quantity) {
        closeConfirm();
        int total = shopItem.getPrice() * quantity;
        String plantPart = selectedPlant != null ? " (" + selectedPlant + ")" : "";
        String message = "Buy " + quantity + "x " + shopItem.getName() + plantPart
                + " for " + total + " " + shopItem.getCurrency().name() + "?";
        confirmPanel = ShopOverlayScreenSupport.buildConfirmPanel(message,
                () -> {
                    buyItem(shopItem, quantity, selectedPlant);
                    closeConfirm();
                    closePlantSelection();
                },
                this::closeConfirm);
        root.addActor(confirmPanel);
        ShopOverlayScreenSupport.centerOverlay(root, confirmPanel, 380, 180);
    }

    @Override
    public void buyItem(ShopItem shopItem, int quantity, String selectedPlant) {
        CommandResult result;
        if (selectedPlant != null) {
            result = controller.getShopController().buy(
                    shopItem.getId(),
                    quantity,
                    PlantType.fromName(selectedPlant)
            );
        } else {
            result = controller.getShopController().buy(shopItem.getId(), quantity);
        }
        UiWidgets.apply(controller, result);
        showPurchaseNotice(result);
    }

    @Override
    public void closePlantSelection() {
        closeConfirm();
        pendingBuyItem = null;
        pendingQty = 0;
        selectedPlant = null;
        if (plantSidebar != null) {
            plantSidebar.remove();
            plantSidebar = null;
        }
    }

    @Override
    public void closeConfirm() {
        if (confirmPanel != null) {
            confirmPanel.remove();
            confirmPanel = null;
        }
    }

    @Override
    public void showPurchaseNotice(CommandResult result) {
        closePurchaseNotice();
        noticePanel = new Table();
        noticePanel.setBackground(
                PvzSkin.get().getDrawable("image_ui_quests_panel_edge_to_edge_ten")
        );
        Label msg = UiWidgets.body(result.message);
        msg.setAlignment(Align.center);
        msg.setWrap(true);
        noticePanel.add(msg).width(420).pad(16).row();
        root.addActor(noticePanel);
        noticePanel.pack();
        noticePanel.setPosition(
                (root.getWidth() - noticePanel.getWidth()) / 2f,
                root.getHeight() - noticePanel.getHeight() - 40f
        );
        noticeTimer = 2.5f;
    }

    private void closePurchaseNotice() {
        noticeTimer = 0f;
        if (noticePanel != null) {
            noticePanel.remove();
            noticePanel = null;
        }
    }

    @Override
    public void refreshDailyDealUi() {
        if (controller == null) {
            return;
        }
        controller.getShopController().refreshDailyDeal();
        var user = controller.getStorage().getCurrentUser();
        if (user == null) {
            return;
        }
        var deal = user.dailyDeal;
        PlantType plant = deal.dailyDealPlant;
        if (plant == null) {
            dailyNameLabel.setText("Unavailable");
            dailyPriceLabel.setText("-");
            dailyBuyButton.setDisabled(true);
            return;
        }
        dailyNameLabel.setText(plant.name);
        dailyPriceLabel.setText(deal.dailyDealPrice + " COIN");
        if (deal.dailyDealPurchased) {
            dailyBuyButton.setText("SOLD");
            dailyBuyButton.setDisabled(true);
        } else {
            dailyBuyButton.setText("BUY");
            dailyBuyButton.setDisabled(false);
        }
    }

    private void showDailyConfirm() {
        ShopOverlayScreenSupport.showDailyConfirm(root, controller, this, (panel, positioner) -> {
            closeConfirm();
            confirmPanel = panel;
            root.addActor(confirmPanel);
            positioner.run();
        });
    }

    private void updateDailyTimerLabel() {
        if (dailyTimerLabel != null) {
            dailyTimerLabel.setText(ShopOverlayScreenSupport.formatDailyRemaining());
        }
    }

    @Override
    public void show(UiViewContext context) {
        this.controller = context.controller;
        this.assest = context.assets;
        lastSeenDate = LocalDate.now();
        refreshDailyDealUi();
        ShopOverlayScreenSupport.loadItemImages(assest, itemImages);
        updateDailyTimerLabel();
        dailyTimerTick = 0f;
    }

    @Override
    public void act(float deltaSeconds) {
        stage.act(deltaSeconds);
        dailyTimerTick += deltaSeconds;
        if (dailyTimerTick >= 1f) {
            dailyTimerTick = 0f;
            updateDailyTimerLabel();
            LocalDate today = LocalDate.now();
            if (!today.equals(lastSeenDate)) {
                lastSeenDate = today;
                refreshDailyDealUi();
            }
        }
        if (noticeTimer > 0f) {
            noticeTimer -= deltaSeconds;
            if (noticeTimer <= 0f) {
                closePurchaseNotice();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public Stage stage() {
        return stage;
    }
}
