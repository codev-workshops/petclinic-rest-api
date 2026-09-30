package org.springframework.samples.petclinic.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.stereotype.Repository;

class CallMonitoringAspectTests {

    @Test
    void defaultsToEnabledWithNoRecordedCalls() {
        CallMonitoringAspect aspect = new CallMonitoringAspect();

        assertThat(aspect.isEnabled()).isTrue();
        assertThat(aspect.getCallCount()).isZero();
        assertThat(aspect.getCallTime()).isZero();
    }

    @Test
    void enabledInvokeReturnsProceedValueAndIncrementsCallCount() throws Throwable {
        CallMonitoringAspect aspect = new CallMonitoringAspect();
        ProceedingJoinPoint joinPoint = mockJoinPoint();
        when(joinPoint.proceed()).thenReturn("result");

        Object result = aspect.invoke(joinPoint);

        assertThat(result).isEqualTo("result");
        verify(joinPoint).proceed();
        assertThat(aspect.getCallCount()).isEqualTo(1);
    }

    @Test
    void enabledInvokeCountsCallsThatThrow() throws Throwable {
        CallMonitoringAspect aspect = new CallMonitoringAspect();
        ProceedingJoinPoint joinPoint = mockJoinPoint();
        IllegalStateException failure = new IllegalStateException("failed");
        when(joinPoint.proceed()).thenThrow(failure);

        IllegalStateException exception =
            assertThrows(IllegalStateException.class, () -> aspect.invoke(joinPoint));
        assertThat(exception).isSameAs(failure);
        assertThat(aspect.getCallCount()).isEqualTo(1);
    }

    @Test
    void disabledInvokeReturnsProceedValueWithoutCounting() throws Throwable {
        CallMonitoringAspect aspect = new CallMonitoringAspect();
        aspect.setEnabled(false);
        ProceedingJoinPoint joinPoint = mockJoinPoint();
        when(joinPoint.proceed()).thenReturn("result");

        Object result = aspect.invoke(joinPoint);

        assertThat(aspect.isEnabled()).isFalse();
        assertThat(result).isEqualTo("result");
        verify(joinPoint).proceed();
        assertThat(aspect.getCallCount()).isZero();
    }

    @Test
    void disabledInvokePropagatesFailureWithoutCounting() throws Throwable {
        CallMonitoringAspect aspect = new CallMonitoringAspect();
        aspect.setEnabled(false);
        ProceedingJoinPoint joinPoint = mockJoinPoint();
        IllegalStateException failure = new IllegalStateException("failed");
        when(joinPoint.proceed()).thenThrow(failure);

        IllegalStateException exception =
            assertThrows(IllegalStateException.class, () -> aspect.invoke(joinPoint));
        assertThat(exception).isSameAs(failure);
        assertThat(aspect.isEnabled()).isFalse();
        assertThat(aspect.getCallCount()).isZero();
    }

    @Test
    void getCallTimeReturnsAverageDuration() throws Throwable {
        CallMonitoringAspect aspect = new CallMonitoringAspect();
        ProceedingJoinPoint joinPoint = mockJoinPoint();
        when(joinPoint.proceed()).thenAnswer(invocation -> {
            Thread.sleep(25);
            return "result";
        });

        aspect.invoke(joinPoint);
        aspect.invoke(joinPoint);

        assertThat(aspect.getCallCount()).isEqualTo(2);
        assertThat(aspect.getCallTime()).isGreaterThanOrEqualTo(20);
    }

    @Test
    void resetClearsRecordedCallsAndTime() throws Throwable {
        CallMonitoringAspect aspect = new CallMonitoringAspect();
        ProceedingJoinPoint joinPoint = mockJoinPoint();
        when(joinPoint.proceed()).thenReturn("result");
        aspect.invoke(joinPoint);

        aspect.reset();

        assertThat(aspect.getCallCount()).isZero();
        assertThat(aspect.getCallTime()).isZero();
    }

    @Test
    void pointcutMatchesRepositoryTypesOnly() {
        CallMonitoringAspect aspect = new CallMonitoringAspect();
        AspectJProxyFactory repositoryFactory = new AspectJProxyFactory(new AnnotatedRepository());
        repositoryFactory.addAspect(aspect);
        AnnotatedRepository repository = repositoryFactory.getProxy();

        assertThat(repository.findValue()).isEqualTo("repository");
        assertThat(aspect.getCallCount()).isEqualTo(1);

        CallMonitoringAspect unannotatedAspect = new CallMonitoringAspect();
        AspectJProxyFactory serviceFactory = new AspectJProxyFactory(new UnannotatedService());
        serviceFactory.addAspect(unannotatedAspect);
        UnannotatedService service = serviceFactory.getProxy();

        assertThat(service.findValue()).isEqualTo("service");
        assertThat(unannotatedAspect.getCallCount()).isZero();
    }

    private ProceedingJoinPoint mockJoinPoint() {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.toShortString()).thenReturn("join point");
        return joinPoint;
    }

    @Repository
    static class AnnotatedRepository {

        public String findValue() {
            return "repository";
        }
    }

    static class UnannotatedService {

        public String findValue() {
            return "service";
        }
    }
}
