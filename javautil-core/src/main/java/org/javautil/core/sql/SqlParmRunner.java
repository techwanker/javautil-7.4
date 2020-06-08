package org.javautil.core.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlParmRunner {
	private final Logger  logger         = LoggerFactory.getLogger(this.getClass());


	private Collection<SqlRunnerParms> parmslist;

	private Binds binds;

	public SqlParmRunner(SqlRunnerParms parm, Binds binds) {
		ArrayList<SqlRunnerParms> a = new ArrayList<SqlRunnerParms>();
		a.add(parm);
		this.parmslist = a;
		this.binds = binds;
	}
	
	public SqlParmRunner(Collection<SqlRunnerParms> parms, Binds binds) {
		this.parmslist = parms;
		this.binds = binds;
	}	

	public void process() throws SQLException {
		for (SqlRunnerParms parms : parmslist)  {
			SqlSplitter splitter = new SqlSplitter(parms.getSplitterInputStream());
			List<SqlStatement> stmts = splitter.getSqlStatements().getStatements();
			processStatements(parms, stmts, splitter);
		}
	}

	public void processStatements(SqlRunnerParms parms, List<SqlStatement> statements, SqlSplitter splitter) throws SQLException {

		logger.debug("processStatements: showSql?: {}, continueOnError? {}", parms.isShowSql(), parms.isContinueOnError());

		int statementNumber = 0;
		for (final SqlStatement ss : statements) {
			statementNumber++;
			ss.setConnection(parms.getConnection());
			ss.setShowError(parms.isShowError());
			if (ss.getSql() == null || ss.getSql().trim().length() == 0) {
				throw new IllegalStateException(String.format("sql is '%s'", ss.getSql()));
			}
			String name = ss.getName();
			if (parms.isShowSql()) {
				if (name == null) {
					System.out.println(String.format("-- SqlRunner - #%d --\n%s", statementNumber, ss.getSql()));
				} else {
					System.out.println(String.format("-- SqlRunner - #%d name: '%s'\n%s", statementNumber, name, ss.getSql()));
				}
			}

			try {
					logger.debug("commitEvery:{} about to execute: \n{}", parms.isCommitOrRollbackEveryStatement(), ss.getSql());
					try {
						ss.executeUpdate(binds);
						if (parms.isCommitOrRollbackEveryStatement()) {
							parms.getConnection().commit();
							logger.debug("commitOrRollbackEveryStatement true, committed");
						}
					} catch (SQLException sqe) {
						if (parms.isShowSqlSplitterOnError()) {
							logger.error("\n{}",splitter.formatLines());
						}
						if (parms.isCommitOrRollbackEveryStatement()) {
							parms.getConnection().rollback();
							logger.debug("commitOrRollbackEveryStatement true rolled back");

						}
						logger.error(sqe.getMessage());
						throw sqe;
					}
					if (parms.isCommitOrRollbackEveryStatement()) {
						parms.getConnection().commit();
					}
				ss.close();
			} catch (final SQLException s) {
				final String message = String.format("While processing: \nSQL:'\n%s\n'\n%s", ss.getSql(), s.getMessage());
				if (parms.isShowError()) {
					logger.error(message);
				}
				if (!parms.isContinueOnError()) {
					throw new SQLException(message, s);
				}
			} catch (Exception e) {
				logger.error("How did we get here?");
				logger.warn("found an error: processStatements: showError {}", parms.isShowError());
				final String message = String.format("While processing: \nSQL:'\n%s\n'\n%s", ss.getSql(), e.getMessage());
				if (parms.isShowError()) {
					logger.error(message);
				}
				if (!parms.isContinueOnError()) {
					throw new SQLException(message, e.getMessage());
				}
			}
		}
		if (parms.isCommit()) {
			parms.getConnection().commit();
		}
	}



}
