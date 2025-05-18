package com.faspix.cryptorate.utility;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;

public class CronUtility {

    private static final Duration FALLBACK_TTL = Duration.ofMinutes(5);

    private static final CronParser PARSER = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING));

    public static Duration computeTTL(String cronExpression) {
        Cron cron = PARSER.parse(cronExpression);
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        ZonedDateTime now = ZonedDateTime.now();

        Optional<ZonedDateTime> next = executionTime.nextExecution(now);
        return next
                .map(nextTime -> Duration.between(now, nextTime))
                .orElse(FALLBACK_TTL); // fallback
    }
}
