package com.myzillawr.simpleserver.lua;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.JseProcess;

public class SimpleServerOsLib extends TwoArgFunction{
	public static String TMP_PREFIX    = ".lua";
	public static String TMP_SUFFIX    = "tmp";

	private static final int CLOCK     = 0;
	private static final int DATE      = 1;
	private static final int DIFFTIME  = 2;
	private static final int EXECUTE   = 3;
	private static final int GETENV    = 4;
	private static final int REMOVE    = 5;
	private static final int RENAME    = 6;
	private static final int TIME      = 7;
	private static final int TMPNAME   = 8;

	private static final String[] NAMES = {
		"clock",
		"date",
		"difftime",
		"execute",
		"getenv",
		"remove",
		"rename",
		"setlocale",
		"time",
		"tmpname",
	};
	
	private static final long t0 = System.currentTimeMillis();
	@SuppressWarnings("unused")
	private static long tmpnames = t0;

	protected Globals globals;
	
	public static int EXEC_IOEXCEPTION = 1;
	public static int EXEC_INTERRUPTED = -2;
	public static int EXEC_ERROR = -3;

	protected Varargs execute(String command){
		int exitValue;
		try{
			exitValue = new JseProcess(command, null, globals.STDOUT, globals.STDERR).waitFor();
		}catch(IOException ioe){
			exitValue = EXEC_IOEXCEPTION;
		}catch(InterruptedException e){
			exitValue = EXEC_INTERRUPTED;
		}catch(Throwable t){
			exitValue = EXEC_ERROR;
		}
		if(exitValue == 0){
			return varargsOf(TRUE, valueOf("exit"), ZERO);
		}
		return varargsOf(NIL, valueOf("signal"), valueOf(exitValue));
	}

	protected void remove(String filename) throws IOException{
		File f = new File(filename);
		if(!f.exists()){
			throw new IOException("No such file or directory.");
		}
		if(!f.delete()){
			throw new IOException("Failed to delete.");
		}
	}

	protected void rename(String oldname, String newname) throws IOException{
		File f = new File(oldname);
		if(!f.exists()){
			throw new IOException("No such file or directory.");
		}
		if(!f.renameTo(new File(newname))){
			throw new IOException("Failed to rename.");
		}
	}

	protected String tmpname() throws IOException{
		try{
			File f = File.createTempFile(TMP_PREFIX ,TMP_SUFFIX);
			return f.getName();
		}catch(IOException ioe){
			throw new IOException("Failed to create temporary file.");
		}
	}
	
	class OsLibFunc extends VarArgFunction {
		public OsLibFunc(int opcode, String name) {
			this.opcode = opcode;
			this.name = name;
		}
		public Varargs invoke(Varargs args) {
			try {
				switch ( opcode ) {
				case CLOCK:
					return valueOf(clock());
				case DATE: {
					String s = args.optjstring(1, null);
					double t = args.optdouble(2,-1);
					return valueOf( date(s, t==-1? System.currentTimeMillis()/1000.: t) );
				}
				case DIFFTIME:
					return valueOf(difftime(args.checkdouble(1),args.checkdouble(2)));
				case EXECUTE:
					return execute(args.optjstring(1, null));
				case GETENV: {
					final String val = getenv(args.checkjstring(1));
					return val!=null? valueOf(val): NIL;
				}
				case REMOVE:
					remove(args.checkjstring(1));
					return LuaValue.TRUE;
				case RENAME:
					rename(args.checkjstring(1), args.checkjstring(2));
					return LuaValue.TRUE;
				case TIME:
					return valueOf(time(args.arg1().isnil()? null: args.checktable(1)));
				case TMPNAME:
					return valueOf(tmpname());
				}
				return NONE;
			} catch ( IOException e ) {
				return varargsOf(NIL, valueOf(e.getMessage()));
			}
		}
	}

	protected double clock() {
		return (System.currentTimeMillis()-t0) / 1000.;
	}

	protected double difftime(double t2, double t1) {
		return t2 - t1;
	}

	protected String date(String format, double time) {
		return new Date((long)(time*1000)).toString();
	}

	protected String getenv(String varname){
		return System.getProperty(varname);
	}

	protected long time(LuaTable table) {
		return System.currentTimeMillis();
	}

	@Override
	public LuaValue call(LuaValue modname, LuaValue env){
		globals = env.checkglobals();
		LuaTable os = new LuaTable();
		for(int i = 0; i < NAMES.length; ++i){
			os.set(NAMES[i], new OsLibFunc(i, NAMES[i]));
		}
		env.set("os", os);
		env.get("package").get("loaded").set("os", os);
		return os;
	}
}
