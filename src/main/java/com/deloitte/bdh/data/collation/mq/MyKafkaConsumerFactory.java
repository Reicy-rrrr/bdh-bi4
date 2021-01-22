//package com.deloitte.bdh.data.collation.mq;
//
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.aop.framework.Advised;
//import org.springframework.aop.support.AopUtils;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.beans.factory.BeanFactoryAware;
//import org.springframework.beans.factory.ListableBeanFactory;
//import org.springframework.beans.factory.ObjectFactory;
//import org.springframework.beans.factory.config.BeanExpressionContext;
//import org.springframework.beans.factory.config.BeanExpressionResolver;
//import org.springframework.beans.factory.config.ConfigurableBeanFactory;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.beans.factory.config.Scope;
//import org.springframework.context.expression.StandardBeanExpressionResolver;
//import org.springframework.core.MethodIntrospector;
//import org.springframework.core.annotation.AnnotationUtils;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.core.convert.converter.GenericConverter;
//import org.springframework.format.Formatter;
//import org.springframework.format.FormatterRegistry;
//import org.springframework.format.support.DefaultFormattingConversionService;
//import org.springframework.kafka.annotation.KafkaListenerConfigurer;
//import org.springframework.kafka.annotation.PartitionOffset;
//import org.springframework.kafka.annotation.TopicPartition;
//import org.springframework.kafka.config.KafkaListenerEndpoint;
//import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
//import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
//import org.springframework.kafka.listener.KafkaListenerErrorHandler;
//import org.springframework.kafka.support.KafkaNull;
//import org.springframework.kafka.support.TopicPartitionOffset;
//import org.springframework.messaging.converter.GenericMessageConverter;
//import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
//import org.springframework.messaging.handler.annotation.support.HeaderMethodArgumentResolver;
//import org.springframework.messaging.handler.annotation.support.HeadersMethodArgumentResolver;
//import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
//import org.springframework.messaging.handler.annotation.support.MessageMethodArgumentResolver;
//import org.springframework.messaging.handler.annotation.support.PayloadArgumentResolver;
//import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
//import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
//import org.springframework.stereotype.Service;
//import org.springframework.util.Assert;
//import org.springframework.util.ReflectionUtils;
//import org.springframework.util.StringUtils;
// 
//@Service
//public class MyKafkaConsumerFactory implements KafkaListenerConfigurer,BeanFactoryAware {
// 
//    private static final Logger logger = LoggerFactory.getLogger(MyKafkaConsumerFactory.class);
// 
//    private KafkaListenerEndpointRegistrar kafkaListenerEndpointRegistrar;
// 
//    private final AtomicInteger counter = new AtomicInteger();
//    private BeanFactory beanFactory;
// 
//    private BeanExpressionResolver resolver = new StandardBeanExpressionResolver();
// 
//    private BeanExpressionContext expressionContext;
// 
//    private final ListenerScope listenerScope = new ListenerScope();
// 
//    private final KafkaHandlerMethodFactoryAdapter messageHandlerMethodFactory =
//            new KafkaHandlerMethodFactoryAdapter();
// 
//    @Override
//    public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
//        this.kafkaListenerEndpointRegistrar = registrar;
//        addFormatters(messageHandlerMethodFactory.defaultFormattingConversionService);
//    }
// 
//    public void startConsumer(KafkaListenerEndpoint endpoint){
//        kafkaListenerEndpointRegistrar.registerEndpoint(endpoint);
//    }
// 
//    public void startConsumer(Object target){
//        logger.info("start consumer {} ...",target.getClass());
//        Class<?> targetClass = AopUtils.getTargetClass(target);
//        Map<Method, Set<DelayKafkaConsumer>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
//                new MethodIntrospector.MetadataLookup<Set<DelayKafkaConsumer>>() {
// 
//                    @Override
//                    public Set<DelayKafkaConsumer> inspect(Method method) {
//                        Set<DelayKafkaConsumer> listenerMethods = findListenerAnnotations(method);
//                        return (!listenerMethods.isEmpty() ? listenerMethods : null);
//                    }
// 
//                });
//        if (annotatedMethods.size()==0)
//            throw new IllegalArgumentException(target.getClass()+" need have method with @DelayKafkaConsumer");
//        for (Map.Entry<Method, Set<DelayKafkaConsumer>> entry : annotatedMethods.entrySet()) {
//            Method method = entry.getKey();
//            logger.info("find message listen handler method : {} , object : {}",method.getName(),target.getClass());
//            for (DelayKafkaConsumer listener : entry.getValue()) {
//                if(listener.topics().length==0) {
//                    logger.info("topics value is empty , will skip it , method : {} , target object : {}",method.getName(),target.getClass());
//                    continue;
//                }
//                processKafkaListener(listener,method,target);
//                logger.info("register method {} success , target object : {}",method.getName(),target.getClass());
//            }
//        }
//        logger.info("{} consumer start complete .",target.getClass());
//    }
// 
//    protected void processKafkaListener(DelayKafkaConsumer kafkaListener, Method method, Object bean) {
//        Method methodToUse = checkProxy(method, bean);
//        MethodKafkaListenerEndpoint endpoint = new MethodKafkaListenerEndpoint();
//        endpoint.setMethod(methodToUse);
//        endpoint.setBeanFactory(this.beanFactory);
//        String errorHandlerBeanName = resolveExpressionAsString(kafkaListener.errorHandler(), "errorHandler");
//        if (StringUtils.hasText(errorHandlerBeanName)) {
//            endpoint.setErrorHandler(this.beanFactory.getBean(errorHandlerBeanName, KafkaListenerErrorHandler.class));
//        }
//        processListener(endpoint, kafkaListener, bean, methodToUse);
//    }
// 
//    protected void processListener(MethodKafkaListenerEndpoint<?, ?> endpoint, DelayKafkaConsumer kafkaListener, Object bean,
//                                   Object adminTarget) {
//        String beanRef = kafkaListener.beanRef();
//        if (StringUtils.hasText(beanRef)) {
//            this.listenerScope.addListener(beanRef, bean);
//        }
//        endpoint.setBean(bean);
//        endpoint.setMessageHandlerMethodFactory(this.messageHandlerMethodFactory);
//        endpoint.setId(getEndpointId(kafkaListener));
//        endpoint.setGroupId(getEndpointGroupId(kafkaListener, endpoint.getId()));
//        endpoint.setTopics(resolveTopics(kafkaListener));
//        endpoint.setTopicPartitions(resolveTopicPartitions(kafkaListener));
//        kafkaListenerEndpointRegistrar.registerEndpoint(endpoint);
//        if (StringUtils.hasText(beanRef)) {
//            this.listenerScope.removeListener(beanRef);
//        }
//    }
// 
//    private String getEndpointId(DelayKafkaConsumer kafkaListener) {
//        if (StringUtils.hasText(kafkaListener.id())) {
//            return resolve(kafkaListener.id());
//        }
//        else {
//            return "Custom-Consumer" + this.counter.getAndIncrement();
//        }
//    }
// 
//    private String getEndpointGroupId(DelayKafkaConsumer kafkaListener, String id) {
//        String groupId = null;
//        if (StringUtils.hasText(kafkaListener.groupId())) {
//            groupId = resolveExpressionAsString(kafkaListener.groupId(), "groupId");
//        }
//        if (groupId == null && StringUtils.hasText(kafkaListener.id())) {
//            groupId = id;
//        }
//        return groupId;
//    }
// 
//    private String[] resolveTopics(DelayKafkaConsumer kafkaListener) {
//        String[] topics = kafkaListener.topics();
//        List<String> result = new ArrayList<>();
//        if (topics.length > 0) {
//            for (int i = 0; i < topics.length; i++) {
//                Object topic = resolveExpression(topics[i]);
//                resolveAsString(topic, result);
//            }
//        }
//        return result.toArray(new String[result.size()]);
//    }
// 
//    private void resolveAsString(Object resolvedValue, List<String> result) {
//        if (resolvedValue instanceof String[]) {
//            for (Object object : (String[]) resolvedValue) {
//                resolveAsString(object, result);
//            }
//        }
//        else if (resolvedValue instanceof String) {
//            result.add((String) resolvedValue);
//        }
//        else if (resolvedValue instanceof Iterable) {
//            for (Object object : (Iterable<Object>) resolvedValue) {
//                resolveAsString(object, result);
//            }
//        }
//        else {
//            throw new IllegalArgumentException(String.format(
//                    "@DelayKafkaConsumer can't resolve '%s' as a String", resolvedValue));
//        }
//    }
// 
//    private TopicPartitionOffset[] resolveTopicPartitions(DelayKafkaConsumer kafkaListener) {
//        TopicPartition[] topicPartitions = kafkaListener.topicPartitions();
//        List<TopicPartitionOffset> result = new ArrayList<>();
//        if (topicPartitions.length > 0) {
//            for (TopicPartition topicPartition : topicPartitions) {
//                result.addAll(resolveTopicPartitionsList(topicPartition));
//            }
//        }
//        return result.toArray(new TopicPartitionOffset[result.size()]);
//    }
// 
//    private List<TopicPartitionOffset> resolveTopicPartitionsList(TopicPartition topicPartition) {
//        Object topic = resolveExpression(topicPartition.topic());
//        Assert.state(topic instanceof String,
//                "topic in @TopicPartition must resolve to a String, not " + topic.getClass());
//        Assert.state(StringUtils.hasText((String) topic), "topic in @TopicPartition must not be empty");
//        String[] partitions = topicPartition.partitions();
//        PartitionOffset[] partitionOffsets = topicPartition.partitionOffsets();
//        Assert.state(partitions.length > 0 || partitionOffsets.length > 0,
//                "At least one 'partition' or 'partitionOffset' required in @TopicPartition for topic '" + topic + "'");
//        List<TopicPartitionOffset> result = new ArrayList<>();
//        for (int i = 0; i < partitions.length; i++) {
//            resolvePartitionAsInteger((String) topic, resolveExpression(partitions[i]), result);
//        }
// 
//        for (PartitionOffset partitionOffset : partitionOffsets) {
//            Object partitionValue = resolveExpression(partitionOffset.partition());
//            Integer partition;
//            if (partitionValue instanceof String) {
//                Assert.state(StringUtils.hasText((String) partitionValue),
//                        "partition in @PartitionOffset for topic '" + topic + "' cannot be empty");
//                partition = Integer.valueOf((String) partitionValue);
//            }
//            else if (partitionValue instanceof Integer) {
//                partition = (Integer) partitionValue;
//            }
//            else {
//                throw new IllegalArgumentException(String.format(
//                        "@PartitionOffset for topic '%s' can't resolve '%s' as an Integer or String, resolved to '%s'",
//                        topic, partitionOffset.partition(), partitionValue.getClass()));
//            }
// 
//            Object initialOffsetValue = resolveExpression(partitionOffset.initialOffset());
//            Long initialOffset;
//            if (initialOffsetValue instanceof String) {
//                Assert.state(StringUtils.hasText((String) initialOffsetValue),
//                        "'initialOffset' in @PartitionOffset for topic '" + topic + "' cannot be empty");
//                initialOffset = Long.valueOf((String) initialOffsetValue);
//            }
//            else if (initialOffsetValue instanceof Long) {
//                initialOffset = (Long) initialOffsetValue;
//            }
//            else {
//                throw new IllegalArgumentException(String.format(
//                        "@PartitionOffset for topic '%s' can't resolve '%s' as a Long or String, resolved to '%s'",
//                        topic, partitionOffset.initialOffset(), initialOffsetValue.getClass()));
//            }
// 
//            Object relativeToCurrentValue = resolveExpression(partitionOffset.relativeToCurrent());
//            Boolean relativeToCurrent;
//            if (relativeToCurrentValue instanceof String) {
//                relativeToCurrent = Boolean.valueOf((String) relativeToCurrentValue);
//            }
//            else if (relativeToCurrentValue instanceof Boolean) {
//                relativeToCurrent = (Boolean) relativeToCurrentValue;
//            }
//            else {
//                throw new IllegalArgumentException(String.format(
//                        "@PartitionOffset for topic '%s' can't resolve '%s' as a Boolean or String, resolved to '%s'",
//                        topic, partitionOffset.relativeToCurrent(), relativeToCurrentValue.getClass()));
//            }
// 
//            TopicPartitionOffset topicPartitionOffset =
//                    new TopicPartitionOffset((String) topic, partition, initialOffset, relativeToCurrent);
//            if (!result.contains(topicPartitionOffset)) {
//                result.add(topicPartitionOffset);
//            }
//            else {
//                throw new IllegalArgumentException(
//                        String.format("@TopicPartition can't have the same partition configuration twice: [%s]",
//                                topicPartitionOffset));
//            }
//        }
//        return result;
//    }
// 
//    private void resolvePartitionAsInteger(String topic, Object resolvedValue,
//                                           List<TopicPartitionOffset> result) {
//        if (resolvedValue instanceof String[]) {
//            for (Object object : (String[]) resolvedValue) {
//                resolvePartitionAsInteger(topic, object, result);
//            }
//        }
//        else if (resolvedValue instanceof String) {
//            Assert.state(StringUtils.hasText((String) resolvedValue),
//                    "partition in @TopicPartition for topic '" + topic + "' cannot be empty");
//            result.add(new TopicPartitionOffset(topic, Integer.valueOf((String) resolvedValue)));
//        }
//        else if (resolvedValue instanceof Integer[]) {
//            for (Integer partition : (Integer[]) resolvedValue) {
//                result.add(new TopicPartitionOffset(topic, partition));
//            }
//        }
//        else if (resolvedValue instanceof Integer) {
//            result.add(new TopicPartitionOffset(topic, (Integer) resolvedValue));
//        }
//        else if (resolvedValue instanceof Iterable) {
//            for (Object object : (Iterable<Object>) resolvedValue) {
//                resolvePartitionAsInteger(topic, object, result);
//            }
//        }
//        else {
//            throw new IllegalArgumentException(String.format(
//                    "@DelayKafkaConsumer for topic '%s' can't resolve '%s' as an Integer or String", topic, resolvedValue));
//        }
//    }
// 
//    private Set<DelayKafkaConsumer> findListenerAnnotations(Method method) {
//        Set<DelayKafkaConsumer> listeners = new HashSet<>();
//        DelayKafkaConsumer ann = AnnotationUtils.findAnnotation(method, DelayKafkaConsumer.class);
//        if (ann != null) {
//            listeners.add(ann);
//        }
//        return listeners;
//    }
// 
// 
// 
//    private Method checkProxy(Method methodArg, Object bean) {
//        Method method = methodArg;
//        if (AopUtils.isJdkDynamicProxy(bean)) {
//            try {
//                method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
//                Class<?>[] proxiedInterfaces = ((Advised) bean).getProxiedInterfaces();
//                for (Class<?> iface : proxiedInterfaces) {
//                    try {
//                        method = iface.getMethod(method.getName(), method.getParameterTypes());
//                        break;
//                    }
//                    catch (NoSuchMethodException noMethod) {
//                    }
//                }
//            }
//            catch (SecurityException ex) {
//                ReflectionUtils.handleReflectionException(ex);
//            }
//            catch (NoSuchMethodException ex) {
//                throw new IllegalStateException(String.format(
//                        "target method '%s' found on bean target class '%s', " +
//                                "but not found in any interface(s) for bean JDK proxy. Either " +
//                                "pull the method up to an interface or switch to subclass (CGLIB) " +
//                                "proxies by setting proxy-target-class/proxyTargetClass " +
//                                "attribute to 'true'", method.getName(), method.getDeclaringClass().getSimpleName()), ex);
//            }
//        }
//        return method;
//    }
// 
//    @Override
//    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
//        this.beanFactory = beanFactory;
//        if (beanFactory instanceof ConfigurableListableBeanFactory) {
//            this.resolver = ((ConfigurableListableBeanFactory) beanFactory).getBeanExpressionResolver();
//            this.expressionContext = new BeanExpressionContext((ConfigurableListableBeanFactory) beanFactory,
//                    this.listenerScope);
//        }
//    }
// 
//    private String resolveExpressionAsString(String value, String attribute) {
//        Object resolved = resolveExpression(value);
//        if (resolved instanceof String) {
//            return (String) resolved;
//        }
//        else {
//            throw new IllegalStateException("The [" + attribute + "] must resolve to a String. "
//                    + "Resolved to [" + resolved.getClass() + "] for [" + value + "]");
//        }
//    }
// 
//    private Object resolveExpression(String value) {
//        String resolvedValue = resolve(value);
//        return this.resolver.evaluate(resolvedValue, this.expressionContext);
//    }
// 
//    /**
//     * Resolve the specified value if possible.
//     * @param value the value to resolve
//     * @return the resolved value
//     * @see ConfigurableBeanFactory#resolveEmbeddedValue
//     */
//    private String resolve(String value) {
//        if (this.beanFactory instanceof ConfigurableBeanFactory) {
//            return ((ConfigurableBeanFactory) this.beanFactory).resolveEmbeddedValue(value);
//        }
//        return value;
//    }
// 
//    private void addFormatters(FormatterRegistry registry) {
//        for (Converter<?, ?> converter : getBeansOfType(Converter.class)) {
//            registry.addConverter(converter);
//        }
//        for (GenericConverter converter : getBeansOfType(GenericConverter.class)) {
//            registry.addConverter(converter);
//        }
//        for (org.springframework.format.Formatter<?> formatter : getBeansOfType(Formatter.class)) {
//            registry.addFormatter(formatter);
//        }
//    }
// 
//    private <T> Collection<T> getBeansOfType(Class<T> type) {
//        if (this.beanFactory instanceof ListableBeanFactory) {
//            return ((ListableBeanFactory) this.beanFactory).getBeansOfType(type).values();
//        }else {
//            return Collections.emptySet();
//        }
//    }
// 
//    private static class ListenerScope implements Scope {
// 
//        private final Map<String, Object> listeners = new HashMap<>();
// 
//        ListenerScope() {
//            super();
//        }
// 
//        public void addListener(String key, Object bean) {
//            this.listeners.put(key, bean);
//        }
// 
//        public void removeListener(String key) {
//            this.listeners.remove(key);
//        }
// 
//        @Override
//        public Object get(String name, ObjectFactory<?> objectFactory) {
//            return this.listeners.get(name);
//        }
// 
//        @Override
//        public Object remove(String name) {
//            return null;
//        }
// 
//        @Override
//        public void registerDestructionCallback(String name, Runnable callback) {
//        }
// 
//        @Override
//        public Object resolveContextualObject(String key) {
//            return this.listeners.get(key);
//        }
// 
//        @Override
//        public String getConversationId() {
//            return null;
//        }
// 
//    }
// 
//    private class KafkaHandlerMethodFactoryAdapter implements MessageHandlerMethodFactory {
// 
//        private final DefaultFormattingConversionService defaultFormattingConversionService = new DefaultFormattingConversionService();
// 
//        private MessageHandlerMethodFactory messageHandlerMethodFactory;
// 
//        public void setMessageHandlerMethodFactory(MessageHandlerMethodFactory kafkaHandlerMethodFactory1) {
//            this.messageHandlerMethodFactory = kafkaHandlerMethodFactory1;
//        }
// 
//        @Override
//        public InvocableHandlerMethod createInvocableHandlerMethod(Object bean, Method method) {
//            return getMessageHandlerMethodFactory().createInvocableHandlerMethod(bean, method);
//        }
// 
//        private MessageHandlerMethodFactory getMessageHandlerMethodFactory() {
//            if (this.messageHandlerMethodFactory == null) {
//                this.messageHandlerMethodFactory = createDefaultMessageHandlerMethodFactory();
//            }
//            return this.messageHandlerMethodFactory;
//        }
// 
//        private MessageHandlerMethodFactory createDefaultMessageHandlerMethodFactory() {
//            DefaultMessageHandlerMethodFactory defaultFactory = new DefaultMessageHandlerMethodFactory();
//            defaultFactory.setBeanFactory(MyKafkaConsumerFactory.this.beanFactory);
// 
//            ConfigurableBeanFactory cbf =
//                    (MyKafkaConsumerFactory.this.beanFactory instanceof ConfigurableBeanFactory ?
//                            (ConfigurableBeanFactory) MyKafkaConsumerFactory.this.beanFactory : null);
// 
// 
//            defaultFactory.setConversionService(this.defaultFormattingConversionService);
// 
//            List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>();
// 
//            // Annotation-based argument resolution
//            argumentResolvers.add(new HeaderMethodArgumentResolver(this.defaultFormattingConversionService, cbf));
//            argumentResolvers.add(new HeadersMethodArgumentResolver());
// 
//            // Type-based argument resolution
//            final GenericMessageConverter messageConverter = new GenericMessageConverter(this.defaultFormattingConversionService);
//            argumentResolvers.add(new MessageMethodArgumentResolver(messageConverter));
//            argumentResolvers.add(new PayloadArgumentResolver(messageConverter) {
// 
//                @Override
//                protected boolean isEmptyPayload(Object payload) {
//                    return payload == null || payload instanceof KafkaNull;
//                }
// 
//            });
//            defaultFactory.setArgumentResolvers(argumentResolvers);
// 
//            defaultFactory.afterPropertiesSet();
//            return defaultFactory;
//        }
// 
//    }
//}
