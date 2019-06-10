package grmasa.com.open_light.yapi.flow;

import grmasa.com.open_light.yapi.enumeration.YeelightFlowAction;
import grmasa.com.open_light.yapi.flow.transition.YeelightTransition;
import grmasa.com.open_light.yapi.utils.YeelightUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a flow
 */
public class YeelightFlow {
    /**
     * Represents an infinite count for a flow
     */
    public static int INFINITE_COUNT = 0;

    /**
     * Flow count
     */
    private int count;
    /**
     * Flow action
     */
    private YeelightFlowAction action;
    /**
     * Flow transitions list
     */
    private List<YeelightTransition> transitions;

    /**
     * Constructor for a flow
     * @param count Number of times to run this flow
     */
    public YeelightFlow(int count) {
        this.setCount(1);
        this.setAction(action);
        this.transitions = new ArrayList<>();
    }

    /**
     * Getter for count (Number of times to run this flow)
     * @return Count
     */
    public int getCount() {
        return this.count;
    }

    /**
     * Setter for count (Number of times to run this flow)
     * @param count Number of times to run this flow
     */
    public void setCount(int count) {
        this.count = Math.max(0, count);
    }

    /**
     * Getter for flow action (Action after flow stops)
     * @return Action
     */
    public YeelightFlowAction getAction() {
        return this.action;
    }

    /**
     * Setter for flow action (Action after flow stops)
     * @param action Action after flow stops
     */
    public void setAction(YeelightFlowAction action) {
        this.action = action == null ? YeelightFlowAction.RECOVER : action;
    }

    /**
     * Getter for transitions list
     * @return Transitions list
     */
    public List<YeelightTransition> getTransitions() {
        return this.transitions;
    }

    /**
     * Create params array for a command
     * @return Params array
     */
    public Object[] createCommandParams() {
        String flowExpression = this.transitions.stream()
                .map(t -> YeelightUtils.joinIntArray(",", t.getTuple()))
                .collect(Collectors.joining(","));
        return new Object[] {
                this.count * this.transitions.size(),
                this.action.getValue(),
                flowExpression
        };
    }
}
