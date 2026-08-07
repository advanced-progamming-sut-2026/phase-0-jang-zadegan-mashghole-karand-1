package model.quest;

import model.storage.user.User;

public interface RewardSystem {
    RewardType getRewardType();
    void reward(User user);
}
