package com.sequenceiq.freeipa.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.DispatcherType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.sequenceiq.cloudbreak.auth.ThreadBasedUserCrnProvider;
import com.sequenceiq.cloudbreak.concurrent.MDCCleanerTaskDecorator;
import com.sequenceiq.cloudbreak.orchestrator.host.HostOrchestrator;
import com.sequenceiq.cloudbreak.orchestrator.state.ExitCriteria;
import com.sequenceiq.cloudbreak.orchestrator.state.ExitCriteriaModel;
import com.sequenceiq.freeipa.logger.MDCContextFilter;

import io.opentracing.contrib.jaxrs2.server.SpanFinishingFilter;

@Configuration
@EnableRetry
@EnableScheduling
public class AppConfig {

    @Value("${freeipa.intermediate.threadpool.core.size:}")
    private int intermediateCorePoolSize;

    @Value("${freeipa.intermediate.threadpool.capacity.size:}")
    private int intermediateQueueCapacity;

    @Inject
    private List<HostOrchestrator> hostOrchestrators;

    @Inject
    private ThreadBasedUserCrnProvider threadBaseUserCrnProvider;

    @Bean
    @Primary
    public AsyncTaskExecutor intermediateBuilderExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(intermediateCorePoolSize);
        executor.setQueueCapacity(intermediateQueueCapacity);
        executor.setThreadNamePrefix("intermediateBuilderExecutor-");
        executor.setTaskDecorator(new MDCCleanerTaskDecorator());
        executor.initialize();
        return executor;
    }

    @Bean
    public Map<String, HostOrchestrator> hostOrchestrators() {
        Map<String, HostOrchestrator> map = new HashMap<>();
        for (HostOrchestrator hostOrchestrator : hostOrchestrators) {
            hostOrchestrator.init(new MyExitCriteria());
            map.put(hostOrchestrator.name(), hostOrchestrator);
        }
        return map;
    }

    @Bean
    public FilterRegistrationBean<MDCContextFilter> mdcContextFilterRegistrationBean() {
        FilterRegistrationBean<MDCContextFilter> registrationBean = new FilterRegistrationBean<>();
        MDCContextFilter filter = new MDCContextFilter(threadBaseUserCrnProvider);
        registrationBean.setFilter(filter);
        registrationBean.setOrder(Integer.MAX_VALUE);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean spanFinishingFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new SpanFinishingFilter());
        filterRegistrationBean.setAsyncSupported(true);
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        filterRegistrationBean.addUrlPatterns("*");
        return filterRegistrationBean;
    }

    private static class MyExitCriteria implements ExitCriteria {
        @Override
        public boolean isExitNeeded(ExitCriteriaModel exitCriteriaModel) {
            return false;
        }

        @Override
        public String exitMessage() {
            return null;
        }
    }
}
