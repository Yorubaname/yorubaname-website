package org.oruko.dictionary.events;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;

/**
 * Manages events publishing.
 *
 * Created by Dadepo Aderemi.
 */
@Component
public class EventPubService {

    private static final Logger logger = LoggerFactory.getLogger(EventPubService.class);
    private final AsyncEventBus eventBus;


    @Autowired
    private NameSearchedEventHandler nameSearchedEventHandler;

    @Autowired
    private NameIndexedEventHandler nameIndexedEventHandler;

    @Autowired
    private NameUploadedEventHandler nameUploadedEventHandler;

    @Autowired
    private ApplicationContext appContext;


    /**
     * public constructor, sets the AsyncEvent bus on construction
     */
    public EventPubService() {
        this.eventBus = new AsyncEventBus(Executors.newCachedThreadPool());
    }

    /**
     * Publishes all events
     * @param events the events to publish
     */
    public void publish(Object... events) {
        Stream.of(events).forEach(event -> {
            this.eventBus.post(event);
        });
    }

    @PostConstruct
    protected void registerListeners() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);

        provider.addIncludeFilter(new AnnotationTypeFilter(Component.class));
        Set<BeanDefinition> beans = provider.findCandidateComponents("org.oruko.dictionary");

        List<Class> classes = new ArrayList<>();
        for (BeanDefinition beanDefinition: beans) {
            final String beanClassName = beanDefinition.getBeanClassName();
            final ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
            final Class<?> aClass = ClassUtils.resolveClassName(beanClassName, classLoader);
            for (Method method: aClass.getMethods()) {
                if (method.isAnnotationPresent(Subscribe.class)) {
                    classes.add(aClass);
                }
            }
        }

//        for(Class aClass: classes) {
//            logger.info("Registered {} as an event handler", aClass.getName());
//            this.eventBus.register(appContext.getBean(aClass));
//        }
        this.eventBus.register(nameSearchedEventHandler);
        this.eventBus.register(nameIndexedEventHandler);
        this.eventBus.register(nameUploadedEventHandler);
    }
}
