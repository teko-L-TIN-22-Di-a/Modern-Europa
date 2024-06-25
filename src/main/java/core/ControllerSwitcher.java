package core;

public interface ControllerSwitcher {

    void queue(Controller controller);
    void queue(Controller controller, Parameters parameters);

    void switchTo(Controller controller);
    void switchTo(Controller controller, Parameters parameters);

}
