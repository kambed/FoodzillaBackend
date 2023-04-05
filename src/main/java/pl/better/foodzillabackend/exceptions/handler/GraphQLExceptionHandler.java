package pl.better.foodzillabackend.exceptions.handler;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.exceptions.type.BadRequestException;
import pl.better.foodzillabackend.exceptions.type.ForbiddenException;
import pl.better.foodzillabackend.exceptions.type.NotFoundException;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {
    @Override
    protected GraphQLError resolveToSingleError(@NotNull Throwable ex, @NotNull DataFetchingEnvironment env) {
        ErrorType errorType = ErrorType.INTERNAL_ERROR;
        if (ex instanceof ConstraintViolationException || ex instanceof BadRequestException) {
            errorType = ErrorType.BAD_REQUEST;
        }
        if (ex instanceof NotFoundException) {
            errorType = ErrorType.NOT_FOUND;
        }
        if (ex instanceof ForbiddenException) {
            errorType = ErrorType.FORBIDDEN;
        }
        return GraphqlErrorBuilder.newError()
                .errorType(errorType)
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .build();
    }
}
