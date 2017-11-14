package com.choosefine.statemachine.config;


import com.choosefine.statemachine.config.MyStateMachineConfig.Events;
import com.choosefine.statemachine.config.MyStateMachineConfig.States;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 * @author 张洁
 * @date 2017/11/14
 */
public class ResetAction implements Action<States,Events> {

    @Override
    public void execute(StateContext<States, Events> context) {
        context.getExtendedState().getVariables().clear();
    }
}
