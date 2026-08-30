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
<<<<<<< Updated upstream
=======
import model.rule.SessionContext;
import model.service.MatchResultUi;
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
                } else if ("TIMEOUT".equals(status) || "CANCELLED".equals(status)) {
                    navigator.showToast("Invite " + status.toLowerCase());
                } else if ("SENT".equals(status)) {
                    navigator.showToast("Invite sent");
=======
                    if (controller.getCurrentMenu() == MenuType.I_ZOMBIE_QUEUE) {
                        controller.openMenu(MenuType.I_ZOMBIE_MODE);
                    }
                } else if ("TIMEOUT".equals(status) || "CANCELLED".equals(status)) {
                    navigator.showToast("Invite " + status.toLowerCase());
                    if (controller.getCurrentMenu() == MenuType.I_ZOMBIE_QUEUE) {
                        controller.openMenu(MenuType.I_ZOMBIE_MODE);
                    }
                } else if ("SENT".equals(status)) {
                    navigator.showToast("Invite delivered to " + (event.b == null ? "player" : event.b));
>>>>>>> Stashed changes
                }
            }
            case QUEUE_STATUS -> {
                if ("WAITING".equals(event.a)) {
                    navigator.showToast("In matchmaking queue...");
<<<<<<< Updated upstream
=======
                } else if ("LEFT".equals(event.a)) {
                    navigator.showToast("Left matchmaking queue");
>>>>>>> Stashed changes
                }
            }
            case MATCH_START -> {
                navigator.showToast("Match starting vs " + event.matchStart.opponent);
                controller.getGameMenuController().beginOnlineMatch(event.matchStart);
            }
            case MATCH_STATE -> applyMatchState(event.matchState);
            case MATCH_END -> {
                session.clearActiveMatch();
<<<<<<< Updated upstream
                navigator.showToast("Match ended");
                if (controller.getCurrentScreen() == ScreenType.GAME) {
                    controller.setScreen(ScreenType.LEVEL_SELECTOR);
=======
                if (controller.getCurrentScreen() == ScreenType.GAME) {
                    MatchResultUi ui = onlineMatchResult(event.a, event.b, model.getPlayContext());
                    controller.getSessionLifecycleController().showMatchResult(ui);
                    navigator.showToast(ui.title);
                } else {
                    navigator.showToast(formatMatchEnd(event.a, event.b, model.getPlayContext()));
                }
            }
            case MATCH_RESTART_OFFER -> {
                navigator.showToast((event.a == null ? "Opponent" : event.a) + " wants to restart");
                controller.openMenu(MenuType.MATCH_RESTART);
            }
            case MATCH_RESTART -> {
                navigator.showToast("Match restarted");
                if (controller.getCurrentScreen() == ScreenType.GAME) {
                    controller.getSessionLifecycleController().restartLevel();
                } else {
                    controller.clearCurrentMenu();
                }
            }
            case MATCH_RESTART_DECLINED -> {
                navigator.showToast("Opponent declined restart");
                if (controller.getCurrentMenu() == MenuType.MATCH_RESTART
                        || controller.getCurrentMenu() == MenuType.MATCH_RESTART_WAIT) {
                    controller.clearCurrentMenu();
                    controller.refreshView();
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
            case ERROR -> navigator.showToast(mapError(event.a));
=======
            case ERROR -> {
                navigator.showToast(mapError(event.a));
                if ("USER_OFFLINE".equals(event.a) || "INVALID_USERNAME".equals(event.a)
                        || "USER_BUSY".equals(event.a) || "UNAUTHORIZED".equals(event.a)) {
                    if (controller.getCurrentMenu() == MenuType.I_ZOMBIE_QUEUE) {
                        controller.openMenu(MenuType.I_ZOMBIE_MODE);
                    }
                }
            }
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
        gs.sunAmount = gs.zombieSun;
=======
        if (gs.networkSunAuthority) {
            SessionContext ctx = model.getPlayContext();
            if (ctx != null && ctx.getConfig() != null
                    && ctx.getConfig().localMatchRole == shared.izombie.MatchRole.PLANTS) {
                gs.sunAmount = gs.plantSun;
            } else {
                gs.sunAmount = gs.zombieSun;
            }
        } else {
            gs.sunAmount = gs.zombieSun;
        }
>>>>>>> Stashed changes

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

<<<<<<< Updated upstream
=======
    private static MatchResultUi onlineMatchResult(String winnerRole, String reason, SessionContext context) {
        shared.izombie.MatchRole local = context != null && context.getConfig() != null
                ? context.getConfig().localMatchRole
                : null;
        boolean localWon = local != null && local.name().equalsIgnoreCase(winnerRole);
        String detail = formatMatchEnd(winnerRole, reason, context);
        String title = localWon ? "Victory!" : "Defeat";
        // Match room is already closed; offer Play again → I, Zombie modes.
        return new MatchResultUi(title, detail, localWon, false, false, true);
    }

    private static String formatMatchEnd(String winnerRole, String reason, SessionContext context) {
        shared.izombie.MatchRole local = context != null && context.getConfig() != null
                ? context.getConfig().localMatchRole
                : null;
        boolean localWon = local != null && local.name().equalsIgnoreCase(winnerRole);
        String r = reason == null ? "" : reason.toUpperCase();

        if ("FORFEIT".equals(r)) {
            return localWon ? "Opponent left — you win" : "You left the match";
        }
        if ("SURVIVED".equals(r)) {
            return localWon ? "You survived — you win" : "Plants survived — you lose";
        }
        if ("ALL_BRAINS".equals(r)) {
            return localWon ? "All brains collected — you win" : "All brains collected — you lose";
        }
        if ("NO_RESOURCES".equals(r)) {
            return localWon ? "Zombies out of resources — you win" : "Out of resources — you lose";
        }
        return localWon ? "Match ended — you win" : "Match ended";
    }

>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
=======
            case "WS_CONNECT_FAILED" -> "Could not connect to game server";
            case "WS_NOT_READY" -> "Still connecting to the game server";
            case "UNAUTHORIZED" -> "Online session expired — log in again";
>>>>>>> Stashed changes
            default -> code;
        };
    }
}
