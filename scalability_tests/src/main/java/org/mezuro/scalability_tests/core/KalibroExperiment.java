package org.mezuro.scalability_tests.core;

import eu.choreos.vv.experiments.Experiment;
import org.mezuro.scalability_tests.strategy.Strategy;

public abstract class KalibroExperiment<T> extends Experiment<T, T> {
	protected Strategy<T> subject;
	protected TestConfiguration configuration;
	
	protected void setAttributes(TestConfiguration configuration, Strategy<T> subject) throws Exception {
		this.configuration = configuration;
		this.subject = subject;
	}
	
	public void start() throws Exception {
		this.run(this.configuration.getSubjectName(), this.configuration.isPlotGraph());
	}

	@Override
	public void beforeExperiment() throws Exception {
		subject.beforeExperiment();
	}

	@Override
	public void afterExperiment() throws Exception {
		subject.afterExperiment();
	}

	@Override
	public void beforeIteration() throws Exception {
		subject.beforeIteration();
	}
	
	@Override
	public void afterIteration() throws Exception {
		subject.afterIteration();
	}
	
	@Override
	public T beforeRequest() throws Exception {
		return subject.beforeRequest();
	}

	@Override
	public T request(T item) throws Exception {
		return subject.request(item);
	}
	
	@Override
	public void afterRequest(T requestResponse) throws Exception {
		subject.afterRequest(requestResponse);
	}
}
