package com.choosefine.statemachine.config;

import com.choosefine.statemachine.config.MyStateMachineConfig.Events;
import com.choosefine.statemachine.config.MyStateMachineConfig.States;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

/**
 * @author 张洁
 * @date 2017/11/14
 */
@Component
public class E2Action implements Action<States,Events> {

    @Override
    public void execute(StateContext<States, Events> context) {
        System.out.println("StateMachineConfig.execute e2Action");
        context.getExtendedState().getVariables().put("E2", true);
    }
}
