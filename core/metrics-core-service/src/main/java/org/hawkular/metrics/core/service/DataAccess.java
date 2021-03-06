/*
 * Copyright 2014-2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hawkular.metrics.core.service;

import java.util.Map;
import java.util.Set;

import org.hawkular.metrics.model.AvailabilityType;
import org.hawkular.metrics.model.Interval;
import org.hawkular.metrics.model.Metric;
import org.hawkular.metrics.model.MetricId;
import org.hawkular.metrics.model.MetricType;
import org.hawkular.metrics.model.Tenant;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;

import rx.Observable;

/**
 * @author John Sanda
 */
public interface DataAccess {
    Observable<ResultSet> insertTenant(String tenantId);

    Observable<ResultSet> insertTenant(Tenant tenant);

    Observable<Row> findAllTenantIds();

    Observable<Row> findTenant(String id);

    <T> ResultSetFuture insertMetricInMetricsIndex(Metric<T> metric);

    <T> Observable<Row> findMetric(MetricId<T> id);

    <T> Observable<ResultSet> addDataRetention(Metric<T> metric);

    <T> Observable<Row> getMetricTags(MetricId<T> id);

    <T> Observable<ResultSet> addTags(Metric<T> metric, Map<String, String> tags);

    <T> Observable<ResultSet> deleteTags(Metric<T> metric, Set<String> tags);

    <T> Observable<Integer> updateMetricsIndex(Observable<Metric<T>> metrics);

    <T> Observable<Row> findMetricsInMetricsIndex(String tenantId, MetricType<T> type);

    Observable<Integer> insertGaugeData(Metric<Double> metric, int ttl);

    Observable<Integer> insertCounterData(Metric<Long> counter, int ttl);

    Observable<Row> findCounterData(MetricId<Long> id, long startTime, long endTime, int limit, Order order);

    Observable<Row> findGaugeData(MetricId<Double> id, long startTime, long endTime, int limit, Order order);

    Observable<Row> findAvailabilityData(MetricId<AvailabilityType> id, long startTime, long endTime, int limit,
            Order order);

    Observable<Row> findAvailabilityData(MetricId<AvailabilityType> id, long timestamp);


    Observable<ResultSet> deleteGaugeMetric(String tenantId, String metric, Interval interval, long dpart);

    Observable<Integer> insertAvailabilityData(Metric<AvailabilityType> metric, int ttl);


    <T> ResultSetFuture findDataRetentions(String tenantId, MetricType<T> type);

    <T> Observable<ResultSet> updateRetentionsIndex(String tenantId, MetricType<T> type,
            Map<String, Integer> retentions);

    <T> ResultSetFuture updateRetentionsIndex(Metric<T> metric);

    <T> Observable<ResultSet> insertIntoMetricsTagsIndex(Metric<T> metric, Map<String, String> tags);

    <T> Observable<ResultSet> deleteFromMetricsTagsIndex(Metric<T> metric, Map<String, String> tags);

    Observable<Row> findMetricsByTagName(String tenantId, String tag);

    Observable<Row> findMetricsByTagNameValue(String tenantId, String tag, String tvalue);
}
