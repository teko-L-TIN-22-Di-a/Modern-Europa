package core.util;

import core.Parameters;

public abstract class State {

    private StateMachine stateMachine = null;

    public void register(StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    protected void transitionBack() {
        transitionBack(Parameters.EMPTY);
    }

    protected void transitionBack(Parameters parameters) {
        ensureRegistered();

        stateMachine.transitionBack(parameters);
    }

    protected void transitionTo(Class<? extends State> stateClass) {
        transitionTo(stateClass, Parameters.EMPTY);
    }
    protected void transitionTo(Class<? extends State> stateClass, Parameters parameters) {
        ensureRegistered();

        stateMachine.transitionTo(stateClass, parameters);
    }

    public abstract void update();

    public abstract void enter(Parameters parameters);
    public abstract void exit(Parameters parameters);

    private void ensureRegistered() {
        if(stateMachine == null) {
            throw new RuntimeException("State was not registered.");
        }
    }

}
