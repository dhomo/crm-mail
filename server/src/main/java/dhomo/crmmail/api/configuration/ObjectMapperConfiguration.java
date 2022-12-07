package dhomo.crmmail.api.configuration;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfiguration {
    @Bean
    public com.fasterxml.jackson.databind.Module Hibernate5Module(){
        return new Hibernate5Module();
    }
    // не нужно т.к. springboot подключает этот модуль автоматически
    // @Bean
    // public com.fasterxml.jackson.databind.Module JavaTimeModule(){
    //     return new JavaTimeModule();
    // }

    // @Bean
    // @Primary
    // public ObjectMapper HibernateAwareObjectMapper() {
    //     return new ObjectMapper()
    //             .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    //             .registerModule(new Hibernate5Module())
    //             .registerModule(new JavaTimeModule())
    //             .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    //             .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    //             ;
    // }
}
