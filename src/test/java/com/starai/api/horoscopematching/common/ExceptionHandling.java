package com.starai.api.horoscopematching.common;

import static com.starai.api.horoscopematching.utils.AppConstants.PROFILE_TEST;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@Profile(PROFILE_TEST)
@ControllerAdvice
public final class ExceptionHandling implements ProblemHandling {}
