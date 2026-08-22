package view.gdx.ui.screens.menus;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
import view.gdx.ui.UiSkin;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.UiWidgets;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public final class ShopOverlayScreen implements UiScreen {

    private enum ShopUiMode {
        LIST,
        PICK_PLANT
    }

    private ShopUiMode mode = ShopUiMode.LIST;

    private ShopItem pendingBuyItem;
    private int pendingQty;

    private String selectedPlant;

    private TextButton selectedPlantButton;

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
    private Label dailyInfoLabel;
    private TextButton dailyBuyButton;

    private Label dailyTimerLabel;
    private float dailyTimerTick;
    private LocalDate lastSeenDate = LocalDate.now();

    private final Map<ShopItem, Image> itemImages = new HashMap<>();

    public ShopOverlayScreen() {

        stage = new Stage(new ScreenViewport());

        root = new Table();
        root.setFillParent(true);

        panel = new Table();
        itemsPanel = new Table();
        itemsPanel.top();

        panel.setBackground(
                PvzSkin.get().getDrawable(
                        "image_ui_quests_panel_edge_to_edge_ten"
                )
        );

        Label dailyTitle = UiWidgets.body("Daily Deal");
        dailyTitle.setAlignment(Align.center);
        panel.add(dailyTitle).colspan(4).padTop(12).padBottom(4).row();

        dailyTimerLabel = UiWidgets.body("Resets in --:--:--");
        dailyTimerLabel.setAlignment(Align.center);
        panel.add(dailyTimerLabel).colspan(4).padBottom(8).row();

        createDailyDealRow();
        panel.add().colspan(4).height(16).row();

        Label item = UiWidgets.body("Item");
        Label price = UiWidgets.body("Price");
        Label info = UiWidgets.body("Info");
        Label qty = UiWidgets.body("Qty");

        item.setAlignment(Align.center);
        price.setAlignment(Align.center);
        info.setAlignment(Align.center);
        qty.setAlignment(Align.center);

        itemsPanel.add(item)
                .width(180)
                .center();

        itemsPanel.add(price)
                .width(100)
                .center();

        itemsPanel.add(info)
                .width(220)
                .center();

        itemsPanel.add(qty)
                .width(100)
                .center()
                .row();

        itemsPanel.add()
                .colspan(4)
                .height(25)
                .row();

        for (ShopItems entry : ShopItems.values()) {
            createShopItem(entry.getShopItem());
        }

        ScrollPane shopScroll = scroll(itemsPanel);
        shopScroll.setScrollingDisabled(true, false);
        shopScroll.setForceScroll(true, false);

        panel.add(shopScroll)
                .colspan(4)
                .width(680)
                .height(520)
                .pad(8)
                .grow()
                .row();

        TextButton close = UiWidgets.plain("Close");

        UiWidgets.onChange(
                close,
                () -> UiWidgets.apply(
                        controller,
                        controller.exitMenu()
                )
        );

        panel.add(close)
                .colspan(4)
                .center()
                .padTop(12)
                .padBottom(15)
                .row();

        root.add(panel)
                .width(700)
                .height(800)
                .center()
                .top();

        stage.addActor(root);
    }

    private void createShopItem(ShopItem shopItem) {

        Label nameLabel = UiWidgets.body(shopItem.getName());
        nameLabel.setAlignment(Align.center);

        Label priceLabel = UiWidgets.body(
                shopItem.getPrice() + " " + shopItem.getCurrency().name()
        );
        priceLabel.setAlignment(Align.center);

        Label descriptionLabel = UiWidgets.body(shopItem.getDescription());
        descriptionLabel.setAlignment(Align.center);
        descriptionLabel.setWrap(true);

        final int[] quantityValue = {1};

        Label quantityLabel = UiWidgets.body("1");
        quantityLabel.setAlignment(Align.center);

        TextButton minusButton = UiWidgets.plain("-");
        TextButton plusButton = UiWidgets.plain("+");

        Table quantityTable = new Table();
        quantityTable.add(minusButton).size(30, 30);
        quantityTable.add(quantityLabel).width(35).center();
        quantityTable.add(plusButton).size(30, 30);

        minusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (quantityValue[0] > 1) {
                    quantityValue[0]--;
                    quantityLabel.setText(String.valueOf(quantityValue[0]));
                }
            }
        });

        plusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                quantityValue[0]++;
                quantityLabel.setText(String.valueOf(quantityValue[0]));
            }
        });

        Image icon = new Image();
        itemImages.put(shopItem, icon);

        Table itemTable = new Table();
        itemTable.add(icon).size(48, 48).padBottom(4).row();
        itemTable.add(nameLabel).width(110).center();

        itemsPanel.add(itemTable)
                .width(180)
                .height(75)
                .center();

        itemsPanel.add(priceLabel)
                .width(100)
                .center();

        itemsPanel.add(descriptionLabel)
                .width(220)
                .center();

        itemsPanel.add(quantityTable)
                .width(100)
                .center()
                .row();

        itemsPanel.add()
                .colspan(4)
                .height(10)
                .row();

        TextButton buyButton = UiWidgets.plain("BUY");

        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int quantity = quantityValue[0];

                if (shopItem == ShopItems.SEED_PACK_SELECTABLE.getShopItem()) {
                    showPlantSelection(shopItem, quantity);
                    return;
                }
                showConfirm(shopItem, quantity);
            }
        });

        itemsPanel.add(buyButton)
                .colspan(4)
                .width(100)
                .height(35)
                .center()
                .padBottom(10)
                .row();
    }

    private void buyItem(ShopItem shopItem, int quantity, String selectedPlant) {
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

    private void showPlantSelection(ShopItem shopItem, int quantity) {
        mode = ShopUiMode.PICK_PLANT;

        pendingBuyItem = shopItem;
        pendingQty = quantity;

        plantSidebar = new Table();
        plantSidebar.setBackground(
                PvzSkin.get().getDrawable(
                        "image_ui_quests_panel_edge_to_edge_ten"
                )
        );

        Label title = UiWidgets.body("Select a Plant");
        title.setAlignment(Align.center);

        plantSidebar.add(title)
                .width(350)
                .height(50)
                .padTop(10)
                .row();

        Table plantList = new Table();

        for (PlantType plantName : controller.getGameNavigation().unlockedPlants) {
            TextButton plantButton = UiWidgets.plain(plantName.name);

            plantButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedPlant = plantName.name;
                    showConfirm(pendingBuyItem, pendingQty);
                }
            });

            plantList.add(plantButton)
                    .width(280)
                    .height(40)
                    .padBottom(5)
                    .row();
        }

        ScrollPane plantScroll = scroll(plantList);
        plantScroll.addListener(new InputListener() {
            @Override
            public boolean scrolled(
                    InputEvent event,
                    float x,
                    float y,
                    float amountX,
                    float amountY
            ) {
                plantScroll.setScrollY(plantScroll.getScrollY() + amountY * 40f);
                return true;
            }
        });

        plantSidebar.add(plantScroll)
                .width(320)
                .height(230)
                .padTop(5)
                .padBottom(10)
                .row();

        TextButton backButton = UiWidgets.plain("BACK");
        UiWidgets.onChange(backButton, this::closePlantSelection);

        plantSidebar.add(backButton)
                .width(100)
                .height(40)
                .padBottom(10)
                .row();

        root.addActor(plantSidebar);

        plantSidebar.setSize(380, 380);
        plantSidebar.setPosition(
                (root.getWidth() - plantSidebar.getWidth()) / 2f,
                (root.getHeight() - plantSidebar.getHeight()) / 2f
        );
    }

    private ScrollPane scroll(Table table) {
        ScrollPane pane = new ScrollPane(table, UiSkin.get());
        pane.setFadeScrollBars(false);
        pane.setScrollbarsVisible(true);
        pane.setOverscroll(false, false);
        return pane;
    }

    private void closePlantSelection() {
        closeConfirm();

        mode = ShopUiMode.LIST;
        pendingBuyItem = null;
        pendingQty = 0;
        selectedPlant = null;
        selectedPlantButton = null;

        if (plantSidebar != null) {
            plantSidebar.remove();
            plantSidebar = null;
        }
    }

    private void showConfirm(ShopItem shopItem, int quantity) {
        closeConfirm();

        confirmPanel = new Table();
        confirmPanel.setBackground(
                PvzSkin.get().getDrawable("image_ui_quests_panel_edge_to_edge_ten")
        );

        int total = shopItem.getPrice() * quantity;
        String plantPart = selectedPlant != null ? " (" + selectedPlant + ")" : "";
        Label text = UiWidgets.body(
                "Buy " + quantity + "x " + shopItem.getName() + plantPart
                        + " for " + total + " " + shopItem.getCurrency().name() + "?"
        );
        text.setAlignment(Align.center);
        text.setWrap(true);

        TextButton yes = UiWidgets.plain("YES");
        TextButton no = UiWidgets.plain("NO");

        yes.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buyItem(shopItem, quantity, selectedPlant);
                closeConfirm();
                closePlantSelection();
            }
        });
        no.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeConfirm();
            }
        });

        confirmPanel.add(text).width(300).pad(16).colspan(2).row();
        confirmPanel.add(yes).width(100).height(40).pad(8);
        confirmPanel.add(no).width(100).height(40).pad(8).row();

        root.addActor(confirmPanel);
        confirmPanel.setSize(380, 180);
        confirmPanel.setPosition(
                (root.getWidth() - confirmPanel.getWidth()) / 2f,
                (root.getHeight() - confirmPanel.getHeight()) / 2f
        );
    }

    private void closeConfirm() {
        if (confirmPanel != null) {
            confirmPanel.remove();
            confirmPanel = null;
        }
    }

    private void showPurchaseNotice(CommandResult result) {
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

    private void refreshDailyDealUi() {
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

    private void createDailyDealRow() {
        dailyNameLabel = UiWidgets.body("-");
        dailyPriceLabel = UiWidgets.body("-");
        dailyInfoLabel = UiWidgets.body("10 seed packets · once per day");
        dailyBuyButton = UiWidgets.plain("BUY");

        dailyNameLabel.setAlignment(Align.center);
        dailyPriceLabel.setAlignment(Align.center);
        dailyInfoLabel.setAlignment(Align.center);
        dailyInfoLabel.setWrap(true);

        Table itemTable = new Table();
        itemTable.add(dailyNameLabel).width(110).center();

        panel.add(itemTable).width(180).center();
        panel.add(dailyPriceLabel).width(100).center();
        panel.add(dailyInfoLabel).width(220).center();
        panel.add(UiWidgets.body("1")).width(100).center().row();
        panel.add().colspan(4).height(10).row();

        dailyBuyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showDailyConfirm();
            }
        });
        panel.add(dailyBuyButton)
                .colspan(4)
                .width(100)
                .height(35)
                .center()
                .padBottom(10)
                .row();
    }

    private void showDailyConfirm() {
        var user = controller.getStorage().getCurrentUser();
        if (user == null || user.dailyDeal.dailyDealPlant == null) {
            return;
        }

        PlantType plant = user.dailyDeal.dailyDealPlant;
        int price = user.dailyDeal.dailyDealPrice;

        closeConfirm();
        confirmPanel = new Table();
        confirmPanel.setBackground(
                PvzSkin.get().getDrawable("image_ui_quests_panel_edge_to_edge_ten")
        );

        Label text = UiWidgets.body(
                "Buy Daily Deal: 10x " + plant.name + " for " + price + " COIN?"
        );
        text.setAlignment(Align.center);

        TextButton yes = UiWidgets.plain("YES");
        TextButton no = UiWidgets.plain("NO");

        yes.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CommandResult result = controller.getShopController().buy("daily", 1);
                UiWidgets.apply(controller, result);
                showPurchaseNotice(result);
                closeConfirm();
                refreshDailyDealUi();
            }
        });
        no.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeConfirm();
            }
        });

        confirmPanel.add(text).width(300).pad(16).colspan(2).row();
        confirmPanel.add(yes).width(100).height(40).pad(8);
        confirmPanel.add(no).width(100).height(40).pad(8).row();

        root.addActor(confirmPanel);
        confirmPanel.setSize(380, 180);
        confirmPanel.setPosition(
                (root.getWidth() - confirmPanel.getWidth()) / 2f,
                (root.getHeight() - confirmPanel.getHeight()) / 2f
        );
    }

    private static String formatDailyRemaining() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextReset = now.toLocalDate().plusDays(1).atStartOfDay();
        Duration remaining = Duration.between(now, nextReset);
        long totalSeconds = remaining.getSeconds();
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("Resets in %02d:%02d:%02d", hours, minutes, seconds);
    }

    private void updateDailyTimerLabel() {
        if (dailyTimerLabel != null) {
            dailyTimerLabel.setText(formatDailyRemaining());
        }
    }

    private String itemImageId(ShopItem item) {
        return switch (item.getId()) {
            case "pot" -> "IMAGE_ZEN_GARDEN_GROWING_PLANT_SLOT_GROWING_PLANT_SLOT_184X161";
            case "plant food" -> "IMAGE_UI_ALMANAC_PLANT_FOOD_STAT_ICON";
            case "seed pack random", "seed pack selectable" -> "IMAGE_UI_STOREMULTI_SEEDPACKETICON";
            case "gem to coin" -> "IMAGE_UI_COINS_STACK_3";
            default -> null;
        };
    }

    private void loadItemImages() {
        if (assest == null) {
            return;
        }
        for (var entry : itemImages.entrySet()) {
            Image img = entry.getValue();
            if (img.getDrawable() != null) {
                continue;
            }
            String id = itemImageId(entry.getKey());
            if (id == null) {
                continue;
            }
            TextureRegion region = assest.region(id);
            if (region != null) {
                img.setDrawable(new TextureRegionDrawable(region));
            }
        }
    }

    @Override
    public void show(UiViewContext context) {
        this.controller = context.controller;
        this.assest = context.assets;
        lastSeenDate = LocalDate.now();
        refreshDailyDealUi();
        loadItemImages();
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
