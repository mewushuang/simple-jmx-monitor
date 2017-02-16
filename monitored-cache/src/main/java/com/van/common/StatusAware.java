package com.van.common;

import com.van.monitor.api.RunningStatusMetric;

/**
 * Created by van on 17-1-16.
 */
public interface StatusAware {
    void setStatus(RunningStatusMetric.RunningStatus status);
}
