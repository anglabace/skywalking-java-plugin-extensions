/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.skywalking.apm.plugin.jdbc.sqlserver.define;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.MultiClassNameMatch;

import static io.skywalking.apm.plugin.jdbc.sqlserver.Constants.STATEMENT_INTERCEPT_CLASS;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class SQLServerStatementInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    public static final String CALLABLE_STATEMENT_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerCallableStatement";
    public static final String CALLABLE_PREPARED_STATEMENT_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement";
    public static final String STATEMENT_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerStatement";

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[0];
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named("execute")
                                .or(named("executeQuery"))
                                .or(named("executeUpdate"))
                                .or(named("executeLargeUpdate"))
                                .or(named("executeBatchInternal"))
                                .or(named("executeUpdateInternal"))
                                .or(named("executeQuery"));
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return STATEMENT_INTERCEPT_CLASS;
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return false;
                    }
                }
        };
    }

    @Override
    protected ClassMatch enhanceClass() {
        return MultiClassNameMatch.byMultiClassMatch(STATEMENT_CLASS,CALLABLE_STATEMENT_CLASS,CALLABLE_PREPARED_STATEMENT_CLASS);
    }
}
