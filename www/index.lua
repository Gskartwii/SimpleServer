write("<DOCTYPE html><html><head><title>SimpleServer Environment Dump</title></head><body><h2>SimpleServer Environment Dump</h2>");
function search(tbl, inset)
	local pre = string.rep("-", inset);
	for i,v in pairs(tbl) do
		if tbl == _ENV and i == "search" then else
			write(pre .. i .. " " .. tostring(v));
			if type(v) == "table" then
				write("<br/>");
				search(table, inset + 1);
			end
			write("<br/>");
		end
	end
end
search(_ENV, 0);
write("<hr/><h4>SERVER Table Contents</h4>");
search(SERVER, 0);
write("<hr/><h4>GET Table Contents</h4>");
search(GET, 0);
write("<hr/><h4>POST Table Contents</h4>");
search(POST, 0);
write("</body></html>");