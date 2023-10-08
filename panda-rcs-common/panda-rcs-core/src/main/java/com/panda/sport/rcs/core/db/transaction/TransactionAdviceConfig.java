package com.panda.sport.rcs.core.db.transaction;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * 事务管理切面配置
 * ClassName: TransactionAdviceConfig <br/>
 * Description: <br/>
 * date: 2019/9/12 16:48<br/>
 * @author kane<br />
 * @since JDK 1.8
 * @date 2019-09-12
 * @version v1.1
 */
@Aspect
@Configuration
@ConditionalOnProperty("jdbc.db.master.url")
public class TransactionAdviceConfig {
    /**
     * 切点表达式
     */
    private static final String AOP_POINTCUT_EXPRESSION = "execution (* com.panda.sport.rcs.service.*.*(..))";
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean("txAdvice")
    public TransactionInterceptor txAdvice() {

        DefaultTransactionAttribute txAttrRequired = new DefaultTransactionAttribute();
        txAttrRequired.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        DefaultTransactionAttribute txAttrRequiredReadOnly = new DefaultTransactionAttribute();
        txAttrRequiredReadOnly.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        txAttrRequiredReadOnly.setReadOnly(true);

        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        source.addTransactionalMethod("add*", txAttrRequired);
        source.addTransactionalMethod("save*", txAttrRequired);
        source.addTransactionalMethod("edit*", txAttrRequired);
        source.addTransactionalMethod("insert*", txAttrRequired);
        source.addTransactionalMethod("delete*", txAttrRequired);
        source.addTransactionalMethod("remove*", txAttrRequired);
        source.addTransactionalMethod("update*", txAttrRequired);
        source.addTransactionalMethod("exec*", txAttrRequired);
        source.addTransactionalMethod("set*", txAttrRequired);
        source.addTransactionalMethod("get*", txAttrRequiredReadOnly);
        source.addTransactionalMethod("query*", txAttrRequiredReadOnly);
        source.addTransactionalMethod("find*", txAttrRequiredReadOnly);
        source.addTransactionalMethod("list*", txAttrRequiredReadOnly);
        source.addTransactionalMethod("count*", txAttrRequiredReadOnly);
        source.addTransactionalMethod("is*", txAttrRequiredReadOnly);
        source.addTransactionalMethod("load*", txAttrRequiredReadOnly);
        return new TransactionInterceptor(transactionManager, source);
    }

    @Bean
    public Advisor txAdviceAdvisor(@Qualifier("txAdvice") TransactionInterceptor txAdvice) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(AOP_POINTCUT_EXPRESSION);
        return new DefaultPointcutAdvisor(pointcut, txAdvice);
    }
}
