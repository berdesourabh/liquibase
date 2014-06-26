package liquibase.sqlgenerator.core;

import liquibase.action.Action;
import liquibase.action.core.UnparsedSql;
import liquibase.exception.UnsupportedException;
import liquibase.statementlogic.StatementLogicChain;
import liquibase.database.Database;
import liquibase.datatype.DataTypeFactory;
import liquibase.exception.ValidationErrors;
import  liquibase.ExecutionEnvironment;
import liquibase.statement.core.DeleteStatement;
import liquibase.structure.core.Column;

public class DeleteGenerator extends AbstractSqlGenerator<DeleteStatement> {

    @Override
    public ValidationErrors validate(DeleteStatement deleteStatement, ExecutionEnvironment env, StatementLogicChain chain) {
        ValidationErrors validationErrors = new ValidationErrors();
        validationErrors.checkRequiredField("tableName", deleteStatement.getTableName());
        return validationErrors;
    }

    @Override
    public Action[] generateActions(DeleteStatement statement, ExecutionEnvironment env, StatementLogicChain chain) throws UnsupportedException {
        Database database = env.getTargetDatabase();

        StringBuffer sql = new StringBuffer("DELETE FROM " + database.escapeTableName(statement.getCatalogName(), statement.getSchemaName(), statement.getTableName()));

        if (statement.getWhereClause() != null) {
            String fixedWhereClause = " WHERE " + statement.getWhereClause();
            for (String columnName : statement.getWhereColumnNames()) {
                if (columnName == null) {
                    continue;
                }
                fixedWhereClause = fixedWhereClause.replaceFirst(":name",
                        database.escapeObjectName(columnName, Column.class));
            }
            for (Object param : statement.getWhereParameters()) {
                fixedWhereClause = fixedWhereClause.replaceFirst("\\?|:value", DataTypeFactory.getInstance().fromObject(param, database).objectToSql(param, database).replaceAll("\\$", "\\$"));
            }
            sql.append(" ").append(fixedWhereClause);
        }

        return new Action[]{new UnparsedSql(sql.toString())};
    }
}
