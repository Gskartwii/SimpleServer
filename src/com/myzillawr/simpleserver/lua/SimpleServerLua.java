package com.myzillawr.simpleserver.lua;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseIoLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.lib.jse.JseOsLib;

import com.myzillawr.simpleserver.util.StringInputStream;

public class SimpleServerLua extends Globals{
	public SimpleServerLua(){
		load(new JseBaseLib());
		load(new PackageLib());
		load(new Bit32Lib());
		load(new TableLib());
		load(new StringLib());
		load(new JseMathLib());
		load(new JseIoLib());
		load(new JseOsLib());
		LuaC.install();
		compiler = LuaC.instance;
		set("_VERSION", "Lua 5.2");
	}

	public void dolocalfile(File file){
		set("print", new print("[" + file.getName() + "] "));
		dofile(file, true);
	}
	
	public void dostring(String fileName, String content){
		set("print", new print("[" + fileName + "] "));
		StringInputStream is = null;
		try{
			is = new StringInputStream(content);
			baselib.loadStream(is, fileName, "bt", this).arg1().invoke();
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
	
	public void dofile(File file){
		set("print", new print("[" + file.getName() + "] "));
		dofile(file, false);
	}
	
	private void dofile(File file, boolean local){
		String chunkname = file.getAbsolutePath();
		if(local){
			chunkname = file.getName();
		}
		if(file.exists()){			
			FileInputStream is = null;
			try{
				is = new FileInputStream(file);
				baselib.loadStream(is, chunkname, "bt", this).arg1().invoke();
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
				LuaString s = SimpleServerLua.this.tostring(args.arg(i)).strvalue();
				System.out.write(s.m_bytes, s.m_offset, s.m_length);
			}
			System.out.write('\n');
			return NONE;
		}
	}
}