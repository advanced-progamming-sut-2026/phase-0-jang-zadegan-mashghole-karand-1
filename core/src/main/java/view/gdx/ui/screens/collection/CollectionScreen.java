package view.gdx.ui.screens.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import controller.ControllerManager;
import model.data.plant.PlantCategory;
import model.data.plant.PlantStats;
import model.data.plant.PlantTag;
import model.data.plant.PlantType;
import model.data.plant.PlantUpgradeCosts;
import model.data.zombie.ZombieType;
import model.service.CollectionViewState;
import model.service.CollectionViewState.Entry;
import model.service.CollectionViewState.Mode;
import model.service.CollectionViewState.Tab;
import model.storage.StorageManager;
import model.storage.user.User;
import pvz.skin.PvzSkin;
import view.gdx.AssetContext;
import view.gdx.catalog.DefaultVisualCatalog;
import view.gdx.catalog.PlantVisualDef;
import view.gdx.catalog.VisualCatalog;
import view.gdx.catalog.ZombieVisualDef;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiSkin;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.PamPreviewActor;
import view.gdx.ui.widgets.UiWidgets;

public final class CollectionScreen implements UiScreen {
    private static final String PANEL_BG = "image_ui_quests_panel_edge_to_edge_ten";
    private static final String LOCK_ICON = "IMAGE_UI_CARDS_LOCK_MEDIUM_GOLD";
    private static final String EMPTY_PACKET = "IMAGE_UI_PACKETS_EMPTY_PACKET";
    private static final String ZOMBIE_UNKNOWN = "IMAGE_UI_ALMANAC_ZOMBIE_SEED_PKT";
    private static final String COIN_ICON = "IMAGE_EFFECTS_COIN_GOLD_COIN_GOLD_98X95";

    private final Stage stage;
    private final Table root;
    private final VisualCatalog catalog = new DefaultVisualCatalog();

    private ControllerManager controller;
    private AssetContext assets;
    private UiViewContext lastContext;

    private String familyFilter = "All";
    private String lockFilter = "All";
    private boolean upgradeableOnly;

    public CollectionScreen() {
        stage = new Stage(new ScreenViewport());
        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
    }

    @Override
    public void show(UiViewContext context) {
        controller = context.controller;
        assets = context.assets;
        lastContext = context;

        CollectionViewState collection = context.collection;
        if (collection.tab == Tab.PLANTS && collection.mode != Mode.ALL) {
            UiWidgets.apply(controller, controller.getCollectionController().showAllPlants());
            return;
        }
        if (collection.tab == Tab.ZOMBIES && collection.mode != Mode.ALL) {
            UiWidgets.apply(controller, controller.getCollectionController().showAllZombies());
            return;
        }

        rebuild(context);
    }

    private void rebuild(UiViewContext context) {
        root.clearChildren();
        root.pad(16f);

        CollectionViewState collection = context.collection;
        Table panel = new Table();
        panel.setBackground(PvzSkin.get().getDrawable(PANEL_BG));
        panel.pad(16f);

        panel.add(UiWidgets.title("Collection")).colspan(3).padBottom(8f).row();
        panel.add(buildTabs(collection)).colspan(3).padBottom(12f).row();

        if (collection.tab == Tab.PLANTS) {
            panel.add(buildPlantFilters()).colspan(3).padBottom(8f).row();
        }

        Table body = new Table();
        body.add(buildListPane(collection)).width(520f).height(520f).padRight(12f);
        body.add(buildDetailPane(collection)).width(360f).height(520f).grow();
        panel.add(body).colspan(3).row();

        root.add(panel).grow();
    }

    private Table buildTabs(CollectionViewState collection) {
        Table tabs = new Table();
        TextButton plants = UiWidgets.plain("Plants");
        TextButton zombies = UiWidgets.plain("Zombies");
        if (collection.tab == Tab.PLANTS) {
            plants.setColor(Color.WHITE);
            zombies.setColor(Color.LIGHT_GRAY);
        } else {
            zombies.setColor(Color.WHITE);
            plants.setColor(Color.LIGHT_GRAY);
        }
        UiWidgets.onChange(plants, () -> {
            UiWidgets.apply(controller, controller.getCollectionController().showAllPlants());
        });
        UiWidgets.onChange(zombies, () -> {
            UiWidgets.apply(controller, controller.getCollectionController().showAllZombies());
        });
        tabs.add(plants).width(140f).height(40f).padRight(8f);
        tabs.add(zombies).width(140f).height(40f);
        return tabs;
    }

    private Table buildPlantFilters() {
        Table filters = new Table();
        Array<String> families = new Array<>();
        families.add("All");
        for (PlantCategory category : PlantCategory.values()) {
            families.add(category.name());
        }
        SelectBox<String> familyBox = UiWidgets.selectBox(families);
        familyBox.setSelected(familyFilter);
        UiWidgets.onChange(familyBox, () -> {
            familyFilter = familyBox.getSelected();
            rebuild(lastContext);
        });

        Array<String> lockOptions = new Array<>();
        lockOptions.add("All");
        lockOptions.add("Unlocked");
        lockOptions.add("Locked");
        SelectBox<String> lockBox = UiWidgets.selectBox(lockOptions);
        lockBox.setSelected(lockFilter);
        UiWidgets.onChange(lockBox, () -> {
            lockFilter = lockBox.getSelected();
            rebuild(lastContext);
        });

        CheckBox upgradeable = UiWidgets.checkBox("Upgradeable");
        upgradeable.setChecked(upgradeableOnly);
        UiWidgets.onChange(upgradeable, () -> {
            upgradeableOnly = upgradeable.isChecked();
            rebuild(lastContext);
        });

        filters.add(UiWidgets.body("Family:")).padRight(6f);
        filters.add(familyBox).width(160f).padRight(12f);
        filters.add(UiWidgets.body("Status:")).padRight(6f);
        filters.add(lockBox).width(120f).padRight(12f);
        filters.add(upgradeable);
        return filters;
    }

    private ScrollPane buildListPane(CollectionViewState collection) {
        Table grid = new Table();
        grid.top().left();
        List<Entry> entries = filteredEntries(collection);
        int columns = collection.tab == Tab.PLANTS ? 3 : 4;
        int col = 0;
        for (Entry entry : entries) {
            Actor card = collection.tab == Tab.PLANTS
                    ? buildPlantCard(entry)
                    : buildZombieCard(entry);
            grid.add(card).size(collection.tab == Tab.PLANTS ? 150f : 110f).pad(6f);
            col++;
            if (col >= columns) {
                col = 0;
                grid.row();
            }
        }
        ScrollPane scroll = new ScrollPane(grid, UiSkin.get());
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);
        return scroll;
    }

    private List<Entry> filteredEntries(CollectionViewState collection) {
        if (collection.tab != Tab.PLANTS) {
            return collection.entries;
        }
        User user = currentUser();
        StorageManager storage = controller.getStorage();
        List<Entry> filtered = new ArrayList<>();
        for (Entry entry : collection.entries) {
            PlantType type = PlantType.fromName(entry.name);
            if (CollectionUiSupport.matchesPlantFilters(entry, type, familyFilter, lockFilter,
                    upgradeableOnly, user, storage)) {
                filtered.add(entry);
            }
        }
        return filtered;
    }

    private Actor buildPlantCard(Entry entry) {
        PlantType type = PlantType.fromName(entry.name);
        User user = currentUser();
        Table card = new Table();
        card.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);

        card.add(buildPlantCardImage(entry, type)).size(80f).padBottom(4f).row();
        card.add(UiWidgets.body(entry.name)).width(140f).align(Align.center).row();
        addPlantCardStatus(card, entry, type, user);
        addPlantCardClickListener(card, entry);
        return card;
    }

    private Stack buildPlantCardImage(Entry entry, PlantType type) {
        Stack imageStack = new Stack();
        Image packet = image(PlantPacketImages.packetId(type), 72f, 72f);
        imageStack.add(packet);
        if (!entry.unlocked) {
            Image lock = image(LOCK_ICON, 36f, 36f);
            imageStack.add(lock);
        }
        return imageStack;
    }

    private void addPlantCardStatus(Table card, Entry entry, PlantType type, User user) {
        if (entry.unlocked && user != null && type != null) {
            addUnlockedPlantCardStatus(card, type, user);
        } else if (!entry.unlocked) {
            addLockedPlantCardStatus(card, entry);
        }
    }

    private void addUnlockedPlantCardStatus(Table card, PlantType type, User user) {
        int level = user.getPlantLevel(type);
        int owned = user.getSeedPackets(type);
        int required = CollectionUiSupport.seedPacketsRequired(type, user);
        int remaining = CollectionUiSupport.seedPacketsRemaining(type, user);
        card.add(UiWidgets.body("Lv " + level)).align(Align.center).row();
        card.add(UiWidgets.body("Packets: " + owned)).align(Align.center).row();
        if (required > 0) {
            card.add(UiWidgets.body("Need: " + required + " (" + remaining + " left)"))
                    .align(Align.center).row();
        } else {
            card.add(UiWidgets.body("Max level")).align(Align.center).row();
        }
    }

    private void addLockedPlantCardStatus(Table card, Entry entry) {
        card.add(UiWidgets.body("Locked")).align(Align.center).row();
        TextButton buy = UiWidgets.secondary("Buy (2000)");
        buy.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.stop();
                UiWidgets.apply(controller,
                        controller.getCollectionController().purchasePlant(entry.name));
            }
        });
        card.add(buy).width(120f).height(32f).padTop(4f).row();
    }

    private void addPlantCardClickListener(Table card, Entry entry) {
        card.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (entry.unlocked) {
                    UiWidgets.apply(controller, controller.getCollectionController().showPlant(entry.name));
                } else {
                    UiWidgets.apply(controller, controller.getCollectionController().showPlantDebug(entry.name));
                }
            }
        });
    }

    private Actor buildZombieCard(Entry entry) {
        Table card = new Table();
        card.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
        ZombieType type = ZombieType.fromName(entry.name);

        if (entry.unlocked && type != null) {
            ZombieVisualDef visual = catalog.zombie(type);
            if (visual != null) {
                PamPreviewActor preview = new PamPreviewActor(
                        assets, visual.pamPath, visual.idleClip, 0.45f, intactArmorVisibility(visual));
                preview.setSize(90f, 90f);
                card.add(preview).size(90f).padBottom(4f).row();
            } else {
                card.add(image(ZOMBIE_UNKNOWN, 72f, 72f)).padBottom(4f).row();
            }
            card.add(UiWidgets.body(entry.name)).width(100f).align(Align.center).row();
            card.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    UiWidgets.apply(controller, controller.getCollectionController().showZombie(entry.name));
                }
            });
        } else {
            Stack placeholder = new Stack();
            placeholder.add(image(EMPTY_PACKET, 72f, 72f));
            placeholder.add(image(ZOMBIE_UNKNOWN, 48f, 48f));
            card.add(placeholder).size(80f).padBottom(4f).row();
            card.add(UiWidgets.body("???")).align(Align.center).row();
        }
        return card;
    }

    private Table buildDetailPane(CollectionViewState collection) {
        Table detail = new Table();
        detail.top().left();
        if (!collection.hasDetail()) {
            detail.add(UiWidgets.body("Select an entry to view details.")).pad(12f);
            return detail;
        }

        String title = collection.detailTitle;
        if (collection.tab == Tab.PLANTS) {
            buildPlantDetail(detail, title, collection);
        } else {
            buildZombieDetail(detail, title, collection);
        }
        return detail;
    }

    private void buildPlantDetail(Table detail, String title, CollectionViewState collection) {
        PlantType type = PlantType.fromName(title);
        User user = currentUser();
        addPlantDetailPreview(detail, type);
        detail.add(UiWidgets.title(title)).padBottom(6f).row();

        if (type != null) {
            addPlantDetailStats(detail, type, user);
        }

        for (String line : collection.detailLines) {
            detail.add(UiWidgets.body(line)).left().row();
        }

        addPlantDetailActions(detail, type, user);
    }

    private void addPlantDetailPreview(Table detail, PlantType type) {
        if (type == null) {
            return;
        }
        PlantVisualDef visual = catalog.plant(type);
        if (visual == null) {
            return;
        }
        PamPreviewActor preview = new PamPreviewActor(assets, visual.pamPath, visual.idleClip, 0.7f);
        preview.setSize(180f, 180f);
        detail.add(preview).size(180f).padBottom(8f).row();
    }

    private void addPlantDetailStats(Table detail, PlantType type, User user) {
        int level = user != null ? user.getPlantLevel(type) : PlantStats.DEFAULT_LEVEL;
        PlantStats stats = PlantStats.forLevel(type, level);
        detail.add(UiWidgets.body("Family: " + type.category.name())).left().row();
        String tags = type.tags == null || type.tags.isEmpty()
                ? "-"
                : type.tags.stream().map(PlantTag::name).collect(Collectors.joining(", "));
        detail.add(UiWidgets.body("Tags: " + tags)).left().padBottom(4f).row();
        detail.add(UiWidgets.body("HP: " + stats.hp)).left().row();
        detail.add(UiWidgets.body("Cost: " + stats.cost)).left().row();
        detail.add(UiWidgets.body("Damage: " + stats.damage)).left().row();
        if (user != null) {
            detail.add(UiWidgets.body("Level: " + stats.level + "/" + PlantStats.MAX_LEVEL)).left().row();
            detail.add(UiWidgets.body("Seed Packets: " + user.getSeedPackets(type))).left().padBottom(6f).row();
        }
    }

    private void addPlantDetailActions(Table detail, PlantType type, User user) {
        if (type == null || user == null) {
            return;
        }
        if (controller.getStorage().isPlantUnlocked(type)) {
            addPlantUpgradeAction(detail, type, user);
        } else if (!type.isBowlingExclusive()) {
            addPlantPurchaseAction(detail, type);
        }
    }

    private void addPlantUpgradeAction(Table detail, PlantType type, User user) {
        if (type.isBowlingExclusive() || user.getPlantLevel(type) >= PlantStats.MAX_LEVEL) {
            return;
        }
        int next = user.getPlantLevel(type) + 1;
        int coinCost = PlantUpgradeCosts.coinCostToReach(next);
        int seedCost = PlantUpgradeCosts.seedPacketCostToReach(next);
        Table upgradeRow = new Table();
        Image coin = image(COIN_ICON, 24f, 24f);
        upgradeRow.add(coin).size(24f).padRight(4f);
        TextButton upgrade = UiWidgets.primary("Upgrade (" + coinCost + " + " + seedCost + " pkts)");
        UiWidgets.onChange(upgrade, () -> UiWidgets.apply(controller,
                controller.getCollectionController().upgradePlant(type.name)));
        upgradeRow.add(upgrade);
        detail.add(upgradeRow).padTop(8f).row();
    }

    private void addPlantPurchaseAction(Table detail, PlantType type) {
        Table purchaseRow = new Table();
        Image coin = image(COIN_ICON, 24f, 24f);
        purchaseRow.add(coin).size(24f).padRight(4f);
        TextButton purchase = UiWidgets.primary("Purchase (" + CollectionUiSupport.PLANT_PURCHASE_COST + ")");
        UiWidgets.onChange(purchase, () -> UiWidgets.apply(controller,
                controller.getCollectionController().purchasePlant(type.name)));
        purchaseRow.add(purchase);
        detail.add(purchaseRow).padTop(8f).row();
    }

    private void buildZombieDetail(Table detail, String title, CollectionViewState collection) {
        ZombieType type = ZombieType.fromName(title);
        if (type != null) {
            ZombieVisualDef visual = catalog.zombie(type);
            if (visual != null) {
                PamPreviewActor preview = new PamPreviewActor(
                        assets, visual.pamPath, visual.idleClip, 0.7f, intactArmorVisibility(visual));
                preview.setSize(180f, 180f);
                detail.add(preview).size(180f).padBottom(8f).row();
            }
        }
        detail.add(UiWidgets.title(title)).padBottom(6f).row();
        for (String line : collection.detailLines) {
            detail.add(UiWidgets.body(line)).left().row();
        }
    }

    private Map<String, Boolean> intactArmorVisibility(ZombieVisualDef visual){
        if(visual == null || visual.armor == null){
            return Collections.emptyMap();
        }
        Map<String, Boolean> map = new HashMap<>();
        if(visual.armor.groupPart != null){
            map.put(visual.armor.groupPart, true);
        }
        if(visual.armor.intactPart != null){
            map.put(visual.armor.intactPart, true);
        }
        return map;
    }

    private Image image(String id, float width, float height) {
        TextureRegion region = assets != null ? assets.region(id) : null;
        Image image = new Image();
        if (region != null) {
            image.setDrawable(new TextureRegionDrawable(region));
        }
        image.setSize(width, height);
        return image;
    }

    private User currentUser() {
        if (controller == null) {
            return null;
        }
        return controller.getStorage().getCurrentUser();
    }

    @Override
    public void act(float deltaSeconds) {
        stage.act(deltaSeconds);
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
