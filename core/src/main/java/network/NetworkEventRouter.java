package network;

import com.badlogic.gdx.Gdx;

import controller.ControllerManager;
import model.ModelManager;
import model.core.GameState;
import model.core.Position;
import model.data.brain.Brain;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;
import shared.dto.MatchStatePayload;
import view.MenuType;
import view.ScreenType;
import view.gdx.ui.UiNavigator;

public final class NetworkEventRouter {
    private final ControllerManager controller;
    private final ModelManager model;
    private final UiNavigator navigator;
    private final NetworkSession session;

    public NetworkEventRouter(ControllerManager controller, ModelManager model, UiNavigator navigator,
            NetworkSession session) {
        this.controller = controller;
        this.model = model;
        this.navigator = navigator;
        this.session = session;
        session.addListener(event -> Gdx.app.postRunnable(() -> handle(event)));
        model.getEventBus().subscribe(model.event.events.BrainCollectedEvent.class, e -> {
            if (session.activeMatch() != null) {
                session.socket().reportBrain(e.row);
            }
        });
    }

    private void handle(NetworkSession.NetworkEvent event) {
        switch (event.type) {
            case INVITE_INCOMING -> {
                navigator.showToast(event.a + " invited you to I, Zombie");
                controller.openMenu(MenuType.I_ZOMBIE_INVITE);
            }
            case INVITE_RESULT -> {
                String status = event.a;
                if ("REJECTED".equals(status)) {
                    navigator.showToast("Invite rejected");
                } else if ("TIMEOUT".equals(status) || "CANCELLED".equals(status)) {
                    navigator.showToast("Invite " + status.toLowerCase());
                } else if ("SENT".equals(status)) {
                    navigator.showToast("Invite sent");
                }
            }
            case QUEUE_STATUS -> {
                if ("WAITING".equals(event.a)) {
                    navigator.showToast("In matchmaking queue...");
                }
            }
            case MATCH_START -> {
                navigator.showToast("Match starting vs " + event.matchStart.opponent);
                controller.getGameMenuController().beginOnlineMatch(event.matchStart);
            }
            case MATCH_STATE -> applyMatchState(event.matchState);
            case MATCH_END -> {
                session.clearActiveMatch();
                navigator.showToast("Match ended");
                if (controller.getCurrentScreen() == ScreenType.GAME) {
                    controller.setScreen(ScreenType.LEVEL_SELECTOR);
                }
            }
            case QUICK_MSG -> navigator.showToast(
                    (event.a == null ? "Opponent" : event.a) + ": " + (event.c == null ? event.b : event.c));
            case LOOKUP_RESULT -> {
                if (!event.flag1) {
                    navigator.showToast("Username not found");
                } else if (!event.flag2) {
                    navigator.showToast("User is offline");
                } else {
                    navigator.showToast(event.a + " is online");
                }
            }
            case ERROR -> navigator.showToast(mapError(event.a));
            default -> {
            }
        }
    }

    private void applyMatchState(MatchStatePayload state) {
        if (state == null || controller.getCurrentScreen() != ScreenType.GAME) {
            return;
        }
        GameState gs = model.getState();
        if (!gs.dualSunMode) {
            return;
        }
        gs.plantSun = state.plantSun;
        gs.zombieSun = state.zombieSun;
        gs.sunAmount = gs.zombieSun;

        if (state.brainsCollected != null) {
            for (int i = 0; i < Math.min(state.brainsCollected.length, gs.brains.size()); i++) {
                if (state.brainsCollected[i]) {
                    Brain brain = gs.getBrainAtRow(i);
                    if (brain != null && !brain.isCollected()) {
                        brain.collect();
                    }
                }
            }
        }

        if (state.plants != null) {
            for (MatchStatePayload.PlacedEntity e : state.plants) {
                if (gs.getPlantAt(e.row, e.col) == null) {
                    PlantType type = PlantType.fromName(e.type);
                    if (type != null) {
                        Plant plant = new Plant(type, e.row, e.col, 1, model.getEventBus());
                        gs.addPlant(plant);
                    }
                }
            }
        }
        if (state.zombies != null) {
            for (MatchStatePayload.PlacedEntity e : state.zombies) {
                boolean exists = gs.zombies.stream()
                        .anyMatch(z -> z.isAlive && z.row == e.row
                                && Math.round(z.position.x / GameState.CELL_WIDTH) == e.col);
                if (!exists) {
                    ZombieType type = ZombieType.fromName(e.type);
                    if (type != null) {
                        Zombie zombie = new Zombie(
                                type,
                                e.row,
                                e.col,
                                new Position(
                                        e.col * GameState.CELL_WIDTH + GameState.CELL_WIDTH / 2f,
                                        e.row * GameState.CELL_HEIGHT + GameState.CELL_HEIGHT / 2f),
                                model.getEventBus());
                        gs.addZombie(zombie);
                    }
                }
            }
        }
    }

    private static String mapError(String code) {
        if (code == null) {
            return "Network error";
        }
        return switch (code) {
            case "INVALID_USERNAME" -> "Invalid username";
            case "USER_OFFLINE" -> "User is offline";
            case "USER_BUSY" -> "User is busy";
            case "NO_INVITE" -> "No pending invite";
            case "WRONG_ROLE" -> "Not your side to place that";
            case "CANNOT_PLACE" -> "Cannot place there";
            case "SERVER_UNAVAILABLE" -> "Server unavailable";
            default -> code;
        };
    }
}
