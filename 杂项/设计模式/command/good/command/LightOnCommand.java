package command.later.good.command;

import command.later.good.furniture.Light;

/**
 * 电灯开  的接口
 */
public class LightOnCommand implements Command{

    private Light light ; //哪个电灯

    public LightOnCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.on();   //直接打开
    }

    @Override
    public void undo() {
        light.off(); //原来是打开的就是关掉
    }
}
