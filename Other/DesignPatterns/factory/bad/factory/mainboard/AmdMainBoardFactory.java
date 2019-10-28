package factory.laterchange.abstractFactory.bad.factory.mainboard;

import factory.laterchange.abstractFactory.bad.product.AmdMainBoard;
import factory.laterchange.abstractFactory.bad.product.MainBoard;

public class AmdMainBoardFactory implements MainBoardFactory{
    @Override
    public MainBoard makeMB() {
        return new AmdMainBoard();
    }
}
