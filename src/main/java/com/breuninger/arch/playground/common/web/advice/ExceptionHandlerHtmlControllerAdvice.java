package com.breuninger.arch.playground.common.web.advice;

import static java.util.stream.Collectors.toList;

import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.breuninger.arch.playground.common.domain.BadRequestException;
import com.breuninger.arch.playground.common.domain.ValidationError;

@ControllerAdvice
public class ExceptionHandlerHtmlControllerAdvice {

  // @ExceptionHandler(RedirectionException.class)
  // public RedirectView redirection(final RedirectionException exception) {
  //   final RedirectView loginRedirectView = new RedirectView(exception.getLocation().toString());
  //   loginRedirectView.setStatusCode(HttpStatus.valueOf(exception.getResponse().getStatus()));
  //   loginRedirectView.setExposeModelAttributes(false);
  //   return loginRedirectView;
  // }

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ModelAndView handleBadRequest(final BadRequestException exception) {
    final MappingJackson2JsonView view = new CustomMappingJackson2JsonView();
    final var modelAndView = new ModelAndView(view);
    modelAndView.addObject(exception.getErrors()
      .getFieldErrors()
      .stream()
      .map(fieldError -> new ValidationError(fieldError.getField(), fieldError.getDefaultMessage()))
      .collect(toList()));
    return modelAndView;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected ModelAndView handleMethodArgumentNotValid(final MethodArgumentNotValidException exception) {
    final MappingJackson2JsonView view = new CustomMappingJackson2JsonView();
    final var modelAndView = new ModelAndView(view);
    modelAndView.addObject(exception.getBindingResult()
      .getFieldErrors()
      .stream()
      .map(fieldError -> new ValidationError(fieldError.getField(), fieldError.getDefaultMessage()))
      .collect(toList()));
    return modelAndView;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ModelAndView handleConstraintViolation(final ConstraintViolationException exception) {
    final MappingJackson2JsonView view = new CustomMappingJackson2JsonView();
    final var modelAndView = new ModelAndView(view);
    modelAndView.addObject(exception.getConstraintViolations()
      .stream()
      .map(constraintViolation -> new ValidationError(((PathImpl) constraintViolation.getPropertyPath()).getLeafNode().getName(),
        constraintViolation.getMessage()))
      .collect(toList()));
    return modelAndView;
  }

  private static class CustomMappingJackson2JsonView extends MappingJackson2JsonView {

    @Override
    protected Object filterModel(final Map<String, Object> model) {
      final var result = super.filterModel(model);
      if (!(result instanceof Map)) {
        return result;
      }
      final var map = (Map) result;
      if (map.size() == 1) {
        return map.values().toArray()[0];
      }
      return map;
    }
  }
}
