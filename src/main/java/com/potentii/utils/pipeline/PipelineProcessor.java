package com.potentii.utils.pipeline;

/**
 * Represents the processing unit of a pipeline chain.
 * Each processor receives the output of the last step, process it, and then returns what will become the next step input.
 *
 * @param <T> The processor input type
 * @param <U> The processor output type
 */
@FunctionalInterface
public interface PipelineProcessor<T, U> {

	U process(T t) throws Exception;

}
