package com.potentii.utils.pipeline;

/**
 * Represents a step-by-step execution of processors.
 * The processors are 'map' implementors ({@link PipelineProcessor}) that takes an input, process it, and returns a (possibly) new output.
 * This type is immutable, and has a fluent interface.
 *
 * @param <T> The input data for this pipeline
 */
public class Pipeline<T> {

	private final T output;


	private Pipeline(T output) {
		this.output = output;
	}


	/**
	 * Starts a new pipeline without initial data
	 *
	 * @return An empty pipeline step (Void)
	 */
	public static Pipeline<Void> startEmpty() {
		return new Pipeline<>(null);
	}


	/**
	 * Starts a new pipeline with initial data
	 *
	 * @param input The initial data to pass to the pipeline
	 * @param <U>   The type of the initial data
	 * @return A pipeline step to process the given data input
	 */
	public static <U> Pipeline<U> given(U input) {
		return new Pipeline<>(input);
	}


	/**
	 * Chains a new processor to the pipeline, and executes it
	 *
	 * @param processor The processor to be appended to this pipeline execution
	 * @param <U>       The processor output type (it will be the type of the returning pipeline)
	 * @return A new pipeline that takes the returned output from the given processor as its input
	 * @throws PipelineWrapperException All exceptions thrown by the given processor will be wrapped in this runtime-exception
	 * @throws IllegalArgumentException If the given processor is not valid (null for example)
	 */
	public <U> Pipeline<U> process(PipelineProcessor<T, U> processor) throws PipelineWrapperException, IllegalArgumentException {
		if (processor == null)
			throw new IllegalArgumentException("Invalid pipeline processor \"null\"");

		try {
			final U newOutput = processor.process(output);
			return new Pipeline<>(newOutput);
		} catch (Exception e) {
			throw new PipelineWrapperException(this, processor, e.getMessage(), e);
		}
	}


	public T getOutput() {
		return output;
	}
}
