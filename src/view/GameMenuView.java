package view;

public class GameMenuView {
    private final Renderer renderer;

    public GameMenuView(Renderer renderer) {
        this.renderer = renderer;
    }
    public void showGemWallet(int gem){
        renderer.print("Gem Wallet: " + gem);
    }
    public void showCoinWallet(int coin){
        renderer.print("Coin Wallet: " + coin);
    }
    public void showProcessCheat(int n , String type){
        renderer.print("Successfully added " + n +" " +  type + " to your wallet.");
    }
    public void showIsLockedChapter(){
        renderer.print("This chapter is locked.");
    }
}
