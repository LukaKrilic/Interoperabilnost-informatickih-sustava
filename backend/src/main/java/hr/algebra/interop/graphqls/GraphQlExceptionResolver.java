package hr.algebra.interop.graphqls;

import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import hr.algebra.interop.service.NotFoundException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class GraphQlExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof NotFoundException) {
            return GraphQLError.newError()
                    .errorType(ErrorType.NOT_FOUND)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .build();
        }
        if (ex instanceof AccessDeniedException) {
            return GraphQLError.newError()
                    .errorType(ErrorType.FORBIDDEN)
                    .message("Nemate ovlasti za ovu operaciju")
                    .path(env.getExecutionStepInfo().getPath())
                    .build();
        }
        return null;
    }
}