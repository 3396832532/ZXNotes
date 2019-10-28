package command.later.bad;

//控制类
public class Control {
    private Light light;
    private TV tv;//
    private final int slotNum = 10;

    public Control(Light light, TV tv) {
        this.light = light;
        this.tv = tv;
    }

    public void on(int slot) {
        switch (slot) {
            case 1:
                light.on();
                break;
            case 2:
                tv.on();
                break;
        }
    }

    public void off(int slot) {
        switch (slot) {
            case 1:
                light.off();
                break;
            case 2:
                tv.off();
                break;
        }
    }
}
