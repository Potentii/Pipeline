package com.potentii.utils.pipeline;

/**
 * Wraps exceptions thrown inside {@link PipelineProcessor}
 */
public class PipelineWrapperException extends RuntimeException {

	private static final long serialVersionUID = -8311375068165971672L;

	private final Pipeline<?> pipeline;
	private final PipelineProcessor<?, ?> processor;


	public PipelineWrapperException(Pipeline<?> pipeline, PipelineProcessor<?, ?> processor, String message, Throwable cause) {
		super(message, cause);
		this.pipeline = pipeline;
		this.processor = processor;
	}


	public Pipeline<?> getPipeline() {
		return pipeline;
	}

	public PipelineProcessor<?, ?> getProcessor() {
		return processor;
	}
}
