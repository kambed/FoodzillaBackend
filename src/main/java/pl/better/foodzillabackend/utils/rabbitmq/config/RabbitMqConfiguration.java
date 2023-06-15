package pl.better.foodzillabackend.utils.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMqConfiguration {

    @Bean
    public CachingConnectionFactory connectionFactory() {
        return new CachingConnectionFactory();
    }

    @Bean
    public RabbitAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public AsyncRabbitTemplate asyncRabbitTemplate(
            RabbitTemplate rabbitTemplate){
        return new AsyncRabbitTemplate(rabbitTemplate);
    }

    @Bean
    public Queue imageGenerateQueue() {
        Map<String, Object> args = Map.of(
                "x-max-priority", 10
        );
        return new Queue("imageGenerateQueue", true, false, false, args);
    }

    @Bean
    public Queue recipesQueue() {
        return new Queue("recipes", true);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue("email", true);
    }

    @Bean
    public DirectExchange exchange() {
        Map<String, Object> args = Map.of(
                "x-max-priority", 10
        );
        return new DirectExchange("imageExchange", true, false, args);
    }

    @Bean
    public Binding binding(DirectExchange exchange,
                           @Qualifier("imageGenerateQueue") Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("images");
    }

}
