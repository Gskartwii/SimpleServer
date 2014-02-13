write("<DOCTYPE html><html><head><title>SimpleServer Environment Dump</title></head><body><h2>SimpleServer Environment Dump</h2>");
for i,v in pairs(_ENV) do
	write(i);
	write(" ");
	write(v);
	write("<br/>");
end
write("</body></html>");
exit();