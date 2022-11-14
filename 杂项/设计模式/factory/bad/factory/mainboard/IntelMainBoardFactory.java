package factory.laterchange.abstractFactory.bad.factory.mainboard;

import factory.laterchange.abstractFactory.bad.product.IntelMainBoard;
import factory.laterchange.abstractFactory.bad.product.MainBoard;

public class IntelMainBoardFactory implements MainBoardFactory{
    @Override
    public MainBoard makeMB() {
        return new IntelMainBoard();
    }
}
