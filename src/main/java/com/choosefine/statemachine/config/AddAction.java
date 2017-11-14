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
public class AddAction implements Action<States,Events> {

    @Override
    public void execute(StateContext<States, Events> context) {
        System.out.println("addAction");
        Object countObj = context.getExtendedState().getVariables().get("COUNT");
        if(null == countObj){
            context.getExtendedState().getVariables().put("COUNT",1);
        }else{
            Integer count = (Integer)countObj;
            context.getExtendedState().getVariables().put("COUNT",count+1);
        }
    }
}
