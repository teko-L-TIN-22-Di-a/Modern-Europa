package core.util;

import core.Parameters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class StateMachine {

    public static final int MAX_STACK_SIZE = 5;

    private Map<String, State> stateMap = new HashMap<String, State>();
    private State currentState;
    private Stack<State> stack = new Stack<State>();

    public StateMachine(List<State> states, Class<? extends State> initialState) {
        for (State state : states) {
            state.register(this);
            stateMap.put(state.getClass().getName(), state);
            if(state.getClass().equals(initialState)) {
                transitionTo(initialState);
            }
        }
    }

    public State getCurrentState() {
        return currentState;
    }

    public void update() {
        if (currentState != null) {
            currentState.update();
        }
    }

    public void transitionTo(Class<? extends State> state) {
        transitionTo(state, Parameters.EMPTY);
    }

    public void transitionTo(Class<? extends State> state, Parameters parameters) {
        if(!stateMap.containsKey(state.getName())) {
            throw new RuntimeException("Trying to transition to a state that doesnt exist: " + state.getName());
        }

        if(currentState != null) {
            currentState.exit(parameters);
        }

        stack.add(currentState);

        if(stack.size() > MAX_STACK_SIZE) {
            stack.remove(0);
        }

        currentState = stateMap.get(state.getName());
        currentState.enter(parameters);
    }

    public void transitionBack(Parameters parameters) {
        if(stack.isEmpty()) {
            throw new RuntimeException("State stack is empty");
        }

        currentState.exit(parameters);

        currentState = stack.pop();
        currentState.enter(parameters);
    }

    public void transitionBack() {
        transitionBack(Parameters.EMPTY);
    }

}
