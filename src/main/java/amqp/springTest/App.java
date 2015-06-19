package amqp.springTest;

import java.util.concurrent.TimeUnit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App implements CommandLineRunner{
	
	public static String queueName = "spring-boot";
	
//	@Autowired
//	AnnotationConfigApplicationContext context;
// doesn;t work but we don;t need it yet... i guess

	@Autowired
	RabbitTemplate rabbitTemplate;
	
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
	Queue queue() {
		return new Queue(queueName, false);
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange("spring-boot-exchange");
	}

	@Bean
	Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(queueName);
	}
    
    @Bean
    Receiver receiver(){
    	return new Receiver();
    }
    
    @Bean 
    MessageListenerAdapter listenerAdapter(Receiver receiver){
    	return new MessageListenerAdapter(receiver, "receiveMessage");
    	//it needs to be the same with the method we defined in the receiver... why not an interface?!?!
    }
    
    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter messageListenerAdapter){

    	SimpleMessageListenerContainer container =  new SimpleMessageListenerContainer(connectionFactory);
    	container.setQueueNames(queueName);
    	container.setMessageListener(messageListenerAdapter);
    	
    	return container;
    }
    
	@Override
	public void run(String... arg0) throws Exception {
		 System.out.println("Waiting five seconds...");
	        Thread.sleep(5000);
	        System.out.println("Sending message...");
	        rabbitTemplate.convertAndSend(queueName, "Hello from RabbitMQ!");
	        receiver().getLatch().await(10000, TimeUnit.MILLISECONDS);
//	        context.close();
	}
}