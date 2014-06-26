package liquibase.sqlgenerator.core;

import liquibase.action.Action;
import liquibase.exception.UnsupportedException;
import liquibase.statementlogic.StatementLogicChain;
import liquibase.exception.ValidationErrors;
import  liquibase.ExecutionEnvironment;
import liquibase.statement.UpdateExecutablePreparedStatement;

/**
 * Dummy SQL generator for <code>UpdateDataChange.ExecutableStatement</code><br>
 */
public class UpdateDataChangeGenerator extends AbstractSqlGenerator<UpdateExecutablePreparedStatement> {
    @Override
    public ValidationErrors validate(UpdateExecutablePreparedStatement statement, ExecutionEnvironment env, StatementLogicChain chain) {
        return new ValidationErrors();
    }

    @Override
    public Action[] generateActions(UpdateExecutablePreparedStatement statement, ExecutionEnvironment env, StatementLogicChain chain) throws UnsupportedException {
        return new Action[0];
    }
}
