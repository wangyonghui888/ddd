package com.panda.sport.rcs.exeception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LogicException extends RuntimeException implements Cloneable {
	private static final long serialVersionUID = 1666385134759048787L;
	protected String code = "-100";
	protected String msg;
	protected String reason;
	protected String action;
	protected Throwable cause;

	protected static String getNewSn() {
		UUID uid = UUID.randomUUID();
		long mh = uid.getMostSignificantBits();
		long lh = uid.getLeastSignificantBits();
		long now = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder(50);
		sb.append(now);
		sb.append(".");
		sb.append(Math.abs(mh & lh));
		return sb.toString();
	}
	public LogicException(){ }

	public LogicException(int i, String msg) {
	}

	public LogicException(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public LogicException(String code, String msg, Throwable cause) {
		this.code = code;
		this.msg = msg;
		setCause(cause);
	}

	public LogicException(String code, String msg, String reason) {
		this.code = code;
		this.msg = msg;
		this.reason = reason;
	}

	public LogicException(String code, String msg, Throwable cause, String action) {
		this.code = code;
		this.msg = msg;
		setCause(cause);
		this.action = action;
	}

	public LogicException(String sn, String code, String msg, String reason, String action) {
		this.code = code;
		this.msg = msg;
		this.reason = reason;
		this.action = action;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return this.msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getReason() {
		return this.reason;
	}

	public void setReason(String reason) {
		if (null != this.cause) {
			StringBuilder sb = new StringBuilder();
			sb.append("@").append(this.cause.getClass().getSimpleName()).append("{");
			sb.append(" msg:").append(this.cause.getMessage());
			sb.append("}");
			this.reason = (reason + " >>>cause: " + sb.toString());
		} else {
			this.reason = reason;
		}
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Throwable getCause() {
		return this.cause;
	}

	public LogicException setCause(Throwable cause) {
		this.cause = cause;
		if (null != cause) {
			StringBuilder sb = new StringBuilder();
			sb.append("@").append(cause.getClass().getSimpleName()).append("{");
			sb.append(" msg:").append(cause.getMessage());
			sb.append("}");
			if (null != this.reason) {
				this.reason = (this.reason + " >>>cause: " + sb.toString());
			} else {
				this.reason = sb.toString();
			}
		}
		if ((null == this.code) && ((cause instanceof LogicException))) {
			LogicException le = (LogicException) cause;
			this.code = le.code;
			this.msg = le.msg;
		}

		return this;
	}

	public void printStackTrace(PrintStream s) {
		synchronized (s) {
			s.print(getMessage());
			if (null != this.cause) {
				s.println(" >>>stack: ");
				this.cause.printStackTrace(s);
			}
		}
	}

	public void printStackTrace(PrintWriter s) {
		synchronized (s) {
			s.print(getMessage());
			if (null != this.cause) {
				s.println(" >>>stack: ");
				this.cause.printStackTrace(s);
			}
		}
	}

	public String printString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		printStackTrace(pw);
		return sw.toString();
	}

	public LogicException clone() {
		LogicException e = new LogicException();
		e.code = this.code;
		e.msg = this.msg;
		e.reason = this.reason;
		e.action = this.action;
		return e;
	}

	public String getMessage() {
		return msg;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("@").append(getClass().getSimpleName()).append("{");
		sb.append(" code:").append(this.code).append(",");
		sb.append(" msg:").append(this.msg).append(",");
		sb.append("\n reason:").append(this.reason).append(",");
		sb.append(" action:").append(this.action);
		sb.append(" }");
		return sb.toString();
	}
	
	public Map<String,Object> getErrorResult(){
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("code",this.code);
		map.put("message", this.getMessage());
		map.put("reason", this.getReason());
		return map;
	}
}
