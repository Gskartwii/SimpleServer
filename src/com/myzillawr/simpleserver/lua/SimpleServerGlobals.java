package com.myzillawr.simpleserver.lua;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import com.myzillawr.luabase.lua.BaseGlobals;
import com.myzillawr.simpleserver.util.StringInputStream;

public class SimpleServerGlobals extends BaseGlobals{
	public SimpleServerGlobals(){
		super();
	}
	
	public LuaValue dolocalfile(File file){
		set("print", new print("[" + file.getName() + "] "));
		return loadfile(file, true);
	}
	
	public LuaValue loadstring(String fileName, String content){
		set("print", new print("[" + fileName + "] "));
		StringInputStream is = null;
		try{
			is = new StringInputStream(content);
			return baselib.loadStream(is, fileName, "bt", this).arg1();
		}finally{
			if(is != null){
				try{
					is.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public LuaValue loadfile(File file){
		set("print", new print("[" + file.getName() + "] "));
		return loadfile(file, false);
	}
	
	private LuaValue loadfile(File file, boolean local){
		String chunkname = file.getAbsolutePath();
		if(local){
			chunkname = file.getName();
		}
		if(file.exists()){			
			FileInputStream is = null;
			try{
				is = new FileInputStream(file);
				return baselib.loadStream(is, chunkname, "bt", this).arg1();
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}finally{
				if(is != null){
					try{
						is.close();
					}catch(IOException e){
						e.printStackTrace();
					}
				}
			}
		}
		return LuaValue.NIL;
	}
	
	public LuaValue tostring(LuaValue arg){
		LuaValue h = arg.metatag(TOSTRING);
		if(!h.isnil()){
			return h.call(arg);
		}
		LuaValue v = arg.tostring();
		if(!v.isnil()){
			return v;
		}
		return valueOf(arg.tojstring());
	}

	final class print extends VarArgFunction{
		private byte[] prepend;
		
		public print(String prepend){
			this.prepend = prepend.getBytes();
		}
		
		public Varargs invoke(Varargs args){
			System.out.write(prepend, 0, prepend.length);
			for(int i = 1, n = args.narg(); i <= n; i++){
				if(i > 1){
					System.out.write(' ');
				}
				LuaString s = SimpleServerGlobals.this.tostring(args.arg(i)).strvalue();
				System.out.write(s.m_bytes, s.m_offset, s.m_length);
			}
			System.out.write('\n');
			return NONE;
		}
	}
}