package view.gdx.ui.screens.menus;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import controller.CommandResult.CommandResult;
import controller.ControllerManager;
import model.data.plant.PlantType;
import model.shop.ShopItem;
import model.shop.ShopItems;
import pvz.skin.PvzSkin;
import view.gdx.AssetContext;
import view.gdx.ui.HudOverlayRenderer;
import view.gdx.ui.UiSkin;
import view.gdx.ui.widgets.UiWidgets;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

final class ShopOverlayScreenSupport {
    private ShopOverlayScreenSupport() {
    }

    interface ShopActions {
        void showPlantSelection(ShopItem shopItem, int quantity);

        void showConfirm(ShopItem shopItem, int quantity);

        void closePlantSelection();

        void buyItem(ShopItem shopItem, int quantity, String selectedPlant);

        void showPurchaseNotice(CommandResult result);

        void closeConfirm();

        void refreshDailyDealUi();
    }

    static ScrollPane scroll(Table table) {
        ScrollPane pane = new ScrollPane(table, UiSkin.get());
        pane.setFadeScrollBars(false);
        pane.setScrollbarsVisible(true);
        pane.setOverscroll(false, false);
        return pane;
    }

    static String formatDailyRemaining() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextReset = now.toLocalDate().plusDays(1).atStartOfDay();
        Duration remaining = Duration.between(now, nextReset);
        long totalSeconds = remaining.getSeconds();
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("Resets in %02d:%02d:%02d", hours, minutes, seconds);
    }

    static String itemImageId(ShopItem item) {
        return switch (item.getId()) {
            case "pot" -> "IMAGE_ZEN_GARDEN_GROWING_PLANT_SLOT_GROWING_PLANT_SLOT_184X161";
            case "plant food" -> HudOverlayRenderer.PF_LEAF;
            case "seed pack random", "seed pack selectable" -> "IMAGE_UI_STOREMULTI_SEEDPACKETICON";
            case "gem to coin" -> "IMAGE_UI_COINS_STACK_3";
            default -> null;
        };
    }

    static void loadItemImages(AssetContext assets, Map<ShopItem, Image> itemImages) {
        if (assets == null) {
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
            TextureRegion region = assets.region(id);
            if (region != null) {
                img.setDrawable(new TextureRegionDrawable(region));
            }
        }
    }

    static void addItemsPanelHeader(Table itemsPanel) {
        Label item = UiWidgets.body("Item");
        Label price = UiWidgets.body("Price");
        Label info = UiWidgets.body("Info");
        Label qty = UiWidgets.body("Qty");

        item.setAlignment(Align.center);
        price.setAlignment(Align.center);
        info.setAlignment(Align.center);
        qty.setAlignment(Align.center);

        itemsPanel.add(item).width(180).center();
        itemsPanel.add(price).width(100).center();
        itemsPanel.add(info).width(220).center();
        itemsPanel.add(qty).width(100).center().row();
        itemsPanel.add().colspan(4).height(25).row();
    }

    static void createShopItem(Table itemsPanel, Map<ShopItem, Image> itemImages, ShopItem shopItem,
            ShopActions actions) {
        Label nameLabel = UiWidgets.body(shopItem.getName());
        nameLabel.setAlignment(Align.center);

        Label priceLabel = UiWidgets.body(
                shopItem.getPrice() + " " + shopItem.getCurrency().name());
        priceLabel.setAlignment(Align.center);

        Label descriptionLabel = UiWidgets.body(shopItem.getDescription());
        descriptionLabel.setAlignment(Align.center);
        descriptionLabel.setWrap(true);

        final int[] quantityValue = {1};
        Label quantityLabel = UiWidgets.body("1");
        quantityLabel.setAlignment(Align.center);

        Table quantityTable = buildQuantityTable(quantityValue, quantityLabel);

        Image icon = new Image();
        itemImages.put(shopItem, icon);

        Table itemTable = new Table();
        itemTable.add(icon).size(48, 48).padBottom(4).row();
        itemTable.add(nameLabel).width(110).center();

        itemsPanel.add(itemTable).width(180).height(75).center();
        itemsPanel.add(priceLabel).width(100).center();
        itemsPanel.add(descriptionLabel).width(220).center();
        itemsPanel.add(quantityTable).width(100).center().row();
        itemsPanel.add().colspan(4).height(10).row();

        TextButton buyButton = UiWidgets.plain("BUY");
        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int quantity = quantityValue[0];
                if (shopItem == ShopItems.SEED_PACK_SELECTABLE.getShopItem()) {
                    actions.showPlantSelection(shopItem, quantity);
                    return;
                }
                actions.showConfirm(shopItem, quantity);
            }
        });
        itemsPanel.add(buyButton).colspan(4).width(100).height(35).center().padBottom(10).row();
    }

    private static Table buildQuantityTable(int[] quantityValue, Label quantityLabel) {
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
        return quantityTable;
    }

    static Table buildPlantSidebar(ControllerManager controller, Consumer<String> onPlantSelected,
            Runnable onBack) {
        Table plantSidebar = new Table();
        plantSidebar.setBackground(
                PvzSkin.get().getDrawable("image_ui_quests_panel_edge_to_edge_ten")
        );

        Label title = UiWidgets.body("Select a Plant");
        title.setAlignment(Align.center);
        plantSidebar.add(title).width(350).height(50).padTop(10).row();

        Table plantList = new Table();
        for (PlantType plantName : controller.getGameNavigation().unlockedPlants) {
            TextButton plantButton = UiWidgets.plain(plantName.name);
            plantButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    onPlantSelected.accept(plantName.name);
                }
            });
            plantList.add(plantButton).width(280).height(40).padBottom(5).row();
        }

        ScrollPane plantScroll = scroll(plantList);
        plantScroll.addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                plantScroll.setScrollY(plantScroll.getScrollY() + amountY * 40f);
                return true;
            }
        });

        plantSidebar.add(plantScroll).width(320).height(230).padTop(5).padBottom(10).row();
        TextButton backButton = UiWidgets.plain("BACK");
        UiWidgets.onChange(backButton, onBack);
        plantSidebar.add(backButton).width(100).height(40).padBottom(10).row();
        return plantSidebar;
    }

    static Table buildConfirmPanel(String message, Runnable onYes, Runnable onNo) {
        Table confirmPanel = new Table();
        confirmPanel.setBackground(
                PvzSkin.get().getDrawable("image_ui_quests_panel_edge_to_edge_ten")
        );

        Label text = UiWidgets.body(message);
        text.setAlignment(Align.center);
        text.setWrap(true);

        TextButton yes = UiWidgets.plain("YES");
        TextButton no = UiWidgets.plain("NO");
        yes.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onYes.run();
            }
        });
        no.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onNo.run();
            }
        });

        confirmPanel.add(text).width(300).pad(16).colspan(2).row();
        confirmPanel.add(yes).width(100).height(40).pad(8);
        confirmPanel.add(no).width(100).height(40).pad(8).row();
        return confirmPanel;
    }

    static void centerOverlay(Table root, Table overlay, float width, float height) {
        overlay.setSize(width, height);
        overlay.setPosition(
                (root.getWidth() - overlay.getWidth()) / 2f,
                (root.getHeight() - overlay.getHeight()) / 2f
        );
    }

    static void showDailyConfirm(Table root, ControllerManager controller, ShopActions actions,
            BiConsumer<Table, Runnable> confirmPresenter) {
        var user = controller.getStorage().getCurrentUser();
        if (user == null || user.dailyDeal.dailyDealPlant == null) {
            return;
        }

        PlantType plant = user.dailyDeal.dailyDealPlant;
        int price = user.dailyDeal.dailyDealPrice;
        String message = "Buy Daily Deal: 10x " + plant.name + " for " + price + " COIN?";
        Table confirmPanel = buildConfirmPanel(message,
                () -> {
                    CommandResult result = controller.getShopController().buy("daily", 1);
                    UiWidgets.apply(controller, result);
                    actions.showPurchaseNotice(result);
                    actions.closeConfirm();
                    actions.refreshDailyDealUi();
                },
                actions::closeConfirm);
        confirmPresenter.accept(confirmPanel, () -> centerOverlay(root, confirmPanel, 380, 180));
    }
}
