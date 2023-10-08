local hashKey = KEYS[1];
local rec = loadstring("return " .. ARGV[1])();
local length = tonumber(ARGV[2]);
if rec == nil then 
	return {0};
end;

local max = nil;
for i = 1,length do
	for j = 1 , length do
		local key = string.format("%03d",(i -1)) .. string.format("%03d",(j -1)); 
		local result = redis.call("HINCRBY",hashKey,key,rec[key]);
		if max == nil then 
			max = result;
		end;
		if max < result then
			max = result;
		end;
	end;
end;

return {max};
