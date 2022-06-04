package com.potentii.utils.pipeline;


import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

public class PipelineTest {


	private static final String ARG = "Arg Test";


	@Test
	public void returnsIdentityArgument() {
		final String identity = Pipeline.given(ARG).getOutput();
		assertThat(identity, is(ARG));
	}

	@Test
	public void returnsEmptyIdentityArgument() {
		final Void identity = Pipeline.startEmpty().getOutput();
		assertThat(identity, is(nullValue()));
	}

	@Test
	public void passesArgumentToNextProcessor() {
		Pipeline
			.given(ARG)
			.process(arg -> {
				assertThat(arg, is(ARG));
				return null;
			});
	}

	@Test
	public void mapsArgumentToSameTypeOnOutput() {
		final String output = Pipeline
			.given(ARG)
			.process(arg -> arg.concat(" concatenated"))
			.getOutput();

		assertThat(output, is(ARG + " concatenated"));
	}

	@Test
	public void mapsArgumentToSameTypeOnNextProcessor() {
		Pipeline
			.given(ARG)
			.process(arg -> arg.concat(" concatenated"))
			.process(arg -> {
				assertThat(arg, is(ARG + " concatenated"));
				return null;
			});
	}

	@Test
	public void mapsArgumentToOtherTypeOnOutput() {
		final Integer output = Pipeline
			.given(ARG)
			.process(String::length)
			.getOutput();

		assertThat(output, is(ARG.length()));
	}

	@Test
	public void mapsArgumentToOtherTypeOnNextProcessor() {
		Pipeline
			.given(ARG)
			.process(String::length)
			.process(arg -> {
				assertThat(arg, is(ARG.length()));
				return null;
			});
	}


	@Test
	public void wrapsRuntimeException() {
		final RuntimeException exception = new RuntimeException("Runtime exception test");

		final PipelineProcessor<String, String> processor = arg -> {
			throw exception;
		};

		try {
			Pipeline
				.given(ARG)
				.process(processor);
		} catch (Exception e) {
			assertThat(e, instanceOf(PipelineWrapperException.class));

			PipelineWrapperException wrapped = (PipelineWrapperException) e;
			assertThat(wrapped.getPipeline(), is(notNullValue()));
			assertThat(wrapped.getPipeline().getOutput(), is(ARG));
			assertThat(wrapped.getProcessor(), sameInstance(processor));
			assertThat(wrapped.getCause(), sameInstance(exception));
		}
	}

	@Test
	public void wrapsException() {
		final Exception exception = new Exception("Exception test");

		final PipelineProcessor<String, String> processor = arg -> {
			throw exception;
		};

		try {
			Pipeline
				.given(ARG)
				.process(processor);
		} catch (Exception e) {
			assertThat(e, instanceOf(PipelineWrapperException.class));

			PipelineWrapperException wrapped = (PipelineWrapperException) e;
			assertThat(wrapped.getPipeline(), is(notNullValue()));
			assertThat(wrapped.getPipeline().getOutput(), is(ARG));
			assertThat(wrapped.getProcessor(), sameInstance(processor));
			assertThat(wrapped.getCause(), sameInstance(exception));
		}
	}

	@Test
	public void stopsPipelineOnExceptions() {
		final Exception exception = new Exception("Exception test");

		final PipelineProcessor<String, String> processor = arg -> {
			throw exception;
		};

		try {
			Pipeline
				.given(ARG)
				.process(processor)
				.process(arg -> {
					fail("Pipeline didn't stopped on exceptions");
					return null;
				});
		} catch (Exception e) {
			assertThat(e.getCause(), sameInstance(exception));
			assertThat(((PipelineWrapperException) e).getProcessor(), sameInstance(processor));
		}
	}


	@Test
	public void dontAllowNullProcessor() {
		try {
			Pipeline
				.given(ARG)
				.process(null);
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Invalid pipeline processor \"null\""));
		}
	}

}
