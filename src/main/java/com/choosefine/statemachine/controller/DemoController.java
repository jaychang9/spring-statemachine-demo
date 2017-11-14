package com.choosefine.statemachine.controller;

import com.choosefine.statemachine.config.MyStateMachineConfig.Events;
import com.choosefine.statemachine.config.MyStateMachineConfig.States;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 张洁
 * @date 2017/11/14
 */
//@RestController
public class DemoController {

    @Autowired
    private StateMachine<States, Events> stateMachine;

    @Autowired
    private StateMachinePersister<States, Events, String> stateMachinePersister;

    /**
     * 初始化一个状态机
     *
     * @param username
     * @return
     * @throws Exception
     */
    @GetMapping("create")
    public Map<String,Object> create(String username) throws Exception {
        if(!StringUtils.isEmpty(username)){
            stateMachinePersister.persist(stateMachine,username);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("CurrentState",stateMachine.getState().getIds().toString());
        map.put("ExtendedState",stateMachine.getExtendedState().getVariables());
        return map;
    }

    /**
     *
     * @param username
     * @return
     * @throws Exception
     */
    @GetMapping("event")
    public Map<String,Object> sendEvent(String username,Events events) throws Exception {
        stateMachinePersister.restore(stateMachine,username);
        stateMachine.sendEvent(events);
        Map<String,Object> map = new HashMap<>();
        map.put("CurrentState",stateMachine.getState().getIds().toString());
        map.put("ExtendedState",stateMachine.getExtendedState().getVariables());

        return map;
    }
}
