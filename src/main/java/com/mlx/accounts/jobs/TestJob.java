package com.mlx.accounts.jobs;

import com.xeiam.sundial.Job;
import com.xeiam.sundial.annotations.SimpleTrigger;
import com.xeiam.sundial.exceptions.JobInterruptException;

import java.util.concurrent.TimeUnit;

/**
 * 10/6/15.
 */
@SimpleTrigger(repeatInterval = TestJob.PERIOD_IN_MIN, timeUnit = TimeUnit.MINUTES)
public class TestJob extends Job {
    public static final long PERIOD_IN_MIN = 5;

    @Override
    public void doRun() throws JobInterruptException {
    }
}
