package pvz.controller;

import pvz.model.greenhouse.Greenhouse;
import pvz.model.news.NewsFeed;
import pvz.model.quest.Quest;
import pvz.model.shop.Shop;
import pvz.view.MetaView;

public class MetaController implements Controller {

    private Greenhouse greenhouse;
    private Shop shop;
    private Quest quest;
    private NewsFeed newsFeed;
    private MetaView metaView;

    @Override
    public void handle(String command) {
    }
}
