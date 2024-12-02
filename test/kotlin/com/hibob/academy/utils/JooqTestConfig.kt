package com.hibob.academy.utils

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.ThreadLocalTransactionProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
@Profile("db-test")
class JooqTestConfig(@Value("\${spring.datasource.url}") val url: String) {

    @Bean
    open fun dslContext(): DSLContext {
        val connectionProvider = DataSourceConnectionProvider(createDataSource())
        val configuration = DefaultConfiguration().apply {
            set(connectionProvider)
            set(SQLDialect.POSTGRES)
            set(ThreadLocalTransactionProvider(connectionProvider))
        }
        return DSL.using(configuration)
    }

    private fun createDataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.url = url
        dataSource.setDriverClassName("org.postgresql.Driver")
        return dataSource
    }

}
