package com.oday.project.StateMachine;


import com.oday.project.configuration.AerospikeConfiguration;

import com.oday.project.model.Server;
import com.oday.project.model.ServerStatus;
import com.oday.project.repository.IncorrectVersion;
import com.oday.project.repository.ServerRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;

import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import static com.oday.project.StateMachine.LoggingUtils.LOGGER;
import static com.oday.project.StateMachine.LoggingUtils.getStateInfo;

@Configuration
@EnableStateMachineFactory
@ComponentScan(basePackageClasses = AerospikeConfiguration.class)
public class SMConfig extends StateMachineConfigurerAdapter<ServerStatus, ServerEvent> {
final ServerRepository serverRepository;

    public SMConfig(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    @Override
    public void configure(StateMachineStateConfigurer<ServerStatus, ServerEvent> states) throws Exception {
        states.withStates().initial(ServerStatus.Createing,entryAction())
        .state(ServerStatus.Active,entryAction())
                .end(ServerStatus.Active);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ServerStatus, ServerEvent> transitions) throws Exception {

        transitions.withInternal()

            .source(ServerStatus.Createing)


                .action(myAction())
                .timerOnce(20000);




    }


    private Action<ServerStatus, ServerEvent> myAction() {
        return ctx -> {

             Long id= Long.valueOf(( ctx.getStateMachine().getId()));
             Server value=serverRepository.findById(id).get();
            value.setStatus(ServerStatus.Active);
            System.out.println("the server with id= " +value.getId()+" now is in = "+value.getStatus());
            try {
				serverRepository.update(value);
			} catch (IncorrectVersion e) {
				// TODO Auto-generated catch block
				System.err.println("errrrrrrrrrrererrererererooooorrrr");
				myAction();
			}
        };
    }


    @Bean
    public Action<ServerStatus, ServerEvent> entryAction() {
        return ctx -> LOGGER.info("Entry action {} to get from {} to {}",
                ctx.getEvent(),
                getStateInfo(ctx.getSource()),
                getStateInfo(ctx.getTarget()));
    }

}
