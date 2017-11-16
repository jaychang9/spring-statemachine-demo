package com.choosefine.statemachine.controller;

import com.choosefine.statemachine.config.StateMachineDemoConfig.Events;
import com.choosefine.statemachine.config.StateMachineDemoConfig.States;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 张洁
 * @date 2017/11/16
 */
@RestController
public class DemoController {
    @Autowired
    private StateMachine<States, Events> stateMachine;

    @Autowired
    private StateMachinePersister<States, Events, String> stateMachinePersister;

    @GetMapping("1")
    public Map<String,Object> demo1(@RequestParam("user") String user,@RequestParam("event") Events event) throws Exception {
        Map<String,Object> map = new HashMap<>();
        StateMachine<States, Events> stateMachine = resetStateMachineFromStore(user);
        if(null != event){
            feedMachine(user,event);
        }
        map.put("CurrentState",stateMachine.getState().getIds());
        map.put("uuid",stateMachine.getUuid());
        return map;
    }


    //tag::snippetD[]
    private void feedMachine(String user, Events id) throws Exception {
        stateMachine.sendEvent(id);
        stateMachinePersister.persist(stateMachine, "testprefix:" + user);
    }
//end::snippetD[]

    //tag::snippetE[]
    private StateMachine<States, Events> resetStateMachineFromStore(String user) throws Exception {
        return stateMachinePersister.restore(stateMachine, "testprefix:" + user);
    }
}
